package com.v2tech.view;

import java.util.ArrayList;
import java.util.List;

import v2av.VideoPlayer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.v2tech.service.GlobalHolder;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Group;
import com.v2tech.vo.User;
import com.v2tech.vo.UserDeviceConfig;

public class VideoList extends Activity {

	private static final int UPDATE_UI = 1;

	private FrameLayout mMain;

	private SurfaceView mSV1;
	private SurfaceView mSV2;
	private SurfaceView mSV3;
	private SurfaceView mSV4;
	private SurfaceView mSV5;
	private Group g;
	private List<User> list = new ArrayList<User>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);
		mMain = (FrameLayout) findViewById(R.id.main);
		Message msg = Message.obtain(mLocalHandler, UPDATE_UI);
		mLocalHandler.sendMessageDelayed(msg, 200);
		g = GlobalHolder.getInstance().getGroupById(getIntent().getLongExtra("gid", 0));
		if (g != null) {
			list.addAll(g.getUsers());
			for (User u : list) {
				if (u.getAccount().equals("v1") || u.getName().equals("v1")) {
					list.remove(u);
					break;
				}
			}
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("fs.exit");
		filter.addCategory("liveshow");
		this.registerReceiver(local, filter);
		
	}
	
	
	
	private BroadcastReceiver local = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (cc != null) {
				handle.postDelayed(new Runnable() {

					@Override
					public void run() {
						long uid = list.get(cc.index).getmUserId();
						UserDeviceConfig ud1= GlobalHolder.getInstance().getUserDefaultDevice(uid);
						if (ud1 != null) {
						UserDeviceConfig ud = new UserDeviceConfig(g.getGroupType().intValue(), g.getmGId(), uid, ud1.getDeviceID(), cc.vp);
						GlobalHolder.getInstance().cs.requestOpenVideoDevice(g,
								ud, null);
						cc = null;
						}
					}
					
				}, 600);
			}
		}
		
	};

	@Override
	protected void onStart() {
		super.onStart();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(local);
	}
	
	

	@Override
	public void onBackPressed() {
		this.setResult(100);
		finish();
	}

	private void updateUI() {
		int width = mMain.getWidth();
		if (width <= 0) {
			mMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			width = mMain.getMeasuredWidth();
			if (width <= 0) {
				mMain.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
				width = mMain.getMeasuredWidth();
			}
		}
		width = width - width % 16;
		int height = width / 4 * 3;
		height = height - height % 16;
		// add surface v1
		mSV1 = new SurfaceView(this);
		VideoPlayer vp1 = new VideoPlayer();
		vp1.SetSurface(mSV1.getHolder());
		vp1.SetViewSize(width, height);
		Callback cl1 = new Callback(0, vp1);
		mSV1.setTag(cl1);
		mSV1.getHolder().addCallback(cl1);
		mSV1.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		FrameLayout.LayoutParams sv1Fl = new FrameLayout.LayoutParams(width,
				height);
		sv1Fl.leftMargin = (mMain.getMeasuredWidth() - width) / 2;
		sv1Fl.topMargin = 10;
		sv1Fl.bottomMargin = 10;
		mSV1.setOnClickListener(lis);
		mMain.addView(mSV1, sv1Fl);

		// add banner
		View v = this.getLayoutInflater().inflate(R.layout.banner, null);
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		FrameLayout.LayoutParams bannerFl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		bannerFl.topMargin = height + 20;
		mMain.addView(v, bannerFl);

		
		int restHeight = mMain.getHeight();
		if (restHeight <= 0) {
			restHeight = mMain.getMeasuredHeight();
		}
		
		restHeight = restHeight -  height - 20 - v.getMeasuredHeight();
		
		width = width / 2;
		int rheight = width / 4 * 3;
		rheight = rheight - rheight % 16;
		
		if (rheight * 2 > restHeight) {
			rheight = restHeight /2;
			rheight = rheight - rheight % 16;
			width = rheight / 3 * 4;
			width = width - width % 16;
		}
		
		mSV2 = new SurfaceView(this);
		VideoPlayer vp2 = new VideoPlayer();
		vp2.SetSurface(mSV2.getHolder());
		vp2.SetViewSize(width, rheight);
		mSV2.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		Callback cl2 = new Callback(1, vp2);
		mSV2.setTag(cl2);
		mSV2.getHolder().addCallback(cl2);
		FrameLayout.LayoutParams sv2Fl = new FrameLayout.LayoutParams(width,
				rheight);
		sv2Fl.leftMargin = (mMain.getMeasuredWidth() - width * 2) / 4;
		sv2Fl.topMargin = height + v.getMeasuredHeight() + 20 + 10;
		sv2Fl.bottomMargin = 10;
		mSV2.setOnClickListener(lis);
		mMain.addView(mSV2, sv2Fl);

		mSV3 = new SurfaceView(this);
		VideoPlayer vp3 = new VideoPlayer();
		vp3.SetSurface(mSV3.getHolder());
		vp3.SetViewSize(width, rheight);
		mSV3.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		Callback cl3 = new Callback(2, vp3);
		mSV3.setTag(cl3);
		mSV3.getHolder().addCallback(cl3);
		FrameLayout.LayoutParams sv3Fl = new FrameLayout.LayoutParams(width,
				rheight);
		sv3Fl.leftMargin = width +  sv2Fl.leftMargin * 3;
		sv3Fl.topMargin = height + v.getMeasuredHeight() + 20 + 10;
		sv3Fl.bottomMargin = 10;
		mSV3.setOnClickListener(lis);
		mMain.addView(mSV3, sv3Fl);

		mSV4 = new SurfaceView(this);
		VideoPlayer vp4 = new VideoPlayer();
		vp4.SetSurface(mSV4.getHolder());
		vp4.SetViewSize(width, rheight);
		mSV4.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		Callback cl4 = new Callback(3, vp4);
		mSV4.setTag(cl4);
		mSV4.getHolder().addCallback(cl4);
		FrameLayout.LayoutParams sv4Fl = new FrameLayout.LayoutParams(width,
				rheight);
		sv4Fl.leftMargin = (mMain.getMeasuredWidth() - width * 2) / 4;
		sv4Fl.topMargin = height + v.getMeasuredHeight() + 20 + 20 + rheight;
		sv4Fl.bottomMargin = 10;
		mSV4.setOnClickListener(lis);
		mMain.addView(mSV4, sv4Fl);

		mSV5 = new SurfaceView(this);
		VideoPlayer vp5 = new VideoPlayer();
		vp5.SetSurface(mSV5.getHolder());
		vp5.SetViewSize(width, rheight);
		mSV5.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		Callback cl5 = new Callback(4, vp5);
		mSV5.setTag(cl5);
		mSV5.getHolder().addCallback(cl5);
		
		FrameLayout.LayoutParams sv5Fl = new FrameLayout.LayoutParams(width,
				rheight);
		sv5Fl.leftMargin = width +  sv4Fl.leftMargin * 3;
		sv5Fl.topMargin = height + v.getMeasuredHeight() + 20 + 20 + rheight;
		sv5Fl.bottomMargin = 10;
		mSV5.setOnClickListener(lis);
		mMain.addView(mSV5, sv5Fl);
	}

	private Handler mLocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI:
				updateUI();
				break;
			}
		}

	};
	
	private Callback cc;
	
	private Handler handle = new Handler();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		handle.postDelayed(new Runnable() {

			@Override
			public void run() {
				long uid = list.get(cc.index).getmUserId();
				UserDeviceConfig ud1= GlobalHolder.getInstance().getUserDefaultDevice(uid);
				if (ud1 != null) {
				UserDeviceConfig ud = new UserDeviceConfig(g.getGroupType().intValue(), g.getmGId(), uid, ud1.getDeviceID(), cc.vp);
				GlobalHolder.getInstance().cs.requestOpenVideoDevice(g,
						ud, null);
				}
			}
			
		}, 600);
		
	}

	private View.OnClickListener lis = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Callback cl = (Callback)v.getTag();
			long uid = list.get(cl.index).getmUserId();
			UserDeviceConfig ud= GlobalHolder.getInstance().getUserDefaultDevice(uid);
			
			if (ud != null) {
				cc = cl;
				
				GlobalHolder.getInstance().cs.requestCloseVideoDevice(g,
						ud, null);
				Intent i = new Intent();
				i.setClass(getApplicationContext(), FS.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("gid", g.getmGId());
				i.putExtra("uid", uid);
				i.putExtra("devid", ud.getDeviceID());
				startActivity(i);
			}
			
		}
		
	};
	
	class Callback implements SurfaceHolder.Callback {

		int index;
		ST st = ST.CLOSED;
		VideoPlayer vp;
		public Callback(int index, VideoPlayer vp) {
			super();
			this.index = index;
			this.vp = vp;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			synchronized (st) {
				if (st == ST.CLOSED) {
					if (openDevice()) {
						st = ST.OPENED;
					}
				}
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			synchronized (st) {
				if (st == ST.CLOSED) {
					if (openDevice()) {
						st = ST.OPENED;
					}
				}
			}
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			synchronized (st) {
				if (st == ST.OPENED) {
					closeDevice();
					st = ST.CLOSED;
				}
			}
		}
		
		private boolean openDevice() {
			if (list.size() > index && g != null) {
				UserDeviceConfig ud= GlobalHolder.getInstance().getUserDefaultDevice(list.get(index).getmUserId());
				if (ud != null) {
					ud.setGroupID(g.getmGId());
					ud.setVp(vp);
					GlobalHolder.getInstance().cs.requestOpenVideoDevice(g, ud, null);
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
		
		private void closeDevice() {
			if (list.size() > index && g != null) {
				UserDeviceConfig ud= GlobalHolder.getInstance().getUserDefaultDevice(list.get(index).getmUserId());
				if (ud != null) {
					ud.setGroupID(g.getmGId());
					ud.setVp(vp);
					GlobalHolder.getInstance().cs.requestCloseVideoDevice(g, ud, null);
				}
			}
		}
		
	}
	
	
	enum ST {
		OPENED, CLOSED
	}

}
