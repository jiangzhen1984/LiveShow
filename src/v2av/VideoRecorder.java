package v2av;

import com.V2.jni.util.V2Log;

import v2av.VideoCaptureDevInfo.CapParams;
import v2av.VideoCaptureDevInfo.FrontFacingCameraType;
import v2av.VideoCaptureDevInfo.VideoCaptureDevice;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

//import v2av.VideoRecordInfo.CameraID;

public class VideoRecorder  implements SurfaceHolder.Callback {
	public static SurfaceHolder VideoPreviewSurfaceHolder = null;
	public static int DisplayRotation = 0;
	public static int CodecType = 0;

	private int mSrcWidth;
	private int mSrcHeight;

	private int mVideoWidth;
	private int mVideoHeight;
	private int mBitrate;
	private int mPreviewFormat;
	private int mFrameRate;
	private int mSelectedFrameRate;
	// private int mCameraRotation;
	// private boolean mbMirror;
	private int framecount;

	private int cameraRotation;

	private VideoEncoder mEncoder = null;

	private Camera mCamera = null;
	private VideoCaptureDevInfo mCapDevInfo = null;

	private EncoderPreviewCallBack mVRCallback = null;

	public static boolean isOpenCamera;

	VideoRecorder() {
		mCapDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
	}

	@SuppressWarnings("unused")
	private int StartRecordVideo() {

		if (mCapDevInfo == null) {
			V2Log.e("===== mCapDevInfo:" + mCapDevInfo);
			return -1;
		}
		Log.e("DEBUG", "要开启的默认视频是：" + mCapDevInfo.GetDefaultDevName());
		VideoCaptureDevice device = mCapDevInfo.GetDevice(mCapDevInfo.GetDefaultDevName());
		if (device == null) {
			Log.e("DEBUG", "开启默认视频失败！ -1");
			return -1;
		}

		CapParams capParams = mCapDevInfo.GetCapParams();
		mVideoWidth = capParams.width;
		mVideoHeight = capParams.height;
		mBitrate = capParams.bitrate;
		mFrameRate = capParams.fps;
		mPreviewFormat = capParams.format;

		VeritifyRecordInfo(device);

		switch (InitCamera(device)) {
		case Err_CameraOpenError:
			Log.e("DEBUG", "init camera failed ");
			return -1;
		default:
			break;
		}

		StartPreview();
		isOpenCamera = true;
		return 0;
	}

	@SuppressWarnings("unused")
	private int StopRecordVideo() {
		isOpenCamera = false;
		Log.i("DEBUG", "开始关闭本地视频！isOpenCamera ： " + isOpenCamera);
		UninitCamera();
		return 0;
	}

	@SuppressWarnings("unused")
	private int GetCodecType() {
		return CodecType;
	}

	@SuppressWarnings("unused")
	private int GetRecordWidth() {
		if (cameraRotation == 90 || cameraRotation == 270) {
			return mVideoHeight;
		} else {
			return mVideoWidth;
		}
	}

	@SuppressWarnings("unused")
	private int GetRecordHeight() {
		if (cameraRotation == 90 || cameraRotation == 270) {
			return mVideoWidth;
		} else {
			return mVideoHeight;
		}
	}

	@SuppressWarnings("unused")
	private int GetRecordBitrate() {
		return mBitrate;
	}

	@SuppressWarnings("unused")
	private int GetRecordFPS() {
		return mFrameRate;
	}

	@SuppressWarnings("unused")
	private int GetRecordFormat() {
		return mPreviewFormat;
	}

	@SuppressWarnings("unused")
	private int GetPreviewSize() {
		if (mCamera == null) {
			return -1;
		}
		Size s = null;
		// for MANUFACTURER=Teclast BRAND=MID HOST=droid07-szto cache
		// runtimeexception
		try {
			Camera.Parameters para = mCamera.getParameters();
			s = para.getPreviewSize();
		} catch (RuntimeException e) {

		}

		if (s == null) {
			mSrcWidth = 1920;
			mSrcHeight = 1080;
		} else {
			mSrcWidth = s.width;
			mSrcHeight = s.height;
		}

		return 0;
	}

	@SuppressWarnings("unused")
	private int GetPreviewWidth() {
		return mSrcWidth;
	}

	@SuppressWarnings("unused")
	private int GetPreviewHeight() {
		return mSrcHeight;
	}

	@SuppressWarnings("unused")
	private int GetRotation() {
		return cameraRotation;
	}

	@SuppressWarnings("unused")
	private int StartSend() {
		mVRCallback = new EncoderPreviewCallBack(this);

		if (StartRecord(mVRCallback) != true) {
			return -1;
		}

		return 0;
	}

	@SuppressWarnings("unused")
	private int StopSend() {
		StopRecord();
		if (mVRCallback != null) {
			mVRCallback = null;
		}

		return 0;
	}

	private VideoSize GetSrcSizeByEncSize(VideoCaptureDevice device, int width, int height) {
		VideoSize size = new VideoSize();

		int length = device.capabilites.size();
		if (length <= 0) {
			return null;
		}

		int tempWidth = device.capabilites.get(0).width;
		int tempHeight = device.capabilites.get(0).height;
		size.width = tempWidth;
		size.height = tempHeight;
		int lastValue = tempWidth * tempHeight;
		int setArea = width * height;
		for (int i = 1; i < length; i++) {
			tempWidth = device.capabilites.get(i).width;
			tempHeight = device.capabilites.get(i).height;
			int value = tempWidth * tempHeight;
			if (Math.pow(value - setArea, 2) < Math.pow(lastValue - setArea, 2)) {
				size.width = tempWidth;
				size.height = tempHeight;
				lastValue = value;
			}
		}
		return size;
	}

