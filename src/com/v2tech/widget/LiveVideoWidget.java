package com.v2tech.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.v2tech.vo.Live;

public class LiveVideoWidget extends FrameLayout implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
	
	
	public interface DragListener {
		public void startDrag();
		public void stopDrag();
	}

	private static final int VIDEO_REQUEST = 1;

	private Live live;
	private MediaPlayer mp;
	private LocalState lState = LocalState.NONE;

	private SurfaceView sur;
	
	private DragListener dragListener;

	public LiveVideoWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initInernalLayout();
	}

	public LiveVideoWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initInernalLayout();
	}

	public LiveVideoWidget(Context context) {
		super(context);
		initInernalLayout();
	}

	private void initInernalLayout() {
		sur = new SurfaceView(this.getContext());
		sur.setZOrderOnTop(true);
		sur.getHolder().addCallback(new LocalCallback());
		this.addView(sur, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));

	}
	
	
	

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mp != null) {
			mp.release();
			mp = null;
		}
	}

	public void startLive(Live live) {
		this.live = live;
		if (live == null || live.getUrl() == null) {
			return;
		}
		Message.obtain(localHandler, VIDEO_REQUEST, LocalState.PLAYING)
				.sendToTarget();
	}

	public void pause() {
		Message.obtain(localHandler, VIDEO_REQUEST, LocalState.PAUSED)
				.sendToTarget();
	}

	public void resume() {
		Message.obtain(localHandler, VIDEO_REQUEST, LocalState.PLAYING)
				.sendToTarget();
	}

	public void stop() {
		Message.obtain(localHandler, VIDEO_REQUEST, LocalState.STOPED)
				.sendToTarget();
	}
	
	

	private void doVideoRequest(LocalState newSt) {
		synchronized (lState) {
			if (lState == newSt) {
				return;
			}
			switch (newSt) {
			case PLAYING:
				if (live == null) {
					return;
				}
				if (mp == null) {
					mp = new MediaPlayer();
					try {
						mp.setDataSource(live.getUrl());
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					mp.setDisplay(sur.getHolder());
					try {
						mp.prepare();
					} catch (Exception e) {
						e.printStackTrace();
					}
					mp.setOnBufferingUpdateListener(this);
					mp.setOnCompletionListener(this);
					mp.setOnPreparedListener(this);
				}
				break;
			case STOPED:
				if (mp != null) {
					mp.stop();
				}
				break;
			case PAUSED:
				if (mp != null) {
					mp.pause();
				}
				break;
			}

			lState = newSt;
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	@Override
	public void onPrepared(MediaPlayer mp) {

	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}
	
	
	
	

	public DragListener getDragListener() {
		return dragListener;
	}

	public void setDragListener(DragListener dragListener) {
		this.dragListener = dragListener;
	}


	





	boolean startDrag = false;
	int lastX;
	int lastY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		LayoutParams  lp = (FrameLayout.LayoutParams)this.getLayoutParams();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int)event.getX();
			lastY = (int)event.getY();
			if (dragListener != null) {
				dragListener.startDrag();
			}
			break;
		case  MotionEvent.ACTION_MOVE:
			startDrag = true;
			lp.leftMargin += ((int)event.getX() - lastX);
			lp.topMargin += ((int)event.getY() - lastY);
			((ViewGroup)this.getParent()).updateViewLayout(this, lp);
			break;
		case  MotionEvent.ACTION_UP:
			lastX = 0;
			lastY = 0;
			if (dragListener != null) {
				dragListener.stopDrag();
			}
			startDrag = false;
			break;
		}
		
		return true;
	}








	class LocalCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Canvas can = holder.lockCanvas();
			can.drawColor(Color.BLACK);
			holder.unlockCanvasAndPost(can);
			Message.obtain(localHandler, VIDEO_REQUEST, LocalState.PLAYING)
					.sendToTarget();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Message.obtain(localHandler, VIDEO_REQUEST, LocalState.PLAYING)
					.sendToTarget();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Message.obtain(localHandler, VIDEO_REQUEST, LocalState.STOPED)
					.sendToTarget();
		}

	}

	private Handler localHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VIDEO_REQUEST:
				doVideoRequest((LocalState) msg.obj);
				break;
			}
		}

	};

	enum LocalState {
		NONE, PLAYING, LOADING, PAUSED, STOPED,
	}
}
