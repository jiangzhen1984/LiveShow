package com.v2tech.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.V2.jni.ConfigRequest;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.service.MessageListener;
import com.v2tech.service.UserService;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.util.GlobalConfig;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Conference;
import com.v2tech.vo.ConferenceGroup;
import com.v2tech.vo.Group;
import com.v2tech.vo.Group.GroupType;

public class MainActivity extends Activity {

	private static final int DEFAULT_MAP_LEVEL = 4;

	private static final int LOAD_MAP = 1;
	private static final int UPDATE_MAP = 2;
	private static final int LOGIN_DONE = 3;
	private static final int JOIN_CONFERENCE_DONE = 4;
	private static final int REQUEST_JOIN_CONF = 5;
	private static final int SWITCH_VIDEO = 6;

	private FrameLayout mMainLayout;
	private ImageView mMapBg;
	private Bitmap mMap;
	private boolean isLoadMap;
	private boolean isConnectedServer;
	private MapThreadState mLock = MapThreadState.DONE;
	private ConfState mConfState = ConfState.DONE;

	private ConfigRequest mCR = new ConfigRequest();
	private UserService us = new UserService();
	private ConferenceService cs = new ConferenceService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		mMainLayout = (FrameLayout) findViewById(R.id.main);
		mMapBg = (ImageView) findViewById(R.id.main_background);

		IntentFilter filter = new IntentFilter();
		filter.addCategory(JNIService.JNI_BROADCAST_CATEGROY);
		filter.addAction(JNIService.JNI_BROADCAST_GROUP_NOTIFICATION);

		this.registerReceiver(localReceiver, filter);

		showProgressDialog();
		Message.obtain(LocalHandler, LOAD_MAP,
				new LoadMapObject(0, 0, DEFAULT_MAP_LEVEL, "", ""))
				.sendToTarget();

