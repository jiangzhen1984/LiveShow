package com.v2tech.view;

import v2av.VideoPlayer;
import v2av.VideoRecorder;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.P2PVideoPresenter;
import com.v2tech.presenter.P2PVideoPresenter.P2PVideoPresenterUI;
import com.v2tech.R;

public class P2PVideoActivity extends BaseActivity implements P2PVideoPresenterUI, OnClickListener {
	
	private View ringingView;
	private View connectionView;
	private View userWidgetView;
	
	private SurfaceView remoteUserVideoView;
	private SurfaceView localUserVideoView;
	
	private View videoConnectionHangoffBtn;
	private View videoConnectionSwitchCameraBtn;
	private View videoWaitingHangoffBtn;
	private View videoRingingHangoffBtn;
	private View videoRingingAcceptBtn;
	
	private VideoPlayer remoteVideoPlayer;
	
	private P2PVideoPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p2p_video_connection);
		connectionView = findViewById(R.id.p2p_video_calling_connection_layout);
		ringingView = findViewById(R.id.p2p_video_ringing_layout);
		userWidgetView = findViewById(R.id.p2p_video_waiting_user_view);
		remoteUserVideoView = (SurfaceView)findViewById(R.id.p2p_video_remote_view);
		localUserVideoView = (SurfaceView)findViewById(R.id.p2p_video_local_view);
		
		videoConnectionHangoffBtn = findViewById(R.id.p2p_video_connection_hangoff_btn);
		videoConnectionSwitchCameraBtn = findViewById(R.id.p2p_video_connection_switch_camera_btn);
		videoWaitingHangoffBtn = findViewById(R.id.p2p_video_waiting_hangoff_btn);
		videoRingingHangoffBtn = findViewById(R.id.p2p_video_ringing_hangoff_btn);
		videoRingingAcceptBtn = findViewById(R.id.p2p_video_ringing_accept_btn);
		
		videoConnectionHangoffBtn.setOnClickListener(this);
		videoConnectionSwitchCameraBtn.setOnClickListener(this);
		videoWaitingHangoffBtn.setOnClickListener(this);
		videoRingingHangoffBtn.setOnClickListener(this);
		videoRingingAcceptBtn.setOnClickListener(this);
		
		remoteVideoPlayer = new VideoPlayer();
		remoteUserVideoView.getHolder().addCallback(remoteVideoPlayer);
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VideoRecorder.VideoPreviewSurfaceHolder = null;
	}

	@Override
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new P2PVideoPresenter(this, this);
			localUserVideoView.getHolder().addCallback(presenter);
		}
		return presenter;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//TODO reject another quest
	}

	@Override
	public int getStartType() {
		return getIntent().getIntExtra("type", 0);
	}

	@Override
	public void showCallingLayout() {
		ringingView.setVisibility(View.GONE);
		connectionView.setVisibility(View.VISIBLE);
		userWidgetView.setVisibility(View.VISIBLE);
		videoConnectionHangoffBtn.setVisibility(View.GONE);
		videoConnectionSwitchCameraBtn.setVisibility(View.GONE);
		videoWaitingHangoffBtn.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void showConnectedLayout() {
		ringingView.setVisibility(View.GONE);
		connectionView.setVisibility(View.VISIBLE);
		userWidgetView.setVisibility(View.GONE);
		videoConnectionHangoffBtn.setVisibility(View.VISIBLE);
		videoConnectionSwitchCameraBtn.setVisibility(View.VISIBLE);
		videoWaitingHangoffBtn.setVisibility(View.GONE);
	}

	@Override
	public void showRingingLayout() {
		ringingView.setVisibility(View.VISIBLE);
		connectionView.setVisibility(View.GONE);
		userWidgetView.setVisibility(View.GONE);
	}
	
	
	public VideoPlayer getRemoteVideoPlayer() {
		return remoteVideoPlayer;
	}
	
	
	public long getUserId() {
		return getIntent().getLongExtra("uid", 0);
	}
	
	public String getUserName() {
		return getIntent().getStringExtra("username");
	}
	
	public String getRingingSession() {
		return getIntent().getStringExtra("session");
	}
	
	public String getDeviceId() {
		return getIntent().getStringExtra("device");
	}
	
	@Override
	public void onClick(View v) {
		int id =v.getId();
		switch (id) {
		case R.id.p2p_video_connection_hangoff_btn:
		case R.id.p2p_video_waiting_hangoff_btn:
		case R.id.p2p_video_ringing_hangoff_btn:
			presenter.onHangoffBtnClicked();
			break;
		case R.id.p2p_video_connection_switch_camera_btn:
			presenter.onSwitchCameraBtnClicked();
			break;
		case R.id.p2p_video_ringing_accept_btn:
			presenter.onAcceptBtnClicked();
			break;
		}
		
	}
	
	
	public void quit() {
		finish();
	}
	
	
}
