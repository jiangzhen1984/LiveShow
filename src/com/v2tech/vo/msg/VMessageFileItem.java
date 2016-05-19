package com.v2tech.vo.msg;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.UUID;

import android.text.TextUtils;

import com.v2tech.util.GlobalConfig;

public class VMessageFileItem extends VMessageAbstractItem {

	private String filePath;

	private String fileName;

	private long fileSize;

	private float progress;

	private long downloadedSize;

	private float speed;

	private int fileType;

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	// Always send offline file
	private int transType = 2;

	public VMessageFileItem(VMessage vm, String filePath , int fileState) {
		this(vm, null, filePath, null, 0 , fileState, 0, 0, 0, -1 , 2);
	}

	public VMessageFileItem(VMessage vm, String fileName , int fileState ,String uuid) {
		this(vm, uuid, null, fileName, fileState ,0, 0, 0, 0, -1, 2);
	}

	public VMessageFileItem(VMessage vm, String fileID, long fileSize, int fileState ,
			String fileName, int fileType) {
		this(vm, fileID, null, fileName , fileSize, fileState , 0, 0, 0, fileType, 2);
	}

	public VMessageFileItem(VMessage vm, String uuid, String filePath,
			String fileName, long fileSize, int fileState , float progress,
			long downloadedSize, float speed, int fileType, int transType) {
		super(vm);
		this.uuid = uuid;
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.state = fileState;
		this.progress = progress;
		this.downloadedSize = downloadedSize;
		this.speed = speed;
		this.fileType = fileType;
		this.transType = transType;
		this.type = VMessageAbstractItem.ITEM_TYPE_FILE;

		if (TextUtils.isEmpty(uuid))
			this.uuid = UUID.randomUUID().toString();

		if (!TextUtils.isEmpty(fileName) && TextUtils.isEmpty(filePath)) {
			this.filePath = GlobalConfig.getGlobalFilePath() + "/" + fileName;
		}

		if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(filePath)) {
			int start = filePath.lastIndexOf("/");
			if (start != -1)
				this.fileName = filePath.substring(start + 1);
		}


		if (fileSize == 0 && !TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);
			if (file != null && file.isFile())
				this.fileSize = file.length();
		}
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long size) {
		this.fileSize = size;
	}

	public String getFileSizeStr() {
		Format df = new DecimalFormat("#.0");

		if (fileSize >= 1073741824) {
			return (df.format((double) fileSize / (double) 1073741824)) + "G";
		} else if (fileSize >= 1048576) {
			return (df.format((double) fileSize / (double) 1048576)) + "M";
		} else if (fileSize >= 1024) {
			return (df.format((double) fileSize / (double) 1024)) + "K";
		} else {
			return fileSize + "B";
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toXmlItem() {

		if (filePath == null)
			fileName = getFilePath();

		StringBuilder sb = new StringBuilder();
		// String url = "http://" + GlobalConfig.GLOBAL_IP_ADDRESS + ":" +
		// GlobalConfig.GLOBAL_PORT + "/crowd/" + uuid + "/" + fileName;
		// sb.append(
		// "<filelist><file encrypttype=\"0\" id=\"" + uuid + "\" name=\"" +
		// fileName
		// + "\" size=\"" + fileSize + "\" time=\""
		// + GlobalConfig.getGlobalServerTime() + "\" uploader=\"" +
		// GlobalHolder.getInstance().getCurrentUserId()
		// + "\" url=\"" + url + "\" /></filelist>").append("\n");
		sb.append("<file id=\"" + uuid + "\" name=\"" + filePath + "\" />")
				.append("\n");
		return sb.toString();
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public long getDownloadedSize() {
		return downloadedSize;
	}

	public String getDownloadSizeStr() {
		Format df = new DecimalFormat("#.0");

		if (downloadedSize >= 1073741824) {
			return (df.format((double) downloadedSize / (double) 1073741824))
					+ "G";
		} else if (downloadedSize >= 1048576) {
			return (df.format((double) downloadedSize / (double) 1048576))
					+ "M";
		} else if (downloadedSize >= 1024) {
			return (df.format((double) downloadedSize / (double) 1024)) + "K";
		} else {
			return downloadedSize + "B";
		}
	}

	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public String getSpeedStr() {

		return getFileSize(speed);
	}

	public float getSpeed() {

		return speed;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param totalSpace
	 * @return
	 */
	private String getFileSize(float totalSpace) {

		BigDecimal filesize = new BigDecimal(totalSpace);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		if (returnValue > 1)
			return (returnValue + "MB");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
				.floatValue();
		if (returnValue > 1)
			return (returnValue + "KB");
		else
			return (totalSpace + "B");
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getTransType() {
		return transType;
	}

	public void setTransType(int transType) {
		this.transType = transType;
	}

}
