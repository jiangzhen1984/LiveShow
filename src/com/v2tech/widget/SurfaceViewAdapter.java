package com.v2tech.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class SurfaceViewAdapter extends PagerAdapter {
	
	private Context ctx;
	private SurfaceView surs[];

	public SurfaceViewAdapter(Context ct, int fragmentCounts) {
		this.ctx = ct;
		surs = new SurfaceView[fragmentCounts];
	}

	@Override
	public int getCount() {
		return surs.length;
	}


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (surs[position] == null) {
			surs[position] = createSurfaceView(position + 1);
		}
		container.addView(surs[position], 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		return surs[position];
	}
	
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return false;
	}

	private SurfaceView createSurfaceView(int index) {
		SurfaceView mSurfaceView = new SurfaceView(ctx);
//		mSurfaceView.setZOrderOnTop(true);
//	//	mSurfaceView.setZOrderMediaOverlay(true);
		mSurfaceView.getHolder().addCallback(mHolderCallback);
//		mSurfaceView.setTag(index);
//		mSurfaceView.setWillNotDraw(true);
		return mSurfaceView;
	}
	
	
	
	
	public Object getItem(int index) {
		return surs[index];
	}
	
	
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas c = holder.lockCanvas();
			drawFirstBlankFrame(c);
			holder.unlockCanvasAndPost(c);
			Surface surface = holder.getSurface();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}

	};
	
	static int index = 1;
	private void drawFirstBlankFrame(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(60);
		tmp.drawText((index++) + "", width / 2, height / 2, p);
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}
	

}
