package com.v2tech.vo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

public class V2ShapeMeta {
	
	String docId;
	int pageNo;
	private String id;
	private List<V2Shape> mShapes;
	
	
	public V2ShapeMeta(String id, List<V2Shape> mShapes) {
		this.id = id;
		this.mShapes = mShapes;
		if (this.mShapes == null) {
			this.mShapes = new ArrayList<V2Shape>();
		}
	}


	public V2ShapeMeta(String id) {
		this.id = id;
		mShapes = new ArrayList<V2Shape>();
	}
	
	public void addShape(V2Shape shape) {
		this.mShapes.add(shape);
	}
	
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}


	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public int getPageNo() {
		return pageNo;
	}


	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public void draw(Canvas canvas) {
		for (V2Shape shape :this.mShapes) {
			shape.draw(canvas);
		}
	}
	

}
