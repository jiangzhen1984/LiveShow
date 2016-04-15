package com.v2tech.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import com.V2.jni.AppShareRequest;
import com.V2.jni.AudioRequest;
import com.V2.jni.ChatRequest;
import com.V2.jni.ConfRequest;
import com.V2.jni.ConfigRequest;
import com.V2.jni.FileRequest;
import com.V2.jni.GroupRequest;
import com.V2.jni.ImRequest;
import com.V2.jni.InteractionRequest;
import com.V2.jni.NativeInitializer;
import com.V2.jni.SipRequest;
import com.V2.jni.VideoMixerRequest;
import com.V2.jni.VideoRequest;
import com.V2.jni.WBRequest;
import com.V2.jni.util.V2Log;
import com.baidu.mapapi.SDKInitializer;
import com.v2tech.net.DeamonWorker;
import com.v2tech.net.LogCollectionWorker;
import com.v2tech.net.lv.PacketTransformer;
import com.v2tech.util.GlobalConfig;
import com.v2tech.util.StorageUtil;

public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();
	private Vector<WeakReference<Activity>> list = new Vector<WeakReference<Activity>>();

	private boolean init = false;
	@Override
	public void onCreate() {
		super.onCreate();
	}

	
	
	
	public void onMainCreate() {
		if (init) {
			return;
		}
		init = true;
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
				
		initGloblePath();
		
		String path = GlobalConfig.getGlobalPath();
		File ro = new File(path);
		if (!ro.exists()) {
			boolean ret = ro.mkdirs();
			V2Log.i(" create  dir " + ro.getAbsolutePath() + "  " + ret);
		}
		ro.setWritable(true);
		
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

		File crashPath = new File(GlobalConfig.getGlobalCrashPath());
		if (!crashPath.exists()) {
			boolean res = crashPath.mkdirs();
			V2Log.i(" create crash dir " + crashPath.getAbsolutePath() + "  "
					+ res);
		}
		
		

		initGloblePath();
		initConfigSP();
		initConfigFile();

		//initHZPYDBFile();
		initResource();


		// Load native library
		System.loadLibrary("event");
		System.loadLibrary("v2vi");
		System.loadLibrary("v2ve");
		// System.loadLibrary("NetEvent");
		System.loadLibrary("v2client");

		// Initialize native library
		NativeInitializer.getIntance().initialize(getApplicationContext(), GlobalConfig.getGlobalPath());
		ImRequest.getInstance();
		GroupRequest.getInstance();
		VideoRequest.getInstance();
		ConfRequest.getInstance();
		AudioRequest.getInstance();
		WBRequest.getInstance();
		ChatRequest.getInstance();
		VideoMixerRequest.getInstance();
		FileRequest.getInstance();
		SipRequest.getInstance();
		AppShareRequest.getInstance();

		
		InteractionRequest.getInstance();//.Initialize(VideoBCRequest.getInstance());
		

		// Start deamon service
		getApplicationContext().startService(
				new Intent(getApplicationContext(), JNIService.class));

		initGlobalConfiguration();
		
		new ConfigRequest().setServerAddress(Constants.SERVER, 5123);
		
		
		DeamonWorker.getInstance().setPacketTransformer(new PacketTransformer());
		DeamonWorker.getInstance().connect(Constants.N_SERVER, 9999);
		new LogCollectionWorker().start();
		
		
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
		V2Log.d(" terminating....");
		ImRequest.getInstance().unInitialize();
		GroupRequest.getInstance().unInitialize();
		VideoRequest.getInstance().unInitialize();
		ConfRequest.getInstance().unInitialize();
		AudioRequest.getInstance().unInitialize();
		WBRequest.getInstance().unInitialize();
		ChatRequest.getInstance().unInitialize();
		this.getApplicationContext().stopService(
				new Intent(this.getApplicationContext(), JNIService.class));
		V2Log.d(" terminated");

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		V2Log.i("=================== low memeory :");
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		V2Log.i("=================== trim memeory :" + level);
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
	
	
	/**
	 * 创建SharedPreferences配置文件
	 */
	private void initConfigSP() {
		SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);

		Editor ed = sp.edit();
		ed.putInt("LoggedIn", 0);
		ed.commit();

		boolean isAppFirstLoad = sp.getBoolean("isAppFirstLoad", true);
		if (isAppFirstLoad) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = new File(GlobalConfig.getGlobalRootPath());
					if (file.exists()) {
						recursionDeleteOlderFiles(file);
					}
				}

				private void recursionDeleteOlderFiles(File file) {
					File[] files = file.listFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							File temp = files[i];
							if (temp.exists()) {
								if (temp.isDirectory()) {
									recursionDeleteOlderFiles(temp);
								} else {
									boolean delete = temp.delete();
									V2Log.d(TAG, "文件 - " + temp.getAbsolutePath() + " - 删除是否成功  : " + delete);
								}
							}
						}
					}
				}
			}).start();

			Editor editor = sp.edit();
			editor.putBoolean("isAppFirstLoad", false);
			editor.commit();
		}
	}

	/**
	 * 初始化一些默认资源的名字
	 */
	private void initResource() {
	
	}

	/**
	 * 初始化程序数据存储目录
	 */
	private void initGloblePath() {
		String saveData = "";
		boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
		if (!sdExist) {
			// --data/data/com.v2tech
			GlobalConfig.DEFAULT_GLOBLE_PATH = getApplicationContext().getFilesDir().getParent();
			saveData = GlobalConfig.DEFAULT_GLOBLE_PATH;
			V2Log.d(TAG, "SD can't be used , save path is ：" + GlobalConfig.DEFAULT_GLOBLE_PATH);
		} else {
			GlobalConfig.SDCARD_GLOBLE_PATH = StorageUtil.getAbsoluteSdcardPath();
			saveData = GlobalConfig.SDCARD_GLOBLE_PATH;
			V2Log.d(TAG, "SD can use , save path is ：" + GlobalConfig.SDCARD_GLOBLE_PATH);
		}

		// 创建数据文件夹，如果不成功则程序不能正常运行！
		File target = new File(saveData, GlobalConfig.DATA_SAVE_FILE_NAME);
		if (target.exists()) {
			V2Log.e(TAG, "v2tech folder already exist , : " + target.getAbsolutePath());
		} else {
			File temp = new File(saveData, GlobalConfig.DATA_SAVE_FILE_NAME + "_" + System.currentTimeMillis());
			temp.mkdirs();
			if (temp.exists()) {
				temp.renameTo(target);
			} else {
				V2Log.e(TAG, "Create folder that name is 'v2tech' failed! The application can't run!");
				GlobalConfig.SDCARD_GLOBLE_PATH = 	Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				saveData = GlobalConfig.SDCARD_GLOBLE_PATH;
				target = new File(saveData, GlobalConfig.DATA_SAVE_FILE_NAME);
				target.mkdirs();
			
			}
		}
	}

	/**
	 * 初始化搜索用到的hzpy.db文件
	 */
	private final String DATABASE_FILENAME = "hzpy.db";
	private void initHZPYDBFile() {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			// 获得.db文件的绝对路径 data/data/com.v2tech/databases
			String parent = getDatabasePath(DATABASE_FILENAME).getParent();
			File dir = new File(parent);
			// 如果目录不存在，创建这个目录
			if (!dir.exists())
				dir.mkdir();
			String databaseFilename = getDatabasePath(DATABASE_FILENAME).getPath();
			File file = new File(databaseFilename);
			// 目录中不存在 .db文件，则从res\raw目录中复制这个文件到该目录

		} catch (Exception e) {
			e.getStackTrace();
			V2Log.e("loading HZPY.db SQListe");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	/**
	 * 创建必须存在的默认配置文件
	 */
	private void initConfigFile() {
		File optionsFile = new File(GlobalConfig.getGlobalRootPath(), GlobalConfig.DEFAULT_CONFIG_LOG_FILE);
		File temp = new File(GlobalConfig.getGlobalRootPath());
		if (!temp.exists()) {
			V2Log.e(TAG, "temp - " + GlobalConfig.getGlobalRootPath());
			temp.mkdirs();
		}

		if (!optionsFile.exists()) {
			try {
				optionsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String content = "<xml><path>log</path><v2platform><outputdebugstring>0</outputdebugstring>"
				+ "<level>5</level><basename>v2platform</basename><path>log</path><size>1024</size></v2platform></xml>";
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

		File cfgFile = new File(GlobalConfig.getGlobalRootPath(), GlobalConfig.DEFAULT_CONFIG_FILE);
		String contentCFG = "<v2platform><C2SProxy><ipv4 value=''/><tcpport value=''/></C2SProxy></v2platform>";

		OutputStream os1 = null;
		try {
			os1 = new FileOutputStream(cfgFile);
			os1.write(contentCFG.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os1 != null) {
				try {
					os1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		cfgFile = new File(GlobalConfig.getGlobalPath(), GlobalConfig.DEFAULT_CONFIG_FILE);
		contentCFG = "<v2platform><C2SProxy><ipv4 value=''/><tcpport value=''/></C2SProxy></v2platform>";

		try {
			os1 = new FileOutputStream(cfgFile);
			os1.write(contentCFG.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os1 != null) {
				try {
					os1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
