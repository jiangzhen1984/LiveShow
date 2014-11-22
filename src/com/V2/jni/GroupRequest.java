package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;




import com.V2.jni.ind.FileJNIObject;
import com.V2.jni.ind.GroupQualicationJNIObject;
import com.V2.jni.ind.V2Document;
import com.V2.jni.ind.V2Group;
import com.V2.jni.ind.V2User;
import com.V2.jni.util.EscapedcharactersProcessing;
import com.V2.jni.util.V2Log;
import com.V2.jni.util.XmlAttributeExtractor;


public class GroupRequest {

	public boolean loginResult;
	private static GroupRequest mGroupRequest;

	private List<WeakReference<GroupRequestCallback>> mCallbacks;

	private GroupRequest() {
		mCallbacks = new CopyOnWriteArrayList<WeakReference<GroupRequestCallback>>();
	};

	public static synchronized GroupRequest getInstance() {
		if (mGroupRequest == null) {
			mGroupRequest = new GroupRequest();
			if (!mGroupRequest.initialize(mGroupRequest)) {
				throw new RuntimeException(
						" can't not inintialize group request");
			}
		}
		return mGroupRequest;
	}


	public native boolean initialize(GroupRequest request);

	public native void unInitialize();

	/**
	 * <ul>
	 * delete group. If groupType is {@link V2Group#TYPE_CROWD}, dismiss current
	 * crowd
	 * </ul>
	 * 
	 * @param groupType
	 * @param nGroupID
	 */
	public native void delGroup(int groupType, long nGroupID);

	/**
	 * <ul>
	 * quit from group if user is not administrator or creator
	 * </ul>
	 * 
	 * @param groupType
	 *            {@link V2Group}
	 * @param nGroupID
	 */
	public native void leaveGroup(int groupType, long nGroupID);

	/**
	 * Remove user from group
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 */
	public native void delGroupUser(int groupType, long nGroupID, long nUserID);

	/**
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	public native void modifyGroupInfo(int groupType, long nGroupID, String sXml);

	/**
	 * // sXmlConfData : // <conf canaudio="1" candataop="1" canvideo="1"
	 * conftype="0" haskey="0" // id="0" key="" // layout="1" lockchat="0"
	 * lockconf="0" lockfiletrans="0" mode="2" // pollingvideo="0" //
	 * subject="ss" syncdesktop="0" syncdocument="1" syncvideo="0" //
	 * chairuserid='0' chairnickname=''> // </conf> // szInviteUsers : // <xml>
	 * // <user id="11760" nickname=""/> // <user id="11762" nickname=""/> //
	 * </xml>
	 * 
	 * @param groupType
	 * @param groupInfo
	 * @param userInfo
	 */
	public native void createGroup(int groupType, String groupInfo,
			String userInfo);

	/**
	 * <ul>
	 * Invite user to join group.
	 * <ul>
	 * <ul>
	 * If groupType is {@link V2Group#TYPE_CONF} <br>
	 * groupInfo is :
	 * {@code <conf canaudio="1" candataop="1" canvideo="1" conftype="0" haskey="0" 
	 * id="0" key=""  layout="1" lockchat="0" lockconf="0" lockfiletrans="0"
	 * mode="2"  pollingvideo="0"  subject="ss"  chairuserid='0'
	 * chairnickname=''>  </conf>}
	 * </ul>
	 * 
	 * <ul>
	 * If groupType is {@link V2Group#TYPE_CROWD}<br>
	 * groupInfo is:
	 * {@code <crowd id="" name="" authtype="" size="" announcement=""  summary="" />}
	 * </ul>
	 * 
	 * @param groupType
	 *            {@code V2Group}
	 * @param groupInfo
	 *            group information
	 * @param userInfo
	 *            {@code <userlist><user id='1' /><user id='2' /></userlist>}
	 * @param additInfo
	 */
	public native void inviteJoinGroup(int groupType, String groupInfo,
			String userInfo, String additInfo);

	/**
	 * 
	 * @param groupType
	 * @param srcGroupID
	 * @param dstGroupID
	 * @param nUserID
	 */
	public native void moveUserToGroup(int groupType, long srcGroupID,
			long dstGroupID, long nUserID);

	/**
	 * 
	 * @param type
	 * @param groupId
	 */
	public native void getGroupInfo(int type, long groupId);

	/**********************************************/

