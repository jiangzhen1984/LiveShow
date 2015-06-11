package com.v2tech.view;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.V2.jni.util.V2Log;
import com.v2tech.vo.Live;

public class VideoShowFragment extends Fragment {

	private MediaPlayer mPlayer;
	private SurfaceView mSurfaceView;
	private boolean playing;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlayer = new MediaPlayer();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = new RelativeLayout(getActivity());
		mSurfaceView = new SurfaceView(getActivity());
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
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
		}
		mPlayer.release();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (mPlayer == null) {
			return;
		}
		if (!isVisibleToUser) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
			}
		} else {
			if (!mPlayer.isPlaying()) {
				mPlayer.start();
			}

		}
	}

	public void play(AssetFileDescriptor fd) {
		if (fd == null) {
			return;
		}
		if (playing) {
			mPlayer.stop();
			playing = false;
		}
		try {
			mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			fd.close();
			mPlayer.prepare();
			mPlayer.start();
			playing = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(String url) {
		if (url == null) {
			return;
		}

		if (playing) {
			mPlayer.stop();
			playing = false;
		}
		try {
			mPlayer.setDataSource(url);
			mPlayer.prepare();
			mPlayer.start();
			playing = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(Live live) {
		if (live == null) {
			return;
		}
		if (playing) {
			mPlayer.stop();
			playing = false;
		}

		try {
			mPlayer.setDataSource(live.getUrl());
			mPlayer.prepare();
			mPlayer.start();
			playing = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (playing) {
			mPlayer.stop();
		}
		playing = false;
	}

	private void drawFirstBlankFrame(Canvas c) {
		if (playing) {
			return;
		}
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.BLACK);

		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}

	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas c = holder.lockCanvas();
			drawFirstBlankFrame(c);
			holder.unlockCanvasAndPost(c);
			mPlayer.setDisplay(holder);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			mPlayer.setDisplay(holder);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}

	};

}
