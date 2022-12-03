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

package de.kcodeyt.vanilla.generator.client.handler;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.BinaryStream;
import com.nukkitx.math.vector.Vector2i;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.SubChunkData;
import com.nukkitx.protocol.bedrock.data.SubChunkRequestResult;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import de.kcodeyt.vanilla.generator.chunk.ChunkData;
import de.kcodeyt.vanilla.generator.client.Client;
import de.kcodeyt.vanilla.util.Palette;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@RequiredArgsConstructor
public class PlayingHandler implements BedrockPacketHandler {

    private final Client client;

    @Override
    public boolean handle(DisconnectPacket disconnectPacket) {
        this.client.close();
        return true;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket networkChunkPublisherUpdatePacket) {
        if(this.client.getCurrentDimension() != this.client.getWorld().getDimension()) return true;

        final Vector3i position = networkChunkPublisherUpdatePacket.getPosition();

        this.client.setNetworkPosition(new BlockVector3(position.getX() >> 4, 0, position.getZ() >> 4));
        return true;
    }

    @Override
    public boolean handle(LevelChunkPacket levelChunkPacket) {
        if(this.client.getCurrentDimension() != this.client.getWorld().getDimension()) return true;

        final BinaryStream binaryStream = new BinaryStream(levelChunkPacket.getData());
        final Int2ObjectMap<int[]> biomeSections = new Int2ObjectOpenHashMap<>();

        int[] biomesLast = null;
        for(int y = this.client.getWorld().getMinY(); y < this.client.getWorld().getMaxY(); y++) {
            final int header = binaryStream.getByte();
            final int version = header >> 1;

            final int[] fullBiomes = new int[Palette.SIZE];

            if(version == 0) {
                final int biomeData = binaryStream.getVarInt();

                for(int i = 0; i < Palette.SIZE; i++) fullBiomes[i] = biomeData;

                biomeSections.put(y, fullBiomes);
                biomesLast = fullBiomes;
            } else if(version != 127) {
                final short[] indices = Palette.parseIndices(binaryStream, version);
                final int[] biomePalette = new int[binaryStream.getVarInt()];
                for(int i = 0; i < biomePalette.length; i++)
                    biomePalette[i] = binaryStream.getVarInt();

                for(int i = 0; i < Palette.SIZE; i++) fullBiomes[i] = biomePalette[indices[i]];

                biomeSections.put(y, fullBiomes);
                biomesLast = fullBiomes;
            } else {
                if(biomesLast == null) biomesLast = new int[Palette.SIZE];
                System.arraycopy(biomesLast, 0, fullBiomes, 0, Palette.SIZE);

                biomeSections.put(y, fullBiomes);
            }

            biomeSections.put(y, fullBiomes);
        }

        final long chunkHash = Level.chunkHash(levelChunkPacket.getChunkX(), levelChunkPacket.getChunkZ());

        if(levelChunkPacket.getSubChunkLimit() == -1) {
            this.client.addChunk(chunkHash, new ChunkData(this.client.getWorld(), levelChunkPacket.getChunkX(), levelChunkPacket.getChunkZ(), Collections.emptyList(), biomeSections));
        } else {
            this.client.cacheBiomes(chunkHash, biomeSections);

            final SubChunkRequestPacket subChunkRequestPacket = new SubChunkRequestPacket();
            subChunkRequestPacket.setDimension(this.client.getCurrentDimension());
            final Vector3i networkPos = Vector3i.from(this.client.getNetworkPosition().getX(), this.client.getNetworkPosition().getY(), this.client.getNetworkPosition().getZ());
            subChunkRequestPacket.setSubChunkPosition(networkPos);

            for(int y = 0; y <= levelChunkPacket.getSubChunkLimit(); y++)
                subChunkRequestPacket.getPositionOffsets().add(Vector3i.
                        from(levelChunkPacket.getChunkX(), y + this.client.getWorld().getMinY(), levelChunkPacket.getChunkZ()).
                        sub(networkPos));

            this.client.sendPacket(subChunkRequestPacket);
        }

        return true;
    }

