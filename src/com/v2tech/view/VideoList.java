package com.v2tech.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.v2tech.v2liveshow.R;

public class VideoList extends Activity {

	private static final int UPDATE_UI = 1;

	private FrameLayout mMain;

	private SurfaceView mSV1;
	private SurfaceView mSV2;
	private SurfaceView mSV3;
	private SurfaceView mSV4;
	private SurfaceView mSV5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);
		mMain = (FrameLayout) findViewById(R.id.main);
		Message msg = Message.obtain(mLocalHandler, UPDATE_UI);
		mLocalHandler.sendMessageDelayed(msg, 200);
	}

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
	}

	private void updateUI() {
		int width = mMain.getWidth();
		width = width - width % 16;
		int height = width / 4 * 3;
		height = height - height % 16;
		// add surface v1
		mSV1 = new SurfaceView(this);
		mSV1.setBackgroundColor(Color.RED);
		FrameLayout.LayoutParams sv1Fl = new FrameLayout.LayoutParams(width,
				height);
		sv1Fl.leftMargin = (mMain.getWidth() - width) / 2;
		sv1Fl.topMargin = 10;
		sv1Fl.bottomMargin = 10;
		mMain.addView(mSV1, sv1Fl);

		// add banner
		View v = this.getLayoutInflater().inflate(R.layout.banner, null);
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		FrameLayout.LayoutParams bannerFl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		bannerFl.topMargin = height + 20;
		mMain.addView(v, bannerFl);

		width = width / 2;
		int rheight = width / 4 * 3;
		rheight = rheight - rheight % 16;
		mSV2 = new SurfaceView(this);
		mSV2.setBackgroundColor(Color.GRAY);
		FrameLayout.LayoutParams sv2Fl = new FrameLayout.LayoutParams(width,
				height);
		sv2Fl.leftMargin = (mMain.getWidth() - width * 2) / 2;
		sv2Fl.topMargin = height + v.getMeasuredHeight() + 20 + 10;
		sv2Fl.bottomMargin = 10;
		mMain.addView(mSV2, sv2Fl);

		mSV3 = new SurfaceView(this);
		mSV3.setBackgroundColor(Color.BLUE);
		FrameLayout.LayoutParams sv3Fl = new FrameLayout.LayoutParams(width,
				height);
		sv3Fl.leftMargin = mMain.getWidth() / 2 + sv2Fl.leftMargin;
		sv3Fl.topMargin = height + v.getMeasuredHeight() + 20 + 10;
		sv3Fl.bottomMargin = 10;
		mMain.addView(mSV3, sv2Fl);

		mSV4 = new SurfaceView(this);
		mSV4.setBackgroundColor(Color.BLACK);
		FrameLayout.LayoutParams sv4Fl = new FrameLayout.LayoutParams(width,
				height);
		sv4Fl.leftMargin = (mMain.getWidth() - width * 2) / 2;
		sv4Fl.topMargin = height + v.getHeight() + 20 + 20 + rheight;
		sv4Fl.bottomMargin = 10;
		mMain.addView(mSV4, sv4Fl);

		mSV5 = new SurfaceView(this);
		mSV5.setBackgroundColor(Color.LTGRAY);
		FrameLayout.LayoutParams sv5Fl = new FrameLayout.LayoutParams(width,
				height);
		sv5Fl.leftMargin = mMain.getWidth() / 2 + sv2Fl.leftMargin;
		sv5Fl.topMargin = height + v.getHeight() + 20 + 20 + rheight;
		sv5Fl.bottomMargin = 10;
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

}
