package com.v2tech.view;

import android.app.Activity;

import com.v2tech.presenter.BasePresenter;

public abstract class BaseActivity extends Activity {
	
	protected BasePresenter basePresenter;

	public BaseActivity() {
	}
	
	
	public BasePresenter getPresenter() {
		return this.basePresenter;
	}

}
