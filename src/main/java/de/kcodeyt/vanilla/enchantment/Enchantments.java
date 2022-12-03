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

import de.kcodeyt.vanilla.enchantment.defaults.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public enum Enchantments {

    PROTECTION("minecraft:protection", 0, new ProtectionEnchantment(EnchantmentRarity.COMMON, ProtectionEnchantment.Type.ALL)),
    FIRE_PROTECTION("minecraft:fire_protection", 1, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.FIRE)),
    FEATHER_FALLING("minecraft:feather_falling", 2, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.FALL)),
    BLAST_PROTECTION("minecraft:blast_protection", 3, new ProtectionEnchantment(EnchantmentRarity.RARE, ProtectionEnchantment.Type.EXPLOSION)),
    PROJECTILE_PROTECTION("minecraft:projectile_protection", 4, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE)),
    THORNS("minecraft:thorns", 5, new ThornsEnchantment(EnchantmentRarity.VERY_RARE)),
    RESPIRATION("minecraft:respiration", 6, new RespirationEnchantment(EnchantmentRarity.RARE)),
    DEPTH_STRIDER("minecraft:depth_strider", 7, new DepthStriderEnchantment(EnchantmentRarity.RARE)),
    AQUA_AFFINITY("minecraft:aqua_affinity", 8, new AquaAffinityEnchantment(EnchantmentRarity.RARE)),
    SHARPNESS("minecraft:sharpness", 9, new DamageEnchantment(EnchantmentRarity.COMMON, DamageEnchantment.Type.ALL)),
    SMITE("minecraft:smite", 10, new DamageEnchantment(EnchantmentRarity.UNCOMMON, DamageEnchantment.Type.UNDEAD)),
    BANE_OF_ARTHROPODS("minecraft:bane_of_arthropods", 11, new DamageEnchantment(EnchantmentRarity.UNCOMMON, DamageEnchantment.Type.ARTHROPODS)),
    KNOCKBACK("minecraft:knockback", 12, new KnockbackEnchantment(EnchantmentRarity.UNCOMMON)),
    FIRE_ASPECT("minecraft:fire_aspect", 13, new FireAspectEnchantment(EnchantmentRarity.RARE)),
    LOOTING("minecraft:looting", 14, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.WEAPON)),
    EFFICIENCY("minecraft:efficiency", 15, new EfficiencyEnchantment(EnchantmentRarity.COMMON)),
    SILK_TOUCH("minecraft:silk_touch", 16, new SilkTouchEnchantment(EnchantmentRarity.VERY_RARE)),
    UNBREAKING("minecraft:unbreaking", 17, new UnbreakingEnchantment(EnchantmentRarity.UNCOMMON)),
    FORTUNE("minecraft:fortune", 18, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.TOOL)),
    POWER("minecraft:power", 19, new PowerEnchantment(EnchantmentRarity.COMMON)),
    PUNCH("minecraft:punch", 20, new PunchEnchantment(EnchantmentRarity.RARE)),
    FLAME("minecraft:flame", 21, new FlameEnchantment(EnchantmentRarity.RARE)),
    INFINITY("minecraft:infinity", 22, new InfinityEnchantment(EnchantmentRarity.VERY_RARE)),
    LUCK_OF_THE_SEA("minecraft:luck_of_the_sea", 23, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.FISHING_ROD)),
    LURE("minecraft:lure", 24, new LureEnchantment(EnchantmentRarity.RARE)),
    FROST_WALKER("minecraft:frost_walker", 25, new FrostWalkerEnchantment(EnchantmentRarity.RARE)),
    MENDING("minecraft:mending", 26, new MendingEnchantment(EnchantmentRarity.RARE)),
    BINDING("minecraft:binding", 27, new BindingCurseEnchantment(EnchantmentRarity.VERY_RARE)),
    VANISHING("minecraft:vanishing", 28, new VanishingCurseEnchantment(EnchantmentRarity.VERY_RARE)),
    IMPALING("minecraft:impaling", 29, new ImpalingEnchantment(EnchantmentRarity.RARE)),
    RIPTIDE("minecraft:riptide", 30, new RiptideEnchantment(EnchantmentRarity.RARE)),
    LOYALTY("minecraft:loyalty", 31, new LoyaltyEnchantment(EnchantmentRarity.RARE)),
    CHANNELING("minecraft:channeling", 32, new ChannelingEnchantment(EnchantmentRarity.VERY_RARE)),
    MULTISHOT("minecraft:multishot", 33, new MultishotEnchantment(EnchantmentRarity.RARE)),
    PIERCING("minecraft:piercing", 34, new PiercingEnchantment(EnchantmentRarity.COMMON)),
    QUICK_CHARGE("minecraft:quick_charge", 35, new QuickChargeEnchantment(EnchantmentRarity.UNCOMMON)),
    SOUL_SPEED("minecraft:soul_speed", 36, new SoulSpeedEnchantment(EnchantmentRarity.VERY_RARE)),
    SWITFT_SNEAK("minecraft:switft_sneak", 37, new SwiftSneakEnchantment(EnchantmentRarity.VERY_RARE));

    private final String identifier;
    private final int numericId;
    private final VanillaEnchantment enchantment;

    public static Integer getEnchantmentNumericId(String identifier) {
        final String namespaced = identifier.contains(":") ? identifier : "minecraft:" + identifier;
        final Enchantments[] enchantments = values();
        for(Enchantments value : enchantments) {
            if(value.getIdentifier().equals(namespaced))
                return value.getNumericId();
        }
        return null;
    }

}