    @Override
    public boolean handle(SubChunkPacket subChunkPacket) {
        if(this.client.getCurrentDimension() != this.client.getWorld().getDimension()) return true;

        final Map<Vector2i, List<SubChunkData>> subChunks = new HashMap<>();
        for(SubChunkData subChunk : subChunkPacket.getSubChunks()) {
            if(subChunk.getResult() == SubChunkRequestResult.CHUNK_NOT_FOUND) continue;

            final Vector3i position = subChunk.getPosition().add(subChunkPacket.getCenterPosition());
            final List<SubChunkData> subChunkData = subChunks.computeIfAbsent(position.toVector2(true), k -> new ArrayList<>());
            subChunkData.add(subChunk);
        }

        for(Map.Entry<Vector2i, List<SubChunkData>> entry : subChunks.entrySet()) {
            final int chunkX = entry.getKey().getX();
            final int chunkZ = entry.getKey().getY();
            final Int2ObjectMap<int[]> biomes = this.client.getBiomes(Level.chunkHash(chunkX, chunkZ));

            this.client.addChunk(Level.chunkHash(chunkX, chunkZ), new ChunkData(this.client.getWorld(), chunkX, chunkZ, entry.getValue(), biomes));
        }

        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket movePlayerPacket) {
        if(movePlayerPacket.getRuntimeEntityId() != this.client.getUniqueEntityId() &&
           movePlayerPacket.getRuntimeEntityId() != this.client.getRuntimeEntityId()) return true;

        final Vector3f position = movePlayerPacket.getPosition();
        final Vector3f rotation = movePlayerPacket.getRotation();

        this.client.move(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX());
        return true;
    }

    @Override
    public boolean handle(MoveEntityAbsolutePacket moveEntityAbsolutePacket) {
        if(moveEntityAbsolutePacket.getRuntimeEntityId() != this.client.getUniqueEntityId() &&
           moveEntityAbsolutePacket.getRuntimeEntityId() != this.client.getRuntimeEntityId()) return true;

        final Vector3f position = moveEntityAbsolutePacket.getPosition();
        final Vector3f rotation = moveEntityAbsolutePacket.getRotation();
        this.client.move(position.getX(), position.getY(), position.getZ(), rotation.getY(), rotation.getX());
        return true;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket moveEntityDeltaPacket) {
        if(moveEntityDeltaPacket.getRuntimeEntityId() != this.client.getUniqueEntityId() &&
           moveEntityDeltaPacket.getRuntimeEntityId() != this.client.getRuntimeEntityId()) return true;

        this.client.move(
                moveEntityDeltaPacket.getX(),
                moveEntityDeltaPacket.getY(),
                moveEntityDeltaPacket.getZ(),
                moveEntityDeltaPacket.getYaw(),
                moveEntityDeltaPacket.getPitch());
        return true;
    }

    @Override
    public boolean handle(ChangeDimensionPacket changeDimensionPacket) {
        this.client.setCurrentDimension(changeDimensionPacket.getDimension());

        final Location currentPosition = client.getCurrentPosition();
        final Vector3i newPosition = Vector3i.from(currentPosition.getX(), Math.min(64, currentPosition.getY()), currentPosition.getZ());

        final PlayerActionPacket playerActionPacket = new PlayerActionPacket();
        playerActionPacket.setRuntimeEntityId(this.client.getRuntimeEntityId());
        playerActionPacket.setBlockPosition(newPosition);
        playerActionPacket.setResultPosition(newPosition);
        playerActionPacket.setFace(0);
        playerActionPacket.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS);

        this.client.sendPacket(playerActionPacket);

        this.client.checkReadyState();
        return true;
    }

    @Override
    public boolean handle(CommandOutputPacket commandOutputPacket) {
        final Consumer<CommandOutputPacket> consumer = this.client.getLatestCommandConsumer();
        if(consumer != null) consumer.accept(commandOutputPacket);
        return true;
    }

}
