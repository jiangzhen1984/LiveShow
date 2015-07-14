package com.V2.jni;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.V2.jni.util.V2Log;
import com.v2tech.view.Constants;
import com.v2tech.vo.Live;

public class VideoBCRequest {

	private static VideoBCRequest instance;

	private VideoBCRequest() {

	}

	public static VideoBCRequest getInstance() {
		if (instance == null) {
			instance = new VideoBCRequest();
			instance.initialize(instance);
		}
		return instance;
	}

	public native boolean initialize(VideoBCRequest instance);

	public native void unInitialize();

	public native void startLive();

	public native void stopLive();

	public native void updateGpsRequest(String gpsxml);

	public native void getNeiborhood(int meters);

	public native void GetNeiborhood_Region(String region);

	private void OnStartLive(long nUserID, String szUrl) {
		V2Log.e(nUserID + "  " + szUrl);
		url = szUrl;
	}

	public static String url = null;

	void OnStopLive(long nUserID) {

		url = null;
	}

	void OnGPSUpdated() {

	}

	public native void UpdateWatchStatusRequest(long id, boolean flag);

	public native void CommentVideo(long id, String comments);

	public void OnCommentVideo(long id, String comments) {

	}


	public List<Live> lives = new ArrayList<Live>();

	void OnGetNeiborhood(String szXml) {
		V2Log.e(szXml);
		lives.clear();
		Document doc = buildDocument(szXml);
		NodeList userNodeList = doc.getElementsByTagName("user");
		Element userElement;
		for (int i = 0; i < userNodeList.getLength(); i++) {
			userElement = (Element) userNodeList.item(i);
			String url = userElement.getAttribute("url");
			String uuid = url;
			if (url != null && !url.isEmpty()) {
				int index = url.indexOf("file=");
				if (index != -1) {
					uuid = "http://" + Constants.SERVER + ":8090/hls/"
							+ url.substring(index + 5) + ".m3u8";
					// uuid =
					// "http://"+Constants.SERVER+":8090/hls/1c64f0cd-4acd-44fc-966f-8ee5583d554b.m3u8";

				} else {
					uuid = "http://" + Constants.SERVER + ":8090/hls/" + url
							+ ".m3u8";
					// uuid =
					// "http://"+Constants.SERVER+":8090/hls/1c64f0cd-4acd-44fc-966f-8ee5583d554b.m3u8";
				}
			}

			String lat = userElement.getAttribute("lat");
			String lan = userElement.getAttribute("lon");
			String uid = userElement.getAttribute("userid");
			lives.add(new Live(null,  uuid, Double.parseDouble(lat), Double.parseDouble(lan)));
		}

		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/3FA199B9-EC22-464B-BBA7-449EECCD4124"+ ".m3u8", 39.978437D,116.294172D));
		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/3FA199B9-EC22-464B-BBA7-449EECCD4124"+ ".m3u8", 39.984186D,116.449975D));
		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/5634D096-B45F-48E9-AEA7-FEDCB2FA37FE"+ ".m3u8", 39.873527D,116.308545D));
		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/5634D096-B45F-48E9-AEA7-FEDCB2FA37FE"+ ".m3u8", 39.871312D,116.466072D));
		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/5634D096-B45F-48E9-AEA7-FEDCB2FA37FE"+ ".m3u8", 39.926224D,116.361438D));
		lives.add(new Live(null,  "http://" + Constants.SERVER + ":8090/hls/5634D096-B45F-48E9-AEA7-FEDCB2FA37FE"+ ".m3u8", 39.917813D,116.444225D));
	}

	public static Document buildDocument(String xml) {
		if (xml == null || xml.isEmpty()) {
			V2Log.e(" conference xml is null");
			return null;
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		InputStream is = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			Document doc = dBuilder.parse(is);

			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
