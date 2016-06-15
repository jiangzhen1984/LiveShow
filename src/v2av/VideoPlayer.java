package v2av;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoPlayer implements SurfaceHolder.Callback {
	public static int DisplayRotation = 0;

	private SurfaceHolder mSurfaceH;
	private Bitmap mBitmap;
	private Rect rect;
	
	
	private Bitmap videoBitmap;
	private Bitmap content1Bitmap;
	private Bitmap content2Bitmap;
	
	
	private Rect content1SrcRect;
	private Rect content1TarRect;
	
	
	private Rect content2SrcRect;
	private Rect content2TarRect;
	
	private Canvas rootCanvas;
	private Canvas videoCanvas;
	private VideoRenderType videoRenderType = VideoRenderType.LEFT_CONTENT;
	
	private Bitmap[] screens;
	private int currentIndex;
	
	boolean renderingVideo;

	
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
		rootCanvas = null;
		mIsSuspended = true;
		screens = null;
	}

	
	public void startTranslate() {
	}
	
	/**
	 * limitation from -1.0 to 1.0
	 * @param x
	 * @param y
	 */
	public void translate(float x, float y) {
		int width = mBitmap.getWidth();
		int distance =(int)( Math.abs(x) * mBitmap.getWidth());
		int len = screens.length;
		int nextIndex = 0;
		if (x > 0) {
			nextIndex = (currentIndex + len - 1) % len;
			content2SrcRect.left = width - distance;
			content2SrcRect.right = width;
			content2TarRect.left = 0;
			content2TarRect.right = distance;
			
			content1SrcRect.left = 0;
			content1SrcRect.right = width - distance;
			content1TarRect.left = distance;
			content1TarRect.right = width;
			if (videoRenderType != VideoRenderType.RIGHT_CONTENT) {
				videoRenderType = VideoRenderType.RIGHT_CONTENT;
				videoCanvas = new Canvas(content2Bitmap);
			}
		} else if (x < 0) {
			nextIndex = (currentIndex + len + 1) % len;
			content2SrcRect.left =  0;
			content2SrcRect.right = distance;
			content2TarRect.left = width - distance;
			content2TarRect.right = width;
			
			content1SrcRect.left = distance;
			content1SrcRect.right = width;
			content1TarRect.left = 0;
			content1TarRect.right = width - distance;
			if (videoRenderType != VideoRenderType.LEFT_CONTENT) {
				videoRenderType = VideoRenderType.LEFT_CONTENT;
				videoCanvas = new Canvas(content1Bitmap);
			}
		}
		content1Bitmap = screens[currentIndex];
		content2Bitmap = screens[nextIndex];
		
		if (x == 1.0F || x == -1.0F) {
			currentIndex = nextIndex;
			//TODO notify index changed
		}
//		
//		V2Log.i("cent :" + x +"  curent Index:" + currentIndex+"  nextIndex:"+nextIndex);
//		V2Log.i("video :" + content1SrcRect+" ===> "+ content1TarRect);
//		V2Log.i("rest :" + content2SrcRect+" ===> "+ content2TarRect);
		if (!renderingVideo) {
			postInvalidate();
		}
		
	}
	
	public void finishTranslate() {
//		Canvas c = new Canvas(restBitmap);
//		Rect dst = new Rect(0, 0, restBitmap.getWidth(), restBitmap.getHeight());
//		c.drawBitmap(mBitmap, null, dst, null);
	}
	
	public void setItemIndex(int idx) {
		if (idx < 0  || idx >= screens.length) {
			throw new IndexOutOfBoundsException(" idx :" + idx);
		}
		int width = mBitmap.getWidth();
		currentIndex = idx;
		int len = screens.length;
		int nextIndex = (currentIndex + len + 1) % len;
		
		content1SrcRect.left = 0;
		content1SrcRect.right = width;
		content1TarRect.left = 0;
		content1TarRect.right = width;
		
		content2SrcRect.left =  0;
		content2SrcRect.right = width;
		content2TarRect.left = width ;
		content2TarRect.right = width + width;
		
		
		content1Bitmap = screens[currentIndex];
		content2Bitmap = screens[nextIndex];
		postInvalidate();
	}

	
	
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas hc = holder.lockCanvas();
		int width = hc.getWidth();
		int height = hc.getHeight();
		holder.unlockCanvasAndPost(hc);
		
		Canvas c = null;
		for (int i = 0; screens != null && i < screens.length; i++) {
			if (screens[i] != null && !screens[i].isRecycled()) {
				screens[i].recycle();
			}
			screens[i] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			c = new Canvas(screens[i]);
			drawFirstBlankFrame(c, i +1);
		}
		
		mSurfaceH = holder;
		
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		rootCanvas = new Canvas(mBitmap);
		

		rect = new Rect(0, 0, width, height);
		
		content1SrcRect = new Rect(0, 0, width, height);
		content1TarRect = new Rect(0, 0, width, height);
		content2SrcRect = new Rect(0, 0, width, height);
		content2TarRect = new Rect(0, 0, width, height);
		setItemIndex(0);
		
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
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
		renderingVideo = false;
		Log.w("V2TECH", "JNI destroy bitmap");
		if (videoBitmap != null && !videoBitmap.isRecycled()) {
			videoBitmap.recycle();
		}
		videoBitmap = null;
		videoCanvas = null;
	}
	
	private void recycleBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
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
		renderingVideo = true;
		
		videoBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		videoCanvas = new Canvas(content1Bitmap);
		videoRenderType = VideoRenderType.LEFT_CONTENT; 
		mClearCanvas = 0;

		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas != null) {
			SetViewSize(canvas.getWidth(), canvas.getHeight());
			mSurfaceH.unlockCanvasAndPost(canvas);
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
		videoBitmap.copyPixelsFromBuffer(_playBuffer);
		_playBuffer.rewind();
		
		videoCanvas.drawBitmap(videoBitmap, null, rect, null);
		
		postInvalidate();
		
	}
	
	
	private void postInvalidate() {
		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas == null) {
			return;
		}

		if (mClearCanvas < 2) {
			canvas.drawRGB(0, 0, 0);
			++mClearCanvas;
		}
		
		rootCanvas.drawBitmap(content1Bitmap, content1SrcRect, content1TarRect, null);
		rootCanvas.drawBitmap(content2Bitmap, content2SrcRect, content2TarRect, null);
		
		canvas.drawBitmap(mBitmap, null, rect, null);
		mSurfaceH.unlockCanvasAndPost(canvas);
	}
	
	
	private void drawFirstBlankFrame(Canvas c, int index) {
		int width = c.getWidth();
		int height = c.getHeight();
		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas tmp = new Canvas(bp);
		tmp.drawColor(Color.argb(255, 0, 0, 0));
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(60);
		tmp.drawText((index) + "", width / 2, height / 2, p);
		c.drawBitmap(bp, 0, 0, new Paint());
		bp.recycle();
	}
	
	
	enum VideoRenderType{
		LEFT_CONTENT,
		RIGHT_CONTENT;
	}
}
