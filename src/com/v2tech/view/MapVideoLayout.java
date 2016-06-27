package com.v2tech.view;

import v2av.VideoPlayer;
import v2av.VideoRecorder;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.v2tech.map.MapAPI;
import com.v2tech.map.baidu.BaiduMapImpl;
import com.v2tech.v2liveshow.R;
import com.v2tech.video.SurfaceVideoController;
import com.v2tech.video.VideoController;
import com.v2tech.video.VideoShareSufaceViewCallback;
import com.v2tech.vo.Live;
import com.v2tech.vo.Watcher;
import com.v2tech.widget.BountyMarkerWidget;
import com.v2tech.widget.InquiryBidWidget;
import com.v2tech.widget.InquiryBidWidget.InquiryBidWidgetListener;
import com.v2tech.widget.LiveInformationLayout;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.LiverInteractionLayout;
import com.v2tech.widget.LiverInteractionLayout.InterfactionBtnClickListener;
import com.v2tech.widget.MapLocationTipsWidget;
import com.v2tech.widget.MessageMarqueeLinearLayout;
import com.v2tech.widget.MessageMarqueeLinearLayout.MessageMarqueeLayoutListener;
import com.v2tech.widget.P2PAudioWatcherLayout;
import com.v2tech.widget.P2PAudioWatcherLayout.P2PAudioWatcherLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout;
import com.v2tech.widget.P2PVideoMainLayout.P2PVideoMainLayoutListener;
import com.v2tech.widget.RequestConnectLayout;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.TouchSurfaceView;
import com.v2tech.widget.TouchSurfaceView.Translate;
import com.v2tech.widget.VerticalSpinWidget;
import com.v2tech.widget.VideoShareBtnLayout;
import com.v2tech.widget.VideoShareBtnLayout.VideoShareBtnLayoutListener;
import com.v2tech.widget.VideoShareRightWidget;
import com.v2tech.widget.VideoShareRightWidget.VideoShareRightWidgetListener;
import com.v2tech.widget.VideoWatcherListLayout;
import com.v2tech.widget.VideoWatcherListLayout.VideoWatcherListLayoutListener;

public class MapVideoLayout extends FrameLayout {
	
	private static int VIDEO_SURFACE_HEIGHT = 774;

	private static int FLYING_SLOP = 180;
	
	private static final boolean DEBUG = true;
	
	private int mTouchSlop;
	private int borderY;
	
	private UITypeStatusChangedListener uiTypeListener;
	
	private VideoController videoController;
	private VideoPlayer videoPlayer;
	
	private MapView mMapView;
	private TouchSurfaceView tsv;
	private TouchSurfaceView shareSurfaceView;
	private VideoShareBtnLayout videoShareBtnLayout;
	private MessageMarqueeLinearLayout mMsgLayout;
	private LiverInteractionLayout lierInteractionLayout;
	private RequestConnectLayout   requestConnectLayout;
	private P2PVideoMainLayout p2pVideoLayout;
	private P2PAudioWatcherLayout p2pAudioWatcherLayout;
	private LiveInformationLayout  liveInformationLayout;
	private VideoWatcherListLayout liveWatcherLayout;
	private InquiryBidWidget      inquiryBidWidget;
	private MapLocationTipsWidget mapLocationTipsWidget;
	private BountyMarkerWidget bountyMarker;
	private View inquiryCloseBtn;
	private View returnBtnView;
	private VideoShareRightWidget videoShareRightWidet;
	private VerticalSpinWidget volumnWidget;
	private View volumneIcon;
	
	
	private ScreenType st = ScreenType.VIDEO_MAP;
	private PostState ps = PostState.IDLE;
	private TouchState ts = TouchState.IDLE;
	private Flying fly = new Flying();


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
		videoPlayer = new VideoPlayer(6);
		tsv =  new TouchSurfaceView(getContext()); 
		tsv.setZOrderOnTop(true);
		tsv.setZOrderMediaOverlay(true);
		tsv.getHolder().setFormat(PixelFormat.TRANSPARENT);
		tsv.getHolder().addCallback(videoPlayer);
		tsv.setTranslate(touchSurfaceViewTranslate);
		
		
		shareSurfaceView = new TouchSurfaceView(getContext()); 
		shareSurfaceView.getHolder().addCallback(new VideoShareSufaceViewCallback());
		VideoRecorder.VideoPreviewSurfaceHolder = shareSurfaceView.getHolder();
		
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.compassEnabled(true);
		mapOptions.scaleControlEnabled(true);
		mapOptions.zoomControlsEnabled(false);
		mapOptions.rotateGesturesEnabled(true);
		mMapView = new MapView(getContext(), mapOptions);
		
