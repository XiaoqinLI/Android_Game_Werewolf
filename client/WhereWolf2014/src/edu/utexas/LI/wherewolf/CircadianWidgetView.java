package edu.utexas.LI.wherewolf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

public class CircadianWidgetView extends View{
	private Paint canvasPaint, drawSunMoonPaint, drawNightPaint, drawDayPaint, drawDuskPaint, draw2DuskPaint, textPaint;
	private Bitmap canvasBitmap, moonBitmap, sunBitmap, nightBitmap, dayBitmap, duskBitmap, dusk2Bitmap;
	private Canvas drawCanvas;
	private int currentTime;

	public CircadianWidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
	}
	
	private void initPaint() {		
		drawNightPaint = new Paint();		
		drawDayPaint = new Paint();
		drawDuskPaint = new Paint();
		draw2DuskPaint = new Paint();
		drawSunMoonPaint = new Paint();
		textPaint = new Paint();
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		// be sure that you have pngs or jpgs in your drawables folder with 
		// the corresponding names (moon, night, etc)
		moonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		sunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		nightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.night);
		dayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.day);
		duskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dusk1);
		dusk2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dusk2);
	}
	
	//	Implement the changeTime method. 
	//	This will be called by the OnProgressChangedListener when the slider is changed.
	public void changeTime(int time, Activity activity) {
		this.currentTime = time;	
//		invalidate(); // causes the onDraw method to be invoked
		activity.runOnUiThread(new Runnable() {
			  public void run() {
				  invalidate();
			  }
			});
	}
	
	//	Implement the onDraw function:
	@Override
	protected void onDraw(Canvas canvas) {
		double w = drawCanvas.getWidth();
		double h = drawCanvas.getHeight();

		int iW = moonBitmap.getWidth() / 2;
		int iH = moonBitmap.getHeight() / 2;

		// draw the backdrop here
		// night 
		drawCanvas.drawBitmap(nightBitmap, 0, 0, drawNightPaint);
		// dusk2
		if (currentTime % 24 <= 5){
			draw2DuskPaint.setAlpha(50*(currentTime % 24));
		}	
		else if (currentTime % 24 >= 18 && currentTime % 24 <=23){
			draw2DuskPaint.setAlpha(50*(24 - currentTime%24));
		}
		else
		draw2DuskPaint.setAlpha(0);
		drawCanvas.drawBitmap(dusk2Bitmap, 0, 0, draw2DuskPaint);
		
		// dusk1
		if (currentTime % 24 >= 7 && currentTime % 24 < 12){
			drawDuskPaint.setAlpha(50*(currentTime % 24 - 6));
		}	
		else if (currentTime % 24 >= 13 && currentTime % 24 <18){
			drawDuskPaint.setAlpha(50*(18 - currentTime%24));
		}
		else
			drawDuskPaint.setAlpha(0);
		drawCanvas.drawBitmap(duskBitmap, 0, 0, drawDuskPaint);
		
		// day
		if (currentTime % 24>=5 && currentTime % 24<=19){
			if (currentTime / 12 % 2 == 0)	
				drawDayPaint.setAlpha(currentTime%12*21);
			else if (currentTime / 12 % 2 == 1)
				drawDayPaint.setAlpha(255 - currentTime%12*21 );
		}
		else 
			drawDayPaint.setAlpha(0);
		drawCanvas.drawBitmap(dayBitmap, 0, 0, drawDayPaint);
		
		int textXPos = (canvas.getWidth() / 2);
		int textYPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
		
		textPaint.setTextAlign(Align.CENTER);
	    textPaint.setTextSize(30);
	    textPaint.setColor(Color.WHITE);
		if (currentTime % 24>=6 && currentTime % 24<=18){
			drawCanvas.drawText("day", textXPos, textXPos, textPaint);
		}
		else{
			drawCanvas.drawText("night", textXPos, textXPos, textPaint);
		}
			
		
		// calculate the angle the moon should appear in the sky
		double theta = Math.PI / 2 + Math.PI * currentTime / 12;
		double suntheta = Math.PI / 2 + Math.PI * (currentTime+12) / 12;
		// calculate the x and y coordinates of where to draw the images
		// keep in mind the coordinates are the top left of the images
		// so you can use the bitmap width and height to compensate.
		double sunPosX = w / 2 - w / 3 * Math.cos(theta);
		double sunPosY = w / 2 + 1.5*w / 3 * Math.sin(theta); // replace this with your value
		double moonPosX = w / 2 - w / 3 * Math.cos(suntheta);
		double moonPosY = w / 2 + 1.5*w / 3 * Math.sin(suntheta); // replace this with your value
		drawCanvas.drawBitmap(moonBitmap, 
				(int) moonPosX - iW, (int) moonPosY + iH, drawSunMoonPaint);
		drawCanvas.drawBitmap(sunBitmap, 
				(int) sunPosX - iW, (int) sunPosY + iH, drawSunMoonPaint);
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);


	}
	
	//Implement the onSizeChanged function:
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}


}













