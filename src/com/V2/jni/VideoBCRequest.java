package com.V2.jni;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.V2.jni.ind.V2Live;
import com.V2.jni.ind.V2Location;
import com.V2.jni.ind.V2User;
import com.V2.jni.util.V2Log;
import com.v2tech.view.Constants;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class VideoBCRequest {

	private static final boolean DEBUG = true;
	private static VideoBCRequest instance;
	
	private List<WeakReference<VideoBCRequestCallback>> mCallbacks;

	

	private VideoBCRequest() {
		mCallbacks = new ArrayList<WeakReference<VideoBCRequestCallback>>();
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

	void OnStartLive(long nUserID, String szUrl) {
		V2Log.e("OnStartLive: "  + nUserID + "  " + szUrl);
		String newUrl = getUrl(szUrl);
		for (WeakReference<VideoBCRequestCallback> wf : this.mCallbacks) {
			Object obj = wf.get();
			if (obj != null) {
				VideoBCRequestCallback callback = (VideoBCRequestCallback) obj;
				callback.OnStartLive(nUserID, newUrl);
			}
		}
	}


	void OnStopLive(long nUserID) {
		V2Log.e("OnStopLive:" + nUserID + "  ");
		for (WeakReference<VideoBCRequestCallback> wf : this.mCallbacks) {
			Object obj = wf.get();
			if (obj != null) {
				VideoBCRequestCallback callback = (VideoBCRequestCallback) obj;
				callback.OnStopLive(nUserID);
			}
		}
	}

	void OnGPSUpdated() {

	}

	public native void UpdateWatchStatusRequest(long id, boolean flag);

	public native void CommentVideo(long id, String comments);

	public void OnCommentVideo(long id, String comments) {

	}


	void OnGetNeiborhood(String szXml) {
		if (DEBUG) {
			V2Log.e(szXml);
		}
		List<V2Live> list = new ArrayList<V2Live>();
		Document doc = buildDocument(szXml);
		NodeList userNodeList = doc.getElementsByTagName("user");
		Element userElement;
		for (int i = 0; i < userNodeList.getLength(); i++) {
			userElement = (Element) userNodeList.item(i);
			String url = userElement.getAttribute("url");
			String uuid = null;
			if (url != null && !url.isEmpty()) {
				uuid = getUrl(url);
			}

			
			String lat = userElement.getAttribute("lat");
			String lan = userElement.getAttribute("lon");
			String uid = userElement.getAttribute("userid");
			
			V2Live live = new V2Live();
			live.uuid = uuid;
			live.url = uuid;
			V2User v2user = new V2User();
			v2user.uid = Long.parseLong(uid);
			live.publisher = v2user;
			V2Location v2location = new V2Location();
			v2location.lat = Double.parseDouble(lat);
			v2location.lng = Double.parseDouble(lan);
			live.location = v2location;
			
			list.add(live);			
		}

		
		
		V2Live live = new V2Live();
		live.uuid = "test001";
		live.url = "http://" + Constants.SERVER + ":8090/hls/test001"+ ".m3u8";
		V2User v2user = new V2User();
		v2user.uid = 1;
		live.publisher = v2user;
		V2Location v2location = new V2Location();
		v2location.lat = 39.978437D;
		v2location.lng =116.294172D;
		live.location = v2location;
		
		list.add(live);		
		
		
		live = new V2Live();
		live.uuid = "103D7D14-49A6-414D-940C-67A1FD23C60A";
		live.url = "http://" + Constants.SERVER + ":8090/hls/103D7D14-49A6-414D-940C-67A1FD23C60A"+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 2;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.984186D;
		v2location.lng =116.449975D;
		live.location = v2location;
		list.add(live);	
		
		
		live = new V2Live();
		live.uuid = "test003";
		live.url = "http://" + Constants.SERVER + ":8090/hls/test003"+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 3;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.873527D;
		v2location.lng =116.308545D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "95585331-dfe1-46f7-a694-f932294789d1";
		live.url = "http://" + Constants.SERVER + ":8090/hls/95585331-dfe1-46f7-a694-f932294789d1"+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 4;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.871312D;
		v2location.lng =116.466072D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "8270814d-6a4c-4e24-82a4-4a689e044619";
		live.url = "http://" + Constants.SERVER + ":8090/hls/8270814d-6a4c-4e24-82a4-4a689e044619"+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 6;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.926224D;
		v2location.lng =116.361438D;
		live.location = v2location;
		list.add(live);	
		
		live = new V2Live();
		live.uuid = "A4D8A057-8BE4-4DFF-9568-88B4AA12BD8B";
		live.url = "http://" + Constants.SERVER + ":8090/hls/A4D8A057-8BE4-4DFF-9568-88B4AA12BD8B"+ ".m3u8";
		v2user = new V2User();
		v2user.uid = 7;
		live.publisher = v2user;
		v2location = new V2Location();
		v2location.lat = 39.917813D;
		v2location.lng =116.444225D;
		live.location = v2location;
		list.add(live);	
		
		
//		lives.add(new Live(new User(1, "a"),  "http://" + Constants.SERVER + ":8090/hls/test001"+ ".m3u8", 39.978437D,116.294172D));
//		lives.add(new Live(new User(6, "a1"),  "http://" + Constants.SERVER + ":8090/hls/103D7D14-49A6-414D-940C-67A1FD23C60A"+ ".m3u8", 39.984186D,116.449975D));
//		lives.add(new Live(new User(5, "a2"),  "http://" + Constants.SERVER + ":8090/hls/test003"+ ".m3u8", 39.873527D,116.308545D));
//		lives.add(new Live(new User(4, "a3"),  "http://" + Constants.SERVER + ":8090/hls/95585331-dfe1-46f7-a694-f932294789d1"+ ".m3u8", 39.871312D,116.466072D));
//		lives.add(new Live(new User(3, "a4"),  "http://" + Constants.SERVER + ":8090/hls/8270814d-6a4c-4e24-82a4-4a689e044619"+ ".m3u8", 39.926224D,116.361438D));
//		lives.add(new Live(new User(2, "a5"),  "http://" + Constants.SERVER + ":8090/hls/A4D8A057-8BE4-4DFF-9568-88B4AA12BD8B"+ ".m3u8", 39.917813D,116.444225D));
		
		
		for (WeakReference<VideoBCRequestCallback> wf : this.mCallbacks) {
			Object obj = wf.get();
			if (obj != null) {
				VideoBCRequestCallback callback = (VideoBCRequestCallback) obj;
				callback.OnGetNeiborhood(list);
			}
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
	
	
	private String getUrl(String origin) {
		String newUrl ="";
		int index = origin.indexOf("file=");
		if (index != -1) {
			newUrl = "http://" + Constants.SERVER + ":8090/hls/"
					+ origin.substring(index + 5) + ".m3u8";

		} else {
			newUrl = "http://" + Constants.SERVER + ":8090/hls/" + origin
					+ ".m3u8";
		}
		return newUrl;
	}
	
	/**
	 * 
	 * @param callback
	 */
	public void addCallback(VideoBCRequestCallback callback) {
		this.mCallbacks.add(new WeakReference<VideoBCRequestCallback>(callback));
	}

	public void removeCallback(VideoBCRequestCallback callback) {
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i).get() == callback) {
				this.mCallbacks.remove(i);
				break;
			}
		}
	}
}
