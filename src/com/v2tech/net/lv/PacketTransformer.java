package com.v2tech.net.lv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.V2.jni.util.V2Log;
import com.V2.jni.util.XmlAttributeExtractor;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.net.pkt.Transformer;

public class PacketTransformer implements Transformer<Packet, String> {

	@Override
	public String serialize(Packet f) {
		if (f instanceof LoginReqPacket) {
			return serializeLoginRequest((LoginReqPacket) f);
		} else if (f instanceof GetCodeReqPacket) {
			return serializeGetCodeRequest((GetCodeReqPacket) f);
		} else if (f instanceof FollowReqPacket) {
			return serializeFollowRequest((FollowReqPacket) f);
		} else if (f instanceof LiveQueryReqPacket) {
			return serializeLiveQueryRequest((LiveQueryReqPacket) f);
		} else if (f instanceof LiveWatchingReqPacket) {
			return serializeLiveWatchingRequest((LiveWatchingReqPacket) f);
		} else if (f instanceof LocationReportReqPacket) {
			return serializeLocationReportRequest((LocationReportReqPacket) f);
		} else if (f instanceof LivePublishReqPacket) {
			return serializeLivePublishRequest((LivePublishReqPacket) f);
		} else if (f instanceof LogoutReqPacket) {
			return serializeLogoutRequest((LogoutReqPacket) f);
		} else if (f instanceof FansQueryReqPacket) {
			return serializeFansQueryRequest((FansQueryReqPacket) f);
		} else if (f instanceof FollowsQueryReqPacket) {
			return serializeFollowsQueryRequest((FollowsQueryReqPacket) f);
		} else if (f instanceof LiveRecommendReqPacket) {
			return serializeLiveRecommendRequest((LiveRecommendReqPacket) f);
		} else if (f instanceof PacketProxy) {
			return serialize(((PacketProxy) f).getPacket());
		} else {
			throw new RuntimeException("packet is not support : " + f);
		}

	}

