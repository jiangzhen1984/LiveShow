package com.v2tech.service.jni;

public class FileTransStatusIndication extends JNIIndication {
	
	public static final int IND_TYPE_PROGRESS = 1;
	public static final int IND_TYPE_TRANS_ERR = 2;
	public static final int IND_TYPE_DOWNLOAD_ERR = 3;
	public static final int IND_TYPE_TRANS_CANNEL = 4;
	public static final int IND_TYPE_FILE_DELETE = 5;
	
	public static final int IND_TYPE_PROGRESS_TRANSING = 0;
	public static final int IND_TYPE_PROGRESS_END = 1;
	
	public int indType;
	public int nTransType;
	public String uuid;
	public int errorCode;
	
	public FileTransStatusIndication(int indType, int nTransType,
			String uuid , int errorCode) {
		super(Result.SUCCESS);
		this.indType = indType;
		this.nTransType = nTransType;
		this.uuid = uuid;
		this.errorCode = errorCode;
	}
	
	public FileTransStatusIndication(int indType, int nTransType,
			String uuid) {
		super(Result.SUCCESS);
		this.indType = indType;
		this.nTransType = nTransType;
		this.uuid = uuid;
	}
	
	
	

	public static class FileTransProgressStatusIndication extends FileTransStatusIndication {
		public long nTranedSize;
		public int progressType;

		public FileTransProgressStatusIndication(int nTransType,
				String uuid, long nTranedSize , int progressType) {
			super(IND_TYPE_PROGRESS, nTransType, uuid);
			this.nTranedSize = nTranedSize;
			this.progressType = progressType;
		}
	}
	
	public static class FileTransErrorIndication extends FileTransStatusIndication {

		public FileTransErrorIndication(String uuid , int errorCode ,  int nTransType) {
			super(IND_TYPE_TRANS_ERR, nTransType, uuid , errorCode);
		}
		
	}
	
	
	public static class FileDeletedIndication extends FileTransStatusIndication {

		public FileDeletedIndication(String uuid , int errorCode ,  int nTransType) {
			super(IND_TYPE_FILE_DELETE, nTransType, uuid , errorCode);
		}
		
	}
}
