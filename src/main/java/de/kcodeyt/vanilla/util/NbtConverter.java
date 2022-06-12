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

import cn.nukkit.nbt.tag.*;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class NbtConverter {

    public static CompoundTag convert(NbtMap nbtMap) {
        return convert(null, nbtMap);
    }

    public static CompoundTag convert(String baseName, NbtMap nbtMap) {
        final CompoundTag baseTag = new CompoundTag(baseName == null ? "" : baseName);
        for(String tagName : nbtMap.keySet()) {
            final Object value = nbtMap.get(tagName);
            final Tag newTag;
            if(value != null && ((newTag = convertTag(tagName, value)) != null))
                baseTag.put(tagName, newTag);
        }
        return baseTag;
    }

    private static Tag convertTag(String name, Object value) {
        final Class<?> clazz = value.getClass();
        return switch(NbtType.byClass(clazz).getEnum()) {
            case BYTE -> new ByteTag(name, (byte) value);
            case SHORT -> new ShortTag(name, (short) value);
            case INT -> new IntTag(name, (int) value);
            case LONG -> new LongTag(name, (long) value);
            case FLOAT -> new FloatTag(name, (float) value);
            case DOUBLE -> new DoubleTag(name, (double) value);
            case BYTE_ARRAY -> new ByteArrayTag(name, (byte[]) value);
            case STRING -> new StringTag(name, (String) value);
            case LIST -> convertList(name, (NbtList<?>) value);
            case COMPOUND -> convert(name, (NbtMap) value);
            case INT_ARRAY -> new IntArrayTag(name, (int[]) value);
            default -> null;
        };
    }

    private static ListTag<Tag> convertList(String name, NbtList<?> list) {
        final ListTag<Tag> listTag = new ListTag<>(name);
        listTag.setAll(list.stream().map(o -> convertTag("", o)).filter(Objects::nonNull).collect(Collectors.toList()));
        return listTag;
    }

}
