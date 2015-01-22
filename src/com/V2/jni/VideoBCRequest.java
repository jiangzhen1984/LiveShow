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
			int index = url.indexOf("file=");
			String uuid = null;
			if (index != -1) {
				uuid = "http://118.145.28.194:8090/hls/"+url.substring(index + 5)+".m3u8";
			} else {
				uuid = "http://118.145.28.194:8090/hls/"+url+".m3u8";
			}
			
			String lat = userElement.getAttribute("lat");
			String lan = userElement.getAttribute("lon");
			String uid = userElement.getAttribute("userid");
			lives.add(new String[]{uuid,lat,lan,uid});
		}
		
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
