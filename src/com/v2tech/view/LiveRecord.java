package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.V2.jni.ImRequest;
import com.V2.jni.V2ClientType;
import com.V2.jni.V2GlobalEnum;
import com.V2.jni.VideoBCRequest;
import com.example.camera.CameraView;
import com.v2tech.v2liveshow.R;

public class LiveRecord extends Activity {

	private Button mRecordButton;
	private ViewGroup root;
	private CameraView mCameraView;
	private LocalState mState = LocalState.STOPPED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_video);
		
		ImRequest.getInstance().login("dssfw", "111111",
				V2GlobalEnum.USER_STATUS_ONLINE, V2ClientType.ANDROID, true);

		root = (ViewGroup) findViewById(R.id.live_record_root_container);
		mCameraView = new CameraView(this);
		

		root.addView(mCameraView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		mRecordButton = (Button) findViewById(R.id.record_action_button);
		mRecordButton.bringToFront();
		
		mRecordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mState == LocalState.STOPPED) {
					mState = LocalState.RECORDING;
					VideoBCRequest.getInstance().startLive();
					mLocalHandler.sendEmptyMessage(RECORDING);
					mRecordButton.setText(R.string.live_video_stop_recording);
					getWindow()
					.addFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				} else if (mState == LocalState.RECORDING) {
					mState = LocalState.STOPPED;
					mCameraView.stopPublish();
					VideoBCRequest.getInstance().stopLive();
					mRecordButton.setText(R.string.live_video_start_record);
					getWindow()
					.clearFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}
			}
			
		});
	}
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Message m = Message.obtain(mLocalHandler, INIT);
		mLocalHandler.sendMessageDelayed(m, 3000);
	}




	private static final int RECORDING = 1;
	private static final int INIT = 2;
	
	private Handler mLocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECORDING:
				if (VideoBCRequest.getInstance().url == null) {
					Message dm = obtainMessage(RECORDING);
					this.sendMessageDelayed(dm, 300);
				} else {
					String uuid = null;
					int index = VideoBCRequest.getInstance().url.indexOf("file=");
					if (index != -1) {
						uuid = VideoBCRequest.getInstance().url.substring(index + 5);
					}
					mCameraView.publishUrl = "rtmp://118.145.28.194/vod/"+uuid;
					mCameraView.startPublish();
				}
				break;
			case INIT:
				mCameraView.startPreView();
				break;
			}
		}
		
	};

	
	
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		mState = LocalState.STOPPED;
		mCameraView.stopPublish();
		VideoBCRequest.getInstance().stopLive();
		mRecordButton.setText(R.string.live_video_start_record);
		getWindow()
		.clearFlags(
				android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	enum LocalState{
		STOPPED, RECORDING, PAUSED
	}
}
