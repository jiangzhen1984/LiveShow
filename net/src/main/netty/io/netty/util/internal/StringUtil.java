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
package io.netty.util.internal;

/**
 * String utility class.
 */
public final class StringUtil {

    public static final String EMPTY_STRING = "";
    public static final String NEWLINE = System.getProperty("line.separator");

    private static final String[] BYTE2HEX_PAD = new String[256];
    private static final String[] BYTE2HEX_NOPAD = new String[256];

    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    static {
        // Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
        int i;
        for (i = 0; i < 10; i ++) {
            BYTE2HEX_PAD[i] = "0" + i;
            BYTE2HEX_NOPAD[i] = String.valueOf(i);
        }
        for (; i < 16; i ++) {
            char c = (char) ('a' + i - 10);
            BYTE2HEX_PAD[i] = "0" + c;
            BYTE2HEX_NOPAD[i] = String.valueOf(c);
        }
        for (; i < BYTE2HEX_PAD.length; i ++) {
            String str = Integer.toHexString(i);
            BYTE2HEX_PAD[i] = str;
            BYTE2HEX_NOPAD[i] = str;
        }
    }

    private StringUtil() {
        // Unused.
    }

    /**
     * Checks if two strings have the same suffix of specified length
     *
     * @param s            string
     * @param p            string
     * @param len length of the common suffix
     * @return true if both s and p are not null and both have the same suffix. Otherwise - false
     */
    public static boolean commonSuffixOfLength(String s, String p, int len) {
        return s != null && p != null && len >= 0 && s.regionMatches(s.length() - len, p, p.length() - len, len);
    }

    /**
     * Converts the specified byte value into a 2-digit hexadecimal integer.
     */
    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 0xff];
    }

    /**
     * The shortcut to {@link #simpleClassName(Class) simpleClassName(o.getClass())}.
     */
    public static String simpleClassName(Object o) {
        if (o == null) {
            return "null_object";
        } else {
            return simpleClassName(o.getClass());
        }
    }

    /**
     * Generates a simplified name from a {@link Class}.  Similar to {@link Class#getSimpleName()}, but it works fine
     * with anonymous classes.
     */
    public static String simpleClassName(Class<?> clazz) {
        String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }

    /**
     * Get the length of a string, {@code null} input is considered {@code 0} length.
     */
    public static int length(String s) {
        return s == null ? 0 : s.length();
    }

    /**
     * Determine if {@code c} lies within the range of values defined for
     * <a href="http://unicode.org/glossary/#surrogate_code_point">Surrogate Code Point</a>.
     * @param c the character to check.
     * @return {@code true} if {@code c} lies within the range of values defined for
     * <a href="http://unicode.org/glossary/#surrogate_code_point">Surrogate Code Point</a>. {@code false} otherwise.
     */
    public static boolean isSurrogate(char c) {
        return c >= '\uD800' && c <= '\uDFFF';
    }
}
