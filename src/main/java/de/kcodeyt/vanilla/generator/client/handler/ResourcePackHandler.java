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

package de.kcodeyt.vanilla.generator.client.handler;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import com.nukkitx.protocol.bedrock.data.Ability;
import com.nukkitx.protocol.bedrock.data.AbilityType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import de.kcodeyt.vanilla.generator.client.Client;
import de.kcodeyt.vanilla.generator.client.ConnectionState;
import lombok.RequiredArgsConstructor;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@RequiredArgsConstructor
public class ResourcePackHandler implements BedrockPacketHandler {

    private final Client client;

    @Override
    public boolean handle(DisconnectPacket disconnectPacket) {
        this.client.close();
        return true;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket resourcePacksInfoPacket) {
        final ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);

        this.client.sendPacket(resourcePackClientResponsePacket);
        return true;
    }

    @Override
    public boolean handle(ResourcePackStackPacket resourcePackStackPacket) {
        final ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        this.client.sendPacket(resourcePackClientResponsePacket);
        return true;
    }

    @Override
    public boolean handle(StartGamePacket startGamePacket) {
        this.client.setSpawnPosition(new Location(startGamePacket.getDefaultSpawn().getX(), startGamePacket.getDefaultSpawn().getY(), startGamePacket.getDefaultSpawn().getZ()));
        this.client.setCurrentDimension(startGamePacket.getDimensionId());
        this.client.setUniqueEntityId(startGamePacket.getUniqueEntityId());
        this.client.setRuntimeEntityId(startGamePacket.getRuntimeEntityId());

        final RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.setPosition(startGamePacket.getPlayerPosition());
        respawnPacket.setState(RespawnPacket.State.CLIENT_READY);
        this.client.sendPacket(respawnPacket);

        final RequestChunkRadiusPacket requestChunkRadiusPacket = new RequestChunkRadiusPacket();
        requestChunkRadiusPacket.setRadius(Server.getInstance().getViewDistance());
        this.client.sendPacket(requestChunkRadiusPacket);
        return true;
    }

    @Override
    public boolean handle(PlayStatusPacket playStatusPacket) {
        if(playStatusPacket.getStatus() != PlayStatusPacket.Status.PLAYER_SPAWN) return true;

        final SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
        setLocalPlayerAsInitializedPacket.setRuntimeEntityId(this.client.getUniqueEntityId());
        this.client.sendPacket(setLocalPlayerAsInitializedPacket);

        final Location spawnPosition = this.client.getSpawnPosition();
        this.client.move(spawnPosition.x, spawnPosition.y, spawnPosition.z, spawnPosition.yaw, spawnPosition.pitch);

        final RequestAbilityPacket requestAbilityPacket = new RequestAbilityPacket();
        requestAbilityPacket.setType(AbilityType.BOOLEAN);
        requestAbilityPacket.setAbility(Ability.FLYING);
        requestAbilityPacket.setBoolValue(true);

        this.client.sendPacket(requestAbilityPacket);

        final Location currentPosition = this.client.getCurrentPosition();
        this.client.move(currentPosition.getX(), 255, currentPosition.getZ(), 0, 0);
        this.client.checkReadyState();

        this.client.setState(ConnectionState.PLAYING);
        return true;
    }

}
