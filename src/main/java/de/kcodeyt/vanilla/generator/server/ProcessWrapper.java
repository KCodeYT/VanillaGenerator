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

package de.kcodeyt.vanilla.generator.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class ProcessWrapper {

    private final Process process;

    ProcessWrapper(ProcessBuilder builder, Consumer<String> stdoutConsumer) throws IOException {
        this.process = builder.start();
        final Thread stdReader = new Thread(() -> {
            try(final BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()))) {
                String line;
                while((line = reader.readLine()) != null) stdoutConsumer.accept(line);
            } catch(Exception ignored) {
            }
        });
        stdReader.setDaemon(true);
        stdReader.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(ProcessWrapper.this.isAlive())
                kill();
        }));
    }

    private boolean isAlive() {
        return this.process.isAlive();
    }

    public void kill() {
        this.process.destroyForcibly();
    }

}
