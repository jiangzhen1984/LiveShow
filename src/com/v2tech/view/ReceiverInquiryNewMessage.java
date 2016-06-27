package com.v2tech.view;

import com.V2.jni.util.V2Log;
import com.v2tech.service.InquiryService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverInquiryNewMessage extends BroadcastReceiver {



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
			
			V2Log.i(" inquiry :" + irid+"   userid:" + iuid+"  award:"+ award+"  lat:"+ lat+"   lng:"+lng);
			InquiryService.saveNewInquiry(context.getApplicationContext(), irid, iuid, award, lat, lng, null);
		}
	}
	
	
	
}
