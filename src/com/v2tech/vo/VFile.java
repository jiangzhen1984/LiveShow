package com.v2tech.vo;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;

public class VFile {

	protected String id;
	protected String path;
	protected long size;
	protected State state;
	protected long proceedSize;
	protected String name;
	protected User uploader;
	protected Date startTime;
	protected int flag;

	public enum State {
		UNKNOWN,DOWNLOADED, DOWNLOADING, DOWNLOAD_PAUSE, UPLOADED, UPLOADING, UPLOAD_PAUSE,DOWNLOAD_FAILED,UPLOAD_FAILED,REMOVED
	}
	
	public VFile() {
		this.state = State.UNKNOWN;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public long getProceedSize() {
		return proceedSize;
	}
	
	public String getProceedSizeStr() {
		Format df = new DecimalFormat("#.0");

		if (proceedSize >= 1073741824) {
			return (df.format((double) proceedSize / (double) 1073741824)) + "G";
		} else if (proceedSize >= 1048576) {
			return (df.format((double) proceedSize / (double) 1048576)) + "M";
		} else if (proceedSize >= 1024) {
			return (df.format((double) proceedSize / (double) 1024)) + "K";
		} else {
			return proceedSize + "B";
		}
	}

	public void setProceedSize(long proceedSize) {
		this.proceedSize = proceedSize;
		if (this.proceedSize == size) {
			if (this.state == State.DOWNLOADING) {
				this.state = State.DOWNLOADED;
			} else if (this.state == State.UPLOADING) {
				this.state = State.UPLOADED;
			}
		}
			
	}
	
	public String getSpeedStr() {
		if (startTime == null) {
			startTime = new Date();
			return "";
		}
		
		int divder = (int)(System.currentTimeMillis() - startTime.getTime()) /1000;
		long speed = (proceedSize / (divder == 0? 1 :divder) );
		
		Format df = new DecimalFormat("#.0");

		if (speed >= 1073741824) {
			return (df.format((double) speed / (double) 1073741824)) + "G";
		} else if (speed >= 1048576) {
			return (df.format((double) speed / (double) 1048576)) + "M";
		} else if (speed >= 1024) {
			return (df.format((double) speed / (double) 1024)) + "K";
		} else {
			return speed + "B";
		}
	}
	
	
	public String getFileSizeStr() {
		Format df = new DecimalFormat("#.0");

		if (size >= 1073741824) {
			return (df.format((double) size / (double) 1073741824)) + "G";
		} else if (size >= 1048576) {
			return (df.format((double) size / (double) 1048576)) + "M";
		} else if (size >= 1024) {
			return (df.format((double) size / (double) 1024)) + "K";
		} else {
			return size + "B";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUploader() {
		return uploader;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
	
	

}
