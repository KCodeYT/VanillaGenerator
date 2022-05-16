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

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.BinaryStream;
import com.nukkitx.math.vector.Vector2i;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.SubChunkData;
import com.nukkitx.protocol.bedrock.data.SubChunkRequestResult;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import de.kcodeyt.vanilla.generator.chunk.ChunkData;
import de.kcodeyt.vanilla.generator.chunk.ChunkRequest;
import de.kcodeyt.vanilla.generator.client.clientdata.LoginData;
import de.kcodeyt.vanilla.generator.network.ConsumerPacketHandler;
import de.kcodeyt.vanilla.generator.network.EncryptionHandler;
import de.kcodeyt.vanilla.generator.network.EncryptionKeyFactory;
import de.kcodeyt.vanilla.generator.network.PlayerConnectionState;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.jwt.JwtSignatureException;
import de.kcodeyt.vanilla.jwt.JwtToken;
import de.kcodeyt.vanilla.util.Palette;
import de.kcodeyt.vanilla.world.World;
import io.netty.util.AsciiString;
import lombok.Getter;

import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class Client {

    private final EncryptionKeyFactory keyFactory;
    private final ScheduledExecutorService executorService;

    private final BedrockClient bedrockClient;
    private final VanillaServer vanillaServer;
    private final LoginData loginData;
    private final World world;
    private final Level level;
    private final List<Consumer<CommandOutputPacket>> commandConsumers;
    private final Queue<ChunkRequest> queue;
    private final Set<ChunkData> chunks;
    private final Map<Vector2i, Biome[]> chunkBiomes;
    private BedrockClientSession clientSession;
    private PlayerConnectionState state = PlayerConnectionState.HANDSHAKE;
    private Location spawn;
    private int currentDimension;
    private Location currentPos;
    private BlockVector3 networkPos;
    private long ownId;
    private long runtimeId;
    private Consumer<DisconnectReason> disconnectConsumer;
    private boolean disconnected;
    private ScheduledFuture<?> updateFuture;
    @Getter
    private ChunkRequest current;

    private InetSocketAddress serverAddress;

    public Client(VanillaServer vanillaServer, LoginData loginData,
                  EncryptionKeyFactory encryptionKeyFactory, Queue<ChunkRequest> queue) {
        this.vanillaServer = vanillaServer;
        this.loginData = loginData;
        this.world = vanillaServer.getWorld();
        this.level = this.world.getLevel();
        this.keyFactory = encryptionKeyFactory;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.queue = queue;
        this.commandConsumers = new ArrayList<>();
        this.chunks = new CopyOnWriteArraySet<>();
        this.chunkBiomes = new ConcurrentHashMap<>();
        this.bedrockClient = new BedrockClient(new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(10000) + 30000));
        this.bedrockClient.setRakNetVersion(Network.CODEC.getRaknetProtocolVersion());
        this.bedrockClient.bind().join();
    }

    public CompletableFuture<Client> connect(InetSocketAddress serverAddress) {
        return this.bedrockClient.connect(serverAddress).whenComplete((session, connectError) -> {
            if(connectError != null) {
                Server.getInstance().getLogger().error("Could not connect to background server! Address: " + serverAddress, connectError);
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

            final BatchHandler batchHandler = this.clientSession.getBatchHandler();
            this.clientSession.setBatchHandler((bedrockSession, byteBuf, collection) -> {
                try {
                    batchHandler.handle(bedrockSession, byteBuf, collection);
                } catch(Throwable throwable1) {
                    throwable1.printStackTrace();
                }
            });
            this.clientSession.setPacketHandler(new ConsumerPacketHandler(this::handlePacket));
            this.login();
        }).thenApply(session -> this);
    }

    public void disconnect(String message) {
        if(message != null && message.length() > 0) {
            final DisconnectPacket disconnectPacket = new DisconnectPacket();
            disconnectPacket.setKickMessage(message);
            this.send(disconnectPacket);
            this.internalClose();
        } else {
            this.internalClose();
        }
    }

    private void sendImmediately(BedrockPacket packet) {
        if(this.clientSession == null) return;
        this.clientSession.sendPacketImmediately(packet);
    }

    private void send(BedrockPacket packet) {
        if(this.clientSession == null) return;
        this.clientSession.sendPacket(packet);
    }

    private ChunkData getChunk(int chunkX, int chunkZ) {
        for(ChunkData chunkData : this.chunks) {
            if(chunkData.getX() == chunkX && chunkData.getZ() == chunkZ)
                return chunkData;
        }
        return null;
    }

    private void update() {
        while(true) {
            if(this.current != null) {
                final ChunkData chunkData = this.getChunk(this.current.getX(), this.current.getZ());
                if(chunkData != null) {
                    chunkData.setBiomes(this.chunkBiomes.remove(Vector2i.from(this.current.getX(), this.current.getZ())));
                    this.current.getFuture().resolve(chunkData);
                    this.chunks.remove(chunkData);
                    this.current = null;
                } else {
                    this.moveToChunk(this.current);
                    return;
                }
            }

            this.current = this.queue.poll();
            if(this.current != null) {
                final ChunkData chunkData = this.getChunk(this.current.getX(), this.current.getZ());
                if(chunkData != null) {
                    chunkData.setBiomes(this.chunkBiomes.remove(Vector2i.from(this.current.getX(), this.current.getZ())));
                    this.current.getFuture().resolve(chunkData);
                    this.chunks.remove(chunkData);
                    this.current = null;
                } else {
                    this.moveToChunk(this.current);
                    return;
                }
            }
        }
    }

    private void internalClose() {
        if(this.clientSession != null) {
            if(!this.clientSession.isClosed()) this.clientSession.disconnect();
            if(this.updateFuture != null) this.updateFuture.cancel(true);

            this.updateFuture = null;
            this.clientSession = null;
        }
    }

    private void login() {
        if(this.state == PlayerConnectionState.HANDSHAKE) {
            this.state = PlayerConnectionState.LOGIN;

            final MojangLoginForger mojangLoginForger = new MojangLoginForger();
            mojangLoginForger.setPublicKey(this.keyFactory.getKeyPair().getPublic());
            mojangLoginForger.setUsername(this.loginData.getName());
            mojangLoginForger.setUuid(this.loginData.getUniqueId());
            mojangLoginForger.setXuid(this.loginData.getXuid());
            mojangLoginForger.setSkinData(this.loginData.buildSkinData(ThreadLocalRandom.current(), this.serverAddress));

            final String jwt = "{\"chain\":[\"" + mojangLoginForger.forge(this.keyFactory.getKeyPair().getPrivate()) + "\"]}";
            final String skin = mojangLoginForger.forgeSkin(this.keyFactory.getKeyPair().getPrivate());
            final LoginPacket loginPacket = new LoginPacket();
            loginPacket.setProtocolVersion(Network.CODEC.getProtocolVersion());
            loginPacket.setChainData(new AsciiString(jwt));
            loginPacket.setSkinData(new AsciiString(skin));
            this.sendImmediately(loginPacket);
        }
    }

    private void sendChunkRadius() {
        final RequestChunkRadiusPacket chunkRadius = new RequestChunkRadiusPacket();
        chunkRadius.setRadius(Server.getInstance().getViewDistance());
        this.send(chunkRadius);
    }

    private void handlePacket(BedrockPacket bedrockPacket) {
        if(bedrockPacket.getPacketType() == BedrockPacketType.DISCONNECT) {
            final DisconnectPacket disconnect = (DisconnectPacket) bedrockPacket;
            System.out.println("Disconnect: " + disconnect.getKickMessage());
            return;
        }

        if(this.world.getDimension() == this.currentDimension) {
            if(bedrockPacket.getPacketType() == BedrockPacketType.NETWORK_CHUNK_PUBLISHER_UPDATE) {
                final NetworkChunkPublisherUpdatePacket packet = (NetworkChunkPublisherUpdatePacket) bedrockPacket;
                this.networkPos = new BlockVector3(packet.getPosition().getX() >> 4, 0, packet.getPosition().getZ() >> 4);
                return;
            }

            if(bedrockPacket.getPacketType() == BedrockPacketType.LEVEL_CHUNK) {
                final LevelChunkPacket packet = (LevelChunkPacket) bedrockPacket;
                final SubChunkRequestPacket subChunkRequestPacket = new SubChunkRequestPacket();
                subChunkRequestPacket.setDimension(this.currentDimension);
                final Vector3i networkPos = Vector3i.from(this.networkPos.getX(), this.networkPos.getY(), this.networkPos.getZ());
                subChunkRequestPacket.setSubChunkPosition(networkPos);

                final BinaryStream binaryStream = new BinaryStream(packet.getData());

                int[] lastBiomes = null;
                for(int y = this.world.getMinY(); y < this.world.getMaxY(); y++) {
                    final int header = binaryStream.getByte();
                    final int version = header >> 1;

                    if(version == 0) {
                        final int biomeData = binaryStream.getVarInt();
                        final int[] fullBiomes = new int[Palette.SIZE];

                        for(int i = 0; i < Palette.SIZE; i++)
                            fullBiomes[i] = biomeData;
                        lastBiomes = fullBiomes;
                    } else if(version != 127) {
                        final short[] indices = Palette.parseIndices(binaryStream, version);
                        final int[] biomes = new int[binaryStream.getVarInt()];
                        for(int i = 0; i < biomes.length; i++)
                            biomes[i] = binaryStream.getVarInt();

                        final int[] fullBiomes = new int[Palette.SIZE];
                        for(int i = 0; i < Palette.SIZE; i++)
                            fullBiomes[i] = biomes[indices[i]];
                        lastBiomes = fullBiomes;
                    }
                }

                if(lastBiomes != null) {
                    final Biome[] biomes = new Biome[256];
                    for(int x = 0; x < 16; x++)
                        for(int z = 0; z < 16; z++)
                            biomes[(x << 4) | z] = Biome.getBiome(lastBiomes[(x << 8) | (15 << 4) | z]);

                    this.chunkBiomes.put(Vector2i.from(packet.getChunkX(), packet.getChunkZ()), biomes);
                }

                for(int y = 0; y <= packet.getSubChunkLimit(); y++)
                    subChunkRequestPacket.getPositionOffsets().add(Vector3i.
                            from(packet.getChunkX(), y + this.world.getMinY(), packet.getChunkZ()).
                            sub(networkPos));

                this.send(subChunkRequestPacket);
                return;
            }

            if(bedrockPacket.getPacketType() == BedrockPacketType.SUB_CHUNK) {
                final SubChunkPacket subChunkPacket = (SubChunkPacket) bedrockPacket;
                final Map<Vector2i, List<SubChunkData>> subChunks = new HashMap<>();
                for(SubChunkData subChunk : subChunkPacket.getSubChunks()) {
                    if(subChunk.getResult() == SubChunkRequestResult.CHUNK_NOT_FOUND) continue;

                    final Vector3i position = subChunk.getPosition().add(subChunkPacket.getCenterPosition());
                    final List<SubChunkData> subChunkData = subChunks.computeIfAbsent(position.toVector2(true), k -> new ArrayList<>());
                    subChunkData.add(subChunk);
                }

                for(Map.Entry<Vector2i, List<SubChunkData>> entry : subChunks.entrySet()) {
                    this.chunks.add(new ChunkData(this.world, entry.getKey().getX(), entry.getKey().getY(), entry.getValue()));
                }
            }

            if(bedrockPacket.getPacketType() == BedrockPacketType.BLOCK_ENTITY_DATA) {
                final BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) bedrockPacket;

            }

            if(bedrockPacket.getPacketType() == BedrockPacketType.UPDATE_SUB_CHUNK_BLOCKS) {
                final UpdateSubChunkBlocksPacket updateSubChunkBlocksPacket = (UpdateSubChunkBlocksPacket) bedrockPacket;

            }
        }

        if(this.state == PlayerConnectionState.LOGIN) {
            if(bedrockPacket.getPacketType() == BedrockPacketType.PLAY_STATUS) {
                final PlayStatusPacket packetPlayState = (PlayStatusPacket) bedrockPacket;
                if(packetPlayState.getStatus() != PlayStatusPacket.Status.LOGIN_SUCCESS)
                    this.destroy();
                this.state = PlayerConnectionState.RESOURCE_PACK;
                return;
            } else if(bedrockPacket.getPacketType() == BedrockPacketType.SERVER_TO_CLIENT_HANDSHAKE) {
                final ServerToClientHandshakePacket packetEncryptionRequest = (ServerToClientHandshakePacket) bedrockPacket;

                try {
                    final JwtToken token = JwtToken.parse(packetEncryptionRequest.getJwt());
                    final String publicKeyB64 = token.getHeader().getProperty(String.class, "x5u");
                    final PublicKey publicKey = this.keyFactory.createPublicKey(publicKeyB64);

                    if(token.validateSignature(publicKey)) {
                        final EncryptionHandler encryptionHandler = new EncryptionHandler(this.keyFactory, publicKey);
                        if(encryptionHandler.beginServersideEncryption(Base64.getDecoder().decode(token.getClaim(String.class, "salt"))))
                            this.clientSession.enableEncryption(new SecretKeySpec(encryptionHandler.getServerKey(), "AES"));

                        this.send(new ClientToServerHandshakePacket());

                        final ClientCacheStatusPacket cacheStatus = new ClientCacheStatusPacket();
                        cacheStatus.setSupported(false);
                        this.send(cacheStatus);
                        return;
                    }

                    this.disconnect("Invalid jwt signature");
                } catch(JwtSignatureException | NoSuchAlgorithmException e) {
                    System.out.println("Invalid JWT signature from server: ");
                    e.printStackTrace();
                }
            } else if(bedrockPacket.getPacketType() == BedrockPacketType.RESOURCE_PACKS_INFO) {
                this.state = PlayerConnectionState.RESOURCE_PACK;
            }
        }

        if(this.state == PlayerConnectionState.RESOURCE_PACK) {
            if(bedrockPacket.getPacketType() == BedrockPacketType.RESOURCE_PACKS_INFO) {
                final ResourcePackClientResponsePacket packetResourcePackResponse = new ResourcePackClientResponsePacket();
                packetResourcePackResponse.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
                this.send(packetResourcePackResponse);
            } else if(bedrockPacket.getPacketType() == BedrockPacketType.RESOURCE_PACK_STACK) {
                final ResourcePackClientResponsePacket packetResourcePackResponse = new ResourcePackClientResponsePacket();
                packetResourcePackResponse.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
                this.send(packetResourcePackResponse);

                this.state = PlayerConnectionState.PLAYING;
                this.sendChunkRadius();

                final SetLocalPlayerAsInitializedPacket packetSetLocalPlayerAsInitialized = new SetLocalPlayerAsInitializedPacket();
                packetSetLocalPlayerAsInitialized.setRuntimeEntityId(this.ownId);
                this.send(packetSetLocalPlayerAsInitialized);
            }
        }

        if(bedrockPacket.getPacketType() == BedrockPacketType.START_GAME) {
            final StartGamePacket startGame = ((StartGamePacket) bedrockPacket);

            this.spawn = new Location(startGame.getDefaultSpawn().getX(), startGame.getDefaultSpawn().getY(), startGame.getDefaultSpawn().getZ());
            this.currentDimension = startGame.getDimensionId();
            this.ownId = startGame.getUniqueEntityId();
            this.runtimeId = startGame.getRuntimeEntityId();

            final RespawnPacket respawnPacket = new RespawnPacket();
            respawnPacket.setPosition(startGame.getPlayerPosition());
            respawnPacket.setState(RespawnPacket.State.CLIENT_READY);
            this.send(respawnPacket);
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.PLAY_STATUS) {
            final PlayStatusPacket.Status playState = ((PlayStatusPacket) bedrockPacket).getStatus();
            if(playState == PlayStatusPacket.Status.PLAYER_SPAWN) {
                this.move(this.spawn);

                final AdventureSettingsPacket adventureSettingsPacket = new AdventureSettingsPacket();
                adventureSettingsPacket.getSettings().add(AdventureSetting.MAY_FLY);
                adventureSettingsPacket.getSettings().add(AdventureSetting.FLYING);
                adventureSettingsPacket.setUniqueEntityId(this.runtimeId);
                this.send(adventureSettingsPacket);

                this.move(new Location(this.currentPos.getX(), 255, this.currentPos.getZ(), 0f, 0f));
                this.checkReadyState();
            }
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.MOVE_PLAYER) {
            final MovePlayerPacket movePlayerPacket = (MovePlayerPacket) bedrockPacket;
            if(movePlayerPacket.getRuntimeEntityId() == this.ownId || movePlayerPacket.getRuntimeEntityId() == this.runtimeId) {
                final Vector3f position = movePlayerPacket.getPosition();
                final Vector3f rotation = movePlayerPacket.getRotation();
                this.move(new Location(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX()));
            }
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.MOVE_ENTITY_ABSOLUTE) {
            final MoveEntityAbsolutePacket moveEntityAbsolutePacket = (MoveEntityAbsolutePacket) bedrockPacket;
            if(moveEntityAbsolutePacket.getRuntimeEntityId() == this.ownId || moveEntityAbsolutePacket.getRuntimeEntityId() == this.runtimeId) {
                final Vector3f position = moveEntityAbsolutePacket.getPosition();
                final Vector3f rotation = moveEntityAbsolutePacket.getRotation();
                this.move(new Location(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX()));
            }
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.MOVE_ENTITY_DELTA) {
            final MoveEntityDeltaPacket moveEntityDeltaPacket = (MoveEntityDeltaPacket) bedrockPacket;
            if(moveEntityDeltaPacket.getRuntimeEntityId() == this.ownId || moveEntityDeltaPacket.getRuntimeEntityId() == this.runtimeId) {
                this.move(new Location(
                        moveEntityDeltaPacket.getX(),
                        moveEntityDeltaPacket.getY(),
                        moveEntityDeltaPacket.getZ(),
                        moveEntityDeltaPacket.getYaw(),
                        moveEntityDeltaPacket.getPitch()));
            }
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.CHANGE_DIMENSION) {
            final ChangeDimensionPacket changeDimensionPacket = (ChangeDimensionPacket) bedrockPacket;
            this.currentDimension = changeDimensionPacket.getDimension();
            final PlayerActionPacket playerActionPacket = new PlayerActionPacket();
            playerActionPacket.setRuntimeEntityId(this.runtimeId);
            playerActionPacket.setBlockPosition(Vector3i.from(this.currentPos.x, Math.min(64, this.currentPos.y), this.currentPos.z));
            playerActionPacket.setFace(0);
            playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);
            this.send(playerActionPacket);
            this.checkReadyState();
        } else if(bedrockPacket.getPacketType() == BedrockPacketType.COMMAND_OUTPUT) {
            final CommandOutputPacket commandOutputPacket = (CommandOutputPacket) bedrockPacket;
            final Consumer<CommandOutputPacket> consumer = this.commandConsumers.remove(0);
            if(consumer != null)
                consumer.accept(commandOutputPacket);
        }
    }

    private void checkReadyState() {
        if(this.currentDimension == this.world.getDimension()) {
            this.world.getPlugin().getLogger().info("Level " + this.world.getWorldName() + " successfully connected!");
            this.updateFuture = this.executorService.scheduleAtFixedRate(this::update, 50, 50, TimeUnit.MILLISECONDS);
        } else
            this.requestDimensionChange(this.world.getDimension());
    }

    private void sendCommand(String command) {
        this.sendCommand(command, null);
    }

    public void sendCommand(String command, Consumer<CommandOutputPacket> consumer) {
        final CommandRequestPacket commandRequestPacket = new CommandRequestPacket();
        commandRequestPacket.setInternal(false);
        commandRequestPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, this.loginData.getUniqueId(), "none", ThreadLocalRandom.current().nextLong()));
        commandRequestPacket.setCommand(command);
        this.commandConsumers.add(consumer);
        this.send(commandRequestPacket);
    }

    private void requestDimensionChange(int dimension) {
        final String portalBlock = dimension == Level.DIMENSION_OVERWORLD || dimension == Level.DIMENSION_NETHER ? "portal" : "end_portal";
        this.sendCommand("/setblock " + this.currentPos.getFloorX() + " " + this.currentPos.getFloorY() + " " + this.currentPos.getFloorZ() + " " + portalBlock);
        this.move(this.currentPos.add(0, 0.5, 0));
    }

    private void move(Location target) {
        final MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(this.runtimeId);
        target.y = Math.min(target.getY(), 260);
        movePlayerPacket.setPosition(Vector3f.from(target.getX(), target.getY(), target.getZ()));
        movePlayerPacket.setRotation(Vector3f.from(target.getPitch(), target.getYaw(), target.getYaw()));
        movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
        movePlayerPacket.setMode(MovePlayerPacket.Mode.TELEPORT);
        movePlayerPacket.setTick(0);
        this.send(movePlayerPacket);
        this.currentPos = target;
    }

    private void destroy() {
        this.clientSession.disconnect();
    }

    private void moveToChunk(ChunkRequest chunkSquare) {
        final BlockVector3 targetPos = chunkSquare.getCenterPosition();
        final int y = this.world.getDimension() == Level.DIMENSION_NETHER ? 132 : 260;
        this.move(new Location(targetPos.getX(), y, targetPos.getZ(), 0f, 0f));
    }

    public Client onDisconnect(Consumer<DisconnectReason> disconnectConsumer) {
        this.disconnectConsumer = disconnectConsumer;
        return this;
    }

}
