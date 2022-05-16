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

import java.security.*;

final class JwtSignatureES384 implements JwtSignature {

    @Override
    public boolean validate(Key key, byte[] signatureBytes, byte[] digestBytes) throws JwtSignatureException {
        final Signature signature;
        try {
            signature = Signature.getInstance("SHA384withECDSA");
        } catch(NoSuchAlgorithmException e) {
            throw new JwtSignatureException("Could not create signature for ES384 algorithm", e);
        }
        if(!(key instanceof PublicKey))
            throw new JwtSignatureException("Signature Key must be a PublicKey for ES384 validation");

        try {
            signature.initVerify((PublicKey) key);
            signature.update(signatureBytes);
            return signature.verify(this.convertConcatRSToDER(digestBytes));
        } catch(SignatureException | InvalidKeyException e) {
            throw new JwtSignatureException("Could not perform ES384 signature validation", e);
        }
    }

    @Override
    public byte[] sign(Key key, byte[] signatureBytes) throws JwtSignatureException {
        final Signature signature;
        try {
            signature = Signature.getInstance("SHA384withECDSA");
        } catch(NoSuchAlgorithmException e) {
            throw new JwtSignatureException("Could not create signature for ES384 algorithm", e);
        }
        if(!(key instanceof PrivateKey))
            throw new JwtSignatureException("Signature Key must be a PrivateKey for ES384 signing");

        final byte[] der;
        try {
            signature.initSign((PrivateKey) key);
            signature.update(signatureBytes);
            der = signature.sign();
        } catch(SignatureException | InvalidKeyException e) {
            throw new JwtSignatureException("Could not sign ES384 signature", e);
        }

        return this.convertDERToConcatRS(der);
    }

    private byte[] convertDERToConcatRS(byte[] der) throws JwtSignatureException {
        if(der.length < 8 || der[0] != 0x30) throw new JwtSignatureException("Invalid DER signature");

        int offsetR = 4;
        int lengthR = der[offsetR - 1];

        int offsetL = offsetR + lengthR + 2;

        if(der[offsetR] == 0x00) {
            offsetR++;
            lengthR--;
        }

        int lengthL = der[offsetL - 1];
        if(der[offsetL] == 0x00) {
            offsetL++;
            lengthL--;
        }

        if(lengthR > 48 || lengthL > 48) throw new JwtSignatureException("Invalid DER signature for ECSDA 384 bit");

        final byte[] concat = new byte[96];
        System.arraycopy(der, offsetR, concat, 48 - lengthR, lengthR);
        System.arraycopy(der, offsetL, concat, 96 - lengthL, lengthL);
        return concat;
    }

    private byte[] convertConcatRSToDER(byte[] concat) throws JwtSignatureException {
        if(concat.length != 96)
            throw new JwtSignatureException("Invalid ECDSA signature (expected 96 bytes, got " + concat.length + ")");

        final int rawLength = concat.length >> 1;

        int offsetR = 0;
        while(concat[offsetR] == 0x00) offsetR++;

        final int lengthR = rawLength - offsetR;
        final boolean padR = (concat[offsetR] & 0x80) != 0;

        int offsetL = rawLength;
        while(concat[offsetL] == 0x00) offsetL++;

        final int lengthL = (rawLength << 1) - offsetL;
        final boolean padL = (concat[offsetL] & 0x80) != 0;

        final int sigLength = 2 + lengthR + (padR ? 1 : 0) + 2 + lengthL + (padL ? 1 : 0);

        int cursor = 0;
        final byte[] derSignature = new byte[2 + sigLength];
        derSignature[cursor++] = 0x30;
        derSignature[cursor++] = (byte) sigLength;

        derSignature[cursor++] = 0x02;
        derSignature[cursor++] = (byte) (lengthR + (padR ? 1 : 0));

        if(padR) cursor++;

        System.arraycopy(concat, offsetR, derSignature, cursor, lengthR);

        cursor += lengthR;

        derSignature[cursor++] = 0x02;
        derSignature[cursor++] = (byte) (lengthL + (padL ? 1 : 0));

        if(padL) cursor++;

        System.arraycopy(concat, offsetL, derSignature, cursor, lengthL);
        cursor += lengthL;

        if(cursor != derSignature.length)
            throw new JwtSignatureException("Could not convert ECDSA signature to DER format");
        return derSignature;
    }

}
