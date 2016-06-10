package com.v2tech.x;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.V2.jni.util.V2Log;
import com.v2tech.v2liveshow.R;
import com.v2tech.widget.V2SurfaceView;
import com.v2tech.widget.VideoShareBtnLayout;

public class WidgetRootLayout extends ViewGroup implements OnTouchListener {

	
	
	private int mTouchSlop;
	private int mTouchTapTimeout;
	
	private V2SurfaceView mSurfaceView;
	private PagerAdapter mViewPagerAdapter;
	private VideoShareBtnLayout viedeoShartBtnLayout;

	private Direction direction = Direction.D_UNKNOW;
	private State mState = State.PLAYING;
	private FlyingState mFlyState = FlyingState.IDLE;

	private int playingSurfaceViewMoveDistance = 1;
	private boolean touchInterceptFlag = false;
	private Flying mFly;

	public WidgetRootLayout(Context context) {
		super(context);
		init();
	}

	public WidgetRootLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WidgetRootLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private SurfaceHolder mholder;

	private void init() {
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mTouchTapTimeout = ViewConfiguration.getTapTimeout();
		
		
		// // setOnTouchListener(this);
		// mVideoShowPager = new CircleViewPager(getContext());
		// mVideoShowPager.setId(0x10000001);
		// mVideoShowPager.setOnPageChangeListener(this);
		// mViewPagerAdapter = new SurfaceViewAdapter(
		// getContext(),
		// 6);
		// mVideoShowPager.setOffscreenPageLimit(6);
		// mVideoShowPager.setAdapter(mViewPagerAdapter);
		// mVideoShowPager.setCurrentItem(2, false);

		viedeoShartBtnLayout = (VideoShareBtnLayout) LayoutInflater.from(
				getContext()).inflate(R.layout.video_share_btn_layout,
				(ViewGroup) null);

		mSurfaceView = new V2SurfaceView(getContext());
		mholder = mSurfaceView.getHolder();
		mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
		mSurfaceView.setZOrderMediaOverlay(true);
		mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Canvas c = holder.lockCanvas();
				int width = c.getWidth();
				int height = c.getHeight();
				Bitmap bp = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_4444);
				Canvas tmp = new Canvas(bp);
				tmp.drawColor(Color.argb(255, 0, 0, 0));
				c.drawBitmap(bp, 0, 0, new Paint());
				bp.recycle();
				holder.unlockCanvasAndPost(c);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {

			}

		});

		this.addView(mSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT,
				700));
		this.addView(viedeoShartBtnLayout, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		this.setOnTouchListener(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		touchInterceptFlag = checkTouchEvent(ev);
		return touchInterceptFlag;
	}

	private boolean checkTouchEvent(MotionEvent ev) {
		if (mFlyState == FlyingState.FLYING) {
			return false;
		}
		int action = ev.getAction();
		boolean ret = false;
		if (action != MotionEvent.ACTION_DOWN) {
			return ret;
		}
		int x = (int) ev.getX();
		int y = (int) ev.getY();

		switch (mState) {
		case PLAYING:
			ret = (x >= (int) mSurfaceView.getX()
					&& mSurfaceView.getRight() >= x
					&& (int) mSurfaceView.getY() <= y && mSurfaceView
					.getBottom() >= y);
			break;
		case RECODING:
			ret = true;
			break;

		}
		return ret;
	}

	int initX;
	int initY;
	int lastX;
	int lastY;
	int distanceY = 1;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!touchInterceptFlag) {
			return false;
		}
		boolean flag = false;
		int act = event.getAction();
		switch (act) {
		case MotionEvent.ACTION_DOWN:
			flag = doTouchDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			flag = doTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			flag = doTouchUp(event);
			break;
		}
		return flag;
	}

	private boolean doTouchDown(MotionEvent event) {
		initX = (int) event.getX();
		initY = (int) event.getY();
		lastX = initX;
		lastY = initY;
		distanceY = 0;
		return true;
	}

	private boolean doTouchMove(MotionEvent event) {
		// TODO check directory first
		// TODO if direction is vertical
		int offsetY = (int) event.getY() - lastY;
		if (offsetY > 0) {
			direction = Direction.D_DOWN;
		} else if (offsetY < 0) {
			direction = Direction.D_UP;
		}
		if (mState == State.PLAYING) {
			offsetForIdelOrPlaying(offsetY);
			distanceY = (lastY - initY);
		} else if (mState == State.RECODING) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			offsetForRecording(offsetY);
			distanceY = (lastY - initY);
		}

		lastX = (int) event.getX();
		lastY = (int) event.getY();

		return true;
	}

	private boolean doTouchUp(MotionEvent event) {
		if (mTouchSlop > Math.abs(lastX - initX) && mTouchSlop > Math.abs(lastY - initY)) {
			viedeoShartBtnLayout.performClick();
			V2Log.i(" no move:" +mTouchSlop +" x-distance:" + Math.abs(lastX - initX) +"  y-distance:" +Math.abs(lastY - initY));
			return false;
		}
		if (mFly == null) {
			mFly = new Flying();
		}
		switch (mState) {
		case PLAYING:
			if (direction == Direction.D_DOWN) {
				mFly.startFlying(playingSurfaceViewMoveDistance - distanceY, State.RECODING);
			} else if (direction == Direction.D_UP) {
				mFly.startFlying(Math.abs(distanceY), State.PLAYING);
			}
			break;
		case RECODING:
			if (direction == Direction.D_DOWN) {
				mFly.startFlying(playingSurfaceViewMoveDistance - distanceY, State.PLAYING);
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void offsetForIdelOrPlaying(int offset) {
		mSurfaceView.offsetTopAndBottom(offset);
		viedeoShartBtnLayout.offsetTopAndBottom(-offset);
		float cent = (float) distanceY / (float) playingSurfaceViewMoveDistance;
		drawAlpha(cent);
	}

	private void offsetForRecording(int offset) {
		mSurfaceView.offsetLeftAndRight(-offset);
		viedeoShartBtnLayout.offsetTopAndBottom(offset);
		float cent = 1.0F - ((float) distanceY / (float) playingSurfaceViewMoveDistance);
		drawAlpha(cent);
	}

	private void drawAlpha(float cent) {
		Canvas c = mholder.lockCanvas();
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.argb((int) (255 * (1F - cent)), 0, 0, 0));
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
		mholder.unlockCanvasAndPost(c);
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		V2Log.i("=== start to layout: " + mState);
		if (mState == State.PLAYING) {
			playingSurfaceViewMoveDistance = viedeoShartBtnLayout
					.getMeasuredHeight() + top;
			mSurfaceView.layout(0, 0, mSurfaceView.getMeasuredWidth(),
					mSurfaceView.getMeasuredHeight());
			viedeoShartBtnLayout.layout(0, bottom, right, bottom
					+ viedeoShartBtnLayout.getMeasuredHeight() + top);
		} else if (mState == State.RECODING) {
			mSurfaceView.layout(right, 0,
					right + mSurfaceView.getMeasuredWidth(),
					mSurfaceView.getMeasuredHeight());
			viedeoShartBtnLayout.layout(0,
					bottom - viedeoShartBtnLayout.getMeasuredHeight() - top,
					right, bottom + viedeoShartBtnLayout.getMeasuredHeight()
							+ top);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxHeight = 0;
		int maxWidth = 0;
		int childState = 0;

		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChildWithMargins(child, widthMeasureSpec, 0,
						heightMeasureSpec, 0);
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				maxWidth = Math.max(maxWidth, child.getMeasuredWidth()
						+ lp.leftMargin + lp.rightMargin);
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight()
						+ lp.topMargin + lp.bottomMargin);
				childState = combineMeasuredStates(childState,
						child.getMeasuredState());
			}
		}

		// Check against our minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		setMeasuredDimension(
				resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
				resolveSizeAndState(maxHeight, heightMeasureSpec,
						childState << MEASURED_HEIGHT_STATE_SHIFT));
	}

	enum Direction {
		D_UNKNOW, D_LEFT, D_RIGHT, D_UP, D_DOWN
	}

	enum State {
		PLAYING, RECODING,
	}

	enum FlyingState {
		IDLE, FLYING;
	}

	public static class LayoutParams extends MarginLayoutParams {
		/**
		 * The left margin in pixels of the child. Call
		 * {@link ViewGroup#setLayoutParams(LayoutParams)} after reassigning a
		 * new value to this field.
		 */
		@ViewDebug.ExportedProperty(category = "layout")
		public int leftMargin;

		/**
		 * The top margin in pixels of the child. Call
		 * {@link ViewGroup#setLayoutParams(LayoutParams)} after reassigning a
		 * new value to this field.
		 */
		@ViewDebug.ExportedProperty(category = "layout")
		public int topMargin;

		/**
		 * The right margin in pixels of the child. Call
		 * {@link ViewGroup#setLayoutParams(LayoutParams)} after reassigning a
		 * new value to this field.
		 */
		@ViewDebug.ExportedProperty(category = "layout")
		public int rightMargin;

		/**
		 * The bottom margin in pixels of the child. Call
		 * {@link ViewGroup#setLayoutParams(LayoutParams)} after reassigning a
		 * new value to this field.
		 */
		@ViewDebug.ExportedProperty(category = "layout")
		public int bottomMargin;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(android.view.ViewGroup.LayoutParams source) {
			super(source);
		}

		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

	}

	class Flying implements Runnable {

		State nextState;
		int distance;
		int velocity;
		boolean setStart = false;

		public void startFlying(int distance, State next) {
			this.distance = distance;
			velocity = 95;
			mFlyState = FlyingState.FLYING;
			this.nextState = next;
			setStart = true;
			postOnAnimation(this);
		}

		@Override
		public void run() {
			if (!setStart) {
				throw new RuntimeException(" does not call startFlying ");
			}
			V2Log.i("=== remain distance:" + distance + "    "
					+ viedeoShartBtnLayout.getBottom() + "  "
					+ mSurfaceView.getTop() + "  velocity:" + velocity);
			if (distance > 0) {
				if (distance - velocity <= 0) {
					velocity = distance;
				}
				if (mState == State.PLAYING) {
					offsetForIdelOrPlaying(direction == Direction.D_UP ? -velocity
						: velocity);
				} else if (mState == State.RECODING) {
					offsetForRecording(direction == Direction.D_UP ? -velocity
						: velocity);
				}
				distance -= velocity;
				if (distance <= 0) {
					velocity = distance;
				} else {
					velocity++;
				}
				postOnAnimationDelayed(this, 15);
			} else {
				V2Log.i("=== flying quit");
				mFlyState = FlyingState.IDLE;
				mState = nextState;
				requestLayout();
				setStart = false;
			}
		}

	};

}
