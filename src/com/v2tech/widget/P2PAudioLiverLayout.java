package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.v2liveshow.R;

public class P2PAudioLiverLayout extends RelativeLayout {

	private View declineBtn;
	private LinearLayout mapViewLayout;
	private MapView mMapView;
	private View mapReturnBtn;

	private P2PAudioLiverLayoutListener listener;

	public P2PAudioLiverLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public P2PAudioLiverLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public P2PAudioLiverLayout(Context context) {
		super(context);
	}

	private void init() {
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(getContext(), mapOptions);
		mapViewLayout.addView(mMapView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.p2p_audio_liver_decline_btn) {
			declineBtn = child;
			declineBtn.setOnClickListener(clickListener);
		} else if (child.getId() == R.id.p2p_audio_liver_map_ly) {
			mapViewLayout = (LinearLayout) child;
		} else if (child.getId() == R.id.p2p_map_return_btn) {
			this.mapReturnBtn = child;
			this.mapReturnBtn.setOnClickListener(clickListener);
		}
	}



	public BaiduMap getWatcherMapInstance() {
		return mMapView.getMap();
	}

	public void setListener(P2PAudioLiverLayoutListener listener) {
		this.listener = listener;
	}

	public interface P2PAudioLiverLayoutListener {
		public void onDeclineBtn(View view);
		public void onMapReturnBtn(View view);

	}
	
	
	

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			if (mMapView == null) {
				init();
			}
			mMapView.onResume();
		} else {
			if (mMapView != null) {
				mMapView.onPause();
				mapViewLayout.removeAllViews();
				mMapView = null;
			}
			
		}
	}
	
	
	
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.p2p_audio_liver_decline_btn:
				listener.onDeclineBtn(v);
				break;
			case R.id.p2p_map_return_btn:
				listener.onMapReturnBtn(v);
				break;
			}
		}

	};
	

}