	private int SelectFramerate(VideoCaptureDevice device, int fps) {
		int selectedFps = 0;

		for (Integer framerate : device.framerates) {
			if (framerate >= fps) {
				selectedFps = framerate;
				break;
			}
		}

		return selectedFps;
	}

	private void VeritifyRecordInfo(VideoCaptureDevice device) {
		VideoSize size = GetSrcSizeByEncSize(device, mVideoWidth, mVideoHeight);
		mSrcWidth = size.width;
		mSrcHeight = size.height;

		mSelectedFrameRate = SelectFramerate(device, mFrameRate);
	}

	public void onGetVideoFrame(byte[] databuf, int len) {
		if (DropFrame()) {
			return;
		}

		if (mEncoder != null) {
			mEncoder.encodeframe(databuf, len);
		}
	}

	private boolean DropFrame() {
		if (mSelectedFrameRate <= mFrameRate) {
			return false;
		}

		framecount++;

		switch (mSelectedFrameRate) {
		case 10:
			if (framecount % 2 != 0) {
				return true;
			}
			break;
		case 15:
			if (mFrameRate == 5) {
				if (framecount % 3 != 0) {
					return true;
				}
			} else // mVideoRecordInfo.mFrameRate == 10
			{
				if (framecount % 3 == 0) {
					return true;
				}
			}
			break;
		case 30:
			if (mFrameRate == 5) {
				if (framecount % 6 != 0) {
					return true;
				}
			} else if (mFrameRate == 10) {
				if (framecount % 3 != 0) {
					return true;
				}
			} else // mVideoRecordInfo.mFrameRate == 15
			{
				if (framecount % 2 != 0) {
					return true;
				}
			}
			break;
		default:
			break;
		}

		return false;
	}

	private AVCode InitCamera(VideoCaptureDevice device) {
		Log.i("DEBUG", "start init Camera !");
		if (mCamera != null) {
			return AVCode.Err_CameraAlreadyOpen;
		}

		if (Build.VERSION.SDK_INT <= VERSION_CODES.GINGERBREAD) {
			Log.e("DEBUG", "Mobile System Version Error , Less than 2.3");
			return AVCode.Err_ErrorState;
		} else {
			if (!OpenCamera(device)) {
				Log.e("DEBUG", "OpenCamera failed!!!!!!!!!!!!!!!!!!!!!");
			}
		}

		if (mCamera == null) {
			return AVCode.Err_CameraOpenError;
		}

		return AVCode.Err_None;
	}

	private boolean OpenCamera(VideoCaptureDevice device) {
		try {
			mCamera = Camera.open(device.index);
		} catch (RuntimeException e) {
			throw new RuntimeException("OpenCamera 打开视频出错！" + e.getLocalizedMessage());
		}

		if (mCamera == null) {
			return false;
		}

		Camera.Parameters para = mCamera.getParameters();
		CapParams capParams = mCapDevInfo.GetCapParams();
		try {

			Parameters beforeParam = mCamera.getParameters();
			Size before = beforeParam.getPreviewSize();
			Log.d("DEBUG", "设置前 preview size : " + before.width + " : " + before.height + " | capParams.width : "
					+ capParams.width + " | capParams.height ： " + capParams.height);

			para.setPreviewSize(capParams.width, capParams.height);
			para.setPreviewFormat(mPreviewFormat);
			mCamera.setParameters(para);

			Parameters afterParam = mCamera.getParameters();
			Size after = afterParam.getPreviewSize();
			Log.d("DEBUG", "设置后 preview size : " + after.width + " : " + after.height);
		} catch (Exception e) {
			para.setPreviewSize(mSrcWidth, mSrcHeight);
			para.setPreviewFormat(mPreviewFormat);
			Parameters afterParam = mCamera.getParameters();
			Size after = afterParam.getPreviewSize();
			Log.d("DEBUG", "catch 设置后 preview size : " + after.width + " : " + after.height);
			para.setPreviewFormat(mPreviewFormat);
			mCamera.setParameters(para);
		}

		if (device.frontCameraType == FrontFacingCameraType.Android23) {
			cameraRotation = (device.orientation + DisplayRotation) % 360;
			cameraRotation = (360 - cameraRotation) % 360; // compensate the
															// mirror

			mCamera.setDisplayOrientation(cameraRotation);

			if (cameraRotation == 90) {
				cameraRotation = 270;
			} else if (cameraRotation == 270) {
				cameraRotation = 90;
			}
		} else {
			// back-facing
			cameraRotation = (device.orientation - DisplayRotation + 360) % 360;
			mCamera.setDisplayOrientation(cameraRotation);
		}
		// mCamera.setDisplayOrientation(cameraRotation);
		return true;
	}

	private void UninitCamera() {
		if (mCamera != null) {
			try {
				mCamera.setPreviewCallback(null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SystemClock.sleep(50);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}
	}

	private void StartPreview() {
		V2Log.e( "StartPreview");

		if (mCamera == null) {
			return;
		}

		try {
			if (VideoPreviewSurfaceHolder != null) {
				mCamera.setPreviewDisplay(VideoPreviewSurfaceHolder);
				mCamera.startPreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
			UninitCamera();
			throw new RuntimeException("StartPreview 打开视频出错！" + e.getLocalizedMessage());
		}
	}

	private boolean StartRecord(IPreviewCallBack callback) {
		Log.i("DEBUG", "StartRecord");
		if (mCamera == null) {
			return false;
		}

		mEncoder = new VideoEncoder();
		mCamera.setPreviewCallback(callback);
		return true;
	}

	private void StopRecord() {
		Log.i("DEBUG", "StopRecord");
		if (mCamera == null) {
			return;
		}

		try {
			mCamera.setPreviewCallback(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mEncoder = null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		V2Log.e("====================create");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	
	
	
}
