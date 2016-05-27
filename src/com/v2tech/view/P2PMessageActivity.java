package com.v2tech.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.v2tech.presenter.P2PMessagePresenter;
import com.v2tech.presenter.P2PMessagePresenter.P2PMessagePresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.widget.RichEditText;
import com.v2tech.widget.emoji.EmojiLayoutWidget;

public class P2PMessageActivity extends BaseActivity implements P2PMessagePresenterUI, OnClickListener {

	private P2PMessagePresenter presenter;
	
	private ListView listView;
	private View addtionalLayout;
	private View plusBtn;
	private View emojiBtn;
	private EmojiLayoutWidget emojiWidget;
	private RichEditText messageEt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainApplication)this.getApplication()).onMainCreate();
		setContentView(R.layout.p2p_message_activity);
		listView = (ListView)findViewById(R.id.p2p_message_msg_listview);
		addtionalLayout = findViewById(R.id.p2p_message_addition_lay);
		plusBtn = findViewById(R.id.p2p_message_plus_btn);
		emojiBtn = findViewById(R.id.p2p_message_emoji_btn);
		emojiWidget= (EmojiLayoutWidget)findViewById(R.id.emoji_widget);
		messageEt= (RichEditText)findViewById(R.id.p2p_message_msg_et);
		
		messageEt.appendEmoji(R.drawable.emo_01);
		messageEt.appendEmoji(R.drawable.emo_02);
		messageEt.appendEmoji(R.drawable.emo_03);
		emojiBtn.setOnClickListener(this);
		plusBtn.setOnClickListener(this);
		
		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		
		presenter = new P2PMessagePresenter(this, this);
		
		presenter.onUICreated();
		emojiWidget.setListener(presenter);
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
	
	
	public void updateView(View view, int type, Bitmap bm, CharSequence content) {
		LocalBind lb = (LocalBind)view.getTag();
		if (type == P2PMessagePresenter.ITEM_TYPE_DATE) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.GONE);
		} else if (type == P2PMessagePresenter.ITEM_TYPE_SELF) {
			lb.leftContent.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.VISIBLE);
			lb.rightLy.setVisibility(View.GONE);
		}else if (type == P2PMessagePresenter.ITEM_TYPE_OTHERS) {
			lb.rightContent.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.VISIBLE);
		}
	}
	
	public void updateView(View view, int type, CharSequence content) {
		LocalBind lb = (LocalBind)view.getTag();
		if (type == P2PMessagePresenter.ITEM_TYPE_DATE) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.GONE);
		} else if (type == P2PMessagePresenter.ITEM_TYPE_SELF) {
			lb.leftContent.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.VISIBLE);
			lb.rightLy.setVisibility(View.GONE);
		}else if (type == P2PMessagePresenter.ITEM_TYPE_OTHERS) {
			lb.rightContent.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.VISIBLE);
		}
	}
	
	
	public void showAdditionLayout(boolean flag) {
		addtionalLayout.setVisibility(flag ? View.VISIBLE : View.GONE);
	}
	
	
	
	public void showEmojiLayout(boolean flag) {
		emojiWidget.setVisibility(flag?View.VISIBLE:View.GONE);
	}
	
	
	@Override
	public void finishMainUI() {
		finish();
	}
	
	
	public RichEditText getEditable() {
		return this.messageEt;
	}
	
	
	public void scrollTo(int position) {
		listView.setSelection(position);
	}
	
	
	public long getIntentUserId() {
		return this.getIntent().getLongExtra("chatuserid", -1);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.p2p_message_plus_btn:
			//presenter.plusBtnClicked();
			presenter.sendBtnClicked();
			break;
		case R.id.p2p_message_emoji_btn:
			presenter.emojiBtnClicked();
			break;
		case R.id.title_bar_left_btn:
			presenter.onReturnBtnClicked();
			break;
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
