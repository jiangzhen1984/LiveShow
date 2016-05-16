package com.v2tech.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;
import com.v2tech.vo.Live;

public class VideoShowFragment extends Fragment {

	private static final boolean DEBUG = true;
	private static final String TAG = "VideoShowFragment";

	private int mIndex;
	private PlayerControl playerControl;
	private ExoPlayer player;
	private SurfaceView mSurfaceView;
	private boolean surfacePushed;
	private VideoFragmentStateListener mStateListener;
	private Object tag;
	
	
	public interface VideoFragmentStateListener {
		public void onInited();
		public void onUnInited();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (DEBUG) {
			V2Log.i(TAG, this+"  created");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = new RelativeLayout(getActivity());
		mSurfaceView = new SurfaceView(getActivity());
		mSurfaceView.setZOrderOnTop(false);
		mSurfaceView.setZOrderMediaOverlay(true);
		mSurfaceView.getHolder().addCallback(mHolderCallback);
		rl.addView(mSurfaceView, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		return rl;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (player == null) {
			return;
		}
	
		if (DEBUG) {
			V2Log.i(TAG, "setUserVisibleHint====> isVisibleToUser:"+isVisibleToUser+"   " + this);
		}
		if (!isVisibleToUser) {
			if (playerControl.isPlaying()) {
				playerControl.pause();
			}
		} else {
			playerControl.start();

		}
	}


	
	

	
	public Object getTag1() {
		return tag;
	}

	public void setTag1(Object tag) {
		this.tag = tag;
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}
	

	public SurfaceView getSurfaceView() {
		return this.mSurfaceView;
	}

	public void setStateListener(VideoFragmentStateListener stateListener) {
		this.mStateListener = stateListener;
	}

	private void drawFirstBlankFrame(Canvas c) {
		//int[]  carray = new int[]{Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.RED, Color.WHITE};
		if (player != null && playerControl.isPlaying()) {
			return;
		}
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.BLACK);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(60);
		tmp.drawText(mIndex + "", width / 2, height / 2, p);
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}

	private Surface surface;
	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas c = holder.lockCanvas();
			drawFirstBlankFrame(c);
			holder.unlockCanvasAndPost(c);
			surface = holder.getSurface();
			if (mStateListener != null) {
				mStateListener.onInited();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			surface = null;
			surfacePushed = false;
		}

	};


}
