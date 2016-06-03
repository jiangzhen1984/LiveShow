package com.v2tech.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v2tech.v2liveshow.R;

public class MessageMarqueeLinearLayout extends LinearLayout {

	private static final String IDLE = "IDLE";

	private static final String BUSY = "BUSY";

	private ImageView settingBtn;
	private LinearLayout messagePoll;

	private int mMaxLines = 1;

	private TextView[] messagesText = new TextView[3];
	private Animation[] anmations = new Animation[3];
	private Queue<CharSequence> pendingMsgQueue = new LinkedList<CharSequence>();

	private MessageMarqueeLayoutListener listener;

	public MessageMarqueeLinearLayout(Context context) {
		super(context);
	}

	public MessageMarqueeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MessageMarqueeLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.message_marquee_setting_btn) {
			settingBtn = (ImageView) child;
			settingBtn.setOnClickListener(clickListener);
		} else if (child.getId() == R.id.message_pool) {
			messagePoll = (LinearLayout) child;
			init();
		}
	}

	private void init() {
		messagesText[0] = new TextView(getContext());
		LinearLayout.LayoutParams ll0 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		messagePoll.addView(messagesText[0], ll0);

		messagesText[1] = new TextView(getContext());
		LinearLayout.LayoutParams ll1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		messagePoll.addView(messagesText[1], ll1);

		messagesText[2] = new TextView(getContext());
		LinearLayout.LayoutParams ll2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		messagePoll.addView(messagesText[2], ll2);

	}

	public void addMessageString(CharSequence msg) {
		for (int i = 0; i < mMaxLines; i++) {
			if (IDLE.equals(messagesText[i].getTag())
					|| (messagesText[i].getTag() == null)) {
				messagesText[i].setText(msg);
				messagesText[i].setTag(BUSY);
				startMessageAni(i);
				return;
			}
		}

		pendingMsgQueue.add(msg);
	}

	public void updateMessageShow(boolean flag) {
		settingBtn.setImageResource(flag ? R.drawable.message_marquee_enable
				: R.drawable.message_marquee_disable);
		messagePoll.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	private void startMessageAni(final int index) {
		Animation ani = anmations[index];
		if (ani == null) {
			ani = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT,
					1.0F, TranslateAnimation.RELATIVE_TO_PARENT, -1.0F,
					TranslateAnimation.ABSOLUTE, 1.0F,
					TranslateAnimation.ABSOLUTE, 1.0F);
			ani.setFillAfter(true);
			ani.setDuration(13000);
			ani.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation animation) {
					messagesText[index].setTag(IDLE);
					messagesText[index].setText(null);
					firePendingMsg();
				}

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

			});

			anmations[index] = ani;

		}
		messagesText[index].startAnimation(ani);
	}

	private boolean firePendingMsg() {
		CharSequence pendingMsg = pendingMsgQueue.poll();
		if (pendingMsg == null) {
			return false;
		}
		for (int i = 0; i < mMaxLines; i++) {
			if (IDLE.equals(messagesText[i].getTag())
					|| (messagesText[i].getTag() == null)) {
				messagesText[i].setText(pendingMsg);
				messagesText[i].setTag(BUSY);
				startMessageAni(i);
				return true;
			}
		}

		return false;
	}

	public void clearPendingMsg() {
		pendingMsgQueue.clear();

		for (int i = 0; i < mMaxLines; i++) {
			if (anmations[i] != null) {
				anmations[i].cancel();
			}

		}

	}
	
	

	public MessageMarqueeLayoutListener getListener() {
		return listener;
	}

	public void setListener(MessageMarqueeLayoutListener listener) {
		this.listener = listener;
	}



	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener == null) {
				return;
			}
			switch (v.getId()) {
			case R.id.message_marquee_setting_btn:
				listener.onMessageSettingBtnClicked(v);
				break;
			}

		}

	};

	public interface MessageMarqueeLayoutListener {
		public void onMessageSettingBtnClicked(View v);
	}

}
