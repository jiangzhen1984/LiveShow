package com.v2tech.view;

import java.util.ArrayList;

import v2av.VideoPlayer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.map.MapAPI;
import com.v2tech.map.baidu.BaiduMapImpl;
import com.v2tech.v2liveshow.R;
import com.v2tech.video.VideoController;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.vo.Watcher;
import com.v2tech.widget.BountyMarkerWidget;
import com.v2tech.widget.CircleViewPager;
import com.v2tech.widget.LiveInformationLayout;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.LiverInteractionLayout;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MessageMarqueeLinearLayout;
import com.v2tech.widget.MessageMarqueeLinearLayout.MessageMarqueeLayoutListener;
import com.v2tech.widget.P2PAudioWatcherLayout;
import com.v2tech.widget.P2PAudioWatcherLayout.P2PAudioWatcherLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout;
import com.v2tech.widget.P2PVideoMainLayout.P2PVideoMainLayoutListener;
import com.v2tech.widget.RequestConnectLayout;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.TouchSurfaceView;
import com.v2tech.widget.VideoShowFragment;
import com.v2tech.widget.VideoWatcherListLayout;
import com.v2tech.widget.VideoWatcherListLayout.VideoWatcherListLayoutListener;

public class MapVideoLayout extends FrameLayout implements OnTouchListener,
CircleViewPager.OnPageChangeListener, VideoControllerAPI{
	
	
	private static final int ANIMATION_TYPE_IN = 1;
	private static final int ANIMATION_TYPE_OUT = 2;
	//from down to up  for in and from up to down for out
	private static final int ANIMATION_TYPE_CATEGORY = 1;
	
	private static final int ANIMATION_DURATION = 1000;
	

	private static final boolean DEBUG = true;
	private static final String TAG = "MapVideoLayout";
	
	private int mMaximumFlingVelocity;
	private int mDefaultVelocity = 40;
	private int mTouchSlop;
	private int mCameraShapeSLop;
	private int mTouchTapTimeout;
	

	private VideoPlayer videoController;
	private MapView mMapView;
	//private CircleViewPager mVideoShowPager;
	//private PagerAdapter mViewPagerAdapter;
	private TouchSurfaceView tsv;
	private MessageMarqueeLinearLayout mMsgLayout;
	private RelativeLayout mDragLayout;
	private LiverInteractionLayout lierInteractionLayout;
	private RequestConnectLayout   requestConnectLayout;
	private P2PVideoMainLayout p2pVideoLayout;
	private P2PAudioWatcherLayout p2pAudioWatcherLayout;
	private LiveInformationLayout  liveInformationLayout;
	private VideoWatcherListLayout liveWatcherLayout;
	
	private BountyMarkerWidget bountyMarker;
	
	private LayoutPositionChangedListener mPosInterface;
	private VelocityTracker mVelocityTracker;
	
	//calculate sum when user move down circle view pager
	private int mOffsetTop;
	
	//calculate sum when user move up circle view pager
	private int removedOffset = 0;
	private DragDirection mDragDir = DragDirection.NONE;
	private DragType mDragType = DragType.NONE;
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
//		mVideoShowPager = new CircleViewPager(getContext());
//		mVideoShowPager.setId(0x10000001);
//		mVideoShowPager.setOnPageChangeListener(this);
//		mViewPagerAdapter = new SurfaceViewAdapter(
//				getContext(),
//				6);
//		mVideoShowPager.setOffscreenPageLimit(6);
//		mVideoShowPager.setAdapter(mViewPagerAdapter);
//		mVideoShowPager.setCurrentItem(2, false);
		
		tsv =  new TouchSurfaceView(getContext()); 
		tsv.setZOrderOnTop(true);
		tsv.setZOrderMediaOverlay(true);
		tsv.getHolder().setFormat(PixelFormat.TRANSPARENT);
		videoController = tsv.getPlayerController();

		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(getContext(), mapOptions);
//
		
		mMsgLayout = (MessageMarqueeLinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.message_marquee_layout, (ViewGroup)null);
		
		
		mDragLayout = new RelativeLayout(getContext());
		mDragLayout.setOnTouchListener(this);
		
		lierInteractionLayout = (LiverInteractionLayout)LayoutInflater.from(getContext()).inflate(R.layout.liver_interaction_layout, (ViewGroup)null);
		lierInteractionLayout.showInnerBox(false);
		lierInteractionLayout.setVisibility(View.GONE);
		
		
		requestConnectLayout = (RequestConnectLayout)LayoutInflater.from(getContext()).inflate(R.layout.requesting_connect_layout, (ViewGroup)null);
		requestConnectLayout.setVisibility(View.GONE);
		
		p2pVideoLayout= (P2PVideoMainLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_video_main_layout, (ViewGroup)null);
		p2pVideoLayout.setVisibility(View.GONE);
		
		p2pAudioWatcherLayout= (P2PAudioWatcherLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_audio_watcher_layout, (ViewGroup)null);
		p2pAudioWatcherLayout.setVisibility(View.GONE);
		
		
		liveInformationLayout = (LiveInformationLayout)LayoutInflater.from(getContext()).inflate(R.layout.video_right_border_layout, (ViewGroup)null);
		liveWatcherLayout	 = (VideoWatcherListLayout)LayoutInflater.from(getContext()).inflate(R.layout.video_layout_bottom_layout, (ViewGroup)null);
		
		bountyMarker = (BountyMarkerWidget)LayoutInflater.from(getContext()).inflate(R.layout.bounty_marker_layout, (ViewGroup)null);
		
		this.addView(tsv, -1, generateDefaultLayoutParams());
		this.addView(mDragLayout, -1, generateDefaultLayoutParams());
		this.addView(mMapView, -1, generateDefaultLayoutParams());
		this.addView(lierInteractionLayout, -1, generateDefaultLayoutParams());
		this.addView(p2pVideoLayout, -1, generateDefaultLayoutParams());
		this.addView(p2pAudioWatcherLayout, -1, generateDefaultLayoutParams());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = 10;
		this.addView(mMsgLayout, -1, lp);
		this.addView(liveInformationLayout, -1,  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		this.addView(liveWatcherLayout, -1,  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(requestConnectLayout, -1, generateDefaultLayoutParams());
		this.addView(bountyMarker, -1,  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mTouchSlop = configuration.getScaledTouchSlop();
		mTouchTapTimeout = ViewConfiguration.getTapTimeout();
		mCameraShapeSLop = mTouchSlop * 3;
	}
	
	
	
	
	


	public MapAPI getMap() {
		return new BaiduMapImpl(mMapView.getMap(), mMapView);
	}

	public MapView getMapView() {
		return this.mMapView;
	}

	
	public void addNewMessage(CharSequence msg) {
		mMsgLayout.addMessageString(msg);
	}
	
	
	
	public VideoOpt addNewVideoWindow(final Live l) {
		return null;
	}
	
	
	public int getVideoWindowNums() {
		//return mViewPagerAdapter.getCount();
		return 1;
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
//			mVideoChangedListener
//					.onChanged((VideoShowFragment) mViewPagerAdapter
//							.getItem(position));
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == CircleViewPager.SCROLL_STATE_IDLE) {
			pasuseCurrentVideo(false);
		}
	}
	
	public void onPagePreapredRemove(int item) {
		//Can not remove last one
//		if (mViewPagerAdapter.getCount() <= 1) {
//			mVideoShowPager.requestLayout();
//			return;
//		}
		//reset mOffsetTop for layout. because baidu map always request layout
		mOffsetTop = 0;
	//	mViewPagerAdapter.removeItem(item);
	//	mViewPagerAdapter.notifyDataSetChanged();
		
//		//Notify parent to update item
		if (mVideoChangedListener != null) {
//			mVideoChangedListener
//					.onChanged((VideoShowFragment) mViewPagerAdapter
//							.getItem(mVideoShowPager.getCurrentItem()));
		}
	}

	public void setPosInterface(LayoutPositionChangedListener posInterface) {
		this.mPosInterface = posInterface;
	}

	public void setVideoChangedListener(
			OnVideoFragmentChangedListener videoChangedListener) {
		this.mVideoChangedListener = videoChangedListener;
	}
	

	public VideoController getCurrentVideoController() {
		return new VideoController() {

			@Override
			public View getVideoView() {
				//return (SurfaceView)((SurfaceViewAdapter)mViewPagerAdapter).getItem(mVideoShowPager.getCurrentItem());
				return tsv;
			}
			
		};
	
//		return (VideoShowFragment) mViewPagerAdapter
//				.getItem(this.mVideoShowPager.getCurrentItem());
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
		public void onPreparedFlyingIn();
		
		public void onFlyingIn();

		public void onPreparedFlyingOut();
		
		public void onFlyingOut();
		
		public void onDrag();
		
		public void onVideoScreenClick();
	}

	
	private int mInitX;
	private int mInitY;
	private int mLastY;
	private int mLastX;
	private int mAbsDisX;
	private int mAbsDisY;

	public void requestUpFlying() {
		mDragType = DragType.RESTORE;
		Flying fl = new Flying();
		fl.startFlying(-mDefaultVelocity);
	}
	
	
	public void updateDragType(DragType dragType) {
		mDragType = dragType;
	}
	
	
	public DragType determinteDragType(int disY, int offsetY, DragDirection dir) {
		int ret = mAbsDisY + offsetY;
		if (ret < 0) {
			return DragType.REMOVE;
		}  else {
			return DragType.SHARE;
		}
	}
	

	
	public void updateOffset(int offset) {
		if (mDragType == DragType.SHARE || mDragType == DragType.RESTORE) {
			if (mOffsetTop + offset < 0) {
				mOffsetTop = 0;
			} else {
				mOffsetTop += offset;
			}
		} else if (mDragType == DragType.REMOVE) {
			removedOffset += offset;
			//mVideoShowPager.fakeDragUpBy(offset);
		} else if (mDragType == DragType.NONE) {
			return;
		}
		
		if (mOffsetTop > mCameraShapeSLop) {
			float cent = Math.abs(mOffsetTop - mCameraShapeSLop) / 2;
			if (cent >= 100.0F) {
				fireFlyingdown = true;
			} else {
				fireFlyingdown = false;
			}
		} else {
			fireFlyingdown = false; 
		}
		
		//mMapView.offsetTopAndBottom(offset);
		//mVideoShowPager.offsetTopAndBottom(offset);
		requestLayout();
	}

	public void udpateCover(Bitmap bm) {
		//TODO add cover
	}

	public void pauseDrawState(boolean flag) {
		pasuseCurrentVideo(flag);
		if (flag) {
			mMapView.onPause();
		} else {
			mMapView.onResume();
			bringChildToFront(mMapView);
		}
	}
	
	
	
	public void pasuseCurrentVideo(boolean flag) {

	}
	
	
	
	
	public void updateRendNum(int num) {
		liveInformationLayout.updateRecommands(num+"");
	}
	
	public void updateWatcherNum(int num) {
		
	}
	
	public void updateBalanceSum(float num) {
		liveInformationLayout.updateTips(num + "");
	}
	
    public void showRedBtm(boolean flag) {
		//TODO add implments
	}
	
	public void showIncharBtm(boolean flag) {
		//TODO add implments
	}
	
	public void showVideoBtnLy(boolean flag) {
		liveInformationLayout.setVisibility(flag? View.VISIBLE:View.GONE);
	}
	public void showVideoWatcherListLy(boolean flag) {
		liveWatcherLayout.setVisibility(flag? View.VISIBLE:View.GONE);
	}
	
	
	
	public void showLiverInteractionLy(boolean flag) {
		showOrHidenViewAnimation(lierInteractionLayout, flag);
	}
	
	
	public void showRequestingConnectionLy(boolean flag) {
		showOrHidenViewAnimation(requestConnectLayout, flag);
	}
	
	
	public void showP2PAudioWatcherLy(boolean flag) {
		showOrHidenViewAnimation(p2pAudioWatcherLayout, flag);
	}
	
	public void showP2PVideoLayout(boolean flag) {
		showOrHidenViewAnimation(p2pVideoLayout, flag);
		p2pVideoLayout.bringToFront();
	}
	
	
	private void showOrHidenViewAnimation(View view, boolean flag) {
		if (flag && view.getVisibility() == View.GONE) {
			this.mMapView.onPause();
			view.setVisibility(View.VISIBLE);
			view.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_IN,
					ANIMATION_DURATION, true));
		} else if (!flag  && view.getVisibility() == View.VISIBLE)  {
			view.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_OUT,
					ANIMATION_DURATION, true));
			view.setVisibility(View.GONE);
			this.mMapView.onResume();
		}
	}
	
	private Animation getBoxAnimation(int cate, int type, int duration, boolean fillAfter) {
		Animation tabBlockHolderAnimation = null;
		
		if (type == ANIMATION_TYPE_OUT) {
			tabBlockHolderAnimation = AnimationUtils.loadAnimation(getContext(),
					R.animator.liver_interaction_from_up_to_down_out);
		} else if (type == ANIMATION_TYPE_IN) {
			tabBlockHolderAnimation =  AnimationUtils.loadAnimation(getContext(),
					R.animator.liver_interaction_from_down_to_up_in);
		}
		tabBlockHolderAnimation.setDuration(duration);
		tabBlockHolderAnimation.setFillAfter(fillAfter);
		tabBlockHolderAnimation.setZAdjustment(Animation.ZORDER_TOP);

		return tabBlockHolderAnimation;
		
	}
	
	
	public void updateFollowBtnImageResource(int res) {
		lierInteractionLayout.updateFollowBtnImageResource(res);
	}

	public void updateFollowBtnTextResource(int res) {
		lierInteractionLayout.updateFollowBtnTextResource(res);
	}

	
	
	
	public SurfaceView getP2PWatcherSurfaceView() {
		return p2pVideoLayout.getSurfaceView();
	}
	
	
	public void setRequestConnectLayoutListener(RequestConnectLayoutListener listener)  {
		this.requestConnectLayout.setListener(listener);
	}
	
	
	public void setP2PAudioWatcherLayoutListener(P2PAudioWatcherLayoutListener listener)  {
		this.p2pAudioWatcherLayout.setOutListener(listener);
	}
	
	public void setInterfactionBtnClickListener(InterfactionBtnClickListener listener)  {
		this.lierInteractionLayout.setOutListener(listener);
	}
	
	public void setP2PVideoMainLayoutListener(P2PVideoMainLayoutListener listener) {
		this.p2pVideoLayout.setListener(listener);
	}
	
	public void setMessageMarqueeLayoutListener(MessageMarqueeLayoutListener listener) {
		this.mMsgLayout.setListener(listener);
	}
	
	public void showMarqueeMessageLayout(boolean flag) {
		mMsgLayout.setVisibility(flag? View.VISIBLE : View.GONE);
	}
	public void showMarqueeMessage(boolean flag) {
		mMsgLayout.updateMessageShow(flag);
	}
	
	
	public void setLiveInformationLayoutListener(LiveInformationLayoutListener listener) {
		this.liveInformationLayout.setListener(listener);
	}
	
	public void setVideoWatcherListLayoutListener(VideoWatcherListLayoutListener listener) {
		this.liveWatcherLayout.setListener(listener);
	}
	
	
	public void addWatcher(Watcher watcher) {
		liveWatcherLayout.addWatcher(watcher);
	}
	
	public void removeWatcher(Watcher watcher) {
		liveWatcherLayout.removeWatcher(watcher);
	}
	
	
	public VideoPlayer getVideoPlayer() {
		return tsv.getPlayerController();
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
			doTouchDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			doTouchMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			doTouchUp(ev);
			break;
		}

		return true;
	}
	
	
	private void doTouchDown(MotionEvent ev) {
		mMapView.onPause();
		layoutOffsetY = 0;
		removedOffset = 0;
		mOffsetTop = 0;
		mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
		//mVideoShowPager.beginFakeDrag();
	///	mCurrentPage = mVideoShowPager.getCurrentItem();

		mInitX = (int)ev.getRawX();
		mInitY = (int)ev.getRawY();
		mLastX = mInitX;
		mLastY = mInitY;
		
		pasuseCurrentVideo(true);
		videoController.startTranslate();
	}
	
	int layoutOffsetY = 0;
	
	private void doTouchMove(MotionEvent ev) {
		int rawX = (int)ev.getRawX();
		int rawY = (int)ev.getRawY();
		int offsetX = rawX - mInitX;
		int offsetY = rawY - mInitY;
		int dy =  rawY - mLastY;
		int dx =  rawX - mLastX;
		int absOffsetY = Math.abs(offsetY);
		int absDy = Math.abs(dy);
		
		
		if (mDragDir == DragDirection.NONE) {
			if (Math.abs(dx) > absDy && Math.abs(offsetX) > mTouchSlop) {
				mDragDir = DragDirection.HORIZONTAL;
			} else if (absOffsetY > mTouchSlop){
				mDragDir = DragDirection.VERTICAL;
			}
		}

		if (mDragDir == DragDirection.VERTICAL) {
			
			DragType newType = determinteDragType(offsetY, dy, mDragDir);
			if (DEBUG) {
				V2Log.d("determinteDragType new type:" + newType +"   removedOffset:"+ removedOffset+"   mOffsetTop:"+mOffsetTop +"  layoutOffsetY:"+ layoutOffsetY);
			}
			if (mDragType == DragType.NONE) {
				mDragType = newType;
				updateOffset(dy);
			} else if (mDragType != newType) {
				if (mDragType == DragType.REMOVE) {
//					layoutOffsetY = offsetY;
//					requestLayout();
					
					
//					int dis1 = (int)(absDy - absOffsetY);
//					//restore removed offset
//					//updateOffset(-removedOffset);
//					mDragType = newType;
//					//update new offset for share
//					updateOffset((int)absOffsetY - dis1);
//					if (DEBUG) {
//						V2Log.d("old remove  dis1:" + dis1+"   "+-(absOffsetY - dis1)+"   abs offset:" +absOffsetY);
//					}
				} else if (mDragType == DragType.SHARE) {
					int dis1 = (absDy - absOffsetY);
					updateOffset(-mOffsetTop);
					mDragType = newType;
					updateOffset(-absOffsetY - dis1);
					if (DEBUG) {
						V2Log.d("old share dis1:" + dis1+"   "+-(absOffsetY - dis1)+"   abs offset:" +absOffsetY);
					}
				}
			} else {
				if (mDragType == DragType.REMOVE) {
//					layoutOffsetY = offsetY;
//					requestLayout();
				} else {
					updateOffset(dy);
				}
			}
			if (mPosInterface != null && mDragType == DragType.SHARE) {
				mPosInterface.onDrag();
			}
			
			
		} else if (mDragDir == DragDirection.HORIZONTAL) {
			//mVideoShowPager.fakeDragBy(dx);
			//TODO translate
			V2Log.i("=== >  " + offsetX);
			videoController.translate((float)offsetX /tsv.getMeasuredWidth(), 0);
		}

		mLastX = rawX;
		mLastY = rawY;
		
		mAbsDisX = mLastX- mInitX;
		mAbsDisY = mLastY- mInitY;

	}
	
	private void doTouchUp(MotionEvent ev) {
		mLastX = (int)ev.getRawX();
		mLastY = (int)ev.getRawY();
		
		mAbsDisX = mLastX- mInitX;
		mAbsDisY = mLastY- mInitY;
		
        // A fling must travel the minimum tap distance
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        boolean tap = false;
        if (mTouchTapTimeout > (ev.getEventTime() - ev.getDownTime())) {
        	tap = true;
        }
      
        if (tap && Math.abs(mAbsDisX) < mTouchSlop && Math.abs(mAbsDisY) < mTouchSlop) {
        		doVideoScreenTap();
        } else {
			if (mDragDir == DragDirection.VERTICAL) {
				if (mDragType == DragType.SHARE) {
					Flying fl = new Flying();
					if (fireFlyingdown && mPosInterface != null) {
						mPosInterface.onPreparedFlyingOut();
					} 
					
					fl.startFlying(fireFlyingdown ? mDefaultVelocity : -mDefaultVelocity);
					
				} else if (mDragType == DragType.REMOVE) {
				//	mVideoShowPager.endFakeDrag();
				}
			} else if (mDragDir == DragDirection.HORIZONTAL) {
				//mVideoShowPager.endFakeDrag();
				float cent = (float)mAbsDisX /tsv.getMeasuredWidth();
				new FlyingX().startFlying(cent, Math.abs(mAbsDisX));
				
				
			} else {
				pasuseCurrentVideo(false);
			}
        }
		
		mDragDir = DragDirection.NONE;
		
		mVelocityTracker.clear();
		mMapView.onResume();
		
	}
	
	
	private void doVideoScreenTap() {
		if (mPosInterface != null) {
			mPosInterface.onVideoScreenClick();
		}
	}
	
	
	

	class Flying implements Runnable {

		int initVelocity;

		public void startFlying(int initVelocity) {
			this.initVelocity = initVelocity;
			postOnAnimation(this);
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
				
				fireFlyingdown = false;
				mDragType = DragType.NONE;
				return;
			}

			if (mOffsetTop > getBottom() && initVelocity > 0) {
				if (mPosInterface != null) {
					mPosInterface.onFlyingOut();
				}
				fireFlyingdown = false;
				mDragType = DragType.NONE;
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
	
	
	class FlyingX implements Runnable {

		int initVelocity;
		int distance;
		float cent;

		public void startFlying(float cent, int offset) {
			this.cent = cent;
			if (this.cent > 0.2F) {
				distance = tsv.getMeasuredWidth() - offset;
				initVelocity = -95;
			} else if (this.cent < - 0.2F) {
				distance = tsv.getMeasuredWidth() - offset;
				initVelocity = 95;
			}
			postOnAnimation(this);
		}

		@Override
		public void run() {
			if (distance > 0) {
				if (distance - initVelocity < 0) {
					initVelocity = distance;
				}

				videoController.translate(
						(float) distance / (float) tsv.getMeasuredWidth(), 0F);
				distance -= initVelocity;

				postOnAnimationDelayed(this, 15);
			} else if (distance < 0) {
			} else {
				videoController.finishTranslate();
			}
		}

	};
	
	


	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		
		int realTop = top + mOffsetTop  + layoutOffsetY;
		int bottomChildTop = realTop + tsv.getMeasuredHeight();
//		int bottomChildTop = realTop + mVideoShowPager.getMeasuredHeight();
//		mVideoShowPager.layout(left, realTop, right, bottomChildTop);
//		
//		if (DEBUG) {
//			V2Log.d(TAG, "changed:" + changed + "  bottom:" + bottom + "  "
//					+ mVideoShowPager.getMeasuredHeight() + "  "
//					+ mVideoShowPager.getMeasuredWidth() + "  realTop:" + realTop
//					+ "   realBottom:" + bottomChildTop +"  layoutOffsetY:"+layoutOffsetY);
//		}

		tsv.layout(left, realTop, right, bottomChildTop);
		LayoutParams lp = (LayoutParams)mMsgLayout.getLayoutParams();
		mMsgLayout.layout(left, realTop + lp.topMargin, right, realTop + mMsgLayout.getMeasuredHeight()+ lp.topMargin);
		mMapView.layout(left, bottomChildTop, right, bottom );
		
		int bw = bountyMarker.getMeasuredWidth();
		int bh = bountyMarker.getMeasuredHeight();
		int bl = left + (right - left - bw) / 2;
		int br = bl + bw;
		int bto = mMapView.getTop() + (mMapView.getBottom() - mMapView.getTop() ) / 2 - bh;
		int btm = bto + bh;
	//	bountyMarker.layout(bl, bto, br, btm);
		
		mDragLayout.layout(left, realTop, right, bottomChildTop);
		if (liveInformationLayout.getVisibility() == View.VISIBLE) {
			liveInformationLayout.layout(right - liveInformationLayout.getMeasuredWidth(), realTop, right, bottomChildTop);
		}
		if (liveWatcherLayout.getVisibility() == View.VISIBLE) {
			liveWatcherLayout.layout(left, bottomChildTop - liveWatcherLayout.getMeasuredHeight() , right, bottomChildTop);
		}
		if (lierInteractionLayout.getVisibility() == View.VISIBLE) {
			lierInteractionLayout.layout(left,bottomChildTop, right, bottom + mOffsetTop);
		}
		if (requestConnectLayout.getVisibility() == View.VISIBLE) {
			requestConnectLayout.layout(left, bottomChildTop, right, bottom + mOffsetTop);
		}
		if (p2pVideoLayout.getVisibility() == View.VISIBLE) {
			p2pVideoLayout.layout(left,bottomChildTop , right, bottom + mOffsetTop);
		}
		if (p2pAudioWatcherLayout.getVisibility() == View.VISIBLE) {
			p2pAudioWatcherLayout.layout(left, bottomChildTop, right, bottom + mOffsetTop);
		}
		
		
		
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
	
	
	
	class NotificationWrapper {
		Live live;
		View v;
		User u;
	}

	
	enum Operation {
		NONE, PRESS, DRAGING
	}
	enum DragDirection {
		NONE, VERTICAL, HORIZONTAL;
	}
	
	
	public enum DragType {
		NONE, SHARE, REMOVE, RESTORE
	}

}
