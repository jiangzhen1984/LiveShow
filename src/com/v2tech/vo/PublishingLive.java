package com.v2tech.vo;

import java.util.HashMap;
import java.util.Map;

import com.v2tech.vo.group.Group;

public class PublishingLive extends Live {
	
	public Group group;
	
	protected Map<User, ConnectedUser> connectUser;

	public PublishingLive(User publisher, long lid, double lat, double lng) {
		super(publisher, lid, lat, lng);
	}

	public PublishingLive(User publisher, long lid, long nid, double lat,
			double lng) {
		super(publisher, lid, nid, lat, lng);
	}

	public PublishingLive(User publisher, String url, double lat, double lng,
			boolean canRemove) {
		super(publisher, url, lat, lng, canRemove);
	}

	public PublishingLive(User publisher, String url, double lat, double lng) {
		super(publisher, url, lat, lng);
	}

	public PublishingLive(User pu, String url) {
		super(pu, url);
	}
	
	
	public void addVideoRequestUser(User user) {
		if (connectUser == null) {
			connectUser = new HashMap<User, ConnectedUser>();
		}
		connectUser.put(user, newConnectedUser(user, ConnectionType.VIDEO));
	}
	
	public void addAudioRequestUser(User user) {
		if (connectUser == null) {
			connectUser = new HashMap<User, ConnectedUser>();
		}
	
		connectUser.put(user, newConnectedUser(user, ConnectionType.AUDIO));
	}
	
	private ConnectedUser newConnectedUser(User user, ConnectionType ct) {
		ConnectedUser cu = new ConnectedUser();
		cu.user = user;
		cu.cs = ConnectionStatus.REQUESTING;
		cu.ct = ct;
		return cu;
	}
	
	public class ConnectedUser {
		public User user;
		public ConnectionStatus cs;
		public ConnectionType ct;
		public int viewIdx;
		public boolean suspend;
	}
	
	
	
	
	
	public enum ConnectionStatus {
		REQUESTING, CONNECTED, REJCTED, DISCONNECTED;
	}
	
	public enum ConnectionType {
		AUDIO, VIDEO;
	}

}
