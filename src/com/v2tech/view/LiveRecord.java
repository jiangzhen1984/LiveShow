package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
				} else if (mState == LocalState.RECORDING) {
					mState = LocalState.STOPPED;
					mCameraView.stopPublish();
					VideoBCRequest.getInstance().stopLive();
					mRecordButton.setText(R.string.live_video_start_record);
				}
			}
			
		});
	}
	
	private static final int RECORDING = 1;
	
	private Handler mLocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECORDING:
				if (VideoBCRequest.getInstance().url == null) {
					Message dm = obtainMessage(RECORDING);
					this.sendMessageDelayed(dm, 300);
				} else {
					mCameraView.publishUrl = "rtmp://118.145.28.194/vod/"+VideoBCRequest.getInstance().url;
					mCameraView.startPublish();
				}
				break;
			}
		}
		
	};

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	enum LocalState{
		STOPPED, RECORDING, PAUSED
	}
}
