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

import io.netty.util.CharsetUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.regex.Pattern;
import static io.netty.util.internal.PlatformDependent0.HASH_CODE_ASCII_SEED;
import static io.netty.util.internal.PlatformDependent0.HASH_CODE_C1;
import static io.netty.util.internal.PlatformDependent0.HASH_CODE_C2;
import static io.netty.util.internal.PlatformDependent0.hashCodeAsciiSanitize;

/**
 * Utility that detects various properties specific to the current runtime
 * environment, such as Java version and the availability of the
 * {@code sun.misc.Unsafe} object.
 * <p>
 * You can disable the use of {@code sun.misc.Unsafe} if you specify
 * the system property <strong>io.netty.noUnsafe</strong>.
 */
public final class PlatformDependent {

    private static final boolean IS_WINDOWS = false;
    private static volatile Boolean IS_ROOT;

    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = false;

    private static final boolean HAS_UNSAFE = false;
    private static final boolean DIRECT_BUFFER_PREFERRED = false;
    private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
    private static final AtomicLong DIRECT_MEMORY_COUNTER;
    private static final long DIRECT_MEMORY_LIMIT;

    private static final boolean BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    static {
        // Here is how the system property is used:
        //
        // * <  0  - Don't use cleaner, and inherit max direct memory from java. In this case the
        //           "practical max direct memory" would be 2 * max memory as defined by the JDK.
        // * == 0  - Use cleaner, Netty will not enforce max memory, and instead will defer to JDK.
        // * >  0  - Don't use cleaner. This will limit Netty's total direct memory
        //           (note: that JDK's direct memory limit is independent of this).
        long maxDirectMemory = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1);

