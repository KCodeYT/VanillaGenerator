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

package de.kcodeyt.vanilla.util;

import cn.nukkit.utils.BinaryStream;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class Palette {

    public static final int SIZE = 4096;
    private static final Map<Integer, Integer> WORDS_MAP = new HashMap<>();

    static {
        WORDS_MAP.put(0, 0);
        WORDS_MAP.put(1, 32);
        WORDS_MAP.put(2, 16);
        WORDS_MAP.put(3, 10);
        WORDS_MAP.put(4, 8);
        WORDS_MAP.put(5, 6);
        WORDS_MAP.put(6, 5);
        WORDS_MAP.put(8, 4);
        WORDS_MAP.put(16, 2);
    }

    public static short[] parseIndices(BinaryStream binaryStream, int version) {
        final short[] indices = new short[SIZE];
        final int words = WORDS_MAP.get(version);
        final int iterations = SimpleMath.ceil(SIZE / (float) words);
        for(int i = 0; i < iterations; i++) {
            final int data = binaryStream.getLInt();
            int rIndex = 0;

            for(byte w = 0; w < words; w++) {
                short val = 0;

                for(byte v = 0; v < version; v++) {
                    if((data & (1 << rIndex++)) != 0)
                        val ^= 1 << v;
                }

                final int index = (i * words) + w;
                if(index < SIZE)
                    indices[index] = val;
            }
        }

        return indices;
    }

}
