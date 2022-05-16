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

import cn.nukkit.math.BlockVector3;
import de.kcodeyt.vanilla.async.Future;
import lombok.Getter;

import java.util.Objects;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class ChunkRequest {

    private final int x;
    private final int z;
    private final Future<ChunkData> future;

    public ChunkRequest(int x, int z, Future<ChunkData> future) {
        this.x = x;
        this.z = z;
        this.future = future;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ChunkRequest that = (ChunkRequest) o;
        return x == that.x &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.z);
    }

    public BlockVector3 getCenterPosition() {
        return new BlockVector3((this.x << 4) + 8, 260, (this.z << 4) + 8);
    }

}
