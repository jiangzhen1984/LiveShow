package com.V2.jni.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.text.TextUtils;

import com.V2.jni.V2GlobalEnum;
import com.V2.jni.ind.FileJNIObject;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;

public class XmlAttributeExtractor {

	
	
	public static String extract(String str, String startStr, String endStr) {
		if (startStr == null || endStr == null || startStr.isEmpty()
				|| endStr.isEmpty()) {
			return null;
		}
		int start = str.indexOf(startStr);
		if (start == -1) {
			return null;
		}
		int len = startStr.length();
		int end = str.indexOf(endStr, start + len);
		if (end == -1) {
			return null;
		}
		return str.substring(start + len, end);
	}
	
	/**
	 * <crowd announcement='ss' authtype='0' creatoruserid='12176' id='423' name='ccc' size='500' summary='bb'/>

	 * @param str
	 * @param attribute   just input creatoruserid
	 * @return
	 */
	public static String extractAttribute(String str, String attribute) {
		if (str == null || attribute == null || attribute.isEmpty()
				) {
			return null;
		}
		String key =" "+ attribute;
		int start = str.indexOf(key);
		if (start == -1) {
			return null;
		}
		int len = key.length()+ 2;
		int end = str.indexOf(" ", start + len);
		if (end == -1) {
			end = str.indexOf("/", start + len);
			if (end  == -1) {
				return null;
			}
		}
		return str.substring(start + len, end - 1);
	}

	/**
	 * 
	 * @param xml
	 * @param tag
	 * @return
	 */
	public static List<V2User> parseUserList(String xml, String tag) {
		Document doc = buildDocument(xml);
		List<V2User> listUser = new ArrayList<V2User>();
		NodeList userNodeList = doc.getElementsByTagName(tag);
		Element userElement;

		for (int i = 0; i < userNodeList.getLength(); i++) {
			userElement = (Element) userNodeList.item(i);
			V2User user = null;
			String uid = userElement.getAttribute("id");
			if (uid != null && !uid.isEmpty()) {
				user = new V2User(Long.parseLong(uid));
				String name = userElement.getAttribute("nickname");
				user.name = name;
				listUser.add(user);
			}
		}

		return listUser;
	}

	public static List<V2Group> parseConference(String xml) {
		Document doc = buildDocument(xml);
		if (doc == null) {
			return null;
		}
		List<V2Group> listConf = new ArrayList<V2Group>();
		NodeList conferenceList = doc.getElementsByTagName("conf");
		Element conferenceElement;
		for (int i = 0; i < conferenceList.getLength(); i++) {
			conferenceElement = (Element) conferenceList.item(i);
			String chairManStr = conferenceElement.getAttribute("chairman");
			long cid = 0;
			if (chairManStr != null && !chairManStr.isEmpty()) {
				cid = Long.parseLong(chairManStr);
			}

			String time = conferenceElement.getAttribute("starttime");
			Long times = Long.valueOf(time) * 1000;
			Date date = new Date(times);
			String name = conferenceElement.getAttribute("subject");
			String uid = conferenceElement.getAttribute("createuserid");
			V2User user = null;
			if (uid != null && !uid.isEmpty()) {
				user = new V2User(Long.parseLong(uid));
			}

			listConf.add(new V2Group(Long.parseLong(conferenceElement
					.getAttribute("id")), name, V2Group.TYPE_CONF, user, date,
					new V2User(cid)));
		}
		return listConf;
	}
	
