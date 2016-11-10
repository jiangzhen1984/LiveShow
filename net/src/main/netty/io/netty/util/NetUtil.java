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
package io.netty.util;

import android.util.Log;

import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A class that holds a number of network-related constants.
 * <p/>
 * This class borrowed some of its methods from a  modified fork of the
 * <a href="http://svn.apache.org/repos/asf/harmony/enhanced/java/branches/java6/classlib/modules/luni/
 * src/main/java/org/apache/harmony/luni/util/Inet6Util.java">Inet6Util class</a> which was part of Apache Harmony.
 */
public final class NetUtil {

    /**
     * The {@link Inet4Address} that represents the IPv4 loopback address '127.0.0.1'
     */
    public static final Inet4Address LOCALHOST4;

    static {
        byte[] LOCALHOST4_BYTES = {127, 0, 0, 1};

        // Create IPv4 loopback address.
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address) InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
        } catch (Exception e) {
            // We should not get here as long as the length of the address is correct.
            PlatformDependent.throwException(e);
        }
        LOCALHOST4 = localhost4;

        // Retrieve the list of available network interfaces.
        List<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
        try {
            for (Enumeration<NetworkInterface> i = NetworkInterface.getNetworkInterfaces(); i.hasMoreElements();) {
                NetworkInterface iface = i.nextElement();
                // Use the interface with proper INET addresses only.
                if (iface.getInetAddresses().hasMoreElements()) {
                    ifaces.add(iface);
                }
            }
        } catch (SocketException e) {
            Log.w("NetUtil","Failed to retrieve the list of available network interfaces", e);
        }

        // Find the first loopback interface available from its INET address (127.0.0.1 or ::1)
        // Note that we do not use NetworkInterface.isLoopback() in the first place because it takes long time
        // on a certain environment. (e.g. Windows with -Djava.net.preferIPv4Stack=true)
        NetworkInterface loopbackIface = null;
        loop: for (NetworkInterface iface: ifaces) {
            for (Enumeration<InetAddress> i = iface.getInetAddresses(); i.hasMoreElements();) {
                InetAddress addr = i.nextElement();
                if (addr.isLoopbackAddress()) {
                    // Found
                    loopbackIface = iface;
                    break loop;
                }
            }
        }

        // If failed to find the loopback interface from its INET address, fall back to isLoopback().
        if (loopbackIface == null) {
            try {
                for (NetworkInterface iface: ifaces) {
                    if (iface.isLoopback()) {
                        Enumeration<InetAddress> i = iface.getInetAddresses();
                        if (i.hasMoreElements()) {
                            // Found the one with INET address.
                            loopbackIface = iface;
                            break;
                        }
                    }
                }

                if (loopbackIface == null) {
                    Log.w("NetUtil","Failed to find the loopback interface");
                }
            } catch (SocketException e) {
                Log.w("NetUtil","Failed to find the loopback interface", e);
            }
        }
    }

    /**
     * A constructor to stop this class being constructed.
     */
    private NetUtil() {
        // Unused
    }
}
