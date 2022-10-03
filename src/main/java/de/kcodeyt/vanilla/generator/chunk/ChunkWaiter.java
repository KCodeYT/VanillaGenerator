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

package de.kcodeyt.vanilla.generator.chunk;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;

import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class ChunkWaiter {

    private final Runnable task;

    private ChunkWaiter(Level level, int x, int z, Consumer<FullChunk> chunkConsumer) {
        this.task = () -> {
            final BaseFullChunk fullChunk = level.getChunk(x, z);
            if(fullChunk == null || !fullChunk.isGenerated() || !fullChunk.isPopulated())
                this.scheduleTask();
            else
                chunkConsumer.accept(fullChunk);
        };

        this.scheduleDirectTask();
    }

    public static void waitFor(Position position, Consumer<FullChunk> chunkConsumer) {
        new ChunkWaiter(position.getLevel(), position.getChunkX(), position.getChunkZ(), chunkConsumer);
    }

    private void scheduleTask() {
        Server.getInstance().getScheduler().scheduleDelayedTask(null, this.task, 1);
    }

    private void scheduleDirectTask() {
        Server.getInstance().getScheduler().scheduleTask(null, this.task);
    }

}
