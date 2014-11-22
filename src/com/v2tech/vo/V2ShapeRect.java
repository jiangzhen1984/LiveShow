package com.v2tech.vo;

import android.graphics.Canvas;
import android.graphics.Paint;

public class V2ShapeRect extends V2Shape {
	
	int left;
	int top;
	int right;
	int bottom;
	

	public V2ShapeRect(int left, int top, int right, int bottom) {
		super();
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}


	@Override
	public void draw(Canvas canvas) {
		if (paint == null) {
			paint = new Paint();
		}
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(left, top, right, bottom, paint);
	}

}
