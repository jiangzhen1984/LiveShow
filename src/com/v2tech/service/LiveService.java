package com.v2tech.service;

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

import android.os.Handler;
import android.os.Message;

import com.V2.jni.InteractionRequest;
import com.V2.jni.callback.InteractionRequestCallBack;
import com.V2.jni.ind.V2Live;
import com.V2.jni.ind.V2Location;
import com.V2.jni.ind.V2User;
import com.V2.jni.ind.VideoCommentInd;
import com.V2.jni.util.V2Log;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.lv.LocationReportReqPacket;
import com.v2tech.service.jni.GetNeiborhoodResponse;
import com.v2tech.service.jni.JNIResponse;
import com.v2tech.service.jni.LiveNotification;
import com.v2tech.service.jni.RequestFinishPublishResponse;
import com.v2tech.service.jni.RequestPublishResponse;
import com.v2tech.view.Constants;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;

public class LiveService extends AbstractHandler {
	
	
	private static final int QUERY_NERY = 100;
	private static final int REQUEST_PUBLISH = 101;
	private static final int REQUEST_FINISH_PUBLISH = 102;
	private static final int ADD_FANDS = 103;
	private static final int REMOVE_FANDS = 104;
	
	
	private MessageListener mLiveNotification;
	private InteractionRequestCallBack bcCallback;

	public LiveService() {
		bcCallback = new LocalBCCallback(this);
		InteractionRequest.getInstance().addCallback(bcCallback);
	}
	
	
	public void updateGps(double lat, double lng) {
		 DeamonWorker.getInstance().request(new PacketProxy(new LocationReportReqPacket(lat, lng), null));
	}
	

	@Override
	public void clearCalledBack() {
		InteractionRequest.getInstance().removeCallback(bcCallback);
	}
	
	public void scanNear(double lat, double lng, float radius, MessageListener caller) {
		initTimeoutMessage(QUERY_NERY, DEFAULT_TIME_OUT_SECS, caller);
		V2Log.e("start call InteractionRequest.getInstance().GetNeiborhood_Region" );
		InteractionRequest.getInstance().GetNeiborhood_Region("<gps lon=\"" + lng + "\" lat=\"" + lat
								+ "\" distance=\""+radius+"\" ></gps>");
	}

