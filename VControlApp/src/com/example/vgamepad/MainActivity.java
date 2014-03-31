package com.example.vgamepad;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

	private Socket socket;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	PrintWriter out;
	
	private float gravity[];
	
    private static final int SERVERPORT = 27015;
    private static String SERVER_IP = "192.168.0.100";
    
    private static int aPress = 0;
    private static int dPress = 0;
    
    //int pressLimit = 1;
    
    private byte x = 0;
    private byte y = 0;
    private byte prevY = 0;
    private byte z = 0;
    
    private ArrayList<String> mPressedKeys;
    Boolean useAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useAccelerometer = false;
        
        mPressedKeys = new ArrayList<String>();
        EditText et = (EditText) findViewById(R.id.EditText01);
        et.setText(SERVER_IP);
        
        Button bLeft = (Button) findViewById(R.id.leftButton);        
        Button bRight = (Button) findViewById(R.id.RightButton);
        
        bLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
					if(!mPressedKeys.contains("Left"))
		    		{
		    			mPressedKeys.add("Left");	  
		    		}
                    return true;
                }
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(mPressedKeys.contains("Left"))
		    		{
						String str = "Left Release";
		    			out.println(str);
	    				mPressedKeys.remove(mPressedKeys.indexOf("Left"));
		    		}
					return true;
				}
				return false;
			}
		});
        
        bRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
					if(!mPressedKeys.contains("Right"))
		    		{
		    			mPressedKeys.add("Right");	  
		    		}
                    return true;
                }
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(mPressedKeys.contains("Right"))
		    		{
						String str = "Right Release";
		    			out.println(str);
	    				mPressedKeys.remove(mPressedKeys.indexOf("Right"));
		    		}
					return true;
				}
				return false;
			}
		});
        
        Thread checkPressedKeysThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true)
				{
					if(out != null)
					{
						for(int i = 0; i < mPressedKeys.size(); i++)
						{
							String str = mPressedKeys.get(i) + " Press";
			    			out.println(str);
						}
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});

        checkPressedKeysThread.start();
        
        if(useAccelerometer)
        {
	        // Initializing the gravity vector to zero.
	        gravity = new float[3];
	        gravity[0] = 0;
	        gravity[1] = 0;
	        gravity[2] = 0;
	        
	
	        // Initializing the accelerometer stuff
	        // Register this as SensorEventListener
	        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   
	        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onClick(View view) {
        EditText et = (EditText) findViewById(R.id.EditText01);
        String str = et.getText().toString();
        
        if(str.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}"))
        	SERVER_IP = str;

        new Thread(new ClientThread()).start();
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
	    if(useAccelerometer)
	    	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop()
    {
    	if(useAccelerometer)
    		mSensorManager.unregisterListener(this);
	    super.onStop();
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// This function is called repeatedly. The tempo is set when the listener is register
		// see onCreate() method.

		// Lowpass filter the gravity vector so that sudden movements are filtered. 
	    float alpha = (float) 0.8;
	    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
	    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
	    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
 
	    // Normalize the gravity vector and rescale it so that every component fits one byte.
	    float size=(float) Math.sqrt(Math.pow(gravity[0], 2)+Math.pow(gravity[1], 2)+Math.pow(gravity[2], 2));
	    x = (byte) (128*gravity[0]/size);
	    prevY = y;
	    y = (byte) (128*gravity[1]/size);
	    z = (byte) (128*gravity[2]/size);
	    
	    // Update the GUI
	    /*TextView xView = (TextView) findViewById(R.id.x);
		TextView yView = (TextView) findViewById(R.id.y);
		TextView zView = (TextView) findViewById(R.id.z);
	    xView.setText(Integer.toString(x));
	    yView.setText(Integer.toString(y));
	    zView.setText(Integer.toString(z));*/
	    
    	String str = "";
    	if(socket != null)
    	{
    		Boolean result = sendKeyList(15,  25, 1, 0);
    		if(!result)
    			result = sendKeyList(25,  35, 2, 1);
    		
    		if(!result)
    			result = sendKeyList(35,  50, 3, 2);
    		
    		if(!result)
    			result = sendKeyList(50,  80, 5, 3);
    		
    		if(!result)
    			result = sendKeyList(80,  128, 5, 3);
    		
    		if(!result)
	    	{
	    		if(mPressedKeys.contains("Left"))
    			{
    				str = "Left Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Left"));
    			}
	    		if(mPressedKeys.contains("Right"))
    			{
    				str = "Right Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Right"));
    			}
	    	}
    	}
	}

    public Boolean sendKeyList(int min, int max, int pressLimit, int tolerance)
    {
    	String str = "";
    	if(y < -min && y >= -max)
    	{
    		if(!mPressedKeys.contains("Left"))
    		{
    			if(mPressedKeys.contains("Right"))
    			{
    				str = "Right Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Right"));
    				dPress = 0;
    			}	    
    			if(prevY >= y - tolerance)
    			{
	    			mPressedKeys.add("Left");	  

		    		str = "Left Press";
	    			out.println(str);  
	    			aPress++;
    			}
    		}
    		else
    		{
    			if(prevY >= y - tolerance)
    			{
	    			if(aPress >= pressLimit)
	    			{
	    				aPress = 0;
	    				str = "Left Release";
		    			out.println(str);
	    			}
	    			else
	    			{
	    				str = "Left Press";
		    			out.println(str);  
		    			aPress++;
	    			}
    			}
    			else
    			{
    				str = "Left Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Left"));
    				aPress = 0;
    			}
    		}
    		return true;
    	}
    	else if(y > min && y <= max)
    	{
    		if(!mPressedKeys.contains("Right"))
    		{
    			if(mPressedKeys.contains("Left"))
    			{
    				str = "Left Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Left"));
    				aPress = 0;
    			}	    
    			if(prevY <= y + tolerance)
    			{
	    			mPressedKeys.add("Right");	  

		    		str = "Right Press";
	    			out.println(str);  
	    			dPress++;
    			}
    		}
    		else
    		{
    			if(prevY <= y + tolerance)
    			{
	    			if(dPress >= pressLimit)
	    			{
	    				dPress = 0;
	    				str = "Right Release";
		    			out.println(str);
	    			}
	    			else
	    			{
	    				str = "Right Press";
		    			out.println(str);  
		    			dPress++;
	    			}
    			}	
    			else
    			{
    				str = "Right Release";
	    			out.println(str);
    				mPressedKeys.remove(mPressedKeys.indexOf("Right"));
    				dPress = 0;
    			}
    		}
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
}
