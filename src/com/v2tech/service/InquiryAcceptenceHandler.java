package com.v2tech.service;

import com.v2tech.vo.User;
import com.v2tech.vo.inquiry.InquiryData;

public interface InquiryAcceptenceHandler {
	
	
	public void onTake(User user, InquiryData data);

}
