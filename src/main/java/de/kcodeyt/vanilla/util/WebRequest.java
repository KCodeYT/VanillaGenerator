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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class WebRequest {

    public static String request(String urlString) {
        try {
            final HttpURLConnection httpConnection = (HttpURLConnection) new URL(urlString).openConnection();
            httpConnection.setRequestProperty("User-Agent", "Chrome");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            final StringBuilder resultLine = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
                resultLine.append(line).append("\n");
            bufferedReader.close();
            httpConnection.disconnect();
            return resultLine.toString();
        } catch(Throwable throwable) {
            return "{}";
        }
    }

}
