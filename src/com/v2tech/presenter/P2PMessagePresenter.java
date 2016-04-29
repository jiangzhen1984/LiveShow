package com.v2tech.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.v2tech.service.P2PMessageService;

public class P2PMessagePresenter extends BasePresenter {
	
	
	private static final int TYPE_SHOW_ADDITIONAL_LAYOUT = 1;
	private static final int TYPE_SHOW_EMOJI_LAYOUT = 2;
	
	public static final int ITEM_TYPE_DATE = 2;
	public static final int ITEM_TYPE_SELF = 0;
	public static final int ITEM_TYPE_OTHERS = 1;
	
	
	private Context context;
	private P2PMessagePresenterUI ui;
	
	private List<Item> itemList;
	
	private LocalAdapter localAdapter;
	
	private int additonState;
	
	
	private P2PMessageService messageService;
	
	public interface P2PMessagePresenterUI {
		public void setAdapter(BaseAdapter adapter);
		
		public View getView();
		
		public void updateView(View view, int type, Bitmap bm, String content);
		public void updateView(View view, int type, String content);
		
		public void showAdditionLayout(boolean flag);
		
		public void showEmojiLayout(boolean flag);
		
		public void finishMainUI();
		
		public Editable getEditable();
		
		public void scrollTo(int position);
	}

	public P2PMessagePresenter(Context context, P2PMessagePresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		additonState = 0;
		messageService = new  P2PMessageService();
		
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


	public void sendBtnClicked() {
		Editable et = ui.getEditable();
		Item i = new Item();
		i.type = ITEM_TYPE_SELF;
		i.content = et.toString();
		itemList.add(i);
		localAdapter.notifyDataSetChanged();
		ui.scrollTo(itemList.size());
		
		//TODO build message and sent
	}
	
	

	@Override
	public void onUICreated() {
		super.onUICreated();
		ui.showAdditionLayout(false);
		ui.showEmojiLayout(false);
		
		//TODO get user information
	}
	
	


	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		messageService.clearCalledBack();
		messageService = null;
	}
	
	
	public void onReturnBtnClicked() {
		ui.finishMainUI();
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
