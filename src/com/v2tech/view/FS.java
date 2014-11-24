package com.v2tech.view;

import v2av.VideoPlayer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.v2tech.service.GlobalHolder;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Group;
import com.v2tech.vo.UserDeviceConfig;

public class FS extends Activity {

	private static final int UPDATE_UI = 1;

	private SurfaceView mSV;

	private Group g;
	private long uid;
	private String devID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fs);
		mSV = (SurfaceView) findViewById(R.id.sr);
		g = GlobalHolder.getInstance().getGroupById(getIntent().getLongExtra("gid", 0));
		uid =getIntent().getLongExtra("uid", 0);
		devID =getIntent().getExtras().getString("devid");
		
		VideoPlayer vp = new VideoPlayer();
		vp.SetSurface(mSV.getHolder());
		mSV.getHolder().addCallback(new Callback(vp));
		
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
		Intent wr = new Intent("fs.exit");
		wr.addCategory("liveshow");
		this.sendBroadcast(wr);
	}
	
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	
	
	class Callback implements SurfaceHolder.Callback {

		ST st = ST.CLOSED;
		VideoPlayer vp;
		public Callback(VideoPlayer vp) {
			super();
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
			if (g != null) {
				UserDeviceConfig ud= GlobalHolder.getInstance().getUserDefaultDevice(uid);
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
			if (g != null) {
				UserDeviceConfig ud= GlobalHolder.getInstance().getUserDefaultDevice(uid);
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
