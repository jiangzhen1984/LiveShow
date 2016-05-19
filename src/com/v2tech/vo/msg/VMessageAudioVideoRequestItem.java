package com.v2tech.vo.msg;

import java.util.UUID;


public class VMessageAudioVideoRequestItem extends VMessageAbstractItem {
	
	public static final int TYPE_AUDIO = 1;
	
	public static final int TYPE_VIDEO = 2;
	
	public static final int ACTION_REQUEST = 1;
	public static final int ACTION_ACCEPT = 2;
	public static final int ACTION_DECLINE = 3;
	public static final int ACTION_HANG_OFF = 4;

	private int rtype;
	
	private long uid;
	
	private long lid;
	
	private int action;

	public VMessageAudioVideoRequestItem(VMessage vm, int type, long uid, long lid, int action) {
		super(vm);
		this.rtype = type;
		this.type = ITEM_TYPE_TEXT;
		this.uuid = UUID.randomUUID().toString();
		this.uid = uid;
		this.lid = lid;
		this.action = action;
	}

	
	

	public int getType() {
		return type;
	}




	public void setType(int type) {
		this.type = type;
	}




	public long getUid() {
		return uid;
	}




	public void setUid(long uid) {
		this.uid = uid;
	}




	public long getLid() {
		return lid;
	}




	public void setLid(long lid) {
		this.lid = lid;
	}




	public int getAction() {
		return action;
	}




	public void setAction(int action) {
		this.action = action;
	}




	public String toXmlItem() {
		String tmp = new String("@t"+rtype+"l"+lid+"u"+uid+"a"+action+"@");
		tmp = tmp.replaceAll("&", "&amp;");
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");
		tmp = tmp.replaceAll("'", "&apos;");
		tmp = tmp.replaceAll("\"", "&quot;");
		
		String str = "<TTextChatItem NewLine=\""
				+ (isNewLine ? "True" : "False")
				+ "\" FontIndex=\"0\" Text=\"" + tmp + "\"/>";
		return str;
	}

}

