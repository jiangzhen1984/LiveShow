package com.v2tech.presenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.V2.jni.ind.MessageInd;
import com.V2.jni.util.V2Log;
import com.v2tech.audio.AACEncoder;
import com.v2tech.audio.AACEncoder.AACEncoderNotification;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.LiveMessageHandler;
import com.v2tech.service.P2PMessageService;
import com.v2tech.util.GlobalConfig;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.User;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAbstractItem;
import com.v2tech.vo.msg.VMessageAudioItem;
import com.v2tech.vo.msg.VMessageFaceItem;
import com.v2tech.vo.msg.VMessageTextItem;
import com.v2tech.widget.RichEditText;
import com.v2tech.widget.emoji.EmojiLayoutWidget.EmojiLayoutWidgetListener;

public class P2PMessagePresenter extends BasePresenter implements LiveMessageHandler, EmojiLayoutWidgetListener, AACEncoderNotification {
	
	
	private static final int TYPE_SHOW_ADDITIONAL_LAYOUT = 1;
	private static final int TYPE_SHOW_EMOJI_LAYOUT = 2;
	private static final int TYPE_SHOW_TEXT_LAYOUT = 4;
	private static final int TYPE_SHOW_VOICE_LAYOUT = 8;
	
	
	public static final int ITEM_TYPE_DATE = 2;
	public static final int ITEM_TYPE_SELF = 0;
	public static final int ITEM_TYPE_OTHERS = 1;
	
	private State state = State.IDLE; 
	
	private Context context;
	private P2PMessagePresenterUI ui;
	
	private List<Item> itemList;
	private LocalAdapter localAdapter;
	
	private int additonState;
	
	private User chatUser;
	private P2PMessageService messageService;
	private Handler loader;
	private Handler uiHandler;
	
	private AACEncoder aacRecorder;
	
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
		
		public void showVoiceDialog(boolean flag);
		
		public void showCancelRecordingDialog(boolean flag);
		
		public void updateVoiceDBLevel(int level);
		
		
		public void switchToVoice();
		
		public void switchToText();
		
	}

	public P2PMessagePresenter(Context context, P2PMessagePresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		messageService = new  P2PMessageService(context);
		
		itemList = new ArrayList<Item>(20);
		loader = new Handler(super.backendThread.getLooper());
		uiHandler = new Handler();
		localAdapter = new LocalAdapter();
		ui.setAdapter(localAdapter);
		chatUser = new User(ui.getIntentUserId());
		
		loader.postDelayed(new LoaderWorker(0, 30), 100);
		
		aacRecorder = new AACEncoder(this);
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
	
	
	
	public void switcherBtnClicked() {
		if ((this.additonState & TYPE_SHOW_TEXT_LAYOUT) == TYPE_SHOW_TEXT_LAYOUT) {
			ui.switchToVoice();
			additonState &= (~TYPE_SHOW_TEXT_LAYOUT);
			additonState |= TYPE_SHOW_VOICE_LAYOUT;
		} else if  ((this.additonState & TYPE_SHOW_VOICE_LAYOUT) == TYPE_SHOW_VOICE_LAYOUT) {
			ui.switchToText();
			additonState &= (~TYPE_SHOW_VOICE_LAYOUT);
			additonState |= TYPE_SHOW_TEXT_LAYOUT;
		}
	}
	
	
	
	public void onRecordBtnTouchDown(MotionEvent ev) {
		if (state != State.IDLE) {
			throw new RuntimeException("illegale state " + state);
		}
		ui.showVoiceDialog(true);
		ui.showCancelRecordingDialog(false);
		aacRecorder.start();
	}
	
	public void onRecordBtnTouchUp(MotionEvent ev) {
		ui.showVoiceDialog(false);
		ui.showCancelRecordingDialog(false);
		aacRecorder.stop();
	}

	public void onRecordBtnTouchMoveOutOfBtn(MotionEvent ev) {
		ui.showVoiceDialog(false);
		ui.showCancelRecordingDialog(true);
	}
	
	public void onRecordBtnTouchMoveInBtn(MotionEvent ev) {
		ui.showVoiceDialog(true);
		ui.showCancelRecordingDialog(false);
	}
	
	@Override
	public void onUICreated() {
		super.onUICreated();
		ui.showAdditionLayout(false);
		ui.showEmojiLayout(false);
		additonState |= TYPE_SHOW_VOICE_LAYOUT;
		switcherBtnClicked();
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
		
	}


	@Override
	public void onVdideoMessage(long liveId, long uid, int opt) {
		
	}


	@Override
	public void onLiveMessage(long liveId, long uid, MessageInd ind) {
		
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

	
	
	
	

//////////////////AACEncoderNotification////////////////////////

	@Override
	public void onRecordStart() {
		synchronized(state) {
			state = State.RECORDING;
		}
		
		boolean ret = openAACFile();
		if (!ret) {
			//TODO notify user
		}
		duration = System.currentTimeMillis();
		V2Log.i("=====start to record , open file aac file " + ret +"  file:"+ accFile);
	}


	@Override
	public void onRecordFinish() {
		synchronized(state) {
			if (state == State.RECORDING) {
				//Send audio message;
			}
			state = State.IDLE;
		}
		//TODO check data length, if not available, notify user
		boolean ret = closeAACFile();
		V2Log.i("=====finish record , close file aac file " + ret +"  file:"+ accFile);
		if (!ret) {
			//TODO notify user
		} else {
			duration = (System.currentTimeMillis() - duration);
			
			if (duration < 1500) {
				accFile.deleteOnExit();
				//TODO short time 
				return;
			}
			VMessage vm = new VMessage(0, 0, GlobalHolder.getInstance()
					.getCurrentUser(), chatUser, new Date());
			new VMessageAudioItem(vm, uuid, accFile.getName(), "aac", (int)duration, 0);
			messageService.sendMessage(vm, chatUser);
		}
		
		
	}


	@Override
	public void onError(Throwable e) {
		synchronized(state) {
			state = State.IDLE;
		}
		boolean ret = closeAACFile();
		if (!ret) {
			//TODO notify user
		} else {
			
		}
		
		duration = 0;
		V2Log.e("=====error on record , close file aac file " + ret +"  file:"+ accFile +"  "+ e);
	}


	@Override
	public void onDBChanged(double db) {
		// TODO Auto-generated method stub
		ui.updateVoiceDBLevel(2);
	}


	@Override
	public void onAACDataOutput(byte[] data, int len) {
		if (state ==  State.RECORDING || state ==  State.RECORDING_SHOW_CANCEL_DIALOG) {
			if (!writeAACData(data, len)) {
				//write error
			}
		}
		
	} 

	
	private boolean openAACFile() {
		try {
			uuid = UUID.randomUUID().toString();
			accFile = new File(GlobalConfig.getGlobalAudioPath() + "/"
					+ uuid + ".aac");
			out = new FileOutputStream(accFile);
		} catch (FileNotFoundException e) {
			V2Log.e(" open aac file error:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean closeAACFile() {

		try {
			out.close();
		} catch (IOException e1) {
			V2Log.e(" close aac file error:" + e1.getMessage());
			return false;
		}
		out = null;
		return true;
	}
	
	private boolean writeAACData(byte[] data, int len) {
		//TODO save to file
		try {
			out.write(data, 0, len);
		} catch (IOException e) {
			V2Log.e(" write aac file error:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	
	private long duration;
	private String uuid;
	private File accFile;
	private OutputStream out;


//////////////////AACEncoderNotification////////////////////////




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
	
	
	enum State {
		RECORDING,
		RECORDING_SHOW_CANCEL_DIALOG,
		IDLE,
	}
	
}
