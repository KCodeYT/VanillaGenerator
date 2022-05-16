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

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public enum EnchantmentType {

    ARMOR {
        @Override
        public boolean canEnchant(Item item) {
            return item.isArmor();
        }
    },
    ARMOR_HELMET {
        @Override
        public boolean canEnchant(Item item) {
            return item.isHelmet();
        }
    },
    ARMOR_CHEST {
        @Override
        public boolean canEnchant(Item item) {
            return item.isChestplate();
        }
    },
    ARMOR_LEGGINGS {
        @Override
        public boolean canEnchant(Item item) {
            return item.isLeggings();
        }
    },
    ARMOR_BOOTS {
        @Override
        public boolean canEnchant(Item item) {
            return item.isBoots();
        }
    },
    WEAPON {
        @Override
        public boolean canEnchant(Item item) {
            return item.isSword();
        }
    },
    TOOL {
        @Override
        public boolean canEnchant(Item item) {
            return item.isPickaxe() || item.isAxe() || item.isShovel() || item.isHoe() || item.isShears();
        }
    },
    BREAKABLE {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemDurable;
        }
    },
    WEARABLE {
        @Override
        public boolean canEnchant(Item item) {
            return item.isArmor();
        }
    },
    FISHING_ROD {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemFishingRod;
        }
    },
    TRIDENT {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemTrident;
        }
    },
    BOW {
        @Override
        public boolean canEnchant(Item item) {
            return item instanceof ItemBow;
        }
    },
    CROSSBOW {
        @Override
        public boolean canEnchant(Item item) {
            return item.getId() == ItemID.CROSSBOW;
        }
    },
    VANISH {
        @Override
        public boolean canEnchant(Item item) {
            return true;
        }
    };

    public abstract boolean canEnchant(Item item);

}
