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

package de.kcodeyt.vanilla.behavior;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class LootTableNumber {

    public static LootTableNumber of(Object obj) {
        if(obj instanceof Double) return new LootTableNumber((double) obj);

        if(obj instanceof List) {
            final double min = (double) (Object) ((List<?>) obj).get(0);
            final double max = (double) (Object) ((List<?>) obj).get(1);
            return new LootTableNumber(min, max);
        }

        if(obj instanceof final Map<?, ?> map) {
            final double min = (double) (Object) nonNull(map.get("min"));
            final double max = (double) (Object) nonNull(map.get("max"));
            return new LootTableNumber(min, max);
        }

        throw new RuntimeException("Could not resolve number from object " + obj + "!");
    }

    public static int random(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    @SuppressWarnings("unchecked")
    private static <T> T nonNull(T value) {
        return value == null ? (T) (Object) 0.0 : value;
    }

    private final double min;
    private final double max;

    private LootTableNumber(double value) {
        this(value, value);
    }

    private LootTableNumber(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public int getAsInt() {
        return this.getAsInt(ThreadLocalRandom.current());
    }

    public double getAsDouble() {
        return this.getAsDouble(ThreadLocalRandom.current());
    }

    public int getAsInt(Random random) {
        if(this.min == this.max) return (int) this.min;
        return random(random, (int) this.min, (int) this.max);
    }

    public double getAsDouble(Random random) {
        return this.getAsDouble(random, false);
    }

    public double getAsDouble(Random random, boolean percent) {
        if(this.min == this.max) return this.min;

        if(percent) return random(random, (int) Math.floor(this.min * 100D), (int) Math.floor(this.max * 100D)) / 100D;
        else return random.nextDouble(this.max - this.min + 1) + this.min;
    }

}
