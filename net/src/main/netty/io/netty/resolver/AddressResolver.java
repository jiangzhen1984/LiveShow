package io.netty.resolver;

import io.netty.util.concurrent.Future;
import java.io.Closeable;
import java.net.SocketAddress;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.List;

/**
 * Resolves a possibility unresolved {@link SocketAddress}.
 */
public interface AddressResolver<T extends SocketAddress> extends Closeable {

  /**
   * Returns {@code true} if and only if the specified address is supported by this resolved.
   */
  boolean isSupported(SocketAddress address);

  /**
   * Returns {@code true} if and only if the specified address has been resolved.
   *
   * @throws UnsupportedAddressTypeException if the specified address is not supported by this resolver
   */
  boolean isResolved(SocketAddress address);

  /**
   * Resolves the specified address. If the specified address is resolved already, this method does nothing
   * but returning the original address.
   *
   * @param address the address to resolve
   *
   * @return the {@link SocketAddress} as the result of the resolution
   */
  Future<T> resolve(SocketAddress address);

  /**
   * Resolves the specified address. If the specified address is resolved already, this method does nothing
   * but returning the original address.
   *
   * @param address the address to resolve
   *
   * @return the list of the {@link SocketAddress}es as the result of the resolution
   */
  Future<List<T>> resolveAll(SocketAddress address);

  /**
   * Closes all the resources allocated and used by this resolver.
   */
  @Override
  void close();
}