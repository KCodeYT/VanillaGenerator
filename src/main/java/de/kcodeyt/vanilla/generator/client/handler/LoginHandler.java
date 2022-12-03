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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import de.kcodeyt.vanilla.generator.client.Client;
import de.kcodeyt.vanilla.generator.client.ConnectionState;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Base64;

/**
 * @author Kevims KCodeYT
 */
@RequiredArgsConstructor
public class LoginHandler implements BedrockPacketHandler {

    private final Client client;

    @Override
    public boolean handle(DisconnectPacket disconnectPacket) {
        this.client.close();
        return true;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket serverToClientHandshakePacket) {
        try {
            final SignedJWT signedJWT = SignedJWT.parse(serverToClientHandshakePacket.getJwt());
            final ECPublicKey serverKey = EncryptionUtils.generateKey(signedJWT.getHeader().getX509CertURL().toASCIIString());

            if(EncryptionUtils.verifyJwt(signedJWT, serverKey)) {
                final SecretKey sharedSecretKey = EncryptionUtils.getSecretKey(
                        this.client.getKeyPair().getPrivate(),
                        serverKey,
                        Base64.getDecoder().decode(signedJWT.getJWTClaimsSet().getStringClaim("salt"))
                );
                this.client.getClientSession().enableEncryption(sharedSecretKey);

                this.client.sendPacket(new ClientToServerHandshakePacket());

                final ClientCacheStatusPacket cacheStatus = new ClientCacheStatusPacket();
                cacheStatus.setSupported(false);
                this.client.sendPacket(cacheStatus);

                this.client.setState(ConnectionState.RESOURCE_PACK);
                return true;
            }

            this.client.close();
        } catch(ParseException | JOSEException | NoSuchAlgorithmException | InvalidKeySpecException |
                InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean handle(PlayStatusPacket playStatusPacket) {
        if(playStatusPacket.getStatus() != PlayStatusPacket.Status.LOGIN_SUCCESS) {
            this.client.close();
            return true;
        }

        return true;
    }

}
