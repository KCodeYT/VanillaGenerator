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

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public abstract class Vanilla extends Generator {

    private static final Vector3 SPAWN_VECTOR = new Vector3(0.5, 128, 0.5);

    private VanillaServer vanillaServer;

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        if(this.level.getServer().isPrimaryThread())
            this.vanillaServer = VanillaGeneratorPlugin.getVanillaServer(this.level).join();
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        final BaseFullChunk fullChunk = this.chunkManager.getChunk(chunkX, chunkZ);
        if(this.vanillaServer == null) this.vanillaServer = VanillaGeneratorPlugin.getVanillaServer(this.level).join();

        this.vanillaServer.requestChunk(fullChunk);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public Vector3 getSpawn() {
        return SPAWN_VECTOR;
    }

}