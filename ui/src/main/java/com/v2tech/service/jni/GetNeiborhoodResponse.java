package com.v2tech.service.jni;

import java.util.List;

import com.v2tech.vo.Live;

public class GetNeiborhoodResponse extends JNIResponse {
	
	public List<Live> list;

	public GetNeiborhoodResponse(Result res) {
		super(res);
	}

}