	/**
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 *            the creator user id of crowd group , not current login user id
	 * @param reason
	 */
	public native void refuseInviteJoinGroup(int groupType, long nGroupID,
			long nUserID, String reason);

    /**
     * send application of join group
     *
     * @param groupType
     * @param nGroupID
     * @param sAdditInfo
     */
	public native void applyJoinGroup(int groupType, long nGroupID,
			String sAdditInfo);

	/**
	 * accept application of join group
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param nUserID
	 *            the userID of User that apply join group
	 */
	public native void acceptApplyJoinGroup(int groupType, long nGroupID,
			long nUserID);

	/**
	 * accept invitation of join group
	 * 
	 * @param groupType
	 * @param groupId
	 * @param nUserID
	 *            the userID of Group's creator , No was Invited userID 群主用户的id
	 */
	public native void acceptInviteJoinGroup(int groupType, long groupId,
			long nUserID);

	/**
	 * 创建白板
	 * 
	 * @param groupType
	 * @param groupId
	 * @param nWhiteIndex
	 */
	public native void groupCreateWBoard(int groupType, long groupId,
			int nWhiteIndex);

    /**
     * 销毁白板
     * @param groupId
     * @param szMediaID
     */
	public native void groupDestroyWBoard(long groupId, String szMediaID);

	/**
	 * 创建会议文档共享
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sFileName
	 * @param eWhiteShowType
	 *            类型
	 * @param bStorePersonalSpace
	 *            文档信息是否要保存到服务器上
	 */
	public native void groupCreateDocShare(int eGroupType, long nGroupID,
			String sFileName, int eWhiteShowType, boolean bStorePersonalSpace);

