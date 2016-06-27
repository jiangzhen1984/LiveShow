package com.v2tech.service;

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
