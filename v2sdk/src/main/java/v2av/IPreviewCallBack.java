package v2av;

import android.hardware.Camera.PreviewCallback;

public interface IPreviewCallBack extends PreviewCallback
{
	public void SetFrameSize(int width, int height);
}
