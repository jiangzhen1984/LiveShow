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
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Iterator;

/**
 * In spite of the disadvantages mentioned above, there are certainly the cases
 * where it is more convenient to call {@link #await()}. In such a case, please
 * make sure you do not call {@link #await()} in an I/O thread.  Otherwise,
 * {@link IllegalStateException} will be raised to prevent a dead lock.
 */
public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture> {

    /**
     * Returns the {@link ChannelGroup} which is associated with this future.
     */
    ChannelGroup group();

    /**
     * Returns the {@link ChannelFuture} of the individual I/O operation which
     * is associated with the specified {@link Channel}.
     *
     * @return the matching {@link ChannelFuture} if found.
     *         {@code null} otherwise.
     */
    ChannelFuture find(Channel channel);

    /**
     * Returns {@code true} if and only if all I/O operations associated with
     * this future were successful without any failure.
     */
    @Override
    boolean isSuccess();

    @Override
    ChannelGroupException cause();

    @Override
    ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelGroupFuture await() throws InterruptedException;

    @Override
    ChannelGroupFuture awaitUninterruptibly();

    @Override
    ChannelGroupFuture syncUninterruptibly();

    @Override
    ChannelGroupFuture sync() throws InterruptedException;

    /**
     * Returns the {@link Iterator} that enumerates all {@link ChannelFuture}s
     * which are associated with this future.  Please note that the returned
     * {@link Iterator} is is unmodifiable, which means a {@link ChannelFuture}
     * cannot be removed from this future.
     */
    @Override
    Iterator<ChannelFuture> iterator();
}
