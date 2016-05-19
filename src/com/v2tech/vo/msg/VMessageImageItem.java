package com.v2tech.vo.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import com.V2.jni.util.V2Log;
import com.v2tech.util.GlobalConfig;

public class VMessageImageItem extends VMessageAbstractItem{

	private String filePath;
	private String extension;
	private Bitmap mFullQualityBitmap = null;
	private Bitmap mCompressedBitmap = null;
	private boolean isReceived;

	public VMessageImageItem(VMessage vm, String filePath) {
		super(vm);
		this.filePath = filePath;
		this.type = ITEM_TYPE_IMAGE;
		this.uuid = UUID.randomUUID().toString();
	}

	public VMessageImageItem(VMessage vm, String uuid, String extension) {
		super(vm);
		this.uuid = uuid;
		this.extension = extension;
		this.type = ITEM_TYPE_IMAGE;
		this.filePath = getFilePath();
	}
	
	public VMessageImageItem(VMessage vm) {
		super(vm);
		this.type = ITEM_TYPE_IMAGE;
		this.filePath = getFilePath();
	}

	public String getFilePath() {
		if (filePath == null && extension != null)
			return GlobalConfig.getGlobalPicsPath() + "/" + uuid + extension;
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getExtension() {
		if (extension == null) {
			int pos = filePath.lastIndexOf(".");
			if (pos != -1) {
				extension = filePath.substring(pos);
			}
		}
		return extension;
	}

	public boolean isReceived() {
		return isReceived;
	}

	public void setReceived(boolean isReceived) {
		this.isReceived = isReceived;
	}

	public String toXmlItem() {
		int[] w = new int[2];
		String str = " <TPictureChatItem NewLine=\"True\" AutoResize=\"True\" FileExt=\""
				+ getExtension()
				+ "\" GUID=\""+uuid+"\" Height=\""
				+ w[1]
				+ "\" Width=\"" + w[0] + "\" />";
		return str;
	}

	public byte[] loadImageData() {
		File f = new File(filePath);
		if (!f.exists()) {
			V2Log.e(" file doesn't exist " + filePath);
			return null;
		}
		InputStream is = null;
		try {
			byte[] data = new byte[(int) f.length()];
			is = new FileInputStream(f);
			is.read(data);
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
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

	public Size getCompressedBitmapSize() {
		int[] w = new int[2];
		Size s = new Size();
		s.width = w[0];
		s.height = w[1];
		return s;
	}

	public synchronized Bitmap getCompressedBitmap() {
		if (mCompressedBitmap == null || mCompressedBitmap.isRecycled()) {
		}
		return mCompressedBitmap;
	}
	
	public Size getFullBitmapSize() {
		int[] w = new int[2];
		Size s = new Size();
		s.width = w[0];
		s.height = w[1];
		return s;
	}

	public synchronized Bitmap getFullQuantityBitmap() {
		if (mFullQualityBitmap == null || mFullQualityBitmap.isRecycled()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Config.RGB_565;
			options.inDither = true;
			BitmapFactory.decodeFile(this.filePath, options);
			options.inJustDecodeBounds = false;
			if (options.outWidth > 1920 || options.outHeight > 1080) {
				options.inSampleSize = 2;
				mFullQualityBitmap = BitmapFactory.decodeFile(this.filePath,
						options);
			}  else {
				options.inSampleSize = 1;
				mFullQualityBitmap = BitmapFactory.decodeFile(this.filePath,
						options);
			}

		}

		return mFullQualityBitmap;
	}

	public void recycle() {
		if (mCompressedBitmap != null) {
			mCompressedBitmap.recycle();
			mCompressedBitmap = null;
		}
	}

	public void recycleFull() {
		if (mFullQualityBitmap != null) {
			mFullQualityBitmap.recycle();
			mFullQualityBitmap = null;
		}
	}

	public void recycleAll() {
		recycle();
		recycleFull();
	}

	public class Size {
		public int width;
		public int height;
	}
}