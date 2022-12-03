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

package de.kcodeyt.vanilla.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;

import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class VanillaNether extends Vanilla {

    public static final int TYPE = 101;

    public VanillaNether(Map<String, Object> options) {

    }

    @Override
    public int getId() {
        return VanillaNether.TYPE;
    }

    @Override
    public DimensionData getDimensionData() {
        return DimensionEnum.NETHER.getDimensionData();
    }

    @Override
    public String getName() {
        return "vanilla_nether";
    }

}
