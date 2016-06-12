package com.v2tech.service;

import com.v2tech.net.DeamonWorker;
import com.v2tech.net.NotificationListener;
import com.v2tech.net.lv.InquiryIndPacket;
import com.v2tech.net.lv.InquiryReqPacket;
import com.v2tech.net.pkt.IndicationPacket;
import com.v2tech.net.pkt.ResponsePacket;

public class InquiryService extends AbstractHandler {

	
	private LocalNotificationListener listener;
	
	
	public InquiryService() {
		super();
		listener = new LocalNotificationListener();
	}


	public long startInquiry(float amount, double lat, double lng){
		InquiryReqPacket ir = new InquiryReqPacket();
		ir.award = amount;
		ir.currentUserId = GlobalHolder.getInstance().getCurrentUserId();
		ir.type = InquiryReqPacket.TYPE_NEW;
		ResponsePacket rp = DeamonWorker.getInstance().request(ir);
		if (rp.getHeader().isError()) {
			return -1;
		} else {
			DeamonWorker.getInstance().addNotificationListener(listener);
			return System.currentTimeMillis();
		}
	}
	
	
	public void updateInquiryAward(long id, float amount) {
		
	}
	
	public void cancelInquiry(long id) {
		InquiryReqPacket ir = new InquiryReqPacket();
		ir.currentUserId = GlobalHolder.getInstance().getCurrentUserId();
		ir.type = InquiryReqPacket.TYPE_CANCEL;
		
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
