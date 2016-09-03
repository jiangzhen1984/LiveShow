package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.V2.jni.callback.GroupRequestCallback;

public class GroupRequest {
	private static GroupRequest mGroupRequest;
	private List<WeakReference<GroupRequestCallback>> mCallBacks;

	private GroupRequest() {
		mCallBacks = new ArrayList<WeakReference<GroupRequestCallback>>();
	}

	public static synchronized GroupRequest getInstance() {
		if (mGroupRequest == null) {
			synchronized (GroupRequest.class) {
				if (mGroupRequest == null) {
					mGroupRequest = new GroupRequest();
				}
			}
		}
		return mGroupRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(GroupRequestCallback callback) {
		mCallBacks.add(new WeakReference<GroupRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(GroupRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(GroupRequest request);

	public native void unInitialize();

	/**
	 * @brief 创建一个组（会议/群/讨论组）
	 *
	 * @param nGroupType
	 *            组类型
	 * @param szGroupXml
	 *            组的基本信息Xml 群
	 *            <crowd authtype="0/1/2" name="群组名字" size="500"/> 讨论组
	 *            <discussion/> 会议
	 *            <conf chairuserid="115" createuserid="115" starttime=
	 *            "1443491858" subject="111"/>
	 * @param szUsersXml
	 *            邀请的用户列表Xml
	 *            <users> <user id="34" nickname="sdfa"> <user id="444" nickname
	 *            ="sdffa"> </users>
	 * 
	 * @return None
	 */
	public native void GroupCreate(int nGroupType, String szGroupXml, String szUsersXml);

	/**
	 * @brief 修改组的基本信息
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szGroupXml
	 *            组的基本信息
	 *
	 * @return None
	 */
	public native void GroupModify(int nGroupType, long nGroupID, String szGroupXml);

	/**
	 * @brief 销毁一个组
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 *
	 * @return None
	 */
	public native void GroupDestroy(int nGroupType, long nGroupID);

	/**
	 * @brief 从一个组中退出
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 *
	 * @return None
	 */
	public native void GroupLeave(int nGroupType, long nGroupID);

	/**
	 * @brief 邀请多个用户加入组
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupInfo
	 *            组的信息，xml格式
	 * @param userInfo
	 *            用户列表，Xml格式
	 * @param additInfo
	 *            没有用到，填""
	 * @return None
	 */
	public native void GroupInviteUsers(int nGroupType, String nGroupInfo, String userInfo, String additInfo);

	/**
	 * 接受被邀请加入组
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param nDstUserID
	 *            the userID of Group's creator , No was Invited userID 群主用户的id
	 */
	public native void GroupAcceptInvite(int nGroupType, long nGroupID, long nDstUserID);

	/**
	 * 拒绝被邀请加入组
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param nDstUserID
	 *            the creator user id of crowd group , not current login user id
	 * @param reason
	 */
	public native void GroupRefuseInvite(int nGroupType, long nGroupID, long nDstUserID, String szReason);

	/**
	 * 移动好友到指定组
	 * 
	 * @param nGroupType
	 * @param srcGroupID
	 * @param dstGroupID
	 * @param nUserID
	 */
	public native void GroupMoveUserTo(int nGroupType, long srcGroupID, long dstGroupID, long nUserID);

	/**
	 * @brief 从组中移除一个用户
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            被删的用户ID
	 *
	 * @return None
	 */
	public native void GroupKickUser(int nGroupType, long nGroupID, long nUserID);

	/**
	 * 申请加入组
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sAdditInfo
	 */
	public native void GroupApplyJoin(int nGroupType, long nGroupID, String sAdditInfo);

	/**
	 * 同意申请加入组
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param nUserID
	 *            the userID of User that apply join group
	 */
	public native void GroupAcceptApplyJoin(int nGroupType, long nGroupID, long nUserID);

	/**
	 * 拒绝申请加入组
	 * 
	 * @param nGroupType
	 * @param nGroupId
	 * @param nUserID
	 * @param sReason
	 */
	public native void GroupRefuseApplyJoin(int nGroupType, long nGroupId, long nUserID, String sReason);

	/**
	 * 组中上传文件
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param szFileXml
	 */
	public native void FileTransUploadGroupFile(int nGroupType, long nGroupID, String szFileXml);

	/**
	 * 删除组中的文件
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param szFileID
	 *            files' UUID
	 */
	public native void FileTransDeleteGroupFile(int nGroupType, long nGroupID, String szFileID);

	/**
	 * 获得组中文件信息
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 */
	public native void FileTransEnumGroupFiles(int nGroupType, long nGroupID);

	/**
	 * 搜索组
	 * 
	 * @param nGroupType
	 * @param szUnsharpName
	 * @param nStartNum
	 * @param nSearchNum
	 */
	public native void GroupSearch(int nGroupType, String szUnsharpName, int nStartNum, int nSearchNum);

	/**
	 * 创建会议文档共享
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sFileName
	 * @param bStorePersonalSpace
	 *            文档信息是否要保存到服务器上
	 */
	public native void WBoardCreateDocShare(int nGroupType, long nGroupID, String sFileName,
			boolean bStorePersonalSpace);
	
	
	public native void WBoardCreateDocShare(int dockId, long groupId, String name, boolean b1, boolean b2);

	/**
	 * 组中应用程序共享
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sMediaID
	 * @param nPid
	 * @param type
	 */
	// public native void DocShareGroupCreate(int nGroupType, long nGroupID,
	// String sMediaID, int nPid, int type);

	/**
	 * 获取组信息
	 * 
	 * @param type
	 * @param groupId
	 */
	// public native void getGroupInfo(int type, long groupId);

	/**
	 * 从服务器已有的文件上传到组中
	 * 
	 * @param nGroupType
	 * @param nGroupId
	 * @param sFileID
	 * @param sFileInfo
	 */
	// public native void groupUploadFileFromServer(int nGroupType, long
	// nGroupId, String sFileID, String sFileInfo);

	/**
	 * 文件重命名
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sFileID
	 * @param sNewName
	 */
	// public native void renameGroupFile(int nGroupType, long nGroupID, String
	// sFileID, String sNewName);

	/**
	 * 组中关闭程序共享
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sMediaID
	 */
	// public native void groupDestroyAppShare(int nGroupType, long nGroupID,
	// String sMediaID);

	/**
	 * 创建白板
	 * 
	 * @param nGroupType
	 * @param groupId
	 * @param nWhiteIndex
	 */
	// public native void groupCreateWBoard(int nGroupType, long groupId, int
	// nWhiteIndex);

	/**
	 * 销毁白板
	 * 
	 * @param groupId
	 * @param szMediaID
	 */
	public native void WBoardDestroy(int nGroupType, long groupId, String szMediaID);

	/**
	 * @brief 获得组信息回调函数
	 *
	 * @param nGroupType
	 *            组类型
	 * @param szGroupsXml
	 *            组列表信息xml xml格式如下： eGroupType = EGROUPTYPE_PUBGROUP(公共组) sXml =
	 *            <xml> <pubgroup id="" name=""/> <pubgroup id="" name=""/>
	 *            </xm> eGroupType = EGROUPTYPE_FRIGROUP(好友组) sXml =
	 *            <xml> <friendgroup id='' name=''/> <friendgroup id=''
	 *            name=''/> </xm> eGroupType = EGROUPTYPE_CROWDGROUP(群组) sXml =
	 *            <xml> <crowd announcement='' creatornickname=''
	 *            creatoruserid='' id='' name='' authtype='' size=''
	 *            summary=''/> <crowd announcement='' creatornickname=''
	 *            creatoruserid='' id='' name='' authtype='' size=''
	 *            summary=''/> </xm> eGroupType = EGROUPTYPE_CONFGROUP(会议) sXml
	 *            = < > <conf id = '' subject = '' createuserid = ''
	 *            createusernickname = '' starttime = ''/> <conf id = '' subject
	 *            = '' createuserid = '' createusernickname = '' starttime =
	 *            ''/> </xml>
	 * @return None。
	 */
	private void OnGroupListReport(int nGroupType, String szGroupsXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGetGroupInfo(nGroupType, szGroupsXml);
			}
		}
	}

	/**
	 * @brief 获得组中用户列表的回调函数
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szUsersXml
	 *            用户列表xml xml格式如下：
	 *            <xml> <user accounttype="1" id="" nickname="" mobile=""
	 *            telephone="" address="" avatarlocation="" avatarname=""
	 *            birthday="" bsystemavatar="" commentname="" crowdrole="" email
	 *            ="" fax="" homepage="" job="" needauth="" privacy="" sex=""
	 *            sign="" smsid="" smsno="" uetype = ''/> <user accounttype =
	 *            '2' id = '' nickname = '' mobile = '' telephone = ''/> </xml>
	 *            accounttype : (1普通用户， 2手机用户)
	 *
	 * @return None。
	 */
	private void OnGroupUserListReport(int nGroupType, long nGroupID, String szUsersXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGetGroupUserInfoCallback(nGroupType, nGroupID, szUsersXml);
			}
		}
	}