	public void requestPublish( MessageListener caller) {
		initTimeoutMessage(REQUEST_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().startLive();
	}
	
	
	public void requestFinishPublish(MessageListener caller) {
		initTimeoutMessage(REQUEST_FINISH_PUBLISH, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().stopLive();
	}
	
	
	
	public void addFans(Live l) {
		InteractionRequest.getInstance().UpdateWatchStatusRequest(l.getPublisher().getmUserId(), true);
	}
	
	public void sendComments(long userId, String msg) {
		V2Log.e(userId+":"+msg);
		InteractionRequest.getInstance().CommentVideo(userId, msg);
	}
	
	public void registerLiveNotification(MessageListener listener) {
		this.mLiveNotification = listener;
	}
	
	
	public void addFans(long userId,MessageListener caller) {
		initTimeoutMessage(ADD_FANDS, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().addConcern(userId);
		
	}

	public void removeFans(long userId, MessageListener caller) {
		initTimeoutMessage(REMOVE_FANDS, DEFAULT_TIME_OUT_SECS, caller);
		InteractionRequest.getInstance().cancelConcern(userId);
	}
	
	
	private MessageListener notificator;
	public void setCommentsNotifier(MessageListener ml) {
		notificator = ml;
	}
	
	
	class LocalBCCallback implements InteractionRequestCallBack {
		
		private Handler mCallbackHandler;

		public LocalBCCallback(Handler mCallbackHandler) {
			this.mCallbackHandler = mCallbackHandler;
		}

		@Override
		public void OnStartLive(long nUserID, String szUrl) {
			V2Log.e(szUrl);
			long currentUid = GlobalHolder.getInstance().getCurrentUser() == null ? GlobalHolder
					.getInstance().nyUserId : GlobalHolder.getInstance()
					.getCurrentUser().getmUserId();
	
			if (nUserID == currentUid) {
				Message.obtain(mCallbackHandler, REQUEST_PUBLISH, new RequestPublishResponse(JNIResponse.Result.SUCCESS, szUrl, nUserID))
				.sendToTarget();
			} else {
				if (mLiveNotification != null) {
					Message.obtain(
							mLiveNotification.refH.get(),
							mLiveNotification.what,
							new LiveNotification(
									JNIResponse.Result.SUCCESS, new Live(new User(nUserID), szUrl), LiveNotification.TYPE_START))
							.sendToTarget();
				}
			}
		}

		@Override
		public void OnStopLive(long nUserID) {
			long currentUid = GlobalHolder.getInstance().getCurrentUser() == null ? GlobalHolder
					.getInstance().nyUserId : GlobalHolder.getInstance()
					.getCurrentUser().getmUserId();
	
			if (nUserID == currentUid) {
				Message.obtain(mCallbackHandler, REQUEST_FINISH_PUBLISH, new RequestFinishPublishResponse(JNIResponse.Result.SUCCESS, nUserID))
				.sendToTarget();
			} else {
				if (mLiveNotification != null) {
					Message.obtain(
							mLiveNotification.refH.get(),
							mLiveNotification.what,
							new LiveNotification(
									JNIResponse.Result.SUCCESS, new Live(new User(nUserID), null), LiveNotification.TYPE_STOPPED))
							.sendToTarget();
				}
			}
		}
		
		
		

		@Override
		public void OnGPSUpdated() {
			V2Log.e("=====================");
			
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
		

		private Document buildDocument(String xml) {
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
		

		@Override
		public void OnGetNeiborhood(String resultXml) {
				V2Log.e(resultXml);
			List<V2Live> list = new ArrayList<V2Live>();
			Document doc = buildDocument(resultXml);
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
			live.uuid = "2004";
			live.url = "http://" + Constants.SERVER + ":8090/hls/2004"+ ".m3u8";
			V2User v2user = new V2User();
			v2user.uid = 1;
			live.publisher = v2user;
			V2Location v2location = new V2Location();
			v2location.lat = 39.978437D;
			v2location.lng =116.294172D;
			live.location = v2location;
			
			list.add(live);		
			
			
			live = new V2Live();
			live.uuid = "2005";
			live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
			v2user = new V2User();
			v2user.uid = 2;
			live.publisher = v2user;
			v2location = new V2Location();
			v2location.lat = 39.984186D;
			v2location.lng =116.449975D;
			live.location = v2location;
			list.add(live);	
			
			
			live = new V2Live();
			live.uuid = "2007";
			live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
			v2user = new V2User();
			v2user.uid = 3;
			live.publisher = v2user;
			v2location = new V2Location();
			v2location.lat = 39.873527D;
			v2location.lng =116.308545D;
			live.location = v2location;
			list.add(live);	
			
			live = new V2Live();
			live.uuid = "2006";
			live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
			v2user = new V2User();
			v2user.uid = 4;
			live.publisher = v2user;
			v2location = new V2Location();
			v2location.lat = 39.871312D;
			v2location.lng =116.466072D;
			live.location = v2location;
			list.add(live);	
			
			live = new V2Live();
			live.uuid = "2008";
			live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
			v2user = new V2User();
			v2user.uid = 6;
			live.publisher = v2user;
			v2location = new V2Location();
			v2location.lat = 39.926224D;
			v2location.lng =116.361438D;
			live.location = v2location;
			list.add(live);	
			
			live = new V2Live();
			live.uuid = "2009";
			live.url = "http://" + Constants.SERVER + ":8090/hls/"+ live.uuid+ ".m3u8";
			v2user = new V2User();
			v2user.uid = 7;
			live.publisher = v2user;
			v2location = new V2Location();
			v2location.lat = 39.917813D;
			v2location.lng =116.444225D;
			live.location = v2location;
			list.add(live);	
			
			
//			lives.add(new Live(new User(1, "a"),  "http://" + Constants.SERVER + ":8090/hls/test001"+ ".m3u8", 39.978437D,116.294172D));
//			lives.add(new Live(new User(6, "a1"),  "http://" + Constants.SERVER + ":8090/hls/103D7D14-49A6-414D-940C-67A1FD23C60A"+ ".m3u8", 39.984186D,116.449975D));
//			lives.add(new Live(new User(5, "a2"),  "http://" + Constants.SERVER + ":8090/hls/test003"+ ".m3u8", 39.873527D,116.308545D));
//			lives.add(new Live(new User(4, "a3"),  "http://" + Constants.SERVER + ":8090/hls/95585331-dfe1-46f7-a694-f932294789d1"+ ".m3u8", 39.871312D,116.466072D));
//			lives.add(new Live(new User(3, "a4"),  "http://" + Constants.SERVER + ":8090/hls/8270814d-6a4c-4e24-82a4-4a689e044619"+ ".m3u8", 39.926224D,116.361438D));
//			lives.add(new Live(new User(2, "a5"),  "http://" + Constants.SERVER + ":8090/hls/A4D8A057-8BE4-4DFF-9568-88B4AA12BD8B"+ ".m3u8", 39.917813D,116.444225D));
			OnGetNeiborhood(list);
			
		}


		@Override
		public void OnCommentVideo(long nUserID, String szCommentXml) {
			String msg = "";
			int idx = szCommentXml.indexOf("data=");
			if (idx != -1) {
				char cr =szCommentXml.charAt(idx+6);
				int end = szCommentXml.indexOf(cr, idx + 6);
				if (end != -1) {
					msg = szCommentXml.substring(idx + 6, end);
				}
			}
			V2Log.e("OnCommentVideo:"+nUserID+":"+szCommentXml);
			Handler h = notificator.refH.get();
			if (h != null) {
				Message.obtain(h, notificator.what, new VideoCommentInd(nUserID, msg)).sendToTarget();;
			}
			
		}

		@Override
		public void OnAddConcern(long nSrcUserID, long nDstUserID) {
			V2Log.e("OnAddConcern"+  nSrcUserID+":"+nDstUserID);
			
		}

		@Override
		public void OnCancelConcernl(long nSrcUserID, long nDstUserID) {
			V2Log.e("OnCancelConcernl"+  nSrcUserID+":"+nDstUserID);
			
		}

		@Override
		public void OnMyConcerns(String szConcernsXml) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnMyFans(String szFansXml) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnFansCount(String szFansXml) {
			// TODO Auto-generated method stub
			
		}

		public void OnGetNeiborhood(List<V2Live> liveList) {
			
			List<Live> list = new ArrayList<Live>(liveList.size());
			for (V2Live v2live : liveList) {
				list.add(new Live(new User(v2live.publisher.uid, v2live.publisher.name), v2live.url, v2live.location.lat, v2live.location.lng));
			}
			
			GetNeiborhoodResponse res = new GetNeiborhoodResponse(JNIResponse.Result.SUCCESS);
			res.list = list;
			Message.obtain(mCallbackHandler, QUERY_NERY, res)
			.sendToTarget();
		}
		
	}

}


