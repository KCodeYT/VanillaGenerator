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
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
class BedrockDedicatedServer {

    private static final String WINDOWS_DIST = "https://minecraft.azureedge.net/bin-win/bedrock-server-1.19.10.03.zip";
    private static final String LINUX_DIST = "https://minecraft.azureedge.net/bin-linux/bedrock-server-1.19.10.03.zip";

    private static final File TEMP_DIRECTORY = new File("temp");
    private static final CompletableFuture<File> FUTURE = new CompletableFuture<>();

    static {
        if(TEMP_DIRECTORY.exists() || TEMP_DIRECTORY.mkdirs()) {
            final File distFolder = new File(TEMP_DIRECTORY, "dist");
            if(distFolder.exists() || distFolder.mkdirs()) {
                final String urlForOS = SystemUtils.IS_OS_WINDOWS ? WINDOWS_DIST : LINUX_DIST;
                final String[] urlSplit = urlForOS.split("/");
                final String serverFile = urlSplit[urlSplit.length - 1];

                final File extracted = new File(distFolder, "extracted");
                if(extracted.exists() || extracted.mkdirs()) {
                    final File localServerCopy = new File(distFolder, serverFile);
                    if(!localServerCopy.exists()) {
                        new Thread(() -> {
                            try {
                                FileUtils.copyURLToFile(new URL(urlForOS), localServerCopy, 30000, 5000);
                                unzip(localServerCopy, extracted);
                                FUTURE.complete(extracted);
                            } catch(IOException e) {
                                FUTURE.completeExceptionally(e);
                            }
                        }).start();
                    } else
                        FUTURE.complete(extracted);
                }
            }
        }
    }

    private static void unzip(File fileZip, File destDir) throws IOException {
        final byte[] buffer = new byte[1024];
        try(final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(fileZip))) {
            ZipEntry zipEntry = inputStream.getNextEntry();
            while(zipEntry != null) {
                final File newFile = newFile(destDir, zipEntry);
                if(zipEntry.isDirectory()) {
                    if(!newFile.mkdirs()) {
                        inputStream.closeEntry();
                        return;
                    }
                } else {
                    try(final FileOutputStream outputStream = new FileOutputStream(newFile)) {
                        int len;
                        while((len = inputStream.read(buffer)) > 0)
                            outputStream.write(buffer, 0, len);
                    }
                }

                zipEntry = inputStream.getNextEntry();
            }

            inputStream.closeEntry();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        final File destFile = new File(destinationDir, zipEntry.getName());
        final String destDirPath = destinationDir.getCanonicalPath();
        final String destFilePath = destFile.getCanonicalPath();
        if(!destFilePath.startsWith(destDirPath + File.separator))
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        return destFile;
    }

    static File createTempServer(World world) {
        final File tempServer = new File(TEMP_DIRECTORY, "server-" + world.getWorldName());
        if(!tempServer.exists()) {
            if(!tempServer.mkdirs()) return null;

            FUTURE.whenComplete((original, throwable) -> {
                if(throwable != null)
                    VanillaGeneratorPlugin.getInstance().getLogger().error("Could not download server binaries", throwable);
                else
                    try {
                        FileUtils.copyDirectory(original, tempServer);

                        if(!SystemUtils.IS_OS_WINDOWS) {
                            final File startFile = new File(tempServer, "start.sh");
                            final File binaryFile = new File(tempServer, "bedrock_server");
                            try(final FileOutputStream outputStream = new FileOutputStream(startFile)) {
                                outputStream.write("#!/bin/bash\nLD_LIBRARY_PATH=. ./bedrock_server".getBytes());
                            } catch(IOException e) {
                                VanillaGeneratorPlugin.getInstance().getLogger().error("Could now write bash script for starting BDS", e);
                            }

                            try {
                                Files.setPosixFilePermissions(startFile.toPath(), PosixFilePermissions.fromString("rwxr--r--"));
                            } catch(IOException e) {
                                VanillaGeneratorPlugin.getInstance().getLogger().error("Could not set execute flag on bash script", e);
                            }

                            try {
                                Files.setPosixFilePermissions(binaryFile.toPath(), PosixFilePermissions.fromString("rwxr--r--"));
                            } catch(IOException e) {
                                VanillaGeneratorPlugin.getInstance().getLogger().error("Could not set execute flag on bedrock server binary", e);
                            }
                        }
                    } catch(IOException e) {
                        VanillaGeneratorPlugin.getInstance().getLogger().error("Could not copy from server template to world server directory", e);
                    }
            }).join();
        }

        updateServerProperties(tempServer, world);

        return tempServer;
    }

    private static void updateServerProperties(File tempServer, World world) {
        final File serverProperties = new File(tempServer, "server.properties");

        final List<String> lines = new ArrayList<>();
        try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(serverProperties))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("server-port=")) line = "server-port=0";
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
            VanillaGeneratorPlugin.getInstance().getLogger().error("Could not edit world servers server.properties", e);
        }

        if(!serverProperties.delete()) {
            VanillaGeneratorPlugin.getInstance().getLogger().error("Could not delete world servers server.properties");
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
            VanillaGeneratorPlugin.getInstance().getLogger().error("Could not edit world servers server.properties", e);
        }
    }

}
