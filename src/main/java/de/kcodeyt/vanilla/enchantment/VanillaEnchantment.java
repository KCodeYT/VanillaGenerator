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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@AllArgsConstructor
public abstract class VanillaEnchantment {

    private final EnchantmentRarity rarity;
    private final EnchantmentType type;

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinEnchantability(int level) {
        return level * 10 + 1;
    }

    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 5;
    }

    public boolean canApplyTogether(VanillaEnchantment enchantment) {
        return !this.equals(enchantment);
    }

    public boolean isCompatible(VanillaEnchantment enchantment) {
        return this.canApplyTogether(enchantment) && enchantment.canApplyTogether(this);
    }

    public boolean canGenerateInLoot() {
        return true;
    }

    public boolean isTreasureEnchantment() {
        return false;
    }

}
