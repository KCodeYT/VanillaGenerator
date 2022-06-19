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

package de.kcodeyt.vanilla.world.blockentity;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class MobSpawnerCreator implements BlockEntityCreator {

    @Override
    public void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag) {
        final String entityIdentifier = baseTag.<StringTag>removeAndGet("EntityIdentifier").data;

        final int entityNumericId = AddEntityPacket.LEGACY_IDS.entrySet().stream().
                filter(entry -> entry.getValue().equals(entityIdentifier)).map(Map.Entry::getKey).
                findFirst().orElse(-1);

        if(entityNumericId == -1) return;

        baseTag.putInt("EntityId", entityNumericId);
        baseTag.putShort("MinimumSpawnerCount", 1);
        baseTag.putShort("MaximumSpawnerCount", baseTag.getShort("SpawnCount"));

        BlockEntity.createBlockEntity(BlockEntity.MOB_SPAWNER, fullChunk, baseTag);
    }

}
