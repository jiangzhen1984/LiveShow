package com.v2tech.vo;

import java.util.UUID;


public class VMessageAudioVideoRequestItem extends VMessageAbstractItem {
	
	public static final int TYPE_AUDIO = 1;
	
	public static final int TYPE_VIDEO = 2;

	private int type;
	
	private long uid;
	
	private long lid;

	public VMessageAudioVideoRequestItem(VMessage vm, int type, long uid, long lid) {
		super(vm);
		this.type = type;
		this.type = ITEM_TYPE_TEXT;
		this.uuid = UUID.randomUUID().toString();
		this.uid = uid;
		this.lid = lid;
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




	public String toXmlItem() {
		String tmp = new String("@t"+type+"l"+lid+"u"+uid+"@");
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

