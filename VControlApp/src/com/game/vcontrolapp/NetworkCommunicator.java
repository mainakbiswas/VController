package com.game.vcontrolapp;
import java.util.List;

public abstract class NetworkCommunicator {
	protected String mCurrentServer;
	
	public abstract List<String> getLocalServers();
	
	public abstract String connect(String server);
	
	public abstract void reconnect();
	
	public abstract void disconnect();
	
	public abstract void send(String data);
	
	public abstract String getBroadcastAddress();

}
