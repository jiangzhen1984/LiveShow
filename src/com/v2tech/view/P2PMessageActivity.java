package com.v2tech.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.v2tech.presenter.P2PMessagePresenter;
import com.v2tech.presenter.P2PMessagePresenter.Item;
import com.v2tech.presenter.P2PMessagePresenter.P2PMessagePresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.widget.RichEditText;
import com.v2tech.widget.VoiceRecordDialogWidget;
import com.v2tech.widget.emoji.EmojiLayoutWidget;

public class P2PMessageActivity extends BaseActivity implements P2PMessagePresenterUI, OnClickListener {

	
	private InputMethodManager imm;
	
	private P2PMessagePresenter presenter;
	
	private ListView listView;
	private View addtionalLayout;
	private View plusBtn;
	private View emojiBtn;
	private EmojiLayoutWidget emojiWidget;
	private RichEditText messageEt;
	private ImageView switcherBtn;
	private TextView voiceRecordBtn;
	private PopupWindow   dialog;
	private VoiceRecordDialogWidget voiceDialogWidget;
	private View sendBtn;
	
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
		switcherBtn= (ImageView)findViewById(R.id.p2p_message_switcher_btn);
		voiceRecordBtn= (TextView)findViewById(R.id.p2p_message_voice_record_btn);
		sendBtn = findViewById(R.id.p2p_message_send_btn);
		
		switcherBtn.setOnClickListener(this);
		emojiBtn.setOnClickListener(this);
		plusBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		voiceRecordBtn.setOnTouchListener(touchListener);
		
		messageEt.addTextChangedListener(textWatcher);
		
		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		
		presenter = new P2PMessagePresenter(this.getApplicationContext(), this);
		
