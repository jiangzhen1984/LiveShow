package com.v2tech.net.lv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.V2.jni.util.V2Log;
import com.v2tech.net.pkt.Packet;
import com.v2tech.net.pkt.PacketProxy;
import com.v2tech.net.pkt.ResponsePacket;
import com.v2tech.net.pkt.Transformer;

/**
 * Created by Administrator on 2016/5/6.
 */
public class WebPacketTransform implements Transformer<Packet, WebPackage.Packet>{
	
	@Override
    public WebPackage.Packet serialize(Packet p){
        if (p instanceof LoginReqPacket) {
            return serializeLoginRequest((LoginReqPacket) p);
        } else if (p instanceof GetCodeReqPacket) {
            return serializeGetCodeRequest((GetCodeReqPacket) p);
        } else if (p instanceof FollowReqPacket) {
            return serializeFollowRequest((FollowReqPacket) p);
        } else if (p instanceof LiveQueryReqPacket) {
            return serializeLiveQueryRequest((LiveQueryReqPacket) p);
        } else if (p instanceof LiveWatchingReqPacket) {
            return serializeLiveWatchingRequest((LiveWatchingReqPacket) p);
        } else if (p instanceof LocationReportReqPacket) {
            return serializeLocationReportRequest((LocationReportReqPacket) p);
        } else if (p instanceof LivePublishReqPacket) {
            return serializeLivePublishRequest((LivePublishReqPacket) p);
        } else if (p instanceof LogoutReqPacket) {
            return serializeLogoutRequest((LogoutReqPacket) p);
        } else if (p instanceof FansQueryReqPacket) {
            return serializeFansQueryRequest((FansQueryReqPacket) p);
        } else if (p instanceof FollowsQueryReqPacket) {
            return serializeFollowsQueryRequest((FollowsQueryReqPacket) p);
        } else if (p instanceof LiveRecommendReqPacket) {
            return serializeLiveRecommendRequest((LiveRecommendReqPacket) p);
        } else if (p instanceof WatcherListQueryReqPacket) {
            return serializeWacherListQueryRequest((WatcherListQueryReqPacket) p);
        }else if (p instanceof V2UserIdReportReqPacket) {
            return serializeV2UserIdReportequest((V2UserIdReportReqPacket) p);
        }else if (p instanceof V2AccountReprotReqPacket) {
            return serializeV2AccountReprotRequest((V2AccountReprotReqPacket) p);
        }else if (p instanceof InquiryReqPacket) {
            return serializeInquiryRequest((InquiryReqPacket) p);
        }else if (p instanceof PacketProxy) {
            return serialize(((PacketProxy) p).getPacket());
        } else {
            throw new RuntimeException("packet is not support : " + p);
        }
    }

    public Packet unserialize(WebPackage.Packet webPackage){
        boolean ind = false;
        String from = webPackage.getFrom();
        if("pushServer".equals(from)){
            ind = true;
        }
        String type = webPackage.getMethod();
        if ("login".equalsIgnoreCase(type)) {
            return extraLoginResponse(webPackage);
        } else if ("logout".equalsIgnoreCase(type)) {
            return extraCommonResponse(webPackage);
        } else if ("queryVideoList".equalsIgnoreCase(type)) {
            return extraLiveQueryResponse(webPackage);
        } else if ("publicVideo".equalsIgnoreCase(type)) {
            if (ind) {
                return extraPublisVideoIndication(webPackage);
            } else {
                return extraPublisVideoResponse(webPackage);
            }
        } else if ("followUser".equalsIgnoreCase(type)) {
            return extraCommonResponse(webPackage);
        } else if ("getSMScode".equalsIgnoreCase(type)) {
            return extraGetSMSResponse(webPackage);
        } else if ("watchVideo".equalsIgnoreCase(type)) {
        	if (ind) {
                return extraWatchVideoIndication(webPackage);
            } else {
                return extraCommonResponse(webPackage);
            }
        } else if ("mapPosition".equalsIgnoreCase(type)) {
            return extraLoginResponse(webPackage);
        } else if ("getFollowMe".equalsIgnoreCase(type)) {
            return extraCommonResponse(webPackage);
        } else if ("likeVideo".equalsIgnoreCase(type)) {
            return extraCommonResponse(webPackage);
        } else if ("getFansList".equalsIgnoreCase(type)) {
            return extraFansQueryResponse(webPackage);
        } else if ("getUserInVideo".equalsIgnoreCase(type)) {
            return extraWatcherListQueryResponse(webPackage);
        } else if ("getFollowList".equalsIgnoreCase(type)) {
            return extraFollowsQueryResponse(webPackage);
        } else if ("gratuity".equalsIgnoreCase(type)) {
        	if (ind) {
        		return extraInquiryIndication(webPackage);
        	} else {
        		return extraCommonResponse(webPackage);
        	}
        }  else {
            return null;
        }
    }

