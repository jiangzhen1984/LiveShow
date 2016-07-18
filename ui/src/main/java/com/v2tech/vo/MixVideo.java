package com.v2tech.vo;

public class MixVideo {
	
	public enum LayoutType {
		//  1|2
		LEFT_RIGHT_2,
		
		// 1|2
		// 3|4
		SPLIT_4,
		
		//    1  2
		//       3
		//  4 5  6
		MAIN_6,
		
		/*
		 *    1   2 
		 *        3
		 * 5 6 7  8   
		 */
		MAIN_8,
		
		/*
		 *   1  2  3
		 *   4  5  6
		 *   7  8  9
		 */
		SPLIT_9,
		
		/**
		 * 
		 *   1  2
		 *  3 4 5 6
		 *  7 8 9 10
		 */
		MAIN_2_LINEAR_10,
		
		/**
		 *   1 2 3 4
		 *    5   6
		 *   7 8 9 10 
		 */ 
		MAIN_MIDDLE_2_LINEAR_10,
		
		/**
		 *  2      3
		 *  4   1  5
		 *  6      7
		 *  8 9 10 11
		 */
		MAIN_1_AROUND_11,
		
		/**
		 * 
		 *    1   2   3   4
		 *    5           8
		 *    6    7      9
		 *    10  11  12  13
		 */
		MAIN_1_AROUND_13,
		
		/**
		 * 
		 *   1     2   3
		 *         4   5
		 *  6   7  8   9
		 *  10 11  12 13
		 */
		MAIN_1_LINEAR_13,
		
		/**
		 *    1   2   3    4
		 *    5   6   7    8
		 *    9   10  11   12
		 *    13  14  15   16
		 */
		SPLIT_16,
		UNKOWN;
		
		public static LayoutType fromInt(int val) {
			return LEFT_RIGHT_2;
		}
	}
	
	private String id;
	private LayoutType type;
	
	private int width;
	private int height;
	
	private MixVideoDevice[]  udcs;

	
	public MixVideo(String id) {
		this.id = id;
		this.type = LayoutType.UNKOWN;
	}
	
	public MixVideo(String id, LayoutType type) {
		this(id, type, 320, 480);
	}
	
	public MixVideo(String id, LayoutType type, int width, int height) {
		super();
		this.id = id;
		this.type = type;
		this.width = width;
		this.height = height;
		int size = 2;
		switch (type) {
		case LEFT_RIGHT_2:
			size = 2;
			break;
		case SPLIT_4:
			size = 4;
			break;
		case MAIN_6:
			size = 6;
			break;
		case MAIN_8:
			size = 8;
			break;
		case SPLIT_9:
			size = 9;
			break;
		case MAIN_2_LINEAR_10:
		case MAIN_MIDDLE_2_LINEAR_10:
			size = 10;
			break;
		case MAIN_1_AROUND_11:
			size = 11;
			break;
		case MAIN_1_AROUND_13:
		case MAIN_1_LINEAR_13:
			size = 13;
			break;
		case SPLIT_16:
			size = 16;
			break;
		default:
			size = 1;
		}
		
		udcs = new MixVideoDevice[size];
	}
	
	public void addDevice(UserDeviceConfig udc, int pos) {
		if (udcs.length <= pos) {
			throw new RuntimeException(" Unsupport this pos:" + pos);
		}
		
		udcs[pos] = new MixVideoDevice(pos, this, udc);
	}
	
	public MixVideoDevice removeDevice(int pos) {
		if (udcs.length <= pos) {
			throw new RuntimeException(" Unsupport this pos:" + pos);
		}
		MixVideoDevice mvd = udcs[pos];
		udcs[pos] = null;
		return mvd;
	}
	
	
	public MixVideoDevice removeDevice(UserDeviceConfig udc) {
		MixVideoDevice mvd = null;
		for (int pos = 0; pos < udcs.length; pos++) {
		  if (udcs[pos].udc != null && udcs[pos].udc.equals(udc)) {
			  mvd = udcs[pos];
			  udcs[pos] = null;
			  break;
		  }
		}
		return mvd;
	}
	
	public MixVideoDevice removeDevice(MixVideoDevice rmMvd) {
		if (rmMvd == null || rmMvd.udc == null) {
			return null;
		}
		MixVideoDevice mvd = null;
		for (int pos = 0; pos < udcs.length; pos++) {
		  if (udcs[pos].udc != null && udcs[pos].udc.equals(rmMvd.udc)) {
			  mvd = udcs[pos];
			  udcs[pos] = null;
			  break;
		  }
		}
		return mvd;
	}
	
	
	public MixVideoDevice createMixVideoDevice(int pos, String  id, UserDeviceConfig udc) {
		return new MixVideoDevice( pos,   id,  udc);
	}
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public LayoutType getType() {
		return type;
	}


	public void setType(LayoutType type) {
		this.type = type;
	}


	public MixVideoDevice[] getUdcs() {
		return udcs;
	}


	public void setUdcs(MixVideoDevice[] udcs) {
		this.udcs = udcs;
	}


	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}






	public class MixVideoDevice {
		private int pos;
		private MixVideo mx;
		private UserDeviceConfig udc;
		
		public MixVideoDevice(int pos, MixVideo mx, UserDeviceConfig udc) {
			super();
			this.pos = pos;
			this.mx = mx;
			this.udc = udc;
		}
		
		public MixVideoDevice(int pos, String  id, UserDeviceConfig udc) {
			super();
			this.pos = pos;
			this.mx = new  MixVideo(id);
			this.udc = udc;
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

		public MixVideo getMx() {
			return mx;
		}

		public void setMx(MixVideo mx) {
			this.mx = mx;
		}

		public UserDeviceConfig getUdc() {
			return udc;
		}

		public void setUdc(UserDeviceConfig udc) {
			this.udc = udc;
		}
		
		
		
		
	}
	
	

}
