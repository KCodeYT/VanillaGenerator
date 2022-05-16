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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum JwtAlgorithm {

    ES384(new JwtSignatureES384());

    private final JwtSignature signature;

    public static JwtAlgorithm byName(String alg) {
        for(JwtAlgorithm algorithm : values())
            if(algorithm.name().equalsIgnoreCase(alg)) return algorithm;
        return null;
    }

}
