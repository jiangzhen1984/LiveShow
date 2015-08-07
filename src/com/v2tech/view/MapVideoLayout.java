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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.widget.CameraShape;
import com.v2tech.widget.CircleViewPager;
import com.v2tech.widget.MessageMarqueeLinearLayout;
import com.v2tech.widget.VideoShowFragment;
import com.v2tech.widget.VideoShowFragmentAdapter;

public class MapVideoLayout extends FrameLayout implements OnTouchListener,
CircleViewPager.OnPageChangeListener, VideoControllerAPI {

	private static final boolean DEBUG = false;
	private static final String TAG = "MapVideoLayout";
	
	private int mMinimumFlingVelocity;
	private int mMaximumFlingVelocity;
	private int mDefaultVelocity = 40;
	private int mTouchSlop;
	private int mCameraShapeSLop;
	private static final int CAMEA_SHAPE_HEIGHT = 300;
	

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private CircleViewPager mVideoShowPager;
	private VideoShowFragmentAdapter mViewPagerAdapter;
	private CameraShape mNotificaionShare;
	private MessageMarqueeLinearLayout mMsgLayout;
	private RelativeLayout mDragLayout;
	
	
	private LayoutPositionChangedListener mPosInterface;
	private VelocityTracker mVelocityTracker;
	private int mOffsetTop;
	private DragDirection mDragDir = DragDirection.NONE;
	private boolean fireFlyingdown = false;
	

	
	private OnVideoFragmentChangedListener mVideoChangedListener;


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
		mVideoShowPager = new CircleViewPager(getContext());
		mVideoShowPager.setId(0x10000001);
		mVideoShowPager.setOnPageChangeListener(this);
		mViewPagerAdapter = new VideoShowFragmentAdapter(
				((FragmentActivity) getContext()).getSupportFragmentManager(),
				6);
		mVideoShowPager.setOffscreenPageLimit(6);
		mVideoShowPager.setAdapter(mViewPagerAdapter);
		mVideoShowPager.setCurrentItem(2, false);

		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(true);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(getContext(), mapOptions);

		mBaiduMap = mMapView.getMap();

		mMsgLayout = new MessageMarqueeLinearLayout(getContext());
		mMsgLayout.setOrientation(LinearLayout.VERTICAL);
		mMsgLayout.setBackgroundColor(Color.TRANSPARENT);
		
		
		mNotificaionShare = new CameraShape(getContext());
		mNotificaionShare.updatePrecent(0.0F);
		mNotificaionShare.setVisibility(View.GONE);
		
		
		mDragLayout = new RelativeLayout(getContext());
		
		this.addView(mVideoShowPager);
		this.addView(mMapView);
		this.addView(mMsgLayout);
		this.addView(mNotificaionShare);
		this.addView(mDragLayout);
		this.bringChildToFront(mDragLayout);

		mDragLayout.setOnTouchListener(this);
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mTouchSlop = configuration.getScaledTouchSlop();
		mCameraShapeSLop = mTouchSlop * 3;


		initIcons();
	}
	
	
	
	private void initIcons() {
		ImageView closeButton = new ImageView(this.getContext());
		closeButton.setPadding(10, 10, 10, 10);
		closeButton.setImageResource(R.drawable.video_close_button);
		RelativeLayout.LayoutParams closeButtonLayout = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		closeButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		closeButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		mDragLayout.addView(closeButton, closeButtonLayout);
		
		
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO check if current live can not remove should pop up Message
				//FIXME close all video and release all resources
				mViewPagerAdapter.removeItem(mVideoShowPager.getCurrentItem());
				mViewPagerAdapter.notifyDataSetChanged();
				if (mViewPagerAdapter.getCount() <= 0) {
					return;
				}
				//Notify parent to update item
				if (mVideoChangedListener != null) {
					mVideoChangedListener
							.onChanged((VideoShowFragment) mViewPagerAdapter
									.getItem(mVideoShowPager.getCurrentItem()));
				}
			}
			
		});
		
		
		ImageView favButton = new ImageView(this.getContext());
		favButton.setPadding(10, 10, 10, 10);
		favButton.setImageResource(R.drawable.fav_button_selector);
		RelativeLayout.LayoutParams favButtonLayout = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		favButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		favButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		mDragLayout.addView(favButton, favButtonLayout);
		
		
	}

	public BaiduMap getMap() {
		return this.mBaiduMap;
	}

	public MapView getMapView() {
		return this.mMapView;
	}

	@Override
	public void addNewMessage(String str) {
		mMsgLayout.addMessageString(str);
	}
	
	
	
	public VideoOpt addNewVideoWindow(final Live l) {
		final VideoShowFragment videoFragment = (VideoShowFragment)mViewPagerAdapter.createFragment();
		videoFragment.setStateListener(new VideoShowFragment.VideoFragmentStateListener() {

			@Override
			public void onInited() {
				videoFragment.play(l);
				
			}

			@Override
			public void onUnInited() {
				
			}
			
		});
		mVideoShowPager.setCurrentItem(mViewPagerAdapter.getCount() - 1 , false);
		mViewPagerAdapter.notifyDataSetChanged();

		return videoFragment;
	}
	
	
	public int getVideoWindowNums() {
		return mViewPagerAdapter.getCount();
	}
	

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (DEBUG) {
			V2Log.i(TAG, " change new position:" + position);
		}
		if (mVideoChangedListener != null) {
			mVideoChangedListener
					.onChanged((VideoShowFragment) mViewPagerAdapter
							.getItem(position));
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
	
	public void onPagePreapredRemove(int item) {
		mViewPagerAdapter.removeItem(item);
		mViewPagerAdapter.notifyDataSetChanged();
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

		
		if (mOffsetTop > mCameraShapeSLop) {
			float cent = Math.abs(mOffsetTop - mCameraShapeSLop) / 2;
			mNotificaionShare.updatePrecent(cent >= 100.0F? 100.0F: cent);
			if (cent >= 100.0F) {
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
		//TODO add cover
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
				V2Log.d(TAG, " y:"+ ev.getRawY()+"  "+"  " + dx + "    " + dy + "   " + mDragDir +"  "+offsetY +"  ====" +mCameraShapeSLop);
			}
			if (mDragDir == DragDirection.NONE) {
				if (Math.abs(dx) > Math.abs(dy) && Math.abs(offsetX) > mTouchSlop) {
					mDragDir = DragDirection.HORIZONTAL;
				} else if (Math.abs(offsetY) > mTouchSlop){
					mDragDir = DragDirection.VERTICAL;
					pauseDrawState(true);
				}
			}

			if (mDragDir == DragDirection.VERTICAL) {
				updateOffset((int) dy);
				if (mNotificaionShare.getVisibility() == View.GONE 
						&& Math.abs(offsetY) > mCameraShapeSLop) {
					mNotificaionShare.setVisibility(View.VISIBLE);
					mNotificaionShare.bringToFront();
				}
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
			V2Log.d("changed:"+changed+"  bottom:" + bottom+"  "+ mVideoShowPager.getMeasuredHeight()+"  "+ mVideoShowPager.getHeight()+"  top:" + top);
		}
		int realTop = top + mOffsetTop;
		int realBottom = (bottom
				+ mOffsetTop - top) / 2;
		mVideoShowPager.layout(left, realTop, right, realBottom);
		
		if (mNotificaionShare.getVisibility() == View.VISIBLE) {
			int dis = (mOffsetTop > CAMEA_SHAPE_HEIGHT?((mOffsetTop - CAMEA_SHAPE_HEIGHT) / 5):0);
			mNotificaionShare.layout(left, mTouchSlop + dis, right, CAMEA_SHAPE_HEIGHT + dis);
		}

		mMsgLayout.layout(left, realTop, right, realBottom);
		mMapView.layout(left, (bottom + mOffsetTop - top) / 2, right, bottom);
		mDragLayout.layout(left, realTop, right, realBottom);
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
