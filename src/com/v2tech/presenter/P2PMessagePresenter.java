package com.v2tech.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.V2.jni.ind.MessageInd;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveMessageHandler;
import com.v2tech.service.P2PMessageService;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAbstractItem;
import com.v2tech.vo.msg.VMessageFaceItem;
import com.v2tech.vo.msg.VMessageTextItem;
import com.v2tech.widget.RichEditText;
import com.v2tech.widget.emoji.EmojiLayoutWidget.EmojiLayoutWidgetListener;

public class P2PMessagePresenter extends BasePresenter implements LiveMessageHandler, EmojiLayoutWidgetListener {
	
	
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
	
	private User chatUser;
	private P2PMessageService messageService;
	private Handler loader;
	private Handler uiHandler;
	
	public interface P2PMessagePresenterUI {
		public void setAdapter(BaseAdapter adapter);
		
		public View getView();
		
		public void updateView(View view, int type, Bitmap bm, CharSequence content);
		public void updateView(View view, int type, CharSequence content);
		
		public void showAdditionLayout(boolean flag);
		
		public void showEmojiLayout(boolean flag);
		
		public void finishMainUI();
		
		public RichEditText getEditable();
		
		public void scrollTo(int position);
		
		public long getIntentUserId();
		
	}

	public P2PMessagePresenter(Context context, P2PMessagePresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		additonState = 0;
		messageService = new  P2PMessageService(context);
		
		itemList = new ArrayList<Item>(20);
		loader = new Handler(super.backendThread.getLooper());
		uiHandler = new Handler();
		localAdapter = new LocalAdapter();
		ui.setAdapter(localAdapter);
		chatUser = new User(ui.getIntentUserId());
		
		loader.postDelayed(new LoaderWorker(0, 30), 100);
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
		RichEditText et = ui.getEditable();
		Editable ettext = et.getEditableText();
		//0 for p2p
		int strStart = 0;
		int spStart = -1;
		int spEnd = -1;
		int len = ettext.length();
		VMessage vm = new VMessage(0, 0, GlobalHolder.getInstance()
				.getCurrentUser(), chatUser, new Date());
		ImageSpan[] sbi = ettext.getSpans(0, len, ImageSpan.class);
		
		for (ImageSpan s: sbi) {
			//new VMessageTextItem(vm, i.content);
			spStart = ettext.getSpanStart(s);
		
			if (strStart != spStart) {
				new VMessageTextItem(vm, ettext.subSequence(strStart, spStart).toString());
				strStart = spStart;
			} 
			
			spEnd = ettext.getSpanEnd(s);
			new VMessageFaceItem(vm, Integer.parseInt(s.getSource()));
			//reset string index
			strStart = spEnd;
		}
		
		if (strStart != len) {
			new VMessageTextItem(vm, ettext.subSequence(strStart, len).toString());
		}
		messageService.sendMessage(vm, chatUser);
		
		Item i = new Item();
		i.id = vm.getId();
		i.type = ITEM_TYPE_SELF;
		i.content = new SpannableStringBuilder(ettext);
		itemList.add(i);
		localAdapter.notifyDataSetChanged();
		ui.scrollTo(itemList.size());
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
	
	
	
	
	
	@Override
	public void onAudioMessage(long liveId, long uid, int opt) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onVdideoMessage(long liveId, long uid, int opt) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLiveMessage(long liveId, long uid, MessageInd ind) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onP2PMessage(VMessage vm) {
		Item i = new Item();
		i.content =  buildContent(vm);
		i.type = ITEM_TYPE_OTHERS;
		itemList.add(i);
	}


	
	
	



	@Override
	public void onEmojiClicked(View v) {
		ImageView iv = (ImageView)v; 
		ui.getEditable().appendEmoji(iv.getDrawable(), Integer.parseInt(iv.getTag().toString()));
	}



	private CharSequence buildContent(VMessage vm) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		List<VMessageAbstractItem>  list = vm.getItems();
		for (VMessageAbstractItem item : list) {
			if (item.getType() == VMessageAbstractItem.ITEM_TYPE_TEXT) {
				builder.append(((VMessageTextItem)item).getText());
			} else if (item.getType() == VMessageAbstractItem.ITEM_TYPE_FACE) {
				SpannableStringBuilder emojiBuilder = new SpannableStringBuilder("[at]", 0, 4);
				Drawable dra = context.getResources().getDrawable(R.drawable.emo_01);
				dra.setBounds(0, 0, dra.getIntrinsicWidth(), dra.getIntrinsicHeight());
				ImageSpan span = new ImageSpan(dra);
				emojiBuilder.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				builder.append(emojiBuilder);
			}
		}
		return builder;
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
	
	
	
	class LoaderWorker implements Runnable {

		int start;
		int page;
		public LoaderWorker(int start, int page) {
			super();
			this.start = start;
			this.page = page;
		}
		
		@Override
		public void run() {
			List<VMessage> list = messageService.getVMList(chatUser.getmUserId(), start, page);
			Item i = null;
			long uid = GlobalHolder.getInstance().getCurrentUserId();
			for (VMessage m : list) {
				i = new Item();
				i.content =  buildContent(m);
				if (m.getFromUser().getmUserId() == uid) {
					i.type = ITEM_TYPE_SELF;
				} else {
					i.type = ITEM_TYPE_OTHERS;
				}
				itemList.add(i);
			}
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					localAdapter.notifyDataSetChanged();
				}
				
			});
		}
		
	}
	
	
	class Item {
		long id;
		int type;
		Bitmap avatar;
		CharSequence content;
	}
	
}
