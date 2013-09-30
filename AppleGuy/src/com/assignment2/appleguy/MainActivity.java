package com.assignment2.appleguy;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	//The sensor object
	Sensor accelerometer;
	float sensorX = 0;
	float sensorY = 0;
	final int SENSOR_SENSITIVITY = 3;
	
	//The sensor manager
	SensorManager sm;
	
	//Game objects
	TextureView guy;
	TextureView apple;
	TextView scoreStr;
	TextView highscoreStr;
	ArrayList<TextureView> rotten;
	
	//Screen attributes
	int screenW = 0;
	int screenH = 0;
	int centreX = 0;
	int centreY = 0;
	
	//Game attributes
	boolean runGame = true; 
	int curPosX = 0;
	int curPosY = 0;
	final int SPEED = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Find resolution
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenW = size.x;
		screenH = size.y;
		centreX = screenW / 2;
		centreY = screenH / 2;
		
		//Keep light on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setDimAmount(0);
		
		//Set up the sensor
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//Set up the listener
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//Set up game objects
		scoreStr = (TextView) findViewById(R.id.str_score);
		highscoreStr = (TextView) findViewById(R.id.str_highscore);
		
		apple = new TextureView(this);
		apple.setTextureColor(Color.RED);
		apple.setRandomPosition(screenW, screenH);
		((ViewGroup) scoreStr.getParent()).addView(apple);
		
		guy = new TextureView(this);
		final int centreX = screenW / 2;
		final int centreY = screenH / 2;
		guy.setPosition(centreX, centreY);
		((ViewGroup) scoreStr.getParent()).addView(guy);
		
		rotten = new ArrayList<TextureView>();
		
		//Starts running the game loop
		new Thread() {
		    public void run() {
		    	while (runGame) {
		    		try {
		    			int x = (int)sensorX * SPEED;
		    			int y = (int)sensorY * SPEED;
		    			
		    			guy.addPosition(x, y, screenW, screenH);
		    			if (guy.isColliding(apple)) {
		    				apple.setRandomPosition(screenW, screenH);
		    				
		    		        runOnUiThread(new Runnable() {
		    		            @Override
		    		            public void run() {
	    		            		UpdateGUI();
		    		            }
		    		        });
		    			}
		    			
	    				boolean failed = false;
	    				for (TextureView rottenApple : rotten) {
	    					if (guy.isColliding(rottenApple)) {
	    						failed = true;
	    					}
	    				}
	    				if (failed) {
		    		        runOnUiThread(new Runnable() {
		    		            @Override
		    		            public void run() {
		    		            	ResetGUI();
		    		            }
		    		        });
	    				}

						sleep(10);
					} catch (InterruptedException e) {
						
					}
		    	}
		    }
		}.start();
	}
	
	public void UpdateGUI() {
		int score = apple.getScore();
		int highscore = apple.getHighscore();
		scoreStr.setText("Score: " + score);
		highscoreStr.setText("Highscore: " + highscore);
		
		TextureView newRotten = new TextureView(this);
		newRotten.setTextureColor(Color.GRAY);
		newRotten.setRandomPosition(screenW, screenH);
        while (guy.isColliding(newRotten)) {
        	newRotten.setRandomPosition(screenW, screenH);
        }
		((ViewGroup) scoreStr.getParent()).addView(newRotten);
		rotten.add(newRotten);
		
		scoreStr.bringToFront();
		highscoreStr.bringToFront();
	}
	
	public void ResetGUI() {
		apple.reset();
		scoreStr.setText("Score: 0");
		
		guy.setPosition(centreX, centreY);
		apple.setRandomPosition(screenW, screenH);
		
		for (TextureView rottenApple : rotten) {
			rottenApple.setAlpha(0);
		}
		rotten.clear();
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
		float x = event.values[1]; //Flipped screen
		float y = event.values[0];

		sensorX = x;
		sensorY = y;
	}
}
