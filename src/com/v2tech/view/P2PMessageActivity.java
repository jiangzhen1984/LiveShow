package com.v2tech.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.v2tech.presenter.P2PMessagePresenter;
import com.v2tech.presenter.P2PMessagePresenter.P2PMessagePresenterUI;
import com.v2tech.v2liveshow.R;

public class P2PMessageActivity extends Activity implements P2PMessagePresenterUI {

	private P2PMessagePresenter presenter;
	
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p2p_message_activity);
		listView = (ListView)findViewById(R.id.p2p_message_msg_listview);
		presenter = new P2PMessagePresenter(this, this);
		
		presenter.onUICreated();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onUIDestroyed();
	}

	
	@Override
	public void setAdapter(BaseAdapter adapter) {
		listView.setAdapter(adapter);
	}
	
	
	public View getView() {
		View view = LayoutInflater.from(this).inflate(R.layout.p2p_message_layout, null);
		LocalBind lb = new LocalBind();
		lb.leftAvtar = (ImageView)view.findViewById(R.id.p2p_message_left_avtar);
		lb.rightAvtar = (ImageView)view.findViewById(R.id.p2p_message_right_avtar);
		lb.leftContent = (TextView)view.findViewById(R.id.p2p_message_left_content);
		lb.rightContent = (TextView)view.findViewById(R.id.p2p_message_right_content);
		lb.time = (TextView)view.findViewById(R.id.p2p_message_layout_time);
		lb.leftLy = view.findViewById(R.id.p2p_message_left_ly);
		lb.rightLy = view.findViewById(R.id.p2p_message_right_ly);
		view.setTag(lb);
		return view;
	}
	
	
	public void updateView(View view, int type, Bitmap bm, String content) {
		LocalBind lb = (LocalBind)view.getTag();
		if (type == 2) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.GONE);
		} else if (type == 0) {
			lb.time.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.VISIBLE);
			lb.rightLy.setVisibility(View.GONE);
		}else if (type == 1) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.VISIBLE);
		}
	}
	
	public void updateView(View view, int type, String content) {
		LocalBind lb = (LocalBind)view.getTag();
		if (type == 2) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.GONE);
		} else if (type == 0) {
			lb.time.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.VISIBLE);
			lb.rightLy.setVisibility(View.GONE);
		}else if (type == 1) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.VISIBLE);
		}
	}
	
	
	class LocalBind {
		TextView time;
		ImageView leftAvtar;
		ImageView rightAvtar;
		TextView leftContent;
		TextView rightContent;
		View leftLy;
		View rightLy;
	}
	
}
