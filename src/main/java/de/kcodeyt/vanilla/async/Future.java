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

package de.kcodeyt.vanilla.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class Future<T> {

    private static final byte UNRESOLVED = (byte) 0;
    private static final byte RESOLVED = (byte) 1;
    private static final byte FAILED = (byte) 2;

    private byte state = UNRESOLVED;
    private Object result;

    public synchronized T get() throws InterruptedException, ExecutionException {
        while(this.state == UNRESOLVED) this.wait();
        return this.getResultOrException();
    }

    public synchronized T get(long duration, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long now = System.currentTimeMillis();
        long end = now + unit.toMillis(duration);

        while(this.state == UNRESOLVED && now < end) {
            this.wait(end - now);
            now = System.currentTimeMillis();
        }

        return getResultOrTimeoutException();
    }

    public synchronized boolean isDone() {
        return this.state != UNRESOLVED;
    }

    public synchronized boolean isSuccess() {
        return this.state == RESOLVED;
    }

    public synchronized boolean isFailure() {
        return this.state == FAILED;
    }

    public synchronized void resolve(T result) {
        setWhenUnresolved(result, RESOLVED);
    }

    public synchronized void fail(Throwable cause) {
        setWhenUnresolved(cause, FAILED);
    }

    @SuppressWarnings("unchecked")
    private T getResultOrException() throws ExecutionException {
        return switch(this.state) {
            case RESOLVED -> (T) this.result;
            case FAILED -> throw new ExecutionException("Future operation failed to execute", (Throwable) this.result);
            default -> throw new IllegalStateException("Unexpected value: " + this.state);
        };
    }

    @SuppressWarnings("unchecked")
    private T getResultOrTimeoutException() throws TimeoutException, ExecutionException {
        return switch(this.state) {
            case UNRESOLVED -> throw new TimeoutException("Future operation did not complete within timeout");
            case RESOLVED -> (T) this.result;
            case FAILED -> throw new ExecutionException("Future operation failed to execute", (Throwable) this.result);
            default -> throw new IllegalStateException("Unexpected value: " + this.state);
        };
    }

    private synchronized void setWhenUnresolved(Object result, byte resolved) {
        if(this.state == UNRESOLVED) {
            this.state = resolved;
            this.result = result;
            this.notifyAll();
        }
    }

}
