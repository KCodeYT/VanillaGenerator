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

package de.kcodeyt.vanilla.behavior;

import cn.nukkit.item.Item;
import de.kcodeyt.vanilla.behavior.function.FunctionRegistry;
import de.kcodeyt.vanilla.behavior.function.LootTableFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class LootTableBuilder {

    public static LootTable build(String path, String name, Map<String, Object> lootTableMap) {
        if(!lootTableMap.containsKey("pools"))
            return new LootTable(path, name, Collections.emptyList());

        try {
            final List<Pool> pools = new ArrayList<>();
            for(Object poolObject : ((List<?>) lootTableMap.get("pools"))) {
                final Map<?, ?> poolMap = (Map<?, ?>) poolObject;
                final LootTableNumber rolls = LootTableNumber.of(poolMap.get("rolls"));

                final List<PoolEntry> entries = new ArrayList<>();
                final List<?> entriesList = (List<?>) poolMap.get("entries");
                for(Object entryObject : entriesList) {
                    final Map<?, ?> entryMap = (Map<?, ?>) entryObject;
                    final String entryType = (String) entryMap.get("type");
                    if(entryType == null) continue;

                    final String entryName = (String) entryMap.get("name");
                    final int entryWeight = entryMap.containsKey("weight") ? (int) (double) (Object) entryMap.get("weight") : 1;
                    if(!entryMap.containsKey("functions")) {
                        entries.add(new PoolEntry(entryType, entryName, entryWeight, Collections.emptyList()));
                        continue;
                    }

                    final List<LootTableFunction<Item>> functions = new ArrayList<>();
                    final List<?> functionsList = (List<?>) entryMap.get("functions");
                    for(Object functionObject : functionsList) {
                        final Map<?, ?> functionMap = (Map<?, ?>) functionObject;
                        final LootTableFunction<Item> function = FunctionRegistry.get((String) functionMap.get("function"), functionMap);

                        if(function != null) functions.add(function);
                    }

                    entries.add(new PoolEntry(entryType, entryName, entryWeight, Collections.unmodifiableList(functions)));
                }

                pools.add(new Pool(rolls, Collections.unmodifiableList(entries)));
            }

            return new LootTable(path, name, Collections.unmodifiableList(pools));
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

}
