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

package de.kcodeyt.vanilla.jwt;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtToken {

    @Getter
    private final JwtHeader header;
    private final Map<String, Object> claims;

    private final byte[] signatureBytes;
    private final byte[] signatureDigest;

    public static JwtToken parse(String s) {
        final String[] split = s.split("\\.");
        if(split.length != 3)
            throw new IllegalArgumentException("Invalid JWT Token: Expecting exactly three parts delimited by dots '.'");

        final String jwtHeaderJson = new String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8);
        final String jwtClaimsJson = new String(Base64.getDecoder().decode(split[1]), StandardCharsets.UTF_8);

        final JSONParser parser = new JSONParser(-1);
        final Object jwtHeaderRaw;
        final Object jwtClaimsRaw;
        try {
            jwtHeaderRaw = parser.parse(jwtHeaderJson);
            jwtClaimsRaw = parser.parse(jwtClaimsJson);

            if(!(jwtHeaderRaw instanceof JSONObject) || !(jwtClaimsRaw instanceof JSONObject))
                throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, new Throwable());

            return new JwtToken(
                    new JwtHeader((JSONObject) jwtHeaderRaw),
                    (JSONObject) jwtClaimsRaw,
                    StringUtil.getUTF8Bytes(split[0] + '.' + split[1]),
                    Base64.getUrlDecoder().decode(split[2])
            );
        } catch(ParseException e) {
            throw new IllegalArgumentException("Invalid JWT Token: Expected Base-64 encoded JSON data");
        }
    }

    public <T> T getClaim(Class<T> clazz, String key) {
        final Object value = this.claims.get(key);
        if(value == null || !clazz.isAssignableFrom(value.getClass())) return null;
        return clazz.cast(value);
    }

    public boolean validateSignature(Key key) throws JwtSignatureException {
        final JwtAlgorithm algorithm = this.header.getAlgorithm();
        return (algorithm != null && this.validateSignature(algorithm, key));
    }

    public boolean validateSignature(JwtAlgorithm algorithm, Key key) throws JwtSignatureException {
        final JwtSignature validator = algorithm.getSignature();
        return validator.validate(key, this.signatureBytes, this.signatureDigest);
    }

}
