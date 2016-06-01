package com.v2tech.presenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
import com.v2tech.audio.AACDecoder;
import com.v2tech.audio.AACDecoder.DecoderNotification;
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

public class P2PMessagePresenter extends BasePresenter implements
		LiveMessageHandler, EmojiLayoutWidgetListener, AACEncoderNotification,
		DecoderNotification {

	private static final int TYPE_SHOW_ADDITIONAL_LAYOUT = 1;
	private static final int TYPE_SHOW_EMOJI_LAYOUT = 2;
	private static final int TYPE_SHOW_TEXT_LAYOUT = 4;
	private static final int TYPE_SHOW_VOICE_LAYOUT = 8;

	public static final int ITEM_TYPE_DATE = 2;
	public static final int ITEM_TYPE_SELF = 0;
	public static final int ITEM_TYPE_OTHERS = 1;

	public static final int ITEM_MSG_TYPE_TEXT = 1;
	public static final int ITEM_MSG_TYPE_IMAGE = 2;
	public static final int ITEM_MSG_TYPE_AUDIO = 3;

	public static final int DIALOG_TYPE_NONE = 0;
	public static final int DIALOG_TYPE_VOLUMN = 1;
	public static final int DIALOG_TYPE_TOUCH_UP_CANCEL = 2;
	public static final int DIALOG_TYPE_LONG_DURATION = 3;
	public static final int DIALOG_TYPE_SHORT_DURATION = 4;

	public static final int UI_MSG_SHOW_DIALOG = 1;
	public static final int UI_MSG_DISMISS_DIALOG = 2;

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
	private AACDecoder aacDecoder;

	public interface P2PMessagePresenterUI {
		public void setAdapter(BaseAdapter adapter);

		public View getView();

		public void updateView(View view, int dir, Bitmap bm,
				CharSequence content, int msgType, boolean audioPlay, Object tag);

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

		public void showDialog(boolean flag, int type);

		public void showSendBtn(boolean flag);

		public void showPlusBtn(boolean flag);

	}

	public P2PMessagePresenter(Context context, P2PMessagePresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		messageService = new P2PMessageService(context);

		itemList = new ArrayList<Item>(20);
		loader = new Handler(super.backendThread.getLooper());
		uiHandler = new UIHandler(ui);
		localAdapter = new LocalAdapter();
		ui.setAdapter(localAdapter);
		chatUser = new User(ui.getIntentUserId());

		loader.postDelayed(new LoaderWorker(0, 30), 100);

		aacRecorder = new AACEncoder(this);
		aacDecoder = new AACDecoder(this);
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
		// 0 for p2p
		int strStart = 0;
		int spStart = -1;
		int spEnd = -1;
		int len = ettext.length();
		VMessage vm = new VMessage(0, 0, GlobalHolder.getInstance()
				.getCurrentUser(), chatUser, new Date());
		ImageSpan[] sbi = ettext.getSpans(0, len, ImageSpan.class);

		for (ImageSpan s : sbi) {
			// new VMessageTextItem(vm, i.content);
			spStart = ettext.getSpanStart(s);

			if (strStart != spStart) {
				new VMessageTextItem(vm, ettext.subSequence(strStart, spStart)
						.toString());
				strStart = spStart;
			}

			spEnd = ettext.getSpanEnd(s);
			new VMessageFaceItem(vm, Integer.parseInt(s.getSource()));
			// reset string index
			strStart = spEnd;
		}

		if (strStart != len) {
			new VMessageTextItem(vm, ettext.subSequence(strStart, len)
					.toString());
		}
		messageService.sendMessage(vm, chatUser);

		itemList.add(buildItem(vm));
		notifyUIScroller(true);
	}

	public void switcherBtnClicked() {
		if ((this.additonState & TYPE_SHOW_TEXT_LAYOUT) == TYPE_SHOW_TEXT_LAYOUT) {
			ui.switchToVoice();
			additonState &= (~TYPE_SHOW_TEXT_LAYOUT);
			additonState |= TYPE_SHOW_VOICE_LAYOUT;
		} else if ((this.additonState & TYPE_SHOW_VOICE_LAYOUT) == TYPE_SHOW_VOICE_LAYOUT) {
			ui.switchToText();
			additonState &= (~TYPE_SHOW_VOICE_LAYOUT);
			additonState |= TYPE_SHOW_TEXT_LAYOUT;
		}
	}

	public void onRecordBtnTouchDown(MotionEvent ev) {
		if (state == State.DECODING) {
			// stop decoding first
			aacDecoder.stop();
		}

		ui.showDialog(true, DIALOG_TYPE_VOLUMN);
		ui.showVoiceDialog(true);
		ui.showCancelRecordingDialog(false);
		aacRecorder.start();
	}

	public void onRecordBtnTouchUp(MotionEvent ev) {
		ui.showVoiceDialog(false);
		ui.showCancelRecordingDialog(false);
		// TODO check cancel flag
		aacRecorder.stop();
		ui.showDialog(false, DIALOG_TYPE_VOLUMN);
	}

	public void onRecordBtnTouchMoveOutOfBtn(MotionEvent ev) {
		ui.showVoiceDialog(false);
		ui.showCancelRecordingDialog(true);
		ui.showDialog(true, DIALOG_TYPE_TOUCH_UP_CANCEL);
		synchronized (state) {
			state = State.RECORDING_SHOW_CANCEL_DIALOG;
		}
	}

	public void onRecordBtnTouchMoveInBtn(MotionEvent ev) {
		ui.showVoiceDialog(true);
		ui.showCancelRecordingDialog(false);
		ui.showDialog(true, DIALOG_TYPE_VOLUMN);
		synchronized (state) {
			state = State.RECORDING;
		}
	}

	public void onContentClicked(View view, Object tag) {
		Item item = (Item) tag;
		if (item.msgType == ITEM_MSG_TYPE_AUDIO) {
			aacDecoder.stop();
			if (!item.isPlaying) {
				aacDecoder.play(item.path);
				// TODO start playing animation
			}
			item.isPlaying = !item.isPlaying;

			if (item.vm.isReadState() == VMessageAbstractItem.STATE_UNREAD) {
				messageService.updateVMessageReadFlag(item.id, true);
				item.vm.setReadState(VMessageAbstractItem.STATE_READED);
			}
		} else if (item.msgType == ITEM_MSG_TYPE_TEXT) {

		}
	}

	public void onTextChanged(CharSequence cs) {
		if (cs.length() > 0) {
			ui.showSendBtn(true);
			ui.showPlusBtn(false);
		} else {
			ui.showSendBtn(false);
			ui.showPlusBtn(true);
		}
	}

	@Override
	public void onUICreated() {
		super.onUICreated();
		ui.showAdditionLayout(false);
		ui.showEmojiLayout(false);
		// Check intent flag first

		additonState |= TYPE_SHOW_VOICE_LAYOUT;
		switcherBtnClicked();
		ui.showSendBtn(false);
		// TODO get user information
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
		return (st & flag) == flag ? true : false;
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
		itemList.add(buildItem(vm));
		notifyUIScroller(false);
	}

	@Override
	public void onEmojiClicked(View v) {
		ImageView iv = (ImageView) v;
		ui.getEditable().appendEmoji(iv.getDrawable(),
				Integer.parseInt(iv.getTag().toString()));
	}

	private CharSequence buildContent(VMessage vm) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		List<VMessageAbstractItem> list = vm.getItems();
		for (VMessageAbstractItem item : list) {
			if (item.getType() == VMessageAbstractItem.ITEM_TYPE_TEXT) {
				builder.append(((VMessageTextItem) item).getText());
			} else if (item.getType() == VMessageAbstractItem.ITEM_TYPE_FACE) {
				SpannableStringBuilder emojiBuilder = new SpannableStringBuilder(
						"[at]", 0, 4);
				Drawable dra = context.getResources().getDrawable(
						R.drawable.emo_01);
				dra.setBounds(0, 0, dra.getIntrinsicWidth(),
						dra.getIntrinsicHeight());
				ImageSpan span = new ImageSpan(dra);
				emojiBuilder.setSpan(span, 0, 4,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				builder.append(emojiBuilder);
			}
		}
		return builder;
	}

	private Item buildItem(VMessage vm) {
		Item i = new Item();
		i.content = buildContent(vm);
		i.vm = vm;
		i.id = vm.getId();
		if (vm.getFromUser().getmUserId() == GlobalHolder.getInstance()
				.getCurrentUserId()) {
			i.type = ITEM_TYPE_SELF;
		} else {
			i.type = ITEM_TYPE_OTHERS;
		}

		List<VMessageAudioItem> audios = vm.getAudioItems();
		if (audios.size() > 0) {
			i.msgType = ITEM_MSG_TYPE_AUDIO;
			i.path = audios.get(0).getAudioFilePath();
		} else if (vm.getImageItems().size() > 0) {
			i.msgType = ITEM_MSG_TYPE_IMAGE;
		} else {
			i.msgType = ITEM_MSG_TYPE_TEXT;
		}
		return i;
	}

	private void notifyUIScroller(boolean uiThread) {
		if (uiThread) {
			localAdapter.notifyDataSetChanged();
			return;
		}
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				localAdapter.notifyDataSetChanged();
			}

		});
	}

	// ////////////////AACEncoderNotification////////////////////////

	@Override
	public void onRecordStart() {
		synchronized (state) {
			state = State.RECORDING;
		}

		boolean ret = openAACFile();
		if (!ret) {
			// TODO notify user
		}
		duration = System.currentTimeMillis();
		V2Log.i("=====start to record , open file aac file " + ret + "  file:"
				+ accFile);
	}

	@Override
	public void onRecordFinish() {
		
		boolean ret = closeAACFile();
		V2Log.i("=====finish record , close file aac file " + ret + "  file:"
				+ accFile);
		if (!ret) {
			// TODO notify user
			return;
		} 
		
		boolean sendFlag = false;
		synchronized (state) {
			if (state == State.RECORDING) {
				sendFlag = true;
				state = State.IDLE;
			} else if (state == State.RECORDING_SHOW_CANCEL_DIALOG) {
				state = State.IDLE;
				return;
			}
			
		}
		
		duration = (System.currentTimeMillis() - duration);
		if (duration < 1500) {
			accFile.deleteOnExit();
			Message.obtain(uiHandler, UI_MSG_SHOW_DIALOG,
					DIALOG_TYPE_SHORT_DURATION, 0).sendToTarget();
			Message m = Message.obtain(uiHandler, UI_MSG_DISMISS_DIALOG,
					DIALOG_TYPE_NONE, 0);
			uiHandler.sendMessageDelayed(m, 1200);
			return;
		} else {
			sendFlag = true;
		}
		
		if (sendFlag) {
			 VMessage vm = new VMessage(0, 0, GlobalHolder.getInstance()
			 .getCurrentUser(), chatUser, new Date());
			 new VMessageAudioItem(vm, uuid, null , "aac", (int)duration, 0);
			 messageService.sendMessage(vm, chatUser);
			 itemList.add(buildItem(vm));
			 notifyUIScroller(true);
		}
		

	}

	@Override
	public void onError(Throwable e) {
		synchronized (state) {
			state = State.IDLE;
		}
		boolean ret = closeAACFile();
		if (!ret) {
			// TODO notify user
		} else {

		}

		duration = 0;
		V2Log.e("=====error on record , close file aac file " + ret + "  file:"
				+ accFile + "  " + e);
	}

	@Override
	public void onDBChanged(double db) {
		// TODO Auto-generated method stub
		ui.updateVoiceDBLevel(2);
	}

	@Override
	public void onAACDataOutput(byte[] data, int len) {
		if (state == State.RECORDING
				|| state == State.RECORDING_SHOW_CANCEL_DIALOG) {
			if (!writeAACData(data, len)) {
				// write error
			}
		}

	}

	private boolean openAACFile() {
		try {
			uuid = UUID.randomUUID().toString();
			accFile = new File(GlobalConfig.getGlobalAudioPath() + "/" + uuid
					+ ".aac");
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
		// TODO save to file
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

	// ////////////////AACEncoderNotification////////////////////////

	// ////////////////DecoderNotification////////////////////////

	public void onDecodeFinish() {
		synchronized (state) {
			state = State.IDLE;
		}
		// TODO update message state
	}

	public void onDecodeStart() {
		synchronized (state) {
			state = State.DECODING;
		}
	}

	public void onDecodeError(Throwable e) {

	}

	// ////////////////DecoderNotification////////////////////////

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
			Item item = itemList.get(position);
			ui.updateView(convertView, item.type, item.avatar, item.content,
					item.msgType, item.isPlaying, item);
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
			List<VMessage> list = messageService.getVMList(
					chatUser.getmUserId(), start, page);
			for (VMessage m : list) {
				itemList.add(0, buildItem(m));
			}

			notifyUIScroller(false);
		}

	}

	class UIHandler extends Handler {

		WeakReference<P2PMessagePresenterUI> wui;

		public UIHandler(P2PMessagePresenterUI ppui) {
			super();
			this.wui = new WeakReference<P2PMessagePresenterUI>(ppui);
		}

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UI_MSG_SHOW_DIALOG:
				if (wui.get() != null) {
					wui.get().showDialog(true, msg.arg1);
				}
				break;
			case UI_MSG_DISMISS_DIALOG:
				if (wui.get() != null) {
					wui.get().showDialog(false, msg.arg1);
				}
				break;
			}
		}

	}

	class Item {
		long id;
		int type;
		int msgType;
		Bitmap avatar;
		CharSequence content;
		String path;
		boolean isPlaying;
		VMessage vm;
	}

	enum State {
		RECORDING, RECORDING_SHOW_CANCEL_DIALOG, DECODING, IDLE,
	}

}
