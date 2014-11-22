package com.v2tech.vo;


public class VMessageLinkTextItem extends VMessageAbstractItem {
	
	private String text;
	private String url;

	public VMessageLinkTextItem(VMessage vm, String text, String url) {
		super(vm);
		this.text = text;
		this.url = url;
		this.type = ITEM_TYPE_LINK_TEXT;
		this.uuid = vm.getUUID();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	

	public String getUrl() {
		if(url.startsWith("www"))
			return "http://" + url;
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toXmlItem() {
		String tmp = new String(text);
		tmp = tmp.replaceAll("&", "&amp;");
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");
		tmp = tmp.replaceAll("'", "&apos;");
		tmp = tmp.replaceAll("\"", "&quot;");
		
		String str = "<TLinkTextChatItem NewLine=\""
				+ (isNewLine ? "True" : "False")
				+ "\" FontIndex=\"0\" Text=\"" + tmp + "\" "
				+ " URL=\""+url+"\" LinkType=\"lteHttp\" />";
		return str;
	}

}
