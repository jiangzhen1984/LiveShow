package com.v2tech.presenter;

import java.lang.ref.WeakReference;
import java.util.Vector;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import com.v2tech.view.BaseActivity;
import com.v2tech.view.BaseFragmentActivity;

public class GlobalActivityManager implements ActivityLifecycleCallbacks {
	
	private static GlobalActivityManager instance;
	
	private Vector<WeakReference<Activity>> list = new Vector<WeakReference<Activity>>();

	private GlobalActivityManager() {
	}
	
	public synchronized static GlobalActivityManager getInstance() {
		if (instance == null) {
			instance = new GlobalActivityManager();
		}
		return instance;
	}

	
	public void finishAllActivities() {
		for (int i = 0; i < list.size(); i++) {
			WeakReference<Activity> w = list.get(i);
			Object obj = w.get();
			if (obj != null) {
				((Activity) obj).finish();
			}
		}
	}
	
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		list.add(new WeakReference<Activity>(activity));
		
	}

	@Override
	public void onActivityStarted(Activity activity) {
		if (activity instanceof  BaseActivity) {
			((BaseActivity)activity).getPresenter().onUICreated();
		} else if (activity instanceof BaseFragmentActivity) {
			((BaseFragmentActivity)activity).getPresenter().onUICreated();
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityPaused(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		synchronized(list) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				WeakReference<Activity> w = list.get(i);
				Activity act = w.get();
				if (act == activity) {
					list.remove(i);
					if (activity instanceof  BaseActivity) {
						((BaseActivity)activity).getPresenter().onUIDestroyed();
					} else if (activity instanceof BaseFragmentActivity) {
						((BaseFragmentActivity)activity).getPresenter().onUIDestroyed();
					}
					break;
				}
			}
		}
		
	}
	
	
	

}
