package com.v2tech.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;

import com.V2.jni.AudioRequest;
import com.V2.jni.ChatRequest;
import com.V2.jni.ConfRequest;
import com.V2.jni.ConfigRequest;
import com.V2.jni.FileRequest;
import com.V2.jni.GroupRequest;
import com.V2.jni.ImRequest;
import com.V2.jni.NativeInitializer;
import com.V2.jni.ServerRecordRequest;
import com.V2.jni.VideoBCRequest;
import com.V2.jni.VideoMixerRequest;
import com.V2.jni.VideoRequest;
import com.V2.jni.WBRequest;
import com.V2.jni.util.V2Log;
import com.baidu.mapapi.SDKInitializer;
import com.v2tech.util.GlobalConfig;
import com.v2tech.util.StorageUtil;

public class MainApplication extends Application {

	private Vector<WeakReference<Activity>> list = new Vector<WeakReference<Activity>>();

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
				SDKInitializer.initialize(this);
				
		initGloblePath();
		
		String path = GlobalConfig.getGlobalPath();
		File pa = new File(GlobalConfig.getGlobalUserAvatarPath());
		if (!pa.exists()) {
			boolean res = pa.mkdirs();
			V2Log.i(" create avatar dir " + pa.getAbsolutePath() + "  " + res);
		}
		pa.setWritable(true);
		pa.setReadable(true);

		File image = new File(GlobalConfig.getGlobalPicsPath());
		if (!image.exists()) {
			boolean res = image.mkdirs();
			V2Log.i(" create image dir " + image.getAbsolutePath() + "  " + res);
		}
		File audioPath = new File(GlobalConfig.getGlobalAudioPath());
		if (!audioPath.exists()) {
			boolean res = audioPath.mkdirs();
			V2Log.i(" create audio dir " + audioPath.getAbsolutePath() + "  "
					+ res);
		}
		File filePath = new File(GlobalConfig.getGlobalFilePath());
		if (!filePath.exists()) {
			boolean res = filePath.mkdirs();
			V2Log.i(" create file dir " + filePath.getAbsolutePath() + "  "
					+ res);
		}

		

		initConfFile();

		// Load native library
		System.loadLibrary("event");
		System.loadLibrary("v2vi");
		System.loadLibrary("v2ve");
		// System.loadLibrary("NetEvent");
		System.loadLibrary("v2client");

		// Initialize native library
		NativeInitializer.getIntance()
				.initialize(getApplicationContext(), path);
		ImRequest.getInstance(getApplicationContext());
		GroupRequest.getInstance();
		VideoRequest.getInstance(getApplicationContext());
		ConfRequest.getInstance(getApplicationContext());
		AudioRequest.getInstance(getApplicationContext());
		WBRequest.getInstance(getApplicationContext());
		ChatRequest.getInstance(getApplicationContext());
		VideoMixerRequest.getInstance();
		FileRequest.getInstance(getApplicationContext());
		
		VideoBCRequest.getInstance();//.Initialize(VideoBCRequest.getInstance());
		
		ServerRecordRequest.getInstance().initialize(ServerRecordRequest.getInstance());

		// Start deamon service
		getApplicationContext().startService(
				new Intent(getApplicationContext(), JNIService.class));

		initGlobalConfiguration();
		
		new ConfigRequest().setServerAddress("118.145.28.194", 5123);
	}

	/**
	 * 初始化程序数据存储目录
	 */
	private void initGloblePath() {
		GlobalConfig.SDCARD_GLOBLE_PATH = StorageUtil.getAbsoluteSdcardPath();
		File f = new File(GlobalConfig.getGlobalPath());
		if (!f.exists()) {
			f.mkdirs();
			f.setReadable(true);
			f.setWritable(true);
		}
	}

	private void initConfFile() {
		// Initialize global configuration file

		File optionsFile = new File(GlobalConfig.getGlobalPath()
				+ "/log_options.xml");
		{
			String content = "<xml><path>log</path><v2platform><outputdebugstring>0</outputdebugstring><level>5</level><basename>v2platform</basename><path>log</path><size>1024</size></v2platform></xml>";
			OutputStream os = null;
			try {
				os = new FileOutputStream(optionsFile);
				os.write(content.getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		{
			File cfgFile = new File(GlobalConfig.getGlobalPath()
					+ "/v2platform.cfg");
			String contentCFG = "<v2platform><C2SProxy><ipv4 value=''/><tcpport value=''/></C2SProxy></v2platform>";

			OutputStream os = null;
			try {
				os = new FileOutputStream(cfgFile);
				os.write(contentCFG.getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		V2Log.d(" terminating....");
		ImRequest.getInstance(this).unInitialize();
		GroupRequest.getInstance().unInitialize();
		VideoRequest.getInstance(this).unInitialize();
		ConfRequest.getInstance(this).unInitialize();
		AudioRequest.getInstance(this).unInitialize();
		WBRequest.getInstance(this).unInitialize();
		ChatRequest.getInstance(this).unInitialize();
		this.getApplicationContext().stopService(
				new Intent(this.getApplicationContext(), JNIService.class));
		V2Log.d(" terminated");

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		V2Log.e("=================== low memeory :");
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		V2Log.e("=================== trim memeory :" + level);
	}

	private void initGlobalConfiguration() {
		Configuration conf = getResources().getConfiguration();
		if (conf.smallestScreenWidthDp >= 600) {
			conf.orientation = Configuration.ORIENTATION_LANDSCAPE;
		} else {
			conf.orientation = Configuration.ORIENTATION_PORTRAIT;
		}

	}

	public void requestQuit() {
		for (int i = 0; i < list.size(); i++) {
			WeakReference<Activity> w = list.get(i);
			Object obj = w.get();
			if (obj != null) {
				((Activity) obj).finish();
			}
		}

		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				GlobalConfig.saveLogoutFlag(getApplicationContext());
				System.exit(0);
			}

		}, 1000);
	}

}
