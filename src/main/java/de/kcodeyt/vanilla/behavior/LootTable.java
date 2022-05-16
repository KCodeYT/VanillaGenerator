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

package de.kcodeyt.vanilla.behavior;

import cn.nukkit.Server;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import de.kcodeyt.vanilla.behavior.function.LootTableFunction;
import de.kcodeyt.vanilla.util.ItemIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public record LootTable(String path, String name, List<Pool> pools) {

    public void fillInventory(Inventory inventory, Long randomSeed) {
        final List<Integer> freeSlots = IntStream.range(0, inventory.getSize()).filter(slot -> inventory.getItem(slot).isNull()).boxed().collect(Collectors.toList());
        if(freeSlots.size() < inventory.getSize()) {
            Server.getInstance().getLogger().warning("Tried to overfill inventory!");
            return;
        }

        final Random random = randomSeed != null ? new Random(randomSeed) : new Random();

        for(Pool pool : this.pools) {
            final List<PoolEntry> entries = new ArrayList<>(pool.entries());
            final int rolls = pool.rolls().getAsInt();
            for(int i = 0; i < rolls; i++) {
                Collections.shuffle(entries, random);

                final int fullWeight = entries.stream().mapToInt(PoolEntry::weight).sum();
                int randomWeight = random.nextInt(fullWeight + 1);
                for(PoolEntry poolEntry : entries) {
                    if((randomWeight -= poolEntry.weight()) <= 0) {
                        final Integer slot;
                        if(!poolEntry.type().equals("item") || poolEntry.name() == null) continue;

                        Item item = Item.get(ItemIdentifier.getItemId(poolEntry.name()));
                        if(item.getId() == Item.AIR) continue;

                        for(LootTableFunction<Item> function : poolEntry.functions())
                            item = function.invoke(item, random);

                        inventory.setItem(slot = freeSlots.get(random.nextInt(freeSlots.size())), item);
                        freeSlots.remove(slot);
                        if(freeSlots.isEmpty()) return;

                        break;
                    }
                }
            }
        }
    }

}
