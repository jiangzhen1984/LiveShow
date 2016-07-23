package com.v2tech.presenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.V2.jni.util.V2Log;
import com.v2tech.audio.AACEncoder;
import com.v2tech.audio.AACEncoder.AACEncoderNotification;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.UserService;
import com.v2tech.util.GlobalConfig;
import com.v2tech.view.P2PMessageActivity;
import com.v2tech.view.P2PVideoActivity;
import com.v2tech.vo.User;
import com.v2tech.widget.LivePublisherPersonelLayout.InterfactionBtnClickListener;

public class FansFollowPresenter extends BasePresenter implements
		AACEncoderNotification, InterfactionBtnClickListener {

	public static final int TYPE_FANS = 1;
	public static final int TYPE_FOLLOWERS = 2;
	public static final int TYPE_FRIENDS = 3;

	public static final int DIALOG_TYPE_NONE = 0;
	public static final int DIALOG_TYPE_VOLUMN = 1;
	public static final int DIALOG_TYPE_TOUCH_UP_CANCEL = 2;
	public static final int DIALOG_TYPE_LONG_DURATION = 3;
	public static final int DIALOG_TYPE_SHORT_DURATION = 4;

	public static final int UI_MSG_SHOW_DIALOG = 1;
	public static final int UI_MSG_DISMISS_DIALOG = 2;
	public static final int UI_MSG_UPDATE_VOLUMN_LEVEL = 4;

	public interface FansFollowPresenterUI {
		public void finishMainUI();

		public void updateTitleBar();

		public void showBox();

		public Object getIntentData(String key);

		public void showDialog(boolean flag, int type);

		public void updateVoiceDBLevel(int level);

		public void updatePersonelViewData(Bitmap avatar, String name,
				String sign, String location, int vipLevel, int video,
				int fans, int followers, int type);
		
		public void updateBtnText(boolean addFlag);
	}

	private Handler uiHandler;
	private Context context;
	private FansFollowPresenterUI ui;
	private User u;
	private int type;
	private UserService us;

	private AACEncoder aacRecorder;
	private State state = State.IDLE;
	private boolean addFlag;

	public FansFollowPresenter(Context context, FansFollowPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
		u = (User) ui.getIntentData("user");
		type = (Integer) ui.getIntentData("type");
		aacRecorder = new AACEncoder(this);
		uiHandler = new UIHandler(ui);
		us = new UserService();
	}

	@Override
	public void onUICreated() {
		super.onUICreated();
	}
	

	@Override
	public void onUIStarted() {
		super.onUIStarted();
		ui.updateTitleBar();
		ui.showBox();
		ui.updatePersonelViewData(null, u.getName(), u.getSignature(),
				u.location, u.vipLevel, u.videoCount, u.fansCount, u.fansCount,
				type);

		if (type == FansFollowPresenter.TYPE_FANS) {
			addFlag = true;
		} else if (type == FansFollowPresenter.TYPE_FOLLOWERS) {
			addFlag = false;
		} else if (type == FansFollowPresenter.TYPE_FRIENDS) {
			addFlag = false;
		}
	}

	public void friendsBtnClicked() {

	}

	public void followBtnClicked() {

	}

	public void fansBtnClicked() {

	}

	public void settingBtnClicked() {

	}

	public void returnBtnClicked() {
		ui.finishMainUI();
	}

	@Override
	public void onChattingBtnClicked(View v) {
	}

	@Override
	public void onVideoCallBtnClicked(View v) {
		Intent i = new Intent();
		i.putExtra("uid", u.getmUserId());
		i.putExtra("username", u.getName());
		i.putExtra("device", u.getmUserId()+":Camera");
		i.setClass(context, P2PVideoActivity.class);
		context.startActivity(i);

	}

	@Override
	public void onMsgBtnClicked(View v) {
		Intent i = new Intent();
		i.putExtra("chatuserid", u.getmUserId());
		i.setClass(context, P2PMessageActivity.class);
		context.startActivity(i);

	}

	@Override
	public void onAudioBtnPressed(View v) {
		
	}

	@Override
	public void onAudioBtnReleased(View v) {

	}

	public void onFollowBtnClick(View v) {
		List<User> userList = null;
		if (type == TYPE_FOLLOWERS) {
			userList = GlobalHolder.getInstance().mMyFans;
		} else if (type == TYPE_FANS) {
			userList = GlobalHolder.getInstance().mMyFollowers;
		} else if (type == TYPE_FRIENDS) {
			userList = GlobalHolder.getInstance().mMyFriends;
		}

		us.followUser(u, addFlag);
		if (userList != null) {
			if (addFlag) {
				userList.add(u);
			} else {
				userList.remove(u);
			}
		} 
		addFlag = !addFlag;
		
		ui.updateBtnText(addFlag);
	}

	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		us.clearCalledBack();
	}

	@Override
	public void onRecordStart() {
		synchronized (state) {
			state = State.RECORDING;
		}

		boolean ret = openAACFile();
		V2Log.i("=====finish record , open file aac file " + ret + "  file:"
				+ accFile);
		if (!ret) {
			// TODO notify user

			Message.obtain(uiHandler, UI_MSG_DISMISS_DIALOG, DIALOG_TYPE_NONE,
					0).sendToTarget();
			this.aacRecorder.stop();
			return;
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
			synchronized (state) {
				state = State.IDLE;
			}
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

		int type = 1;
		duration = (System.currentTimeMillis() - duration);
		// Check duration is valid or not
		if (duration < 1500) {
			accFile.deleteOnExit();
			// error and notify short duration
			type = 1;
			sendFlag = true;
			Message.obtain(uiHandler, UI_MSG_DISMISS_DIALOG, DIALOG_TYPE_NONE,
					0).sendToTarget();
		} else {
			// success and send message
			type = 2;
			sendFlag = true;
		}

		if (sendFlag) {
			Intent i = new Intent();
			i.putExtra("type", type);
			// i.putExtra("chatuserid", u.getmUserId());
			i.putExtra("audiofile", this.accFile);
			i.setClass(context, P2PMessageActivity.class);
			context.startActivity(i);
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
		int level = 1;
		if (db == Double.NaN) {
			level = 1;
		} else {
			level = (int) db % 10 + 1;
		}
		Message.obtain(uiHandler, UI_MSG_UPDATE_VOLUMN_LEVEL, level, 0)
				.sendToTarget();
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
		if (out == null) {
			return false;
		}
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

	public void onRecordBtnTouchDown(MotionEvent ev) {
		uiHandler.removeMessages(UI_MSG_DISMISS_DIALOG);
		ui.showDialog(true, DIALOG_TYPE_VOLUMN);
		aacRecorder.start();
	}

	public void onRecordBtnTouchUp(MotionEvent ev) {
		aacRecorder.stop();
		ui.showDialog(false, DIALOG_TYPE_VOLUMN);
	}

	public void onRecordBtnTouchMoveOutOfBtn(MotionEvent ev) {
		ui.showDialog(true, DIALOG_TYPE_TOUCH_UP_CANCEL);
		synchronized (state) {
			state = State.RECORDING_SHOW_CANCEL_DIALOG;
		}
	}

	public void onRecordBtnTouchMoveInBtn(MotionEvent ev) {
		ui.showDialog(true, DIALOG_TYPE_VOLUMN);
		synchronized (state) {
			state = State.RECORDING;
		}
	}

	class UIHandler extends Handler {

		WeakReference<FansFollowPresenterUI> wui;

		public UIHandler(FansFollowPresenterUI ppui) {
			super();
			this.wui = new WeakReference<FansFollowPresenterUI>(ppui);
		}

		@Override
		public void handleMessage(Message msg) {
			if (wui.get() == null) {
				V2Log.e(" miss message " + msg.what + "  due to no context ");
				return;
			}
			int what = msg.what;
			switch (what) {
			case UI_MSG_SHOW_DIALOG:
				wui.get().showDialog(true, msg.arg1);
				break;
			case UI_MSG_DISMISS_DIALOG:
				wui.get().showDialog(false, msg.arg1);
				break;
			case UI_MSG_UPDATE_VOLUMN_LEVEL:
				wui.get().updateVoiceDBLevel(msg.arg1);
				break;
			}
		}

	}

	enum State {
		RECORDING, RECORDING_SHOW_CANCEL_DIALOG, DECODING, IDLE,
	}
}
