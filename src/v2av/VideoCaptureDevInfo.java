package v2av;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

public class VideoCaptureDevInfo {
	private final static String TAG = "VideoCaptureDevInfo";

	private final static String CAMERA_FACE_FRONT = "Camera Facing front";
	public final static String CAMERA_FACE_BACK = "Camera Facing back";

	private String mDefaultDevName = "";

	private CapParams mCapParams = new CapParams();

	public class CapParams {
		public int width = 176;
		public int height = 144;
		public int bitrate = 70000;
		public int fps = 15;
		public int format = ImageFormat.NV21;
	}

	public void SetDefaultDevName(String devName) {
		mDefaultDevName = devName;
	}

	public String GetDefaultDevName() {
		return mDefaultDevName;
	}

	public void SetCapParams(int width, int height, int bitrate, int fps,
			int format) {
		mCapParams.width = width;
		mCapParams.height = height;
		mCapParams.bitrate = bitrate;
		mCapParams.fps = fps;
		mCapParams.format = format;
	}
	
	public void SetCapParams(int width, int height) {
		mCapParams.width = width;
		mCapParams.height = height;
	}

	public CapParams GetCapParams() {
		return mCapParams;
	}

	public void updateCameraOrientation(int orientation) {
		for (VideoCaptureDevice dev : deviceList) {
			dev.orientation = orientation;

		}
	}

	// Private class with info about all available cameras and the capabilities
	public class VideoCaptureDevice {
		VideoCaptureDevice() {
			frontCameraType = FrontFacingCameraType.None;
			index = 0;
		}

		public String deviceUniqueName;
		public FrontFacingCameraType frontCameraType;

		public LinkedList<CaptureCapability> capabilites = new LinkedList<CaptureCapability>();
		public LinkedList<Integer> framerates = new LinkedList<Integer>();

		// Orientation of camera as described in
		// android.hardware.Camera.CameraInfo.Orientation
		public int orientation;

		// Camera index used in Camera.Open on Android 2.3 and onwards
		public int index;

		public List<Integer> previewformats;

		public VideoSize GetSrcSizeByEncSize(int width, int height) {
			VideoSize size = new VideoSize();

			int length = capabilites.size();
			if (length <= 0) {
				return null;
			}

			int tempWidth = capabilites.get(0).width;
			int tempHeight = capabilites.get(0).height;
			size.width = tempWidth;
			size.height = tempHeight;
			int lastValue = tempWidth * tempHeight;
			int setArea = width * height;
			for (int i = 1; i < length; i++) {
				tempWidth = capabilites.get(i).width;
				tempHeight = capabilites.get(i).height;
				int value = tempWidth * tempHeight;
				if (Math.pow(value - setArea, 2) < Math.pow(
						lastValue - setArea, 2)) {
					size.width = tempWidth;
					size.height = tempHeight;
					lastValue = value;
				}
			}
			return size;
		}
	}

	public enum FrontFacingCameraType {
		None, // This is not a front facing camera
		GalaxyS, // Galaxy S front facing camera.
		HTCEvo, // HTC Evo front facing camera
		Android23, // Android 2.3 front facing camera.
	}

	public List<VideoCaptureDevice> deviceList;

	private static VideoCaptureDevInfo s_self = null;

	public static VideoCaptureDevInfo CreateVideoCaptureDevInfo() {
		if (s_self == null) {
			s_self = new VideoCaptureDevInfo();
			if (s_self.Init() != 0) {
				s_self = null;
				Log.d(TAG, "Failed to create VideoCaptureDevInfo.");
			}
		}

		return s_self;
	}

	public VideoCaptureDevice GetDevice(String devName) {
		VideoCaptureDevice device = null;
		for (VideoCaptureDevice dev : deviceList) {
			if (dev.deviceUniqueName.equals(devName)) {
				device = dev;
				break;
			}
		}

		return device;
	}

	public VideoCaptureDevice GetCurrDevice() {
		return GetDevice(mDefaultDevName);
	}

	private VideoCaptureDevInfo() {
		deviceList = new ArrayList<VideoCaptureDevice>();
	}

	/**
	 * Before do this option, please do close local device first. After call
	 * this function, then open local device again.
	 */
	public boolean reverseCamera() {
		// Only has one camera, can not reverse
		if (Camera.getNumberOfCameras() <= 1) {
			return false;
		}
		if (CAMERA_FACE_FRONT.equals(mDefaultDevName)) {
			mDefaultDevName = CAMERA_FACE_BACK;
			return true;
		} else if (CAMERA_FACE_BACK.equals(mDefaultDevName)) {
			mDefaultDevName = CAMERA_FACE_FRONT;
			return true;
		} else {
			Log.e("VideoCaptureDevInfo", " Unknow camera default name:"
					+ mDefaultDevName);
			return false;
		}
	}

