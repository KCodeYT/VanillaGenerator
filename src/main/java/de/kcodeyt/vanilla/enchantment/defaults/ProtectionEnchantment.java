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
public class ProtectionEnchantment extends VanillaEnchantment {

    private final Type protectionType;

    public ProtectionEnchantment(EnchantmentRarity rarity, Type type) {
        super(rarity, type == Type.FALL ? EnchantmentType.ARMOR_BOOTS : EnchantmentType.ARMOR);
        this.protectionType = type;
    }

    @Override
    public int getMinEnchantability(int level) {
        return this.protectionType.getMinEnchantability() + (level - 1) * this.protectionType.getLevelCost();
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + this.protectionType.getLevelCost();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canApplyTogether(VanillaEnchantment enchantment) {
        return enchantment instanceof ProtectionEnchantment ?
                this.protectionType != ((ProtectionEnchantment) enchantment).protectionType &&
                        (this.protectionType == Type.FALL || ((ProtectionEnchantment) enchantment).protectionType == Type.FALL) :
                super.canApplyTogether(enchantment);
    }

    /**
     * @author Kevims KCodeYT
     * @version 1.0-SNAPSHOT
     */
    @Getter
    @AllArgsConstructor
    public enum Type {
        ALL(1, 11),
        FIRE(10, 8),
        FALL(5, 6),
        EXPLOSION(5, 8),
        PROJECTILE(3, 6);

        private final int minEnchantability;
        private final int levelCost;
    }

}
