package com.v2tech.view;

import android.support.v4.app.FragmentActivity;

import com.v2tech.presenter.BasePresenter;

public abstract class BaseFragmentActivity extends FragmentActivity {
	

	public BaseFragmentActivity() {
	}
	
	
	public abstract BasePresenter getPresenter();

}
