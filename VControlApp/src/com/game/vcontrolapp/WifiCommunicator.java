package com.game.vcontrolapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WifiCommunicator extends NetworkCommunicator {
	
	private Socket mSocket;
	private static final int mPort = 2357;
	private InterfaceAddress mOwnInterfaceAddress;
	private String mCurrentServerName;
	
	private PrintWriter mOut;

	private class StringIPConverter {
		int ip;
		int mask;
		
		public StringIPConverter(String ipString, int netPrefix) {
			String[] ipPartsString = ipString.split("\\.");
			int[] ipParts = new int[4];
			for(int i = 0; i < 4 ; ++i) {
				ipParts[i] = Integer.parseInt(ipPartsString[i]);
			}
			ip = ((ipParts[0] * 256 | ipParts[1]) * 256 | ipParts[2]) * 256 | ipParts[3];
			
			mask = (int) (Math.pow(2, netPrefix + 1) - 1);
			mask = mask << (32 - netPrefix);
		}
		
		public List<String> listIpsInSubnet(){
			List<String> result = new ArrayList<String>();
	        int index;
	        int count = 1;
	        int temp = mask;
	        long a, b, c, d;

	        while ((temp & 1) == 0)
	        {
	            count *= 2;
	            temp >>= 1;
	        }

	        for (index = 1; index < count -1; index++)
	        {
	            long newIP = ((ip & mask) | index) & 0xFFFFFFFFL;


	            d = newIP & 0xFF;
	            c = (newIP / 256) & 0xFF;
	            b = (newIP / 65536) & 0xFF;
	            a = (newIP / 16777216) & 0xFF;

	            result.add("" + a + "." + b + "." + c + "." + d);
	        }
	        
	        return result;
		}
	}
	
	
	@Override
	public List<String> getLocalServers() {
		getOwnInterfaceAddress();
		
		if(mOwnInterfaceAddress == null)
			return new ArrayList<String>();			
		
		StringIPConverter converter = new StringIPConverter(mOwnInterfaceAddress.getAddress().getHostAddress(), 
															mOwnInterfaceAddress.getNetworkPrefixLength());
		
		List<String> servers = new ArrayList<String>();
		for(String ip: converter.listIpsInSubnet()) {
			connect(ip);
			if (mSocket != null) {
				disconnect();
				servers.add(ip);
			}
		}
		return servers;
	}
	
	@Override
	public String getBroadcastAddress() {
		if(mOwnInterfaceAddress == null)
			getOwnInterfaceAddress();
		
		return mOwnInterfaceAddress.getBroadcast().getHostAddress();
	}
	
	private String getHostName(String server) {		
		try {
			InetAddress addr = InetAddress.getByName(server);
			return addr.getHostName();
		} catch (UnknownHostException e) {
		}
		return server;
	}
	

	@Override
	public String connect(final String server) {
		Thread connectThread = new Thread() {			
			@Override
			public void run() {
				try {
					if (mSocket != null)
						mSocket.close();
					mSocket = new Socket();
					mSocket.connect(new InetSocketAddress(server, mPort), 200);
					mOut = new PrintWriter(new BufferedWriter(
	                        new OutputStreamWriter(mSocket.getOutputStream())),
	                        true);
					mCurrentServer = server;
					mCurrentServerName = getHostName(mCurrentServer);
				} catch (UnknownHostException e) {
					mSocket = null;
				} catch (IOException e) {
					mSocket = null;
				}				
			}
		};
		connectThread.start();
		try {
			connectThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mCurrentServerName;
	}
	
	public void disconnect()
	{
		if (mSocket != null)
		{
			try {
				mOut.close();
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send(String data) {
		mOut.println(data);
	}
	
	private void getOwnInterfaceAddress()
	{
		Thread getOwnIpThread = new Thread() {
			@Override
			public void run() {
				try {
		            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		                NetworkInterface intf = (NetworkInterface) en.nextElement();
		                List<InterfaceAddress> intAddresses = intf.getInterfaceAddresses();
		                for(InterfaceAddress intA: intAddresses)
		                {
		                	InetAddress inetAddress = intA.getAddress();
		                	if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
		                         mOwnInterfaceAddress = intA;
		                         return;
		                    }
		                }
		            }
		        } 
		    	catch (SocketException ex) {
		    		mOwnInterfaceAddress = null;
		        }
			}			
		};
		
		getOwnIpThread.start();
		try {
			getOwnIpThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reconnect() {
		if(mSocket == null && mCurrentServer != null)
			connect(mCurrentServer);
	}
}
