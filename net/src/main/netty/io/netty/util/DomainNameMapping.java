/*
 * Copyright 2014 The Netty Project
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

package io.netty.util;

import io.netty.util.internal.StringUtil;

import java.net.IDN;
import java.util.Locale;
import java.util.Map;

import static io.netty.util.internal.ObjectUtil.checkNotNull;
import static io.netty.util.internal.StringUtil.commonSuffixOfLength;

/**
 * Maps a domain name to its associated value object.
 * <p>
 * DNS wildcard is supported as hostname, so you can use {@code *.netty.io} to match both {@code netty.io}
 * and {@code downloads.netty.io}.
 * </p>
 */
public class DomainNameMapping<V> implements Mapping<String, V> {

    final V defaultValue;
    private final Map<String, V> map;

    DomainNameMapping(Map<String, V> map, V defaultValue) {
        this.defaultValue = checkNotNull(defaultValue, "defaultValue");
        this.map = map;
    }

    /**
     * Adds a mapping that maps the specified (optionally wildcard) host name to the specified output value.
     * <p>
     * <a href="http://en.wikipedia.org/wiki/Wildcard_DNS_record">DNS wildcard</a> is supported as hostname.
     * For example, you can use {@code *.netty.io} to match {@code netty.io} and {@code downloads.netty.io}.
     * </p>
     *
     * @param hostname the host name (optionally wildcard)
     * @param output   the output value that will be returned by {@link #map(String)} when the specified host name
     *                 matches the specified input host name
     * @deprecated use {@link DomainNameMappingBuilder} to create and fill the mapping instead
     */
    @Deprecated
    public DomainNameMapping<V> add(String hostname, V output) {
        map.put(normalizeHostname(checkNotNull(hostname, "hostname")), checkNotNull(output, "output"));
        return this;
    }

    /**
     * Simple function to match <a href="http://en.wikipedia.org/wiki/Wildcard_DNS_record">DNS wildcard</a>.
     */
    static boolean matches(String template, String hostName) {
        if (template.startsWith("*.")) {
            return template.regionMatches(2, hostName, 0, hostName.length())
                || commonSuffixOfLength(hostName, template, template.length() - 1);
        }
        return template.equals(hostName);
    }

    /**
     * IDNA ASCII conversion and case normalization
     */
    static String normalizeHostname(String hostname) {
        if (needsNormalization(hostname)) {
            hostname = IDN.toASCII(hostname, IDN.ALLOW_UNASSIGNED);
        }
        return hostname.toLowerCase(Locale.US);
    }

    private static boolean needsNormalization(String hostname) {
        final int length = hostname.length();
        for (int i = 0; i < length; i++) {
            int c = hostname.charAt(i);
            if (c > 0x7F) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V map(String hostname) {
        if (hostname != null) {
            hostname = normalizeHostname(hostname);

            for (Map.Entry<String, V> entry : map.entrySet()) {
                if (matches(entry.getKey(), hostname)) {
                    return entry.getValue();
                }
            }
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(default: " + defaultValue + ", map: " + map + ')';
    }
}
