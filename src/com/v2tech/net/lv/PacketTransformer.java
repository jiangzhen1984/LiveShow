package com.v2tech.net.lv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.Transformer;

public class PacketTransformer implements Transformer<Packet, String> {

	@Override
	public String serialize(Packet f) {
		if (f instanceof LoginReqPacket) {
			return serializeLoginRequest((LoginReqPacket)f);
		} else if (f instanceof GetCodeReqPacket) {
			return serializeGetCodeRequest((GetCodeReqPacket)f);
		} else if (f instanceof FollowReqPacket) {
			return serializeFollowRequest((FollowReqPacket)f);
		} else if (f instanceof LiveQueryReqPacket) {
			return serializeLiveQueryRequest((LiveQueryReqPacket)f);
		}  else if (f instanceof LiveWatchingReqPacket) {
			return serializeLiveWatchingRequest((LiveWatchingReqPacket)f);
		} else if (f instanceof LocationReportReqPacket) {
			return serializeLocationReportRequest((LocationReportReqPacket)f);
		} else if (f instanceof LivePublishReqPacket) {
			return serializeLivePublishRequest((LivePublishReqPacket)f);
		}else if (f instanceof LogoutReqPacket) {
			return serializeLogoutRequest((LogoutReqPacket)f);
		} else if (f instanceof PacketProxy) {
			return serialize(((PacketProxy)f).getPacket());
		} else {
			throw new RuntimeException("packet is not support : "+ f);
		}
		
	}

	@Override
	public Packet unserializeFromStr(String t) {
		Pattern regrex = Pattern.compile("(xmlns=\")([a-zA-Z]+)(\")");
		Matcher mat = regrex.matcher(t);
		if (mat.find()) {
			String sg = mat.group();
			String type = sg.substring(7, sg.length() - 1);
			if ("login".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("logout".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("queryVideoList".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("publicVideo".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("followUser".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("getSMScode".equalsIgnoreCase(type)) {
				return extraGetSMSResponse(t);
			} else if ("watchVideo".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("mapPosition".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			}
		}
		
		return null;
	}

	
	
	
	private String serializeLoginRequest(LoginReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "login");
		
		
		if (p.isAs()) {
			appendAttrText(buffer, "type", "visitor");
		} else {
			appendAttrText(buffer, "type", "normal");
		}
		appendTagStartEnd(buffer, true);
		
		if (p.isAs()) {
			appendTagText(buffer, "username", "");
			appendTagText(buffer, "password", "");
			appendTagText(buffer, "deviceID", p.deviceId);
		} else {
			appendTagText(buffer, "username", p.username);
			appendTagText(buffer, "password", p.pwd);
			appendTagText(buffer, "deviceID", "");
		}
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	private String serializeGetCodeRequest(GetCodeReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "getSMScode");
		appendAttrText(buffer, "type", "login");
		appendTagStartEnd(buffer, true);
		
		appendTagText(buffer, "phone", p.phone);
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	private String serializeLiveQueryRequest(LiveQueryReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "queryVideoList");
		appendAttrText(buffer, "type", "map");
		appendTagStartEnd(buffer, true);
		
		
		appendTagText(buffer, "longitude", p.lng+"");
		appendTagText(buffer, "latitude", p.lat+"");
		appendTagText(buffer, "radius", p.radius+"");
		
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	
	
	
	
	private String serializeFollowRequest(FollowReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "followUser");
		if (p.add) {
			appendAttrText(buffer, "type", "add");
		} else {
			appendAttrText(buffer, "type", "delete");
		}
		appendTagStartEnd(buffer, true);
		
		appendTagStart(buffer, "follow", false);
		appendAttrText(buffer, "id", p.uid+"");
		appendAttrText(buffer, "descName", "");
		appendTagStartEnd(buffer, true);
		
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	private String serializeLiveWatchingRequest(LiveWatchingReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "watchVideo");
		if (p.type == LiveWatchingReqPacket.WATCHING) {
			appendAttrText(buffer, "type", "enter");
		} else {
			appendAttrText(buffer, "type", "leave");
		}
		appendTagStartEnd(buffer, true);
		
		
		appendTagText(buffer, "videoId", p.lid+"");
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	private String serializeLocationReportRequest(LocationReportReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "watchVideo");
		appendTagStartEnd(buffer, true);
		
		
		appendTagStart(buffer, "mapPosition", false);
		appendTagText(buffer, "longitude", p.lng+"");
		appendTagText(buffer, "latitude", p.lat+"");
		appendTagStartEnd(buffer, true);
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	private String serializeLogoutRequest(LogoutReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "logout");
		appendTagStartEnd(buffer, true);
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	private String serializeLivePublishRequest(LivePublishReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId()+"", "", "");
		
		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "publicVideo");
		appendAttrText(buffer, "type", "public");
		appendTagStartEnd(buffer, true);
		
		appendTagStart(buffer, "video", false);
		appendAttrText(buffer, "videoNum", p.lid+"");
		appendAttrText(buffer, "longitude", p.lng+"");
		appendAttrText(buffer, "latitude", p.lat+"");
		appendTagStartEnd(buffer, true);
		
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	
	
	
	
	private Packet extraLoginResponse(String str) {
		LoginRespPacket lrp = new LoginRespPacket();
		Pattern p = Pattern.compile("type=\"success\"");
		Matcher m = p.matcher(str);
		if (m.find()) {
			lrp.setErrorFlag(false);
			
			Pattern idp = Pattern.compile("(to=\")([0-9]+)(\")");
			Matcher idm = idp.matcher(str);
			if (idm.find()) {
				String sg = idm.group();
				String strId = sg.substring(4, sg.length() - 1);
				lrp.uid = Long.parseLong(strId);
			}
			
		} else {
			lrp.setErrorFlag(true);
		}
		

		return lrp;
	}
	
	
	
	private Packet extraGetSMSResponse(String str) {
		Pattern p = Pattern.compile("type=\"success\"");
		Matcher m = p.matcher(str);
		GetCodeRespPacket gcp = new GetCodeRespPacket();
		if (m.find()) {
			gcp.setErrorFlag(false);
		} else {
			gcp.setErrorFlag(true);
		}
		return gcp;
	}
	
	
	
	private void appendStart(StringBuffer buffer, String id, String from, String to) {
		appendTagStart(buffer, "iq", false);
		appendAttrText(buffer, "id", id);
		appendAttrText(buffer, "from", from);
		appendTagStartEnd(buffer, false);
	}
	
	
	private void appendTagStart(StringBuffer buf, String tag, boolean close){
		buf.append("<").append(tag);
		if (close) {
			buf.append(">");
		} else {
			buf.append(" ");
		}
	}
	
	
	private void appendTagStartEnd(StringBuffer buf, boolean close){
		buf.append(close?"/":"").append(">");
	}
	
	private void appendTagEnd(StringBuffer buf, String tag){
		buf.append("</").append(tag).append("> ");
	}
	
	private void appendTagText(StringBuffer buf, String tag, String text) {
		buf.append("<").append(tag).append(">");
		buf.append(text);
		buf.append("</").append(tag).append(">");
	}
	
	private void appendAttrText(StringBuffer buf, String attr, String val) {
		buf.append(" ").append(attr).append("=\"");
		buf.append(val).append("\"").append(" ");
	}
}
