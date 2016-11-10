/*
 * Copyright 2013 The Netty Project
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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.EventLoop;

import java.util.Set;

public interface ChannelGroup extends Set<Channel>, Comparable<ChannelGroup> {

    /**
     * Returns the name of this group.  A group name is purely for helping
     * you to distinguish one group from others.
     */
    String name();

    /**
     * Returns the {@link Channel} which has the specified {@link ChannelId}.
     *
     * @return the matching {@link Channel} if found. {@code null} otherwise.
     */
    Channel find(ChannelId id);

    /**
     * Writes the specified {@code message} to all {@link Channel}s in this
     * group. If the specified {@code message} is an instance of
     * {@link ByteBuf}, it is automatically
     * {@linkplain ByteBuf#duplicate() duplicated} to avoid a race
     * condition. The same is true for {@link ByteBufHolder}. Please note that this operation is asynchronous as
     * {@link Channel#write(Object)} is.
     *
     * @return itself
     */
    ChannelGroupFuture write(Object message);

    /**
     * Writes the specified {@code message} to all {@link Channel}s in this
     * group that are matched by the given {@link ChannelMatcher}. If the specified {@code message} is an instance of
     * {@link ByteBuf}, it is automatically
     * {@linkplain ByteBuf#duplicate() duplicated} to avoid a race
     * condition. The same is true for {@link ByteBufHolder}. Please note that this operation is asynchronous as
     * {@link Channel#write(Object)} is.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture write(Object message, ChannelMatcher matcher);

    /**
     * Writes the specified {@code message} to all {@link Channel}s in this
     * group that are matched by the given {@link ChannelMatcher}. If the specified {@code message} is an instance of
     * {@link ByteBuf}, it is automatically
     * {@linkplain ByteBuf#duplicate() duplicated} to avoid a race
     * condition. The same is true for {@link ByteBufHolder}. Please note that this operation is asynchronous as
     * {@link Channel#write(Object)} is.
     *
     * If {@code voidPromise} is {@code true} {@link Channel#voidPromise()} is used for the writes and so the same
     * restrictions to the returned {@link ChannelGroupFuture} apply as to a void promise.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise);

    /**
     * Flush all {@link Channel}s in this
     * group. If the specified {@code messages} are an instance of
     * {@link ByteBuf}, it is automatically
     * {@linkplain ByteBuf#duplicate() duplicated} to avoid a race
     * condition. Please note that this operation is asynchronous as
     * {@link Channel#write(Object)} is.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroup flush();

    /**
     * Flush all {@link Channel}s in this group that are matched by the given {@link ChannelMatcher}.
     * If the specified {@code messages} are an instance of
     * {@link ByteBuf}, it is automatically
     * {@linkplain ByteBuf#duplicate() duplicated} to avoid a race
     * condition. Please note that this operation is asynchronous as
     * {@link Channel#write(Object)} is.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroup flush(ChannelMatcher matcher);

    /**
     * Disconnects all {@link Channel}s in this group from their remote peers.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture disconnect();

    /**
     * Disconnects all {@link Channel}s in this group from their remote peers,
     * that are matched by the given {@link ChannelMatcher}.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture disconnect(ChannelMatcher matcher);

    /**
     * Closes all {@link Channel}s in this group.  If the {@link Channel} is
     * connected to a remote peer or bound to a local address, it is
     * automatically disconnected and unbound.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture close();

    /**
     * Closes all {@link Channel}s in this group that are matched by the given {@link ChannelMatcher}.
     * If the {@link Channel} is  connected to a remote peer or bound to a local address, it is
     * automatically disconnected and unbound.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    ChannelGroupFuture close(ChannelMatcher matcher);

    /**
     * @deprecated This method will be removed in the next major feature release.
     *
     * Deregister all {@link Channel}s in this group from their {@link EventLoop}.
     * Please note that this operation is asynchronous as {@link Channel#deregister()} is.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    @Deprecated
    ChannelGroupFuture deregister();

    /**
     * @deprecated This method will be removed in the next major feature release.
     *
     * Deregister all {@link Channel}s in this group from their {@link EventLoop} that are matched by the given
     * {@link ChannelMatcher}. Please note that this operation is asynchronous as {@link Channel#deregister()} is.
     *
     * @return the {@link ChannelGroupFuture} instance that notifies when
     *         the operation is done for all channels
     */
    @Deprecated
    ChannelGroupFuture deregister(ChannelMatcher matcher);
}
