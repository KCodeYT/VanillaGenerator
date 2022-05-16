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

public final class StringUtil {

    public static byte[] getUTF8Bytes(String input) {
        final byte[] output = new byte[input.length() * 3];
        int byteCount = 0;

        int fastForward = 0;
        //noinspection StatementWithEmptyBody
        for(int i = Math.min(input.length(), output.length);
            fastForward < i && input.charAt(fastForward) < 128;
            output[byteCount++] = (byte) input.charAt(fastForward++)) {
        }

        for(int i = fastForward; i < input.length(); i++) {
            final char c = input.charAt(i);

            if(c < 128) {
                output[byteCount++] = (byte) c;
            } else if(c < 2048) {
                output[byteCount++] = (byte) (192 | c >> 6);
                output[byteCount++] = (byte) (128 | c & 63);
            } else if(Character.isHighSurrogate(c)) {
                if(i + 1 >= input.length()) {
                    output[byteCount++] = (char) 63;
                } else {
                    final char low = input.charAt(i + 1);
                    if(Character.isLowSurrogate(low)) {
                        final int intChar = Character.toCodePoint(c, low);
                        output[byteCount++] = (byte) (240 | intChar >> 18);
                        output[byteCount++] = (byte) (128 | intChar >> 12 & 63);
                        output[byteCount++] = (byte) (128 | intChar >> 6 & 63);
                        output[byteCount++] = (byte) (128 | intChar & 63);
                        ++i;
                    } else output[byteCount++] = (char) 63;
                }
            } else if(Character.isLowSurrogate(c)) {
                output[byteCount++] = (char) 63;
            } else {
                output[byteCount++] = (byte) (224 | c >> 12);
                output[byteCount++] = (byte) (128 | c >> 6 & 63);
                output[byteCount++] = (byte) (128 | c & 63);
            }
        }

        if(byteCount != output.length) {
            final byte[] copy = new byte[byteCount];
            System.arraycopy(output, 0, copy, 0, byteCount);
            return copy;
        }

        return output;
    }

}
