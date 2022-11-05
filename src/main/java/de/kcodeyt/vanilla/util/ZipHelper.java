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

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class ZipHelper {

    public void unzip(File fileZip, File destDir) throws IOException {
        if(!destDir.exists() && !destDir.mkdirs()) return;

        try(final ZipInputStream inputStream = new ZipInputStream(new FileInputStream(fileZip))) {
            ZipEntry zipEntry;
            while((zipEntry = inputStream.getNextEntry()) != null) {
                final File newFile = newFile(destDir, zipEntry);
                if(zipEntry.isDirectory()) {
                    if(!newFile.mkdirs()) {
                        inputStream.closeEntry();
                        return;
                    }
                } else {
                    try(final FileOutputStream outputStream = new FileOutputStream(newFile)) {
                        inputStream.transferTo(outputStream);
                    }
                }
            }

            inputStream.closeEntry();
        }
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        final File destFile = new File(destinationDir, zipEntry.getName());
        final String destDirPath = destinationDir.getCanonicalPath();
        final String destFilePath = destFile.getCanonicalPath();
        if(!destFilePath.startsWith(destDirPath + File.separator))
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        return destFile;
    }

}
