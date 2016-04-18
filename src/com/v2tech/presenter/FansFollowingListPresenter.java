package com.v2tech.presenter;

import android.content.Context;
import android.content.Intent;

import com.v2tech.view.FansFollowActivity;

public class FansFollowingListPresenter extends BasePresenter {
	
	
	public interface FansFollowingListPresenterUI {
		
	}

	
	
	private Context context;
	private FansFollowingListPresenterUI ui;
	public FansFollowingListPresenter(Context context,
			FansFollowingListPresenterUI ui) {
		super();
		this.context = context;
		this.ui = ui;
	}
	
	
	
	
	public void onItemClicked(int position,
			long id) {
		Intent i = new Intent();
		i.setClass(context, FansFollowActivity.class);
		context.startActivity(i);
	}
}
