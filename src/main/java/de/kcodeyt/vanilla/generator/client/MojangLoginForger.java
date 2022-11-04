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

import com.nimbusds.jose.*;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONStyle;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import de.kcodeyt.vanilla.generator.client.clientdata.LoginData;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.util.Base64;
import java.util.Collections;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class MojangLoginForger {

    private static final String TITLE_ID_MINECRAFT_PE = "1739947436";

    public String forge(KeyPair keyPair, JSONObject jsonObject) throws JOSEException {
        final JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.ES384).
                        x509CertURL(URI.create(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()))).
                        build(),
                new Payload(jsonObject)
        );

        EncryptionUtils.signJwt(jwsObject, (ECPrivateKey) keyPair.getPrivate());

        return jwsObject.serialize();
    }

    public String forgeLoginChain(KeyPair keyPair, LoginData loginData) throws JOSEException {
        final long timestamp = System.currentTimeMillis() / 1000;

        final JSONObject extraData = new JSONObject().
                appendField("displayName", loginData.getName()).
                appendField("identity", loginData.getUniqueId().toString()).
                appendField("XUID", loginData.getXuid()).
                appendField("titleId", TITLE_ID_MINECRAFT_PE);

        final JSONObject chainData = new JSONObject().
                appendField("nbf", timestamp - 60 * 60).
                appendField("exp", timestamp + 24 * 60 * 60).
                appendField("iat", timestamp).
                appendField("iss", "self").
                appendField("certificateAuthority", true).
                appendField("identityPublicKey", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())).
                appendField("extraData", extraData);

        final JSONObject loginChain = new JSONObject().
                appendField("chain", Collections.singletonList(MojangLoginForger.forge(keyPair, chainData)));

        return loginChain.toJSONString(JSONStyle.LT_COMPRESS);
    }

}
