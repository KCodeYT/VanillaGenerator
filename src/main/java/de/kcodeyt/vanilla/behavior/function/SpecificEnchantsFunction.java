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

package de.kcodeyt.vanilla.behavior.function;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import de.kcodeyt.vanilla.behavior.LootTableNumber;
import de.kcodeyt.vanilla.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class SpecificEnchantsFunction implements LootTableFunction<Item> {

    private final List<EnchantmentEntry> enchantments;

    public SpecificEnchantsFunction(Map<String, Object> arguments) {
        this.enchantments = new ArrayList<>();
        for(Object enchant : ((List<?>) arguments.get("enchants"))) {
            if(enchant instanceof final Map<?, ?> map) {
                final String enchantmentId = (String) map.get("id");
                final LootTableNumber level = LootTableNumber.of(map.get("level"));

                final Integer id = Enchantments.getEnchantmentNumericId(enchantmentId);
                if(id == null) continue;

                this.enchantments.add(new EnchantmentEntry(id, level));
            }
        }
    }

    @Override
    public Item invoke(Item item, Random random) {
        if(this.enchantments.isEmpty()) return item;

        for(EnchantmentEntry entry : this.enchantments)
            item.addEnchantment(Enchantment.
                    getEnchantment(entry.numericId()).
                    setLevel(entry.level().getAsInt(random))
            );

        return item;
    }

    private record EnchantmentEntry(int numericId, LootTableNumber level) {

    }

}