    /**
     * accept the invitation that join group
     * @param groupType
     * @param groupId
     * @param nUserID
     */
	private void OnAcceptInviteJoinGroup(int groupType, long groupId,
			long nUserID) {
		V2Log.d("Group Request  OnAcceptInviteJoinGroup  ==>" + groupType
				+ "   " + groupId + "  " + nUserID);
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnAcceptInviteJoinGroup(groupType, groupId, nUserID);
			}
		}
	}

	public void OnConfSyncOpenVideo(String str) {

	}

    /**
     * Reject application of join group
     *
     * @param groupType
     * @param nGroupId
     * @param nUserID
     * @param sReason
     */
	public native void refuseApplyJoinGroup(int groupType, long nGroupId,
			long nUserID, String sReason);

	/**
	 * Upload group file
	 * 
	 * @param groupType
	 * @param nGroupId
	 * @param sXml
	 *            {@code <file encrypttype='1' id='C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA'
	 * name='83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif' size='497236'
	 * time='1411112464' uploader='11029' />}
	 */
	public native void groupUploadFile(int groupType, long nGroupId, String sXml);

	/**
	 * Upload existing files on server to group 从服务器已有的文件上传到组中
	 * 
	 * @param groupType
	 * @param nGroupId
	 * @param sFileID
	 * @param sFileInfo
	 */
	public native void groupUploadFileFromServer(int groupType, long nGroupId,
			String sFileID, String sFileInfo);

	/**
	 * Delete group files<br>
	 * 
	 * @param groupType
	 * @param nGroupId
	 * @param fileId
	 *            files' UUID
	 */
	public native void delGroupFile(int groupType, long nGroupId, String fileId);

	/**
	 * get group file list
	 * 
	 * @param groupType
	 * @param nGroupId
	 */
	public native void getGroupFileInfo(int groupType, long nGroupId);

	public native void renameGroupFile(int eGroupType, long nGroupID,
			String sFileID, String sNewName);

	/**
	 * search group 搜索组
	 * 
	 * @param eGroupType
	 * @param szUnsharpName
	 * @param nStartNum
	 * @param nSearchNum
	 */
	public native void searchGroup(int eGroupType, String szUnsharpName,
			int nStartNum, int nSearchNum);

	/**
	 * 
	 * @param eGroupType
	 * @param nGroupId
	 * @param sXml
	 */
	private void OnAddGroupFile(int eGroupType, long nGroupId, String sXml) {
		V2Log.d("GroupRequest UI", "OnAddGroupFile ---> eGroupType :"
				+ eGroupType + " | nGroupId: " + nGroupId + " | sXml: " + sXml);
		List<FileJNIObject> list = XmlAttributeExtractor.parseFiles(sXml);
		V2Group group = new V2Group(nGroupId, eGroupType);

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnAddGroupFile(group, list);
			}
		}
	}

	/**
	 * 
	 * @param type
	 * @param nGroupId
	 * @param fileId
	 */
	private void OnDelGroupFile(int type, long nGroupId, String fileId) {
		V2Log.e("GroupRequest  OnDelGroupFile" + type + "   " + nGroupId + "  "
				+ fileId);

		List<FileJNIObject> list = new ArrayList<FileJNIObject>();
		list.add(new FileJNIObject(null, fileId, null, 0, 0));
		V2Group group = new V2Group(nGroupId, type);

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnDelGroupFile(group, list);
			}
		}
	}

	/**
	 * 组中应用程序共享
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sMediaID
	 * @param nPid
	 * @param type
	 */
	public native void groupCreateAppShare(int eGroupType, long nGroupID,
			String sMediaID, int nPid, int type);

	/**
	 * 组中关闭程序共享
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sMediaID
	 */
	public native void groupDestroyAppShare(int eGroupType, long nGroupID,
			String sMediaID);

	/**
	 * 创建个人空间的文档共享
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sFileID
	 * @param sFileName
	 * @param nFileSize
	 * @param nPageCount
	 * @param sDownUrl
	 */
	public native void groupCreatePersonalSpaceDoc(int eGroupType,
			long nGroupID, String sFileID, String sFileName, long nFileSize,
			int nPageCount, String sDownUrl);

	/**
	 * <filelist><file encrypttype='1' id='C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA'
	 * name='83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif' size='497236'
	 * time='1411112464' uploader='11029' url=
	 * 'http://192.168.0.38:8090/crowd/C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA/C2A65B9B-63C7-4C9E-A8DD-F15F74ABA6CA/83025aafa40f4bfb24fdb8d1034f78f0f7361801.gif'
	 * / > < / f i l e l i s t >
	 * 
	 * @param groupType
	 * @param nGroupId
	 * @param sXml
	 */
	private void OnGetGroupFileInfo(int groupType, long nGroupId, String sXml) {
		V2Log.e("Group Request  OnGetGroupFileInfo" + nGroupId + "  " + sXml);
		List<FileJNIObject> list = XmlAttributeExtractor.parseFiles(sXml);
		V2Group group = new V2Group(nGroupId, groupType);

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGetGroupFileInfo(group, list);
			}
		}
	}

	/**
	 * This is unsolicited callback. This function will be call after log in
	 * 
	 * @param groupType
	 *            4 : conference
	 * @param sXml
	 */
	private void OnGetGroupInfo(int groupType, String sXml) {
		V2Log.d("OnGetGroupInfo==>" + "groupType:" + groupType + "," + "sXml:"
				+ sXml);
		List<V2Group> list = null;
		if (groupType == V2Group.TYPE_CONF) {
			list = XmlAttributeExtractor.parseConference(sXml);
		} else if (groupType == V2Group.TYPE_CROWD) {
			list = XmlAttributeExtractor.parseCrowd(sXml);
		} else if (groupType == V2Group.TYPE_CONTACTS_GROUP) {
			list = XmlAttributeExtractor.parseContactsGroup(sXml);
		} else if (groupType == V2Group.TYPE_ORG) {
			list = XmlAttributeExtractor.parseOrgGroup(sXml);
		}
		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = this.mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGetGroupInfoCallback(groupType, list);
			}
		}
	}

	public void addCallback(GroupRequestCallback callback) {
		this.mCallbacks.add(new WeakReference<GroupRequestCallback>(callback));
	}

	public void removeCallback(GroupRequestCallback callback) {
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i).get() == callback) {
				mCallbacks.remove(i);
				break;
			}
		}
	}

	/**
	 * @comment-user:wenzl 2014年9月25日
	 * @overview:
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 *            <xml><user account='wenzl1' address='地址' authtype='1'
	 *            birthday='1997-12-30' bsystemavatar='1'
	 *            email='youxiang@qww.com' fax='22222'
	 *            homepage='http://wenzongliang.com' id='130' job='职务'
	 *            mobile='18610297182' nickname='显示名称' privacy='0' sex='1'
	 *            sign='签名' telephone='03702561038'/></xml>
	 * @return:
	 */
	private void OnGetGroupUserInfo(int groupType, long nGroupID, String sXml) {
		V2Log.d("OnGetGroupUserInfo==>" + "groupType:" + groupType + ","
				+ "nGroupID:" + nGroupID + "," + "sXml:" + sXml);
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGetGroupUserInfoCallback(groupType, nGroupID, sXml);
			}
		}

	}

	/**
	 * 10-28 10:21:14.740: D/V2TECH(4636): OnAddGroupUserInfo ->1:61111:<user
	 * id='11112131'/>
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	private void OnAddGroupUserInfo(int groupType, long nGroupID, String sXml) {
		V2Log.d("OnAddGroupUserInfo ->" + groupType + ":" + nGroupID + ":"
				+ sXml);
		
		V2User remoteUser = XmlAttributeExtractor.fromGroupXml(sXml);
		if(remoteUser == null){
			V2Log.e("OnAddGroupUserInfo -> parse xml failed ...get null user : " + sXml);
			return ;
		}
		
	
		ImRequest.getInstance().getUserBaseInfo(remoteUser.uid);
		
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnAddGroupUserInfoCallback(groupType, nGroupID, remoteUser);
			}
		}
	}

	private void OnDelGroupUser(int groupType, long nGroupID, long nUserID) {
		V2Log.d("OnDelGroupUser -> " + groupType + ":" + nGroupID + ":"
				+ nUserID);
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnDelGroupUserCallback(groupType, nGroupID, nUserID);
			}
		}
	}

	/**
	 * <ul>
	 * Crowd: {@code <crowd authtype='0' id='44' name='hhh mjj ' size='100'/>}
	 * </ul>
	 * 
	 * @param groupType
	 * @param nParentID
	 * @param nGroupID
	 * @param sXml
	 */
	private void OnAddGroupInfo(int groupType, long nParentID, long nGroupID,
			String sXml) {
		V2Log.d("OnAddGroupInfo:: " + groupType + ":" + nParentID + ":"
				+ nGroupID + ":" + sXml);

		String gid = XmlAttributeExtractor.extract(sXml, " id='", "'");
		String name = XmlAttributeExtractor.extract(sXml, " name='", "'");
		String createUesrID = XmlAttributeExtractor.extract(sXml,
				" creatoruserid='", "'");
		V2Group vg = new V2Group(Long.parseLong(gid), name, groupType);
		if (gid != null && !gid.isEmpty() && createUesrID != null) {
			vg.owner = new V2User(Long.valueOf(createUesrID));
			vg.creator = vg.owner;
		}else{
			V2Log.e("OnAddGroupInfo:: parse xml failed , don't get group id or user id ...." + sXml);
		}

		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.onAddGroupInfo(vg);
			}
		}
	}

	/**
	 * TODO to be implement comment
	 * 
	 * OnModifyGroupInfo::-->3:4174:<crowd id="4174" name="u1" authtype="0"
	 * size="0" announcement="hhhh zahjhhjj" summary="njhhjjbhjjj"
	 * creatoruserid="1287"/>
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param sXml
	 */
	private void OnModifyGroupInfo(int groupType, long nGroupID, String sXml) {
		V2Log.d("OnModifyGroupInfo::-->" + groupType + ":" + nGroupID + ":"
				+ sXml);
		V2Group group = new V2Group(nGroupID, groupType);
		if (groupType == V2GlobalEnum.GROUP_TYPE_CROWD) {
			String name = XmlAttributeExtractor.extractAttribute(sXml, "name");
			String announcement = XmlAttributeExtractor.extractAttribute(sXml,
					"announcement");
			String summary = XmlAttributeExtractor.extractAttribute(sXml,
					"summary");
			String authtype = XmlAttributeExtractor.extractAttribute(sXml,
					"authtype");
			group.name = name;
			group.announce = announcement;
			group.brief = summary;
			if (authtype != null) {
				group.authType = Integer.parseInt(authtype);
			} else {
				V2Log.e("No found authtype attrbitue, use 0 as default");
			}

		} else if (groupType == V2GlobalEnum.GROUP_TYPE_CONFERENCE) {
			String sync = XmlAttributeExtractor.extract(sXml, " syncdesktop='",
					"'");
			if (sync == null || "".equals(sync)) {
				V2Log.e("Now only support syncdesktop attribute, doens't find such attirbute ");
				return;
			}
			if ("1".equalsIgnoreCase(sync)) {
				group.isSync = true;
			}
		}

		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnModifyGroupInfoCallback(group);
			}
		}

	}

	/**
	 * TODO add implement comment
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param bMovetoRoot
	 */
	private void OnDelGroup(int groupType, long nGroupID, boolean bMovetoRoot) {
		V2Log.d("OnDelGroup::==>" + groupType + ":" + nGroupID + ":"
				+ bMovetoRoot);
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnDelGroupCallback(groupType, nGroupID, bMovetoRoot);
			}
		}
	}

	/**
	 * The CallBack that invited join group from other
	 * 
	 * @param groupType
	 * @param groupInfo
	 * @param userInfo
	 * @param additInfo
	 */
	private void OnInviteJoinGroup(int groupType, String groupInfo,
			String userInfo, String additInfo) {
		V2Log.d("OnInviteJoinGroup::==>" + groupType + ":" + groupInfo + ":"
				+ userInfo + ":" + additInfo);
		V2Group group = null;
		V2User user = null;
		if (groupType == V2Group.TYPE_CONF) {
			String id = XmlAttributeExtractor.extract(groupInfo, " id='", "'");

			if (id == null || id.isEmpty()) {
				V2Log.e(" Unknow group information:" + groupInfo);
				return;
			}
			group = new V2Group(Long.parseLong(id), groupType);
			String name = XmlAttributeExtractor.extract(groupInfo, "subject='",
					"'");
			name = EscapedcharactersProcessing.reverse(name);
			String starttime = XmlAttributeExtractor.extract(groupInfo,
					"starttime='", "'");
			String createuserid = XmlAttributeExtractor.extract(groupInfo,
					"createuserid='", "'");
			group.name = name;
			group.createTime = new Date(Long.parseLong(starttime) * 1000);
			group.chairMan = new V2User(Long.valueOf(createuserid));
			group.owner = new V2User(Long.valueOf(createuserid));

		} else if (groupType == V2Group.TYPE_CROWD) {
			String id = XmlAttributeExtractor.extract(groupInfo, " id='", "'");
			if (id == null || id.isEmpty()) {
				V2Log.e(" Unknow group information:" + groupInfo);
				return;
			}
			group = new V2Group(Long.parseLong(id), groupType);
			String createuserid = XmlAttributeExtractor.extract(userInfo,
					" id='", "'");
			String auth = XmlAttributeExtractor.extract(groupInfo,
					"authtype='", "'");
			String uname = XmlAttributeExtractor.extract(userInfo,
					"nickname='", "'");
			uname = EscapedcharactersProcessing.reverse(uname);
			String name = XmlAttributeExtractor.extract(groupInfo, "name='",
					"'");
			name = EscapedcharactersProcessing.reverse(name);
			group.name = name;
			group.creator = new V2User(Long.valueOf(createuserid), uname);
			if (auth != null) {
				group.authType = Integer.parseInt(auth);
			}
		} else if (groupType == V2Group.TYPE_CONTACTS_GROUP) {
			String id = XmlAttributeExtractor.extract(userInfo, " id='", "'");
			if (id == null || id.isEmpty()) {
				V2Log.e(" Unknow user information:" + userInfo);
				return;
			}
			user = new V2User(Long.parseLong(id));
			String name = XmlAttributeExtractor.extract(userInfo,
					" nickname='", "'");
			name = EscapedcharactersProcessing.reverse(name);
			user.name = name;
		}

		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				if (groupType == V2Group.TYPE_CONTACTS_GROUP) {
					callback.OnRequestCreateRelationCallback(user, additInfo);
				} else {
					callback.OnInviteJoinGroupCallback(group);
				}
			}
		}

	}

    /**
     * this funcation was called when be invited user refused to join group
     *
     * @param groupType
     * @param nGroupID
     * @param nUserID
     * @param reason
     */
	private void OnRefuseInviteJoinGroup(int groupType, long nGroupID,
			long nUserID, String reason) {
		V2Log.d("OnRefuseInviteJoinGroup ==>" + "groupType:" + groupType + ","
				+ "nGroupID:" + nGroupID + "," + "nUserID:" + nUserID + ","
				+ "reason:" + reason);

		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				/**
				 * The callBack was only called when somebody refused the invite from you sended..
				 * SomeBody invite you to join group and you refuse it ... this callback isn't called
				 */
				callback.OnRefuseInviteJoinGroup(new GroupQualicationJNIObject(
						groupType, nGroupID, nUserID, 1,
						3 , reason));
			}
		}

		// // ƴװ������Ϣ
		// RefuseMsgType refuseMsgType = new RefuseMsgType();
		// refuseMsgType.setReason(sxml);
		// refuseMsgType.setUserBaseInfo(sxml);
		//
		// Intent addIntent = new Intent(SplashActivity.IM);
		// addIntent.putExtra("MsgType", MsgType.REFUSE_ADD);
		// addIntent.putExtra("MSG", refuseMsgType);
		// context.sendOrderedBroadcast(addIntent,null);
	}

	private void OnMoveUserToGroup(int groupType, long srcGroupID,
			long dstGroupID, long nUserID) {
		V2Log.d("OnMoveUserToGroup:: " + groupType + ":" + srcGroupID + ":"
				+ dstGroupID + ":" + nUserID);
		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnMoveUserToGroup(groupType, new V2Group(srcGroupID,
						"", groupType), new V2Group(dstGroupID, "", groupType),
						new V2User(nUserID));
			}
		}
	}

	/**
	 * 10-18 16:45:50.096: E/ImRequest UI(31807): OnApplyJoinGroup:: 3:431:<user
	 * account='bbb' accounttype='1' bsystemavatar='1' id='12176' nickname='bbb'
	 * uetype='1'/>:nnn
	 * 
	 * @param groupType
	 * @param nGroupID
	 * @param userInfo
	 * @param reason
	 */
	private void OnApplyJoinGroup(int groupType, long nGroupID,
			String userInfo, String reason) {
		V2Log.d("OnApplyJoinGroup:: " + groupType + ":" + nGroupID + ":"
				+ userInfo + ":" + reason);
		String id = XmlAttributeExtractor.extractAttribute(userInfo, "id");
		String nickname = XmlAttributeExtractor.extractAttribute(userInfo,
				"nickname");
		if (id == null || id.trim().isEmpty()) {
			return;
		}
		V2Group vg = new V2Group(nGroupID, groupType);
		V2User vu = new V2User(Long.parseLong(id), nickname);

		for (WeakReference<GroupRequestCallback> wrcb : mCallbacks) {
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnApplyJoinGroup(vg, vu, reason);
			}
		}
	}

	/**
	 * 10-10 22:11:57.505: D/V2TECH(8011): OnAcceptApplyJoinGroup
	 * ==>groupType:3,sXml:<crowd announcement='' authtype='0'
	 * creatoruserid='1290' id='492' name='12' size='0' summary=''/>
	 * 
	 * @param groupType
	 * @param sXml
	 */
	private void OnAcceptApplyJoinGroup(int groupType, String sXml) {
		V2Log.d("OnAcceptApplyJoinGroup ==>" + "groupType:" + groupType + ","
				+ "sXml:" + sXml);

		V2Group parseSingleCrowd = XmlAttributeExtractor.parseSingleCrowd(sXml);
		if (parseSingleCrowd == null)
			return;

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnAcceptApplyJoinGroup(parseSingleCrowd);
			}
		}
	}

    /**
     * The CallBack that refuse apply for join group 拒绝申请加入群回调
     *
     * @param groupType
     * @param sXml
     * @param reason
     */
	private void OnRefuseApplyJoinGroup(int groupType, String sXml,
			String reason) {
		V2Log.d("OnRefuseApplyJoinGroup ==>" + "groupType:" + groupType + ","
				+ "sXml:" + sXml + "," + "reason:" + reason);

		V2Group parseSingleCrowd = XmlAttributeExtractor.parseSingleCrowd(sXml);
		if (parseSingleCrowd == null)
			return;

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnRefuseApplyJoinGroup(parseSingleCrowd, reason);
			}
		}
	}

	/**
	 * The CallBack that join group failed 加入群失败（如群已经被删除等）
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nErrorNo
	 */
	private void OnJoinGroupError(int eGroupType, long nGroupID, int nErrorNo) {
		V2Log.e("GroupRequest UI", "OnJoinGroupError ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | nErrorNo: "
				+ nErrorNo);
		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnJoinGroupError(eGroupType, nGroupID, nErrorNo);
			}
		}
	};

	/**
	 * 会议中创建白板的回调 TODO implement
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 * @param nWhiteIndex
	 */
	private void OnGroupCreateWBoard(int eGroupType, long nGroupID,
			String szWBoardID, int nWhiteIndex) {
		V2Log.e("GroupRequest UI", "OnGroupCreateWBoard ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | szWBoardID: "
				+ szWBoardID + " | nWhiteIndex: " + nWhiteIndex);
		V2Document v2doc = new V2Document();
		v2doc.mId = szWBoardID;
		v2doc.mIndex = nWhiteIndex;
		V2Group v2group = new V2Group(nGroupID, eGroupType);
		v2doc.mGroup = v2group;
		v2doc.mType = V2Document.Type.BLANK_BOARD;

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGroupWBoardNotification(v2doc,
						GroupRequestCallback.DocOpt.CREATE);
			}
		}
	};

	/**
	 * 文件重命名
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param sFileID
	 * @param sNewName
	 */
	private void OnRenameGroupFile(int eGroupType, long nGroupID,
			String sFileID, String sNewName) {
		V2Log.e("GroupRequest UI", "OnRenameGroupFile ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | sFileID: "
				+ sFileID + " | sNewName: " + sNewName);
	};

	/**
	 * 收到白板会话被关闭的回调
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 */
	private void OnWBoardDestroy(int eGroupType, long nGroupID,
			String szWBoardID) {
		V2Log.e("GroupRequest UI", "OnWBoardDestroy ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | szWBoardID: "
				+ szWBoardID);

		V2Document v2doc = new V2Document();
		v2doc.mId = szWBoardID;
		V2Group v2group = new V2Group(nGroupID, eGroupType);
		v2doc.mGroup = v2group;
		v2doc.mType = V2Document.Type.BLANK_BOARD;

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGroupWBoardNotification(v2doc,
						GroupRequestCallback.DocOpt.DESTROY);
			}
		}
	};

	/**
	 * 会议中创建文档共享的回调 eWhiteShowType白板显示类型
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param szWBoardID
	 * @param szFileName
	 * @param eWhiteShowType
	 */
	private void OnGroupCreateDocShare(int eGroupType, long nGroupID,
			String szWBoardID, String szFileName, int eWhiteShowType) {
		V2Log.e("GroupRequest UI", "OnGroupCreateDocShare ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | szWBoardID: "
				+ szWBoardID + " | szFileName: " + szFileName
				+ " | eWhiteShowType: " + eWhiteShowType);

		V2Document v2doc = new V2Document();
		v2doc.mId = szWBoardID;
		V2Group v2group = new V2Group(nGroupID, eGroupType);
		v2doc.mGroup = v2group;
		v2doc.mType = V2Document.Type.DOCUMENT;
		v2doc.mFileName = szFileName;

		for (int i = 0; i < mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wrcb = mCallbacks.get(i);
			Object obj = wrcb.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnGroupWBoardNotification(v2doc,
						GroupRequestCallback.DocOpt.CREATE);
			}
		}
	};

	/**
	 * the CallBack after invoked search group 搜索群组回调
	 * {@code <crowdlist> <crowd announcement='' authtype='1'
	 * creatornickname='zhao1' creatoruserid='14' id='44' name='13269997886'
	 * size='500' summary=''/></crowdlist> }
	 * 
	 * @param eGroupType
	 * @param InfoXml
	 */
	private void OnSearchGroup(int eGroupType, String InfoXml) {
		V2Log.e("GroupRequest UI", "onSearchGroup ---> eGroupType :"
				+ eGroupType + " | InfoXml: " + InfoXml);
		List<V2Group> list = XmlAttributeExtractor.parseCrowd(InfoXml);
		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallbacks.get(i);
			Object obj = wf.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnSearchCrowdCallback(list);
			}
		}
	}

	/**
	 * administrator was removed from group by himself administrator 管理员把自己从组中请出
	 * 
	 * @param eGroupType
	 * @param nGroupID
	 * @param nUserID
	 */
	private void OnKickGroupUser(int eGroupType, long nGroupID, long nUserID) {
		V2Log.e("GroupRequest UI", "OnKickGroupUser ---> eGroupType :"
				+ eGroupType + " | nGroupID: " + nGroupID + " | nUserID: "
				+ nUserID);

		for (int i = 0; i < this.mCallbacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallbacks.get(i);
			Object obj = wf.get();
			if (obj != null) {
				GroupRequestCallback callback = (GroupRequestCallback) obj;
				callback.OnKickGroupUser(eGroupType, nGroupID, nUserID);
			}
		}
	}
}
