package v2av;

import java.nio.ByteBuffer;

import com.V2.jni.util.V2Log;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoPlayer {
	public static int DisplayRotation = 0;

	private SurfaceHolder mSurfaceH;
	private Bitmap mBitmap;
	
	private Bitmap mVideoBitmap;
	private Rect videoSrcRect;
	private Rect videoTarRect;
	
	private Bitmap restBitmap;
	private Rect restSrcRect;
	private Rect restTarRect;
	private boolean needDrawRest;
	
	private Canvas rootCanvas;
	private Rect rect;
	
	
	private Bitmap[] screens;
	private int currentIndex;

	
	// private int mDisplayMode = 0; //0,1,2
	private int mClearCanvas = 2;

	private boolean mIsSuspended;

	private ByteBuffer _playBuffer;


	public VideoPlayer() {
	}


	public VideoPlayer(int itemCount) {
		if (itemCount <= 0) {
			throw new RuntimeException(" item count must be number");
		}
		screens = new Bitmap[itemCount];
	}


	public void SetViewSize(int w, int h) {
		mClearCanvas = 0;
	}

	public boolean isSuspended() {
		return mIsSuspended;
	}

	public void setSuspended(boolean spended) {
		this.mIsSuspended = spended;
	}

	public void SetSurface(SurfaceHolder holder) {
		mSurfaceH = holder;
	}



	void Release() {
		recycleBitmap();
		
		mSurfaceH = null;
		mBitmap = null;
		mVideoBitmap = null;
		rootCanvas = null;
		mIsSuspended = true;
		restBitmap = null;
		screens = null;
	}

	
	public void startTranslate() {
		needDrawRest = true;
	}
	
	/**
	 * limitation from -1.0 to 1.0
	 * @param x
	 * @param y
	 */
	public void translate(float x, float y) {
		int width = mVideoBitmap.getWidth();
		int distance =(int)( Math.abs(x) * mVideoBitmap.getWidth());
		int len = screens.length;
		int nextIndex = 0;
		if (x > 0) {
			nextIndex = (currentIndex + len - 1) % len;
			restSrcRect.left = width - distance;
			restSrcRect.right = width;
			restTarRect.left = 0;
			restTarRect.right = distance;
			
			videoSrcRect.left = 0;
			videoSrcRect.right = width - distance;
			videoTarRect.left = distance;
			videoTarRect.right = width;
		} else if (x < 0) {
			nextIndex = (currentIndex + len + 1) % len;
			restSrcRect.left =  0;
			restSrcRect.right = distance;
			restTarRect.left = width - distance;
			restTarRect.right = width;
			
			videoSrcRect.left = distance;
			videoSrcRect.right = width;
			videoTarRect.left = 0;
			videoTarRect.right = width - distance;
		}
		
		restBitmap = screens[nextIndex];
		V2Log.i("cent :" + x);
		V2Log.i("video :" + videoSrcRect+" ===> "+ videoTarRect);
		V2Log.i("rest :" + restSrcRect+" ===> "+ restTarRect);
		
	}
	
	public void finishTranslate() {
		needDrawRest = false;
		Canvas c = new Canvas(restBitmap);
		Rect dst = new Rect(0, 0, restBitmap.getWidth(), restBitmap.getHeight());
		c.drawBitmap(mVideoBitmap, null, dst, null);
	}
	
	public void setItemIndex(int idx) {
		if (idx < 0  || idx >= screens.length) {
			throw new IndexOutOfBoundsException(" idx :" + idx);
		}
		currentIndex = idx;
	}


	/*
	 * Called by native
	 */
	@SuppressWarnings("unused")
	private void SetBitmapRotation(int rotation) {
		mClearCanvas = 0;
	}

	@SuppressWarnings("unused")
	private void DestroyBitmap() {
		Log.w("V2TECH", "JNI destroy bitmap");
	}
	
	private void recycleBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		
		if (mVideoBitmap != null) {
			mVideoBitmap.recycle();
		}
		
		if (restBitmap != null) {
			restBitmap.recycle();
		}

		for (int i = 0; screens != null && i < screens.length; i++) {
			if (screens[i] != null && !screens[i].isRecycled()) {
				screens[i].recycle();
			}
		}
	}

	/*
	 * Called by native
	 */
	@SuppressWarnings("unused")
	private void CreateBitmap(int width, int height) {
		Log.i("jni", "call create bitmap " + width + " " + height);
		recycleBitmap();

		// mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mVideoBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		restBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		videoSrcRect = new Rect(0, 0, width, height);
		videoTarRect = new Rect(0, 0, width, height);
		restSrcRect = new Rect(0, 0, width, height);
		restTarRect = new Rect(0, 0, width, height);
		
		
		rootCanvas = new Canvas(mBitmap);
		mClearCanvas = 0;

		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas != null) {
			SetViewSize(canvas.getWidth(), canvas.getHeight());
			mSurfaceH.unlockCanvasAndPost(canvas);
		}
		
		
		Canvas c = null;
		for (int i = 0; screens != null && i < screens.length; i++) {
			if (screens[i] != null && !screens[i].isRecycled()) {
				screens[i].recycle();
			}
			screens[i] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			c = new Canvas(screens[i]);
			drawFirstBlankFrame(c);
		}

		_playBuffer = ByteBuffer.allocateDirect(width * height * 4);
		mIsSuspended = false;
	}

	/*
	 * Called by native
	 */
	@SuppressWarnings("unused")
	private void OnPlayVideo() {
		if (mSurfaceH == null || !mSurfaceH.getSurface().isValid()) {
			return;
		}
		if (this.mIsSuspended) {
			return;
		}
		mVideoBitmap.copyPixelsFromBuffer(_playBuffer);
		_playBuffer.rewind();

		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas == null) {
			return;
		}

		if (mClearCanvas < 2) {
			canvas.drawRGB(0, 0, 0);
			++mClearCanvas;
		}
		
		if (rect == null) {
			rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		
		rootCanvas.drawBitmap(mVideoBitmap, videoSrcRect, videoTarRect, null);
		if (needDrawRest) {
			rootCanvas.drawBitmap(restBitmap, restSrcRect, restTarRect, null);
		}
		
		canvas.save();
		canvas.drawBitmap(mBitmap, null, rect, null);
		canvas.restore();

		mSurfaceH.unlockCanvasAndPost(canvas);
	}
	
	
	static int index = 1;
	private void drawFirstBlankFrame(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.argb(255, 0, 0, 0));
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(60);
		tmp.drawText((index++) + "", width / 2, height / 2, p);
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}
}
