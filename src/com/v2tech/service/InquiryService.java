package com.v2tech.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;

import com.v2tech.db.MessageDescriptor;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NotificationListener;
import com.v2tech.net.lv.InquiryIndPacket;
import com.v2tech.net.lv.InquiryReqPacket;
import com.v2tech.net.lv.InquiryRespPacket;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.ResponsePacket;

public class InquiryService extends AbstractHandler {

	
	private LocalNotificationListener listener;
	
	
	public InquiryService() {
		super();
		listener = new LocalNotificationListener();
	}


	public long startInquiry(float amount, double lat, double lng, String desc){
		InquiryReqPacket ir = new InquiryReqPacket();
		ir.award = amount;
		ir.type = InquiryReqPacket.TYPE_NEW;
		ir.lat = lat;
		ir.lng = lng;
		ir.inquiryUserId = GlobalHolder.getInstance().getCurrentUserId();
		ResponsePacket rp = DeamonWorker.getInstance().request(ir);
		if (rp.getHeader().isError()) {
			return -1;
		} else {
			DeamonWorker.getInstance().addNotificationListener(listener);
			return ((InquiryRespPacket)rp).inquireId;
		}
	}
	
	
	public void updateInquiryAward(long id, float amount) {
		
	}
	
	public void cancelInquiry(long id) {
		InquiryReqPacket ir = new InquiryReqPacket();
		ir.type = InquiryReqPacket.TYPE_CANCEL;
		ir.inquireId = id;
		
		DeamonWorker.getInstance().request(ir);
		DeamonWorker.getInstance().removeNotificationListener(listener);
	}
	
	
	public static void saveNewInquiry(Context ctx, long inquiryId, long userId, float award,
			double lat, double lng, String desc) {
		JSONObject json = new JSONObject();
		try {
			json.put("type", "inquiry");
			json.put("irid", inquiryId);
			json.put("userid", userId);
			json.put("award", award);
			json.put("lat", lat);
			json.put("lng", lng);
			json.put("desc", desc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ContentValues values = new ContentValues();
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TYPE , MessageDescriptor.MessageSession.MES_TYPE_SYSTEM);
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_TIMESTAMP,  System.currentTimeMillis());
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_STATE,  MessageDescriptor.MessageSession.MES_STATE_UNREAD);
		values.put(MessageDescriptor.MessageSession.Cols.SYSTEM_MESSAGE_FROM_USER_NAME,  json.toString());
		ctx.getContentResolver().insert(MessageDescriptor.MessageSession.CONTENT_URI, values);
	}

	@Override
	public void clearCalledBack() {
		DeamonWorker.getInstance().removeNotificationListener(listener);
	}
	
	
	
	class LocalNotificationListener implements  NotificationListener {

		@Override
		public void onNodification(IndicationPacket ip) {
			if (!(ip instanceof InquiryIndPacket)) {
				return;
			}
			
			//TODO handle indication
		}

		@Override
		public void onResponse(ResponsePacket rp) {
			
		}

		@Override
		public void onStateChanged() {
			
		}

		@Override
		public void onTimeout(ResponsePacket rp) {
			
		}
		
	}

}
