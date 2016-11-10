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
package io.netty.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Creates a new {@link ByteBuf} by allocating new space or by wrapping
 * or copying existing byte arrays, byte buffers and a string.
 *
 * <h3>Use static import</h3>
 * This classes is intended to be used with Java 5 static import statement:
 *
 * <pre>
 * import static io.netty.buffer.{@link Unpooled}.*;
 *
 * {@link ByteBuf} heapBuffer    = buffer(128);
 * {@link ByteBuf} directBuffer  = directBuffer(256);
 * {@link ByteBuf} wrappedBuffer = wrappedBuffer(new byte[128], new byte[256]);
 * {@link ByteBuf} copiedBuffe r = copiedBuffer({@link ByteBuffer}.allocate(128));
 * </pre>
 *
 * <h3>Allocating a new buffer</h3>
 *
 * Three buffer types are provided out of the box.
 *
 * <ul>
 * <li>{@link #buffer(int)} allocates a new fixed-capacity heap buffer.</li>
 * <li>{@link #directBuffer(int)} allocates a new fixed-capacity direct buffer.</li>
 * </ul>
 *
 * <h3>Creating a wrapped buffer</h3>
 *
 * Wrapped buffer is a buffer which is a view of one or more existing
 * byte arrays and byte buffers.  Any changes in the content of the original
 * array or buffer will be visible in the wrapped buffer.  Various wrapper
 * methods are provided and their name is all {@code wrappedBuffer()}.
 * You might want to take a look at the methods that accept varargs closely if
 * you want to create a buffer which is composed of more than one array to
 * reduce the number of memory copy.
 *
 * <h3>Creating a copied buffer</h3>
 *
 * Copied buffer is a deep copy of one or more existing byte arrays, byte
 * buffers or a string.  Unlike a wrapped buffer, there's no shared data
 * between the original data and the copied buffer.  Various copy methods are
 * provided and their name is all {@code copiedBuffer()}.  It is also convenient
 * to use this operation to merge multiple buffers into one buffer.
 */
public final class Unpooled {

    private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;

    /**
     * Big endian byte order.
     */
    public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;

    /**
     * Little endian byte order.
     */
    public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;

    /**
     * A buffer whose capacity is {@code 0}.
     */
    public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);

    static {
        assert EMPTY_BUFFER instanceof EmptyByteBuf: "EMPTY_BUFFER must be an EmptyByteBuf.";
    }

    /**
     * Creates a new big-endian Java heap buffer with reasonably small initial capacity, which
     * expands its capacity boundlessly on demand.
     */
    public static ByteBuf buffer() {
        return ALLOC.heapBuffer();
    }

    /**
     * Creates a new big-endian Java heap buffer with the specified {@code capacity}, which
     * expands its capacity boundlessly on demand.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0}.
     */
    public static ByteBuf buffer(int initialCapacity) {
        return ALLOC.heapBuffer(initialCapacity);
    }

    /**
     * Creates a new big-endian direct buffer with the specified {@code capacity}, which
     * expands its capacity boundlessly on demand.  The new buffer's {@code readerIndex} and
     * {@code writerIndex} are {@code 0}.
     */
    public static ByteBuf directBuffer(int initialCapacity) {
        return ALLOC.directBuffer(initialCapacity);
    }

    /**
     * Creates a new big-endian Java heap buffer with the specified
     * {@code initialCapacity}, that may grow up to {@code maxCapacity}
     * The new buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0}.
     */
    public static ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return ALLOC.heapBuffer(initialCapacity, maxCapacity);
    }

    /**
     * Creates a new big-endian buffer which wraps the specified {@code array}.
     * A modification on the specified array's content will be visible to the
     * returned buffer.
     */
    public static ByteBuf wrappedBuffer(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BUFFER;
        }
        return new UnpooledHeapByteBuf(ALLOC, array, array.length);
    }

    /**
     * Creates a new big-endian buffer which wraps the sub-region of the
     * specified {@code array}.  A modification on the specified array's
     * content will be visible to the returned buffer.
     */
    public static ByteBuf wrappedBuffer(byte[] array, int offset, int length) {
        if (length == 0) {
            return EMPTY_BUFFER;
        }

        if (offset == 0 && length == array.length) {
            return wrappedBuffer(array);
        }

        return wrappedBuffer(array).slice(offset, length);
    }

    /**
     * Creates a new buffer which wraps the specified NIO buffer's current
     * slice.  A modification on the specified buffer's content will be
     * visible to the returned buffer.
     */
    public static ByteBuf wrappedBuffer(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return EMPTY_BUFFER;
        }
        if (!buffer.isDirect() && buffer.hasArray()) {
            return wrappedBuffer(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining()).order(buffer.order());
        } else {
            if (buffer.isReadOnly()) {
                return new ReadOnlyByteBufferBuf(ALLOC, buffer);
            }  else {
                return new UnpooledDirectByteBuf(ALLOC, buffer, buffer.remaining());
            }
        }
    }

    /**
     * Creates a new buffer which wraps the specified buffer's readable bytes.
     * A modification on the specified buffer's content will be visible to the
     * returned buffer.
     * @param buffer The buffer to wrap. Reference count ownership of this variable is transfered to this method.
     * @return The readable portion of the {@code buffer}, or an empty buffer if there is no readable portion.
     * The caller is responsible for releasing this buffer.
     */
    public static ByteBuf wrappedBuffer(ByteBuf buffer) {
        if (buffer.isReadable()) {
            return buffer.slice();
        } else {
            buffer.release();
            return EMPTY_BUFFER;
        }
    }

    /**
     * Creates a new big-endian composite buffer which wraps the readable bytes of the
     * specified buffers without copying them.  A modification on the content
     * of the specified buffers will be visible to the returned buffer.
     * @param buffers The buffers to wrap. Reference count ownership of all variables is transfered to this method.
     * @return The readable portion of the {@code buffers}. The caller is responsible for releasing this buffer.
     */
    public static ByteBuf wrappedBuffer(ByteBuf... buffers) {
        return wrappedBuffer(AbstractByteBufAllocator.DEFAULT_MAX_COMPONENTS, buffers);
    }

    /**
     * Creates a new big-endian composite buffer which wraps the readable bytes of the
     * specified buffers without copying them.  A modification on the content
     * of the specified buffers will be visible to the returned buffer.
     * @param maxNumComponents Advisement as to how many independent buffers are allowed to exist before
     * consolidation occurs.
     * @param buffers The buffers to wrap. Reference count ownership of all variables is transfered to this method.
     * @return The readable portion of the {@code buffers}. The caller is responsible for releasing this buffer.
     */
    public static ByteBuf wrappedBuffer(int maxNumComponents, ByteBuf... buffers) {
        switch (buffers.length) {
        case 0:
            break;
        case 1:
            ByteBuf buffer = buffers[0];
            if (buffer.isReadable()) {
                return wrappedBuffer(buffer.order(BIG_ENDIAN));
            } else {
                buffer.release();
            }
            break;
        default:
            for (int i = 0; i < buffers.length; i++) {
                ByteBuf buf = buffers[i];
                if (buf.isReadable()) {
                    return new CompositeByteBuf(ALLOC, false, maxNumComponents, buffers, i, buffers.length);
                }
                buf.release();
            }
            break;
        }
        return EMPTY_BUFFER;
    }

    /**
     * Creates a read-only buffer which disallows any modification operations
     * on the specified {@code buffer}.  The new buffer has the same
     * {@code readerIndex} and {@code writerIndex} with the specified
     * {@code buffer}.
     *
     * @deprecated Use {@link ByteBuf#asReadOnly()}.
     */
    @Deprecated
    public static ByteBuf unmodifiableBuffer(ByteBuf buffer) {
        ByteOrder endianness = buffer.order();
        if (endianness == BIG_ENDIAN) {
            return new ReadOnlyByteBuf(buffer);
        }

        return new ReadOnlyByteBuf(buffer.order(BIG_ENDIAN)).order(LITTLE_ENDIAN);
    }

    private Unpooled() {
        // Unused
    }
}
