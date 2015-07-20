package com.v2tech.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageMarqueeLinearLayout extends LinearLayout {

	private static final String IDLE = "IDLE";
	
	private static final String BUSY = "BUSY";

	private int mMaxLines = 3;

	private TextView[] messagesText = new TextView[3];
	private Animation[] anmations = new Animation[3];
	private Queue<String> pendingMsgQueue = new LinkedList<String>(); 

	public MessageMarqueeLinearLayout(Context context) {
		super(context);
		init();
	}

	public MessageMarqueeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MessageMarqueeLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		messagesText[0] = new TextView(getContext());
		LinearLayout.LayoutParams ll0 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll0.topMargin = 35;
		this.addView(messagesText[0], ll0);

		messagesText[1] = new TextView(getContext());
		LinearLayout.LayoutParams ll1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll1.topMargin = 15;
		this.addView(messagesText[1], ll1);

		messagesText[2] = new TextView(getContext());
		LinearLayout.LayoutParams ll2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll2.topMargin = 15;
		this.addView(messagesText[2], ll2);

	}

	public void addMessageString(String msg) {
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

	@Override
	public boolean performClick() {
		return false;
	}
	
	
	
	private boolean firePendingMsg() {
		String pendingMsg = pendingMsgQueue.poll();
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
	

}
