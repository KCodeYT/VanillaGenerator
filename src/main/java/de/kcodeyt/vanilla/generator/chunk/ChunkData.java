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

package de.kcodeyt.vanilla.generator.chunk;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import com.nukkitx.protocol.bedrock.data.SubChunkData;
import com.nukkitx.protocol.bedrock.data.SubChunkRequestResult;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.util.Palette;
import de.kcodeyt.vanilla.world.BlockEntityData;
import de.kcodeyt.vanilla.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteOrder;
import java.util.List;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@RequiredArgsConstructor
public class ChunkData {

    private final World world;
    private final int x;
    private final int z;
    private final List<SubChunkData> data;
    private final Int2ObjectMap<int[]> biomes;

    public void build(VanillaServer vanillaServer, BaseFullChunk fullChunk) {
        try {
            if(!(fullChunk instanceof final Chunk anvilChunk)) return;

            final long startTime = System.currentTimeMillis();

            anvilChunk.setGenerated();
            anvilChunk.setPopulated();

            for(Int2ObjectMap.Entry<int[]> entry : this.biomes.int2ObjectEntrySet()) {
                final int[] biomes = entry.getValue();
                final int subY = entry.getIntKey();

                for(int i = 0; i < biomes.length; i++)
                    fullChunk.setBiomeId((i >> 8) & 0xF, (subY << 4) + (i & 0xF), (i >> 4) & 0xF, biomes[i]);
            }

            for(SubChunkData chunkData : this.data) {
                if(chunkData.getResult() != SubChunkRequestResult.SUCCESS) continue;

                final BinaryStream binaryStream = new BinaryStream(chunkData.getData());
                final int subChunkVersion = binaryStream.getByte();
                final byte layers = (byte) binaryStream.getByte();
                final byte subY = (byte) binaryStream.getByte();

                for(byte layer = 0; layer < layers; layer++) {
                    final int header = binaryStream.getByte();
                    final int version = header >> 1;

                    if(version == 0) {
                        BlockState blockState;
                        try {
                            blockState = BlockStateRegistry.getBlockStateByRuntimeId(binaryStream.getVarInt());
                        } catch(Exception e) {
                            blockState = BlockStateRegistry.getFallbackBlockState();
                        }

                        for(int i = 0; i < Palette.SIZE; i++)
                            anvilChunk.setBlockStateAtLayer((i >> 8) & 0xF, (subY << 4) + (i & 0xF), (i >> 4) & 0xF, layer, blockState);
                    } else {
                        final short[] indices = Palette.parseIndices(binaryStream, version);
                        final BlockState[] blockStates = new BlockState[binaryStream.getVarInt()];
                        for(int i = 0; i < blockStates.length; i++)
                            try {
                                blockStates[i] = BlockStateRegistry.getBlockStateByRuntimeId(binaryStream.getVarInt());
                            } catch(Exception e) {
                                blockStates[i] = BlockStateRegistry.getFallbackBlockState();
                            }

                        for(int i = 0; i < Palette.SIZE; i++) {
                            final BlockState blockState = blockStates[indices[i]];
                            if(blockState != null)
                                anvilChunk.setBlockStateAtLayer((i >> 8) & 0xF, (subY << 4) + (i & 0xF), (i >> 4) & 0xF, layer, blockState);
                        }
                    }
                }

                try(final FastByteArrayInputStream inputStream = new FastByteArrayInputStream(binaryStream.get())) {
                    while(true) {
                        try {
                            final CompoundTag compoundTag = NBTIO.read(inputStream, ByteOrder.LITTLE_ENDIAN, true);
                            if(compoundTag != null && compoundTag.containsString("id"))
                                BlockEntityData.direct(vanillaServer, fullChunk, compoundTag);
                        } catch(Exception e) {
                            break;
                        }
                    }
                }
            }

            final long timeTook = System.currentTimeMillis() - startTime;
            if(timeTook > 1500)
                VanillaGeneratorPlugin.getInstance().getLogger().warning("Build chunk take too long! Took " + timeTook + "ms!");
        } catch(Throwable throwable) {
            VanillaGeneratorPlugin.getInstance().getLogger().error("GOT EXCEPTION", throwable);
        }
    }

}
