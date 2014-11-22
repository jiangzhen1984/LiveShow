package com.v2tech.vo;


public abstract class VMessageAbstractItem {

	
	public static final int ITEM_TYPE_ALL = 0;
	
	public static final int ITEM_TYPE_TEXT = 1;

	public static final int ITEM_TYPE_IMAGE = 2;

	public static final int ITEM_TYPE_FACE = 3;

	public static final int ITEM_TYPE_AUDIO = 4;

	public static final int ITEM_TYPE_VIDEO = 5;
	
	public static final int ITEM_TYPE_FILE = 6;
	
	public static final int ITEM_TYPE_LINK_TEXT = 7;
	
	public static final int NEW_LINE_FLAG_VALUE = 8;
	
	//Use to mark audio item
	public static final int STATE_UNREAD = 9;
	public static final int STATE_READED = 10;
	
	public static final int STATE_NORMAL = 33;
	public static final int STATE_SENT_SUCCESS = 34;
	public static final int STATE_SENT_FALIED = 35;
	
	//Use to mark file item
	public static final int STATE_FILE_NORMAL = 10;
	public static final int STATE_FILE_UNDOWNLOAD = 11;
	public static final int STATE_FILE_DOWNLOADING = 12;
	public static final int STATE_FILE_PAUSED_DOWNLOADING = 13;
	public static final int STATE_FILE_MISS_DOWNLOAD = 14;
	public static final int STATE_FILE_DOWNLOADED_FALIED = 15;
	public static final int STATE_FILE_DOWNLOADED = 16;
	
	public static final int STATE_FILE_SENDING = 20;
	public static final int STATE_FILE_SENT_FALIED = 21;
	public static final int STATE_FILE_SENDER_CANCEL = 22;
	public static final int STATE_FILE_SENT = 23;
	public static final int STATE_FILE_PAUSED_SENDING = 24;
	

	protected VMessage vm;
	
	protected int id;

	protected boolean isNewLine;

	protected int type;
	
	protected int state;
	
	protected String uuid;

	public VMessageAbstractItem(VMessage vm) {
		super();
		this.vm = vm;
		this.vm.addItem(this);
	}

	public VMessage getVm() {
		return vm;
	}

	public void setVm(VMessage vm) {
		this.vm = vm;
	}

	public boolean isNewLine() {
		return isNewLine;
	}

	public void setNewLine(boolean isNewLine) {
		this.isNewLine = isNewLine;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public abstract String toXmlItem();

	

}
