package com.v2tech.service.jni;

import java.util.List;

import com.v2tech.vo.VCrowdFile;

/**
 * Used to wrap response data from JNI when receive call from JNI
 * 
 * @author 28851274
 * 
 */
public class RequestFetchGroupFilesResponse extends JNIResponse {

	List<VCrowdFile> list;

	/**
	 * This class is wrapper that wrap response of chat service
	 * 
	 * @param result
	 *            {@link Result}
	 */
	public RequestFetchGroupFilesResponse(Result result) {
		super(result);
	}

	public List<VCrowdFile> getList() {
		return list;
	}

	public void setList(List<VCrowdFile> list) {
		this.list = list;
	}
	
	
}
