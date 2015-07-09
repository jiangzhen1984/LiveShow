package com.v2tech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.V2.jni.util.V2Log;
import com.v2tech.v2liveshow.R;

public class BottomButtonLayout extends FrameLayout {
	
	private final boolean DEBUG = true;
	private static final String TAG = "BottomButtonLayout";
	
	public static final int MAP_BUTTON = 1;
	public static final int WORD_BUTTON = 2;
	
	private FrameLayout mRoot;
	private View mWordButton;
	private View mMapButton;
	private EditText mTextView;
	
	private static int DEFAULT_VELOCITY = 35;
	private static int MARGIN = 20;
	private static int MARGIN_BOTTOM = 10;
	
	private ButtonState mButtonState = ButtonState.INIT;
	
	private int mTouchSlop;
	private int mTapTimeout;
	private int mMaximumFlingVelocity;
	private int mMinimumFlingVelocity;
	private Fling mFling;
	private VelocityTracker mVelocityTracker;
	
	private boolean firstInitLayout;
	
	private ButtonClickedListener mButtonListener;

	public BottomButtonLayout(Context context) {
		super(context);
		init();
	}

	public BottomButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BottomButtonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mRoot = (FrameLayout)inflater.inflate(R.layout.bottom_layout, null);
		mWordButton = mRoot.findViewById(R.id.msg_button);
		mMapButton = mRoot.findViewById(R.id.map_button);
		
		
		mWordButton.setOnTouchListener(mButtonTouchListener);
		mMapButton.setOnTouchListener(mButtonTouchListener);
		
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = 25; //configuration.getScaledTouchSlop();
		mTapTimeout = ViewConfiguration.getTapTimeout();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mMinimumFlingVelocity = (int)(configuration.getScaledMinimumFlingVelocity() * 0.5);
		
		
		mTextView = new EditText(getContext());
		mTextView.setLines(1);
		mTextView.setBackgroundResource(R.drawable.input_bg);
		
		this.addView(mRoot, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	}
	
	
	private void prepareTextView() {
		int width = mRoot.getWidth() - mWordButton.getWidth() - mMapButton.getWidth();
		FrameLayout.LayoutParams rl = (FrameLayout.LayoutParams)mTextView.getLayoutParams();
		int leftMargin = -1;
		if (currentOptView == mWordButton) {
			mWordButton.bringToFront();
			leftMargin = mWordButton.getLeft() - width - MARGIN;
		} else if (currentOptView == mMapButton) {
			mMapButton.bringToFront();
			leftMargin = mMapButton.getRight() + MARGIN;
		} else {
			throw new RuntimeException("unknow layout");
		}
		
		if (rl == null) {
			rl = new FrameLayout.LayoutParams(width, mWordButton.getMeasuredHeight());
			rl.leftMargin = leftMargin;
			mRoot.addView(mTextView, rl);
		} else {
			rl.leftMargin = leftMargin;
			mTextView.setLayoutParams(rl);
		}
		
		
		mRoot.requestLayout();

	}
	

	
	private void requestScroll(int disX) {
		if (currentOptView == mWordButton) {
			FrameLayout.LayoutParams rlWord = (FrameLayout.LayoutParams)mWordButton.getLayoutParams();
			rlWord.leftMargin += disX;
			mWordButton.setLayoutParams(rlWord);
		}
		
		if (currentOptView == mMapButton) {
			FrameLayout.LayoutParams rlMap = (FrameLayout.LayoutParams)mMapButton.getLayoutParams();
			rlMap.leftMargin += disX;
			mMapButton.setLayoutParams(rlMap);
		}
		
		FrameLayout.LayoutParams rlText = (FrameLayout.LayoutParams)mTextView.getLayoutParams();
		rlText.leftMargin += disX;
		mTextView.setLayoutParams(rlText);
		mRoot.requestLayout();
	}
	
	
	
	
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mMapButton.getMeasuredWidth() <= 0 || mMapButton.getMeasuredHeight() <= 0) {
			mMapButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		}
		if (mWordButton.getMeasuredWidth() <= 0 || mMapButton.getMeasuredHeight() <= 0) {
			mWordButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		}
		if (!firstInitLayout || changed) {
			FrameLayout.LayoutParams  flWord = (FrameLayout.LayoutParams)mWordButton.getLayoutParams();
			flWord.leftMargin = MARGIN;
			mWordButton.setLayoutParams(flWord);
			
			FrameLayout.LayoutParams  fl = (FrameLayout.LayoutParams)mMapButton.getLayoutParams();
			fl.leftMargin = r - mWordButton.getMeasuredWidth() - MARGIN;
			mMapButton.setLayoutParams(fl);
			
			firstInitLayout = true;
		}
		
		super.onLayout(changed, l, t, r, b - MARGIN_BOTTOM *5);
	}




	private float mInitX;
	private float mLastX;
	
	
	private View currentOptView;
	private OnTouchListener mButtonTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);
			
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN :
				mInitX = event.getRawX();
				mLastX = mInitX;
				currentOptView = v;
				prepareTextView();
