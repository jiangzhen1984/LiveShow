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

import io.netty.channel.Channel;

/**
 * Helper class which provides often used {@link ChannelMatcher} implementations.
 */
public final class ChannelMatchers {

    private static final ChannelMatcher ALL_MATCHER = new ChannelMatcher() {
        @Override
        public boolean matches(Channel channel) {
            return true;
        }
    };


    private ChannelMatchers() {
        // static methods only
    }

    /**
     * Returns a {@link ChannelMatcher} that matches all {@link Channel}s.
     */
    public static ChannelMatcher all() {
        return ALL_MATCHER;
    }

    /**
     * Returns a {@link ChannelMatcher} that matches the given {@link Channel}.
     */
    public static ChannelMatcher is(Channel channel) {
        return new InstanceMatcher(channel);
    }

    private static final class InstanceMatcher implements ChannelMatcher {
        private final Channel channel;

        InstanceMatcher(Channel channel) {
            this.channel = channel;
        }

        @Override
        public boolean matches(Channel ch) {
            return channel == ch;
        }
    }
}
