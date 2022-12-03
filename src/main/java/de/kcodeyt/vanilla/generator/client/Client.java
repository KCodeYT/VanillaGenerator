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

package de.kcodeyt.vanilla.generator.client;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockVector3;
import com.nimbusds.jose.JOSEException;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.generator.chunk.ChunkData;
import de.kcodeyt.vanilla.generator.chunk.ChunkRequest;
import de.kcodeyt.vanilla.generator.client.clientdata.LoginData;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.world.World;
import io.netty.util.AsciiString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class Client {

    private final ScheduledExecutorService executorService;

    private final BedrockClient bedrockClient;
    private final VanillaServer vanillaServer;
    private final LoginData loginData;
    private final KeyPair keyPair;
    private final World world;
    private final Level level;
    private final List<Consumer<CommandOutputPacket>> commandConsumers;
    private final Queue<ChunkRequest> queue;
    private final Long2ObjectMap<ChunkData> chunks;
    private final Long2ObjectMap<Int2ObjectMap<int[]>> chunkBiomes;

    private BedrockClientSession clientSession;
    private ConnectionState currentState;

    private Consumer<DisconnectReason> disconnectConsumer;
    private boolean disconnected;
    private ScheduledFuture<?> updateFuture;

    @Setter
    private int currentDimension;
    @Setter
    private long uniqueEntityId;
    @Setter
    private long runtimeEntityId;

    @Setter
    private Location spawnPosition;
    @Setter
    private Location currentPosition;
    @Setter
    private BlockVector3 networkPosition;

    @Getter
    private ChunkRequest current;

    private InetSocketAddress serverAddress;

    public Client(VanillaServer vanillaServer, LoginData loginData, Queue<ChunkRequest> queue) {
        this.vanillaServer = vanillaServer;
        this.loginData = loginData;
        this.keyPair = EncryptionUtils.createKeyPair();
        this.world = vanillaServer.getWorld();
        this.level = this.world.getLevel();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.queue = queue;
        this.commandConsumers = new ArrayList<>();
        this.chunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
        this.chunkBiomes = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
        this.bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", 0));
        this.bedrockClient.setRakNetVersion(Network.CODEC.getRaknetProtocolVersion());
        this.bedrockClient.bind().join();
    }

    public CompletableFuture<Client> connect(InetSocketAddress serverAddress) {
        return this.bedrockClient.connect(serverAddress).whenComplete((session, connectError) -> {
            if(connectError != null) {
                VanillaGeneratorPlugin.getInstance().getLogger().error("Could not connect to background server! Address: " + serverAddress, connectError);
                return;
            }

            this.serverAddress = serverAddress;
            this.clientSession = session;
            this.clientSession.setPacketCodec(Network.CODEC);
            this.clientSession.addDisconnectHandler(disconnectReason -> {
                if(!this.disconnected) {
                    this.disconnected = true;
                    this.disconnectConsumer.accept(disconnectReason);
                }
            });

            this.setState(ConnectionState.NETWORK_INIT);
            this.initNetwork();
        }).thenApply(session -> this);
    }

    public void close() {
        if(this.clientSession != null) {
            if(!this.clientSession.isClosed()) this.clientSession.disconnect();
            this.clientSession = null;
        }

        if(this.updateFuture != null) this.updateFuture.cancel(true);
        this.updateFuture = null;

        this.bedrockClient.close();
    }

    public void sendPacketImmediately(BedrockPacket packet) {
        if(this.clientSession == null) return;
        this.clientSession.sendPacketImmediately(packet);
    }

    public void sendPacket(BedrockPacket packet) {
        if(this.clientSession == null) return;
        this.clientSession.sendPacket(packet);
    }

    public void setState(ConnectionState state) {
        if(this.clientSession == null) return;
        if(this.currentState == state) return;

        this.currentState = state;
        this.clientSession.setPacketHandler(state.newPacketHandler(this));
    }

    public void initNetwork() {
        if(this.currentState != ConnectionState.NETWORK_INIT) return;

        final RequestNetworkSettingsPacket requestNetworkSettingsPacket = new RequestNetworkSettingsPacket();
        requestNetworkSettingsPacket.setProtocolVersion(Network.CODEC.getProtocolVersion());

        this.sendPacketImmediately(requestNetworkSettingsPacket);
    }

    public void login() {
        if(this.currentState != ConnectionState.NETWORK_INIT) return;

        try {
            final LoginPacket loginPacket = new LoginPacket();
            loginPacket.setProtocolVersion(Network.CODEC.getProtocolVersion());
            loginPacket.setChainData(AsciiString.of(MojangLoginForger.forgeLoginChain(this.keyPair, this.loginData)));
            loginPacket.setSkinData(AsciiString.of(MojangLoginForger.forge(this.keyPair, this.loginData.buildSkinData(ThreadLocalRandom.current(), this.serverAddress))));
            this.sendPacketImmediately(loginPacket);
        } catch(JOSEException e) {
            this.world.getPlugin().getLogger().error("Could not build login packet for client!", e);
        }
    }

    public void checkReadyState() {
        if(this.currentDimension == this.world.getDimension()) {
            this.world.getPlugin().getLogger().info("Level " + this.world.getWorldName() + " successfully connected!");
            this.updateFuture = this.executorService.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
        } else
            this.requestDimensionChange(this.world.getDimension());
    }

    public void sendCommand(String command) {
        this.sendCommand(command, null);
    }

    public void sendCommand(String command, Consumer<CommandOutputPacket> consumer) {
        final CommandRequestPacket commandRequestPacket = new CommandRequestPacket();
        commandRequestPacket.setInternal(false);
        commandRequestPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, this.loginData.getUniqueId(), "none", ThreadLocalRandom.current().nextLong()));
        commandRequestPacket.setCommand(command);
        this.commandConsumers.add(consumer);

        this.sendPacket(commandRequestPacket);
    }

    public Consumer<CommandOutputPacket> getLatestCommandConsumer() {
        return this.commandConsumers.size() > 0 ? this.commandConsumers.remove(0) : null;
    }

    public void addChunk(long chunkHash, ChunkData chunkData) {
        this.chunks.put(chunkHash, chunkData);
    }

    public void cacheBiomes(long chunkHash, Int2ObjectMap<int[]> biomeSections) {
        this.chunkBiomes.put(chunkHash, biomeSections);
    }

    public Int2ObjectMap<int[]> getBiomes(long chunkHash) {
        return this.chunkBiomes.remove(chunkHash);
    }

    public void move(double x, double y, double z, double yaw, double pitch) {
        y = Math.min(y, 260);

        final MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(this.runtimeEntityId);
        movePlayerPacket.setPosition(Vector3f.from(x, y, z));
        movePlayerPacket.setRotation(Vector3f.from(pitch, yaw, yaw));
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setMode(MovePlayerPacket.Mode.TELEPORT);
        movePlayerPacket.setTick(0);

        this.sendPacket(movePlayerPacket);
        this.currentPosition = new Location(x, y, z, yaw, pitch);
    }

    private void requestDimensionChange(int dimension) {
        final String portalBlock = dimension == Level.DIMENSION_OVERWORLD || dimension == Level.DIMENSION_NETHER ? "portal" : "end_portal";
        this.sendCommand("/setblock " + this.currentPosition.getFloorX() + " " + this.currentPosition.getFloorY() + " " + this.currentPosition.getFloorZ() + " " + portalBlock);

        final Location positionToMove = this.currentPosition.floor().add(0.5, 0.5, 0.5);

        this.move(positionToMove.x, positionToMove.y, positionToMove.z, positionToMove.yaw, positionToMove.pitch);
    }

    private void update() {
        while(this.handleCurrentRequest())
            this.current = this.queue.poll();
    }

    private boolean handleCurrentRequest() {
        if(this.current == null) return true;

        final long chunkHash = Level.chunkHash(this.current.getX(), this.current.getZ());
        final ChunkData chunkData = this.chunks.get(chunkHash);

        if(chunkData == null) {
            this.moveToChunk(this.current);
            return false;
        }

        this.current.getFuture().complete(chunkData);
        this.chunks.remove(chunkHash);
        this.current = null;
        return true;
    }

    private void moveToChunk(ChunkRequest chunkSquare) {
        final BlockVector3 targetPos = chunkSquare.getCenterPosition();
        final int y = this.world.getDimension() == Level.DIMENSION_NETHER ? 132 : 260;

        this.move(targetPos.getX(), y, targetPos.getZ(), 0f, 0f);
    }

    public Client onDisconnect(Consumer<DisconnectReason> disconnectConsumer) {
        this.disconnectConsumer = disconnectConsumer;
        return this;
    }

}
