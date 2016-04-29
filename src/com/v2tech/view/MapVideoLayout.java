package com.v2tech.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.presenter.LiverAction;
import com.v2tech.v2liveshow.R;
import com.v2tech.vo.Live;
import com.v2tech.vo.User;
import com.v2tech.widget.CameraShape;
import com.v2tech.widget.CircleViewPager;
import com.v2tech.widget.LiverInteractionLayout;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MessageMarqueeLinearLayout;
import com.v2tech.widget.P2PAudioWatcherLayout;
import com.v2tech.widget.P2PAudioWatcherLayout.P2PAudioWatcherLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout;
import com.v2tech.widget.RequestConnectLayout;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.VideoShowFragment;
import com.v2tech.widget.VideoShowFragmentAdapter;

public class MapVideoLayout extends FrameLayout implements OnTouchListener,
CircleViewPager.OnPageChangeListener, VideoControllerAPI, View.OnClickListener {
	
	
	private static final int ANIMATION_TYPE_IN = 1;
	private static final int ANIMATION_TYPE_OUT = 2;
	//from down to up  for in and from up to down for out
	private static final int ANIMATION_TYPE_CATEGORY = 1;
	
	private static final int ANIMATION_DURATION = 1000;
	

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
	private LiverInteractionLayout lierInteractionLayout;
	private RequestConnectLayout   requestConnectLayout;
	private P2PVideoMainLayout p2pVideoLayout;
	private P2PAudioWatcherLayout p2pAudioWatcherLayout;
	
	
	private LayoutPositionChangedListener mPosInterface;
	private VelocityTracker mVelocityTracker;
	
	//calculate sum when user move down circle view pager
	private int mOffsetTop;
	
	//calculate sum when user move up circle view pager
	private int removedOffset = 0;
	private DragDirection mDragDir = DragDirection.NONE;
	private DragType mDragType = DragType.NONE;
	private Operation mOper = Operation.NONE;
	private boolean fireFlyingdown = false;
	private ImageView favButton;
	private ImageView inchargeButton;
	private LinearLayout notificationLayout;
	private TextView inchargeView;
	private TextView recdCountView;
	private ImageView marqueeEnableBtn;
	
	private OnNotificationClickedListener mNotificationClickedListener;
	private OnVideoFragmentChangedListener mVideoChangedListener;
	private List<NotificationWrapper> notificationList;

	


	private final ArrayList<View> mMatchParentChildren = new ArrayList<View>(1);
	private boolean mMeasureAllChildren = false;
	
	private LiverAction liverAction;

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
		mVideoShowPager.setBackgroundColor(Color.BLACK);
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
		mapOptions.zoomControlsEnabled(false);
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
		
		lierInteractionLayout = (LiverInteractionLayout)LayoutInflater.from(getContext()).inflate(R.layout.liver_interaction_layout, null);
		lierInteractionLayout.showInnerBox(false);
		lierInteractionLayout.setVisibility(View.GONE);
		
		
		requestConnectLayout = (RequestConnectLayout)LayoutInflater.from(getContext()).inflate(R.layout.requesting_connect_layout, null);
		requestConnectLayout.setVisibility(View.GONE);
		
		p2pVideoLayout= (P2PVideoMainLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_video_main_layout, null);
		p2pVideoLayout.setVisibility(View.GONE);
		
		p2pAudioWatcherLayout= (P2PAudioWatcherLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_audio_watcher_layout, null);
		p2pAudioWatcherLayout.setVisibility(View.GONE);
		
		
		this.addView(mVideoShowPager);
		this.addView(mMapView);
		this.addView(lierInteractionLayout);
		this.addView(requestConnectLayout);
		this.addView(p2pVideoLayout);
		this.addView(p2pAudioWatcherLayout);
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
		notificationList = new ArrayList<NotificationWrapper>();
	}
	
	
	
	
	
	private void initIcons() {
		
		View rightSideLayout = LayoutInflater.from(getContext()).inflate(R.layout.video_right_border_layout, null);
		favButton = (ImageView) rightSideLayout
				.findViewById(R.id.recommendation_button);
		inchargeButton = (ImageView) rightSideLayout
				.findViewById(R.id.incharge_button);

		recdCountView = (TextView) rightSideLayout
				.findViewById(R.id.recommendation_count_tv);
		inchargeView = (TextView) rightSideLayout
				.findViewById(R.id.balance_count_tv);
		marqueeEnableBtn = (ImageView) rightSideLayout
				.findViewById(R.id.message_marquee_btn);
		
		

		RelativeLayout.LayoutParams rightSideLayoutParm = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rightSideLayoutParm.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		
		mDragLayout.addView(rightSideLayout, rightSideLayoutParm);
		
		
		
		View bottomLayout = LayoutInflater.from(getContext()).inflate(R.layout.video_layout_bottom_layout, null);
		
		View publisherButton = bottomLayout.findViewById(R.id.liver_ly);
		RelativeLayout.LayoutParams bottomLayoutParm = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		bottomLayoutParm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		mDragLayout.addView(bottomLayout, bottomLayoutParm);
		
		marqueeEnableBtn.setOnClickListener(this);
		favButton.setOnClickListener(this);
		inchargeButton.setOnClickListener(this);
		publisherButton.setOnClickListener(this);
		
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
				//videoFragment.play(l);
				
			}

			@Override
			public void onUnInited() {
				
			}
			
		});
		
		mViewPagerAdapter.notifyDataSetChanged();
		mVideoShowPager.setCurrentItem(mViewPagerAdapter.getCount() - 1 , false);

		return null;
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
		if (state == CircleViewPager.SCROLL_STATE_IDLE) {
			pasuseCurrentVideo(false);
		}
	}
	
	public void onPagePreapredRemove(int item) {
		//Can not remove last one
		if (mViewPagerAdapter.getCount() <= 1) {
			mVideoShowPager.requestLayout();
			return;
		}
		//reset mOffsetTop for layout. because baidu map always request layout
		mOffsetTop = 0;
		mViewPagerAdapter.removeItem(item);
		mViewPagerAdapter.notifyDataSetChanged();
		
//		//Notify parent to update item
		if (mVideoChangedListener != null) {
			mVideoChangedListener
					.onChanged((VideoShowFragment) mViewPagerAdapter
							.getItem(mVideoShowPager.getCurrentItem()));
		}
	}

	public void setPosInterface(LayoutPositionChangedListener posInterface) {
		this.mPosInterface = posInterface;
	}

	public void setVideoChangedListener(
			OnVideoFragmentChangedListener videoChangedListener) {
		this.mVideoChangedListener = videoChangedListener;
	}
	
	public void setNotificationClickedListener(OnNotificationClickedListener listener) {
		this.mNotificationClickedListener = listener;
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
		public void onPreparedFlyingIn();
		
		public void onFlyingIn();

		public void onPreparedFlyingOut();
		
		public void onFlyingOut();
		
		public void onDrag();
	}

	
	public interface OnNotificationClickedListener {
		public void onNotificationClicked(View v, Live live, User u);
	}
	
	
	
	
	private float mInitX;
	private float mInitY;
	private float mLastY;
	private float mLastX;
	private float mAbsDisX;
	private float mAbsDisY;

	public void requestUpFlying() {
		mDragType = DragType.RESTORE;
		Flying fl = new Flying();
		fl.startFlying(-mDefaultVelocity);
	}
	
	
	public void updateDragType(DragType dragType) {
		mDragType = dragType;
	}
	
	
	public DragType determinteDragType(int disY, int offsetY, DragDirection dir) {
		int ret = (int)mAbsDisY + offsetY;
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
			mVideoShowPager.fakeDragUpBy(offset);
		} else if (mDragType == DragType.NONE) {
			return;
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
	
	
	public void removeLiveNotificaiton(Live l) {
		if (l == null || l.getPublisher() == null) {
			return;
		}
		for (NotificationWrapper wr: notificationList) {
			if (wr.u.getmUserId() == l.getPublisher().getmUserId()) {
				removeLiveNotificaiton(wr);
				break;
			}
		}
	}
	
	
	public void updateRendNum(int num) {
		recdCountView.setText(num+"");
	}
	
	public void updateWatcherNum(int num) {
		recdCountView.setText(num+"");
	}
	
	public void updateBalanceSum(float num) {
		inchargeView.setText(num+"");
	}
	
    public void showRedBtm(boolean flag) {
		if (flag) {
			favButton.setImageResource(R.drawable.unrecommendation_button);
		} else {
			favButton.setImageResource(R.drawable.recommendation_button);
		}
	}
	
	public void showIncharBtm(boolean flag) {
		if (flag) {
			inchargeButton.setImageResource(R.drawable.unfollow_button);
		} else {
			inchargeButton.setImageResource(R.drawable.follow_button);
		}
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
	}
	
	
	private void showOrHidenViewAnimation(View view, boolean flag) {
		if (flag && view.getVisibility() == View.GONE) {
			this.mMapView.onPause();
			view.setVisibility(View.VISIBLE);
			view.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_IN,
					ANIMATION_DURATION, true));
		} else if (!flag  && view.getVisibility() == View.VISIBLE)  {
			view.setVisibility(View.GONE);
			view.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_OUT,
					ANIMATION_DURATION, true));
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
	
	public void showMarqueeMessage(boolean flag) {
		mMsgLayout.setVisibility(flag?View.VISIBLE:View.GONE);
		marqueeEnableBtn.setImageResource(flag?R.drawable.message_marquee_enable : R.drawable.message_marquee_disable);
	}
	
	
	private void removeLiveNotificaiton(NotificationWrapper wr) {
		notificationList.remove(wr);
		updateNotificationLayout(wr.v, 0);
	}
	
	
	private void updateNotificationLayout(View v, int type) {
		if (type == 1) {
			notificationLayout.addView(v, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			v.setOnClickListener(mLocalNotificationClickListener);
		} else {
			notificationLayout.removeView(v);	
		}
	}
	
	
	private OnClickListener mLocalNotificationClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mNotificationClickedListener != null) {
				NotificationWrapper wrapper = (NotificationWrapper)v.getTag();
				mNotificationClickedListener.onNotificationClicked(v, wrapper.live, wrapper.u);
			}
		
		}
		
	};
	
	

	
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
		removedOffset = 0;
		mOffsetTop = 0;
		mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
		mVideoShowPager.beginFakeDrag();
		mCurrentPage = mVideoShowPager.getCurrentItem();

		mInitX = ev.getRawX();
		mInitY = ev.getRawY();
		mLastX = mInitX;
		mLastY = mInitY;
		
		pasuseCurrentVideo(true);
		mOper = Operation.PRESS;
	}
	
	private void doTouchMove(MotionEvent ev) {
		mMapView.clearFocus();
		mOper = Operation.DRAGING;
		float offsetX = ev.getRawX() - mInitX;
		float offsetY = ev.getRawY() - mInitY;
		float dy =  ev.getRawY() - mLastY;
		float dx =  ev.getRawX() - mLastX;
		
		
		if (mDragDir == DragDirection.NONE) {
			if (Math.abs(dx) > Math.abs(dy) && Math.abs(offsetX) > mTouchSlop) {
				mDragDir = DragDirection.HORIZONTAL;
			} else if (Math.abs(offsetY) > mTouchSlop){
				mDragDir = DragDirection.VERTICAL;
			}
		}

		if (mDragDir == DragDirection.VERTICAL) {
			
			DragType newType = determinteDragType((int)offsetY, (int)dy, mDragDir);
			if (DEBUG) {
				V2Log.d("determinteDragType new type:" + newType +"   removedOffset:"+ removedOffset+"   mOffsetTop:"+mOffsetTop);
			}
			if (mDragType == DragType.NONE) {
				mDragType = newType;
				updateOffset((int) dy);
			} else if (mDragType != newType) {
				if (mDragType == DragType.REMOVE) {
					int dis1 = (int)(Math.abs(dy) - Math.abs(offsetY));
					//restore removed offset
					updateOffset(-removedOffset);
					mDragType = newType;
					//update new offset for share
					updateOffset((int)Math.abs(offsetY) - dis1);
					if (DEBUG) {
						V2Log.d("old remove  dis1:" + dis1+"   "+-(Math.abs(offsetY) - dis1)+"   abs offset:" +Math.abs(offsetY));
					}
				} else if (mDragType == DragType.SHARE) {
					int dis1 = (int)(Math.abs(dy) - Math.abs(offsetY));
					updateOffset(-mOffsetTop);
					mDragType = newType;
					updateOffset(-(int)Math.abs(offsetY) - dis1);
					if (DEBUG) {
						V2Log.d("old share dis1:" + dis1+"   "+-(Math.abs(offsetY) - dis1)+"   abs offset:" +Math.abs(offsetY));
					}
				}
			} else {
				updateOffset((int) dy);
			}
			if (mPosInterface != null && mDragType == DragType.SHARE) {
				mPosInterface.onDrag();
			}
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
		
		mAbsDisX = mLastX- mInitX;
		mAbsDisY = mLastY- mInitY;

	}
	
	private void doTouchUp(MotionEvent ev) {
		mOper = Operation.NONE;
        // A fling must travel the minimum tap distance
        final VelocityTracker velocityTracker = mVelocityTracker;
        final int pointerId = ev.getPointerId(0);
        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        final float velocityX = velocityTracker.getXVelocity(pointerId);

      
		if (mDragDir == DragDirection.VERTICAL) {
			if (mDragType == DragType.SHARE) {
				Flying fl = new Flying();
				if (fireFlyingdown) {
					if (mPosInterface != null) {
						mPosInterface.onPreparedFlyingOut();
					}
					fl.startFlying(mDefaultVelocity);
				} else {
					fl.startFlying(-mDefaultVelocity);
				}
				
			} else if (mDragType == DragType.REMOVE) {
				mVideoShowPager.endFakeDrag();
			}
		} else if (mDragDir == DragDirection.HORIZONTAL) {
			mVideoShowPager.endFakeDrag();
		} else {
			pasuseCurrentVideo(false);
		}
		
		mDragDir = DragDirection.NONE;
		
		mVelocityTracker.clear();
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
				
				mNotificaionShare.setVisibility(View.GONE);
				fireFlyingdown = false;
				mDragType = DragType.NONE;
				return;
			}

			if (mOffsetTop > getBottom() && initVelocity > 0) {
				if (mPosInterface != null) {
					mPosInterface.onFlyingOut();
				}
				fireFlyingdown = false;
				mNotificaionShare.setVisibility(View.GONE);
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
	
	
	

	public LiverAction getLiverAction() {
		return liverAction;
	}

	public void setLiverAction(LiverAction liverAction) {
		this.liverAction = liverAction;
	}
	
	
	
	

	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.liver_ly:
			liverAction.onLiverButtonClicked();
			break;
		case R.id.recommendation_button:
			liverAction.onRemButtonClicked();
			break;
		case R.id.incharge_button:
			liverAction.onInchargeButtonClicked();
			break;
		case R.id.message_marquee_btn:
			liverAction.onMarqueeBtnClicked(v);
			break;
		}
		
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		if (DEBUG) {
			V2Log.d(TAG, "changed:" + changed + "  bottom:" + bottom + "  "
					+ mVideoShowPager.getMeasuredHeight() + "  "
					+ mVideoShowPager.getMeasuredWidth() + "  top:" + top
					+ "   mOffsetTop:" + mOffsetTop);
		}
		int realTop = top + mOffsetTop;
		int realBottom = realTop + mVideoShowPager.getMeasuredHeight();
		mVideoShowPager.layout(left, realTop, right, realBottom);
		
		if (mNotificaionShare.getVisibility() == View.VISIBLE) {
			int dis = (mOffsetTop > CAMEA_SHAPE_HEIGHT?((mOffsetTop - CAMEA_SHAPE_HEIGHT) / 5):0);
			mNotificaionShare.layout(left, mTouchSlop + dis, right, CAMEA_SHAPE_HEIGHT + dis);
		}

		mMsgLayout.layout(left, realTop, right, realBottom);
		mMapView.layout(left, realTop + mVideoShowPager.getMeasuredHeight(), right, bottom + mOffsetTop);
		mDragLayout.layout(left, realTop, right, realBottom);
		if (lierInteractionLayout.getVisibility() == View.VISIBLE) {
			lierInteractionLayout.layout(left, realTop + mVideoShowPager.getMeasuredHeight(), right, bottom + mOffsetTop);
		}
		if (requestConnectLayout.getVisibility() == View.VISIBLE) {
			requestConnectLayout.layout(left, realTop + mVideoShowPager.getMeasuredHeight(), right, bottom + mOffsetTop);
		}
		if (p2pVideoLayout.getVisibility() == View.VISIBLE) {
			p2pVideoLayout.layout(left, realTop + mVideoShowPager.getMeasuredHeight(), right, bottom + mOffsetTop);
		}
		if (p2pAudioWatcherLayout.getVisibility() == View.VISIBLE) {
			p2pAudioWatcherLayout.layout(left, realTop + mVideoShowPager.getMeasuredHeight(), right, bottom + mOffsetTop);
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
