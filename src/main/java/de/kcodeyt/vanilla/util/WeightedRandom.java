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

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class WeightedRandom {

    private static int getTotalWeight(List<? extends Item> weightedList) {
        return weightedList.stream().mapToInt(Item::getWeight).sum();
    }

    private static <T extends WeightedRandom.Item> T getRandomItem(Random random, List<T> weightedList, int totalWeight) {
        return totalWeight <= 0 ? null : getRandomItem(weightedList, random.nextInt(totalWeight));
    }

    private static <T extends WeightedRandom.Item> T getRandomItem(List<T> weightedList, int weight) {
        int i = 0;
        for(int j = weightedList.size(); i < j; ++i) {
            final T weightItem = weightedList.get(i);
            weight -= weightItem.weight;
            if (weight < 0)
                return weightItem;
        }

        return null;
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, List<T> weightedList) {
        return getRandomItem(random, weightedList, getTotalWeight(weightedList));
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        final int weight;
    }

}
