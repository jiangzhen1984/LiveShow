package com.v2tech.net.lv;

import java.util.List;

import com.v2tech.net.pkt.ResponsePacket;

public class LiveQueryRespPacket extends ResponsePacket {
	
	int count;
	/**
	 * 0 : id='' 1: userId='' 2:longitude='' 3:latitude='' 4:sum=''
	 */
	List<String[]> videos;
	
	
	
	
	
	public LiveQueryRespPacket() {
		super();
	}





	public LiveQueryRespPacket(int count, List<String[]> videos) {
		super();
		this.count = count;
		this.videos = videos;
	}





	public int getCount() {
		return count;
	}





	public void setCount(int count) {
		this.count = count;
	}





	public List<String[]> getVideos() {
		return videos;
	}





	public void setVideos(List<String[]> videos) {
		this.videos = videos;
	}
	
	


}
