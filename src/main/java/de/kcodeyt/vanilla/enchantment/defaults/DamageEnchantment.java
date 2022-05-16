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
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class DamageEnchantment extends VanillaEnchantment {

    private final Type damageType;

    public DamageEnchantment(EnchantmentRarity rarity, Type type) {
        super(rarity, EnchantmentType.WEAPON);
        this.damageType = type;
    }

    @Override
    public int getMinEnchantability(int level) {
        return this.damageType.getMinEnchantability() + (level - 1) * this.damageType.getLevelCost();
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canApplyTogether(VanillaEnchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment);
    }

    /**
     * @author Kevims KCodeYT
     * @version 1.0-SNAPSHOT
     */
    @Getter
    @AllArgsConstructor
    public enum Type {
        ALL(1, 11),
        UNDEAD(5, 8),
        ARTHROPODS(5, 8);

        private final int minEnchantability;
        private final int levelCost;
    }

}
