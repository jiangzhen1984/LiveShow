package v2av;

import java.util.ArrayList;

import v2av.VideoCaptureDevInfo.VideoCaptureDevice;

public class VideoDeviceAndroid
{
	private VideoCaptureDevInfo mCapDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
	
	private String GetVideoDevInfo()
	{
		if (mCapDevInfo == null) {
			mCapDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
		}
		if (mCapDevInfo.deviceList == null) {
			mCapDevInfo.deviceList =  new ArrayList<VideoCaptureDevice>();
		}
		int devNum = mCapDevInfo.deviceList.size();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<devicelist>");
		for (int i = 0; i < devNum; i++)
		{
			VideoCaptureDevice dev = mCapDevInfo.deviceList.get(i);
			int frameSize = dev.framerates.size();
			sb.append("<device devName='");
			sb.append(dev.deviceUniqueName);
			sb.append("' fps='");
			if(frameSize > 0 && dev.framerates.get(frameSize - i - 1) != null){
				sb.append(dev.framerates.get(frameSize - i - 1));
			}
			else{
				sb.append(15);
			}
			sb.append("'>");
			
			for (CaptureCapability cap : dev.capabilites)
			{
				sb.append("<size width='");
				sb.append(cap.width);
				sb.append("' height='");
				sb.append(cap.height);
				sb.append("'>");
				sb.append("</size>");
			}
			
			sb.append("</device>");
		}
		
		sb.append("</devicelist>");
		
		return sb.toString();
	}
}
