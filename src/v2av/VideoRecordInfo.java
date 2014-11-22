package v2av;

/*
 * 视频录制参数
 */
public class VideoRecordInfo 
{
	//视频录制尺寸
	public int mVideoWidth = 176;
	public int mVideoHeight = 144;
	//视频录制帧率,需手机系统支持
	public int mFrameRate = 15;//10的时候，三星4.0系统摄像头崩溃
	
	//使用前置摄像头时,是否为"照镜子"效果
	public boolean mbMirror = false;
	
	//当前使用摄像头的角度
	public int mCameraRotation = 0;
}
