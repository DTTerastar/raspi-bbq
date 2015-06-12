package com.raspi.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by Darrell on 6/7/2015.
 */
public class misc {

    public static String BytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    static public InetAddress GetLocalAddress() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface ni : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            for (InetAddress address : Collections.list(inetAddresses))
                if (!address.isMulticastAddress() && !address.isAnyLocalAddress() && !address.isLinkLocalAddress() && !address.isLoopbackAddress())
                    return address;
        }
        return null;
    }
}

