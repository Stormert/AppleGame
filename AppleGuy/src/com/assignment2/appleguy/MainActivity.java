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
	TextView healthStr;
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
	final float SPEED = 1;
	int invisible = 0;
	
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
		healthStr = (TextView) findViewById(R.id.str_health);
		scoreStr.setText(getResources().getString(R.string.apple_score) + " 0");
		highscoreStr.setText(getResources().getString(R.string.apple_highscore) + " 0");
		healthStr.setText(getResources().getString(R.string.apple_health) + " 0");
		
		//...The apple object
		apple = new TextureView(this);
		apple.setTextureColor(Color.RED);
		apple.setRandomPosition(screenW, screenH);
		apple.setActive(true);
		((ViewGroup) scoreStr.getParent()).addView(apple);
		
		//...The guy object
		guy = new TextureView(this);
		final int centreX = screenW / 2;
		final int centreY = screenH / 2;
		guy.setPosition(centreX, centreY);
		guy.setActive(true);
		((ViewGroup) scoreStr.getParent()).addView(guy);
		
		//...The empty list of rotten apples
		rotten = new ArrayList<TextureView>();
		
		//Make sure the text is at the top
		scoreStr.bringToFront();
		highscoreStr.bringToFront();
		healthStr.bringToFront();
		
		//Starts running the game loop
		new Thread() {
		    public void run() {
		    	while (runGame) {
		    		try {
		    			//Calculate position change
		    			int x = (int)(sensorX * SPEED);
		    			int y = (int)(sensorY * SPEED);
		    			
		    			//Preform the change
		    			guy.addPosition(x, y, screenW, screenH);
		    			
		    			//Check if the apple is hit
		    			if (guy.isColliding(apple)) {
		    				//Deactivate for now (don't alow new pickups until moved)
		    				apple.setActive(false);
		    				
		    				//Move until not touching
		    		        while (apple.isColliding(guy)) {
		    		        	apple.setRandomPosition(screenW, screenH);
		    		        }
		    		        
		    		        //Apple in a new position, it is ok to unlock it now
		    		        apple.setActive(true);
		    		        
		    		        //For each 5th apple, grant a health point
		    		        if ((apple.getScore() % 5) == 0) {
		    		        	invisible++;
		    		        }
		    				
		    		        //Update the gui and add new rotten apple
		    		        runOnUiThread(new Runnable() {
		    		            @Override
		    		            public void run() {
	    		            		UpdateGUI();
		    		            }
		    		        });
		    			}
		    			
	    		        runOnUiThread(new Runnable() {
	    		            @Override
	    		            public void run() {
	    	    		        boolean failed = false;
	    	    		        
	    	    				for (TextureView rottenApple : rotten) {
	    	    					//Check all rotten apples for collision
	    	    					if (guy.isColliding(rottenApple)) {
	    	    						
	    	    						//Check if you can spend a life
	    	    						if (invisible > 0) {
	    	    							RemoveRottenApple(rottenApple);
	    	    							invisible--;
	    	    							healthStr.setText(getResources().getString(R.string.apple_health) +  " " + invisible);
	    	    						}
	    	    						else {
	    	    							//You lose
	    	    							failed = true;
	    	    						}
	    	    					}
	    	    				}
	    	    				if (failed) {
	    	    					//Reset data and gui
	    	    					ResetGUI();
	    	    				}
	    		            }
	    		        });

	    		        //Delay the loop to run for 10ms (preformance enhancement)
						sleep(10);
					} catch (InterruptedException e) {
						
					}
		    	}
		    }
		}.start();
	}
	
	public void RemoveRottenApple(TextureView rottenApple) {
		//Can't delete, hide and deactivate insteath
		rottenApple.setAlpha(0);
		rottenApple.setActive(false);
	}
	
	public void UpdateGUI() {
		//Update text statistics
		int score = apple.getScore();
		int highscore = apple.getHighscore();
		scoreStr.setText(getResources().getString(R.string.apple_score) + " " + score);
		highscoreStr.setText(getResources().getString(R.string.apple_highscore) + " " + highscore);
		healthStr.setText(getResources().getString(R.string.apple_health) +  " " + invisible);
		
		//Add new rotten apple
		TextureView newRotten = new TextureView(this);
		newRotten.setTextureColor(Color.GRAY);
		newRotten.setRandomPosition(screenW, screenH);
		
		//Place randomly, until it's not ontop the player
        while (newRotten.isColliding(guy)) {
        	newRotten.setRandomPosition(screenW, screenH);
        }
        
        //If you collide in it, it's you're fault
        newRotten.setActive(true);
		((ViewGroup) scoreStr.getParent()).addView(newRotten);
		rotten.add(newRotten);
		
		//Bring the text ontop of all other elements
		scoreStr.bringToFront();
		highscoreStr.bringToFront();
		healthStr.bringToFront();
	}
	
	public void ResetGUI() {
		//Reset the points
		apple.reset();
		
		//Reset the text statistics
		scoreStr.setText(getResources().getString(R.string.apple_score) + " 0");
		healthStr.setText(getResources().getString(R.string.apple_health) +  " 0");
		
		//Reset positions
		guy.setPosition(centreX, centreY);
		guy.setActive(true);
		apple.setRandomPosition(screenW, screenH);
		apple.setActive(true);
		
		//Disable all old rotten apples
		for (TextureView rottenApple : rotten) {
			RemoveRottenApple(rottenApple);
		}
		
		//Clear the list
		rotten.clear();
		
		//Reset bonus health
		invisible = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//This adds items to the action bar if it is present.
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

		//Set the global data
		sensorX = x;
		sensorY = y;
	}
}
