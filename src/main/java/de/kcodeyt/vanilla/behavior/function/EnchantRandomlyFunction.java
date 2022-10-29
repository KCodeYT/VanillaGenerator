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
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import de.kcodeyt.vanilla.behavior.LootTableNumber;
import de.kcodeyt.vanilla.enchantment.Enchantments;
import de.kcodeyt.vanilla.enchantment.VanillaEnchantment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class EnchantRandomlyFunction implements LootTableFunction<Item> {

    private final boolean treasure;

    public EnchantRandomlyFunction(Map<String, Object> arguments) {
        this.treasure = arguments.containsKey("treasure") && (boolean) arguments.get("treasure");
    }

    @Override
    public Item invoke(Item item, Random random) {
        final List<Enchantments> availableEnchantments = Arrays.stream(Enchantments.values()).filter(enchantments ->
                (item.getId() == ItemID.BOOK || enchantments.getEnchantment().getType().canEnchant(item)) &&
                enchantments.getEnchantment().canGenerateInLoot() &&
                (!enchantments.getEnchantment().isTreasureEnchantment() || this.treasure)).toList();

        final Item result = item.getId() == ItemID.BOOK ? Item.get(Item.ENCHANT_BOOK) : item;
        final Enchantments enchantments = availableEnchantments.get(random.nextInt(availableEnchantments.size()));
        final VanillaEnchantment enchantment = enchantments.getEnchantment();
        result.addEnchantment(Enchantment.getEnchantment(enchantments.getNumericId()).
                setLevel(LootTableNumber.random(random, enchantment.getMinLevel(), enchantment.getMaxLevel())));

        return result;
    }

}
