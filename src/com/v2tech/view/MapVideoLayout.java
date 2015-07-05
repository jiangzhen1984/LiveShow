package com.v2tech.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.widget.CameraShape;
import com.v2tech.widget.CircleViewPager;
import com.v2tech.widget.LoopViewPager;
import com.v2tech.widget.VideoShowFragment;

public class MapVideoLayout extends FrameLayout implements OnTouchListener,
LoopViewPager.OnPageChangeListener, VideoCommentsAPI {

	private static final boolean DEBUG = false;
	private static final String TAG = "MapVideoLayout";
	
	private int mMinimumFlingVelocity;
	private int mMaximumFlingVelocity;
	private int mDefaultVelocity = 40;
	private int mTouchSlop;
	

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LoopViewPager mVideoShowPager;
	private VideoShowFragmentAdapter mViewPagerAdapter;
	private CameraShape mNotificaionShare;

	private LayoutPositionChangedListener mPosInterface;
	private VelocityTracker mVelocityTracker;
	private int mOffsetTop;
	private DragDirection mDragDir = DragDirection.NONE;
	private boolean fireFlyingdown = false;
	

	
	private OnVideoFragmentChangedListener mVideoChangedListener;

	private LinearLayout mMsgLayout;

	private final ArrayList<View> mMatchParentChildren = new ArrayList<View>(1);
	private boolean mMeasureAllChildren = false;

	public MapVideoLayout(Context context) {
		super(context);
		init();
	}

	public MapVideoLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MapVideoLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// setOnTouchListener(this);
		mVideoShowPager = new LoopViewPager(getContext());
		mVideoShowPager.setId(0x10000001);
		mVideoShowPager.setOnPageChangeListener(this);
		mViewPagerAdapter = new VideoShowFragmentAdapter(
				((FragmentActivity) getContext()).getSupportFragmentManager(),
				6);
		mVideoShowPager.setOffscreenPageLimit(6);
		mVideoShowPager.setAdapter(mViewPagerAdapter);

		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(false);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(false);
		mMapView = new MapView(getContext(), mapOptions);

		mBaiduMap = mMapView.getMap();

		mMsgLayout = new LinearLayout(getContext());
		mMsgLayout.setOrientation(LinearLayout.VERTICAL);
		mMsgLayout.setBackgroundColor(Color.TRANSPARENT);
		
		
		mNotificaionShare = new CameraShape(getContext());
		mNotificaionShare.updatePrecent(0.0F);
		mNotificaionShare.setVisibility(View.GONE);
		
		this.addView(mVideoShowPager);
		this.addView(mMapView);
		this.addView(mMsgLayout);
		this.addView(mNotificaionShare);
		this.bringChildToFront(mMsgLayout);

		mMsgLayout.setOnTouchListener(this);
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mTouchSlop = configuration.getScaledTouchSlop();

	}

	public BaiduMap getMap() {
		return this.mBaiduMap;
	}

	public MapView getMapView() {
		return this.mMapView;
	}

	@Override
	public void addNewMessage(String str) {
		final TextView tv = new TextView(getContext());
		tv.setText(str);
		tv.setTextColor(Color.WHITE);
		mMsgLayout.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		TranslateAnimation ani = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 1.0F, TranslateAnimation.RELATIVE_TO_PARENT,
				-1.0F, TranslateAnimation.ABSOLUTE, 1.0F,
				TranslateAnimation.ABSOLUTE, 1.0F);
		 ani.setFillAfter(true);
		 ani.setDuration(13000);
		 ani.setAnimationListener(new AnimationListener () {

			@Override
			public void onAnimationEnd(Animation animation) {
				mMsgLayout.removeView(tv);
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			 
		 });
		 tv.startAnimation(ani);
	}
	

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (mVideoChangedListener != null) {
			mVideoChangedListener
					.onChanged((VideoShowFragment) mViewPagerAdapter
							.getItem(position));
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public void setPosInterface(LayoutPositionChangedListener posInterface) {
		this.mPosInterface = posInterface;
	}

	public void setVideoChangedListener(
			OnVideoFragmentChangedListener videoChangedListener) {
		this.mVideoChangedListener = videoChangedListener;
	}

	public VideoShowFragment getCurrentVideoFragment() {
		return (VideoShowFragment) mViewPagerAdapter
				.getItem(this.mVideoShowPager.getCurrentItem());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	public interface OnVideoFragmentChangedListener {
		public void onChanged(VideoShowFragment videoFrag);
	}

	public interface LayoutPositionChangedListener {
		public void onFlyingIn();

		public void onFlyingOut();
	}

	private float mInitX;
	private float mInitY;
	private float mLastY;
	private float mLastX;

	public void requestUpFlying() {
		Flying fl = new Flying();
		fl.startFlying(-mDefaultVelocity);
		postOnAnimation(fl);
	}

	public void updateOffset(int offset) {
		if (mOffsetTop + offset < 0) {
			mOffsetTop = 0;
		} else {
			mOffsetTop += offset;
		}

		
		if (mOffsetTop > mTouchSlop) {
			float cent = Math.abs(mOffsetTop - mTouchSlop) / 4;
			mNotificaionShare.updatePrecent(cent);
			if (cent > 100.0F) {
				fireFlyingdown = true;
			} else {
				fireFlyingdown = false;
			}
		} else {
			mNotificaionShare.updatePrecent(0.0F);
			fireFlyingdown = false;
		}
		requestLayout();
	}

	public void udpateCover(Bitmap bm) {
	}

	public void pauseDrawState(boolean flag) {
		if (flag) {
			((VideoOpt) mViewPagerAdapter.getItem(mVideoShowPager
					.getCurrentItem())).pause();
			mMapView.onPause();
		} else {
			((VideoOpt) mViewPagerAdapter.getItem(mVideoShowPager
					.getCurrentItem())).resume();
			mMapView.onResume();
			bringChildToFront(mMapView);
		}
	}

	
	int mActivePointerId = -1;
	int mCurrentPage = -1;
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		int action = ev.getAction();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mVideoShowPager.beginFakeDrag();
			mCurrentPage = mVideoShowPager.getCurrentItem();

			mInitX = ev.getRawX();
			mInitY = ev.getRawY();
			mLastX = mInitX;
			mLastY = mInitY;
			
			//Pause
			((VideoShowFragment)mViewPagerAdapter.getItem(mCurrentPage)).pause();
			break;
		case MotionEvent.ACTION_MOVE:
			mMapView.clearFocus();
			float offsetX = ev.getRawX() - mInitX;
			float offsetY = ev.getRawY() - mInitY;
			float dy =  ev.getRawY() - mLastY;
			float dx =  ev.getRawX() - mLastX;
			if (DEBUG) {
				V2Log.d(TAG, " y:"+ ev.getRawY()+"  "+"  " + dx + "    " + dy + "   " + mDragDir);
			}
			if (mDragDir == DragDirection.NONE) {
				if (Math.abs(dx) > Math.abs(dy) && Math.abs(offsetX) > mTouchSlop) {
					mDragDir = DragDirection.HORIZONTAL;
				} else if (Math.abs(offsetY) > mTouchSlop){
					mDragDir = DragDirection.VERTICAL;
					pauseDrawState(true);
					mNotificaionShare.setVisibility(View.VISIBLE);
					mNotificaionShare.bringToFront();
				}
			}

			if (mDragDir == DragDirection.VERTICAL) {
				updateOffset((int) dy);
			} else if (mDragDir == DragDirection.HORIZONTAL) {
				mVideoShowPager.fakeDragBy(dx);
			}

			mLastX = ev.getRawX();
			mLastY = ev.getRawY();

			break;
		case MotionEvent.ACTION_UP:
            // A fling must travel the minimum tap distance
            final VelocityTracker velocityTracker = mVelocityTracker;
            final int pointerId = ev.getPointerId(0);
            velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
            final float velocityX = velocityTracker.getXVelocity(pointerId);

          
			if (mDragDir == DragDirection.VERTICAL) {
				Flying fl = new Flying();
				if (fireFlyingdown) {
					fl.startFlying(mDefaultVelocity);
				} else {
					fl.startFlying(-mDefaultVelocity);
				}
				postOnAnimation(fl);
			} else if (mDragDir == DragDirection.HORIZONTAL) {
				mVideoShowPager.endFakeDrag();
				int cpage = mVideoShowPager.getCurrentItem();
				if (Math.abs(velocityX) > mMinimumFlingVelocity) {
					if (DEBUG) {
						V2Log.d(TAG, " do X fling :"+ velocityX+"  cpage:" + cpage+"   downPage:"+mCurrentPage);
					}
					if (mCurrentPage == cpage) {
						if (velocityX > 0) {
							mVideoShowPager.setCurrentItem(cpage - 1, true);
						} else {
							mVideoShowPager.setCurrentItem(cpage + 1, true);
						}
					}
				}
				
			}
			
			mDragDir = DragDirection.NONE;
			
			mVelocityTracker.clear();
			break;
		}

		return true;
	}

	class Flying implements Runnable {

		int initVelocity;

		public void startFlying(int initVelocity) {
			this.initVelocity = initVelocity;
		}

		@Override
		public void run() {
			if (DEBUG) {
				V2Log.d(TAG, "[FLYING] : " + mOffsetTop + "   " + initVelocity
						+ "   " + getBottom());
			}
			if (mOffsetTop <= 0 && initVelocity < 0) {
				mOffsetTop = 0;
				updateOffset(mOffsetTop);
				if (mPosInterface != null) {
					mPosInterface.onFlyingIn();
				}
				
				mNotificaionShare.setVisibility(View.GONE);
				fireFlyingdown = false;
				return;
			}

			if (mOffsetTop > getBottom() && initVelocity > 0) {
				if (mPosInterface != null) {
					mPosInterface.onFlyingOut();
				}
				fireFlyingdown = false;
				mNotificaionShare.setVisibility(View.GONE);
				return;
			}
			if (initVelocity > 0) {
				initVelocity += 35;
			} else {
				initVelocity -= 35;
			}
			updateOffset(initVelocity);

			postOnAnimationDelayed(this, 35);
		}

	};

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (DEBUG) {
			V2Log.d("bottom:" + bottom+"  "+ mVideoShowPager.getMeasuredHeight()+"  "+ mVideoShowPager.getHeight());
		}
		mVideoShowPager.layout(left, top + mOffsetTop, right, (bottom
				+ mOffsetTop - top) / 2);
		
		if (mNotificaionShare.getVisibility() == View.VISIBLE) {
			mNotificaionShare.layout(left, mTouchSlop, right, 400);
		}

		mMsgLayout.layout(left, top + mOffsetTop, right,
				(bottom + mOffsetTop - top) / 2);
		mMapView.layout(left, (bottom + mOffsetTop - top) / 2, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int count = getChildCount();

		final boolean measureMatchParentChildren = MeasureSpec
				.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
				|| MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
		mMatchParentChildren.clear();

		int maxWidth = 0;
		int childState = 0;
		int maxHeight = resolveSizeAndState(0, heightMeasureSpec, childState);
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (mMeasureAllChildren || child.getVisibility() != GONE) {
				measureChildWithMargins(child, widthMeasureSpec, 0,
						heightMeasureSpec, maxHeight / 2);
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				maxWidth = Math.max(maxWidth, child.getMeasuredWidth()
						+ lp.leftMargin + lp.rightMargin);
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight()
						+ lp.topMargin + lp.bottomMargin);
				childState = combineMeasuredStates(childState,
						child.getMeasuredState());
				if (measureMatchParentChildren) {
					if (lp.width == LayoutParams.MATCH_PARENT
							|| lp.height == LayoutParams.MATCH_PARENT) {
						mMatchParentChildren.add(child);
					}
				}
			}
		}

		// Check against our minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		// Check against our foreground's minimum height and width
		final Drawable drawable = getForeground();
		if (drawable != null) {
			maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
			maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
		}

		setMeasuredDimension(
				resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
				resolveSizeAndState(maxHeight, heightMeasureSpec,
						childState << MEASURED_HEIGHT_STATE_SHIFT));

		count = mMatchParentChildren.size();
		if (count > 1) {
			for (int i = 0; i < count; i++) {
				final View child = mMatchParentChildren.get(i);

				final MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();
				int childWidthMeasureSpec;
				int childHeightMeasureSpec;

				if (lp.width == LayoutParams.MATCH_PARENT) {
					childWidthMeasureSpec = MeasureSpec
							.makeMeasureSpec(getMeasuredWidth() - lp.leftMargin
									- lp.rightMargin, MeasureSpec.EXACTLY);
				} else {
					childWidthMeasureSpec = getChildMeasureSpec(
							widthMeasureSpec, lp.leftMargin + lp.rightMargin,
							lp.width);
				}

				if (lp.height == LayoutParams.MATCH_PARENT) {
					childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
							getMeasuredHeight() - lp.topMargin
									- lp.bottomMargin, MeasureSpec.EXACTLY);
				} else {
					childHeightMeasureSpec = getChildMeasureSpec(
							heightMeasureSpec, lp.topMargin + lp.bottomMargin,
							lp.height);
				}

				child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			}
		}

	}

	enum DragDirection {
		NONE, VERTICAL, HORIZONTAL;
	}

}
