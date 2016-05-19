package com.v2tech.vo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;

import com.V2.jni.util.EscapedcharactersProcessing;
import com.V2.jni.util.V2Log;
import com.v2tech.service.GlobalHolder;
import com.v2tech.vo.group.Group;

/**
 * User information
 * 
 * @author 28851274
 * 
 */

public class User implements Serializable, Comparable<User> {

	
	public long nId;
	// Server transfer fields
	// 通过OnGetGroupUserInfo传来
	// 登录用的帐号字符串
	private String mAccount;
	private String mAddress;
	private int mAuthtype = 0;// 取值0允许任何人，1需要验证，2不允许任何人
	private Date mBirthday;
	private String mStringBirthday;
	// bsystemavatar='1'
	private String mEmail;
	private String mFax;
	// homepage='http://wenzongliang.com'
	private long mUserId;
	private String mJob;
	private String mMobile;
	// 登录后显示的昵称
	private String mNickName;
	// privacy='0'
	private String mSex;
	private String mSignature;
	private String mTelephone;
	private String mCommentname;

	// group
	private String mCompany;
	private String mDepartment;
	// end Server transfer fields

	// custom fields
	private boolean isCurrentLoggedInUser;
	private DeviceType mType;
	private Status mStatus;
	private String mName;

	private Set<Group> mBelongsGroup;
	private String mAvatarPath;
	private String abbra;
	
	public List<User> fansList;

	// This value indicate this object is dirty, construct locally without any
	// user information
	private boolean isDirty;
	
	
	public boolean follow;
	
	
	public boolean isNY;
	
	public boolean isMale;
	
	public List<UserDeviceConfig> ll ;

	public User(long mUserId) {
		this(mUserId, null, null, null);
	}

	public User(long mUserId, String name) {
		this(mUserId, name, null, null);
	}



	public User(long mUserId, String name, String email, String signature) {
		this.mUserId = mUserId;
		this.mName = name;
		this.mEmail = email;
		this.mSignature = signature;
		mBelongsGroup = new CopyOnWriteArraySet<Group>();
		isCurrentLoggedInUser = false;
		this.mStatus = Status.OFFLINE;
		initAbbr();
		isDirty = true;
	}

	private void initAbbr() {
		abbra = "";
	}

	public boolean isCurrentLoggedInUser() {
		return isCurrentLoggedInUser;
	}

	public void setCurrentLoggedInUser(boolean isCurrentLoggedInUser) {
		this.isCurrentLoggedInUser = isCurrentLoggedInUser;
	}

	public long getmUserId() {
		return mUserId;
	}

	public void setmUserId(long mUserId) {
		this.mUserId = mUserId;
	}

	public void setmCommentname(String mCommentname) {
		this.mCommentname = mCommentname;
	}

	public String getmCommentname() {
		return this.mCommentname;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
		initAbbr();
	}

	public String getmEmail() {
		return mEmail;
	}

	public void setEmail(String mail) {
		this.mEmail = mail;
	}

	public String getSignature() {
		return mSignature;
	}

	public DeviceType getDeviceType() {
		return mType;
	}

	public void setDeviceType(DeviceType type) {
		this.mType = type;
	}

	public void setSignature(String signature) {
		this.mSignature = signature;
	}

	public Set<Group> getBelongsGroup() {
		return mBelongsGroup;
	}