		mMsgLayout = (MessageMarqueeLinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.message_marquee_layout, (ViewGroup)null);
		videoShareBtnLayout = (VideoShareBtnLayout)LayoutInflater.from(getContext()).inflate(R.layout.video_share_btn_layout, (ViewGroup)null);
		
		
		lierInteractionLayout = (LiverInteractionLayout)LayoutInflater.from(getContext()).inflate(R.layout.liver_interaction_layout, (ViewGroup)null);
		lierInteractionLayout.showInnerBox(false);
		
		
		requestConnectLayout = (RequestConnectLayout)LayoutInflater.from(getContext()).inflate(R.layout.requesting_connect_layout, (ViewGroup)null);
		
		p2pVideoLayout= (P2PVideoMainLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_video_main_layout, (ViewGroup)null);
		p2pVideoLayout.getSurfaceView().getHolder().addCallback(new  VideoShareSufaceViewCallback());
		
		p2pAudioWatcherLayout= (P2PAudioWatcherLayout)LayoutInflater.from(getContext()).inflate(R.layout.p2p_audio_watcher_layout, (ViewGroup)null);
		
		liveInformationLayout = (LiveInformationLayout)LayoutInflater.from(getContext()).inflate(R.layout.video_right_border_layout, (ViewGroup)null);
		liveWatcherLayout	 = (VideoWatcherListLayout)LayoutInflater.from(getContext()).inflate(R.layout.video_layout_bottom_layout, (ViewGroup)null);
		
		bountyMarker = (BountyMarkerWidget)LayoutInflater.from(getContext()).inflate(R.layout.bounty_marker_layout, (ViewGroup)null);
		inquiryBidWidget = (InquiryBidWidget)LayoutInflater.from(getContext()).inflate(R.layout.inquiry_bid_layout, (ViewGroup)null);
		mapLocationTipsWidget = (MapLocationTipsWidget)LayoutInflater.from(getContext()).inflate(R.layout.map_location_tips_widget, (ViewGroup)null);
		inquiryCloseBtn = new ImageView(getContext());
		((ImageView)inquiryCloseBtn).setImageResource(R.drawable.inquiry_close_btn);
		inquiryCloseBtn.setOnClickListener(clickListener);
		
		returnBtnView = new ImageView(getContext());
		((ImageView)returnBtnView).setImageResource(R.drawable.title_bar_return_btn);
		returnBtnView.setOnClickListener(clickListener);
		
		
		videoShareRightWidet = (VideoShareRightWidget)LayoutInflater.from(getContext()).inflate(R.layout.video_share_right_widget_layout, (ViewGroup)null);
		
		
		volumnWidget = (VerticalSpinWidget)LayoutInflater.from(getContext()).inflate(R.layout.vertical_spin_widget_layout, (ViewGroup)null);
		volumnWidget.setCent(0.5F);
		
		volumneIcon= new ImageView(getContext());
		((ImageView)volumneIcon).setImageResource(R.drawable.voice_volumn_icon);
		
		
		this.addView(shareSurfaceView, -1, new LayoutParams(LayoutParams.MATCH_PARENT, VIDEO_SURFACE_HEIGHT));
		this.addView(videoShareBtnLayout, -1, generateDefaultLayoutParams());
		
