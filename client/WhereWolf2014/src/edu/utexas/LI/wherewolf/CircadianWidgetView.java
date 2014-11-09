package edu.utexas.LI.wherewolf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircadianWidgetView extends View{
	private Paint canvasPaint, drawPaint;
	private Bitmap canvasBitmap, moonBitmap, sunBitmap, nightBitmap;
	private Canvas drawCanvas;
	private double currentTime;
	
	public CircadianWidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
	}
	
	private void initPaint() {
		drawPaint = new Paint();
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		// be sure that you have pngs or jpgs in your drawables folder with 
		// the corresponding names (moon, night, etc)
		moonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		nightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.night);
	}
	
	//	Implement the changeTime method. 
	//	This will be called by the OnProgressChangedListener when the slider is changed.
	public void changeTime(double time) {
		this.currentTime = time;	
		invalidate(); // causes the onDraw method to be invoked
	}
	
	//	Implement the onDraw function:
	protected void onDraw(Canvas canvas) {
		double w = drawCanvas.getWidth();
		double h = drawCanvas.getHeight();

		int iW = moonBitmap.getWidth() / 2;
		int iH = moonBitmap.getHeight() / 2;

		// draw the backdrop here

		// calculate the angle the moon should appear in the sky
		double theta = Math.PI / 2 + Math.PI * currentTime / 12;

		// calculate the x and y coordinates of where to draw the images
		// keep in mind the coordinates are the top left of the images
		// so you can use the bitmap width and height to compensate.

		double moonPosX = w / 2 - w / 3 * Math.cos(theta);
		double moonPosY = w / 2 - w / 3 * Math.sin(theta); // replace this with your value

		drawCanvas.drawBitmap(moonBitmap, 
				(int) moonPosX - iW, (int) moonPosY + iH, drawPaint); 

		// draw your sun and other things here as well.
		// experiment with drawCanvas.drawText for putting labels of whether it is day
		// or night.

	}
	
	//Implement the onSizeChanged function:
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}


}













