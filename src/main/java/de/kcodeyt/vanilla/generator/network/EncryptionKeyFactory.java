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

import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import lombok.Getter;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class EncryptionKeyFactory {

    public static final EncryptionKeyFactory INSTANCE = new EncryptionKeyFactory();

    @Getter
    private final List<Exception> initExceptions = new ArrayList<>();

    private final KeyFactory keyFactory;
    private final KeyPairGenerator keyPairGenerator;

    public EncryptionKeyFactory() {
        this.keyFactory = this.initKeyFactory();
        this.keyPairGenerator = this.initKeyPairGenerator();
    }

    private KeyFactory initKeyFactory() {
        try {
            return KeyFactory.getInstance("EC");
        } catch(NoSuchAlgorithmException e) {
            this.initExceptions.add(e);
            return null;
        }
    }

    public KeyPairGenerator initKeyPairGenerator() {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(384);
            return generator;
        } catch(NoSuchAlgorithmException e) {
            this.initExceptions.add(e);
            return null;
        }
    }

    public KeyPair createKeyPair() {
        return this.keyPairGenerator.generateKeyPair();
    }

    public PublicKey createPublicKey(String base64) {
        try {
            return this.keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch(InvalidKeySpecException e) {
            VanillaGeneratorPlugin.getInstance().getLogger().error("Failed to create public key from base64 string: " + base64, e);
            return null;
        }
    }

}
