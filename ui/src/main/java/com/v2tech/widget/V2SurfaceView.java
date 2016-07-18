package com.v2tech.widget;

import com.V2.jni.util.V2Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class V2SurfaceView extends SurfaceView {

	public V2SurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public V2SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public V2SurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	float alpha = 1.0F;
	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.alpha = alpha;
	}

	@Override
	public void draw(Canvas canvas) {
		V2Log.i("====== draw");
		super.draw(canvas);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		V2Log.i("======on draw");
		super.onDraw(canvas);
	}
	
	
	
	

}