		this.addView(tsv, -1, new LayoutParams(LayoutParams.MATCH_PARENT, VIDEO_SURFACE_HEIGHT));
		this.addView(mMapView, -1, generateDefaultLayoutParams());
		this.addView(lierInteractionLayout, -1, generateDefaultLayoutParams());
		this.addView(p2pVideoLayout, -1, generateDefaultLayoutParams());
		this.addView(p2pAudioWatcherLayout, -1, generateDefaultLayoutParams());
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = 10;
		this.addView(mMsgLayout, -1, lp);
		this.addView(liveInformationLayout, -1,  new LayoutParams(LayoutParams.WRAP_CONTENT, VIDEO_SURFACE_HEIGHT ));
		this.addView(liveWatcherLayout, -1,  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(requestConnectLayout, -1, generateDefaultLayoutParams());
		this.addView(bountyMarker, -1,  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(inquiryBidWidget, -1,  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(mapLocationTipsWidget, -1,  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(inquiryCloseBtn, -1,  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = 40;
		lp.rightMargin = 40;
		this.addView(returnBtnView, -1,  lp);
		
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, VIDEO_SURFACE_HEIGHT);
		lp.topMargin = 40;
		lp.rightMargin = 40;
		lp.bottomMargin = 40;
		this.addView(videoShareRightWidet, -1,  lp);
		
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		lp.topMargin = 40;
		lp.leftMargin = 20;
		lp.bottomMargin = 40;
		this.addView(volumnWidget, -1,  lp);
		
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 40;
		this.addView(volumneIcon, -1,  lp);
		
		
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		
		videoController = new SurfaceVideoController(tsv, videoPlayer);
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
	

	public VideoController getCurrentvideoPlayer() {
		return videoController;
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
	}
	
	public void showIncharBtm(boolean flag) {
	}
	
	public void updateVideoShareBtnBackground(int res) {
		videoShareBtnLayout.updateSharedBtnBackground(res);
	}
	
	
	public void updateMapLocationAddress(String address) {
		mapLocationTipsWidget.updateMapLocationAddress(address);
	}
	
	
	public void showVideoBtnLy(boolean flag) {
		liveInformationLayout.setVisibility(flag? View.VISIBLE:View.GONE);
	}
	public void showVideoWatcherListLy(boolean flag) {
		liveWatcherLayout.setVisibility(flag? View.VISIBLE:View.GONE);
	}
	
	public void showMap(boolean flag) {
		if (st != ScreenType.VIDEO_SHARE) {
			throw new RuntimeException(" only support " + ScreenType.VIDEO_SHARE +" type but current st: " + st);
		}
		int distance = getBottom() - borderY;
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_MAP,
					ScreenType.VIDEO_SHARE_MAP, PostState.RESTORE,
					distance);
		} else {
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_MAP,
					ScreenType.VIDEO_SHARE, PostState.GO_NEXT, distance);
		}
	}
	
	public void showLiverInteractionLy(boolean flag) {
		if (st != ScreenType.VIDEO_MAP && flag) {
			throw new RuntimeException(" screen type can not support: " + st
					+ "  only support from " + ScreenType.VIDEO_MAP + " to "
					+ ScreenType.VIDEO_PUBLISHER_SHOW);
		}
		int distance = getBottom() - tsv.getMeasuredHeight();
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.VIDEO_PUBLISHER_SHOW,
					ScreenType.VIDEO_PUBLISHER_SHOW, PostState.RESTORE,
					distance);
		} else {
			turnUITypeAnimation(ScreenType.VIDEO_PUBLISHER_SHOW,
					ScreenType.VIDEO_MAP, PostState.GO_NEXT, distance);
		}
	}

	public void showRequestingConnectionLy(boolean flag) {
		int distance = getBottom() - tsv.getMeasuredHeight();
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_CONNECTION_REQUESTING,
					ScreenType.VIDEO_SHARE_CONNECTION_REQUESTING,
					PostState.RESTORE, distance);
		} else {
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_CONNECTION_REQUESTING,
					ScreenType.VIDEO_SHARE, PostState.GO_NEXT, distance);
		}
	}

	public void showP2PAudioWatcherLy(boolean flag) {
		int distance = getBottom() - tsv.getMeasuredHeight();
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.VIDEO_WATCHING_AUDIO_CONNECTION,
					ScreenType.VIDEO_WATCHING_AUDIO_CONNECTION,
					PostState.RESTORE, distance);
		} else {
			turnUITypeAnimation(ScreenType.VIDEO_WATCHING_AUDIO_CONNECTION,
					ScreenType.VIDEO_MAP, PostState.GO_NEXT, distance);
		}
	}
	
	public void showP2PVideoLayout(boolean flag) {
		int distance = getBottom() - tsv.getMeasuredHeight();
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_P2P_PUBLISHER,
					ScreenType.VIDEO_SHARE_P2P_PUBLISHER,
					PostState.RESTORE, distance);
		} else {
			turnUITypeAnimation(ScreenType.VIDEO_SHARE_P2P_PUBLISHER,
					ScreenType.VIDEO_SHARE, PostState.GO_NEXT, distance);
		}
	}
	
	
	public void showInquiryWidget(boolean flag) {
		int distance = getBottom() - tsv.getMeasuredHeight();
		if (flag) {
			// fake UI behavior for simulate touch move up.
			turnUITypeAnimation(ScreenType.INQUIRE_BIDING,
					ScreenType.INQUIRE_BIDING,
					PostState.RESTORE, distance);
		} else {
			turnUITypeAnimation(ScreenType.INQUIRE_BIDING,
					ScreenType.VIDEO_MAP, PostState.GO_NEXT, distance);
		}
	}
	
	private void turnUITypeAnimation(ScreenType currentST, ScreenType nextST, PostState nextPS, int distance) {
		st = currentST;
		ps = nextPS;
		fly.startFlying(distance, nextST);
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
	
	public String getInquiryAward() {
		return inquiryBidWidget.getTipsEditText().getEditableText().toString();
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
	
	public void setVideoShareBtnLayoutListener(VideoShareBtnLayoutListener listener) {
		this.videoShareBtnLayout.setListener(listener);
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
	
	public void setInquiryBidWidgetListener(InquiryBidWidgetListener listener) {
		this.inquiryBidWidget.setListener(listener);
	}
	
	
	public void addWatcher(Watcher watcher) {
		liveWatcherLayout.addWatcher(watcher);
	}
	
	public void removeWatcher(Watcher watcher) {
		liveWatcherLayout.removeWatcher(watcher);
	}
	
	
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
	
	
	
	
	public UITypeStatusChangedListener getUiTypeListener() {
		return uiTypeListener;
	}

	public void setUiTypeListener(UITypeStatusChangedListener uiTypeListener) {
		this.uiTypeListener = uiTypeListener;
	}


	public void setVideoShareRightWidgetListener(VideoShareRightWidgetListener listener) {
		this.videoShareRightWidet.setListener(listener);
	}


	private int mInitY;
	private int mInitX;
	private int mLastY;
	

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean flag = false;
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		int disX = Math.abs(x - mInitX);
		
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mInitY = y;
			mLastY = mInitY;
			mInitX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int yDiff = Math.abs((int)ev.getY() - mInitY);
			flag = checkTouchRectEvent(ev) && yDiff > disX && yDiff > mTouchSlop;
			break;
		case MotionEvent.ACTION_UP:
			flag = false;
			break;
		}
		return flag;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		int y = (int)ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!checkTouchRectEvent(ev)) {
				return false;
			}
			doTouchDown(ev);
			
			break;
		case MotionEvent.ACTION_MOVE:
			doTouchMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			doTouchUp(ev);
			break;
		}

		mLastY = y;
		
		return true;
	}
	
	
	private boolean checkTouchRectEvent(MotionEvent ev) {
		boolean ret = false;
		int x = (int) ev.getX();
		int y = (int) ev.getY();

		switch (st) {
		case VIDEO_MAP:
			ret = (x >= (int) tsv.getLeft() && tsv.getRight() >= x
					&& (int) tsv.getTop() <= y && tsv.getBottom() >= y)
					//check volume touch event
					&& !(x > volumnWidget.getLeft()
							&& x < volumnWidget.getRight()
							&& y > volumnWidget.getTop() && y < volumnWidget
							.getBottom());
			break;
		case VIDEO_SHARE:
			ret = true;
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			ret = (x >= (int) requestConnectLayout.getLeft()
			&& requestConnectLayout.getRight() >= x
			&& (int) requestConnectLayout.getTop() <= y && requestConnectLayout
			.getBottom() >= y);
			break;
		case VIDEO_SHARE_MAP:
			break;
		case VIDEO_SHARE_P2P_PUBLISHER:
			ret = (x >= (int) p2pVideoLayout.getLeft()
			&& p2pVideoLayout.getRight() >= x
			&& (int) p2pVideoLayout.getTop() <= y && p2pVideoLayout
			.getBottom() >= y);
			break;
		case VIDEO_PUBLISHER_SHOW:
			ret = (x >= (int) lierInteractionLayout.getLeft()
			&& lierInteractionLayout.getRight() >= x
			&& (int) lierInteractionLayout.getTop() <= y && lierInteractionLayout
			.getBottom() >= y);
			break;
		case VIDEO_WATCHING_AUDIO_CONNECTION:
			ret = (x >= (int) p2pAudioWatcherLayout.getLeft()
			&& p2pAudioWatcherLayout.getRight() >= x
			&& (int) p2pAudioWatcherLayout.getTop() <= y && p2pAudioWatcherLayout
			.getBottom() >= y);
			break;
		case INQUIRE_BIDING:
			ret = (x >= (int) inquiryBidWidget.getLeft()
			&& inquiryBidWidget.getRight() >= x
			&& (int) inquiryBidWidget.getTop() <= y && inquiryBidWidget
			.getBottom() >= y);
			break;
		default:
			break;
		}

		return ret;
	}
	
	
	private void doTouchDown(MotionEvent ev) {
		mInitY = (int)ev.getY();
		mLastY = mInitY;
		mInitX = (int)ev.getX();
		
		ts = TouchState.DRAGGING;
	}
	
	
	private void doTouchMove(MotionEvent ev) {
		if (ts != TouchState.DRAGGING) {
			ts = TouchState.DRAGGING;
		}
		int dy =  (int)ev.getY() - mLastY;
		switch (st) {
		case VIDEO_MAP:
			translateTsvAndMap(dy);
			break;
		case VIDEO_SHARE:
			translateTsvAndMap(dy);
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			translateBottomView(requestConnectLayout, dy);
			break;
		case VIDEO_SHARE_MAP:
			break;
		case VIDEO_SHARE_P2P_PUBLISHER:
			translateBottomView(p2pVideoLayout, dy);
			break;
		case VIDEO_PUBLISHER_SHOW:
			translateBottomView(lierInteractionLayout, dy);
			break;
		case VIDEO_WATCHING_AUDIO_CONNECTION:
			translateBottomView(p2pAudioWatcherLayout, dy);
			break;
		case INQUIRE_BIDING:
			if (mMapView.getTop() + dy > getTop()) {
				translateBottomView(inquiryBidWidget, dy);
				translateTopView(tsv, dy);
				translateTopView(mMsgLayout, dy);
				translateTopView(lierInteractionLayout, dy);
				translateTopView(liveWatcherLayout, dy);
				bountyMarker.offsetTopAndBottom(dy);
				inquiryCloseBtn.offsetTopAndBottom(dy);
				mMapView.offsetTopAndBottom(dy);
			}
			break;
		default:
			break;
		}
	}
	
	private void doTouchUp(MotionEvent ev) {
		int absDisY = Math.abs((int)ev.getY() - mInitY);
		int disY = (int)ev.getY() - mInitY;
		switch (st) {
		case VIDEO_MAP:
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				if (disY < 0) {
					fly.startFlying(tsv.getMeasuredHeight() - absDisY , ScreenType.INQUIRE_BIDING);
				} else {
					fly.startFlying(getBottom() - absDisY , ScreenType.VIDEO_SHARE);
				}
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.VIDEO_MAP);
			}
			break;
		case VIDEO_SHARE:
			if (absDisY > FLYING_SLOP) {
				if (disY < 0) {
					ps = PostState.GO_NEXT;
					fly.startFlying(getBottom() - absDisY , ScreenType.VIDEO_MAP);
				} else {
					if (DEBUG) {
						V2Log.w("==  drag down, not go next ");
					}
				}
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.VIDEO_SHARE);
			}
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				fly.startFlying(getBottom() - borderY - absDisY , ScreenType.VIDEO_SHARE);
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , st);
			}
			break;
		case VIDEO_SHARE_MAP:
			break;
		case VIDEO_SHARE_P2P_PUBLISHER:
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				fly.startFlying(getBottom() - borderY - absDisY , ScreenType.VIDEO_SHARE);
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.VIDEO_SHARE_P2P_PUBLISHER);
			}
			break;
		case VIDEO_PUBLISHER_SHOW:
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				fly.startFlying(getBottom() - borderY - absDisY , ScreenType.VIDEO_MAP);
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.VIDEO_PUBLISHER_SHOW);
			}
			break;
		case VIDEO_WATCHING_AUDIO_CONNECTION:
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				fly.startFlying(getBottom() - borderY - absDisY , ScreenType.VIDEO_MAP);
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.VIDEO_WATCHING_AUDIO_CONNECTION);
			}
			break;
		case INQUIRE_BIDING:
			if (disY <= 0) {
				break;
			}
			if (absDisY > FLYING_SLOP) {
				ps = PostState.GO_NEXT;
				fly.startFlying(getBottom() - borderY - absDisY , ScreenType.VIDEO_MAP);
			} else {
				ps = PostState.RESTORE;
				fly.startFlying(absDisY , ScreenType.INQUIRE_BIDING);
			}
			break;
		default:
			break;
		}
		
	}
	
	

	private void translateTsvAndMap(int offset) {
		if (tsv.getTop() +offset >= getBottom()) {
			return;
		}
		tsv.offsetTopAndBottom(offset);
		mMapView.offsetTopAndBottom(offset);
		//for inquiry bid
		if (tsv.getTop() < 0) {
			V2Log.i("===>" + inquiryBidWidget.getTop() +"   left:" + inquiryBidWidget.getLeft() +"   right:" + inquiryBidWidget.getRight() +"   bottom:" + inquiryBidWidget.getBottom());
			shareSurfaceView.offsetTopAndBottom(offset);
			videoShareBtnLayout.offsetTopAndBottom(offset);
			
			liveInformationLayout.offsetTopAndBottom(offset);
			liveWatcherLayout.offsetTopAndBottom(offset);
			mMsgLayout.offsetTopAndBottom(offset);
			inquiryBidWidget.offsetTopAndBottom(offset);
			volumnWidget.offsetTopAndBottom(offset);
			volumneIcon.offsetTopAndBottom(offset);
		}
	}
	
	
	
	private void translateTopView(View topView, int offset) {
		int top = topView.getTop();
		if (DEBUG) {
			V2Log.i("st:"+st+" offset: "+ offset+"    top:"+top+"   view:" + topView);
		}
		if (top + offset > getTop()) {
			V2Log.w("cross top limition :" + top +"   view:"+ topView);
			return;
		}
		topView.offsetTopAndBottom(offset);
	}
	
	private void translateBottomView(View bottomView, int offset) {
		int top = bottomView.getTop();
		if (DEBUG) {
			V2Log.i("st:"+st+" offset: "+ offset+"    top:"+top+"   borderY:"+borderY);
		}
		if (top + offset < borderY) {
			V2Log.w("cross bodery Y :" + borderY +"   view:"+ bottomView);
			return;
		}
		bottomView.offsetTopAndBottom(offset);
	}
	
	private void postTranslation(int offset, ScreenType next) {
		switch (st) {
		case VIDEO_MAP:
			if (ps == PostState.GO_NEXT) {
				if (next == ScreenType.INQUIRE_BIDING) {
					translateTsvAndMap(-offset);
				} else {
					translateTsvAndMap(offset);
				}
			} else {
				if (next == ScreenType.INQUIRE_BIDING) {
					translateTsvAndMap(offset);
				} else {
					translateTsvAndMap(-offset);
				}
			}
			break;
		case VIDEO_SHARE:
			if (ps == PostState.GO_NEXT) {
				translateTsvAndMap(-offset);
			} else {
				translateTsvAndMap(offset);
			}
			break;
		case VIDEO_SHARE_CONNECTION_REQUESTING:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(requestConnectLayout, offset);
			} else {
				translateBottomView(requestConnectLayout, -offset);
			}
			break;
		case VIDEO_SHARE_MAP:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(mMapView, offset);
				translateBottomView(returnBtnView, offset);
			} else {
				translateBottomView(mMapView, -offset);
				translateBottomView(returnBtnView, -offset);
			}
			break;
		case VIDEO_SHARE_P2P_PUBLISHER:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(p2pVideoLayout, offset);
			} else {
				translateBottomView(p2pVideoLayout, -offset);
			}
			break;
		case VIDEO_PUBLISHER_SHOW:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(lierInteractionLayout, offset);
			} else {
				translateBottomView(lierInteractionLayout, -offset);
			}
			break;
		case VIDEO_WATCHING_AUDIO_CONNECTION:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(p2pAudioWatcherLayout, offset);
			} else {
				translateBottomView(p2pAudioWatcherLayout, -offset);
			}
			break;
		case INQUIRE_BIDING:
			if (ps == PostState.GO_NEXT) {
				translateBottomView(inquiryBidWidget, offset);
				translateTopView(tsv, offset);
				translateTopView(mMsgLayout, offset);
				translateTopView(lierInteractionLayout, offset);
				translateTopView(liveWatcherLayout, offset);
				mMapView.offsetTopAndBottom(offset);
				bountyMarker.offsetTopAndBottom(offset);
				inquiryCloseBtn.offsetTopAndBottom(offset);
			} else {
				translateBottomView(inquiryBidWidget, -offset);
			}
			break;
		default:
			break;
		}
	}
	
	
	
	class Flying implements Runnable {
		
		int distance;
		ScreenType nextType;
		int velocity;
		public void startFlying(int distance, ScreenType nextType) {
			this.distance = distance;
			this.nextType = nextType;
			velocity = 95;
			ts = TouchState.FLYING;
			postOnAnimation(this);
		}

		@Override
		public void run() {
			if (DEBUG) {
				V2Log.i("=== remain distance:" +distance +"  velocity:"+ velocity+"   st:"+ st);
			}
			if (distance > 0) {
				if (distance - velocity <= 0) {
					velocity = distance;
				}
				postTranslation(velocity, nextType);
				distance -= velocity;
				postOnAnimationDelayed(this, 5);
			} else {
				ps = PostState.IDLE;
				if (nextType != st) {
					st = nextType;
					if (uiTypeListener != null) {
						uiTypeListener.onUITypeChanged(st);
					}
				}
				
				ts = TouchState.IDLE;
				requestLayout();
			}
		}
		
	}

	
	


	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (ts != TouchState.IDLE) {
			V2Log.e("=== can not layout due to touch state is not idle " + ts);
			return;
		}
		V2Log.i("====> layout st:"+ st +"   ts:"+ ts);
		int bottomChildTop = top + tsv.getMeasuredHeight();
		int bottomHeight = bottom - bottomChildTop;

		if (st == ScreenType.VIDEO_MAP) {
			borderY = tsv.getMeasuredHeight();
			tsv.layout(left, top, right, bottomChildTop);
			shareSurfaceView.layout(left, top, right, bottomChildTop);
			videoShareBtnLayout.layout(left, bottomChildTop, right, bottom);
			mMapView.layout(left, bottomChildTop, right, bottom);
			lierInteractionLayout.layout(left,bottom, right, bottom + lierInteractionLayout.getMeasuredHeight());
			requestConnectLayout.layout(left,bottom, right, bottom + requestConnectLayout.getMeasuredHeight());
			p2pAudioWatcherLayout.layout(left, bottom, right, bottom + p2pAudioWatcherLayout.getMeasuredHeight());
			inquiryBidWidget.layout(left, bottom, right, bottom + inquiryBidWidget.getMeasuredHeight());
			bountyMarker.layout(left, bottom, right, bottom + bountyMarker.getMeasuredHeight());
			mapLocationTipsWidget.layout(left, -mapLocationTipsWidget.getMeasuredHeight(), right, 0);
			bountyMarker.layout(left, bottom, right, bottom + bountyMarker.getMeasuredHeight());
			inquiryCloseBtn.layout(left, bottom, right, bottom + inquiryCloseBtn.getMeasuredHeight());
			returnBtnView.layout(left, bottom, right, bottom + returnBtnView.getMeasuredHeight());
			videoShareRightWidet.layout(left, bottom, right, bottom + videoShareRightWidet.getMeasuredHeight());
			
			LayoutParams lp = (LayoutParams) volumneIcon.getLayoutParams();
			volumneIcon
					.layout(left + lp.leftMargin,
							bottomChildTop - liveWatcherLayout.getMeasuredHeight()
									- lp.bottomMargin
									- volumneIcon.getMeasuredHeight(),
							left + lp.leftMargin
									+ volumneIcon.getMeasuredWidth(), bottomChildTop
									- liveWatcherLayout.getMeasuredHeight()
									- lp.bottomMargin);

			 lp = (LayoutParams) volumnWidget.getLayoutParams();
			int vwbottom =   bottomChildTop
					- lp.bottomMargin
					- volumneIcon.getMeasuredHeight()
					- liveWatcherLayout.getMeasuredHeight();
			volumnWidget.layout(left + lp.leftMargin, top + lp.topMargin
					+ mMsgLayout.getMeasuredHeight(),
					left + volumnWidget.getMeasuredWidth() + lp.leftMargin, vwbottom);			
			
		} else if (st == ScreenType.VIDEO_SHARE) {
			borderY = shareSurfaceView.getMeasuredHeight();
			tsv.layout(left, bottom, right, bottom + tsv.getMeasuredHeight());
			shareSurfaceView.layout(left, top, right, bottomChildTop);
			videoShareBtnLayout.layout(left, bottomChildTop, right, bottom);
			mMapView.layout(left, bottom, right, bottom + mMapView.getMeasuredHeight());
			lierInteractionLayout.layout(left,bottom, right, bottom + lierInteractionLayout.getMeasuredHeight());
			requestConnectLayout.layout(left,bottom, right, bottom + requestConnectLayout.getMeasuredHeight());
			p2pAudioWatcherLayout.layout(left, bottom, right, bottom + p2pAudioWatcherLayout.getMeasuredHeight());
			inquiryBidWidget.layout(left, bottom, right, bottom + inquiryBidWidget.getMeasuredHeight());
			p2pVideoLayout.layout(left,bottom , right, bottom + p2pVideoLayout.getMeasuredHeight());
			volumnWidget.layout(left,bottom , right, bottom + volumnWidget.getMeasuredHeight());
			volumneIcon.layout(left,bottom , right, bottom + volumneIcon.getMeasuredHeight());
			
			LayoutParams lp = (LayoutParams) returnBtnView.getLayoutParams();
			returnBtnView.layout(right - returnBtnView.getMeasuredWidth()
					- lp.rightMargin, bottom + lp.topMargin, right
					- lp.rightMargin,
					bottom + returnBtnView.getMeasuredHeight() +lp.topMargin
							);
			 lp = (LayoutParams) videoShareRightWidet.getLayoutParams();
			videoShareRightWidet.layout(
					right - videoShareRightWidet.getMeasuredWidth()
							- lp.rightMargin,  lp.topMargin, right
							- lp.rightMargin, borderY - lp.bottomMargin);
			
		} else if (st ==  ScreenType.VIDEO_PUBLISHER_SHOW) {
			borderY = tsv.getMeasuredHeight();
			tsv.layout(left, top, right, bottomChildTop);
			mMapView.layout(left, bottomChildTop, right, bottom);
			lierInteractionLayout.layout(left,bottomChildTop, right, bottom);
			p2pAudioWatcherLayout.layout(left, bottom, right, bottom + p2pAudioWatcherLayout.getMeasuredHeight());
		} else if (st == ScreenType.VIDEO_SHARE_CONNECTION_REQUESTING) {
			borderY = shareSurfaceView.getMeasuredHeight();
			shareSurfaceView.layout(left, top, right, bottomChildTop);
			videoShareBtnLayout.layout(left, bottomChildTop, right, bottom);
			requestConnectLayout.layout(left,shareSurfaceView.getMeasuredHeight(), right, bottom);
			p2pAudioWatcherLayout.layout(left, bottom, right, bottom + p2pAudioWatcherLayout.getMeasuredHeight());
		} else if (st == ScreenType.VIDEO_WATCHING_AUDIO_CONNECTION) {
			borderY = tsv.getMeasuredHeight();
			tsv.layout(left, top, right, bottomChildTop);
			mMapView.layout(left, bottomChildTop, right, bottom);
			p2pAudioWatcherLayout.layout(left, bottomChildTop, right, bottomChildTop + p2pAudioWatcherLayout.getMeasuredHeight());
		} else if (st == ScreenType.VIDEO_SHARE_P2P_PUBLISHER) {
			shareSurfaceView.layout(left, top, right, bottomChildTop);
			videoShareBtnLayout.layout(left, bottomChildTop, right, bottom);
			p2pVideoLayout.layout(left,bottomChildTop , right, bottom);
			mMapView.layout(left, bottom, right, bottom + bottomHeight);
		} else if (st == ScreenType.INQUIRE_BIDING) {
			borderY = bottom - tsv.getMeasuredHeight();
			mMapView.layout(left, top, right, borderY);
			tsv.layout(left, - tsv.getMeasuredHeight(), right, 0 );
			shareSurfaceView.layout(left, - tsv.getMeasuredHeight(), right, 0);
			videoShareBtnLayout.layout(left, top, right, borderY);
			inquiryBidWidget.layout(left, borderY, right, bottom);
			mapLocationTipsWidget.layout(left, top, right, top + mapLocationTipsWidget.getMeasuredHeight());
			
			int bw = bountyMarker.getMeasuredWidth();
			int bh = bountyMarker.getMeasuredHeight();
			int bl = left + (right - left - bw) / 2;
			int br = bl + bw;
			int bto = mMapView.getTop() + (mMapView.getBottom() - mMapView.getTop() ) / 2 - bh;
			int btm = bto + bh;
			bountyMarker.layout(bl, bto, br, btm);
			

			int irleft = left + (right - left - inquiryCloseBtn.getMeasuredWidth()) / 2;
			int irTop = mMapView.getBottom() - inquiryCloseBtn.getMeasuredHeight() - 30;
			inquiryCloseBtn.layout(irleft , irTop , irleft +inquiryCloseBtn.getMeasuredWidth() , irTop + inquiryCloseBtn.getMeasuredHeight());
			
		} else if (st == ScreenType.VIDEO_SHARE_MAP) {
			
		}
		
		
		LayoutParams lp = (LayoutParams)mMsgLayout.getLayoutParams();
		mMsgLayout.layout(left, tsv.getTop() + lp.topMargin, right, tsv.getTop() + mMsgLayout.getMeasuredHeight()+ lp.topMargin);
		
		if (liveInformationLayout.getVisibility() == View.VISIBLE) {
			liveInformationLayout.layout(
					right - liveInformationLayout.getMeasuredWidth(),
					tsv.getTop(), right, tsv.getBottom());
		}
		if (liveWatcherLayout.getVisibility() == View.VISIBLE) {
			liveWatcherLayout.layout(left,
					bottomChildTop - liveWatcherLayout.getMeasuredHeight()
							- tsv.getTop(),
					right - liveInformationLayout.getMeasuredWidth(),
					tsv.getBottom());
		}
		
	}

	
	
	public interface UITypeStatusChangedListener {
		public void onUITypeChanged(ScreenType screenType);
	}
	
	
	private Translate touchSurfaceViewTranslate = new Translate() {

		
		@Override
		public void onStartTranslate() {
			videoPlayer.startTranslate();
		}


		@Override
		public void onTranslate(float x, float y) {
			videoPlayer.translate(x, y);
		}
		

		@Override
		public void onFinishTranslate() {
			videoPlayer.finishTranslate();
		}
		
	};
	
	
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == inquiryCloseBtn) {
				// fake UI behavior for simulate touch move up.
				turnUITypeAnimation(ScreenType.INQUIRE_BIDING,
						ScreenType.VIDEO_MAP,
						PostState.GO_NEXT, getBottom() - mMapView.getBottom());
			} else if (v == returnBtnView) {
				V2Log.i("====> restore to video_share");
				// fake
				turnUITypeAnimation(ScreenType.VIDEO_SHARE_MAP,
						ScreenType.VIDEO_SHARE,
						PostState.GO_NEXT, mMapView.getBottom() - mMapView.getTop());
			}
		}
		
	};
	

	
	enum PostState {
		IDLE, RESTORE, GO_NEXT;
	}
	
	public enum ScreenType {
		VIDEO_MAP, 
		VIDEO_SHARE,
		VIDEO_SHARE_CONNECTION_REQUESTING, 
		VIDEO_SHARE_MAP, 
		VIDEO_SHARE_P2P_WATCHER, 
		VIDEO_SHARE_P2P_PUBLISHER, 
		VIDEO_PUBLISHER_SHOW, 
		VIDEO_WATCHING_AUDIO_CONNECTION, 
		INQUIRE_BIDING;
	}

	enum TouchState {
		IDLE, DRAGGING, FLYING;
	}
	
}
