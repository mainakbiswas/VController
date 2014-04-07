package com.game.vcontrolapp;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ListActivity{
	
	private Button mWifi;
	private Button mBluetooth;
	private Button mConnect;
	
	private EditText mEnterIP;
	
	private ArrayAdapter<String> mServersAdapter;
	private List<String> mServerList;
	
	private NetworkCommunicator mNetworkCommunicator;
	
	Intent mIntent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mWifi = (Button) findViewById(R.id.Wifi);
        mBluetooth = (Button) findViewById(R.id.Bluetooth);
        mConnect = (Button) findViewById(R.id.Connect);
        if(mConnect != null)
        	mConnect.setEnabled(false);
        
        mEnterIP = (EditText) findViewById(R.id.EnterIp);
        
        mServerList = new ArrayList<String>();
        
        mServersAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mServerList);
        
		mIntent = new Intent(this, ControllerActivity.class);
        
        setListAdapter(mServersAdapter);
        
        if(mWifi != null)
        {
        	mWifi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mNetworkCommunicator = new WifiCommunicator();
					mEnterIP.setText(mNetworkCommunicator.getBroadcastAddress());
					if(mConnect != null)
			        	mConnect.setEnabled(true);
					//mServerList.addAll(mNetworkCommunicator.getLocalServers());
					//addItems();
				}
			});
        }
        
        if(mConnect != null)
        {
    		mConnect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mNetworkCommunicator.connect(mEnterIP.getText().toString());
                	startActivity(mIntent);
				}
			});
        }
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
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if(mNetworkCommunicator != null)
    		mNetworkCommunicator.disconnect();
    }
    
    @Override
    protected void onResume()
    {
	    super.onResume(); 
	    if(mNetworkCommunicator != null)
	    	mNetworkCommunicator.reconnect();
    }
    
    @Override
    protected void onStop()
    {
	    super.onStop();
    }
}
