package com.v2tech.vo;

import java.util.UUID;


public class VMessageTextItem extends VMessageAbstractItem {

	private String text;

	public VMessageTextItem(VMessage vm, String text) {
		super(vm);
		this.text = text;
		this.type = ITEM_TYPE_TEXT;
		this.uuid = UUID.randomUUID().toString();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toXmlItem() {
		String tmp = new String(text);
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

