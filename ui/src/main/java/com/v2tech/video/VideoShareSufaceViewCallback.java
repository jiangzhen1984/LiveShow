package com.v2tech.video;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class VideoShareSufaceViewCallback implements SurfaceHolder.Callback {

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas c = holder.lockCanvas();
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.argb(255, 0, 0, 0));
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
		holder.unlockCanvasAndPost(c);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	
}
