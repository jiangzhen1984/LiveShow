package com.V2.jni;

import android.content.Context;

public class NativeInitializer {
	
	private static NativeInitializer instance;
	
	private NativeInitializer() {
		
	}
	public static NativeInitializer getIntance() {
		if (instance == null) {
			instance = new NativeInitializer();
		}
		return instance;
	}
	public native void initialize(Context context, String path);

}
