package com.v2tech.vo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class V2ShapeEllipse extends V2Shape {

	int left;
	int top;
	int right;
	int bottom;
	
	public V2ShapeEllipse(int left, int top, int right, int bottom) {
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
		RectF f = new RectF();
		f.left = left;
		f.top = top;
		f.right = right;
		f.bottom = bottom;
		canvas.drawOval(f, paint);
	}

}
