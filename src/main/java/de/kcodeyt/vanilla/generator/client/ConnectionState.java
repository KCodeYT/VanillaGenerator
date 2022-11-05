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

package de.kcodeyt.vanilla.generator.client;

import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import de.kcodeyt.vanilla.generator.client.handler.LoginHandler;
import de.kcodeyt.vanilla.generator.client.handler.NetworkHandler;
import de.kcodeyt.vanilla.generator.client.handler.PlayingHandler;
import de.kcodeyt.vanilla.generator.client.handler.ResourcePackHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@RequiredArgsConstructor
public enum ConnectionState {

    NETWORK_INIT(NetworkHandler::new),
    LOGIN(LoginHandler::new),
    RESOURCE_PACK(ResourcePackHandler::new),
    PLAYING(PlayingHandler::new);

    private final Function<Client, BedrockPacketHandler> constructor;

    public BedrockPacketHandler newPacketHandler(Client client) {
        return this.constructor.apply(client);
    }

}
