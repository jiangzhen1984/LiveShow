package com.v2tech.view;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.V2.jni.util.V2Log;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.InquiryAcceptenceHandler;
import com.v2tech.service.InquiryService;
import com.v2tech.vo.User;
import com.v2tech.vo.inquiry.InquiryData;

public class ReceiverInquiryHandler extends BroadcastReceiver {



	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String subAction = intent.getExtras().getString("sub");
		if ("com.v2tech.inquiry_new".equals(subAction)) {
			long irid = intent.getLongExtra("irid", -1);
			long iuid = intent.getLongExtra("iuid", -1);
			float award = intent.getFloatExtra("award", 0);
			double lat = intent.getDoubleExtra("lat", 0);
			double lng = intent.getDoubleExtra("lng", 0);
			String desc = intent.getStringExtra("desc");
			
			V2Log.i(" inquiry :" + irid+"   userid:" + iuid+"  award:"+ award+"  lat:"+ lat+"   lng:"+lng+"  desc:"+ desc);
			InquiryService.saveNewInquiry(context.getApplicationContext(), irid, iuid, award, lat, lng, desc);
		} else if ("com.v2tech.inquiry_accept".equals(subAction)) {
			
			long irid = intent.getLongExtra("irid", -1);
			long auid = intent.getLongExtra("auid", -1);
			double tlat = intent.getDoubleExtra("tlat", 0);
			double tlng = intent.getDoubleExtra("tlng", 0);
			double slat = intent.getDoubleExtra("slat", 0);
			double slng = intent.getDoubleExtra("slng", 0);
			
			
			User answer = new User(auid);
			InquiryData id = new InquiryData();
			id.targetLat = tlat;
			id.targetLng = tlng;
			id.sourceLat = slat;
			id.sourceLng = slng;
			id.answer = answer;
			List<InquiryAcceptenceHandler> list = GlobalPresenterManager.getInstance().getInquiryAcceptenceHandler();
			if (list != null) {
				for (InquiryAcceptenceHandler h : list) {
					h.onTake(answer, id);
				}
			} else {
				//TODO start activity for new user take this
			}
			
			
			
		}
	}
	
	
	
}
