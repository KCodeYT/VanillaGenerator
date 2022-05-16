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

package de.kcodeyt.vanilla.generator.client.clientdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@AllArgsConstructor
public class Skin {

    private final BufferedImage image;
    private final String geometryName;
    private final String geometryData;
    private final String geometryDataEngineVersion;

    public byte[] getSkinData() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for(int y = 0; y < this.image.getHeight(); ++y) {
            for(int x = 0; x < this.image.getWidth(); ++x) {
                final Color color = new Color(this.image.getRGB(x, y), true);
                outputStream.write(color.getRed());
                outputStream.write(color.getGreen());
                outputStream.write(color.getBlue());
                outputStream.write(color.getAlpha());
            }
        }

        return outputStream.toByteArray();
    }

}
