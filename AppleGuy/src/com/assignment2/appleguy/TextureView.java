package com.assignment2.appleguy;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class TextureView extends View {

	//private int width;
	//private int height;
	private int radius = 32;
	private int posX = 0;
	private int posY = 0;
	private Paint paint;
	private int score = -1;
	private int highscore = -1;
	private boolean active = false;
	
	public TextureView(Context context) {
		super(context);
		
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
	}
	
	public void setTextureColor(int col) {
		paint.setColor(col);
	}
	
	public void addPosition(float x, float y, int screenW, int screenH) {
		posX += x;
		posY += y;
		
		//Check for collision with outer bounderies
		if (posX - radius < 0) {
			posX = 0 + radius;
		}
		else if (posX + radius > screenW) {
			posX = screenW - radius;
		}
		
		if (posY - radius < 0) {
			posY = 0 + radius;
		}
		else if (posY + radius > screenH) {
			posY = screenH - radius;
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
	
	/*
	public int getTextureW() {
		return width;
	}
	
	public int getTextureH() {
		return height;
	}
	*/
	
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
    	//Draw circle
    	canvas.drawCircle(posX, posY, radius, paint);
    	
    	//Draw image
    	//canvas.blitImage(image) ?
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
		if (randX - radius < 0) {
			randX = 0 + radius;
		}
		else if (randX + radius > screenW) {
			randX = screenW - radius;
		}
		
		if (randY - radius < 0) {
			randY = 0 + radius;
		}
		else if (randY + radius > screenH) {
			randY = screenH - radius;
		}
		
		//Set the new position
		setPosition(randX, randY);
		
		//Update score
		score += 1;
		if (score > highscore) {
			highscore = score;
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