		new ConnectServerThread().start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(localReceiver);
	}

	private Dialog mDialog = null;

	private void showProgressDialog() {
		mDialog = ProgressDialog.show(this, "", "正在载入.....", false);
	}

	private void doLoadMap(LoadMapObject obj) {
		if (obj.width <= 0 || obj.height <= 0) {
			obj.width = mMainLayout.getWidth();
			obj.height = mMainLayout.getHeight();
		}

		File f = new File(GlobalConfig.getGlobalPath() + "/s" + obj.width + "_"
				+ obj.height + "_" + obj.level + ".png");
		Bitmap bm = null;
		if (f.exists()) {
			bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
					new BitmapFactory.Options());
			Message.obtain(LocalHandler, UPDATE_MAP, bm).sendToTarget();
		} else {
			synchronized (mLock) {
				if (mLock == MapThreadState.DONE) {
					mLock = MapThreadState.LOADING;
					new MapThread(obj.width, obj.height, obj.level, obj.city,
							obj.center).start();
				}
			}
		}

	}

	private void doUpdateMap(Bitmap bm) {
		mMapBg.setScaleType(ScaleType.CENTER_CROP);
		mMapBg.setImageBitmap(bm);
		if (mMap != null && !mMap.isRecycled()) {
			mMap.recycle();
		}
		mMap = bm;
		isLoadMap = true;
	}

	private void doLoginDone(JNIResponse res) {
		if (res.getResult() == JNIResponse.Result.SUCCESS) {
			requestJoinConf();
		}
	}

	private void requestJoinConf() {
		List<Group> list = GlobalHolder.getInstance().getGroup(
				Group.GroupType.CONFERENCE.intValue());
		if (list.size() > 0) {
			Group g = list.get(0);
			Message.obtain(LocalHandler, REQUEST_JOIN_CONF,
					new Conference((ConferenceGroup) g)).sendToTarget();
		} else {

		}
	}

	private void doJoinConfDone(JNIResponse res) {
		if (res.getResult() == JNIResponse.Result.SUCCESS) {
			isConnectedServer = true;
			//
			if (mDialog != null) {
				mDialog.dismiss();
			}
			
			refreshVideoUI();
		} else {

		}
	}

	private void refreshVideoUI() {
		RelativeLayout videoLayout = new RelativeLayout(this);
		int width = mMainLayout.getWidth() / 2;
		width = width - width % 16;
		int height = width / 4 * 3;
		height = height - height % 16;

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(width,
				height);
		fl.leftMargin = mMainLayout.getWidth() - width;
		fl.topMargin = 10;

		SurfaceView sv = new SurfaceView(this);
		sv.setBackgroundColor(Color.BLACK);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		videoLayout.addView(sv, rl);

		ImageView leftImage = new ImageView(this);
		leftImage.setImageResource(R.drawable.arrow_left_gray);
		RelativeLayout.LayoutParams leftRl = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		leftRl.addRule(RelativeLayout.CENTER_VERTICAL);
		leftRl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		leftRl.leftMargin = 15;
		videoLayout.addView(leftImage, leftRl);
		leftImage.bringToFront();
		leftImage.setOnClickListener(switchVideo);
		leftImage.setTag("1");
		
		
		ImageView rightImage = new ImageView(this);
		rightImage.setImageResource(R.drawable.arrow_right_gray);
		RelativeLayout.LayoutParams rigthRl = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rigthRl.addRule(RelativeLayout.CENTER_VERTICAL);
		rigthRl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rigthRl.rightMargin = 15;
		videoLayout.addView(rightImage, rigthRl);
		rightImage.bringToFront();
		rightImage.setOnClickListener(switchVideo);
		rightImage.setTag("0");

		mMainLayout.addView(videoLayout, fl);
	}

	private Handler LocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_MAP:
				doLoadMap((LoadMapObject) msg.obj);
				break;
			case UPDATE_MAP:
				doUpdateMap((Bitmap) msg.obj);
				break;
			case LOGIN_DONE:
				doLoginDone((JNIResponse) msg.obj);
				break;
			case JOIN_CONFERENCE_DONE:
				doJoinConfDone((JNIResponse) msg.obj);
				break;
			case REQUEST_JOIN_CONF:
				if (mConfState == ConfState.DONE) {
					mConfState = ConfState.REQUESTING;
					cs.requestEnterConference((Conference) msg.obj,
							new MessageListener(LocalHandler,
									JOIN_CONFERENCE_DONE, null));
					JNIResponse res = new JNIResponse(JNIResponse.Result.SUCCESS);
					
					Message.obtain(this, JOIN_CONFERENCE_DONE, res).sendToTarget();
				}
				break;
			case SWITCH_VIDEO:
				break;
			}
		}

	};
	
	
	private OnClickListener switchVideo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
		}
		
	};

	private BroadcastReceiver localReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (JNIService.JNI_BROADCAST_GROUP_NOTIFICATION.equals(intent
					.getAction())) {
				int type = intent.getIntExtra("gtype", -1);
				if (type == GroupType.CONFERENCE.intValue()) {
					requestJoinConf();
				}
			}
		}

	};

	class LoadMapObject {
		int width;
		int height;
		int level;
		String city;
		String center;

		public LoadMapObject(int width, int height, int level, String city,
				String center) {
			super();
			this.width = width;
			this.height = height;
			this.level = level;
			this.city = city;
			this.center = center;
		}

	}

	class ConnectServerThread extends Thread {

		@Override
		public void run() {
			mCR.setServerAddress("111.206.87.107", 5123);
			us.login("v1", "111111", new MessageListener(LocalHandler,
					LOGIN_DONE, null));
		}

	}

	class MapThread extends Thread {

		private int width;
		private int height;
		private int level;

		public MapThread(int width, int height, int level, String city,
				String center) {
			super();
			this.width = width;
			this.height = height;
			this.level = level;
		}

		@Override
		public void run() {
			URL url = null;
			OutputStream out = null;
			HttpURLConnection urlConnection = null;
			File f = null;
			try {
				url = new URL(
						"http://api.go2map.com/engine/api/static/image+%7B'height':"
								+ height
								+ ",'width':"
								+ width
								+ ",'zoom':"
								+ level
								+ ",'center':%E2%80%98%E5%8C%97%E4%BA%AC%E2%80%99,'city':'%E5%8C%97%E4%BA%AC'%7D.png");

				urlConnection = (HttpURLConnection) url.openConnection();

				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				f = new File(GlobalConfig.getGlobalPath() + "/s" + width + "_"
						+ height + "_" + level + ".png");

				out = new FileOutputStream(f);
				byte[] buf = new byte[1024];
				int n = 0;
				while ((n = in.read(buf)) > 0) {
					out.write(buf, 0, n);
				}
				in.close();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				urlConnection.disconnect();
				synchronized (mLock) {
					mLock = MapThreadState.DONE;
				}
			}

			Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
					new BitmapFactory.Options());
			Message.obtain(LocalHandler, UPDATE_MAP, bm).sendToTarget();
		}

	}

	enum MapThreadState {
		DONE, LOADING;
	}

	enum ConfState {
		REQUESTING, DONE;
	}

}
