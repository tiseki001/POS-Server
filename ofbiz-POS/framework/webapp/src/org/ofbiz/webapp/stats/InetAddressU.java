package org.ofbiz.webapp.stats;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InetAddressU {
	
	
	public static InetAddress getLocalHost() throws SocketException {
        Enumeration<NetworkInterface> netInterfaces = null;
        
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (ip.getHostAddress()!=null&&ip.getHostAddress().equals("127.0.0.1")) {
                        return ip;
                    }
                }
            }
         
        return null;
    }

}
