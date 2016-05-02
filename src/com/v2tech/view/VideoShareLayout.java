package com.v2tech.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.v2tech.v2liveshow.R;
import com.v2tech.widget.LiveInformationLayout;
import com.v2tech.widget.LiveInformationLayout.LiveInformationLayoutListener;
import com.v2tech.widget.P2PAudioLiverLayout;
import com.v2tech.widget.P2PAudioLiverLayout.P2PAudioLiverLayoutListener;
import com.v2tech.widget.P2PVideoMainLayout;
import com.v2tech.widget.P2PVideoMainLayout.P2PVideoMainLayoutListener;
import com.v2tech.widget.RequestConnectLayout;
import com.v2tech.widget.RequestConnectLayout.RequestConnectLayoutListener;
import com.v2tech.widget.VideoShareBtnLayout;
import com.v2tech.widget.VideoShareBtnLayout.VideoShareBtnLayoutListener;
import com.v2tech.widget.VideoWatcherListLayout;
import com.v2tech.widget.VideoWatcherListLayout.VideoWatcherListLayoutListener;

public class VideoShareLayout extends LinearLayout {
	
	private static final int ANIMATION_TYPE_IN = 1;
	private static final int ANIMATION_TYPE_OUT = 2;
	//from down to up  for in and from up to down for out
	private static final int ANIMATION_TYPE_CATEGORY = 1;
	
	private static final int ANIMATION_DURATION = 1000;
	
	
	private SurfaceView localCameraSurface;
	private LiveInformationLayout  liverInformationLayout;
	private VideoWatcherListLayout videoWatcherListLayout;
	private VideoShareBtnLayout videoShareBtnLayout;
	private RequestConnectLayout connectionRequestLayout;
	private P2PVideoMainLayout p2pVideoMainLayout;
	private P2PAudioLiverLayout p2pAudioLiverLayout;

	public VideoShareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideoShareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoShareLayout(Context context) {
		super(context);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		if (child.getId() == R.id.video_share_top_ly) {
			localCameraSurface = (SurfaceView)child.findViewById(R.id.local_camera_view);
			liverInformationLayout = (LiveInformationLayout)child.findViewById(R.id.video_right_border_layout);
			videoWatcherListLayout = (VideoWatcherListLayout)child.findViewById(R.id.video_layout_bottom_layout);
		} else if (child.getId() == R.id.video_share_bottom_ly) {
			videoShareBtnLayout = (VideoShareBtnLayout)child.findViewById(R.id.video_share_btn_ly);
			connectionRequestLayout = (RequestConnectLayout)child.findViewById(R.id.video_share_request_connect);
			p2pVideoMainLayout = (P2PVideoMainLayout)child.findViewById(R.id.p2p_video_main_layout);
			p2pAudioLiverLayout = (P2PAudioLiverLayout)child.findViewById(R.id.p2p_audio_liver_layout);
		}
	}
	
	
	
	public void setVideoShareBtnLayoutListener(VideoShareBtnLayoutListener listener) {
		this.videoShareBtnLayout.setListener(listener);
	}

	public void setLiveInformationLayoutListener(LiveInformationLayoutListener listener) {
		this.liverInformationLayout.setListener(listener);
	}
	
	
	public void setVideoWatcherListLayoutListener(VideoWatcherListLayoutListener listener) {
		this.videoWatcherListLayout.setListener(listener);
	}
	
	
	public void setRequestConnectLayoutListener(RequestConnectLayoutListener listener) {
		this.connectionRequestLayout.setListener(listener);
	}
	
	
	public void setP2PVideoMainLayoutListener(P2PVideoMainLayoutListener listener) {
		this.p2pVideoMainLayout.setListener(listener);
	}
	
	public void setP2PAudioLiverLayoutListener(P2PAudioLiverLayoutListener listener) {
		this.p2pAudioLiverLayout.setListener(listener);
	}
	
	
	
	
	
	public void updateVideoShareBtnBackground(int res) {
		videoShareBtnLayout.updateSharedBtnBackground(res);
	}
	
	
	public SurfaceView getLocalCameraView() {
		return localCameraSurface;
	}
	
	
	public SurfaceView getP2PMainSurface() {
		return p2pVideoMainLayout.getSurfaceView();
	}

	public RequestConnectLayout getConnectionRequestLayout() {
		return connectionRequestLayout;
	}
	
	
	
	public void showRequestConnectionLayout(boolean flag) {
		if (flag && connectionRequestLayout.getVisibility() == View.GONE) {
			connectionRequestLayout.setVisibility(View.VISIBLE);
			connectionRequestLayout.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_IN,
					ANIMATION_DURATION, true));
		} else if (!flag  && connectionRequestLayout.getVisibility() == View.VISIBLE)  {
			connectionRequestLayout.setVisibility(View.GONE);
			connectionRequestLayout.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_OUT,
					ANIMATION_DURATION, true));
		}
	}
	
	
	public void showP2PLiverLayout(boolean flag) {
		Animation ani = null;
		if (flag && connectionRequestLayout.getVisibility() == View.GONE) {
			connectionRequestLayout.setVisibility(View.VISIBLE);
			ani = getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_IN,
					ANIMATION_DURATION, true);
		}else if (!flag  && connectionRequestLayout.getVisibility() == View.VISIBLE)  {
			connectionRequestLayout.setVisibility(View.GONE);
			ani = getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_OUT,
					ANIMATION_DURATION, true);
		}
		
		p2pAudioLiverLayout.startAnimation(ani);
	}
	
	
	public void showP2PVideoLayout(boolean flag) {
		if (flag && p2pVideoMainLayout.getVisibility() == View.GONE) {
			p2pVideoMainLayout.setVisibility(View.VISIBLE);
			p2pVideoMainLayout.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_IN,
					ANIMATION_DURATION, true));
		} else if (!flag  && p2pVideoMainLayout.getVisibility() == View.VISIBLE)  {
			p2pVideoMainLayout.setVisibility(View.GONE);
			p2pVideoMainLayout.startAnimation(getBoxAnimation(
					ANIMATION_TYPE_CATEGORY, ANIMATION_TYPE_OUT,
					ANIMATION_DURATION, true));
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
}
