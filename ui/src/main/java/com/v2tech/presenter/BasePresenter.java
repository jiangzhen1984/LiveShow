package com.v2tech.presenter;

import android.os.HandlerThread;

public abstract class BasePresenter {
	
	
	protected HandlerThread backendThread;
	
	
	
	
	public BasePresenter() {
		super();
		backendThread = new HandlerThread(this.getClass().getName());
		backendThread.start();
	}

	
	protected void destroyBackendThread() {
		backendThread.quit();
	}

	public void onUICreated() {
		GlobalPresenterManager.getInstance().onPresenterCreated(this);
	}
	
	
	public  void onUIStarted() {
		
	}
	
	public  void onUIResumed() {
		
	}
	
	public  void onUIPaused() {
		
	}
	
	public  void onUIStopped() {
		
	}
	
	public  void onUIDestroyed() {
		destroyBackendThread();
		GlobalPresenterManager.getInstance().onPresenterDestroyed(this);
	}
	
	
	public void onReturnBtnClicked() {
		
	}
}
