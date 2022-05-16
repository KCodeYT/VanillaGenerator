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

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.util.ItemIdentifier;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public interface BlockEntityCreator {

    void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag);

    default CompoundTag createBaseBlockEntityTag(CompoundTag baseTag) {
        return new CompoundTag()
                .putString("id", baseTag.getString("id"))
                .putByte("isMovable", baseTag.getByte("isMovable"))
                .putInt("x", baseTag.getInt("x"))
                .putInt("y", baseTag.getInt("y"))
                .putInt("z", baseTag.getInt("z"));
    }

    default Item createItem(CompoundTag itemTag) {
        final int itemId = ItemIdentifier.getItemId(itemTag.getString("Name"));
        if(itemId == Item.AIR) return null;

        final Item item = Item.get(itemId);
        item.setDamage(itemTag.containsShort("Damage") ? itemTag.getShort("Damage") : 0);
        item.setCount(itemTag.containsByte("Count") ? itemTag.getByte("Count") : 0);
        if(itemTag.containsCompound("tag"))
            item.setNamedTag(itemTag.getCompound("tag"));
        return item;
    }

}
