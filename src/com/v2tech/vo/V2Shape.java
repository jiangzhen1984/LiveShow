package com.v2tech.vo;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class V2Shape {
	
	protected Paint paint;
	protected Type type;
	
	
	public enum Type {
		ADD,CHANGE,DELETE;
	}
	
	public abstract void draw(Canvas canvas);
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public void setColor(int color) {
		if (this.paint == null) {
			this.paint = new Paint();
		}
		this.paint.setColor(color);
	}
	
	public void setWidth(int width) {
		if (this.paint == null) {
			this.paint = new Paint();
		}
		this.paint.setStrokeWidth(width);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	

}
