/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kcodeyt.vanilla.generator.server;

import cn.nukkit.Server;
import cn.nukkit.utils.Logger;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.util.GameVersion;
import de.kcodeyt.vanilla.util.ZipHelper;
import de.kcodeyt.vanilla.world.World;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BedrockDedicatedServer {

    private static final GameVersion NEWEST = GameVersion.of("1.19.50.02");
    private static final String DIST_URL_TEMPLATE = "https://minecraft.azureedge.net/bin-[os]/bedrock-server-[version].zip";

    private static final Logger LOGGER = VanillaGeneratorPlugin.getInstance().getLogger();
    private static final File TEMP_DIRECTORY = new File("temp");
    private static final CompletableFuture<File> FUTURE = new CompletableFuture<>();

    public static void downloadServer() throws IOException {
        if(!TEMP_DIRECTORY.exists() && !TEMP_DIRECTORY.mkdirs()) return;

        final File distFolder = new File(TEMP_DIRECTORY, "dist");
        if(!distFolder.exists() && !distFolder.mkdirs()) return;

        final String distUrl = DIST_URL_TEMPLATE.
                replace("[os]", SystemUtils.IS_OS_WINDOWS ? "win" : "linux").
                replace("[version]", NEWEST.toString());

        final String serverFile = distUrl.substring(distUrl.lastIndexOf('/') + 1);

        removeOldServerBinaries(distFolder, serverFile);

        final File extracted = new File(distFolder, "extracted");
        final File localServerCopy = new File(distFolder, serverFile);
        if(localServerCopy.exists() && extracted.exists()) {
            FUTURE.complete(extracted);
            return;
        }

        LOGGER.info("Downloading bedrock server binary files! This may take a while...");
        CompletableFuture.runAsync(() -> {
            try {
                if(extracted.exists()) FileUtils.deleteDirectory(extracted);

                FileUtils.copyURLToFile(new URL(distUrl), localServerCopy, 30000, 5000);
                ZipHelper.unzip(localServerCopy, extracted);

                LOGGER.info("Successfully downloaded bedrock server binary files!");
                FUTURE.complete(extracted);
            } catch(IOException e) {
                LOGGER.error("Failed to download bedrock server binary files!", e);
                FUTURE.complete(null);
            }
        });
    }

    private static void removeOldServerBinaries(File distDir, String serverFile) throws IOException {
        final File[] filesInDistDir = distDir.listFiles();
        if(filesInDistDir == null) return;

        for(File file : filesInDistDir) {
            if(file.isDirectory()) continue;

            if(!file.getName().equalsIgnoreCase(serverFile)) {
                FileUtils.delete(file);
            }
        }
    }

    public static File createTempServer(World world, int serverPort) {
        final File tempServer = new File(TEMP_DIRECTORY, "server-" + world.getWorldName());

        if(!tempServer.exists() && !tempServer.mkdirs()) {
            LOGGER.error("Could not create temporary server for world [" + world.getWorldName() + "]!");
            return null;
        }

        final GameVersion versionOfServer = getVersionOfServer(tempServer);

        if(versionOfServer.isOlderThan(NEWEST)) {
            if(versionOfServer.equals(GameVersion.NULL_VERSION))
                LOGGER.info("Creating bedrock server for world " + world.getWorldName() + ". This may take a while...");
            else
                LOGGER.info("Updating bedrock server for world " + world.getWorldName() + ". This may take a while...");

            copyOrUpdateServer(tempServer);

            if(versionOfServer.equals(GameVersion.NULL_VERSION))
                LOGGER.info("Successfully created bedrock server!");
            else
                LOGGER.info("Successfully updated bedrock server!");
        }

        updateServerVersion(tempServer);
        updateServerProperties(tempServer, world, serverPort);

        return tempServer;
    }

    private static void copyOrUpdateServer(File tempServer) {
        FUTURE.whenComplete((original, throwable) -> {
            if(original == null) return;

            try {
                final File[] filesInTempServer = tempServer.listFiles();
                if(filesInTempServer != null) {
                    for(File file : filesInTempServer) {
                        final File fileInOriginal = new File(original, file.getName());

                        if(fileInOriginal.exists()) {
                            if(file.isDirectory()) FileUtils.deleteDirectory(file);
                            else FileUtils.delete(file);
                        }
                    }
                }

                FileUtils.copyDirectory(original, tempServer);

                if(!SystemUtils.IS_OS_WINDOWS) createStartFile(tempServer);
            } catch(IOException e) {
                LOGGER.error("Could not copy from server template to world server directory", e);
            }
        }).join();
    }

    private static void createStartFile(File tempServer) {
        if(!SystemUtils.IS_OS_WINDOWS) {
            final File startFile = new File(tempServer, "start.sh");
            final File binaryFile = new File(tempServer, "bedrock_server");

            try(final FileOutputStream outputStream = new FileOutputStream(startFile)) {
                outputStream.write("#!/bin/bash\nLD_LIBRARY_PATH=. ./bedrock_server".getBytes());
            } catch(IOException e) {
                LOGGER.error("Could now write bash script for starting BDS", e);
            }

            try {
                Files.setPosixFilePermissions(startFile.toPath(), PosixFilePermissions.fromString("rwxr--r--"));
            } catch(IOException e) {
                LOGGER.error("Could not set execute flag on bash script", e);
            }

            try {
                Files.setPosixFilePermissions(binaryFile.toPath(), PosixFilePermissions.fromString("rwxr--r--"));
            } catch(IOException e) {
                LOGGER.error("Could not set execute flag on bedrock server binary", e);
            }
        }
    }

    private static void updateServerProperties(File tempServer, World world, int serverPort) {
        final File serverProperties = new File(tempServer, "server.properties");

        final List<String> lines = new ArrayList<>();
        try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(serverProperties))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("server-port=")) line = "server-port=" + serverPort;
                if(line.startsWith("server-portv6=")) line = "server-portv6=0";
                if(line.startsWith("online-mode=")) line = "online-mode=false";
                if(line.startsWith("gamemode=")) line = "gamemode=creative";
                if(line.startsWith("level-seed=")) line = "level-seed=" + world.getSeed();
                if(line.startsWith("allow-cheats=")) line = "allow-cheats=true";
                if(line.startsWith("view-distance="))
                    line = "view-distance=" + Server.getInstance().getViewDistance();
                if(line.startsWith("default-player-permission-level="))
                    line = "default-player-permission-level=operator";
                if(line.startsWith("player-idle-timeout=")) line = "player-idle-timeout=1440";
                if(line.startsWith("server-authoritative-movement="))
                    line = "server-authoritative-movement=client-auth";
                if(line.startsWith("correct-player-movement=")) line = "correct-player-movement=false";
                if(line.startsWith("server-authoritative-block-breaking="))
                    line = "server-authoritative-block-breaking=false";

                lines.add(line);
            }
        } catch(IOException e) {
            LOGGER.error("Could not edit world servers server.properties", e);
        }

        if(!serverProperties.delete()) {
            LOGGER.error("Could not delete world servers server.properties");
            return;
        }

        try {
            try(final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(serverProperties))) {
                for(String line : lines) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }

                bufferedWriter.flush();
            }
        } catch(IOException e) {
            LOGGER.error("Could not edit world servers server.properties", e);
        }
    }

    private static GameVersion getVersionOfServer(File tempServer) {
        final File versionFile = new File(tempServer, ".version");

        if(!versionFile.exists()) return GameVersion.NULL_VERSION;

        try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(versionFile))) {
            return GameVersion.of(bufferedReader.readLine());
        } catch(Exception e) {
            LOGGER.error("Could not read server version from file!", e);
            return GameVersion.NULL_VERSION;
        }
    }

    private static void updateServerVersion(File tempServer) {
        final File versionFile = new File(tempServer, ".version");

        try(final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(versionFile))) {
            bufferedWriter.write(BedrockDedicatedServer.NEWEST.toString());
        } catch(IOException e) {
            LOGGER.error("Could not update the server version file!", e);
        }
    }

}