    private WebPackage.Packet serializeLoginRequest(LoginReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("login");
        if (p.isAs()) {
            packetBuilder.setOperateType("visitor");
        } else {
            if (p.isUsesms()) {
                packetBuilder.setOperateType("smscode");
            } else {
                packetBuilder.setOperateType("normal");
            }
        }

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.User.Builder user = WebPackage.User.newBuilder();
        if (p.isAs()) {
            user.setDeviceID(p.deviceId);
        } else {
            user.setPhone(p.username);
            if (p.isUsesms()) {
                user.setPwd2OrCode(p.smscode);
            } else {
                user.setPwd(p.pwd);
            }
        }
        data.addUser(user);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraLoginResponse(WebPackage.Packet webPackage) {
        LoginRespPacket lrp = new LoginRespPacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());

        if (!lrp.getHeader().isError()) {
        	if (webPackage.getData().getUserCount() <= 0) {
        		lrp.getHeader().setError(true);
        		lrp.getHeader().setErrorCode(-1);
        		return lrp;
        	}
        	
            WebPackage.User userPacket = webPackage.getData().getUser(0);
            lrp.uid = userPacket.getId();
            lrp.v2account = userPacket.getV2UserName();
            lrp.v2password = userPacket.getV2Pwd();
            if (lrp.v2account != null && lrp.v2account.equals("null")) {
            	  lrp.v2account = null;
            	  lrp.v2password = null;
            }

            List<WebPackage.User> userList = webPackage.getData().getUserList();
            if(userList != null){
                List<LoginRespPacket.Fans> list = new ArrayList<LoginRespPacket.Fans>();
                WebPackage.User user;
                LoginRespPacket.Fans fan;
                for(int i=1; i<userList.size(); i++){
                    user = userList.get(i);
                    fan = lrp.new Fans();
                    fan.id = user.getId();
                    fan.phone = user.getPhone();
                    fan.name = user.getName();
                    fan.signText = user.getSignText();
                    fan.headurl = user.getHeadurl();
                    fan.type = String.valueOf(user.getFollowType());
                    list.add(fan);
                }
                lrp.fansList = list;
            }
        }
        return lrp;
    }

    private WebPackage.Packet serializeGetCodeRequest(GetCodeReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("getSMScode");
        packetBuilder.setOperateType("login");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setNormal(p.phone);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraCommonResponse(WebPackage.Packet webPackage) {
        ResponsePacket lrp = new ResponsePacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());
        return lrp;
    }

