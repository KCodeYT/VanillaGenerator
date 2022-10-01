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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.crypto.KeyAgreement;
import java.security.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@RequiredArgsConstructor
public class EncryptionHandler {

    private static final ThreadLocal<MessageDigest> SHA256_DIGEST = new ThreadLocal<>();

    private final KeyPair keyPair;
    private final PublicKey serverPublicKey;

    private byte[] serverKey;
    private byte[] serverIv;

    public boolean beginServersideEncryption(byte[] salt) throws NoSuchAlgorithmException {
        if(this.serverKey != null && this.serverIv != null) {
            System.out.println("Already initialized");
            return true;
        }

        final byte[] secret = this.generateECDHSecret(this.keyPair.getPrivate(), this.serverPublicKey);
        if(secret == null) return false;

        final byte[] result = new byte[16];
        System.arraycopy(this.serverKey = this.hashSHA256(salt, secret), 0, result, 0, 16);
        this.serverIv = result;
        return true;
    }

    private MessageDigest getSHA256() throws NoSuchAlgorithmException {
        MessageDigest digest = SHA256_DIGEST.get();
        if(digest != null) {
            digest.reset();
            return digest;
        }

        digest = MessageDigest.getInstance("SHA-256");
        SHA256_DIGEST.set(digest);
        return digest;
    }

    private byte[] generateECDHSecret(PrivateKey privateKey, PublicKey publicKey) {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret();
        } catch(NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] hashSHA256(byte[]... message) throws NoSuchAlgorithmException {
        final MessageDigest digest = getSHA256();
        final ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        for(byte[] bytes : message) buf.writeBytes(bytes);

        digest.update(buf.nioBuffer());
        buf.release();
        return digest.digest();
    }

}
