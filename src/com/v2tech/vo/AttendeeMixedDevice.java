package com.v2tech.vo;

import java.util.ArrayList;
import java.util.List;


public class AttendeeMixedDevice extends Attendee {

	private MixVideo mv;
	private UserDeviceConfig[] udcs;

	public AttendeeMixedDevice(MixVideo mv) {
		super();
		this.mv = mv;
		if (this.mv != null) {
			MixVideo.MixVideoDevice[] uds = mv.getUdcs();
			udcs = new UserDeviceConfig[uds.length];
			for (int i = 0; i < uds.length; i++) {
				udcs[i] = new UserDeviceConfig(0 , 0 , 
						0,
						mv.getId(),
						null,
						UserDeviceConfig.UserDeviceConfigType.EVIDEODEVTYPE_VIDEOMIXER);
				udcs[i].setBelongsAttendee(this);
				udcs[i].setEnable(true);
			}
		}
		this.isJoined = true;
	}

	public MixVideo getMV() {
		return this.mv;
	}

	@Override
	public long getAttId() {
		return mv.getId().hashCode();
	}

	@Override
	public UserDeviceConfig getDefaultDevice() {
		if (udcs != null && udcs.length > 0) {
			return udcs[0];
		}
		return null;
	}
	
	

	@Override
	public List<UserDeviceConfig> getmDevices() {
		List<UserDeviceConfig> l = new ArrayList<UserDeviceConfig>();
		if (udcs.length > 0) {
			l.add(udcs[0]);
		}
		return l;
	}

	@Override
	public String getAttName() {
		return "混合视频  (" + mv.getWidth() + "  x " + mv.getHeight() + ")";
	}
	
	@Override
	public int getType() {
		return TYPE_MIXED_VIDEO;
	}

}
