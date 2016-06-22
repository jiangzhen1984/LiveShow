package com.v2tech.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.v2liveshow.R;

public class P2PVideoActivity extends BaseActivity {
	
	private View ringingView;
	private View waitingView;
	private View connectionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p2p_video_connection);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public BasePresenter getPresenter() {
		return null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//TODO reject another quest
	}
	
	

}
