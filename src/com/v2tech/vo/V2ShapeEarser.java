package com.v2tech.vo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class V2ShapeEarser extends V2Shape {

	private Path p;
	List<Float> l;

	public V2ShapeEarser() {
		p = new Path();
		l = new ArrayList<Float>();
	}

	public void addPoint(int x, int y) {
		l.add(Float.valueOf(x));
		l.add(Float.valueOf(y));
	}

	public void addLine(int x1, int y1, int x2, int y2) {
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
	}

	public void lineToLine(int x1, int y1, int x2, int y2) {
		p.lineTo(x1, y1);
		p.lineTo(x2, y2);
	}

	public void addRect(int left, int top, int right, int bottom) {
		p.addRect(left, top, right, bottom, Path.Direction.CCW);
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas == null) {
			throw new NullPointerException(" canvas is null ");
		}
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		//canvas.drawPath(p, paint);
		float[] fr = new float[l.size()];
		for (int i = 0; i < fr.length; i += 2) {
			fr[0] = l.get(i);
			
			canvas.drawRect(new Rect((int) fr[0] - 10, (int) fr[1] - 10,
					(int) fr[0] + 10, (int) fr[0] + 10), paint);
		}

	}

}
