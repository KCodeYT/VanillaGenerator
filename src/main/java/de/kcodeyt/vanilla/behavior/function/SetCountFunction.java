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

import cn.nukkit.item.Item;
import de.kcodeyt.vanilla.behavior.LootTableNumber;

import java.util.Map;
import java.util.Random;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class SetCountFunction implements LootTableFunction<Item> {

    private final LootTableNumber count;

    public SetCountFunction(Map<String, Object> arguments) {
        this.count = LootTableNumber.of(arguments.get("count"));
    }

    @Override
    public Item invoke(Item item, Random random) {
        if(this.count == null) return item;

        item.setCount(this.count.getAsInt(random));
        return item;
    }

}
