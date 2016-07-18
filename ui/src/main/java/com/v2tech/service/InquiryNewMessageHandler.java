package com.v2tech.service;

public interface InquiryNewMessageHandler {
	
	public void handleNewInquiry(long inquiryId, long userId, float award,
			double lat, double lng, String desc);

}
