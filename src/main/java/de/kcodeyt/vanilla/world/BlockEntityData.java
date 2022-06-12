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

package de.kcodeyt.vanilla.world;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import de.kcodeyt.vanilla.generator.chunk.ChunkWaiter;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.util.NbtConverter;
import de.kcodeyt.vanilla.world.blockentity.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BlockEntityData {

    private static final BasicCreator BASIC_CREATOR = new BasicCreator();
    private static final Map<String, BlockEntityCreator> CREATOR_MAP = new HashMap<>();

    static {
        CREATOR_MAP.put(BlockEntity.BREWING_STAND, new BrewingStandCreator());
        CREATOR_MAP.put(BlockEntity.ITEM_FRAME, new ItemFrameCreator());
        CREATOR_MAP.put(BlockEntity.BEEHIVE, new BeehiveCreator());
        CREATOR_MAP.put(BlockEntity.LECTERN, new LecternCreator());
        CREATOR_MAP.put(BlockEntity.FURNACE, new BasicFurnaceCreator());
        CREATOR_MAP.put(BlockEntity.SMOKER, new BasicFurnaceCreator());
        CREATOR_MAP.put(BlockEntity.BLAST_FURNACE, new BasicFurnaceCreator());
        CREATOR_MAP.put(BlockEntity.CHEST, new ChestCreator());
    }

    public static void direct(VanillaServer vanillaServer, FullChunk fullChunk, CompoundTag baseTag) {
        System.out.println(baseTag);
        CREATOR_MAP.getOrDefault(baseTag.getString("id"), BASIC_CREATOR).createBlockEntity(vanillaServer, fullChunk, baseTag);
    }

    public static void from(VanillaServer vanillaServer, Level level, CompoundTag baseTag) {
        if(!baseTag.containsString("id")) return;

        ChunkWaiter.waitFor(new Position(baseTag.getInt("x"), baseTag.getInt("y"), baseTag.getInt("z"), level),
                fullChunk -> BlockEntityData.direct(vanillaServer, fullChunk, baseTag));
    }

    public static void from(VanillaServer vanillaServer, Level level, BlockEntityDataPacket dataPacket) {
        final CompoundTag compoundTag = NbtConverter.convert(dataPacket.getData());
        if(!compoundTag.containsString("id")) return;

        final Vector3i blockPos = dataPacket.getBlockPosition();
        ChunkWaiter.waitFor(new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ(), level),
                fullChunk -> BlockEntityData.direct(vanillaServer, fullChunk, compoundTag));
    }

}
