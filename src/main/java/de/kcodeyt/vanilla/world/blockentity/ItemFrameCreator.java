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
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class ItemFrameCreator implements BlockEntityCreator {

    @Override
    public void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag) {
        final float itemDropChance = baseTag.containsFloat("ItemDropChance") ? baseTag.getFloat("ItemDropChance") : 0;
        final float itemRotation = baseTag.containsFloat("ItemRotation") ? baseTag.getFloat("ItemRotation") : 0;
        final Item item = baseTag.containsCompound("Item") ? this.createItem(baseTag.getCompound("Item")) : null;
        BlockEntity.createBlockEntity(BlockEntity.ITEM_FRAME, fullChunk,
                this.createBaseBlockEntityTag(baseTag)
                        .putFloat("ItemDropChance", itemDropChance)
                        .putFloat("ItemRotation", itemRotation)
                        .putCompound("Item", item != null ? NBTIO.putItemHelper(item) : new CompoundTag())
        );
    }

}
