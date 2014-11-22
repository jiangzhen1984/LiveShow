package com.v2tech.vo;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.V2.jni.util.V2Log;

public class V2ShapeLine extends V2Shape {

	private V2ShapePoint[] points;

	public V2ShapeLine(V2ShapePoint[] points) {
		this.points = points;
		if (this.points == null) {
			this.points = new V2ShapePoint[] {};
		}
	}

	public void addPoints(V2ShapePoint point) {
		if (point == null) {
			return;
		}
		V2ShapePoint[] newPointsArr = new V2ShapePoint[points.length + 1];
		System.arraycopy(points, 0, newPointsArr, 0, points.length);
		newPointsArr[newPointsArr.length - 1] = point;
		this.points = newPointsArr;
	}

	public void addPoints(V2ShapePoint[] newPoints) {
		if (newPoints == null) {
			return;
		}
		V2ShapePoint[] newPointsArr = new V2ShapePoint[this.points.length
				+ newPoints.length];
		System.arraycopy(this.points, 0, newPointsArr, 0, this.points.length);
		System.arraycopy(newPoints, 0, newPointsArr, this.points.length,
				newPoints.length);
		this.points = newPointsArr;
	}

	@Override
	public void draw(Canvas canvas) {
		if (points == null) {
			V2Log.e(" No points");
			return;
		}
		if (paint == null) {
			paint = new Paint();
		}
		float[] fp = new float[points.length * 2];
		int index = 0;
		for (V2ShapePoint p : points) {
			fp[index++] = p.x;
			fp[index++] = p.y;
		}
		index = 0;
		while (index <= fp.length - 4) {
			canvas.drawLine(fp[index], fp[index+1], fp[index+2], fp[index+3], paint);
			index +=2;
		}
	}

}
