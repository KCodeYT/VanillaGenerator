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
import cn.nukkit.nbt.tag.ListTag;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BasicFurnaceCreator implements BlockEntityCreator {

    @Override
    public void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag) {
        final short cookTime = (short) (baseTag.containsShort("CookTime") ? baseTag.getShort("CookTime") : 0);
        final short burnTime = (short) (baseTag.containsShort("BurnTime") ? baseTag.getShort("BurnTime") : 0);
        final short burnDuration = (short) (baseTag.containsShort("BurnDuration") ? baseTag.getShort("BurnDuration") : 0);
        final int storedXPInt = baseTag.containsInt("StoredXPInt") ? baseTag.getInt("StoredXPInt") : 0;
        final ListTag<CompoundTag> itemsTag = baseTag.containsList("Items") ? this.rewriteItemsTag(baseTag.getList("Items", CompoundTag.class)) : new ListTag<>("Items");
        BlockEntity.createBlockEntity(baseTag.getString("id"), fullChunk,
                this.createBaseBlockEntityTag(baseTag)
                        .putShort("CookTime", cookTime)
                        .putShort("BurnTime", burnTime)
                        .putShort("BurnDuration", burnDuration)
                        .putInt("StoredXPInt", storedXPInt)
                        .putList(itemsTag)
        );
    }

    private ListTag<CompoundTag> rewriteItemsTag(ListTag<CompoundTag> items) {
        final ListTag<CompoundTag> itemsTag = new ListTag<>("Items");
        for(int i = 0; i < 3; i++)
            itemsTag.add(new CompoundTag());
        for(CompoundTag itemTag : items.getAll()) {
            final Item item = this.createItem(itemTag);
            if(item == null || item.getId() == Item.AIR)
                continue;
            final byte slot = (byte) (itemTag.containsByte("Slot") ? itemTag.getByte("Slot") : -1);
            if(slot == -1)
                itemsTag.add(NBTIO.putItemHelper(item));
            else
                itemsTag.add(slot, NBTIO.putItemHelper(item, (int) slot));
        }
        return itemsTag;
    }

}
