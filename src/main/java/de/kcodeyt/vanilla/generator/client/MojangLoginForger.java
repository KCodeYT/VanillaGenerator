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

import com.nimbusds.jose.shaded.json.JSONObject;
import de.kcodeyt.vanilla.jwt.JwtAlgorithm;
import de.kcodeyt.vanilla.jwt.JwtSignatureException;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@Setter
public class MojangLoginForger {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private String username;
    private UUID uuid;
    private PublicKey publicKey;
    private JSONObject skinData;
    private String xuid;

    public String forge(PrivateKey privateKey) {
        final JwtAlgorithm algorithm = JwtAlgorithm.ES384;

        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

        JSONObject header = new JSONObject();
        header.put("alg", algorithm.name());
        header.put("x5u", publicKeyBase64);

        long timestamp = System.currentTimeMillis() / 1000;

        JSONObject claims = new JSONObject();
        claims.put("nbf", timestamp - 1);
        claims.put("exp", timestamp + 24 * 60 * 60);
        claims.put("iat", timestamp + 24 * 60 * 60);
        claims.put("iss", "self");
        claims.put("certificateAuthority", true);
        // claims.put("randomNonce", ThreadLocalRandom.current().nextInt());

        JSONObject extraData = new JSONObject();
        extraData.put("displayName", this.username);
        extraData.put("identity", this.uuid.toString());
        extraData.put("XUID", this.xuid);
        extraData.put("titleId", "1739947436");

        claims.put("extraData", extraData);
        claims.put("identityPublicKey", publicKeyBase64);

        StringBuilder builder = new StringBuilder();
        builder.append(ENCODER.encodeToString(header.toJSONString().getBytes(StandardCharsets.UTF_8)));
        builder.append('.');
        builder.append(ENCODER.encodeToString(claims.toJSONString().getBytes(StandardCharsets.UTF_8)));

        byte[] signatureBytes = builder.toString().getBytes(StandardCharsets.US_ASCII);
        byte[] signatureDigest;
        try {
            signatureDigest = algorithm.getSignature().sign(privateKey, signatureBytes);
        } catch(JwtSignatureException e) {
            e.printStackTrace();
            return null;
        }

        builder.append('.');
        builder.append(ENCODER.encodeToString(signatureDigest));

        return builder.toString();
    }

    public String forgeSkin(PrivateKey privateKey) {
        final JwtAlgorithm algorithm = JwtAlgorithm.ES384;

        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

        JSONObject header = new JSONObject();
        header.put("alg", algorithm.name());
        header.put("x5u", publicKeyBase64);

        StringBuilder builder = new StringBuilder();
        builder.append(ENCODER.encodeToString(header.toJSONString().getBytes(StandardCharsets.UTF_8)));
        builder.append('.');
        builder.append(ENCODER.encodeToString(this.skinData.toJSONString().getBytes(StandardCharsets.UTF_8)));

        byte[] signatureBytes = builder.toString().getBytes(StandardCharsets.US_ASCII);
        byte[] signatureDigest;
        try {
            signatureDigest = algorithm.getSignature().sign(privateKey, signatureBytes);
        } catch(JwtSignatureException e) {
            e.printStackTrace();
            return null;
        }

        builder.append('.');
        builder.append(ENCODER.encodeToString(signatureDigest));

        return builder.toString();
    }

    public void setSkinData(JSONObject skinData) {
        this.skinData = skinData;
    }

}
