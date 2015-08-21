/**
 * 
 */
package com.v2tech.view;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.v2tech.service.UserService;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.User;

/**
 * @author jiangzhen
 * 
 */
public class FansFollowingListActivity extends Activity implements OnClickListener {

	private static final int TYPE_FANS = 1;
	private static final int TYPE_FOLLOWING = 2;

	private UserService us;

	private HandlerThread ht;
	private Handler requestHandler;
	private int mType;
	private TextView mTitle;
	private ListView mListView;
	private LocalAdapter mAdapter;
	private List<User> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fans_following_list_activity);
		us = new UserService();
		ht = new HandlerThread("FansFollowingListActivity");
		ht.start();

		
		mTitle =  (TextView)findViewById(R.id.fans_following_title);
		mListView = (ListView)findViewById(R.id.fans_following_listview);
		
		findViewById(R.id.return_button).setOnClickListener(this);
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		requestHandler = new Handler(ht.getLooper());
		this.overridePendingTransition(R.animator.right_to_left_in, 0);

		init(getIntent());
	}
	
	
	private void init(Intent i) {
		String type = i.getStringExtra("type");
		if ("fans".equalsIgnoreCase(type)) {
			mTitle.setText(R.string.fans_list_title);
			mType = TYPE_FANS;
		} else {
			mTitle.setText(R.string.following_list_title);
			mType = TYPE_FOLLOWING;
		}
		//Init List
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.return_button:
		default:
				finish();
				return;
		}
		
	}




	

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.animator.left_to_right_out);
	}

	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		us.clearCalledBack();
		ht.quit();
	}
	
	



	private Handler localHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case R.id.return_button:
				finish();
				break;
			default:
				break;
			}
		}

	};
	
	
	
	class LocalAdapter extends BaseAdapter {
		
		List<User> list;
		
		
		public LocalAdapter(List<User> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
		
	}


}
