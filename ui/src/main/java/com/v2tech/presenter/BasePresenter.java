package com.v2tech.presenter;

import android.os.HandlerThread;

public abstract class BasePresenter {
	
	
	protected HandlerThread backendThread;
	
	protected  UIState uiState;

	public BasePresenter() {
		super();
		uiState = UIState.UNKNOWN;
		backendThread = new HandlerThread(this.getClass().getName());
		backendThread.start();
	}

	
	protected void destroyBackendThread() {
		backendThread.quit();
	}

	public void onUICreated() {
		uiState = UIState.CREATED;
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
		uiState = UIState.DESTROYED;
		destroyBackendThread();
		GlobalPresenterManager.getInstance().onPresenterDestroyed(this);
	}
	
	
	public void onReturnBtnClicked() {
		
	}



	public enum UIState {
		UNKNOWN,
		CREATED,
		DESTROYED;

	}
}
