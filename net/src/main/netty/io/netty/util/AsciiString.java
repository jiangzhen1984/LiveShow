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

import io.netty.util.ByteProcessor.IndexOfProcessor;
import io.netty.util.internal.PlatformDependent;
import java.util.Arrays;
import static io.netty.util.internal.MathUtil.isOutOfBounds;
import static io.netty.util.internal.ObjectUtil.checkNotNull;

public final class AsciiString implements CharSequence, Comparable<CharSequence> {
    public static final AsciiString EMPTY_STRING = new AsciiString("");
    private static final char MAX_CHAR_VALUE = 255;

    public static final int INDEX_NOT_FOUND = -1;

    private final byte[] value;
    /**
     * Offset into {@link #value} that all operations should use when acting upon {@link #value}.
     */
    private final int offset;
    /**
     * Length in bytes for {@link #value} that we care about. This is independent from {@code value.length}
     * because we may be looking at a subsection of the array.
     */
    private final int length;
    private int hash;
    /**
     * Used to cache the {@link #toString()} value.
     */
    private String string;

    /**
     * Initialize this byte string based upon a byte array.
     * {@code copy} determines if a copy is made or the array is shared.
     */
    public AsciiString(byte[] value, boolean copy) {
        this(value, 0, value.length, copy);
    }

