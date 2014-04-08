package com.game.vcontrolapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ControllerActivity extends Activity {
	
	private String mServerAddress;
	private String mHost;
	private NetworkCommunicator mNetworkCommunicator;
	
	private String mConnectionType;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        Intent intent = getIntent();
        mServerAddress = intent.getStringExtra("com.game.vcontrolapp.server");
        mConnectionType = intent.getStringExtra("com.game.vcontrolapp.connectionType");
        if(mConnectionType.equals("wifi"))
        	mNetworkCommunicator = new WifiCommunicator();
        
        mHost = mNetworkCommunicator.connect(mServerAddress);
        if(mHost == null)
        	finish();
        
        mNetworkCommunicator.send("test");
    }
	
	@Override
	protected void onPause() {
		mNetworkCommunicator.disconnect();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mNetworkCommunicator.reconnect();
	}
}
