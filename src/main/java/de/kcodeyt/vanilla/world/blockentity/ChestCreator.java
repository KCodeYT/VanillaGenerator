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

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.LongTag;
import de.kcodeyt.vanilla.behavior.LootTable;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class ChestCreator implements BlockEntityCreator {

    @Override
    public void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag) {
        final String lootTableIdentifier = baseTag.containsString("LootTable") ? baseTag.getString("LootTable") : null;
        final Long lootTableSeed = baseTag.get("LootTableSeed") instanceof LongTag ? baseTag.getLong("LootTableSeed") : null;

        final BlockEntity blockEntity = BlockEntity.createBlockEntity(BlockEntity.CHEST, fullChunk, this.createBaseBlockEntityTag(baseTag));
        if(blockEntity instanceof BlockEntityChest && lootTableIdentifier != null) {
            final LootTable lootTable = generator.getLootTableManager().getLootTable(lootTableIdentifier);
            if(lootTable == null) {
                Server.getInstance().getLogger().warning("Could not find loot table with name \"" + lootTableIdentifier + "\"!");
                return;
            }

            lootTable.fillInventory(((BlockEntityChest) blockEntity).getInventory(), lootTableSeed);
        }
    }

}
