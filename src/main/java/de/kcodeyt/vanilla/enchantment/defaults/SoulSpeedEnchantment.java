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

package de.kcodeyt.vanilla.enchantment.defaults;

import de.kcodeyt.vanilla.enchantment.EnchantmentRarity;
import de.kcodeyt.vanilla.enchantment.EnchantmentType;
import de.kcodeyt.vanilla.enchantment.VanillaEnchantment;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class SoulSpeedEnchantment extends VanillaEnchantment {

    public SoulSpeedEnchantment(EnchantmentRarity rarity) {
        super(rarity, EnchantmentType.ARMOR_BOOTS);
    }

    @Override
    public int getMinEnchantability(int level) {
        return level * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public boolean canGenerateInLoot() {
        return false;
    }

}