	public void setmBelongsGroup(Set<Group> belongsGroup) {
		this.mBelongsGroup = belongsGroup;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public String getMobile() {
		return mMobile;
	}

	public void setMobile(String mCellPhone) {
		this.mMobile = mCellPhone;
	}

	public String getCompany() {
		if (mCompany == null) {
			mCompany = loadCompany(this.getFirstBelongsGroup());
		}
		return mCompany;
	}

	private String loadCompany(Group g) {
		if (g == null) {
			return "";
		}
		if (g.getParent() != null) {
			return loadCompany(g.getParent());
		} else {
			return g.getName();
		}
	}

	public void setCompany(String mCompany) {
		this.mCompany = mCompany;
	}

	public String getDepartment() {
		// FIXME first group is real department
		Group g = this.getFirstBelongsGroup();
		if (g != null) {
			if (g.getParent() == null) {
				mDepartment = "";
			} else {
				mDepartment = g.getName();
			}
		}
		return mDepartment;
	}

	public void setDepartment(String mDepartment) {
		this.mDepartment = mDepartment;
	}

	public String getSex() {
		return mSex;
	}

	public void setSex(String mGender) {
		this.mSex = mGender;
	}

	public Date getBirthday() {
		return mBirthday;
	}

	public String getBirthdayStr() {
		if (mBirthday != null) {
			DateFormat sd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			return sd.format(mBirthday);
		} else {
			return mStringBirthday;
		}
	}

	public String getmStringBirthday() {
		return mStringBirthday;
	}

	public void setmStringBirthday(String mStringBirthday) {
		this.mStringBirthday = mStringBirthday;
	}

	public void setBirthday(Date mBirthday) {
		this.mBirthday = mBirthday;
	}

	public String getTelephone() {
		return mTelephone;
	}

	public void setTelephone(String mTelephone) {
		this.mTelephone = mTelephone;
	}

	public String getJob() {
		return mJob;
	}

	public void setJob(String mJob) {
		this.mJob = mJob;
	}

	public Status getmStatus() {
		return mStatus;
	}

	public String getArra() {
		return this.abbra;
	}

	public void updateStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public String getFax() {
		return this.mFax;
	}

	public void setFax(String fax) {
		this.mFax = fax;
	}

	public int getAuthtype() {
		return this.mAuthtype;
	}

	public void setAuthtype(int authtype) {
		this.mAuthtype = authtype;
	}

	public String getAccount() {
		return this.mAccount;
	}

	public void setAccount(String acc) {
		this.mAccount = acc;
	}

	public void addUserToGroup(Group g) {
		if (g == null) {
			V2Log.e(" group is null can't add user to this group");
			return;
		}
		this.mBelongsGroup.add(g);
	}

	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String nickName) {
		this.mNickName = nickName;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void updateUser(boolean dirty) {
		this.isDirty = dirty;
	}

	public Group getFirstBelongsGroup() {
		if (this.mBelongsGroup.size() > 0) {
			for (Group g : mBelongsGroup) {
				if (g.getGroupType() != Group.GroupType.CONFERENCE) {
					return g;
				}
			}
		}
		return null;
	}

	public String getAvatarPath() {
		return mAvatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.mAvatarPath = avatarPath;
	}

	private Bitmap avatar;

	public Bitmap getAvatarBitmap() {
		if (avatar == null || avatar.isRecycled()) {
			avatar = GlobalHolder.getInstance().getUserAvatar(this.mUserId);
		}
		return avatar;
	}

	public void setAvatarBitmap(Bitmap bm) {
		this.avatar = bm;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (mUserId != other.mUserId)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (mUserId ^ (mUserId >>> 32));
		return result;
	}

	@Override
	public int compareTo(User another) {
		// make sure current user align first position
		if (this.mUserId == GlobalHolder.getInstance().getCurrentUserId()) {
			return -1;
		}
		if (another.getmUserId() == GlobalHolder.getInstance()
				.getCurrentUserId()) {
			return 1;
		}
		if (another.getmStatus() == this.mStatus) {
			return this.abbra.compareTo(another.abbra);
		}
		if (this.mStatus == Status.ONLINE) {
			return -1;
		} else if (another.getmStatus() == Status.ONLINE) {
			return 1;
		}
		if (this.mStatus == Status.LEAVE
				|| this.mStatus == Status.DO_NOT_DISTURB
				|| this.mStatus == Status.BUSY) {
			if (another.getmStatus() == Status.LEAVE
					|| another.getmStatus() == Status.DO_NOT_DISTURB
					|| another.getmStatus() == Status.BUSY) {
				return this.abbra.compareTo(another.abbra);
			} else {
				return -1;
			}
		} else if (another.getmStatus() == Status.LEAVE
				|| another.getmStatus() == Status.DO_NOT_DISTURB
				|| another.getmStatus() == Status.BUSY) {
			return 1;
		}

		return this.abbra.compareTo(another.abbra);
	}

	public String toXml() {
		DateFormat dp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String xml = "<user " + " address='"
				+ (this.getAddress() == null ? "" : EscapedcharactersProcessing
						.convert(this.getAddress()))
				+ "' "
				+ "authtype='"
				+ this.getAuthtype()
				+ "' "
				+ "birthday='"
				+ (this.mBirthday == null ? "" : dp.format(this.mBirthday))
				+ "' "
				+ "job='"
				+ (this.getJob() == null ? "" : this.getJob())
				+ "' "
				+ "mobile='"
				+ (this.getMobile() == null ? "" : EscapedcharactersProcessing
						.convert(this.getMobile()))
				+ "' "
				+ "nickname='"
				+ (this.getName() == null ? "" : EscapedcharactersProcessing
						.convert(this.getName()))
				+ "'  "
				+ "sex='"
				+ (this.getSex() == null ? "" : this.getSex())
				+ "'  "
				+ "sign='"
				+ (this.getSignature() == null ? ""
						: EscapedcharactersProcessing.convert(this
								.getSignature())) + "' " + "telephone='"
				+ (this.getTelephone() == null ? "" : this.getTelephone())
				+ "'> " + "<videolist/> </user> ";
		return xml;
	}

	/**
	 * 
	 * @param xml
	 *            <xml><user account='wenzl1' address='地址' authtype='1'
	 *            birthday='1997-12-30' bsystemavatar='1'
	 *            email='youxiang@qww.com' fax='22222'
	 *            homepage='http://wenzongliang.com' id='130' job='职务'
	 *            mobile='18610297182' nickname='显示名称' privacy='0' sex='1'
	 *            sign='签名' telephone='03702561038'/></xml>
	 * @return
	 */
	public static List<User> fromXml(String xml) {
		List<User> l = new ArrayList<User>();

		InputStream is = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList gList = doc.getElementsByTagName("user");
			Element element;
			for (int i = 0; i < gList.getLength(); i++) {
				element = (Element) gList.item(i);
				String strId = element.getAttribute("id");
				if (strId == null || strId.isEmpty()) {
					continue;
				}

				User u = new User(Long.parseLong(strId));

				u.setName(getAttribute(element, "nickname"));
				u.setNickName(getAttribute(element, "commentname"));

				u.setAccount(getAttribute(element, "account"));
				u.setSignature(getAttribute(element, "sign"));
				u.setSex(getAttribute(element, "sex"));
				u.setTelephone(getAttribute(element, "telephone"));
				u.setMobile(getAttribute(element, "mobile"));

				u.setFax(getAttribute(element, "fax"));
				u.setJob(getAttribute(element, "job"));

				u.setEmail(getAttribute(element, "email"));
				u.setAddress(getAttribute(element, "address"));
				u.setmStringBirthday(getAttribute(element, "birthday"));

				String authType = getAttribute(element, "authtype");
				if (authType == null) {
					u.setAuthtype(0);
				} else {
					u.setAuthtype(Integer.parseInt(authType));
				}

				l.add(u);

			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return l;
	}

	private static String getAttribute(Element el, String name) {
		Attr atr = el.getAttributeNode(name);
		if (atr != null) {
			return atr.getValue();
		}
		return null;
	}

	


	public enum DeviceType {
		CELL_PHONE(2), PC(1), UNKNOWN(-1);
		private int code;

		private DeviceType(int code) {
			this.code = code;
		}

		public int toIntValue() {
			return code;
		}

		public static DeviceType fromInt(int type) {
			switch (type) {
			case 1:// pc
				return PC;
			case 2:// 安卓
			case 3:// IOS
			case 4:// sip,h323
				return CELL_PHONE;
			default:
				return UNKNOWN;
			}
		}
	}

	public enum Status {
		LEAVE(2), BUSY(3), DO_NOT_DISTURB(4), HIDDEN(5), ONLINE(1), OFFLINE(0), UNKNOWN(
				-1);
		private int code;

		private Status(int code) {
			this.code = code;
		}

		public int toIntValue() {
			return code;
		}

		public static Status fromInt(int status) {
			switch (status) {
			case 0:
				return OFFLINE;
			case 1:
				return ONLINE;
			case 2:
				return LEAVE;
			case 3:
				return BUSY;
			case 4:
				return DO_NOT_DISTURB;
			case 5:
				return HIDDEN;
			default:
				return UNKNOWN;
			}
		}
	}

}
