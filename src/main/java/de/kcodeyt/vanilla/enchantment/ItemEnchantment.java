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

import cn.nukkit.item.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@AllArgsConstructor
public enum ItemEnchantment {

    DEFAULT(0) {
        @Override
        public boolean isItem(Item item) {
            return false;
        }
    },
    LEATHER_ARMOR(15) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_LEATHER;
        }
    },
    CHAIN_ARMOR(12) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_CHAIN;
        }
    },
    IRON_ARMOR(9) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_IRON;
        }
    },
    GOLD_ARMOR(25) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_GOLD;
        }
    },
    DIAMOND_ARMOR(10) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_DIAMOND;
        }
    },
    TURTLE_ARMOR(9) {
        @Override
        public boolean isItem(Item item) {
            return item.isArmor() && item.getTier() == ItemArmor.TIER_OTHER;
        }
    },
    NETHERITE(15) {
        @Override
        public boolean isItem(Item item) {
            return (item.isArmor() && item.getTier() == ItemArmor.TIER_NETHERITE) || (item.isTool() && item.getTier() == ItemTool.TIER_NETHERITE);
        }
    },
    BOOK(1) {
        @Override
        public boolean isItem(Item item) {
            return item.getId() == ItemID.BOOK;
        }
    },
    FISHING_ROD(1) {
        @Override
        public boolean isItem(Item item) {
            return item.getId() == ItemID.FISHING_ROD;
        }
    },
    BOW(1) {
        @Override
        public boolean isItem(Item item) {
            return item.getId() == ItemID.BOW || item.getId() == ItemID.CROSSBOW;
        }
    },
    WOOD_TIER(15) {
        @Override
        public boolean isItem(Item item) {
            return item.isTool() && item.getTier() == ItemTool.TIER_WOODEN;
        }
    },
    STONE_TIER(5) {
        @Override
        public boolean isItem(Item item) {
            return item.isTool() && item.getTier() == ItemTool.TIER_STONE;
        }
    },
    IRON_TIER(14) {
        @Override
        public boolean isItem(Item item) {
            return item.isTool() && item.getTier() == ItemTool.TIER_IRON;
        }
    },
    GOLD_TIER(22) {
        @Override
        public boolean isItem(Item item) {
            return item.isTool() && item.getTier() == ItemTool.TIER_GOLD;
        }
    },
    DIAMOND_TIER(10) {
        @Override
        public boolean isItem(Item item) {
            return item.isTool() && item.getTier() == ItemTool.TIER_DIAMOND;
        }
    },
    TRIDENT(1) {
        @Override
        public boolean isItem(Item item) {
            return item.getId() == ItemID.TRIDENT;
        }
    };

    public static ItemEnchantment getEnchantment(Item item) {
        return Arrays.stream(values()).filter(enchantment -> enchantment.isItem(item)).findAny().orElse(DEFAULT);
    }

    private final int enchantability;

    public abstract boolean isItem(Item item);

}
