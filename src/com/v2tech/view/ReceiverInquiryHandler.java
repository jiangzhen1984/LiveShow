package com.v2tech.view;

import java.util.List;

import com.V2.jni.util.V2Log;
import com.v2tech.presenter.GlobalPresenterManager;
import com.v2tech.service.InquiryAcceptenceHandler;
import com.v2tech.service.InquiryService;
import com.v2tech.vo.inquiry.InquiryData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverInquiryHandler extends BroadcastReceiver {



	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String subAction = intent.getExtras().getString("sub");
		V2Log.i("=== on receive1 "+ subAction);
		if ("com.v2tech.inquiry_new".equals(subAction)) {
			long irid = intent.getLongExtra("irid", -1);
			long iuid = intent.getLongExtra("iuid", -1);
			float award = intent.getFloatExtra("award", 0);
			double lat = intent.getDoubleExtra("lat", 0D);
			double lng = intent.getDoubleExtra("lng", 0D);
			String desc = intent.getStringExtra("desc");
			
			V2Log.i(" inquiry :" + irid+"   userid:" + iuid+"  award:"+ award+"  lat:"+ lat+"   lng:"+lng+"  desc:"+ desc);
			InquiryService.saveNewInquiry(context.getApplicationContext(), irid, iuid, award, lat, lng, desc);
		} else if ("com.v2tech.inquiry_accept".equals(subAction)) {
			
			InquiryData id  = (InquiryData)intent.getSerializableExtra("data");
			List<InquiryAcceptenceHandler> list = GlobalPresenterManager.getInstance().getInquiryAcceptenceHandler();
			if (list != null) {
				for (InquiryAcceptenceHandler h : list) {
					V2Log.i("===  >" +h);
					h.onTake(id.answer, id);
				}
			} else {
				//TODO start activity for new user take this
			}
			
			
			
		}
	}
	
	
	
}
