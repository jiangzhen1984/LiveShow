/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This implementation allows to register {@link ChannelFuture} instances which will get notified once some amount of
 * data was written and so a checkpoint was reached.
 */
public final class ChannelFlushPromiseNotifier {

    private long writeCounter;
    private final Queue<FlushCheckpoint> flushCheckpoints = new ArrayDeque<FlushCheckpoint>();
    private final boolean tryNotify;

    /**
     * Create a new instance
     *
     * @param tryNotify if {@code true} the {@link ChannelPromise}s will get notified with
     *                  {@link ChannelPromise#trySuccess()} and {@link ChannelPromise#tryFailure(Throwable)}.
     *                  Otherwise {@link ChannelPromise#setSuccess()} and {@link ChannelPromise#setFailure(Throwable)}
     *                  is used
     */
    public ChannelFlushPromiseNotifier(boolean tryNotify) {
        this.tryNotify = tryNotify;
    }

    /**
     * @deprecated use {@link #add(ChannelPromise, long)}
     */
    @Deprecated
    public ChannelFlushPromiseNotifier add(ChannelPromise promise, int pendingDataSize) {
        return add(promise, (long) pendingDataSize);
    }

    /**
     * Add a {@link ChannelPromise} to this {@link ChannelFlushPromiseNotifier} which will be notified after the given
     * {@code pendingDataSize} was reached.
     */
    public ChannelFlushPromiseNotifier add(ChannelPromise promise, long pendingDataSize) {
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        if (pendingDataSize < 0) {
            throw new IllegalArgumentException("pendingDataSize must be >= 0 but was " + pendingDataSize);
        }
        long checkpoint = writeCounter + pendingDataSize;
        if (promise instanceof FlushCheckpoint) {
            FlushCheckpoint cp = (FlushCheckpoint) promise;
            cp.flushCheckpoint(checkpoint);
            flushCheckpoints.add(cp);
        } else {
            flushCheckpoints.add(new DefaultFlushCheckpoint(checkpoint, promise));
        }
        return this;
    }

    interface FlushCheckpoint {
        void flushCheckpoint(long checkpoint);
        ChannelPromise promise();
    }

    private static class DefaultFlushCheckpoint implements FlushCheckpoint {
        private final ChannelPromise future;

        DefaultFlushCheckpoint(long checkpoint, ChannelPromise future) {
            this.future = future;
        }

        @Override
        public void flushCheckpoint(long checkpoint) {
        }

        @Override
        public ChannelPromise promise() {
            return future;
        }
    }
}
