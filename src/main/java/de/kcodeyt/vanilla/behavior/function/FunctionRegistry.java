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

package de.kcodeyt.vanilla.behavior.function;

import cn.nukkit.Server;
import cn.nukkit.item.Item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class FunctionRegistry {

    private static final Map<String, Class<? extends LootTableFunction<Item>>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("minecraft:enchant_randomly", EnchantRandomlyFunction.class);
        FUNCTIONS.put("minecraft:enchant_with_levels", EnchantWithLevelsFunction.class);
        FUNCTIONS.put("minecraft:random_aux_value", RandomAuxValueFunction.class);
        FUNCTIONS.put("minecraft:set_data", SetDataFunction.class);
        FUNCTIONS.put("minecraft:set_count", SetCountFunction.class);
        FUNCTIONS.put("minecraft:set_damage", SetDamageFunction.class);
        FUNCTIONS.put("minecraft:specific_enchants", SpecificEnchantsFunction.class);
    }

    public static LootTableFunction<Item> get(String name, Map<?, ?> args) {
        final String identifier = name.contains(":") ? name : "minecraft:" + name;

        for(Map.Entry<String, Class<? extends LootTableFunction<Item>>> entry : FUNCTIONS.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(identifier)) {
                try {
                    final Constructor<? extends LootTableFunction<Item>> constructor = entry.getValue().getConstructor(Map.class);
                    constructor.setAccessible(true);
                    return constructor.newInstance(args);
                } catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    Server.getInstance().getLogger().error("Could not initialize function!", e);
                }
            }
        }

        return null;
    }

}
