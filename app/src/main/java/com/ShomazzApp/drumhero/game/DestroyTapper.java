package com.ShomazzApp.drumhero.game;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class DestroyTapper implements IDrawable, View.OnTouchListener{

	public static final int firstLineX = 510;
	public static final int oldLineWidth = 210;
	private final int bigTapperWidth = 230;
	private final int bigTapperHeight = 220;
	private final int smallTapperWidth = 210;
	private final int smallTapperHeight = 160;
	private int onTouchMargin;
	private int x;
	private int y;
	private int mainY;
	private int numberOfTapper;
	private int width;
	private int height;
	private boolean holded = false;
	private RectF tapperRect;

	public DestroyTapper(int numberOfTapper) {

		this.numberOfTapper = numberOfTapper;

		switch (numberOfTapper) {
			case 1:
				x = getLineX(numberOfTapper) - bigTapperWidth - 10;
				y = DestroyLine.getDestroyLineY(false)
						+ DestroyLine.getDestroyLineHeight(false) + 55;
				width = bigTapperWidth;
				height = bigTapperHeight;
				break;
			case 2:
				x = getLineX(numberOfTapper) - 5;
				y = DestroyLine.getDestroyLineY(false)
						+ DestroyLine.getDestroyLineHeight(false) + smallTapperHeight +
						5;
				width = smallTapperWidth;
				height = smallTapperHeight;
				break;
			case 3:
				x = getLineX(numberOfTapper)
						+ DestroyLine.getDestroyLineWidth(false) + 5;
				y = DestroyLine.getDestroyLineY(false)
						+ DestroyLine.getDestroyLineHeight(false) + smallTapperHeight
						+ 5;
				width = smallTapperWidth;
				height = smallTapperHeight;
				break;
			case 4:
				x = getLineX(numberOfTapper)
						+ DestroyLine.getDestroyLineWidth(false) + bigTapperWidth + 10;
				y = DestroyLine.getDestroyLineY(false)
						+ DestroyLine.getDestroyLineHeight(false) + 55;
				width = bigTapperWidth;
				height = bigTapperHeight;
				break;
		}
		mainY = y;
		onTouchMargin = width / 12;
	}

	@Override
	public void draw(Canvas canvas) {
		tapperRect = new RectF((x - width) / MySurfaceView.sizeWidthCoff, (y - height) / MySurfaceView.sizeHeightCoff,
				(x + width) / MySurfaceView.sizeWidthCoff, (y + height) / MySurfaceView.sizeHeightCoff);
		switch (numberOfTapper) {
			case 1:
				MySurfaceView.drawImage(Game.desTapperRed, tapperRect, canvas);
				break;
			case 2:
				MySurfaceView.drawImage(Game.desTapperGreen, tapperRect, canvas);
				break;
			case 3:
				MySurfaceView.drawImage(Game.desTapperYellow, tapperRect, canvas);
				break;
			case 4:
				MySurfaceView.drawImage(Game.desTapperBlue, tapperRect, canvas);
				break;
		}

	}

	public void onTouchDown() {
		y = mainY + onTouchMargin;
		holded = true;
	}

	public void onTouchUp() {
		y = mainY;
		holded = false;
	}

	public boolean in(float x, float y) {
		if (x > (this.x - this.width) / MySurfaceView.sizeWidthCoff
				&& x < (this.x + this.width) / MySurfaceView.sizeWidthCoff
				&& y > (this.y - this.height) / MySurfaceView.sizeHeightCoff
				&& y < (this.y + this.height) / MySurfaceView.sizeHeightCoff)
			return true;
		return false;
	}

	public static int getLineX(int numOfLine) {
		return firstLineX + (oldLineWidth + 10) * (numOfLine - 1) + 9;
	}

	public float getX(){
		return x;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public float getMainY(){
		return mainY;
	}

	public boolean isHolded(){
		return holded;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				onTouchDown();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				onTouchUp();
				break;
		}
		return false;
	}
}