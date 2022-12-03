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

import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.nukkitx.protocol.bedrock.packet.NetworkSettingsPacket;
import de.kcodeyt.vanilla.generator.client.Client;
import de.kcodeyt.vanilla.generator.client.ConnectionState;
import lombok.RequiredArgsConstructor;

/**
 * @author Kevims KCodeYT
 */
@RequiredArgsConstructor
public class NetworkHandler implements BedrockPacketHandler {

    private final Client client;

    @Override
    public boolean handle(DisconnectPacket disconnectPacket) {
        this.client.close();
        return true;
    }

    @Override
    public boolean handle(NetworkSettingsPacket networkSettingsPacket) {
        this.client.getClientSession().setCompression(networkSettingsPacket.getCompressionAlgorithm());
        this.client.login();

        this.client.setState(ConnectionState.LOGIN);
        return true;
    }

}
