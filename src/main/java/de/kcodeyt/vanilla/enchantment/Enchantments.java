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

import cn.nukkit.item.enchantment.Enchantment;
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

    PROTECTION(Enchantment.NAME_PROTECTION_ALL, Enchantment.ID_PROTECTION_ALL, new ProtectionEnchantment(EnchantmentRarity.COMMON, ProtectionEnchantment.Type.ALL)),
    FIRE_PROTECTION(Enchantment.NAME_PROTECTION_FIRE, Enchantment.ID_PROTECTION_FIRE, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.FIRE)),
    FEATHER_FALLING(Enchantment.NAME_PROTECTION_FALL, Enchantment.ID_PROTECTION_FALL, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.FALL)),
    BLAST_PROTECTION(Enchantment.NAME_PROTECTION_EXPLOSION, Enchantment.ID_PROTECTION_EXPLOSION, new ProtectionEnchantment(EnchantmentRarity.RARE, ProtectionEnchantment.Type.EXPLOSION)),
    PROJECTILE_PROTECTION(Enchantment.NAME_PROTECTION_PROJECTILE, Enchantment.ID_PROTECTION_PROJECTILE, new ProtectionEnchantment(EnchantmentRarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE)),
    THORNS(Enchantment.NAME_THORNS, Enchantment.ID_THORNS, new ThornsEnchantment(EnchantmentRarity.VERY_RARE)),
    RESPIRATION(Enchantment.NAME_WATER_BREATHING, Enchantment.ID_WATER_BREATHING, new RespirationEnchantment(EnchantmentRarity.RARE)),
    DEPTH_STRIDER(Enchantment.NAME_WATER_WALKER, Enchantment.ID_WATER_WALKER, new DepthStriderEnchantment(EnchantmentRarity.RARE)),
    AQUA_AFFINITY(Enchantment.NAME_WATER_WORKER, Enchantment.ID_WATER_WORKER, new AquaAffinityEnchantment(EnchantmentRarity.RARE)),
    SHARPNESS(Enchantment.NAME_DAMAGE_ALL, Enchantment.ID_DAMAGE_ALL, new DamageEnchantment(EnchantmentRarity.COMMON, DamageEnchantment.Type.ALL)),
    SMITE(Enchantment.NAME_DAMAGE_SMITE, Enchantment.ID_DAMAGE_SMITE, new DamageEnchantment(EnchantmentRarity.UNCOMMON, DamageEnchantment.Type.UNDEAD)),
    BANE_OF_ARTHROPODS(Enchantment.NAME_DAMAGE_ARTHROPODS, Enchantment.ID_DAMAGE_ARTHROPODS, new DamageEnchantment(EnchantmentRarity.UNCOMMON, DamageEnchantment.Type.ARTHROPODS)),
    KNOCKBACK(Enchantment.NAME_KNOCKBACK, Enchantment.ID_KNOCKBACK, new KnockbackEnchantment(EnchantmentRarity.UNCOMMON)),
    FIRE_ASPECT(Enchantment.NAME_FIRE_ASPECT, Enchantment.ID_FIRE_ASPECT, new FireAspectEnchantment(EnchantmentRarity.RARE)),
    LOOTING(Enchantment.NAME_LOOTING, Enchantment.ID_LOOTING, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.WEAPON)),
    EFFICIENCY(Enchantment.NAME_EFFICIENCY, Enchantment.ID_EFFICIENCY, new EfficiencyEnchantment(EnchantmentRarity.COMMON)),
    SILK_TOUCH(Enchantment.NAME_SILK_TOUCH, Enchantment.ID_SILK_TOUCH, new SilkTouchEnchantment(EnchantmentRarity.VERY_RARE)),
    UNBREAKING(Enchantment.NAME_DURABILITY, Enchantment.ID_DURABILITY, new UnbreakingEnchantment(EnchantmentRarity.UNCOMMON)),
    FORTUNE(Enchantment.NAME_FORTUNE_DIGGING, Enchantment.ID_FORTUNE_DIGGING, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.TOOL)),
    POWER(Enchantment.NAME_BOW_POWER, Enchantment.ID_BOW_POWER, new PowerEnchantment(EnchantmentRarity.COMMON)),
    PUNCH(Enchantment.NAME_BOW_KNOCKBACK, Enchantment.ID_BOW_KNOCKBACK, new PunchEnchantment(EnchantmentRarity.RARE)),
    FLAME(Enchantment.NAME_BOW_FLAME, Enchantment.ID_BOW_FLAME, new FlameEnchantment(EnchantmentRarity.RARE)),
    INFINITY(Enchantment.NAME_BOW_INFINITY, Enchantment.ID_BOW_INFINITY, new InfinityEnchantment(EnchantmentRarity.VERY_RARE)),
    LUCK_OF_THE_SEA(Enchantment.NAME_FORTUNE_FISHING, Enchantment.ID_FORTUNE_FISHING, new LootBonusEnchantment(EnchantmentRarity.RARE, EnchantmentType.FISHING_ROD)),
    LURE(Enchantment.NAME_LURE, Enchantment.ID_LURE, new LureEnchantment(EnchantmentRarity.RARE)),
    FROST_WALKER(Enchantment.NAME_FROST_WALKER, Enchantment.ID_FROST_WALKER, new FrostWalkerEnchantment(EnchantmentRarity.RARE)),
    MENDING(Enchantment.NAME_MENDING, Enchantment.ID_MENDING, new MendingEnchantment(EnchantmentRarity.RARE)),
    BINDING(Enchantment.NAME_BINDING_CURSE, Enchantment.ID_BINDING_CURSE, new BindingCurseEnchantment(EnchantmentRarity.VERY_RARE)),
    VANISHING(Enchantment.NAME_VANISHING_CURSE, Enchantment.ID_VANISHING_CURSE, new VanishingCurseEnchantment(EnchantmentRarity.VERY_RARE)),
    IMPALING(Enchantment.NAME_TRIDENT_IMPALING, Enchantment.ID_TRIDENT_IMPALING, new ImpalingEnchantment(EnchantmentRarity.RARE)),
    RIPTIDE(Enchantment.NAME_TRIDENT_RIPTIDE, Enchantment.ID_TRIDENT_RIPTIDE, new RiptideEnchantment(EnchantmentRarity.RARE)),
    LOYALTY(Enchantment.NAME_TRIDENT_LOYALTY, Enchantment.ID_TRIDENT_LOYALTY, new LoyaltyEnchantment(EnchantmentRarity.RARE)),
    CHANNELING(Enchantment.NAME_TRIDENT_CHANNELING, Enchantment.ID_TRIDENT_CHANNELING, new ChannelingEnchantment(EnchantmentRarity.VERY_RARE)),
    MULTISHOT(Enchantment.NAME_CROSSBOW_MULTISHOT, Enchantment.ID_CROSSBOW_MULTISHOT, new MultishotEnchantment(EnchantmentRarity.RARE)),
    PIERCING(Enchantment.NAME_CROSSBOW_PIERCING, Enchantment.ID_CROSSBOW_PIERCING, new PiercingEnchantment(EnchantmentRarity.COMMON)),
    QUICK_CHARGE(Enchantment.NAME_CROSSBOW_QUICK_CHARGE, Enchantment.ID_CROSSBOW_QUICK_CHARGE, new QuickChargeEnchantment(EnchantmentRarity.UNCOMMON)),
    SOUL_SPEED(Enchantment.NAME_SOUL_SPEED, Enchantment.ID_SOUL_SPEED, new SoulSpeedEnchantment(EnchantmentRarity.VERY_RARE)),
    SWITFT_SNEAK(Enchantment.NAME_SWIFT_SNEAK, Enchantment.ID_SWIFT_SNEAK, new SwiftSneakEnchantment(EnchantmentRarity.VERY_RARE));

    private final String identifier;
    private final int numericId;
    private final VanillaEnchantment enchantment;

    public static Integer getEnchantmentNumericId(String identifier) {
        final int indexOfSplitter = identifier.indexOf(':');
        final String namespaced = indexOfSplitter != -1 ? identifier.substring(indexOfSplitter + 1) : identifier;

        for(Enchantments value : values()) {
            if(value.getIdentifier().equals(namespaced))
                return value.getNumericId();
        }

        return null;
    }

}
