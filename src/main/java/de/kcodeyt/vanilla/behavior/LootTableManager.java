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

import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@NoArgsConstructor
public class LootTableManager {

    private static final Gson GSON = new Gson();

    @Getter
    private final Set<LootTable> lootTables = new HashSet<>();

    public LootTable getLootTable(String name) {
        for(LootTable lootTable : this.lootTables)
            if(lootTable.path().equalsIgnoreCase(name) || lootTable.name().equalsIgnoreCase(name))
                return lootTable;

        return null;
    }

    public void loadPacks(File packsDir) {
        if(!packsDir.isDirectory()) return;

        for(File file : Objects.requireNonNull(packsDir.listFiles()))
            if(file.isDirectory())
                this.loadPack(file);
    }

    private void loadPack(File packFile) {
        final File manifestFile = new File(packFile, "manifest.json");
        final File lootTablesDir = new File(packFile, "loot_tables");
        if(manifestFile.exists() && lootTablesDir.exists())
            for(File lootTableFile : Objects.requireNonNull(lootTablesDir.listFiles()))
                this.loadLootTable(lootTableFile, "loot_tables");
    }

    private void loadLootTable(File lootTableFile, String currentPath) {
        if(lootTableFile.isDirectory()) {
            for(File nextLootTableFile : Objects.requireNonNull(lootTableFile.listFiles()))
                this.loadLootTable(nextLootTableFile, currentPath + "/" + lootTableFile.getName());
            return;
        }

        final String lootTablePath = currentPath + "/" + lootTableFile.getName();
        final String lootTableName = lootTableFile.getName().split("\\.")[0];
        if(!lootTablePath.contains("chest")) return;

        try(final FileReader fileReader = new FileReader(lootTableFile);
            final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            this.lootTables.add(LootTableBuilder.build(lootTablePath, lootTableName, GSON.<Map<String, Object>>fromJson(bufferedReader, Map.class)));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