	public static V2Group parseSingleCrowd(String sXml){
		
		if(TextUtils.isEmpty(sXml)){
			V2Log.e("XmlAttributeExtractor parseSingleCrowd --> parse failed , given xml is null");
			return null;
		}
		
		String name = XmlAttributeExtractor.extract(sXml, "name='", "'");
		String id = XmlAttributeExtractor.extract(sXml, " id='", "'");
		V2Group g = new V2Group(Long.parseLong(id), name, V2GlobalEnum.GROUP_TYPE_CROWD);

		String summary = XmlAttributeExtractor.extract(sXml, "summary='", "'");
		String announcement = XmlAttributeExtractor.extract(sXml,
				"announcement='", "'");
		String authtype = XmlAttributeExtractor
				.extract(sXml, "authtype='", "'");
		g.announce = announcement;
		g.brief = summary;
		if (authtype != null) {
			g.authType = Integer.parseInt(authtype);
		}
		String creatoruserid = XmlAttributeExtractor.extract(sXml,
				"creatoruserid='", "'");
		if (creatoruserid != null) {
			V2User u = new V2User();
			u.uid = Long.parseLong(creatoruserid);
			g.owner = u;
			g.creator = u;
		}
		return g;
	}

	public static List<V2Group> parseCrowd(String xml) {
		xml = EscapedcharactersProcessing.convertAmp(xml);
		Document doc = buildDocument(xml);
		if(doc == null){
			V2Log.e("XmlAttributeExtractor parseCrowd --> parse xml failed...get Document is null...xml is : " + xml);
			return null;
		}
		List<V2Group> listCrowd = new ArrayList<V2Group>();
		NodeList crowdList = doc.getElementsByTagName("crowd");
		Element crowdElement;

		for (int i = 0; i < crowdList.getLength(); i++) {
			crowdElement = (Element) crowdList.item(i);
			V2User creator = null;
			String uid = crowdElement.getAttribute("creatoruserid");
			if (uid != null && !uid.isEmpty()) {
				creator = new V2User(Long.parseLong(uid),
						crowdElement.getAttribute("creatornickname"));
			}

			String id = crowdElement.getAttribute("id");
			if(TextUtils.isEmpty(id)){
				V2Log.e("parseCrowd the id is wroing...break");
				continue;
			}
			
			if (crowdElement.getAttribute("name") == null)
				V2Log.e("parseCrowd the name is wroing...the group is :"
						+ crowdElement.getAttribute("id"));
			long gid = Long.parseLong(crowdElement
					.getAttribute("id"));
			
			String crowdName = crowdElement.getAttribute("name");
			crowdName = EscapedcharactersProcessing.reverse(crowdName);
			
			V2Group crowd = new V2Group(gid, crowdName,
					V2Group.TYPE_CROWD, creator);
			crowd.brief = crowdElement.getAttribute("summary");
			crowd.announce = crowdElement.getAttribute("announcement");
			crowd.creator = creator;
			String authType = crowdElement.getAttribute("authtype");
			crowd.authType = authType == null ? 0 : Integer.parseInt(authType);
			listCrowd.add(crowd);
		}

		return listCrowd;
	}

	public static List<V2Group> parseContactsGroup(String xml) {
		Document doc = buildDocument(xml);
		if (doc == null) {
			return null;
		}
		if (doc.getChildNodes().getLength() <= 0) {
			return null;
		}
		List<V2Group> list = new ArrayList<V2Group>();
		iterateNodeList(V2Group.TYPE_CONTACTS_GROUP, null, doc.getChildNodes()
				.item(0).getChildNodes(), list);
		return list;
	}

	public static List<V2Group> parseOrgGroup(String xml) {
		Document doc = buildDocument(xml);
		if (doc == null) {
			return null;
		}
		if (doc.getChildNodes().getLength() <= 0) {
			return null;
		}
		List<V2Group> list = new ArrayList<V2Group>();
		iterateNodeList(V2Group.TYPE_ORG, null, doc.getChildNodes().item(0)
				.getChildNodes(), list);

		return list;
	}

