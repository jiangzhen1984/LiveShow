package v2av;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoPlayer {
	public static int DisplayRotation = 0;

	private SurfaceHolder mSurfaceH;
	private Bitmap mBitmap;
	private Matrix mMatrix = null;

	// private int mDisplayMode = 0; //0,1,2
	private int mClearCanvas = 2;

	private int mRotation = 0;
	private int mBmpRotation = 0;
	private boolean mIsSuspended;

	private VideoDisplayMatrix mDisMatrix;

	private ByteBuffer _playBuffer;

	private int mixVideoType = -1;

	public VideoPlayer() {
	}

	public void zoomIn() {
		if (mDisMatrix == null)
			return;

		mDisMatrix.zoomIn();
		mMatrix = mDisMatrix.getDisplayMatrix();
		mClearCanvas = 0;
	}

	public void zoomOut() {
		if (mDisMatrix == null)
			return;

		mDisMatrix.zoomOut();
		mMatrix = mDisMatrix.getDisplayMatrix();
		mClearCanvas = 0;
	}

	public void translate(float dx, float dy) {
		if (mDisMatrix == null)
			return;

		mDisMatrix.translate(dx, dy);
		mMatrix = mDisMatrix.getDisplayMatrix();
		mClearCanvas = 0;
	}

	public void zoomTo(float scale, float cx, float cy, float durationMs) {
		mDisMatrix.zoomTo(scale, cx, cy, durationMs);
		mClearCanvas = -2;
	}

	private float mBaseScale;

	public float getBaseScale() {
		return mBaseScale;
	}

	public float getScale() {
		if (mDisMatrix == null) {
			return 0.0f;
		}

		return mDisMatrix.getScale();
	}

	private void UpdateMatrix() {
		mDisMatrix.resetMatrix();
		mMatrix = mDisMatrix.getDisplayMatrix();
		mBaseScale = mDisMatrix.getScale();
	}

	public void SetViewSize(int w, int h) {
		mClearCanvas = 0;

		if (mDisMatrix != null) {
			mDisMatrix.setViewSize(w, h);
			UpdateMatrix();
		}
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

	public void SetRotation(int rotation) {
		if (mRotation == rotation) {
			return;
		}

		int temp = (rotation + 45) / 90 * 90;
		temp = (temp + DisplayRotation) % 360;

		temp = 360 - temp;

		if (mRotation == temp) {
			return;
		}

		mRotation = temp;

		mClearCanvas = 0;

		if (mDisMatrix != null) {
			mDisMatrix.setRotation((mRotation + mBmpRotation) % 360);
			UpdateMatrix();
		}
	}

	// public void SetDisplayMode(int mode)
	// {
	// mDisplayMode = mode;
	// if(mDisMatrix != null)
	// {
	// UpdateMatrix();
	// }
	// }

	void Release() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}

		mMatrix = null;
		mSurfaceH = null;
		mBitmap = null;
		mDisMatrix = null;
	}

	public void setLayout(int lay) {
		mixVideoType = lay;
	}

	/*
	 * Called by native
	 */
	@SuppressWarnings("unused")
	private void SetBitmapRotation(int rotation) {
		mBmpRotation = rotation;
		mClearCanvas = 0;

		if (mDisMatrix != null) {
			mDisMatrix.setRotation((mRotation + mBmpRotation) % 360);
			UpdateMatrix();
		}
	}

	@SuppressWarnings("unused")
	private void DestroyBitmap() {
		Log.w("V2TECH", "JNI destroy bitmap");
	}

	/*
	 * Called by native
	 */
	@SuppressWarnings("unused")
	private void CreateBitmap(int width, int height) {
		Log.i("jni", "call create bitmap " + width + " " + height);
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}

		// mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);

		if (mDisMatrix == null)
			mDisMatrix = new VideoDisplayMatrix();

		mDisMatrix.setBitmap(mBitmap);
		mDisMatrix.setRotation((mRotation + mBmpRotation) % 360);

		mClearCanvas = 0;

		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas != null) {
			SetViewSize(canvas.getWidth(), canvas.getHeight());
			mSurfaceH.unlockCanvasAndPost(canvas);
		}

		_playBuffer = ByteBuffer.allocateDirect(width * height * 4);
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
		mBitmap.copyPixelsFromBuffer(_playBuffer);
		_playBuffer.rewind();

		Canvas canvas = mSurfaceH.lockCanvas();
		if (canvas == null) {
			return;
		}

		if (mClearCanvas < 2) {
			canvas.drawRGB(0, 0, 0);
			++mClearCanvas;
		}
		Rect dest = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		// if (mMatrix == null) {
		canvas.drawBitmap(mBitmap, null, dest, null);
		// } else {
		// canvas.drawBitmap(mBitmap, mMatrix, null);
		// }

		// draw border for combined video
		// if mixVideoType equals -1, means current video is not combined video
		// FIXME this class should not cared combined video type,
		// see MixVideo.LayoutType
		if (mixVideoType > 0) {
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			Paint p = new Paint();
			p.setColor(Color.WHITE);
			int boxHeight = 0;
			int boxWidth = 0;
			switch (mixVideoType) {
			case 4:
				boxHeight = height / 2;
				boxWidth = width / 2;
				canvas.drawLine(0, boxHeight, width, boxHeight, p);
				canvas.drawLine(boxWidth, 0, boxWidth, height, p);
				break;
			case 6:
				boxHeight = height / 3;
				boxWidth = width / 3;
				canvas.drawLine(boxWidth * 2, boxHeight, width, boxHeight, p);
				canvas.drawLine(0, boxHeight * 2, width, boxHeight * 2, p);

				canvas.drawLine(boxWidth, boxHeight * 2, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, 0, boxWidth * 2, height, p);
				break;
			case 8:
				boxHeight = height / 4;
				boxWidth = width / 4;
				canvas.drawLine(boxWidth * 3, boxHeight, width, boxHeight, p);
				canvas.drawLine(boxWidth * 3, boxHeight * 2, width,
						boxHeight * 2, p);
				canvas.drawLine(0, boxHeight * 3, width, boxHeight * 3, p);

				canvas.drawLine(boxWidth, boxHeight * 3, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, boxHeight * 3, boxWidth * 2,
						height, p);
				canvas.drawLine(boxWidth * 3, 0, boxWidth * 3, height, p);

				break;
			case 9:
				boxHeight = height / 3;
				boxWidth = width / 3;

				canvas.drawLine(0, boxHeight, width, boxHeight, p);
				canvas.drawLine(0, boxHeight * 2, width, boxHeight * 2, p);

				canvas.drawLine(boxWidth, 0, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, 0, boxWidth * 2, height, p);
				break;
			case 101:
				boxHeight = height / 4;
				boxWidth = width / 4;

				canvas.drawLine(0, boxHeight * 2, width, boxHeight * 2, p);
				canvas.drawLine(0, boxHeight * 3, width, boxHeight * 3, p);

				canvas.drawLine(boxWidth, boxHeight * 2, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, boxHeight * 2, boxWidth * 2,
						height, p);
				canvas.drawLine(boxWidth * 3, boxHeight * 2, boxWidth * 3,
						height, p);
				break;

			case 11:
				boxHeight = height / 4;
				boxWidth = width / 4;
				canvas.drawLine(0, boxHeight, boxWidth, boxHeight, p);
				canvas.drawLine(boxWidth * 3, boxHeight, width, boxHeight, p);
				canvas.drawLine(0, boxHeight * 2, boxWidth, boxHeight * 2, p);
				canvas.drawLine(boxWidth * 3, boxHeight * 2, width,
						boxHeight * 2, p);
				canvas.drawLine(0, boxHeight * 3, width, boxHeight * 3, p);

				canvas.drawLine(boxWidth, 0, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, boxHeight * 3, boxWidth * 2,
						height, p);
				canvas.drawLine(boxWidth * 3, boxHeight * 3, boxWidth * 3,
						height, p);
				canvas.drawLine(boxWidth * 3, 0, boxWidth * 3, height, p);

				break;
			case 131:
				boxHeight = height / 4;
				boxWidth = width / 4;
				canvas.drawLine(0, boxHeight, width, boxHeight, p);
				canvas.drawLine(0, boxHeight * 2, boxWidth, boxHeight * 2, p);
				canvas.drawLine(boxWidth * 3, boxHeight * 2, width,
						boxHeight * 2, p);
				canvas.drawLine(0, boxHeight * 3, width, boxHeight * 3, p);

				canvas.drawLine(boxWidth, 0, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, 0, boxWidth * 2, boxHeight, p);
				canvas.drawLine(boxWidth * 2, boxHeight * 3, boxWidth * 2,
						height, p);
				canvas.drawLine(boxWidth * 3, 0, boxWidth * 3, height, p);

				break;
			case 16:
				boxHeight = height / 4;
				boxWidth = width / 4;
				canvas.drawLine(0, boxHeight, width, boxHeight, p);
				canvas.drawLine(0, boxHeight * 2, width, boxHeight * 2, p);
				canvas.drawLine(0, boxHeight * 3, width, boxHeight * 3, p);

				canvas.drawLine(boxWidth, 0, boxWidth, height, p);
				canvas.drawLine(boxWidth * 2, 0, boxWidth * 2, height, p);
				canvas.drawLine(boxWidth * 3, 0, boxWidth * 3, height, p);

				break;
			}
		}

		mSurfaceH.unlockCanvasAndPost(canvas);
	}
}