	/**
	 * @brief 收到组中有用户增加的回调函数
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szUserXml
	 *            用户基本信息XML
	 *
	 * @return None
	 */
	private void OnGroupUserAdded(int nGroupType, long nGroupID, String szUserXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAddGroupUserInfoCallback(nGroupType, nGroupID, szUserXml);
			}
		}
	}

	/**
	 * @brief 收到组中有用户被删除的回调函数
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            用户ID
	 *
	 * @return None
	 */
	private void OnGroupUserDeleted(int nGroupType, long nGroupID, long nUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDelGroupUserCallback(nGroupType, nGroupID, nUserID);
			}
		}
	}

	/**
	 * @brief 当前用户被从组中踢出
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nAdminUserID
	 *            踢人者用户ID
	 *
	 * @return None
	 */
	private void OnGroupMeKicked(int nGroupType, long nGroupID, long nAdminUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnKickGroupUser(nGroupType, nGroupID, nAdminUserID);
			}
		}
	}

	private void OnGroupAdded(int nGroupType, long nParentID, long nGroupID, String sXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().onAddGroupInfo(nGroupType, nParentID, nGroupID, sXml);
			}
		}
	}

	private void OnModifyGroupInfo(int nGroupType, long nGroupID, String sXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnModifyGroupInfo(nGroupType, nGroupID, sXml);
			}
		}
	}

	/**
	 * @brief 组被销毁
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param bDelUser
	 *            （true删除组中用户， false不删除组中用户）
	 *
	 * @return None
	 */
	private void OnGroupDestroyed(int nGroupType, long nGroupID, boolean bDelUser) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDelGroupCallback(nGroupType, nGroupID, bDelUser);
			}
		}
	}

	/**
	 * @brief 收到某个用户邀请我加入某个组的回调
	 *
	 * @param nGroupType
	 *            组类型
	 * @param szGroupXml
	 *            组ID
	 * @param userInfo
	 *            邀请者用户列表
	 * @param additInfo
	 *            没用上，填""
	 *
	 * @return None
	 */
	private void OnGroupUserInvite(int nGroupType, String nGroupID, String userInfo, String additInfo) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnInviteJoinGroup(nGroupType, nGroupID, userInfo, additInfo);
			}
		}
	}

	/**
	 * @brief 用户被移动到一个新的组的回调
	 *
	 * @param nGroupType
	 *            组类型
	 * @param srcGroupID
	 *            原组ID
	 * @param dstGroupID
	 *            新组ID
	 * @param nUserID
	 *            被移动的用户ID
	 *
	 * @return None
	 */
	private void OnGroupUserMovedTo(int nGroupType, long srcGroupID, long dstGroupID, long nUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnMoveUserToGroup(nGroupType, srcGroupID, dstGroupID, nUserID);
			}
		}
	}

	/**
	 * @brief 邀请用户加入组的邀请被拒绝的回调
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            被邀请的用户ID
	 * @param szReason
	 *            拒绝理由
	 *
	 * @return None
	 */
	private void OnGroupInviteRefused(int nGroupType, long nGroupID, long nUserID, String szReason) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnRefuseInviteJoinGroup(nGroupType, nGroupID, nUserID, szReason);
			}
		}
	}

	private void OnGroupJoinFailured(int nGroupType, long nGroupID, int nErrorNo) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnJoinGroupError(nGroupType, nGroupID, nErrorNo);
			}
		}
	};

	private void OnGroupApplyJoined(int nGroupType, long nGroupID, String userInfo, String reason) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnApplyJoinGroup(nGroupType, nGroupID, userInfo, reason);
			}
		}
	}

	private void OnGroupAcceptApplyJoined(int nGroupType, String sXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAcceptApplyJoinGroup(nGroupType, sXml);
			}
		}
	}

	private void OnGroupRefuseApplyJoined(int nGroupType, String sXml, String reason) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnRefuseApplyJoinGroup(nGroupType, sXml, reason);
			}
		}
	}

	/**
	 * @brief 邀请用户加入组的邀请被接受的回调
	 *
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param nUserID
	 *            被邀请的用户ID
	 *
	 * @return None
	 */
	private void OnGroupInviteAccepted(int nGroupType, long nGroupID, long nUserID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAcceptInviteJoinGroup(nGroupType, nGroupID, nUserID);
			}
		}
	}

	/**
	 * 创建个人空间的文档共享
	 * 
	 * @param nGroupType
	 * @param nGroupID
	 * @param sFileID
	 * @param sFileName
	 * @param nFileSize
	 * @param nPageCount
	 * @param sDownUrl
	 */
	// public native void groupCreatePersonalSpaceDoc(int nGroupType, long
	// nGroupID, String sFileID, String sFileName,
	// long nFileSize, int nPageCount, String sDownUrl);

	/**
	 * @brief 组中添加文件
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szFileXml
	 *            文件信息
	 *            <file encrypttype=’加密类型’ id=’fileID’ name=’上传时的全路径’ size= 大小’
	 *            time=’ ’ uploader=’userID’ url=’下载地址’/>
	 * @return None
	 */
	private void OnGroupFileAdded(int nGroupType, long nGroupID, String szFileXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnAddGroupFile(nGroupType, nGroupID, szFileXml);
			}
		}
	}

	/**
	 * @brief 组中删除文件
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szFileID
	 *            文件ID
	 * 
	 * @return None
	 */
	private void OnGroupFileDeleted(int nGroupType, long nGroupID, String szFileID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnDelGroupFile(nGroupType, nGroupID, szFileID);
			}
		}
	}

	private void OnGroupFilesEnumResult(int nGroupType, long nGroupId, String sXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGetGroupFileInfo(nGroupType, nGroupId, sXml);
			}
		}
	}

	/**
	 * @brief 组中文件重命名
	 * 
	 * @param nGroupType
	 *            组类型
	 * @param nGroupID
	 *            组ID
	 * @param szFileID
	 *            文件ID
	 * @param szNewName
	 *            新的名字
	 * 
	 * @return None
	 */
	private void OnGroupFileRenamed(int nGroupType, long nGroupID, String sFileID, String sNewName) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnRenameGroupFile(nGroupType, nGroupID, sFileID, sNewName);
			}
		}
	};

	private void OnGroupCreateWBoard(int nGroupType, long nGroupID, String szWBoardID, int nWhiteIndex) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGroupCreateWBoard(nGroupType, nGroupID, szWBoardID, nWhiteIndex);
			}
		}
	}

	private void OnWBoardDestroyed(int nGroupType, long nGroupID, String szWBoardID) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnWBoardDestroy(nGroupType, nGroupID, szWBoardID);
			}
		}
	};

	private void OnWBoardDocShareCreated(int nGroupType, long nGroupID, String szWBoardID, String szFileName) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGroupCreateDocShare(nGroupType, nGroupID, szWBoardID, szFileName);
			}
		}
	};

	private void OnSearchGroup(int nGroupType, String InfoXml) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<GroupRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSearchGroup(nGroupType, InfoXml);
			}
		}
	}
}