	private static void iterateNodeList(int type, V2Group parent,
			NodeList gList, List<V2Group> list) {

		for (int j = 0; j < gList.getLength(); j++) {
			Element subGroupEl = (Element) gList.item(j);
			V2Group group = null;

			group = new V2Group(Long.parseLong(subGroupEl.getAttribute("id")),
					subGroupEl.getAttribute("name"), type);
			// If type is contact and is first item, means this group is default
			if (type == V2Group.TYPE_CONTACTS_GROUP && j == 0) {
				group.isDefault = true;
				// TODO use localization
				group.name = "我的好友";
			}

			if (parent == null) {
				list.add(group);
			} else {
				parent.childs.add(group);
				group.parent = parent;
			}
			// Iterate sub group
			iterateNodeList(type, group, subGroupEl.getChildNodes(), null);
		}

	}

	/**
	 * <file encrypttype='1' id='C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA'
	 * name='83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif'
	 * size='497236'
	 * time='1411112464' uploader='11029'
	 * url='http://192.168.0.38:8090/crowd/C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA/
	 * C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA/83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif'/>
	 * @param xml
	 */
	public static List<FileJNIObject> parseFiles(String xml) {
		Document doc = buildDocument(xml);
		if (doc == null) {
			return null;
		}
		if (doc.getChildNodes().getLength() <= 0) {
			return null;
		}

		List<FileJNIObject> list = new ArrayList<FileJNIObject>();
		NodeList nList = doc.getChildNodes().item(0).getChildNodes();
		if(nList.getLength() <= 0){
			Element el = (Element) doc.getChildNodes().item(0);
			buildUploadFiles(list, el);
		}
		else{
			for (int j = 0; j < nList.getLength(); j++) {
				Element el = (Element) nList.item(j);
				buildUploadFiles(list, el);
			}
		}
		return list;
	}

	/**
	 * build upload file Object
	 * @param list
	 * @param el
	 */
	private static void buildUploadFiles(List<FileJNIObject> list, Element el) {
		String id = el.getAttribute("id");
		String name = el.getAttribute("name");
		String uploader = el.getAttribute("uploader");
		String url = el.getAttribute("url");
		String size = el.getAttribute("size");
		int index = name.lastIndexOf("/");
		if (index != -1) {
			name = name.substring(index);
		}

		FileJNIObject file = new FileJNIObject(new V2User(
				Long.parseLong(uploader)), id, name, Long.parseLong(size),
				1);
		file.url = url;
		list.add(0 , file);
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
	
	
    /**
     *
     * @param xml
     * @return
     */
    public static V2User fromGroupXml(String xml){
        String id = extractAttribute(xml, "id");
        if(TextUtils.isEmpty(id))
            return null;
        else
            return fromXml(Long.valueOf(id) , xml);
    }

	/**
	 * @param xml
	 * @return
	 */
	public static V2User fromXml(long userID , String xml) {

		String nickName = extractAttribute(xml, "nickname");
		String signature = extractAttribute(xml,"sign");
		String job = extractAttribute(xml,"job");
		String telephone = extractAttribute(xml,"telephone");
		String mobile = extractAttribute(xml,"mobile");
		String address = extractAttribute(xml,"address");
		String gender = extractAttribute(xml,"sex");
		String email = extractAttribute(xml,"email");
		String bir = extractAttribute(xml,"birthday");
		String account = extractAttribute(xml,"account");
		String fax = extractAttribute(xml,"fax");
		String commentname = extractAttribute(xml,"commentname");
		String authtype = extractAttribute(xml,"authtype");
		DateFormat dp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

		V2User u = new V2User(userID, nickName);
		u.mSignature = signature;
		u.mJob=job;
		u.mTelephone = telephone;
		u.mMobile = mobile;
		u.mAddress =address;
		u.mSex = gender;
		u.mEmail = email;
		u.mFax = fax;
		u.mCommentname = commentname;
		u.mAccount = account;
		if (authtype != null && authtype != "") {
			u.mAuthtype = Integer.parseInt(authtype);
		} else {
			u.mAuthtype = 0;
		}

		if (bir != null && bir.length() > 0) {
			try {
				u.mBirthday = dp.parse(bir);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return u;
	}
}
