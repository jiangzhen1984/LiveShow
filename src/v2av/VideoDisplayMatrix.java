package v2av;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
//import android.util.Log;

public class VideoDisplayMatrix 
{
	//图像按比例全屏显示
	private Matrix mBaseMatrix = new Matrix();
	//动态调整图像显示大小及位置
	private Matrix mSuppMatrix = new Matrix();
	//图像要最终显示
	private Matrix mDisplayMatrix = new Matrix();
	
    private final float[] mMatrixValues = new float[9];
    //窗口大小
    private int mViewWidth,mViewHeight;
    
    static final float SCALE_RATE = 1.06F;
    
	private RotateBitmap mRoBitmap = new RotateBitmap(null);
	
//	private static Handler mHandler = new Handler();
	private Thread mThread = null;
	
    public void setBitmap(Bitmap bitmap)
    {
    	mRoBitmap.setBitmap(bitmap);
    	getProperBaseMatrix(mBaseMatrix);
    }
    
    public void setViewSize(int width, int height)
    {
    	mViewWidth = width;
    	mViewHeight = height;
    	getProperBaseMatrix(mBaseMatrix);
    }
    
    public void setRotation(int rotation)
    {
    	mRoBitmap.setRotation(rotation);
    	getProperBaseMatrix(mBaseMatrix);
    }
    
    public void zoomIn() {

        zoomIn(SCALE_RATE);
    }

    public void zoomOut() {

        zoomOut(SCALE_RATE);
    }
    
	//图像放大
	public void zoomIn(float rate)
	{
		if (getScale() > 3)
        {
     	   return;
        }
		
        float cx = mViewWidth / 2F;
        float cy = mViewHeight / 2F;

        mSuppMatrix.postScale(rate, rate, cx, cy);
	}
	
	//图像缩小
	public void zoomOut(float rate)
	{
		if(getScale() < 0.3)
        {
     	   return;
        }
		
        float cx = mViewWidth / 2F;
        float cy = mViewHeight / 2F;
        
        mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
	}
	
	//图像平移
    public void translate(float dx, float dy) {
        postTranslate(dx, dy);
    }
    
    private void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
    }

	
	public Matrix getDisplayMatrix()
	{
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}
	
    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }
    // Get the scale factor out of the matrix.
    float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }
    float getScale() {
        return getScale(mSuppMatrix);
    }
    
    void resetMatrix()
    {
    	mSuppMatrix.reset();
    }
    
    protected void zoomTo(float scale) 
    {
        float cx = mViewWidth / 2F;
        float cy = mViewHeight / 2F;

        zoomTo(scale, cx, cy);
    }
    protected void zoomTo(float scale, float centerX, float centerY) 
    {
        float oldScale = getScale();
        float deltaScale = scale / oldScale;

        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        
        center(true, true);
    }
    
    float oldScale;
    long startTime;
    float incrementPerMs;
    float centerX;
    float centerY;
    float durationMs;
    protected void zoomTo(float pscale, float pcenterX,
                          float pcenterY, float pdurationMs) {
    	centerX = pcenterX;
    	centerY = pcenterY;
    	durationMs = pdurationMs;
    	incrementPerMs = (pscale - getScale()) / durationMs;
        oldScale = getScale();
        startTime = System.currentTimeMillis();
        
        if(mThread == null)
        {
        	mThread = new Thread(new Runnable() {
				
				public void run() 
				{
	        		float currentMs = 0;
	        		while(currentMs < durationMs)
	        		{
	        			long now = System.currentTimeMillis();
	        			currentMs = Math.min(durationMs, now - startTime);
	        			float target = oldScale + (incrementPerMs * currentMs);
	        			zoomTo(target, centerX, centerY);
	        		}
				}
			});
        }
        mThread.run();
    }
    
    // Setup the base matrix so that the image is centered and scaled properly.
    private void getProperBaseMatrix(Matrix matrix) 
    {
    	if(mViewWidth == 0)
    		return;
    	
    	float w = mRoBitmap.getWidth();
    	float h = mRoBitmap.getHeight();
    	
        matrix.reset();
        // We limit up-scaling to 3x otherwise the result may look bad if it's
        // a small icon.
        float widthScale = (float)((float)mViewWidth / w);//Math.min((float)((float)mViewWidth / w), 3.0f);
        float heightScale = (float)((float)mViewHeight / h);//Math.min((float)((float)mViewHeight / h), 3.0f);
        float scale = Math.max(widthScale, heightScale);
        
        matrix.postConcat(mRoBitmap.getRotateMatrix());
        matrix.postScale(scale, scale);

        matrix.postTranslate(
                (mViewWidth  - w * scale) / 2F,
                (mViewHeight - h * scale) / 2F);
    }
    
    
 // Center as much as possible in one or both axis.  Centering is
    // defined as follows:  if the image is scaled down below the
    // view's dimensions then center it (literally).  If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    protected void center(boolean horizontal, boolean vertical) 
    {
        Matrix m = getDisplayMatrix();

        RectF rect = new RectF(0, 0,mRoBitmap.getBitmap().getWidth(),
        						mRoBitmap.getBitmap().getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width  = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            if (height < mViewHeight) {			//图片高度小于窗口
                deltaY = (mViewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {			//图片大于等于窗口,
                deltaY = -rect.top;
            } else if (rect.bottom < mViewHeight) {
                deltaY = mViewHeight - rect.bottom;
            }
        }
        if (horizontal) {
            if (width < mViewWidth) {
                deltaX = (mViewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < mViewWidth) {
                deltaX = mViewWidth - rect.right;
            }
        }
        postTranslate(deltaX, deltaY);
        getDisplayMatrix();
    }
    
}
