package com.assignment2.appleguy;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	//The sensor object
	Sensor accelerometer;
	float sensorX = 0;
	float sensorY = 0;
	final int SENSOR_SENSITIVITY = 3;
	
	//The sensor manager
	SensorManager sm;
	
	//The guy object
	TextView guy;
	
	//Screen attibutes
	final int SCREEN_WIDTH = 854;
	final int SCREEN_HEIGHT = 480;
	
	//Game attributes
	boolean runGame = true; 
	int curPosX = 0;
	int curPosY = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set up the sensor
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//Set up the listener
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//Set up the guy
		guy = (TextView) findViewById(R.id.str_guy);
		
		//Initial position
		curPosX = guy.getLeft();
		curPosY = guy.getTop();
		
		//Starts running the game loop
		new Thread() {
		    public void run() {
		    	while (runGame) {
		    		try {
						Log.i("Thread", "Current X = " + curPosX);
						Log.i("Thread", "Current Y = " + curPosY);
		    			
						sleep(500);
					} catch (InterruptedException e) {
						
					}
		    	}
		    }
		}.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//Gathers sensor data
		sensorX = event.values[0];
		sensorY = event.values[1];
	}
}
