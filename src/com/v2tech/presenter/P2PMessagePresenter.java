package com.v2tech.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class P2PMessagePresenter extends BasePresenter {
	
	
	private static final int TYPE_SHOW_ADDITIONAL_LAYOUT = 1;
	private static final int TYPE_SHOW_EMOJI_LAYOUT = 2;
	
	private Context context;
	private P2PMessagePresenterUI ui;
	
	private List<Item> itemList;
	
	private LocalAdapter localAdapter;
	
	private int additonState;
	
	public interface P2PMessagePresenterUI {
		public void setAdapter(BaseAdapter adapter);
		
		public View getView();
		
		public void updateView(View view, int type, Bitmap bm, String content);
		public void updateView(View view, int type, String content);
		
		public void showAdditionLayout(boolean flag);
		
		public void showEmojiLayout(boolean flag);
		
		public void finishMainUI();
	}

	public P2PMessagePresenter(Context context, P2PMessagePresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		additonState = 0;
		
		itemList = new ArrayList<Item>(60);
		Item item = new Item();
		item.type = 2;
		item.content ="2016-04-20 14:34:32";
		itemList.add(item);
		for (int i = 0; i <60; i++) {
			item = new Item();
			item.type = i % 2;
			item.content ="特素推送地方 " + i+" \n\n";
			itemList.add(item);
		}
		
		localAdapter = new LocalAdapter();
		ui.setAdapter(localAdapter);
	
	}
	
	
	public void emojiBtnClicked() {
		ui.showAdditionLayout(false);
		if (isState(additonState, TYPE_SHOW_EMOJI_LAYOUT)) {
			additonState &= (~TYPE_SHOW_EMOJI_LAYOUT);
			additonState &= (~TYPE_SHOW_ADDITIONAL_LAYOUT);
			ui.showEmojiLayout(false);
		} else {
			additonState |= TYPE_SHOW_EMOJI_LAYOUT;
			additonState &= (~TYPE_SHOW_ADDITIONAL_LAYOUT);
			ui.showEmojiLayout(true);
		}
	}
	
	public void plusBtnClicked() {
		ui.showEmojiLayout(false);
		if (isState(additonState, TYPE_SHOW_ADDITIONAL_LAYOUT)) {
			additonState &= (~TYPE_SHOW_EMOJI_LAYOUT);
			additonState &= (~TYPE_SHOW_ADDITIONAL_LAYOUT);
			ui.showAdditionLayout(false);
		} else {
			additonState |= TYPE_SHOW_ADDITIONAL_LAYOUT;
			additonState &= (~TYPE_SHOW_EMOJI_LAYOUT);
			ui.showAdditionLayout(true);
		}
	}
	
	
	

	public void returnBtnClicked() {
		ui.finishMainUI();
	}


	
	
	

	@Override
	public void onUICreated() {
		super.onUICreated();
		ui.showAdditionLayout(false);
		ui.showEmojiLayout(false);
	}


	private boolean isState(int st, int flag) {
		return (st & flag) == flag? true : false;
	}
	
	
	class LocalAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = ui.getView();
			}
			ui.updateView(convertView, itemList.get(position).type, itemList.get(position).content);
			return convertView;
		}
		
	}
	
	
	class Item {
		int type;
		Bitmap avatar;
		String content;
	}
	
}
