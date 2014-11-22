package com.v2tech.vo;

import java.util.Date;

public class VCrowdFile extends VFile {

	private CrowdGroup crowd;

	private String url;

	public CrowdGroup getCrowd() {
		return crowd;
	}

	public void setCrowd(CrowdGroup crowd) {
		this.crowd = crowd;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <file encrypttype='1' id='C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA'
	 * name='83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif' size='497236'
	 * time='1411112464' uploader='11029' />
	 * 
	 * @return
	 */
	public String toXml() {
		String str = "<file encrypttype='1' id='"+this.getId()+"' "
				+ " name='"+this.getPath()+"' size='"+this.size+"' "
				+ " time='"+new Date().getTime()/1000+"' uploader='"+this.uploader.getmUserId()+"' />";
		return str;
	}

}
