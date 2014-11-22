package com.V2.jni;

import java.io.File;
import java.io.IOException;

public class DataStoreUtils {

	
	@SuppressWarnings("unused")
	private static void createStorePath(String path){
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		chmod(file.getAbsolutePath());
	}
	
	
	public static void  chmod(String path){
		String cmd="chmod 777  "+path;
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("更改权限失败!");
		}
		
	}
}

