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

import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.async.Future;
import de.kcodeyt.vanilla.behavior.LootTableManager;
import de.kcodeyt.vanilla.generator.chunk.ChunkRequest;
import de.kcodeyt.vanilla.generator.client.Client;
import de.kcodeyt.vanilla.generator.client.clientdata.DeviceOS;
import de.kcodeyt.vanilla.generator.client.clientdata.LoginData;
import de.kcodeyt.vanilla.generator.client.clientdata.UIProfile;
import de.kcodeyt.vanilla.world.World;
import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class VanillaServer {

    @Getter
    private final long startupTime;
    @Getter
    private final World world;
    private final Queue<ChunkRequest> queue = new ConcurrentLinkedQueue<>();
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final AtomicBoolean manualClose = new AtomicBoolean(false);
    private int port;
    private ProcessWrapper processWrapper;
    @Getter
    private LootTableManager lootTableManager;

    private boolean firstSeen = true;

    public VanillaServer(World world) {
        this.startupTime = System.currentTimeMillis();
        this.world = world;
        final File tempServer = BedrockDedicatedServer.createTempServer(this.world);
        if(tempServer == null) return;

        final String tempPath = tempServer.getAbsolutePath() + File.separator;
        final ProcessBuilder builder = new ProcessBuilder((SystemUtils.IS_OS_WINDOWS ? tempPath + "bedrock_server.exe" : tempPath + "start.sh"));
        builder.directory(tempServer);

        this.lootTableManager = new LootTableManager();
        final File behaviorPacksDir = new File(tempServer, "behavior_packs");
        if(behaviorPacksDir.exists() && behaviorPacksDir.isDirectory())
            this.lootTableManager.loadPacks(behaviorPacksDir);

        try {
            final long startTime = System.currentTimeMillis();
            this.processWrapper = new ProcessWrapper(builder, line -> {
                if(line.contains("IPv4 supported, port:") && this.firstSeen) {
                    String[] split = line.split(" ");
                    this.port = Integer.parseInt(split[split.length - 1]);
                    VanillaGeneratorPlugin.getInstance().getLogger().info("Server " + this.world.getWorldName() + " bound to " + this.port + " (Started in " + (Math.round(((System.currentTimeMillis() - startTime) / 1000f) * 100) / 100f) + "s!)");
                    this.firstSeen = false;

                    this.connect();
                }
            });
        } catch(IOException e) {
            VanillaGeneratorPlugin.getInstance().getLogger().error("Could not start BDS", e);
        }
    }

    private void connect() {
        for(int i = 0; i < VanillaGeneratorPlugin.getInstance().getFakePlayersPerGenerator(); i++)
            this.world.getPlugin()
                    .getExecutorService()
                    .schedule(this::connectClient, (i + 1) * 2000L, TimeUnit.MILLISECONDS);
    }

    public Client getClient() {
        return this.clients.isEmpty() ? null : this.clients.get(0);
    }

    private LoginData generateLoginData() {
        return new LoginData(
                "GenBot_" + (this.clients.size() + 1),
                Long.toString(ThreadLocalRandom.current().nextLong()),
                UUID.randomUUID(),
                VanillaGeneratorPlugin.STEVE_SKIN,
                DeviceOS.DEDICATED,
                UIProfile.CLASSIC
        );
    }

    private void connectClient() {
        try {
            final LoginData loginData = this.generateLoginData();
            final Client client = new Client(this, loginData, this.world.getPlugin().getEncryptionKeyFactory(), this.queue);
            this.clients.add(client.onDisconnect(reason -> {
                final ChunkRequest chunkRequest = client.getCurrent();
                if(chunkRequest != null) this.queue.offer(chunkRequest);

                this.clients.remove(client);
                if(this.manualClose.get()) return;

                this.world.getPlugin().getExecutorService().schedule(this::connectClient, 500, TimeUnit.MILLISECONDS);
            }).connect(new InetSocketAddress(InetAddress.getLocalHost(), this.port)).join());
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void requestChunk(BaseFullChunk fullChunk) {
        final ChunkRequest request = new ChunkRequest(fullChunk.getX(), fullChunk.getZ(), new Future<>());
        this.queue.offer(request);

        try {
            request.getFuture().get().build(this, fullChunk);
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isLevel(Level level) {
        return this.world.getLevel().equals(level);
    }

    public void close() {
        this.manualClose.set(true);
        for(Client client : this.clients)
            client.disconnect("Closing chunk generator");
        this.processWrapper.kill();
    }

}
