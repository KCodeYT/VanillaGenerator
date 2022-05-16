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

package de.kcodeyt.vanilla.util;

import cn.nukkit.item.Item;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class ItemIdentifier {

    private static final String RUNTIME_ITEM_STATES_URL = "https://raw.githubusercontent.com/CloudburstMC/Data/master/legacy_item_ids.json";
    private static final Map<String, Integer> RUNTIME_ITEM_MAP = new LinkedHashMap<>();

    static {
        (new Gson()).<Map<String, Object>>fromJson(WebRequest.request(RUNTIME_ITEM_STATES_URL), Map.class).
                forEach((key, value) -> RUNTIME_ITEM_MAP.put(key, (int) (double) value));
    }

    public static int getItemId(String itemName) {
        final String name = itemName.contains(":") ? itemName : "minecraft:" + itemName;
        if(name.equalsIgnoreCase("fire_charge"))
            return 385;
        return RUNTIME_ITEM_MAP.getOrDefault(name, Item.AIR);
    }

}