	private int Init() {
		// Populate the deviceList with available cameras and their
		// capabilities.
		Camera camera = null;
		try {
			// From Android 2.3 and onwards
			for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
				VideoCaptureDevice newDevice = new VideoCaptureDevice();
				// Set first camera
				if (this.mDefaultDevName == null
						|| this.mDefaultDevName.isEmpty()) {
					this.mDefaultDevName = newDevice.deviceUniqueName;
				}

				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(i, info);
				newDevice.index = i;
				newDevice.orientation = info.orientation;
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					newDevice.deviceUniqueName = CAMERA_FACE_BACK;
					newDevice.frontCameraType = FrontFacingCameraType.None;
					Log.d(TAG, "Camera " + i
							+ ", Camera Facing back, Orientation "
							+ info.orientation);
				} else {
					newDevice.deviceUniqueName = CAMERA_FACE_FRONT;
					newDevice.frontCameraType = FrontFacingCameraType.Android23;
					Log.d(TAG, "Camera " + i
							+ ", Camera Facing front, Orientation "
							+ info.orientation);
					// If has front-side camera, use front-side camera as
					// default
					this.mDefaultDevName = newDevice.deviceUniqueName;
				}

				camera = Camera.open(i);
				Camera.Parameters parameters = camera.getParameters();
				AddDeviceInfo(newDevice, parameters);
				camera.release();
				camera = null;
				deviceList.add(newDevice);
			}

			// VerifyCapabilities();
		} catch (Exception ex) {
			Log.e(TAG,
					"Failed to init VideoCaptureDeviceInfo ex"
							+ ex.getLocalizedMessage());
			return -1;
		}

		return 0;
	}

	// Adds the capture capabilities of the currently opened device
	private void AddDeviceInfo(VideoCaptureDevice newDevice,
			Camera.Parameters parameters) {
		newDevice.previewformats = parameters.getSupportedPreviewFormats();

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		for (Size s : sizes) {
			Log.v(TAG, "VideoCaptureDeviceInfo " + "CaptureCapability:"
					+ s.width + " " + s.height);
			newDevice.capabilites.add(new CaptureCapability(s.width, s.height));
		}

		List<int[]> fps_ranges = parameters.getSupportedPreviewFpsRange();
		for (int[] range : fps_ranges) {
			String strRange = "range is : ";
			for (int val : range) {
				strRange += val + " ";
			}

			Log.e("getSupportedPreviewFpsRange", strRange);
		}

		List<Integer> frameRates = parameters.getSupportedPreviewFrameRates();
		for (Integer fps : frameRates) {
			if (fps == 5 || fps == 10 || fps == 15 || fps == 30) {
				Log.v(TAG, "VideoCaptureDeviceInfo " + "framerates:" + fps);
				newDevice.framerates.add(fps);
			}
		}
	}

	// Function that make sure device specific capabilities are
	// in the capability list.
	// Ie Galaxy S supports CIF but does not list CIF as a supported capability.
	// Motorola Droid Camera does not work with frame rate above 15fps.
	// http://code.google.com/p/android/issues/detail?id=5514#c0
	/*
	 * private void VerifyCapabilities() { // Nexus S or Galaxy S
	 * if(android.os.Build.DEVICE.equals("GT-I9000") ||
	 * android.os.Build.DEVICE.equals("crespo")) { CaptureCapability
	 * specificCapability = new CaptureCapability(); specificCapability.width =
	 * 352; specificCapability.height = 288; specificCapability.maxFPS = 15;
	 * AddDeviceSpecificCapability(specificCapability);
	 * 
	 * specificCapability = new CaptureCapability(); specificCapability.width =
	 * 176; specificCapability.height = 144; specificCapability.maxFPS = 15;
	 * AddDeviceSpecificCapability(specificCapability);
	 * 
	 * specificCapability = new CaptureCapability(); specificCapability.width =
	 * 320; specificCapability.height = 240; specificCapability.maxFPS = 15;
	 * AddDeviceSpecificCapability(specificCapability); }
	 * 
	 * // Motorola Milestone Camera server does not work at 30fps // even though
	 * it reports that it can
	 * if(android.os.Build.MANUFACTURER.equals("motorola") &&
	 * android.os.Build.DEVICE.equals("umts_sholes")) { for(VideoCaptureDevice
	 * device:deviceList) { for(CaptureCapability
	 * capability:device.captureCapabilies) { capability.maxFPS=15; } } } }
	 * 
	 * private void AddDeviceSpecificCapability(CaptureCapability
	 * specificCapability) { for (VideoCaptureDevice device:deviceList) {
	 * boolean foundCapability = false; for(CaptureCapability
	 * capability:device.captureCapabilies) { if(capability.width ==
	 * specificCapability.width && capability.height ==
	 * specificCapability.height) { foundCapability = true; break; } }
	 * 
	 * if (foundCapability==false) { CaptureCapability newCaptureCapabilies[] =
	 * new CaptureCapability[device.captureCapabilies.length+1]; for(int i = 0;
	 * i < device.captureCapabilies.length; ++i) { newCaptureCapabilies[i+1] =
	 * device.captureCapabilies[i]; } newCaptureCapabilies[0] =
	 * specificCapability; device.captureCapabilies = newCaptureCapabilies; } }
	 * }
	 */
}
