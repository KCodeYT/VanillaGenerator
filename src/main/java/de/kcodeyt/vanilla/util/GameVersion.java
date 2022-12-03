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

import lombok.Value;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
public class GameVersion {

    public static final GameVersion NULL_VERSION = new GameVersion(0, 0, 0, 0, "0.0.0.0");

    int major, minor, patch, build;
    String versionString;

    public static GameVersion of(String version) {
        final String[] split = version.split("\\.");

        try {
            final int major, minor, patch, build;
            if(split.length == 0)
                throw new UnsupportedOperationException("Could not parse major of version: " + version);
            major = Integer.parseInt(split[0]);

            if(split.length == 1)
                throw new UnsupportedOperationException("Could not parse minor of version: " + version);
            minor = Integer.parseInt(split[1]);

            if(split.length == 2)
                throw new UnsupportedOperationException("Could not parse patch of version: " + version);
            patch = Integer.parseInt(split[2]);

            if(split.length == 3) build = 0;
            else build = Integer.parseInt(split[3]);

            return new GameVersion(major, minor, patch, build, version);
        } catch(NumberFormatException e) {
            throw new UnsupportedOperationException("Could not parse version: " + version);
        }
    }

    @Override
    public String toString() {
        return this.versionString;
    }

    public boolean isOlderThan(GameVersion other) {
        if(this.major < other.major) return true;
        if(this.major > other.major) return false;

        if(this.minor < other.minor) return true;
        if(this.minor > other.minor) return false;

        if(this.patch < other.patch) return true;
        if(this.patch > other.patch) return false;

        return this.build < other.build;
    }

}
