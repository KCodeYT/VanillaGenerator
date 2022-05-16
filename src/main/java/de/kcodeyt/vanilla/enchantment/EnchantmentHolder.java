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

import de.kcodeyt.vanilla.util.WeightedRandom;
import lombok.Getter;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class EnchantmentHolder extends WeightedRandom.Item {

    private final VanillaEnchantment enchantment;
    private final Enchantments enchantments;
    private final int level;

    EnchantmentHolder(VanillaEnchantment enchantment, Enchantments enchantments, int level) {
        super(enchantment.getRarity().getWeight());
        this.enchantment = enchantment;
        this.enchantments = enchantments;
        this.level = level;
    }

}
