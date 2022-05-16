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

package de.kcodeyt.vanilla.generator.network;

import lombok.Getter;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
public class EncryptionKeyFactory {

    private KeyFactory keyFactory;
    private KeyPair keyPair;

    public EncryptionKeyFactory() {
        try {
            this.keyFactory = KeyFactory.getInstance("EC");
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(384);
            this.keyPair = generator.generateKeyPair();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey createPublicKey(String base64) {
        try {
            return this.keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch(InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