    private WebPackage.Packet serializeFollowRequest(FollowReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("followUser");
        if (p.add) {
            packetBuilder.setOperateType("add");
        } else {
            packetBuilder.setOperateType("delete");
        }

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.User.Builder user = WebPackage.User.newBuilder();
        user.setId((int)p.uid);
//        user.setDescName("");
        data.addUser(user);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeLiveQueryRequest(LiveQueryReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("queryVideoList");
        packetBuilder.setFrom(String.valueOf(p.uid));
        packetBuilder.setOperateType("map");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setFrom(1);
        data.setTo(20);
        WebPackage.Position.Builder position = WebPackage.Position.newBuilder();
        position.setLongitude(p.lng);
        position.setLatitude(p.lat);
        position.setRadius(p.radius);
        data.addPosition(position);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraLiveQueryResponse(WebPackage.Packet webPackage) {
        LiveQueryRespPacket lrp = new LiveQueryRespPacket();
        lrp.setErrorFlag(!webPackage.getResult().getResult());

        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        List<WebPackage.Video> videoList = webPackage.getData().getVideoList();
        List<String[]> list = new ArrayList<String[]>(videoList.size());
        WebPackage.Position position;
        for (WebPackage.Video video : videoList){
            String[] data = new String[6];
            data[0] = String.valueOf(video.getId());
            data[1] = String.valueOf(video.getUserId());
            position = video.getPosition();
            data[2] = String.valueOf(position.getLongitude());
            data[3] = String.valueOf(position.getLatitude());
            data[4] = String.valueOf(video.getSum());
            data[5] = video.getVideoNum();
            list.add(data);
        }
        lrp.videos = list;
        lrp.count = webPackage.getData().getSum();
        return lrp;
    }

    private Packet extraGetSMSResponse(WebPackage.Packet webPackage) {
        GetCodeRespPacket gcp = new GetCodeRespPacket();
        gcp.setRequestId(Long.valueOf(webPackage.getId()));
        gcp.setErrorFlag(!webPackage.getResult().getResult());
        return gcp;
    }

    private WebPackage.Packet serializeLiveWatchingRequest(LiveWatchingReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("watchVideo");
        packetBuilder.setFrom(String.valueOf(p.uid));
        if (p.type == LiveWatchingReqPacket.WATCHING) {
            packetBuilder.setOperateType("enter");
        } else {
            packetBuilder.setOperateType("leave");
        }
        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setNormal(String.valueOf(p.type));
        WebPackage.Video.Builder video = WebPackage.Video.newBuilder();
        video.setVideoNum(String.valueOf(p.nid));
        data.addVideo(video);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeLocationReportRequest(LocationReportReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("mapPosition");
        packetBuilder.setFrom(String.valueOf(p.uid));

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.Position.Builder position = WebPackage.Position.newBuilder();
        position.setLongitude(p.lng);
        position.setLatitude(p.lat);
        data.addPosition(position);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeLivePublishRequest(LivePublishReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("publicVideo");
        packetBuilder.setFrom(String.valueOf(p.uid));
        packetBuilder.setOperateType("public");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.Video.Builder video = WebPackage.Video.newBuilder();
        video.setVideoNum(String.valueOf(p.lid));
        WebPackage.Position.Builder positon = WebPackage.Position.newBuilder();
        positon.setLongitude(p.lng);
        positon.setLatitude(p.lat);
        video.setPosition(positon);
        video.setUserId((int)p.uid);
        data.addVideo(video);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraPublisVideoIndication(WebPackage.Packet webPackage) {
        LivePublishIndPacket lrp = new LivePublishIndPacket();
        lrp.setErrorFlag(!webPackage.getResult().getResult());
        lrp.vid = Long.valueOf(webPackage.getData().getNormal());
        WebPackage.Video video = webPackage.getData().getVideo(0);
        lrp.lid = Long.parseLong(video.getVideoNum());
        lrp.uid = video.getUserId();
        WebPackage.Position position = video.getPosition();
        lrp.lng = position.getLongitude();
        lrp.lat = position.getLatitude();
        lrp.v2uid = webPackage.getData().getUser(0).getId();
        return lrp;
    }
    
    
    private Packet extraWatchVideoIndication(WebPackage.Packet webPackage) {
        long uid = webPackage.getData().getUser(0).getId();
        long lid = webPackage.getData().getVideo(0).getId();
        int type = Integer.parseInt(webPackage.getData().getNormal());
        LiveWatchingIndPacket lrp = new LiveWatchingIndPacket(uid, lid, type);    
        lrp.setErrorFlag(!webPackage.getResult().getResult());
    	return lrp;
    }

    private Packet extraPublisVideoResponse(WebPackage.Packet webPackage) {
        LivePublishRespPacket lrp = new LivePublishRespPacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());
        if(webPackage.getResult().getResult()){
            lrp.nvid = Long.valueOf(webPackage.getData().getNormal());
        }else {
            V2Log.e("=== extraPublisVideoResponse error");
            lrp.setErrorFlag(true);
        }
        return lrp;
    }

    private WebPackage.Packet serializeLogoutRequest(LogoutReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("logout");
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeFansQueryRequest(FansQueryReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("getFansList");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setFrom(1);
        data.setTo(20);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeFollowsQueryRequest(FollowsQueryReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("getFollowList");
        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setFrom(p.start);
        data.setTo(p.count);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraFollowsQueryResponse(WebPackage.Packet webPackage) {
        FollowsQueryRespPacket lrp = new FollowsQueryRespPacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());

        List<WebPackage.User> userList = webPackage.getData().getUserList();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>(userList.size());
        for (WebPackage.User user : userList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(user.getId()));
            map.put("phone", user.getPhone());
            map.put("headurl", user.getHeadurl());
            map.put("signText", user.getSignText());
            map.put("v2id", user.getV2ID());
            map.put("fansCount", user.getFansCount()+"");
            map.put("followCount", user.getFollowCount()+"");
            map.put("signature", user.getSignText());
           
            list.add(map);
        }
        lrp.follows = list;
        return lrp;
    }

    private WebPackage.Packet serializeLiveRecommendRequest(LiveRecommendReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("likeVideo");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setNormal(String.valueOf(p.nvid));
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private WebPackage.Packet serializeWacherListQueryRequest(WatcherListQueryReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("getUserInVideo");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.setFrom(p.start);
        data.setTo(p.count);
        data.setNormal(String.valueOf(p.lid));
        packetBuilder.setData(data);
        return packetBuilder.build();
    }

    private Packet extraFansQueryResponse(WebPackage.Packet webPackage) {
        FansQueryRespPacket lrp = new FansQueryRespPacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());

        List<WebPackage.User> userList = webPackage.getData().getUserList();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>(userList.size());
        for (WebPackage.User user : userList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(user.getId()));
            map.put("phone", user.getPhone());
            map.put("headurl", user.getHeadurl());
            map.put("signText", user.getSignText());
            map.put("v2id", user.getV2ID());
            map.put("fansCount", user.getFansCount()+"");
            map.put("followCount", user.getFollowCount()+"");
            map.put("signature", user.getSignText());
            list.add(map);
        }
        lrp.fansList = list;
        return lrp;
    }

    private Packet extraWatcherListQueryResponse(WebPackage.Packet webPackage) {
        WatcherListQueryRespPacket lrp = new WatcherListQueryRespPacket();
        lrp.setRequestId(Long.valueOf(webPackage.getId()));
        lrp.setErrorFlag(!webPackage.getResult().getResult());

        if (lrp.getHeader().isError()) {
            return lrp;
        }
        List<WebPackage.User> userList = webPackage.getData().getUserList();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>(userList.size());
        WebPackage.Position position;
        for (WebPackage.User user : userList){
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(user.getId()));
            map.put("phone", user.getPhone());
            map.put("name", user.getName());
            map.put("headurl", user.getHeadurl());
            map.put("signText", user.getSignText());
            map.put("v2id", user.getV2ID());
            position = user.getPosition();
            map.put("longitude", String.valueOf(position.getLongitude()));
            map.put("latitude", String.valueOf(position.getLatitude()));
            list.add(map);
        }
        lrp.watcherList = list;
        return lrp;
    }
    
    
    
    
    private WebPackage.Packet serializeV2UserIdReportequest(V2UserIdReportReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("handleV2ID");
        packetBuilder.setOperateType("set");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.User.Builder user = WebPackage.User.newBuilder();
        user.setV2ID(p.v2userId+"");
        data.addUser(user);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }
    
    private WebPackage.Packet serializeV2AccountReprotRequest(V2AccountReprotReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("v2NameAndPwd");

        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        WebPackage.User.Builder user = WebPackage.User.newBuilder();
        user.setV2UserName(p.account);
        user.setV2Pwd(p.password);
        data.addUser(user);
        packetBuilder.setData(data);
        return packetBuilder.build();
    }
    
    private WebPackage.Packet serializeInquiryRequest(InquiryReqPacket p) {
        WebPackage.Packet.Builder packetBuilder = WebPackage.Packet.newBuilder();
        packetBuilder.setPacketType(WebPackage.Packet.type.iq);
        packetBuilder.setId(String.valueOf(p.getId()));
        packetBuilder.setMethod("gratuity");
        packetBuilder.setOperateType("normal");

        WebPackage.Gratuity.Builder bid = WebPackage.Gratuity.newBuilder();
        bid.setAmount(p.award);
        bid.setFromUserID((int)p.currentUserId);
        if (p.type == InquiryReqPacket.TYPE_CANCEL) {
        	bid.setAnswer(WebPackage.Gratuity.Answer.cancel);
        }
        
        WebPackage.Data.Builder data = WebPackage.Data.newBuilder();
        data.addGratuity(bid);
        
        
        return packetBuilder.build();
    }
    
    
    
    private Packet extraInquiryIndication(WebPackage.Packet webPackage) {
    	InquiryIndPacket lrp = new InquiryIndPacket();
        lrp.setErrorFlag(!webPackage.getResult().getResult());
        long uid = webPackage.getData().getUserList().get(0).getId();
		V2Log.i("====> get inquiry indiction : " + uid + "  type:  "
				+ webPackage.getData().getGratuity(0).getAnswer().ordinal());
        return lrp;
    }
    
    
}