//				if (mButtonState == ButtonState.INIT) {
//					
//					prepareTextView();
//				}
				postDelayed(mButtonTapTimeOutRunnable, mTapTimeout);
				break;
			case MotionEvent.ACTION_MOVE:
				float dx = event.getRawX() - mLastX;
				if (DEBUG) {
					V2Log.d(TAG, "distance:"+ (event.getRawX() - mInitX) +"   mTouchSlop:"+mTouchSlop);
				}
				if (Math.abs(event.getRawX() - mInitX) > mTouchSlop) {
					mButtonState = ButtonState.DRAGING;
				}
				
				removeCallbacks(mButtonTapTimeOutRunnable);
				
				requestScroll((int)dx);
				mLastX = event.getRawX();
				break;
			case MotionEvent.ACTION_UP :
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
				int velocityX = (int)mVelocityTracker.getXVelocity();
				if (DEBUG) {
					V2Log.d("dis:" + Math.abs(event.getRawX() - mInitX)+"   "+ mTouchSlop+"   mMinimumFlingVelocity:"+ mMinimumFlingVelocity+"  velocityX:"+velocityX+"  mButtonState:"+mButtonState);
				}
				if ((mMinimumFlingVelocity <= velocityX) || mButtonState == ButtonState.DRAGING) {
					 if (Math.abs(event.getRawX() - mInitX) < mTouchSlop) {
						 velocityX = -velocityX;
					 }
					if (mFling == null) {
						mFling = new Fling();
					}
					mFling.startFling(velocityX > 0 ? DEFAULT_VELOCITY : -DEFAULT_VELOCITY);
				} else if (mButtonState == ButtonState.PREPARED){
					if (mButtonListener != null) {
						mButtonListener.onButtonClicked(v, mTextView, currentOptView ==  mMapButton? MAP_BUTTON : WORD_BUTTON);
					}
				}
				mVelocityTracker.clear();
				
				break;
			}
			
			return true;
		}
		
	};
	
	
	private Runnable mButtonTapTimeOutRunnable = new Runnable() {

		@Override
		public void run() {
		}
		
	};
	
	
	
	
	class Fling implements Runnable {
		
		private int initVelocity;

		
		public void startFling(int initVelocity) {
			this.initVelocity = initVelocity;
			postOnAnimation(this);
		}
		
		@Override
		public void run() {
			if (initVelocity > 0) {
				int r  = currentOptView.getRight();
				if (r < mRoot.getWidth() - MARGIN) {
					if (r + initVelocity > mRoot.getWidth() - MARGIN) {
						initVelocity = mRoot.getWidth() - MARGIN - r;
					}
					requestScroll(initVelocity);

					postOnAnimation(this);
					return;
				}
			} else {
				int l = currentOptView.getLeft();
				if (l > MARGIN) {
					if (l + initVelocity < MARGIN) {
						initVelocity = - (l - MARGIN);
					}
					requestScroll(initVelocity);
					postOnAnimation(this);
					return;
				}
			}
			
			
			if (currentOptView == mWordButton) {
				if (currentOptView.getLeft() == MARGIN) {
					mButtonState = ButtonState.INIT;
				} else {
					mButtonState = ButtonState.PREPARED;
				}
			}
			
			if (currentOptView == mMapButton) {
				if (currentOptView.getLeft() == MARGIN) {
					mButtonState = ButtonState.PREPARED;
				} else {
					mButtonState = ButtonState.INIT;
				}
			}

			currentOptView = null;
			
		}

	}
	
	
	
	
	
	public void setButtonListener(ButtonClickedListener buttonListener) {
		this.mButtonListener = buttonListener;
	}

	public interface ButtonClickedListener {
		/**
		 * 
		 * @param v
		 * @param et
		 * @param flag
		 */
		public void onButtonClicked(View v, EditText et, int flag);
	}
	
	enum ButtonState {
		INIT, DRAGING, PREPARED,
	}

}
