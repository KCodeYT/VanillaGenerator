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

package de.kcodeyt.vanilla.world.blockentity;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import de.kcodeyt.vanilla.generator.server.VanillaServer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BeehiveCreator implements BlockEntityCreator {

    @Override
    public void createBlockEntity(VanillaServer generator, FullChunk fullChunk, CompoundTag baseTag) {
        final byte shouldSpawnBees = (byte) (baseTag.containsByte("ShouldSpawnBees") ? baseTag.getByte("ShouldSpawnBees") : 1);
        final ListTag<CompoundTag> occupantsTag = baseTag.containsList("Occupants", CompoundTag.TAG_Compound) ?
                this.rewriteOccupants(baseTag.getList("Occupants", CompoundTag.class)) : new ListTag<>("Occupants");
        BlockEntity.createBlockEntity(BlockEntity.BEEHIVE, fullChunk,
                this.createBaseBlockEntityTag(baseTag)
                        .putByte("ShouldSpawnBees", shouldSpawnBees)
                        .putList(occupantsTag)
        );
    }

    private ListTag<CompoundTag> rewriteOccupants(ListTag<CompoundTag> occupantsTag) {
        final ListTag<CompoundTag> newOccupantsTag = new ListTag<>("Occupants");
        for(int i = 0; i < occupantsTag.size(); i++) {
            final CompoundTag occupantTag = occupantsTag.get(i);
            if(occupantTag == null)
                continue;
            newOccupantsTag.add(occupantTag.putString("ActorIdentifier", this.validIdentifier(occupantTag.getString("ActorIdentifier"))));
        }
        return newOccupantsTag;
    }

    private String validIdentifier(String actorIdentifier) {
        return actorIdentifier.contains("<") ? actorIdentifier.substring(0, actorIdentifier.indexOf("<")) : actorIdentifier;
    }

}
