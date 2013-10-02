package com.assignment2.appleguy;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class TextureView extends View {

	private int radius = 32;
	private int posX = 0;
	private int posY = 0;
	private Paint paint;
	private int score = -1;
	private int highscore = -1;
	private boolean active = false;
	private Bitmap image;
	private int direction = 0;
	private SharedPreferences preferences;
	
	public TextureView(Context context) {
		super(context);
		
		preferences = context.getSharedPreferences("Highscore", 0);
		highscore = preferences.getInt("highscore", -1);
		
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
	}
	
	public void setTextureColor(int col) {
		paint.setColor(col);
	}
	
	public void setDirection(int dir) {
		direction = dir;
	}
	
	public void addPosition(float x, float y, int screenW, int screenH) {
		posX += x;
		posY += y;
		
		//Check for collision with outer bounderies
		if (posX < 0) {
			posX = 0;
		}
		else if (posX + radius*2 > screenW) {
			posX = screenW - radius*2;
		}
		
		if (posY < 0) {
			posY = 0;
		}
		else if (posY + radius*2 > screenH) {
			posY = screenH - radius*2;
		}
		
		//Force to draw the object again
		postInvalidate();
	}
	
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
		
		//Force to draw the object again
		postInvalidate();
	}
	
	public void setImage(int resource) {
		image = BitmapFactory.decodeResource(getResources(), resource);
		radius = (int)(image.getWidth() / 2);
	}
	
	public void setActive(boolean value) {
		active = value;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public int getTextureRadius() {
		return radius;
	}
	
	public int getTextureX() {
		return posX;
	}
	
	public int getTextureY() {
		return posY;
	}
	
    @Override
    public void onDraw(final Canvas canvas) {
    	//Draw image
    	canvas.save(Canvas.MATRIX_SAVE_FLAG);
    	canvas.rotate(direction, posX + radius, posY + radius);
        canvas.drawBitmap(image, posX, posY, paint);
        canvas.restore();
    }
    
    public boolean isColliding(TextureView obj) {
    	boolean hit = false;
    	
    	//Gather objectdata
    	int x1 = posX;
    	int y1 = posY;
    	int r1 = radius;
    	int x2 = obj.getTextureX();
    	int y2 = obj.getTextureY();
    	int r2 = obj.getTextureRadius();
    	
    	//Circle hitbox calculations
    	int distX = x2 - x1;
    	int expX = getExponantial(distX, 2);
    	int distY = y1 - y2;
    	int expY = getExponantial(distY, 2);
    	int sizeR = r1 + r2;
    	int expR = getExponantial(sizeR, 2);
    	
    	//Circle collision detection
    	if (expX + expY <= expR) {
    		if (obj.getActive()) {
    			hit = true;
    		}
    	}
    	
    	return hit;
    }
    
    public void setRandomPosition(int screenW, int screenH) {
		Random randomizer = new Random();
		int randX = randomizer.nextInt(screenW);
		int randY = randomizer.nextInt(screenH);

		//Check for collision with outer bounderies
		if (randX < 0) {
			randX = 0;
		}
		else if (randX + radius*2 > screenW) {
			randX = screenW - radius*2;
		}
		
		if (randY < 0) {
			randY = 0;
		}
		else if (randY + radius*2 > screenH) {
			randY = screenH - radius*2;
		}
		
		//Set the new position
		setPosition(randX, randY);
		
		//Update score
		score += 1;
		if (score > highscore) {
			highscore = score;
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("highscore", score);
			editor.commit();
		}
    }
    
    public int getScore() {
    	return score;
    }
    
    public int getHighscore() {
    	return highscore;
    }
    
    public void reset() {
    	score = -1;
    }
    
    private int getExponantial(int core, int exp) {
    	//Returns core^exp (ex: 3^2 = 9)
    	int result = 1;
    	int exp_left = exp;
    	
    	while (exp_left > 0) {
    		result *= core;
    		exp_left--;
    	}
    	
    	return result;
    }
}