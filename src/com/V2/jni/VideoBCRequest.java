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

import com.V2.jni.ind.V2User;
import com.V2.jni.util.V2Log;
import com.v2tech.view.Constants;

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


	public List<String[]> lives = new ArrayList<String[]>();

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
			lives.add(new String[] { uuid, lat, lan, uid });
		}

		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/03318FDB-8AD6-4FCE-83D3-33521D612477"+ ".m3u8", "40.072245","116.351065","12331"});
		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/3FA199B9-EC22-464B-BBA7-449EECCD4124"+ ".m3u8", "40.072245","116.251065","12332"});
		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/5634D096-B45F-48E9-AEA7-FEDCB2FA37FE"+ ".m3u8", "40.072245","116.151065","12333"});
		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/6E9B7B4C-38B7-4B4B-8136-6B4C2C105C7D"+ ".m3u8", "40.072245","116.551065","12334"});
		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/C822C4BB-3EAE-4B3F-A134-93CC5E044EE7"+ ".m3u8", "40.072245","116.651065","12335"});
		lives.add(new String[]{"http://" + Constants.SERVER + ":8090/hls/CA3FCB82-D07F-4E4E-AF87-A835C156F0D2"+ ".m3u8", "40.072245","116.751065","12336"});
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
