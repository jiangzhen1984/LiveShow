package com.v2tech.view;

import android.app.Activity;

import com.v2tech.presenter.BasePresenter;

public abstract class BaseActivity extends Activity {
	

	public BaseActivity() {
	}
	
	
	public abstract BasePresenter getPresenter();

}
