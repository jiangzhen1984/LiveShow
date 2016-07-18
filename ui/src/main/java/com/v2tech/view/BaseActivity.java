package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;

import com.v2tech.presenter.BasePresenter;

public abstract class BaseActivity extends Activity {
	

	public BaseActivity() {
	}
	
	
	public abstract BasePresenter getPresenter();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	

}