		presenter.onUICreated();
		emojiWidget.setListener(presenter);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onUIDestroyed();
		messageEt.removeTextChangedListener(textWatcher);
	}

	
	@Override
	public void setAdapter(BaseAdapter adapter) {
		listView.setAdapter(adapter);
	}
	
	
	public View getView() {
		View view = LayoutInflater.from(this).inflate(R.layout.p2p_message_layout, (ViewGroup)null);
		LocalBind lb = new LocalBind();
		lb.leftAvtar = (ImageView)view.findViewById(R.id.p2p_message_left_avtar);
		lb.rightAvtar = (ImageView)view.findViewById(R.id.p2p_message_right_avtar);
		lb.leftContent = (TextView)view.findViewById(R.id.p2p_message_left_content);
		lb.rightContent = (TextView)view.findViewById(R.id.p2p_message_right_content);
		lb.time = (TextView)view.findViewById(R.id.p2p_message_layout_time);
		lb.leftLy = view.findViewById(R.id.p2p_message_left_ly);
		lb.rightLy = view.findViewById(R.id.p2p_message_right_ly);
		lb.leftContentAdr = (TextView)view.findViewById(R.id.p2p_message_left_content_adr);
		lb.rightContentAdr = (TextView)view.findViewById(R.id.p2p_message_right_content_adr);
		
		
		lb.leftContentBG  = view.findViewById(R.id.p2p_message_left_content_bg);
		lb.rightContentBG  = view.findViewById(R.id.p2p_message_right_content_bg);
		lb.leftContentMacro  = (ImageView)view.findViewById(R.id.p2p_message_left_content_macro);
		lb.rightContentMacro = (ImageView)view.findViewById(R.id.p2p_message_right_content_macro);
		
		lb.leftContentBG.setTag(lb);
		lb.rightContentBG.setTag(lb);
		lb.leftContentBG.setOnClickListener(contentClickListener);
		lb.rightContentBG.setOnClickListener(contentClickListener);
		view.setTag(lb);
		return view;
	}
	
	
	public void updateView(View view, int dir, Bitmap bm, CharSequence content, int msgType, boolean isAudioPlaying, Object tag) {
		LocalBind lb = (LocalBind)view.getTag();
		if (dir == P2PMessagePresenter.ITEM_TYPE_DATE) {
			lb.time.setText(content);
			lb.time.setVisibility(View.VISIBLE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.GONE);
		} else if (dir == P2PMessagePresenter.ITEM_TYPE_SELF) {
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.VISIBLE);
			lb.rightLy.setVisibility(View.GONE);
			if (msgType == P2PMessagePresenter.ITEM_MSG_TYPE_TEXT) {
				lb.leftContentAdr.setVisibility(View.GONE);
				lb.leftContent.setText(content);
				lb.leftContent.setVisibility(View.VISIBLE);
				lb.leftContentMacro.setVisibility(View.GONE);
				//TODO stop animation
			} else if (msgType == P2PMessagePresenter.ITEM_MSG_TYPE_AUDIO) {
				lb.leftContentAdr.setVisibility(View.VISIBLE);
				lb.leftContent.setVisibility(View.GONE);
				lb.leftContent.setText("");
				lb.leftContentMacro.setVisibility(View.VISIBLE);
				//TODO update content length according to audio duration
				if (isAudioPlaying)  {
					//TODO start animation
				}
			}
			
			if (bm != null) {
				lb.leftAvtar.setImageBitmap(bm);
			}
		} else if (dir == P2PMessagePresenter.ITEM_TYPE_OTHERS) {
			lb.rightContent.setText(content);
			lb.time.setVisibility(View.GONE);
			lb.leftLy.setVisibility(View.GONE);
			lb.rightLy.setVisibility(View.VISIBLE);
			if (msgType == P2PMessagePresenter.ITEM_MSG_TYPE_TEXT) {
				lb.rightContentAdr.setVisibility(View.GONE);
				lb.rightContent.setText(content);
				lb.rightContent.setVisibility(View.VISIBLE);
				lb.rightContentMacro.setVisibility(View.GONE);
				//TODO stop animation
			} else if (msgType == P2PMessagePresenter.ITEM_MSG_TYPE_AUDIO) {
				lb.rightContentAdr.setVisibility(View.VISIBLE);
				lb.rightContent.setText("");
				lb.rightContent.setVisibility(View.GONE);
				lb.rightContentMacro.setVisibility(View.VISIBLE);
				//TODO update content length according to audio duration
				if (isAudioPlaying)  {
					//TODO start animation
				}
			}
			if (bm != null) {
				lb.rightAvtar.setImageBitmap(bm);
			}
		}
		
		lb.tag = tag;
	}
	
	
	
	public void showAdditionLayout(boolean flag) {
		addtionalLayout.setVisibility(flag ? View.VISIBLE : View.GONE);
	}
	
	
	
	public void showEmojiLayout(boolean flag) {
		emojiWidget.setVisibility(flag?View.VISIBLE:View.GONE);
		if (flag) {
			imm.hideSoftInputFromWindow(messageEt.getWindowToken(), 0);
		}
	}
	
	
	@Override
	public void finishMainUI() {
		finish();
	}
	
	
	public RichEditText getEditable() {
		return this.messageEt;
	}
	
	
	public void scrollTo(final int position) {
//		listView.post(new Runnable() {
//
//			@Override
//			public void run() {
//				listView.setSelection(position);
//			}
//
//		});
		listView.setSelection(position);
	}
	
	
	public long getIntentUserId() {
		return this.getIntent().getLongExtra("chatuserid", 0);
	}
	
	
	public void updateVoiceDBLevel(int level) {
		voiceDialogWidget.updateVolumnLevel(level);
	}
	
	public void switchToVoice() {
		switcherBtn.setImageResource(R.drawable.text_switcher_btn);
		voiceRecordBtn.setText(R.string.p2p_message_btn_text_pressed_tip);
		voiceRecordBtn.setVisibility(View.VISIBLE);
		messageEt.setVisibility(View.GONE);
	
	    imm.hideSoftInputFromWindow(messageEt.getWindowToken(), 0);
	}
	
	public void switchToText() {
		switcherBtn.setImageResource(R.drawable.voice_switcher_btn);
		voiceRecordBtn.setVisibility(View.GONE);
		messageEt.setVisibility(View.VISIBLE);
	}
	
	
	
	public void showDialog(boolean flag, int type) {
		if (dialog == null) {
			dialog = new PopupWindow(this);
			voiceDialogWidget = (VoiceRecordDialogWidget) LayoutInflater.from(
					this).inflate(R.layout.voice_record_dialog_widget_layout,
							(ViewGroup)null);
			dialog.setContentView(voiceDialogWidget);
			dialog.setOutsideTouchable(false);
			voiceDialogWidget.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			dialog.setWidth(voiceDialogWidget.getMeasuredWidth());
			dialog.setHeight(voiceDialogWidget.getMeasuredHeight());
		}
		
		if (flag) {
			switch (type) {
			case P2PMessagePresenter.DIALOG_TYPE_VOLUMN:
				voiceDialogWidget.showVolumnView();
				break;
			case P2PMessagePresenter.DIALOG_TYPE_LONG_DURATION:
				voiceDialogWidget.showLongDurationView();
				break;
			case P2PMessagePresenter.DIALOG_TYPE_SHORT_DURATION:
				voiceDialogWidget.showRequireMoreDuration();
				break;
			case P2PMessagePresenter.DIALOG_TYPE_TOUCH_UP_CANCEL:
				voiceDialogWidget.showTouchUpCancelView();
				break;
			}
			if (!dialog.isShowing()) {
				dialog.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
		} else {
			dialog.dismiss();
		}
	}
	
	
	
	public void showSendBtn(boolean flag) {
		this.sendBtn.setVisibility(flag? View.VISIBLE : View.GONE);
	}
	
	public void showPlusBtn(boolean flag) {
		this.plusBtn.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}
	
	
	public void startAudioPlayAniamtion(View v, Item item) {
		LocalBind lb = (LocalBind)v.getTag();
		if (lb.leftLy.getVisibility() == View.VISIBLE) {
			lb.leftContentMacro.setImageResource(R.drawable.audio_play_left_volumn_ani);
		} else if (lb.rightLy.getVisibility() == View.VISIBLE) {
			lb.rightContentMacro.setImageResource(R.drawable.audio_play_right_volumn_ani);
		}
		lb.playingState = true;
	}
	
	public void stopAudioPlayAniamtion(View v, Item item) {
		LocalBind lb = (LocalBind)v.getTag();
		if (lb.leftLy.getVisibility() == View.VISIBLE) {
			lb.leftContentMacro.setImageResource(R.drawable.audio_play_voice_left_frame3);
		} else if (lb.rightLy.getVisibility() == View.VISIBLE) {
			lb.rightContentMacro.setImageResource(R.drawable.audio_play_voice_right_frame3);
		}
		lb.playingState = false;
	}
	
	
	
	
	
	@Override
	public void stopPlayAniamtion() {
		int len  = listView.getChildCount();
		for (int i = 0; i < len; i++) {
			View v = listView.getChildAt(i);
			LocalBind lb = (LocalBind)v.getTag();
			if (lb.playingState) {
				stopAudioPlayAniamtion(v, null);
			}
		}
		
	}
	
	
	

	@Override
	public void simulateKeboardKeyEvent(KeyEvent evt) {
		messageEt.dispatchKeyEvent(evt);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.p2p_message_plus_btn:
			presenter.plusBtnClicked();
			//presenter.sendBtnClicked();
			break;
		case R.id.p2p_message_emoji_btn:
			presenter.emojiBtnClicked();
			break;
		case R.id.title_bar_left_btn:
			presenter.onReturnBtnClicked();
			break;
		case R.id.p2p_message_switcher_btn:
			presenter.switcherBtnClicked();
			break;
		case R.id.p2p_message_send_btn:
			presenter.sendBtnClicked();
			break;
		}
		
	}
	
	
	private boolean inBoundsFlag = true;
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				voiceRecordBtn.setPressed(true);
				voiceRecordBtn.setText(R.string.p2p_message_btn_text_pressed_tip);
				presenter.onRecordBtnTouchDown(event);
				inBoundsFlag = true;
				break;
			case MotionEvent.ACTION_MOVE:
				Rect r = new Rect();
				v.getDrawingRect(r);
				if (r.contains((int)event.getX(), (int)event.getY())) {
					if (!inBoundsFlag) {
						inBoundsFlag = true;
						presenter.onRecordBtnTouchMoveInBtn(event);
					}
				} else {
					if (inBoundsFlag) {
						presenter.onRecordBtnTouchMoveOutOfBtn(event);
						inBoundsFlag = false;
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				voiceRecordBtn.setPressed(false);
				presenter.onRecordBtnTouchUp(event);
				voiceRecordBtn.setText(R.string.p2p_message_btn_text_release_tip);
				break;
			}
			return true;
		}
		
	};
	
	
	private OnClickListener contentClickListener = new  OnClickListener() {

		@Override
		public void onClick(View v) {
			LocalBind lb = (LocalBind)v.getTag();
			presenter.onContentClicked(v, lb.tag);
		}
		
	};



	
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			presenter.onTextChanged(s);
		}
		
	};

	class LocalBind {
		TextView time;
		ImageView leftAvtar;
		ImageView rightAvtar;
		TextView leftContent;
		TextView rightContent;
		View leftContentBG;
		View rightContentBG;
		ImageView leftContentMacro;
		ImageView rightContentMacro;
		TextView leftContentAdr;
		TextView rightContentAdr;
		View leftLy;
		View rightLy;
		Object tag;
		boolean playingState;
	}
	
}
