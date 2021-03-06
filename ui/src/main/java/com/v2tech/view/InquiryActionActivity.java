package com.v2tech.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.R;
import com.v2tech.map.MapAPI;
import com.v2tech.map.MapLocation;
import com.v2tech.map.baidu.BaiduMapImpl;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.InquiryActionPresenter;
import com.v2tech.presenter.InquiryActionPresenter.InquiryActionPresenterUI;
import com.v2tech.vo.inquiry.InquiryData;

public class InquiryActionActivity extends BaseActivity implements InquiryActionPresenterUI, OnClickListener {
	
	
	private InquiryActionPresenter presenter;
	private View acceptBtn;
	private View audioBtn;
	private View videoBtn;
	private FrameLayout mapRoot;
	private MapView mapView;
	private MapAPI mapApi;
	private EditText addressText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inquiry_action_layout);
		acceptBtn = findViewById(R.id.inquiry_action_accept_btn);
		audioBtn = findViewById(R.id.inquiry_audio_btn);
		videoBtn = findViewById(R.id.inquiry_video_share_btn);
		mapRoot = (FrameLayout)findViewById(R.id.inquiry_action_layout_map_root);
		addressText = (EditText)findViewById(R.id.inquiry_map_location_tips_search_text_et);
		
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mapView = new MapView(this, mapOptions);
		mapRoot.addView(mapView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		
		
		findViewById(R.id.title_bar_logo).setVisibility(View.VISIBLE);
		findViewById(R.id.title_bar_center_tv).setVisibility(View.GONE);
		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		acceptBtn.setOnClickListener(this);
		audioBtn.setOnClickListener(this);
		videoBtn.setOnClickListener(this);
		
		mapApi = new BaiduMapImpl(mapView.getMap(), mapView);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new InquiryActionPresenter(this, this);
		}
		return presenter;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.inquiry_action_accept_btn:
			presenter.acceptBtnClicked(v);
			break;
		case R.id.inquiry_audio_btn:
			presenter.audioBtnClicked(v);
			break;
		case R.id.inquiry_video_share_btn:
			presenter.videoShareBtnClicked(v);
			break;
		case R.id.title_bar_left_btn:
			presenter.returnBtnClicked(v);
			break;
		}
	}


	@Override
	public MapAPI getMap() {
		return mapApi;
	}


	@Override
	public MapLocation getTargetLocation() {
		double lat = getIntent().getDoubleExtra("lat", 0.0);
		double lng = getIntent().getDoubleExtra("lng", 0.0);
		return mapApi.buildLocation(lat, lng);
	}


	@Override
	public void showBtn(boolean acceptBtn, boolean audioBtn, boolean videoBtn) {
		this.acceptBtn.setVisibility(acceptBtn ? View.VISIBLE : View.GONE);
		this.audioBtn.setVisibility(audioBtn ? View.VISIBLE : View.GONE);
		this.videoBtn.setVisibility(videoBtn ? View.VISIBLE : View.GONE);
	}

	@Override
	public void showTargetAddress(String str) {
		addressText.setText(str);
	}


	@Override
	public InquiryData getInquiryDataFromUI() {
		return (InquiryData)getIntent().getExtras().get("inquiry");
	}



	private Toast t;
	@Override
	public void showWaitingLocation() {
		if (t != null) {
			t.cancel();
			t = null;
		}

		t = Toast.makeText(this, R.string.inquiry_accept_error_no_location, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}


	public void quit() {
		finish();
	}
}
