package com.example.camera;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.V2.jni.VideoBCRequest;
import com.V2.jni.util.V2Log;
import com.v2tech.view.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.AudioFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PreviewCallback 
{
	private static final String TAG = "CameraView";
	public Camera mCamera = null;
	public boolean isFailed;
	private boolean bIfPreview = false;

	public String publishUrl;
	boolean streamStarted = false;
	Camera.Size realsize;
	int realfmt;
	int realfps;
	int cameraCount = 0;

	int cameraPosition = 1;// 0代表前置摄像头，1代表后置摄像头
	PcmRecorder pcmSource = null;
	private static Context context;
	static MyBroadCastReceiver myBroadCast;
	public CameraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		CameraView.context = context;
		initSurface();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("changecamera");
		myBroadCast = new MyBroadCastReceiver();
		context.registerReceiver(myBroadCast, intentFilter);
	}
	
	public CameraView(Context context)
	{
		super(context);
		CameraView.context = context;
		initSurface();
		myBroadCast = new MyBroadCastReceiver();
	}

	public static void connectionLostNotify()
	{
		Intent intent = new Intent();
		intent.setAction("rectimeoutbroadcast");
		context.sendBroadcast(intent);
	}
	
	public static void unRegisterReceiver(Context context)
	{
		context.unregisterReceiver(myBroadCast);
	}

	
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("changecamera");
		context.registerReceiver(myBroadCast, intentFilter);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		context.unregisterReceiver(myBroadCast);
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
	}



	public class MyBroadCastReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context arg0, Intent intent) 
		{
			String action = intent.getAction();
			if ("changecamera".equals(action)) 
			{
				cameraPosition = intent.getIntExtra("cameraPosition", 1);
				cameraCount = Camera.getNumberOfCameras();
				if (cameraCount > 1) 
				{
					CameraInfo cameraInfo = new CameraInfo();
					for (int i = 0; i < cameraCount; i++) 
					{
						Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息

						if (cameraPosition == 1) 
						{
							// 现在是后置，变更为前置
							if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
							{
								changeCamera(i);
								cameraPosition = 0;
								break;
							}
						}
						else
						{
							// 现在是前置， 变更为后置
							if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
							{
								changeCamera(i);
								cameraPosition = 1;
								break;
							}
						}
					}
				}
			}
		}
	}

	private void changeCamera(int i)
	{
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();// 停掉原来摄像头的预览
		mCamera.release();// 释放资源
		mCamera = null;// 取消原来摄像头
		
		mCamera = Camera.open(i);// 打开当前选中的摄像头
		try 
		{
			deal();
			mCamera.setPreviewDisplay(holder); // 通过surfaceview显示取景画面
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();// 开始预览
	}

	SurfaceHolder holder;

	@SuppressWarnings("deprecation")
	private void initSurface() 
	{
		holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
//		Canvas c = holder.lockCanvas();
//		int width = c.getWidth();
//		int height = c.getHeight();
//		Bitmap bp = Bitmap.createBitmap(width, height,
//				Bitmap.Config.ARGB_4444);
//		Canvas tmp = new Canvas(bp);
//		tmp.drawColor(Color.BLACK);
//
//		c.drawBitmap(bp, 0, 0, new Paint());
//		bp.recycle();
//		holder.unlockCanvasAndPost(c);
		
		
		if (isPreViewing && mCamera == null) {
			tryOpenCamera();
			initCamera();
		}
		
		if (isPreViewing) {
			mCamera.setPreviewCallback(this);
			mCamera.startPreview();
			this.setKeepScreenOn(true);
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
	{
		initCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		deinitCamera();
	}
	
	private void tryOpenCamera() {
		if (cameraPosition == 1)
		{
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
		}
		else
		{
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);// 打开摄像头
		}
		try 
		{
			mCamera.setPreviewDisplay(holder);
		} 
		catch (Exception ex) 
		{
			if (null != mCamera) 
			{
				mCamera.release();
				mCamera = null;
			}
		}
	}

	public boolean startPublish() 
	{
		V2Log.e("start publish===="+publishUrl);
		streamStarted = openPublisher(publishUrl);

		if (streamStarted == false) 
		{
			return false;
		}
		if (cameraPosition == 1)
		{
			openVideoEncoder(realsize.width, realsize.height, true, 270,
					realfps, 1, 100, 17);
		}
		else
		{
			openVideoEncoder(realsize.width, realsize.height, true, 90,
					realfps, 1, 100, 17);
		}
		openAudioEncoder(2, 2, 44100, 64);
		if (pcmSource == null)
		{
			pcmSource = new PcmRecorder();
		}
		Thread audioProvider = new Thread(pcmSource);
		audioProvider.start();

		return true;
	}

	public boolean stopPublish() 
	{
		if (streamStarted = true) 
		{
			streamStarted = false;
			if (pcmSource != null && pcmSource.isRecording) 
			{
				pcmSource.stopRecording();
				closePublisher();
			}
			
			return true;
		}
		return false;
	}

	FileOutputStream fout;
	int mPreviewWidth;
	int mPreviewHeight;

	private void initCamera() 
	{
		if (null != mCamera) 
		{
			deal();
			// mCamera.autoFocus(null);
			//
			bIfPreview = true;
			// verify if successfully set
			realsize = mCamera.getParameters().getPreviewSize();
			mPreviewHeight = realsize.height; //
			mPreviewWidth = realsize.width;
			realfmt = mCamera.getParameters().getPreviewFormat();
			realfps = mCamera.getParameters().getPreviewFrameRate();
		}
	}
	
	
	private boolean isPreViewing;
	
	public void startPreView() {
		if (mCamera == null) {
			isPreViewing = true;
			return;
		}
		if (!isPreViewing) {
			isPreViewing = true;
			mCamera.startPreview();
			this.setKeepScreenOn(true);
		}
		
	}
	
	public void stopPreView() {
		isPreViewing = false;
		if (mCamera != null) {
			mCamera.stopPreview();
			this.setKeepScreenOn(false);
		}
	}

	private Camera.Parameters deal() 
	{
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);// default
		parameters.setPreviewFrameRate(15);

		// =================根据手机分辨率设置4:3的预览========================
		// 优先选择640x480 的分辨率 如果不支持640x480 则从播放列表中获取一个4:3的预览尺寸
		List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
		int PreviewWidth = 0;
		int PreviewHeight = 0;
		Boolean findSize = false;
		if (sizeList.size() > 1) 
		{
			Iterator<Camera.Size> itor = sizeList.iterator();
			while (itor.hasNext()) 
			{
				Camera.Size cur = itor.next();
				findSize = true;
				PreviewWidth = cur.width;
				PreviewHeight = cur.height;
				if (PreviewHeight == 480
						&& PreviewHeight * 16 / 9 == PreviewWidth) 
				{
					break;
				}
			}
			
			if (PreviewWidth != 640) 
			{
				itor = sizeList.iterator();
				while (itor.hasNext()) 
				{
					Camera.Size cur = itor.next();
					findSize = true;
					PreviewWidth = cur.width;
					PreviewHeight = cur.height;
					if (PreviewHeight * 16 / 9 == PreviewWidth) 
					{
						break;
					}
				}
			}
		}
		
		if (!findSize) 
		{
			PreviewWidth = sizeList.get(0).width;
			PreviewHeight = sizeList.get(0).height;
		}
		
		V2Log.e("----"+PreviewWidth+"   "+PreviewHeight);
		parameters.setPreviewSize(PreviewWidth, PreviewHeight);
		// =================根据手机分辨率设置4:3的预览========================

		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(this);
		mCamera.setParameters(parameters);
		return parameters;
	}

	private void deinitCamera() 
	{
		if (null != mCamera) 
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			bIfPreview = false;
			mCamera.release();
			mCamera = null;
		}
	}

	boolean first_frame = true;
	long s_ts;
	long cnt = 0;
	long avg = 0;

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) 
	{
		try 
		{
			if (streamStarted) 
			{
				long ts = System.currentTimeMillis();
				long delta = ts - s_ts;
				s_ts = ts;

				if (first_frame) 
				{
					first_frame = false;
				} 
				else 
				{
					avg += delta;
					cnt += 1;
				}

				x264Input(data, data.length, ts);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private int cdata;

	public native boolean openPublisher(String uri);

	public native boolean openVideoEncoder(int width, int height,
			boolean square, int rotate, int fpsnum, int fpsden, int bitrate,
			int csp);

	public native void x264Input(byte[] in, int in_size, long ts);

	public native boolean openAudioEncoder(int channels, int samplebytes,
			int samplerate, int bitrate);

	public native void faacInput(short[] in, int in_size, long ts);

	public native void closePublisher();

	static 
	{
		System.loadLibrary("encoder");
	}

	long ts_last;

	public class PcmRecorder implements Runnable 
	{
		private volatile boolean isRecording;

		public PcmRecorder() 
		{
			super();
			isRecording = false;
		}

		public void stopRecording() 
		{
			isRecording = false;
		}

		@Override
		public void run() 
		{
			long ts = 0;

			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

			int bufferRead = 0;
			int bufferSize = AudioRecord.getMinBufferSize(44100,
					AudioFormat.CHANNEL_IN_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			bufferSize = bufferSize > 2048 ? bufferSize : 2048;
			bufferSize *= 2;// make twice bufferSize

			short[] tempBuffer = new short[bufferSize];
			AudioRecord recordInstance = new AudioRecord(
					MediaRecorder.AudioSource.MIC, 44100,
					AudioFormat.CHANNEL_IN_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize);

			recordInstance.startRecording();
			isRecording = true;
			while (this.isRecording) 
			{
				bufferRead = recordInstance.read(tempBuffer, 0, 2048);
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) 
				{
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				} 
				else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) 
				{
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_BAD_VALUE");
				} 
				else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) 
				{
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				}

				ts = System.currentTimeMillis();
				faacInput(tempBuffer, bufferRead, ts);
			}
			recordInstance.stop();
		}
	}
}
