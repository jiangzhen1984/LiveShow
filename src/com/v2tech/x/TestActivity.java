package com.v2tech.x;

import java.io.IOException;

import v2av.VideoRecorder;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.service.ConferenceService;
import com.v2tech.service.GlobalHolder;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.UserDeviceConfig;
import com.v2tech.widget.VideoShareBtnLayout.VideoShareBtnLayoutListener;

public class TestActivity extends Activity {

	
	private FrameLayout fl;
	private MapView mMapView;
	
	private ConferenceService vs;
	
	private Camera camera;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vs = new ConferenceService();
		this.setContentView(R.layout.main_activity_x);
		fl = (FrameLayout) findViewById(R.id.main_map);
		
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(this, mapOptions);
		
		FrameLayout.LayoutParams flpar = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		
		fl.addView(mMapView, flpar);
		final WidgetRootLayout wrl =  (WidgetRootLayout)findViewById(R.id.main_widget);
		camera = Camera.open();
		
		wrl.bringToFront();
		wrl.viedeoShartBtnLayout.setListener(new VideoShareBtnLayoutListener() {

			@Override
			public void onVideoSharedBtnClicked(View v) {
//				UserDeviceConfig duc = new UserDeviceConfig(4,
//						1, GlobalHolder.getInstance()
//								.getCurrentUserId(), "", null);
//				VideoRecorder.VideoPreviewSurfaceHolder = wrl.mholder1;
//				VideoRecorder.VideoPreviewSurfaceHolder
//						.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//				vs.requestOpenVideoDevice(duc, null);
				if (v.getTag() == null || v.getTag().equals("stop")) {
					try {
						camera.setPreviewDisplay(wrl.mholder1);
					} catch (IOException e) {
						e.printStackTrace();
					}
					camera.startPreview();
					v.setTag("start");
				} else {
					v.setTag("stop");
					camera.stopPreview();
				}

			}

			@Override
			public void onMapShareBtnClicked(View v) {
				
			}
			
		});
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}

	
	
}
