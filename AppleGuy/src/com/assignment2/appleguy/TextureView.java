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
	
	public TextureView(Context context) {
		super(context);
		
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
	}
	
	public void setTextureColor(int col) {
		paint.setColor(col);
	}
	
	public void addPosition(int x, int y, int screenW, int screenH) {
		posX += x;
		posY += y;
		
		float offset = 2.5f;
		
		if (posX - radius < 0) {
			posX = 0 + radius;
		}
		else if (posX + (radius * offset) > screenW) {
			posX = (int) (screenW - (radius * offset));
		}
		
		if (posY - radius < 0) {
			posY = 0 + radius;
		}
		else if (posY + (radius * offset) > screenH) {
			posY = (int) (screenH - (radius * offset));
		}
		
		postInvalidate();
	}
	
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
		
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
    	canvas.drawCircle(posX, posY, radius, paint);
    }
    
    public boolean isColliding(TextureView obj) {
    	boolean hit = false;
    	int x = obj.getTextureX();
    	int y = obj.getTextureY();
    	int distX = posX - x;
    	int distY = posY - y;
    	float hitbox = 1.5f;
    	
    	if (distX >= (-radius * hitbox) && distX <= (radius * hitbox)) {
    		//Inside x
        	if (distY >= (-radius * hitbox) && distY <= (radius * hitbox)) {
        		//Inside y
        		hit = true;
        	}
    	}
    	
    	return hit;
    }
    
    public void setRandomPosition(int screenW, int screenH) {
		Random randomizer = new Random();
		int randX = randomizer.nextInt(screenW);
		int randY = randomizer.nextInt(screenH);
		float offset = 2.5f;
		
		if (randX - radius < 0) {
			randX = 0 + radius;
		}
		else if (randX + (radius * offset) > screenW) {
			randX = (int) (screenW - (radius * offset));
		}
		
		if (randY - radius < 0) {
			randY = 0 + radius;
		}
		else if (randY + (radius * offset) > screenH) {
			randY = (int) (screenH - (radius * offset));
		}
		
		setPosition(randX, randY);
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
}