package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.ImRequestCallback;

public class ImRequest {
	private List<WeakReference<ImRequestCallback>> mCallBacks;
	private static ImRequest mImRequest;

	private ImRequest() {
		mCallBacks = new ArrayList<WeakReference<ImRequestCallback>>();
	}

	public static synchronized ImRequest getInstance() {
		if (mImRequest == null) {
			synchronized (ImRequest.class) {
				if (mImRequest == null) {
					mImRequest = new ImRequest();
					if (!mImRequest.initialize(mImRequest)) {
						throw new RuntimeException("can't initilaize ImRequest");
					}
				}
			}
		}
		return mImRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(ImRequestCallback callback) {
		this.mCallBacks.add(new WeakReference<ImRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(ImRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	/**
	 * @brief 注册jni回调对象
	 * 
	 * @param request
	 *            需要注册的对象
	 *
	 * @return 注册结果（true成功, false失败）
	 */
	public native boolean initialize(ImRequest request);

	/**
	 * 反注册jni回调对象
	 */
	public native void unInitialize();

	/**
	 * @brief 登录IM
	 *
	 * @param szName
	 *            登录帐号
	 * @param szPassword
	 *            登录密码
	 * @param status
	 *            在线状态
	 * @param nDeviceType
	 *            终端类型
	 * @param nDeviceID
	 *            终端ID
	 * @param isAnonymous
	 *            是否匿名登录
	 *
	 * @return None
	 */
	public native void ImLogin(String szName, String szPassword, int status, int nDeviceType, String nDeviceID,
			boolean isAnonymous);

	/**
	 * @brief 离开IM回调函数
	 */
	public native void ImLogout();

	/**
	 * @brief 更改状态
	 * 
	 * @param nStatus
	 *            在线状态
	 * @param szStatusDesc
	 *            在线描述
	 * @return None
	 */
	public native void ImChangeMyStatus(int nStatus, String szStatusDesc);

	/**
	 * 获取基本信息
	 * 
	 * @param nUserID
	 */
	public native void ImGetUserBaseInfo(long nUserID);

	/**
	 * 修改基本信息
	 * 
	 * @param InfoXml
	 */
	public native void ImModifyBaseInfo(String InfoXml);

	/**
	 * @brief 修改好友的备注名称
	 *
	 * @param nUserID
	 *            好友的用户ID
	 * @param sCommentName
	 *            新的备注名称
	 *
	 * @return None
	 */
	public native void ImChangeFriendMemoName(long nUserId, String sCommentName);

	/**
	 * @brief 在服务器上使用关键字查找用户
	 *
	 * @param szUnsharpName
	 *            查找关键字
	 * @param nStartNum
	 *            开始索引
	 * @param nSearchNum
	 *            查找数量
	 *
	 * @return None
	 */
	public native void ImSearchUsers(String szUnsharpName, int nStartNum, int nSearchNum);

	/**
	 * 更改系统头像
	 * 
	 * @param szAvatarName
	 */
	// public native void changeSystemAvatar(String szAvatarName);

	/**
	 * @brief 修改自定义头像.
	 * @param AvatarAddress
	 *            头像二进制数据地址.
	 * @param len
	 *            头像二进制数据长度.
	 * @param szExtensionName
	 *            头像文件扩展名.
	 */
	public native void ImChangeCustomAvatar(byte[] AvatarAddress, int len, String szExtensionName);

	/**
	 * @brief 手动下载对方头像(移动端登录, 默认不进行头像下载, 当看对方头像的时候再进行下载)
	 * @param nUserID
	 *            用户ID
	 * @param szName
	 *            名字
	 * @param szDownURL
	 *            头像地址
	 */
	public native void ImGetAvatar(long nUserID, String szName, String szDownURL);

	/**
	 * @brief 生成验证码 发送至手机
	 * @param phonenumber
	 *            手机号码
	 */
	public native void ImCreateValidateCode(String phonenumber);

	/**
	 * @brief 注册手机用户
	 * @param phonenumber
	 *            手机号
	 * @param validatecode
	 *            验证码
	 */
	public native void ImRegisterPhoneUser(String phonenumber, int validatecode);

	/**
	 * @brief 更新密码
	 * @param oldpwd
	 *            旧密码
	 * @param newpwd
	 *            新密码
	 */
	public native void ImUpdatePhoneUserPwd(String oldpwd, String newpwd);

	/**
	 * @brief 本地与服务器断开连接，请求服务器重连
	 */
	public native void ImMobileNetDisconnect();

	/**
	 * 启动自动更新
	 */
	// public native void onStartUpdate();

	/**
	 * 停止自动更新
	 */
	// public native void onStopUpdate();

	// public native void createPhoneFriend(String str);

	// public native void destroyPhoneFriend(long id);

	// public native void updatePhoneFriend(long id, String str);

	// public native void searchCrowd(String szUnsharpName, int nStartNum, int
	// nSearchNum);

	/**
	 * @brief 登录IM回调函数
	 *
	 * @param nUserID
	 *            用户ID
	 * @param nStatus
	 *            用户登录状态
	 * @param nServerTimeUTC
	 *            登录时间
	 * @param sDBID
	 * @param nResult
	 *            登录结果(0 登录成功)
	 *
	 * @return None
	 */
	private void OnImLogin(long nUserID, int nStatus, long serverTime, String sDBID, int nResult) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnLoginCallback(nUserID, nStatus, nResult, serverTime, sDBID.trim());
			}
		}
	}

	/**
	 * @brief 离开IM回调函数
	 *
	 * @param nReason
	 *            离开原因
	 *
	 * @return None
	 */
	private void OnImLogout(int nReason) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnLogoutCallback(nReason);
			}
		}
	}

	private void OnGroupReportAllBegin() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGetGroupsInfoBegin();
			}
		}
	}

	private void OnGroupReportAllEnd() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnGroupsLoaded();
			}
		}
	}

	/**
	 * @brief 用户在线状态更新回调函数
	 *
	 * @param nUserID
	 *            用户ID
	 * @param nUEType
	 *            终端类型
	 * @param nUserStatus
	 *            用户在线状态 (nStatus = 1 在线, 2 离开, 3 忙碌, 4 请勿打扰, 5 隐身)
	 * @param szStatusDesc
	 *            状态描述
	 * @return None
	 */
	private void OnImUserStatusReport(long nUserID, int nUEType, int nUserStatus, String szStatusDesc) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnUserStatusUpdatedCallback(nUserID, nUEType, nUserStatus, szStatusDesc);
			}
		}
	}

	/**
	 * @brief 用户基本信息回调函数
	 *
	 * @param nUserID
	 *            用户ID
	 * @param szUserXml
	 *            用户基本信息xml szUserXml格式：
	 *            <user avatarlocation='' avatarname='' bsystemavatar=''
	 *            email='' id='' mobile='' needauth='' nickname='' privacy=''
	 *            sign='' telephone='' smsid = '' smsno = '' uetype = '' sex =
	 *            '' address = '' birthday = '' homepage = '' job = '' fax = ''
	 *            accounttype = '1'/> 注：nSMSID == 0(不可以收发短信) nSMSNO ==
	 *            0(不可以接收短信) uetype 用户类型(1-->pc, 2-->手机, 3-->sip)
	 *
	 * @return None
	 */
	private void OnImUserBaseInfoReport(long nUserID, String szUserXml) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnUpdateBaseInfoCallback(nUserID, szUserXml);
			}
		}
	}

	/**
	 * @brief 在服务器上查找用户的回调函数
	 *
	 * @param xmlinfo
	 *            返回的用户列表的XML
	 * @return None
	 */
	private void OnImSearchUsersResult(String xmlinfo) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSearchUserCallback(xmlinfo);
			}
		}
	}

	private void OnImUserAvatarReport(int nAvatarType, long nUserID, String AvatarName) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnChangeAvatarCallback(nAvatarType, nUserID, AvatarName);
			}
		}
	}

	/**
	 * @brief 连接服务器结果
	 * 
	 * @param nResult
	 *            结果
	 *
	 * @return None
	 */
	private void OnImConnectResult(int nResult) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnConnectResponseCallback(nResult);
			}
		}
	}

	/**
	 * @brief 更改好友的备注名称的回调函数
	 *
	 * @param nUserId
	 *            好友的用户ID
	 * @param sCommmentName
	 *            新的备注名称
	 * @return None
	 */
	private void OnImChangeFriendMemoNameResult(long nUserId, String sCommmentName) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnModifyCommentNameCallback(nUserId, sCommmentName);
			}
		}
	}

	/**
	 * @brief 连接成功的情况下和服务器连接断开
	 * 
	 * @return None
	 */
	private void OnImDisconnected() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnSignalDisconnected();
			}
		}
	}

	private void OnImOfflineStart() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnOfflineStart();
			}
		}
	}

	private void OnImOfflineEnd() {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnOfflineEnd();
			}
		}
	}

	/**
	 * @brief 创建验证码回调
	 * @param ret
	 *            返回值： 0=成功 ERR_IM_CREATE_VALIDATECODE=生成验证码失败
	 *            ERR_IM_SEND_VALIDATECODE=发送短信失败 ERR_IM_ALREADY_REGISTED=账号已被注册
	 *            ERR_IM_WRONG_PHONENUMBER=手机号格式错误
	 *            ERR_IM_CON_REGSERVER_FAIL=连接服务器失败
	 */
	private void OnImUserCreateValidateCode(int ret) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnImUserCreateValidateCode(ret);
			}
		}
	};

	/**
	 * @brief 手机用户注册回调
	 * @param ret
	 *            返回值： 0=成功 ERR_IM_REG_FAIL=注册失败 ERR_IM_INVALIDATECODE=无效验证码
	 *            ERR_IM_WRONG_PHONENUMBER=手机号格式错误
	 *            ERR_IM_CON_REGSERVER_FAIL=连接服务器失败
	 *            ERR_IM_ALREADY_REGISTED=账号已被注册
	 */
	private void OnImRegisterPhoneUser(int ret) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnImRegisterPhoneUser(ret);
			}
		}
	};

	/**
	 * 更新密码回调
	 * 
	 * @param oldPsw
	 * @param newPsw
	 */
	private void OnImUpdateUserPwd(int ret) {
		for (int i = 0; i < this.mCallBacks.size(); i++) {
			WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnImUpdateUserPwd(ret);
			}
		}
	};

	// private void OnHaveUpdateNotify(String updatefilepath, String updatetext)
	// {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnHaveUpdateNotify(updatefilepath, updatetext);
	// }
	// }
	// }

	// private void OnUpdateDownloadBegin(long filesize) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnUpdateDownloadBegin(filesize);
	// }
	// }
	// }

	// private void OnUpdateDownloading(long size) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnUpdateDownloading(size);
	// }
	// }
	// }

	// private void OnUpdateDownloadEnd(boolean error) {
	// for (int i = 0; i < this.mCallBacks.size(); i++) {
	// WeakReference<ImRequestCallback> wf = this.mCallBacks.get(i);
	// if (wf != null && wf.get() != null) {
	// wf.get().OnUpdateDownloadEnd(error);
	// }
	// }
	// }
}
