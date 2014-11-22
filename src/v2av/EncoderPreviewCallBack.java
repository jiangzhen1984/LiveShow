package v2av;

import android.hardware.Camera;

public class EncoderPreviewCallBack implements IPreviewCallBack
{
	private VideoRecorder mRecorder = null;
	
	public EncoderPreviewCallBack(VideoRecorder recorder)
	{
		mRecorder = recorder;
	}
	
	static int i = 0;
	
	public void onPreviewFrame(byte[] data, Camera camera) 
	{
		mRecorder.onGetVideoFrame(data, data.length);
	}

	public void SetFrameSize(int width, int height)
	{
	}
}


