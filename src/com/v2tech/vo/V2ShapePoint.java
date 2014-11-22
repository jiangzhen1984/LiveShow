package com.v2tech.vo;

import android.graphics.Canvas;
import android.graphics.Paint;

public class V2ShapePoint extends V2Shape {

	int x;
	int y;
	int z;
	
	public V2ShapePoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public V2ShapePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public void draw(Canvas canvas) {
		if (paint == null) {
			paint = new Paint();
		}
		canvas.drawPoint(x, y, paint);
		
	}
	
	

}
