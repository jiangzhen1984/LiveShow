package com.v2tech.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.util.EscapedcharactersProcessing;
import com.v2tech.service.GlobalHolder;

public class Live implements Serializable, Comparable<Live>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5215649680130458839L;

	protected long lid;
	
	protected User publisher;
	
	protected String url;
	
	protected double lat;
	
	protected double lng;
	
	protected boolean canRemove;
	
	protected boolean rend;
	
	public boolean isInchr;
	
	public int rendCount;
	
	public int watcherCount;
	
	public float balanceSum;
	
	protected long nid;
	
	protected List<User> watcher;
	
	
	public Live(User publisher, long lid, double lat, double lng) {
		super();
		this.publisher = publisher;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
		this.canRemove = false;
	}
	
	public Live(User publisher, long lid, long nid, double lat, double lng) {
		super();
		this.publisher = publisher;
		this.lid = lid;
		this.lat = lat;
		this.lng = lng;
		this.nid = nid;
		this.canRemove = false;
	}
	
	
	public Live(User publisher, String url, double lat, double lng) {
		super();
		this.publisher = publisher;
		this.url = url;
		this.lat = lat;
		this.lng = lng;
		this.canRemove = false;
	}

	
	public Live(User publisher, String url, double lat, double lng, boolean canRemove) {
		super();
		this.publisher = publisher;
		this.url = url;
		this.lat = lat;
		this.lng = lng;
		this.canRemove = canRemove;
	}


	public Live(User pu, String url) {
		this.publisher = pu;
		this.url = url;
	}



	public User getPublisher() {
		return publisher;
	}



	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public double getLng() {
		return lng;
	}



	public void setLng(double lng) {
		this.lng = lng;
	}
	
	



	public boolean isCanRemove() {
		return canRemove;
	}



	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}
	
	

	public long getLid() {
		return lid;
	}


	public void setLid(long lid) {
		this.lid = lid;
	}


	




	public long getNid() {
		return nid;
	}


	public void setNid(long nid) {
		this.nid = nid;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (nid ^ (nid >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Live other = (Live) obj;
		if (nid != other.nid)
			return false;
		return true;
	}
	
	
	
	public void addWatcher(User u) {
		if (this.watcher == null) {
			this.watcher = new ArrayList<User>();
		}
		this.watcher.add(u);
	}
	


	@Override
	public int compareTo(Live another) {
		return 0;
	}


	@Override
	public String toString() {
		return "Live [lid : "+ this.lid +" nid "+ this.nid+"  publisher=" + publisher + ", url=" + url + ", lat=" + lat
				+ ", lng=" + lng + "]";
	}
	
	
	
	
	public boolean isRend() {
		return rend;
	}


	public void setRend(boolean rend) {
		this.rend = rend;
	}


	/**
	 * <conf canaudio="1" candataop="1" canvideo="1" conftype="0" haskey="0" //
	 * id="0" key="" // layout="1" lockchat="0" lockconf="0" lockfiletrans="0"
	 * mode="2" // pollingvideo="0" // subject="ss" // chairuserid='0'
	 * chairnickname=''> // </conf>
	 * 
	 * @return
	 */
	public String getConferenceConfigXml() {
		User loggedUser = GlobalHolder.getInstance().getCurrentUser();
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<conf canaudio=\"1\" candataop=\"1\" canvideo=\"1\" conftype=\"0\" haskey=\"0\" ")
				.append(" id=\"0\" key=\"\" layout=\"1\" lockchat=\"0\" lockconf=\"0\" lockfiletrans=\"0\" mode=\"2\" pollingvideo=\"0\" ")
				.append(" syncdesktop=\"0\" syncdocument=\"1\" syncvideo=\"0\" ")
				.append("subject=\"")
				.append(EscapedcharactersProcessing.convert(System.currentTimeMillis()+""))
				.append("\" ")
				.append("chairuserid=\"")
				.append(GlobalHolder.getInstance().getCurrentUserId())
				.append("\" ")
				.append("chairnickname=\"")
				.append(loggedUser == null ? "" : EscapedcharactersProcessing
						.convert(loggedUser.getName()))
				.append("\"  starttime=\"" + System.currentTimeMillis() / 1000
						+ "\" >").append("</conf>");
		return sb.toString();

	}

	public String getInvitedAttendeesXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		sb.append("</xml>");
		return sb.toString();
	}
	
}