    /**
     * Construct a new instance from a {@code byte[]} array.
     * @param copy {@code true} then a copy of the memory will be made. {@code false} the underlying memory
     * will be shared.
     */
    public AsciiString(byte[] value, int start, int length, boolean copy) {
        if (copy) {
            this.value = Arrays.copyOfRange(value, start, start + length);
            this.offset = 0;
        } else {
            if (isOutOfBounds(start, length, value.length)) {
                throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" +
                        length + ") <= " + "value.length(" + value.length + ')');
            }
            this.value = value;
            this.offset = start;
        }
        this.length = length;
    }

    /**
     * Create a copy of {@code value} into this instance assuming ASCII encoding.
     */
    public AsciiString(CharSequence value) {
        this(value, 0, value.length());
    }

    /**
     * Create a copy of {@code value} into this instance assuming ASCII encoding.
     * The copy will start at index {@code start} and copy {@code length} bytes.
     */
    public AsciiString(CharSequence value, int start, int length) {
        if (isOutOfBounds(start, length, value.length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                            + ") <= " + "value.length(" + value.length() + ')');
        }

        this.value = new byte[length];
        for (int i = 0, j = start; i < length; i++, j++) {
            this.value[i] = c2b(value.charAt(j));
        }
        this.offset = 0;
        this.length = length;
    }

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in ascending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the readable bytes.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public int forEachByte(ByteProcessor visitor) throws Exception {
        return forEachByte0(0, length(), visitor);
    }

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in ascending order.
     * (i.e. {@code index}, {@code (index + 1)},  .. {@code (index + length - 1)}).
     *
     * @return {@code -1} if the processor iterated to or beyond the end of the specified area.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public int forEachByte(int index, int length, ByteProcessor visitor) throws Exception {
        if (isOutOfBounds(index, length, length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= index(" + index + ") <= start + length(" + length
                    + ") <= " + "length(" + length() + ')');
        }
        return forEachByte0(index, length, visitor);
    }

    private int forEachByte0(int index, int length, ByteProcessor visitor) throws Exception {
        final int len = offset + index + length;
        for (int i = offset + index; i < len; ++i) {
            if (!visitor.process(value[i])) {
                return i - offset;
            }
        }
        return -1;
    }

    /**
     * Iterates over the readable bytes of this buffer with the specified {@code processor} in descending order.
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the readable bytes.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public int forEachByteDesc(ByteProcessor visitor) throws Exception {
        return forEachByteDesc0(0, length(), visitor);
    }

    /**
     * Iterates over the specified area of this buffer with the specified {@code processor} in descending order.
     * (i.e. {@code (index + length - 1)}, {@code (index + length - 2)}, ... {@code index}).
     *
     * @return {@code -1} if the processor iterated to or beyond the beginning of the specified area.
     *         The last-visited index If the {@link ByteProcessor#process(byte)} returned {@code false}.
     */
    public int forEachByteDesc(int index, int length, ByteProcessor visitor) throws Exception {
        if (isOutOfBounds(index, length, length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= index(" + index + ") <= start + length(" + length
                    + ") <= " + "length(" + length() + ')');
        }
        return forEachByteDesc0(index, length, visitor);
    }

    private int forEachByteDesc0(int index, int length, ByteProcessor visitor) throws Exception {
        final int end = offset + index;
        for (int i = offset + index + length - 1; i >= end; --i) {
            if (!visitor.process(value[i])) {
                return i - offset;
            }
        }
        return -1;
    }

    public byte byteAt(int index) {
        // We must do a range check here to enforce the access does not go outside our sub region of the array.
        // We rely on the array access itself to pick up the array out of bounds conditions
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index: " + index + " must be in the range [0," + length + ")");
        }
        return value[index + offset];
    }

    /**
     * Determine if this instance has 0 length.
     */
    public boolean isEmpty() {
        return length == 0;
    }

    /**
     * The length in bytes of this instance.
     */
    @Override
    public int length() {
        return length;
    }

    public byte[] array() {
        return value;
    }
    public int arrayOffset() {
        return offset;
    }

    /**
     * Copies the content of this string to a byte array.
     *
     * @param srcIdx the starting offset of characters to copy.
     * @param dst the destination byte array.
     * @param dstIdx the starting offset in the destination byte array.
     * @param length the number of characters to copy.
     */
    public void copy(int srcIdx, byte[] dst, int dstIdx, int length) {
        if (isOutOfBounds(srcIdx, length, length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= srcIdx(" + srcIdx + ") <= srcIdx + length("
                            + length + ") <= srcLen(" + length() + ')');
        }

        System.arraycopy(value, srcIdx + offset, checkNotNull(dst, "dst"), dstIdx, length);
    }

    @Override
    public char charAt(int index) {
        return b2c(byteAt(index));
    }

    /**
     * Determines if this {@code String} contains the sequence of characters in the {@code CharSequence} passed.
     *
     * @param cs the character sequence to search for.
     * @return {@code true} if the sequence of characters are contained in this string, otherwise {@code false}.
     */
    public boolean contains(CharSequence cs) {
        return indexOf(cs) >= 0;
    }

    /**
     * Compares the specified string to this string using the ASCII values of the characters. Returns 0 if the strings
     * contain the same characters in the same order. Returns a negative integer if the first non-equal character in
     * this string has an ASCII value which is less than the ASCII value of the character at the same position in the
     * specified string, or if this string is a prefix of the specified string. Returns a positive integer if the first
     * non-equal character in this string has a ASCII value which is greater than the ASCII value of the character at
     * the same position in the specified string, or if the specified string is a prefix of this string.
     *
     * @param string the string to compare.
     * @return 0 if the strings are equal, a negative integer if this string is before the specified string, or a
     *         positive integer if this string is after the specified string.
     * @throws NullPointerException if {@code string} is {@code null}.
     */
    @Override
    public int compareTo(CharSequence string) {
        if (this == string) {
            return 0;
        }

        int result;
        int length1 = length();
        int length2 = string.length();
        int minLength = Math.min(length1, length2);
        for (int i = 0, j = arrayOffset(); i < minLength; i++, j++) {
            result = b2c(value[j]) - string.charAt(i);
            if (result != 0) {
                return result;
            }
        }

        return length1 - length2;
    }

    /**
     * Compares the specified string to this string ignoring the case of the characters and returns true if they are
     * equal.
     *
     * @param string the string to compare.
     * @return {@code true} if the specified string is equal to this string, {@code false} otherwise.
     */
    public boolean contentEqualsIgnoreCase(CharSequence string) {
        if (string == null || string.length() != length()) {
            return false;
        }

        if (string.getClass() == AsciiString.class) {
            AsciiString rhs = (AsciiString) string;
            for (int i = arrayOffset(), j = rhs.arrayOffset(); i < length(); ++i, ++j) {
                if (!equalsIgnoreCase(value[i], rhs.value[j])) {
                    return false;
                }
            }
            return true;
        }

        for (int i = arrayOffset(), j = 0; i < length(); ++i, ++j) {
            if (!equalsIgnoreCase(b2c(value[i]), string.charAt(j))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Copied the content of this string to a character array.
     *
     * @param srcIdx the starting offset of characters to copy.
     * @param dst the destination character array.
     * @param dstIdx the starting offset in the destination byte array.
     * @param length the number of characters to copy.
     */
    public void copy(int srcIdx, char[] dst, int dstIdx, int length) {
        if (dst == null) {
            throw new NullPointerException("dst");
        }

        if (isOutOfBounds(srcIdx, length, length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= srcIdx(" + srcIdx + ") <= srcIdx + length("
                            + length + ") <= srcLen(" + length() + ')');
        }

        final int dstEnd = dstIdx + length;
        for (int i = dstIdx, j = srcIdx + arrayOffset(); i < dstEnd; i++, j++) {
            dst[i] = b2c(value[j]);
        }
    }

    /**
     * Copies a range of characters into a new string.
     * @param start the offset of the first character (inclusive).
     * @param end The index to stop at (exclusive).
     * @return a new string containing the characters from start to the end of the string.
     * @throws IndexOutOfBoundsException if {@code start < 0} or {@code start > length()}.
     */
    @Override
    public AsciiString subSequence(int start, int end) {
       return subSequence(start, end, true);
    }

    /**
     * Either copy or share a subset of underlying sub-sequence of bytes.
     * @param start the offset of the first character (inclusive).
     * @param end The index to stop at (exclusive).
     * @param copy If {@code true} then a copy of the underlying storage will be made.
     * If {@code false} then the underlying storage will be shared.
     * @return a new string containing the characters from start to the end of the string.
     * @throws IndexOutOfBoundsException if {@code start < 0} or {@code start > length()}.
     */
    public AsciiString subSequence(int start, int end, boolean copy) {
        if (isOutOfBounds(start, end - start, length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= length("
                            + length() + ')');
        }

        if (start == 0 && end == length()) {
            return this;
        }

        if (end == start) {
            return EMPTY_STRING;
        }

        return new AsciiString(value, start + offset, end - start, copy);
    }

    /**
     * Searches in this string for the first index of the specified string. The search for the string starts at the
     * beginning and moves towards the end of this string.
     *
     * @param string the string to find.
     * @return the index of the first character of the specified string in this string, -1 if the specified string is
     *         not a substring.
     * @throws NullPointerException if {@code string} is {@code null}.
     */
    public int indexOf(CharSequence string) {
        return indexOf(string, 0);
    }

    /**
     * Searches in this string for the index of the specified string. The search for the string starts at the specified
     * offset and moves towards the end of this string.
     *
     * @param subString the string to find.
     * @param start the starting offset.
     * @return the index of the first character of the specified string in this string, -1 if the specified string is
     *         not a substring.
     * @throws NullPointerException if {@code subString} is {@code null}.
     */
    public int indexOf(CharSequence subString, int start) {
        if (start < 0) {
            start = 0;
        }

        final int thisLen = length();

        int subCount = subString.length();
        if (subCount <= 0) {
            return start < thisLen ? start : thisLen;
        }
        if (subCount > thisLen - start) {
            return -1;
        }

        final char firstChar = subString.charAt(0);
        if (firstChar > MAX_CHAR_VALUE) {
            return -1;
        }
        ByteProcessor IndexOfVisitor = new IndexOfProcessor((byte) firstChar);
        try {
            for (;;) {
                int i = forEachByte(start, thisLen - start, IndexOfVisitor);
                if (i == -1 || subCount + i > thisLen) {
                    return -1; // handles subCount > count || start >= count
                }
                int o1 = i, o2 = 0;
                while (++o2 < subCount && b2c(value[++o1 + arrayOffset()]) == subString.charAt(o2)) {
                    // Intentionally empty
                }
                if (o2 == subCount) {
                    return i;
                }
                start = i + 1;
            }
        } catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    /**
     * Searches in this string for the index of the specified char {@code ch}.
     * The search for the char starts at the specified offset {@code start} and moves towards the end of this string.
     *
     * @param ch the char to find.
     * @param start the starting offset.
     * @return the index of the first occurrence of the specified char {@code ch} in this string,
     * -1 if found no occurrence.
     */
    public int indexOf(char ch, int start) {
        if (start < 0) {
            start = 0;
        }

        final int thisLen = length();

        if (ch > MAX_CHAR_VALUE) {
            return -1;
        }

        try {
            return forEachByte(start, thisLen - start, new IndexOfProcessor((byte) ch));
        } catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    /**
     * Copies this string replacing occurrences of the specified character with another character.
     *
     * @param oldChar the character to replace.
     * @param newChar the replacement character.
     * @return a new string with occurrences of oldChar replaced by newChar.
     */
    public AsciiString replace(char oldChar, char newChar) {
        if (oldChar > MAX_CHAR_VALUE) {
            return this;
        }

        final int index;
        final byte oldCharByte = c2b(oldChar);
        try {
            index = forEachByte(new IndexOfProcessor(oldCharByte));
        } catch (Exception e) {
            PlatformDependent.throwException(e);
            return this;
        }
        if (index == -1) {
            return this;
        }

        final byte newCharByte = c2b(newChar);
        byte[] buffer = new byte[length()];
        for (int i = 0, j = arrayOffset(); i < buffer.length; i++, j++) {
            byte b = value[j];
            if (b == oldCharByte) {
                b = newCharByte;
            }
            buffer[i] = b;
        }

        return new AsciiString(buffer, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Provides a case-insensitive hash code for Ascii like byte strings.
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = PlatformDependent.hashCodeAscii(value, offset, length);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AsciiString.class) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        AsciiString other = (AsciiString) obj;
        return length() == other.length() &&
               hashCode() == other.hashCode() &&
               PlatformDependent.equals(array(), arrayOffset(), other.array(), other.arrayOffset(), length());
    }

    /**
     * Translates the entire byte string to a {@link String}.
     * @see {@link #toString(int)}
     */
    @Override
    public String toString() {
        if (string != null) {
            return string;
        }
        string = toString(0);
        return string;
    }

    /**
     * Translates the entire byte string to a {@link String} using the {@code charset} encoding.
     * @see {@link #toString(int, int)}
     */
    public String toString(int start) {
        return toString(start, length());
    }

    /**
     * Translates the [{@code start}, {@code end}) range of this byte string to a {@link String}.
     */
    public String toString(int start, int end) {
        int length = end - start;
        if (length == 0) {
            return "";
        }

        if (isOutOfBounds(start, length, length())) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= srcIdx + length("
                            + length + ") <= srcLen(" + length() + ')');
        }

        @SuppressWarnings("deprecation")
        final String str = new String(value, 0, start + offset, length);
        return str;
    }

    /**
     * Returns an {@link AsciiString} containing the given character sequence. If the given string is already a
     * {@link AsciiString}, just returns the same instance.
     */
    public static AsciiString of(CharSequence string) {
        return string.getClass() == AsciiString.class ? (AsciiString) string : new AsciiString(string);
    }

    /**
     * Returns the case-insensitive hash code of the specified string. Note that this method uses the same hashing
     * algorithm with {@link #hashCode()} so that you can put both {@link AsciiString}s and arbitrary
     * {@link CharSequence}s into the same headers.
     */
    public static int hashCode(CharSequence value) {
        if (value == null) {
            return 0;
        }
        if (value.getClass() == AsciiString.class) {
            return value.hashCode();
        }

        return PlatformDependent.hashCodeAscii(value);
    }

    /**
     * Determine if {@code a} contains {@code b} in a case sensitive manner.
     */
    public static boolean contains(CharSequence a, CharSequence b) {
        return contains(a, b, DefaultCharEqualityComparator.INSTANCE);
    }

    /**
     * Returns {@code true} if both {@link CharSequence}'s are equals when ignore the case. This only supports 8-bit
     * ASCII.
     */
    public static boolean contentEqualsIgnoreCase(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            return a == b;
        }

        if (a.getClass() == AsciiString.class) {
            return ((AsciiString) a).contentEqualsIgnoreCase(b);
        }
        if (b.getClass() == AsciiString.class) {
            return ((AsciiString) b).contentEqualsIgnoreCase(a);
        }

        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0, j = 0; i < a.length(); ++i, ++j) {
            if (!equalsIgnoreCase(a.charAt(i),  b.charAt(j))) {
                return false;
            }
        }
        return true;
    }

    private interface CharEqualityComparator {
        boolean equals(char a, char b);
    }

    private static final class DefaultCharEqualityComparator implements CharEqualityComparator {
        static final DefaultCharEqualityComparator INSTANCE = new DefaultCharEqualityComparator();
        private DefaultCharEqualityComparator() { }

        @Override
        public boolean equals(char a, char b) {
            return a == b;
        }
    }

    private static boolean contains(CharSequence a, CharSequence b, CharEqualityComparator cmp) {
        if (a == null || b == null || a.length() < b.length()) {
            return false;
        }
        if (b.length() == 0) {
            return true;
        }
        int bStart = 0;
        for (int i = 0; i < a.length(); ++i) {
            if (cmp.equals(b.charAt(bStart), a.charAt(i))) {
                // If b is consumed then true.
                if (++bStart == b.length()) {
                    return true;
                }
            } else if (a.length() - i < b.length()) {
                // If there are not enough characters left in a for b to be contained, then false.
                return false;
            } else {
                bStart = 0;
            }
        }
        return false;
    }

    /**
     * <p>Finds the first index in the {@code CharSequence} that matches the
     * specified character.</p>
     *
     * @param cs  the {@code CharSequence} to be processed, not null
     * @param searchChar the char to be searched for
     * @param start  the start index, negative starts at the string start
     * @return the index where the search char was found,
     * -1 if char {@code searchChar} is not found or {@code cs == null}
     */
    //-----------------------------------------------------------------------
    public static int indexOf(final CharSequence cs, final char searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        } else if (cs instanceof AsciiString) {
            return ((AsciiString) cs).indexOf(searchChar, start);
        }
        if (cs == null) {
            return INDEX_NOT_FOUND;
        }
        final int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < sz; i++) {
            if (cs.charAt(i) == searchChar) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    private static boolean equalsIgnoreCase(byte a, byte b) {
        return a == b || toLowerCase(a) == toLowerCase(b);
    }

    private static boolean equalsIgnoreCase(char a, char b) {
        return a == b || toLowerCase(a) == toLowerCase(b);
    }

    private static byte toLowerCase(byte b) {
        return isUpperCase(b) ? (byte) (b + 32) : b;
    }

    private static char toLowerCase(char c) {
        return isUpperCase(c) ? (char) (c + 32) : c;
    }

    public static boolean isUpperCase(byte value) {
        return value >= 'A' && value <= 'Z';
    }

    public static boolean isUpperCase(char value) {
        return value >= 'A' && value <= 'Z';
    }

    public static byte c2b(char c) {
        return (byte) ((c > MAX_CHAR_VALUE) ? '?' : c);
    }

    public static char b2c(byte b) {
        return (char) (b & 0xFF);
    }
}
