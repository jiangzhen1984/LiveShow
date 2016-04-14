package com.v2tech.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.v2tech.service.UserService;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.UserVideo;


public class PersonelVideosPresenter extends BasePresenter {
	
	public static final int ITEM_POS_LEFT = 1;
	public static final int ITEM_POS_CENTER = 2;
	public static final int ITEM_POS_RIGHT = 3;
	
	List<UserVideo>  videoList;
	UserService us;
	Context context;
	private boolean mode;

	public interface PersonelVideosPresenterUI {

		public void doFinish();

		public void updateTitle();
		
		public void refreshListView();
		
		public void updateItemLeftShot(Object obj, Bitmap screenShot, String time, Object tag);
		public void updateItemCenterShot(Object obj, Bitmap screenShot, String time, Object tag);
		public void updateItemRightShot(Object obj,  Bitmap screenShot, String time, Object tag);
		
		public void updateItemSelected(boolean select, View view);
		
		public void updateItemSelected(boolean select, Object bind, int type);
		
		public void updateSelectMode(boolean flag);
		
	}

	private Bitmap bm;
	private Bitmap bm1;
	private Bitmap bm2;
	private PersonelVideosPresenterUI ui;

	public PersonelVideosPresenter(Context context, PersonelVideosPresenterUI ui) {
		super();
		this.ui = ui;
		us = new UserService();
		videoList = new ArrayList<UserVideo>();
		this.context = context;
		bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.demo_1);
		bm1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.demo_2);
		bm2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.demo_3);
		
		for (int i = 0; i < 60; i++) {
			UserVideo uv = new UserVideo();
			uv.time = 25 + i;
			videoList.add(uv);
		}
		
	}

	@Override
	public void onUICreated() {
		ui.updateTitle();
		ui.refreshListView();
		ui.updateSelectMode(mode);
	}

	@Override
	public void onUIDestroyed() {
		super.destroyBackendThread();
		us.clearCalledBack();
	}

	
	
	@Override
	public void onReturnBtnClicked() {
		if (mode) {
			mode = false;
			ui.updateSelectMode(mode);
			//
			for (UserVideo uv : videoList) {
				uv.select = false;
			}
			ui.refreshListView();
			//TODO clear all selected item
		} else {
			ui.doFinish();
		}
	}

	public void doUpdateView(Object obj, int position) {
		int size = videoList.size();
		int idx = position * 3;
		if ( idx < size) {
			UserVideo uv = videoList.get(idx);
			int minute = (int)uv.time / 60;
			int second = (int)uv.time - minute * 60;
			String minuteStr = minute >= 10 ?  minute +"" : "0" + minute;
			String secondStr = second >= 10 ?  second +"" : "0" + second;
			ui.updateItemLeftShot(obj, bm, minuteStr +" :" + secondStr,   uv);
			ui.updateItemSelected(uv.select, obj, ITEM_POS_LEFT);
		}
		
		if ( idx + 1 < size) {
			UserVideo uv = videoList.get(idx + 1);
			int minute = (int)uv.time / 60;
			int second = (int)uv.time - minute * 60;
			String minuteStr = minute >= 10 ?  minute +"" : "0" + minute;
			String secondStr = second >= 10 ?  second +"" : "0" + second;
			ui.updateItemCenterShot(obj, bm1,  minuteStr +" :" + secondStr, uv);
			ui.updateItemSelected(uv.select, obj, ITEM_POS_CENTER);
		}
		
		if ( idx + 2 < size) {
			UserVideo uv = videoList.get(idx + 2);
			int minute = (int)uv.time / 60;
			int second = (int)uv.time - minute * 60;
			String minuteStr = minute >= 10 ?  minute +"" : "0" + minute;
			String secondStr = second >= 10 ?  second +"" : "0" + second;
			ui.updateItemRightShot(obj, bm2,  minuteStr +" :" + secondStr, uv);
			ui.updateItemSelected(uv.select, obj, ITEM_POS_RIGHT);
		}
	}

	
	public int getCount() {
		int size = videoList.size();
		
		return size == 0 ? 0 : size / 3 + 1;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return -1;
	}

	
	
	public void onVideoItemLongClicked(Object tag, View view) {
		if (!mode) {
			mode = true;
			ui.updateSelectMode(mode);
		}
		UserVideo uv = (UserVideo)tag;
		if (!uv.select) {
			ui.updateItemSelected(true, view);
			uv.select = true;
		} else {
			ui.updateItemSelected(false, view);
			uv.select = false;
		}
	}
	
	
	public void onVideoItemClicked(Object tag, View view) {
		UserVideo uv = (UserVideo)tag;
		if (uv.select) {
			ui.updateItemSelected(false, view);
			uv.select = false;
		} 
	}
	
}