	@Override
	public Packet unserializeFromStr(String t) {
		Pattern pushRex = Pattern.compile("(from=\"pushServer\")");
		Matcher mat = pushRex.matcher(t);
		boolean ind = mat.find();
		Pattern regrex = Pattern.compile("(xmlns=\")([a-zA-Z]+)(\")");
		mat = regrex.matcher(t);
		if (mat.find()) {
			String sg = mat.group();
			String type = sg.substring(7, sg.length() - 1);
			if ("login".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("logout".equalsIgnoreCase(type)) {
				return extraCommonResponse(t);
			} else if ("queryVideoList".equalsIgnoreCase(type)) {
				return extraLiveQueryResponse(t);
			} else if ("publicVideo".equalsIgnoreCase(type)) {
				if (ind) {
					return extraPublisVideoIndication(t);
				} else {
					return extraPublisVideoResponse(t);
				}
			} else if ("followUser".equalsIgnoreCase(type)) {
				return extraCommonResponse(t);
			} else if ("getSMScode".equalsIgnoreCase(type)) {
				return extraGetSMSResponse(t);
			} else if ("watchVideo".equalsIgnoreCase(type)) {
				return extraCommonResponse(t);
			} else if ("mapPosition".equalsIgnoreCase(type)) {
				return extraLoginResponse(t);
			} else if ("getFollowMe".equalsIgnoreCase(type)) {
				return extraCommonResponse(t);
			} else if ("likeVideo".equalsIgnoreCase(type)) {
				return extraCommonResponse(t);
			} else if ("getFansList".equalsIgnoreCase(type)) {
				return extraFansQueryResponse(t);
			}else if ("getFollowList".equalsIgnoreCase(type)) {
				return extraFollowsQueryResponse(t);
			}

		}

		return null;
	}

	private String serializeLoginRequest(LoginReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "login");

		if (p.isAs()) {
			appendAttrText(buffer, "type", "visitor");
		} else {
			if (p.isUsesms()) {
				appendAttrText(buffer, "type", "smscode");
			} else {
				appendAttrText(buffer, "type", "normal");
			}
		}
		appendTagStartEnd(buffer, true);

		if (p.isAs()) {
			appendTagText(buffer, "username", "");
			appendTagText(buffer, "smscode", "");
			appendTagText(buffer, "password", "");
			appendTagText(buffer, "deviceID", p.deviceId);
		} else {
			appendTagText(buffer, "username", p.username);
			if (p.isUsesms()) {
				appendTagText(buffer, "smscode", p.smscode);
				appendTagText(buffer, "password", "");
			} else {
				appendTagText(buffer, "smscode", "");
				appendTagText(buffer, "password", p.pwd);
			}
			appendTagText(buffer, "deviceID", "");
		}
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeGetCodeRequest(GetCodeReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

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
		appendStart(buffer, p.getId() + "", p.uid + "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "queryVideoList");
		appendAttrText(buffer, "type", "map");
		appendTagStartEnd(buffer, true);

		appendTagText(buffer, "longitude", p.lng + "");
		appendTagText(buffer, "latitude", p.lat + "");
		appendTagText(buffer, "radius", p.radius + "");

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeFollowRequest(FollowReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "followUser");
		if (p.add) {
			appendAttrText(buffer, "type", "add");
		} else {
			appendAttrText(buffer, "type", "delete");
		}
		appendTagStartEnd(buffer, true);

		appendTagStart(buffer, "follow", false);
		appendAttrText(buffer, "id", p.uid + "");
		appendAttrText(buffer, "descName", "");
		appendTagStartEnd(buffer, true);

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeLiveWatchingRequest(LiveWatchingReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", p.uid + "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "watchVideo");
		if (p.type == LiveWatchingReqPacket.WATCHING) {
			appendAttrText(buffer, "type", "enter");
		} else {
			appendAttrText(buffer, "type", "leave");
		}
		appendTagStartEnd(buffer, true);

		appendTagText(buffer, "videoId", p.nid + "");

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeLocationReportRequest(LocationReportReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", p.uid + "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "mapPosition");
		appendTagStartEnd(buffer, true);

		appendTagStart(buffer, "position", false);
		appendAttrText(buffer, "longitude", p.lng + "");
		appendAttrText(buffer, "latitude", p.lat + "");
		appendTagStartEnd(buffer, true);

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeLogoutRequest(LogoutReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "logout");
		appendTagStartEnd(buffer, true);

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeFansQueryRequest(FansQueryReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "getFansList");
		appendTagStartEnd(buffer, true);

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	private String serializeFollowsQueryRequest(FollowsQueryReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "getFollowList");
		appendTagStartEnd(buffer, true);

		appendTagStart(buffer, "row", false);
		appendAttrText(buffer, "from", p.start+"");
		appendAttrText(buffer, "to", p.count+"");
		appendTagStartEnd(buffer, true);
		
		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}
	

	private String serializeLiveRecommendRequest(LiveRecommendReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "likeVideo");
		appendTagStartEnd(buffer, true);

		appendTagText(buffer, "videoId", p.nvid + "");

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private String serializeLivePublishRequest(LivePublishReqPacket p) {
		StringBuffer buffer = new StringBuffer();
		appendStart(buffer, p.getId() + "", p.uid + "", "");

		appendTagStart(buffer, "query", false);
		appendAttrText(buffer, "xmlns", "publicVideo");
		appendAttrText(buffer, "type", "public");
		appendTagStartEnd(buffer, true);

		appendTagStart(buffer, "video", false);
		appendAttrText(buffer, "videoNum", p.lid + "");
		appendAttrText(buffer, "longitude", p.lng + "");
		appendAttrText(buffer, "latitude", p.lat + "");
		appendTagStartEnd(buffer, true);

		appendTagEnd(buffer, "iq");
		buffer.append("\r\n");
		return buffer.toString();
	}

	private Packet extraLoginResponse(String str) {
		LoginRespPacket lrp = new LoginRespPacket();
		lrp.setRequestId(extraRequestId(str));
		lrp.setErrorFlag(!extraResult(str));

		if (!lrp.getHeader().isError()) {
			Pattern idp = Pattern.compile("(to=\")([0-9]+)(\")");
			Matcher idm = idp.matcher(str);
			if (idm.find()) {
				String sg = idm.group();
				String strId = sg.substring(4, sg.length() - 1);
				lrp.uid = Long.parseLong(strId);
			}

			if (!extraVistorType(str)) {
				// TODO ADD fans
				Document doc = XmlAttributeExtractor.buildDocument(str);
				if (doc != null) {
					NodeList nl = doc.getElementsByTagName("fans");
					Element ve;
					LoginRespPacket.Fans fan;
					int c = nl.getLength();
					List<LoginRespPacket.Fans> list = new ArrayList<LoginRespPacket.Fans>(
							c);
					for (int i = 0; i < c; i++) {
						ve = (Element) nl.item(i);
						fan = lrp.new Fans();
						fan.id = Long.parseLong(ve.getAttribute("id"));
						fan.phone = ve.getAttribute("phone");
						fan.name = ve.getAttribute("name");
						fan.signText = ve.getAttribute("signText");
						fan.headurl = ve.getAttribute("headurl");
						fan.type = ve.getAttribute("type");
						list.add(fan);
					}

					lrp.fansList = list;
				}

			}

		}

		return lrp;
	}

	private Packet extraLiveQueryResponse(String str) {

		// str
		// +="<video id ='1' videoNum='1514600988896'  userId='1' longitude='116.4373200000' latitude='39.9704230000' sum='12'/>"
		// +
		// "<video  id ='5' videoNum='1514600988896' userId='2' longitude='116.3229120000' latitude='39.9668840000' sum='21'/>"
		// +
		// "<video id ='2'  videoNum='1514600826995' userId='3' longitude='116.3321100000' latitude='39.8903080000' sum='232'/>"
		// +
		// "<video id ='3' videoNum='1514600988896' userId='4' longitude='116.4562920000' latitude='39.9040360000' sum='2455'/>"
		// +
		// "<video  id ='4' videoNum='1514600826995' userId='5' longitude='116.4223720000' latitude='39.9496290000' sum='6562'/>";
		// String root ="<test>" + str +"</test>";
		LiveQueryRespPacket lrp = new LiveQueryRespPacket();
		lrp.setErrorFlag(!extraResult(str));

		lrp.setRequestId(extraRequestId(str));
		Document doc = XmlAttributeExtractor.buildDocument(str);
		if (doc == null) {
			lrp.setErrorFlag(true);
			return lrp;
		}

		NodeList nl = doc.getElementsByTagName("video");
		Element ve;
		int c = nl.getLength();
		List<String[]> list = new ArrayList<String[]>(c);
		for (int i = 0; i < c; i++) {
			ve = (Element) nl.item(i);
			String[] data = new String[6];
			data[0] = ve.getAttribute("id");
			data[1] = ve.getAttribute("userId");
			data[2] = ve.getAttribute("longitude");
			data[3] = ve.getAttribute("latitude");
			data[4] = ve.getAttribute("sum");
			data[5] = ve.getAttribute("videoNum");
			list.add(data);
		}
		lrp.count = c;
		lrp.videos = list;

		return lrp;
	}

	private Packet extraFansQueryResponse(String str) {
		FansQueryRespPacket lrp = new FansQueryRespPacket();
		lrp.setRequestId(extraRequestId(str));
		lrp.setErrorFlag(!extraResult(str));

		Document doc = XmlAttributeExtractor.buildDocument(str);
		if (doc == null) {
			lrp.setErrorFlag(true);
			return lrp;
		}

		NodeList nl = doc.getElementsByTagName("fans");
		Element ve;
		int c = nl.getLength();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(c);
		for (int i = 0; i < c; i++) {
			Map<String, String> map = new HashMap<String, String>();
			ve = (Element) nl.item(i);
			map.put("id", ve.getAttribute("id"));
			map.put("phone", ve.getAttribute("phone"));
			map.put("headurl", ve.getAttribute("headurl"));
			map.put("signText", ve.getAttribute("signText"));
			list.add(map);
		}
		lrp.fansList = list;

		return lrp;
	}
	
	
	
	private Packet extraFollowsQueryResponse(String str) {
		FollowsQueryRespPacket lrp = new FollowsQueryRespPacket();
		lrp.setRequestId(extraRequestId(str));
		lrp.setErrorFlag(!extraResult(str));

		Document doc = XmlAttributeExtractor.buildDocument(str);
		if (doc == null) {
			lrp.setErrorFlag(true);
			return lrp;
		}

		NodeList nl = doc.getElementsByTagName("follow");
		Element ve;
		int c = nl.getLength();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(c);
		for (int i = 0; i < c; i++) {
			Map<String, String> map = new HashMap<String, String>();
			ve = (Element) nl.item(i);
			map.put("id", ve.getAttribute("id"));
			map.put("phone", ve.getAttribute("phone"));
			map.put("headurl", ve.getAttribute("headurl"));
			map.put("signText", ve.getAttribute("signText"));
			list.add(map);
		}
		lrp.follows = list;

		return lrp;
	}
	

	private Packet extraPublisVideoResponse(String str) {
		LivePublishRespPacket lrp = new LivePublishRespPacket();
		lrp.setRequestId(extraRequestId(str));
		lrp.setErrorFlag(!extraResult(str));
		Pattern p = Pattern.compile("(<video)( +)(id=)('|\")([0-9]+)('|\")");
		Matcher m = p.matcher(str);
		if (m.find()) {
			String gr = m.group();
			Pattern p1 = Pattern.compile("([0-9]+)");
			m = p1.matcher(gr);
			if (m.find()) {
				lrp.nvid = Long.parseLong(m.group());
			}
		} else {
			V2Log.e("=== extra video id error");
			lrp.setErrorFlag(true);
		}

		return lrp;
	}
	
	private Packet extraPublisVideoIndication(String str) {
		LivePublishIndPacket lrp = new LivePublishIndPacket();
		lrp.setErrorFlag(!extraResult(str));
		Pattern p = Pattern.compile("(<video)( +)(id=)('|\")([0-9]+)('|\")");
		Matcher m = p.matcher(str);
		if (m.find()) {
			String gr = m.group();
			Pattern p1 = Pattern.compile("([0-9]+)");
			m = p1.matcher(gr);
			if (m.find()) {
				lrp.vid = Long.parseLong(m.group());
			}
			
			String mtr = extraRegGroup(str, "(videoNum=)('|\")([0-9]+)('|\")");
			mtr = extraRegGroup(mtr, "([0-9]+)");
			lrp.lid = Long.parseLong(mtr);
			mtr = extraRegGroup(str, "(userId=)('|\")([0-9]+)('|\")");
			mtr = extraRegGroup(mtr, "([0-9]+)");
			lrp.uid = Long.parseLong(mtr);
			
			mtr = extraRegGroup(str, "(longitude=)('|\")([0-9.]+)('|\")");
			mtr = extraRegGroup(mtr, "([0-9.]+)");
			lrp.lng = Double.parseDouble(mtr);
			mtr = extraRegGroup(str, "(latitude=)('|\")([0-9.]+)('|\")");
			mtr = extraRegGroup(mtr, "([0-9.]+)");
			lrp.lat = Double.parseDouble(mtr);
		} else {
			V2Log.e("=== extra video id error");
			lrp.setErrorFlag(true);
		}

		return lrp;
	}
	

	private Packet extraCommonResponse(String str) {
		ResponsePacket lrp = new ResponsePacket();
		lrp.setRequestId(extraRequestId(str));
		lrp.setErrorFlag(!extraResult(str));
		return lrp;
	}

	private long extraRequestId(String str) {
		Pattern p = Pattern.compile("(id=\")[0-9]+(\")");
		Matcher m = p.matcher(str);
		if (m.find()) {
			String gr = m.group();
			return Long.parseLong(gr.substring(4, gr.length() - 1));
		} else {
			return -1;
		}
	}

	private boolean extraResult(String str) {
		Pattern p = Pattern.compile("(type=\"success\")");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean extraVistorType(String str) {
		Pattern p = Pattern.compile("(type=\"visitor\")");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	private Packet extraGetSMSResponse(String str) {
		GetCodeRespPacket gcp = new GetCodeRespPacket();
		gcp.setRequestId(extraRequestId(str));
		gcp.setErrorFlag(!extraResult(str));
		return gcp;
	}
	
	
	private String extraRegGroup(String content, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}
	

	private void appendStart(StringBuffer buffer, String id, String from,
			String to) {
		appendTagStart(buffer, "iq", false);
		appendAttrText(buffer, "id", id);
		appendAttrText(buffer, "from", from);
		appendTagStartEnd(buffer, false);
	}

	private void appendTagStart(StringBuffer buf, String tag, boolean close) {
		buf.append("<").append(tag);
		if (close) {
			buf.append(">");
		} else {
			buf.append(" ");
		}
	}

	private void appendTagStartEnd(StringBuffer buf, boolean close) {
		buf.append(close ? "/" : "").append(">");
	}

	private void appendTagEnd(StringBuffer buf, String tag) {
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
