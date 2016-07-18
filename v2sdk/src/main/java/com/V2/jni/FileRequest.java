package com.V2.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.V2.jni.callback.FileRequestCallback;

public class FileRequest {
	private static FileRequest mFileRequest;

	private List<WeakReference<FileRequestCallback>> mCallBacks;

	private FileRequest() {
		mCallBacks = new ArrayList<WeakReference<FileRequestCallback>>();
	}

	public static synchronized FileRequest getInstance() {
		if (mFileRequest == null) {
			synchronized (FileRequest.class) {
				if (mFileRequest == null) {
					mFileRequest = new FileRequest();
					if (!mFileRequest.initialize(mFileRequest)) {
						throw new RuntimeException("can't initilaize FileRequest");
					}
				}
			}
		}
		return mFileRequest;
	}

	/**
	 * 添加自定义的回调，监听接收到的服务信令
	 * 
	 * @param callback
	 */
	public void addCallback(FileRequestCallback callback) {
		this.mCallBacks.add(new WeakReference<FileRequestCallback>(callback));
	}

	/**
	 * 移除自定义添加的回调
	 * 
	 * @param callback
	 */
	public void removeCallback(FileRequestCallback callback) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				if (wf.get() == callback) {
					mCallBacks.remove(wf);
					return;
				}
			}
		}
	}

	public native boolean initialize(FileRequest request);

	public native void unInitialize();

	/**
	 * @brief 发送文件
	 * 
	 * @param nUserId
	 *            user Id
	 * @param szFileXml
	 *            <file id="" name="{FILE PATH}" encrypttype="0"/>
	 * @param nFileType
	 *            2: OFFLINE 1:ONLINE
	 */
	public native void FileTransInviteImFile(long nUserId, String szFileXml, int nFileType);

	/**
	 * @brief 接受文件
	 * 
	 * @param szFileID
	 * @param szSavePath
	 */
	public native void FileTransAcceptImFile(String szFileID, String szSavePath);

	/**
	 * @brief 拒绝文件
	 * 
	 * @param szFileID
	 */
	public native void FileTransRefuseImFile(String szFileID);

	/**
	 * @brief 取消文件发送
	 * 
	 * @param szFileID
	 */
	public native void FileTransCancelImFile(String szFileID);

	/**
	 * @brief 发送方正在发送的文件取消上传
	 * 
	 * @param szFileID
	 */
	public native void FileTransCloseSendFile(String szFileID);

	/**
	 * @brief 接受方正在接受的文件取消接受
	 * 
	 * @param szFileID
	 */
	public native void FileTransCloseRecvFile(String szFileID);

	/**
	 * @brief 继续上传或发送文件
	 * 
	 * @param szFileID
	 */
	public native void FileTransResumeUploadFile(String szFileID);

	/**
	 * @brief 暂停上传或发送文件
	 * 
	 * @param szFileID
	 */
	public native void FileTransPauseUploadFile(String szFileID);

	/**
	 * @brief 继续下载文件
	 * 
	 * @param szFileID
	 */
	public native void FileTransResumeDownloadFile(String szFileID);

	/**
	 * @brief 暂停下载文件
	 * 
	 * @param szFileID
	 */
	public native void FileTransPauseDownloadFile(String szFileID);

	/**
	 * 下载失败，重新下载时调用
	 * 
	 * @param url
	 * @param sfileid
	 * @param filePath
	 * @param encrypttype
	 * @param businessType
	 *            填1即可
	 */
	public native void FileTransDownloadFile(String url, String sfileid, String filePath, int encrypttype);

	/**
	 * @brief 邀请给你发送文件
	 * 
	 * @param nFromUserID
	 *            源用户ID
	 * @param szFileID
	 *            文件ID
	 * @param szFileName
	 *            文件名字
	 * @param nFileBytes
	 *            文件大小
	 * @param szDownloadURL
	 *            如果离线, 下载地址
	 * @param nFileType
	 *            文件发送类型
	 * 
	 * @return None
	 */
	private void OnFileTransImFileInvite(long nFromUserID, String szFileID, String szFileName, long nFileBytes,
			String szDownloadURL, int nFileType) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransInvite(nFromUserID, szFileID, szFileName, nFileBytes, szDownloadURL, nFileType);
			}
		}
	}

	/**
	 * @brief 对方接受文件
	 * 
	 * @param szFileID
	 *            文件ID
	 * 
	 * @return None
	 */
	private void OnFileTransImFileAccepted(String szFileID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransAccepted(szFileID);
			}
		}
	}

	/**
	 * @brief 对方拒绝文件
	 * 
	 * @param szFileID
	 *            文件ID
	 * 
	 * @return None
	 */
	private void OnFileTransImFileRefused(String szFileID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransRefuse(szFileID);
			}
		}
	}

	/**
	 * @brief 开始传输
	 * 
	 * @param szFileID
	 *            文件ID
	 * @param nTransType
	 *            文件传输类型(上传/下载)
	 * @param nFileSize
	 *            文件大小
	 * 
	 * @return None
	 */
	private void OnFileTransBegin(String szFileID, int nTransType, long nFileSize) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransBegin(szFileID, nTransType, nFileSize);
			}
		}
	}

	/**
	 * @brief 传输过程中
	 * 
	 * @param szFileID
	 *            文件ID
	 * @param nBytesTransed
	 *            传输大小
	 * @param nTransType
	 *            文件传输类型(上传/下载)
	 * 
	 * @return None
	 */
	private void OnFileTransProgress(String szFileID, long nBytesTransed, int nTransType) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransProgress(szFileID, nBytesTransed, nTransType);
			}
		}
	}

	/**
	 * @brief 传输完成
	 * 
	 * @param szFileID
	 *            文件ID
	 * @param szFileName
	 *            文件名字
	 * @param nFileSize
	 *            文件大小
	 * @param nTransType
	 *            文件传输类型(上传/下载)
	 * @param szUrl
	 *            下载地址
	 * 
	 * @return None
	 */
	private void OnFileTransEnd(String szFileID, String szFileName, long nFileSize, int nTransType, String szUrl) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransEnd(szFileID, szFileName, nFileSize, nTransType);
			}
		}
	}
	
	/**
	 * @brief 对方取消文件
	 * 
	 * @param szFileID
	 *            文件ID
	 * 
	 * @return None
	 */
	private void OnFileTransCanceled(String szFileID) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransCancel(szFileID);
			}
		}
	}

	/**
	 * @brief 传输失败
	 * 
	 * @param szFileID
	 *            文件ID
	 * @param nErrorCode
	 *            失败原因
	 * @param nTransType
	 *            文件传输类型(上传/下载)
	 * 
	 * @return None
	 */
	private void OnFileTransFailure(String szFileID, int nErrorCode, int nTransType) {
		for (int i = 0; i < mCallBacks.size(); i++) {
			WeakReference<FileRequestCallback> wf = mCallBacks.get(i);
			if (wf != null && wf.get() != null) {
				wf.get().OnFileTransError(szFileID, nErrorCode, nTransType);
			}
		}
	}
}
