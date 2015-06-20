package com.v2tech.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;

public class MapVideoLayout extends FrameLayout  implements OnTouchListener, OnPageChangeListener {
	
	private static final boolean DEBUG = true;

	private View mDragView;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ViewPager mVideoShowPager;
	private VideoShowFragmentAdapter mViewPagerAdapter;
	private ImageView mTestImage;

	private LayoutPositionChangedListener mPosInterface;
	private VelocityTracker mVelocityTracker;
	private int mOffsetTop;
	private DragDirection mDragDir = DragDirection.NONE;
	private int mDefaultVelocity = 120;
	
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
		//setOnTouchListener(this);
		mVideoShowPager = new ViewPager(getContext());
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
		
		mDragView = new ImageView(getContext());
		mTestImage= new ImageView(getContext());
		
		this.addView(mVideoShowPager);
		this.addView(mTestImage);
		this.addView(mMapView);
		this.addView(mDragView);
		this.bringChildToFront(mDragView);
		
		mDragView.setOnTouchListener(this);

	}

	public BaiduMap getMap() {
		return this.mBaiduMap;
	}

	public MapView getMapView() {
		return this.mMapView;
	}
	
	
	
	
	

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		if (mVideoChangedListener != null) {
			mVideoChangedListener.onChanged((VideoShowFragment)mViewPagerAdapter.getItem(position));
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
		return (VideoShowFragment)mViewPagerAdapter.getItem(this.mVideoShowPager.getCurrentItem());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mVelocityTracker = VelocityTracker.obtain();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVelocityTracker.recycle();
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
		post(fl);
	}
	
	public void updateOffset(int offset) {
		if (mOffsetTop + offset < 0) {
			mOffsetTop = 0;
		} else {
			mOffsetTop += offset;
		}
		requestLayout();
		//offsetTopAndBottom(mOffsetTop);
	}
	
	public void udpateCover(Bitmap bm) {
		mTestImage.setScaleType(ScaleType.CENTER_CROP);
		mTestImage.setImageBitmap(bm);
	}
	
	public void updateCoverState(boolean flag) {
		if (flag) {
			mMapView.onPause();
			this.bringChildToFront(mTestImage);
//			mMapView.setVisibility(View.GONE);
			//removeView(mMapView);
		} else {
		//	addView(mMapView);
//			mMapView.setVisibility(View.VISIBLE);
			mMapView.onResume();
			bringChildToFront(mMapView);
		}
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		int action = ev.getAction();
		mVelocityTracker.addMovement(ev);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mVideoShowPager.beginFakeDrag();
			
			mInitX = ev.getX();
			mInitY = ev.getY();
			mLastX = mInitX;
			mLastY = mInitY;
			
			break;
		case MotionEvent.ACTION_MOVE:
			mMapView.clearFocus();
			float dy = ev.getY() - mLastY;
			float dx = ev.getX() - mLastX;
			if (DEBUG) {
				V2Log.d(dx+"    "+ dy+"   "+ mDragDir);
			}
			if (mDragDir == DragDirection.NONE) {
				if (Math.abs(dx)  > Math.abs(dy)) {
					mDragDir = DragDirection.HORIZONTAL;
				} else {
					mDragDir = DragDirection.VERTICAL;
					updateCoverState(true);
				}
			} 
			
			if (mDragDir == DragDirection.VERTICAL) {
				updateOffset((int)dy);
			} else if (mDragDir == DragDirection.HORIZONTAL) {
				mVideoShowPager.fakeDragBy(dx);
			}
			
			mLastX = ev.getX();
			mLastY = ev.getY();
			
			break;
		case MotionEvent.ACTION_UP:
			if (mDragDir == DragDirection.VERTICAL) {
				Flying fl = new Flying();
				fl.startFlying(mDefaultVelocity);
				post(fl);
			} else if (mDragDir == DragDirection.HORIZONTAL) {
				mVideoShowPager.endFakeDrag();
			}
			mDragDir = DragDirection.NONE;
			break;
		}

		return true;
	}
	
	
	
	class Flying implements  Runnable {
		
		int initVelocity;
		
		public void startFlying(int initVelocity) {
			this.initVelocity = initVelocity;
		}
		
		@Override
		public void run() {
			if (DEBUG) {
				V2Log.d("[FLYING] : " + mOffsetTop+"   " + initVelocity+"   "+mOrigBottom);
			}
			mMapView.clearFocus();
			if (mOffsetTop <= 0) {
				mOffsetTop = 0;
				updateOffset(mOffsetTop);
				if (mPosInterface != null) {
					mPosInterface.onFlyingIn();
				}
				updateCoverState(false);
				return;
			}
			
			if (mOffsetTop > mOrigBottom) {
				if (mPosInterface != null) {
					mPosInterface.onFlyingOut();
				}
				updateCoverState(false);
				return;
			}
			if (initVelocity > 0) {
				initVelocity += 15;
			} else {
				initVelocity -= 15;
			}
			updateOffset(initVelocity);
			post(this);
		}
		
	};
	
	private int mOrigBottom;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mOrigBottom = bottom;
		mVideoShowPager.layout(left, top + mOffsetTop, right, (bottom
				+ mOffsetTop - top) / 2);
		mDragView.layout(left, top + mOffsetTop, right, (bottom
				+ mOffsetTop - top) / 2);
		mMapView.layout(left, (bottom + mOffsetTop - top) / 2, right, bottom);
		mTestImage.layout(left, (bottom + mOffsetTop - top) / 2, right, bottom);
		
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
