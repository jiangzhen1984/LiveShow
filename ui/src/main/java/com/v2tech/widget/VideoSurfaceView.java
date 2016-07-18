package com.v2tech.widget;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

public class VideoSurfaceView extends View {

    static private final String TAG = "SurfaceView";
    static private final boolean DEBUG = false;

    final ArrayList<SurfaceHolder.Callback> mCallbacks
            = new ArrayList<SurfaceHolder.Callback>();

    final int[] mLocation = new int[2];

    final ReentrantLock mSurfaceLock = new ReentrantLock();
     Surface mSurface;
    boolean mDrawingStopped = true;

    final WindowManager.LayoutParams mLayout
            = new WindowManager.LayoutParams();
    final Rect mVisibleInsets = new Rect();
    final Rect mWinFrame = new Rect();
    final Rect mOverscanInsets = new Rect();
    final Rect mContentInsets = new Rect();
    final Configuration mConfiguration = new Configuration();



    final ViewTreeObserver.OnScrollChangedListener mScrollChangedListener
            = new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        updateWindow(false, false);
                    }
            };

    boolean mRequestedVisible = false;
    boolean mWindowVisibility = false;
    boolean mViewVisibility = false;
    int mRequestedWidth = -1;
    int mRequestedHeight = -1;
    /* Set SurfaceView's format to 565 by default to maintain backward
     * compatibility with applications assuming this format.
     */
    int mRequestedFormat = PixelFormat.RGB_565;

    boolean mHaveFrame = false;
    boolean mSurfaceCreated = false;
    long mLastLockTime = 0;

    boolean mVisible = false;
    int mLeft = -1;
    int mTop = -1;
    int mWidth = -1;
    int mHeight = -1;
    int mFormat = -1;
    final Rect mSurfaceFrame = new Rect();
    int mLastSurfaceWidth = -1, mLastSurfaceHeight = -1;
    boolean mUpdateWindowNeeded;
    boolean mReportDrawNeeded;

    private final ViewTreeObserver.OnPreDrawListener mDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // reposition ourselves where the surface is
                    mHaveFrame = getWidth() > 0 && getHeight() > 0;
                    updateWindow(false, false);
                    return true;
                }
            };
    private boolean mGlobalListenersAdded;

    public VideoSurfaceView(Context context) {
        super(context);
        init();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWillNotDraw(true);
        int[] mTexNames = new int[200];
        GLES20.glGenTextures(1, mTexNames, 0);
        final SurfaceTexture st = new SurfaceTexture(mTexNames[0]);
        mSurface  = new Surface(st);
        
    }

    /**
     * Return the SurfaceHolder providing access and control over this
     * SurfaceView's underlying surface.
     *
     * @return SurfaceHolder The holder of the surface.
     */
    public SurfaceHolder getHolder() {
        return mSurfaceHolder;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLayout.token = getWindowToken();
        mLayout.setTitle("SurfaceView");
        mViewVisibility = getVisibility() == VISIBLE;

        if (!mGlobalListenersAdded) {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnScrollChangedListener(mScrollChangedListener);
            observer.addOnPreDrawListener(mDrawListener);
            mGlobalListenersAdded = true;
        }
        this.updateWindow(true, true);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mWindowVisibility = visibility == VISIBLE;
        mRequestedVisible = mWindowVisibility && mViewVisibility;
        updateWindow(false, false);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mViewVisibility = visibility == VISIBLE;
        boolean newRequestedVisible = mWindowVisibility && mViewVisibility;
        if (newRequestedVisible != mRequestedVisible) {
            // our base class (View) invalidates the layout only when
            // we go from/to the GONE state. However, SurfaceView needs
            // to request a re-layout when the visibility changes at all.
            // This is needed because the transparent region is computed
            // as part of the layout phase, and it changes (obviously) when
            // the visibility changes.
            requestLayout();
        }
        mRequestedVisible = newRequestedVisible;
        updateWindow(false, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mGlobalListenersAdded) {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.removeOnScrollChangedListener(mScrollChangedListener);
            observer.removeOnPreDrawListener(mDrawListener);
            mGlobalListenersAdded = false;
        }

        mRequestedVisible = false;
        updateWindow(false, false);
        mHaveFrame = false;
        mLayout.token = null;

        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mRequestedWidth >= 0
                ? resolveSizeAndState(mRequestedWidth, widthMeasureSpec, 0)
                : getDefaultSize(0, widthMeasureSpec);
        int height = mRequestedHeight >= 0
                ? resolveSizeAndState(mRequestedHeight, heightMeasureSpec, 0)
                : getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

  





 

    private void updateWindow(boolean force, boolean redrawNeeded) {
        if (!mHaveFrame) {
            return;
        }
 
        int myWidth = mRequestedWidth;
        if (myWidth <= 0) myWidth = getWidth();
        int myHeight = mRequestedHeight;
        if (myHeight <= 0) myHeight = getHeight();

        getLocationInWindow(mLocation);
        final boolean formatChanged = mFormat != mRequestedFormat;
        final boolean sizeChanged = mWidth != myWidth || mHeight != myHeight;
        final boolean visibleChanged = mVisible != mRequestedVisible;

        if (force ||  formatChanged || sizeChanged || visibleChanged
            || mLeft != mLocation[0] || mTop != mLocation[1]
            || mUpdateWindowNeeded || mReportDrawNeeded || redrawNeeded) {

            if (DEBUG) Log.i(TAG, "Changes: creating="                     + " format=" + formatChanged + " size=" + sizeChanged
                    + " visible=" + visibleChanged
                    + " left=" + (mLeft != mLocation[0])
                    + " top=" + (mTop != mLocation[1]));

            final boolean visible = mVisible = mRequestedVisible;
			mLeft = mLocation[0];
			mTop = mLocation[1];
			mWidth = myWidth;
			mHeight = myHeight;
			mFormat = mRequestedFormat;

			// Scaling/Translate window's layout here because mLayout is not used elsewhere.

			// Places the window relative
			mLayout.x = mLeft;
			mLayout.y = mTop;
			mLayout.width = getWidth();
			mLayout.height = getHeight();
          

			mLayout.format = mRequestedFormat;
			mLayout.flags |=WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			              | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			              | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
			              | WindowManager.LayoutParams.FLAG_SCALED
			              | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			              | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			              ;
           

			boolean realSizeChanged;
			boolean reportDrawNeeded;

			int relayoutResult;

			mSurfaceLock.lock();
			try {
			    mUpdateWindowNeeded = false;
			    reportDrawNeeded = mReportDrawNeeded;
			    mReportDrawNeeded = false;
			    mDrawingStopped = !visible;

			    if (DEBUG) Log.i(TAG, "Cur surface: " + mSurface);
			    if (DEBUG) Log.i(TAG, "New surface: " 
			            + ", vis=" + visible + ", frame=" + mWinFrame);

			    mSurfaceFrame.left = 0;
			    mSurfaceFrame.top = 0;
			    mSurfaceFrame.right = mWinFrame.width();
			    mSurfaceFrame.bottom = mWinFrame.height();

			    final int surfaceWidth = mSurfaceFrame.right;
			    final int surfaceHeight = mSurfaceFrame.bottom;
			    realSizeChanged = mLastSurfaceWidth != surfaceWidth
			            || mLastSurfaceHeight != surfaceHeight;
			    mLastSurfaceWidth = surfaceWidth;
			    mLastSurfaceHeight = surfaceHeight;
			} finally {
			    mSurfaceLock.unlock();
			}

			try {
			    redrawNeeded |= reportDrawNeeded;

			    SurfaceHolder.Callback callbacks[] = null;


			    if (visible && mSurface.isValid()) {
			        if (!mSurfaceCreated && ( visibleChanged)) {
			            mSurfaceCreated = true;
			            if (DEBUG) Log.i(TAG, "visibleChanged -- surfaceCreated");
			            if (callbacks == null) {
			                callbacks = getSurfaceCallbacks();
			            }
			            for (SurfaceHolder.Callback c : callbacks) {
			                c.surfaceCreated(mSurfaceHolder);
			            }
			        }
			        if (formatChanged || sizeChanged
			                || visibleChanged || realSizeChanged) {
			            if (DEBUG) Log.i(TAG, "surfaceChanged -- format=" + mFormat
			                    + " w=" + myWidth + " h=" + myHeight);
			            if (callbacks == null) {
			                callbacks = getSurfaceCallbacks();
			            }
			            for (SurfaceHolder.Callback c : callbacks) {
			                c.surfaceChanged(mSurfaceHolder, mFormat, myWidth, myHeight);
			            }
			        }
			        if (redrawNeeded) {
			            if (DEBUG) Log.i(TAG, "surfaceRedrawNeeded");
			            if (callbacks == null) {
			                callbacks = getSurfaceCallbacks();
			            }
			            for (SurfaceHolder.Callback c : callbacks) {
			                if (c instanceof SurfaceHolder.Callback2) {
			                    ((SurfaceHolder.Callback2)c).surfaceRedrawNeeded(
			                            mSurfaceHolder);
			                }
			            }
			        }
			    }
			} finally {
			    if (redrawNeeded) {
			        if (DEBUG) Log.i(TAG, "finishedDrawing");
			    }
			}
            if (DEBUG) Log.v(
                TAG, "Layout: x=" + mLayout.x + " y=" + mLayout.y +
                " w=" + mLayout.width + " h=" + mLayout.height +
                ", frame=" + mSurfaceFrame);
        }
    }

    private SurfaceHolder.Callback[] getSurfaceCallbacks() {
        SurfaceHolder.Callback callbacks[];
        synchronized (mCallbacks) {
            callbacks = new SurfaceHolder.Callback[mCallbacks.size()];
            mCallbacks.toArray(callbacks);
        }
        return callbacks;
    }

    void handleGetNewSurface() {
        updateWindow(false, false);
    }

    /**
     * Check to see if the surface has fixed size dimensions or if the surface's
     * dimensions are dimensions are dependent on its current layout.
     *
     * @return true if the surface has dimensions that are fixed in size
     * @hide
     */
    public boolean isFixedSize() {
        return (mRequestedWidth != -1 || mRequestedHeight != -1);
    }

   

    private final SurfaceHolder mSurfaceHolder = new SurfaceHolder() {

        private static final String LOG_TAG = "SurfaceHolder";

        @Override
        public boolean isCreating() {
        	return false;
        }

        @Override
        public void addCallback(Callback callback) {
            synchronized (mCallbacks) {
                // This is a linear search, but in practice we'll
                // have only a couple callbacks, so it doesn't matter.
                if (mCallbacks.contains(callback) == false) {
                    mCallbacks.add(callback);
                }
            }
        }

        @Override
        public void removeCallback(Callback callback) {
            synchronized (mCallbacks) {
                mCallbacks.remove(callback);
            }
        }

        @Override
        public void setFixedSize(int width, int height) {
            if (mRequestedWidth != width || mRequestedHeight != height) {
                mRequestedWidth = width;
                mRequestedHeight = height;
                requestLayout();
            }
        }

        @Override
        public void setSizeFromLayout() {
            if (mRequestedWidth != -1 || mRequestedHeight != -1) {
                mRequestedWidth = mRequestedHeight = -1;
                requestLayout();
            }
        }

        @Override
        public void setFormat(int format) {

            // for backward compatibility reason, OPAQUE always
            // means 565 for SurfaceView
            if (format == PixelFormat.OPAQUE)
                format = PixelFormat.RGB_565;

            mRequestedFormat = format;
        }

        /**
         * @deprecated setType is now ignored.
         */
        @Override
        @Deprecated
        public void setType(int type) { }

        @Override
        public void setKeepScreenOn(boolean screenOn) {
        }

        /**
         * Gets a {@link Canvas} for drawing into the SurfaceView's Surface
         *
         * After drawing into the provided {@link Canvas}, the caller must
         * invoke {@link #unlockCanvasAndPost} to post the new contents to the surface.
         *
         * The caller must redraw the entire surface.
         * @return A canvas for drawing into the surface.
         */
        @Override
        public Canvas lockCanvas() {
            return internalLockCanvas(null);
        }

        /**
         * Gets a {@link Canvas} for drawing into the SurfaceView's Surface
         *
         * After drawing into the provided {@link Canvas}, the caller must
         * invoke {@link #unlockCanvasAndPost} to post the new contents to the surface.
         *
         * @param inOutDirty A rectangle that represents the dirty region that the caller wants
         * to redraw.  This function may choose to expand the dirty rectangle if for example
         * the surface has been resized or if the previous contents of the surface were
         * not available.  The caller must redraw the entire dirty region as represented
         * by the contents of the inOutDirty rectangle upon return from this function.
         * The caller may also pass <code>null</code> instead, in the case where the
         * entire surface should be redrawn.
         * @return A canvas for drawing into the surface.
         */
        @Override
        public Canvas lockCanvas(Rect inOutDirty) {
            return internalLockCanvas(inOutDirty);
        }

        private final Canvas internalLockCanvas(Rect dirty) {
            mSurfaceLock.lock();

            Canvas c = null;
            if (!mDrawingStopped) {
                try {
                    c = mSurface.lockCanvas(dirty);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exception locking surface", e);
                }
            }

            if (DEBUG) Log.i(TAG, "Returned canvas: " + c);
            if (c != null) {
                mLastLockTime = SystemClock.uptimeMillis();
                return c;
            }

            // If the Surface is not ready to be drawn, then return null,
            // but throttle calls to this function so it isn't called more
            // than every 100ms.
            long now = SystemClock.uptimeMillis();
            long nextTime = mLastLockTime + 100;
            if (nextTime > now) {
                try {
                    Thread.sleep(nextTime-now);
                } catch (InterruptedException e) {
                }
                now = SystemClock.uptimeMillis();
            }
            mLastLockTime = now;
            mSurfaceLock.unlock();

            return null;
        }

        /**
         * Posts the new contents of the {@link Canvas} to the surface and
         * releases the {@link Canvas}.
         *
         * @param canvas The canvas previously obtained from {@link #lockCanvas}.
         */
        @Override
        public void unlockCanvasAndPost(Canvas canvas) {
            mSurface.unlockCanvasAndPost(canvas);
            mSurfaceLock.unlock();
        }

        @Override
        public Surface getSurface() {
            return mSurface;
        }

        @Override
        public Rect getSurfaceFrame() {
            return mSurfaceFrame;
        }
    };
	
	
}
