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

package de.kcodeyt.vanilla.world;

import cn.nukkit.level.Level;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@AllArgsConstructor
public class World {

    private final VanillaGeneratorPlugin plugin;
    private final Level level;
    private final int dimension;
    private final int minY;
    private final int maxY;

    public String getWorldName() {
        return this.level.getName();
    }

    public long getSeed() {
        return this.level.getSeed();
    }

}