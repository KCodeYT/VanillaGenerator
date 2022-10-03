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

package de.kcodeyt.vanilla.enchantment;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.MathHelper;
import de.kcodeyt.vanilla.util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class EnchantmentHelper {

    public static Item addRandomEnchantment(Random random, Item nonFinalItem, int level, boolean allowTreasure) {
        final List<EnchantmentHolder> enchantments = buildEnchantmentList(random, nonFinalItem, level, allowTreasure);
        final Item item = nonFinalItem.getId() == ItemID.BOOK ? Item.get(Item.ENCHANT_BOOK) : nonFinalItem;
        enchantments.forEach(enchantment -> item.addEnchantment(
                Enchantment.getEnchantment(enchantment.getEnchantments().getNumericId()).
                        setLevel(enchantment.getLevel(), false)));
        return item;
    }

    private static List<EnchantmentHolder> buildEnchantmentList(Random randomIn, Item item, int level, boolean allowTreasure) {
        final List<EnchantmentHolder> holders = new ArrayList<>();
        final int itemEnchantability = ItemEnchantment.getEnchantment(item).getEnchantability();
        if(itemEnchantability <= 0)
            return holders;
        level = level + 1 + randomIn.nextInt(itemEnchantability / 4 + 1) + randomIn.nextInt(itemEnchantability / 4 + 1);
        final float randomFloat = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
        level = MathHelper.clamp(Math.round((float) level + (float) level * randomFloat), 1, Integer.MAX_VALUE);
        final List<EnchantmentHolder> availableHolders = getEnchantments(level, item, allowTreasure);
        if(!availableHolders.isEmpty()) {
            holders.add(WeightedRandom.getRandomItem(randomIn, availableHolders));
            while(randomIn.nextInt(50) <= level) {
                final VanillaEnchantment lastEntry = (holders.get(holders.size() - 1)).getEnchantment();
                availableHolders.removeIf(enchantment1 -> !lastEntry.isCompatible(enchantment1.getEnchantment()));
                if(availableHolders.isEmpty())
                    break;
                holders.add(WeightedRandom.getRandomItem(randomIn, availableHolders));
                level /= 2;
            }
        }

        return holders;
    }

    private static List<EnchantmentHolder> getEnchantments(int level, Item item, boolean allowTreasure) {
        final List<EnchantmentHolder> list = new ArrayList<>();
        final boolean isBook = item.getId() == ItemID.BOOK;

        for(Enchantments enchantments : Enchantments.values()) {
            final VanillaEnchantment enchantment = enchantments.getEnchantment();
            if((!enchantment.isTreasureEnchantment() || allowTreasure) && enchantment.canGenerateInLoot() && (enchantment.getType().canEnchant(item) || isBook)) {
                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if(level >= enchantment.getMinEnchantability(i) && level <= enchantment.getMaxEnchantability(i)) {
                        list.add(new EnchantmentHolder(enchantment, enchantments, i));
                        break;
                    }
                }
            }
        }

        return list;
    }

}