        USE_DIRECT_BUFFER_NO_CLEANER = false;
        DIRECT_MEMORY_COUNTER = null;
        DIRECT_MEMORY_LIMIT = maxDirectMemory;
    }

    /**
     * Return {@code true} if the JVM is running on Windows
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    /**
     * Return {@code true} if the current user is root.  Note that this method returns
     * {@code false} if on Windows.
     */
    public static boolean isRoot() {
        if (IS_ROOT == null) {
            synchronized (PlatformDependent.class) {
                if (IS_ROOT == null) {
                    IS_ROOT = isRoot0();
                }
            }
        }
        return IS_ROOT;
    }

    /**
     * Returns {@code true} if and only if it is fine to enable TCP_NODELAY socket option by default.
     */
    public static boolean canEnableTcpNoDelayByDefault() {
        return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }

    /**
     * Return {@code true} if {@code sun.misc.Unsafe} was found on the classpath and can be used for acclerated
     * direct memory access.
     */
    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    /**
     * Returns {@code true} if the platform has reliable low-level direct buffer access API and a user has not specified
     * {@code -Dio.netty.noPreferDirect} option.
     */
    public static boolean directBufferPreferred() {
        return DIRECT_BUFFER_PREFERRED;
    }

    /**
     * Raises an exception bypassing compiler checks for checked exceptions.
     */
    public static void throwException(Throwable t) {
        PlatformDependent.<RuntimeException>throwException0(t);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwException0(Throwable t) throws E {
        throw (E) t;
    }

    /**
     * Creates a new fastest {@link ConcurrentMap} implementaion for the current platform.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    private static long getLongSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (long) bytes[offset] << 56 |
                    ((long) bytes[offset + 1] & 0xff) << 48 |
                    ((long) bytes[offset + 2] & 0xff) << 40 |
                    ((long) bytes[offset + 3] & 0xff) << 32 |
                    ((long) bytes[offset + 4] & 0xff) << 24 |
                    ((long) bytes[offset + 5] & 0xff) << 16 |
                    ((long) bytes[offset + 6] & 0xff) <<  8 |
                    (long) bytes[offset + 7] & 0xff;
        }
        return (long) bytes[offset] & 0xff |
                ((long) bytes[offset + 1] & 0xff) << 8 |
                ((long) bytes[offset + 2] & 0xff) << 16 |
                ((long) bytes[offset + 3] & 0xff) << 24 |
                ((long) bytes[offset + 4] & 0xff) << 32 |
                ((long) bytes[offset + 5] & 0xff) << 40 |
                ((long) bytes[offset + 6] & 0xff) << 48 |
                ((long) bytes[offset + 7] & 0xff) << 56;
    }

    private static int getIntSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return bytes[offset] << 24 |
                    (bytes[offset + 1] & 0xff) << 16 |
                    (bytes[offset + 2] & 0xff) << 8 |
                    bytes[offset + 3] & 0xff;
        }
        return bytes[offset] & 0xff |
                (bytes[offset + 1] & 0xff) << 8 |
                (bytes[offset + 2] & 0xff) << 16 |
                bytes[offset + 3] << 24;
    }

    private static short getShortSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (short) (bytes[offset] << 8 | (bytes[offset + 1] & 0xff));
        }
        return (short) (bytes[offset] & 0xff | (bytes[offset + 1] << 8));
    }
    
    /**
     * Identical to {@link PlatformDependent0#hashCodeAsciiCompute(long, int)} but for {@link CharSequence}.
     */
    private static int hashCodeAsciiCompute(CharSequence value, int offset, int hash) {
        // masking with 0x1f reduces the number of overall bits that impact the hash code but makes the hash
        // code the same regardless of character case (upper case or lower case hash is the same).
        return hash * HASH_CODE_C1 +
                // Low order int
                hashCodeAsciiSanitizeInt(value, offset) * HASH_CODE_C2 +
                // High order int
                hashCodeAsciiSanitizeInt(value, offset + 4);
    }
    
    
    

    /**
     * Identical to {@link PlatformDependent0#hashCodeAsciiSanitize(int)} but for {@link CharSequence}.
     */
    private static int hashCodeAsciiSanitizeInt(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            // mimic a unsafe.getInt call on a big endian machine
            return (value.charAt(offset) & 0x1f) |
                   (value.charAt(offset + 2) & 0x1f) << 8 |
                   (value.charAt(offset + 1) & 0x1f) << 16 |
                   (value.charAt(offset) & 0x1f) << 24;
        }
        return (value.charAt(offset + 3) & 0x1f) << 24 |
               (value.charAt(offset + 2) & 0x1f) << 16 |
               (value.charAt(offset + 1) & 0x1f) << 8 |
               (value.charAt(offset) & 0x1f);
    }

    /**
     * Identical to {@link PlatformDependent0#hashCodeAsciiSanitize(short)} but for {@link CharSequence}.
     */
    private static int hashCodeAsciiSanitizeShort(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            // mimic a unsafe.getShort call on a big endian machine
            return (value.charAt(offset + 1) & 0x1f) |
                    (value.charAt(offset) & 0x1f) << 8;
        }
        return (value.charAt(offset + 1) & 0x1f) << 8 |
                (value.charAt(offset) & 0x1f);
    }

    /**
     * Identical to {@link PlatformDependent0#hashCodeAsciiSanitize(byte)} but for {@link CharSequence}.
     */
    private static int hashCodeAsciiSanitizsByte(char value) {
        return value & 0x1f;
    }

    public static ByteBuffer allocateDirectNoCleaner(int capacity) {
        assert USE_DIRECT_BUFFER_NO_CLEANER;

        incrementMemoryCounter(capacity);
        decrementMemoryCounter(capacity);
        return null;
    }

    private static void incrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER != null) {
            for (;;) {
                long usedMemory = DIRECT_MEMORY_COUNTER.get();
                long newUsedMemory = usedMemory + capacity;
                if (newUsedMemory > DIRECT_MEMORY_LIMIT) {
                    throw new OutOfDirectMemoryError("failed to allocate " + capacity
                            + " byte(s) of direct memory (used: " + usedMemory + ", max: " + DIRECT_MEMORY_LIMIT + ')');
                }
                if (DIRECT_MEMORY_COUNTER.compareAndSet(usedMemory, newUsedMemory)) {
                    break;
                }
            }
        }
    }

    private static void decrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER != null) {
            long usedMemory = DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
            assert usedMemory >= 0;
        }
    }

    /**
     * Compare two {@code byte} arrays for equality. For performance reasons no bounds checking on the
     * parameters is performed.
     *
     * @param bytes1 the first byte array.
     * @param startPos1 the position (inclusive) to start comparing in {@code bytes1}.
     * @param bytes2 the second byte array.
     * @param startPos2 the position (inclusive) to start comparing in {@code bytes2}.
     * @param length the amount of bytes to compare. This is assumed to be validated as not going out of bounds
     * by the caller.
     */
    public static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        return equalsSafe(bytes1, startPos1, bytes2, startPos2, length);
    }

    /**
     * Calculate a hash code of a byte array assuming ASCII character encoding.
     * The resulting hash code will be case insensitive.
     * @param bytes The array which contains the data to hash.
     * @param startPos What index to start generating a hash code in {@code bytes}
     * @param length The amount of bytes that should be accounted for in the computation.
     * @return The hash code of {@code bytes} assuming ASCII character encoding.
     * The resulting hash code will be case insensitive.
     */
    public static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        return hashCodeAsciiSafe(bytes, startPos, length);
    }

    /**
     * Calculate a hash code of a byte array assuming ASCII character encoding.
     * The resulting hash code will be case insensitive.
     * <p>
     * This method assumes that {@code bytes} is equivalent to a {@code byte[]} but just using {@link CharSequence}
     * for storage. The upper most byte of each {@code char} from {@code bytes} is ignored.
     * @param bytes The array which contains the data to hash (assumed to be equivalent to a {@code byte[]}).
     * @return The hash code of {@code bytes} assuming ASCII character encoding.
     * The resulting hash code will be case insensitive.
     */
    public static int hashCodeAscii(CharSequence bytes) {
        int hash = HASH_CODE_ASCII_SEED;
        final int remainingBytes = bytes.length() & 7;
        // Benchmarking shows that by just naively looping for inputs 8~31 bytes long we incur a relatively large
        // performance penalty (only achieve about 60% performance of loop which iterates over each char). So because
        // of this we take special provisions to unroll the looping for these conditions.
        switch (bytes.length()) {
            case 31:
            case 30:
            case 29:
            case 28:
            case 27:
            case 26:
            case 25:
            case 24:
                hash = hashCodeAsciiCompute(bytes, bytes.length() - 24,
                        hashCodeAsciiCompute(bytes, bytes.length() - 16,
                          hashCodeAsciiCompute(bytes, bytes.length() - 8, hash)));
                break;
            case 23:
            case 22:
            case 21:
            case 20:
            case 19:
            case 18:
            case 17:
            case 16:
                hash = hashCodeAsciiCompute(bytes, bytes.length() - 16,
                         hashCodeAsciiCompute(bytes, bytes.length() - 8, hash));
                break;
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
                hash = hashCodeAsciiCompute(bytes, bytes.length() - 8, hash);
                break;
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
                break;
            default:
                for (int i = bytes.length() - 8; i >= remainingBytes; i -= 8) {
                    hash = hashCodeAsciiCompute(bytes, i, hash);
                }
                break;
        }
        switch(remainingBytes) {
            case 7:
                return ((hash * HASH_CODE_C1 + hashCodeAsciiSanitizsByte(bytes.charAt(0)))
                              * HASH_CODE_C2 + hashCodeAsciiSanitizeShort(bytes, 1))
                              * HASH_CODE_C1 + hashCodeAsciiSanitizeInt(bytes, 3);
            case 6:
                return (hash * HASH_CODE_C1 + hashCodeAsciiSanitizeShort(bytes, 0))
                             * HASH_CODE_C2 + hashCodeAsciiSanitizeInt(bytes, 2);
            case 5:
                return (hash * HASH_CODE_C1 + hashCodeAsciiSanitizsByte(bytes.charAt(0)))
                             * HASH_CODE_C2 + hashCodeAsciiSanitizeInt(bytes, 1);
            case 4:
                return hash * HASH_CODE_C1 + hashCodeAsciiSanitizeInt(bytes, 0);
            case 3:
                return (hash * HASH_CODE_C1 + hashCodeAsciiSanitizsByte(bytes.charAt(0)))
                             * HASH_CODE_C2 + hashCodeAsciiSanitizeShort(bytes, 1);
            case 2:
                return hash * HASH_CODE_C1 + hashCodeAsciiSanitizeShort(bytes, 0);
            case 1:
                return hash * HASH_CODE_C1 + hashCodeAsciiSanitizsByte(bytes.charAt(0));
            default:
                return hash;
        }
    }

    /**
     * Create a new optimized {@link AtomicReferenceFieldUpdater} or {@code null} if it
     * could not be created. Because of this the caller need to check for {@code null} and if {@code null} is returned
     * use {@link AtomicReferenceFieldUpdater#newUpdater(Class, Class, String)} as fallback.
     */
    public static <U, W> AtomicReferenceFieldUpdater<U, W> newAtomicReferenceFieldUpdater(
            Class<? super U> tclass, String fieldName) {
        return null;
    }

    /**
     * Create a new optimized {@link AtomicIntegerFieldUpdater} or {@code null} if it
     * could not be created. Because of this the caller need to check for {@code null} and if {@code null} is returned
     * use {@link AtomicIntegerFieldUpdater#newUpdater(Class, String)} as fallback.
     */
    public static <T> AtomicIntegerFieldUpdater<T> newAtomicIntegerFieldUpdater(
            Class<? super T> tclass, String fieldName) {
        return null;
    }

    /**
     * Create a new optimized {@link AtomicLongFieldUpdater} or {@code null} if it
     * could not be created. Because of this the caller need to check for {@code null} and if {@code null} is returned
     * use {@link AtomicLongFieldUpdater#newUpdater(Class, String)} as fallback.
     */
    public static <T> AtomicLongFieldUpdater<T> newAtomicLongFieldUpdater(
            Class<? super T> tclass, String fieldName) {
        return null;
    }

    /**
     * Return the {@link ClassLoader} for the given {@link Class}.
     */
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        return PlatformDependent0.getClassLoader(clazz);
    }

    /**
     * Return the context {@link ClassLoader} for the current {@link Thread}.
     */
    public static ClassLoader getContextClassLoader() {
        return PlatformDependent0.getContextClassLoader();
    }

    /**
     * Return the system {@link ClassLoader}.
     */
    public static ClassLoader getSystemClassLoader() {
        return PlatformDependent0.getSystemClassLoader();
    }

    private static boolean isRoot0() {
        if (isWindows()) {
            return false;
        }

        String[] ID_COMMANDS = { "/usr/bin/id", "/bin/id", "/usr/xpg4/bin/id", "id"};
        Pattern UID_PATTERN = Pattern.compile("^(?:0|[1-9][0-9]*)$");
        for (String idCmd: ID_COMMANDS) {
            Process p = null;
            BufferedReader in = null;
            String uid = null;
            try {
                p = Runtime.getRuntime().exec(new String[] { idCmd, "-u" });
                in = new BufferedReader(new InputStreamReader(p.getInputStream(), CharsetUtil.US_ASCII));
                uid = in.readLine();
                in.close();

                for (;;) {
                    try {
                        int exitCode = p.waitFor();
                        if (exitCode != 0) {
                            uid = null;
                        }
                        break;
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            } catch (Throwable ignored) {
                // Failed to run the command.
                uid = null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                if (p != null) {
                    try {
                        p.destroy();
                    } catch (Exception e) {
                        // Android sometimes triggers an ErrnoException.
                    }
                }
            }

            if (uid != null && UID_PATTERN.matcher(uid).matches()) {
                return "0".equals(uid);
            }
        }


        Pattern PERMISSION_DENIED = Pattern.compile(".*(?:denied|not.*permitted).*");
        for (int i = 1023; i > 0; i --) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket();
                ss.setReuseAddress(true);
                ss.bind(new InetSocketAddress(i));
                return true;
            } catch (Exception e) {
                // Failed to bind.
                // Check the error message so that we don't always need to bind 1023 times.
                String message = e.getMessage();
                if (message == null) {
                    message = "";
                }
                message = message.toLowerCase();
                if (PERMISSION_DENIED.matcher(message).matches()) {
                    break;
                }
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (Exception e) {
                        // Ignore.
                    }
                }
            }
        }
        return false;
    }

    private static boolean equalsSafe(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        final int end = startPos1 + length;
        for (int i = startPos1, j = startPos2; i < end; ++i, ++j) {
            if (bytes1[i] != bytes2[j]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Package private for testing purposes only!
     */
    private static int hashCodeAsciiSafe(byte[] bytes, int startPos, int length) {
        int hash = HASH_CODE_ASCII_SEED;
        final int remainingBytes = length & 7;
        final int end = startPos + remainingBytes;
        for (int i = startPos - 8 + length; i >= end; i -= 8) {
            hash = PlatformDependent0.hashCodeAsciiCompute(getLongSafe(bytes, i), hash);
        }
        switch(remainingBytes) {
        case 7:
            return ((hash * HASH_CODE_C1 + hashCodeAsciiSanitize(bytes[startPos]))
                          * HASH_CODE_C2 + hashCodeAsciiSanitize(getShortSafe(bytes, startPos + 1)))
                          * HASH_CODE_C1 + hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 3));
        case 6:
            return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(getShortSafe(bytes, startPos)))
                         * HASH_CODE_C2 + hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 2));
        case 5:
            return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(bytes[startPos]))
                         * HASH_CODE_C2 + hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 1));
        case 4:
            return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(getIntSafe(bytes, startPos));
        case 3:
            return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(bytes[startPos]))
                         * HASH_CODE_C2 + hashCodeAsciiSanitize(getShortSafe(bytes, startPos + 1));
        case 2:
            return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(getShortSafe(bytes, startPos));
        case 1:
            return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(bytes[startPos]);
        default:
            return hash;
        }
    }

    private PlatformDependent() {
        // only static method supported
    }
}
