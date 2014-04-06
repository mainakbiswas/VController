package com.game.vcontrolapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends ListActivity{

	private Socket socket;
	private Button mWifi;
	private Button mBluetooth;
	private ListView mServers;
	
	private ArrayAdapter<String> mServersAdapter;
	private ArrayList<String> mServerList;
	
	PrintWriter out;
	
    private static final int SERVERPORT = 27015;
    private static String SERVER_IP = "192.168.0.100";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mWifi = (Button) findViewById(R.id.Wifi);
        mBluetooth = (Button) findViewById(R.id.Bluetooth);
        //mServers = (ListView) findViewById(R.id.Servers);
        mServerList = new ArrayList<String>();
        
        mServersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mServerList);
        
        setListAdapter(mServersAdapter);
        
        if(mWifi != null)
        {
        	mWifi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Thread t = new WifiServersThread();
					t.start();
					try {
						t.join();
						addItems();
					} catch (InterruptedException e) {
						addItems(e.toString());
					}
				}
			});
        }
        //addItems();
        //addItems();
    }
    
    private String getOwnIp() {
    	String ip = "";
    	try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                List<InterfaceAddress> intAddresses = intf.getInterfaceAddresses();
                for(int i = 0; i < intAddresses.size(); i++)
                {
                	InetAddress inetAddress = intAddresses.get(i).getAddress();
                	if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                        ip = ip + "/" + intAddresses.get(i).getNetworkPrefixLength();
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
        }
    	return null;
    }
    
    private void showWifiServers()
    {
    	//get own ip and subnet
    	//for all possible hosts in subnet, try to connect and add to list if succeessful	
    	String ownIp = getOwnIp();
    	if (ownIp == null) return;
    	int prefix = Integer.parseInt(ownIp.substring(ownIp.indexOf("/") + 1));
    	
    	mServerList.add(ownIp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void addItems(String s) {
    	mServerList.add(s);
    	mServersAdapter.notifyDataSetChanged();
    }
    
    public void addItems() {
    	mServersAdapter.notifyDataSetChanged();
    }
    
    /*
    public void onClick(View view) {
        EditText et = (EditText) findViewById(R.id.IPAssign);
        String str = et.getText().toString();
        
        if(str.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}"))
        	SERVER_IP = str;

        new Thread(new ClientThread()).start();
    }*/
    
    class WifiServersThread extends Thread {
        @Override
        public void run() {
            showWifiServers();
        }
    }
    
    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
            } catch (UnknownHostException e1) {
                //e1.printStackTrace();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
    		if(socket != null)
    			socket.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }
    
    @Override
    protected void onResume()
    {
	    super.onResume(); 
    }
    
    @Override
    protected void onStop()
    {
	    super.onStop();
    }
}
