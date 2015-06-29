package com.v2tech.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CameraShape extends View {

	private static int MARGIN = 50;
	
	private static final float STAGE_1 = 35.0F;
	
	private static final float STAGE_2 = 35.0F;
	
	private static final float STAGE_3 = 30.0F;

	private float cent;
	private Paint p;
	private Path pathLeft;
	private Path pathRight;
	private RectF arcLeftTop;
	private RectF arcRightTop;
	private RectF arcLeftBottom;
	private RectF arcRightBottom;
	private Path line1;
	private Path line2;
	private Path line3;

	public CameraShape(Context context) {
		super(context);
		init();
	}

	public CameraShape(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraShape(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		p = new Paint();
		p.setStrokeWidth(10.0F);
		p.setStyle(Style.STROKE);
		pathLeft = new Path();
		pathRight = new Path();

		arcLeftTop = new RectF();
		arcRightTop = new RectF();
		arcLeftBottom = new RectF();
		arcRightBottom = new RectF();
		
		line1 = new Path();
		line2 = new Path();
		line3 = new Path();
	}

	public void updatePrecent(float cent) {
		this.cent = cent;
		if (cent >= 100.0F) {
			p.setColor(Color.WHITE);
		} else {
			p.setColor(Color.GRAY);
		}
		this.invalidate();
	}

	private void prepareLeftPart(int left, int top, int right, int bottom,
			int midX, int midY) {

		int topLineStartX = midX;
		int topLineStartY = MARGIN;
		int topLineEndX = topLineStartX - (midX - left) / 3 * 2;
		int topLineEndY = MARGIN;

		pathLeft.reset();

		int leftArcTop = top;
		int leftArcLeft = left;
		int leftArcRight = topLineEndX;
		int leftArcBottom = topLineEndX;

		pathLeft.moveTo(topLineStartX, topLineStartY);
		if (cent > 0F) {
			if (cent >= STAGE_1) {
				pathLeft.lineTo(topLineEndX, topLineEndY);

				// add connect line
				pathLeft.lineTo(leftArcLeft + (leftArcRight - leftArcLeft) / 2,
						leftArcTop);

				arcLeftTop.top = leftArcTop;
				arcLeftTop.left = leftArcLeft;
				arcLeftTop.right = leftArcRight;
				arcLeftTop.bottom = leftArcBottom;
				pathLeft.addArc(arcLeftTop, 180.0F, 90.0F);

			} else {
				pathLeft.lineTo(topLineStartX - (topLineStartX - topLineEndX) * (cent / STAGE_1), topLineEndY);
			}
		}

		int leftVerticalLineStartX = leftArcLeft;
		int leftVerticalLineStartY = leftArcTop + (leftArcBottom - leftArcTop)
				/ 2;

		int leftVerticalLineEndX = leftArcLeft;
		int leftVerticalLineEndY = bottom - (leftArcBottom - leftArcLeft) / 2;

		int leftBottomArcTop = bottom - (topLineEndX - leftArcLeft);
		int leftBottomArcLeft = leftArcLeft;
		int leftBottomArcRight = topLineEndX;
		int leftBottomArcBottom = bottom;
		
		
		if (cent > STAGE_1) {
			pathLeft.moveTo(leftVerticalLineStartX, leftVerticalLineStartY);
			if (cent >= (STAGE_1 + STAGE_2)) {
				pathLeft.lineTo(leftVerticalLineEndX, leftVerticalLineEndY);

				pathLeft.lineTo(leftVerticalLineEndX, leftBottomArcTop
						+ (leftBottomArcBottom - leftBottomArcTop) / 2);
				// add connect line
				pathLeft.moveTo(leftBottomArcRight, leftBottomArcBottom);

				arcLeftBottom.top = leftBottomArcTop;
				arcLeftBottom.left = leftBottomArcLeft;
				arcLeftBottom.right = leftBottomArcRight;
				arcLeftBottom.bottom = leftBottomArcBottom;
				pathLeft.addArc(arcLeftBottom, 90.0F, 90.0F);

			} else {
				pathLeft.lineTo(
						leftVerticalLineEndX,
						leftVerticalLineStartY
								+ (leftVerticalLineEndY - leftVerticalLineStartY) * ((cent - STAGE_1) / STAGE_2));
			}
		}

		int bottomLineStartX = leftBottomArcLeft
				+ (leftBottomArcRight - leftBottomArcLeft) / 2;
		int bottomLineStartY = bottom;
		int bottomLineEndX = midX;
		int bottomLineEndY = bottom;

		if (cent > (STAGE_1 + STAGE_2)) {
			pathLeft.moveTo(bottomLineStartX, bottomLineStartY);
			if (cent >= (STAGE_1 + STAGE_2 + STAGE_3)) {
				pathLeft.lineTo(bottomLineEndX, bottomLineEndY);
			} else {
				pathLeft.lineTo(bottomLineStartX + (bottomLineEndX - bottomLineStartX) * ((cent - (STAGE_1 + STAGE_2)) / STAGE_3),
						bottomLineEndY);
			}
		}

	}

	private void prepareRightPart(int left, int top, int right, int bottom,
			int midX, int midY) {

		int topLineStartX = midX;
		int topLineStartY = MARGIN;
		int topLineEndX = midX + (right - midX) / 3 * 2;
		int topLineEndY = MARGIN;

		pathRight.reset();

		int rightArcTop = top;
		int rightArcLeft = topLineEndX;
		int rightArcRight = right;
		int rightArcBottom = rightArcTop + (rightArcRight - rightArcLeft);

		pathRight.moveTo(topLineStartX, topLineStartY);
		if (cent > 0F) {
			if (cent >= STAGE_1) {
				pathRight.lineTo(topLineEndX, topLineEndY);

				pathRight.lineTo(rightArcLeft + (rightArcRight - rightArcLeft)
						/ 2, rightArcTop);

				arcRightTop.top = rightArcTop;
				arcRightTop.left = rightArcLeft;
				arcRightTop.right = rightArcRight;
				arcRightTop.bottom = rightArcBottom;
				pathRight.addArc(arcRightTop, 270.0F, 90.0F);

			} else {
				pathRight.lineTo(topLineStartX + (topLineEndX - topLineStartX) * (cent / STAGE_1), topLineEndY);
			}
		}

		int rightVerticalLineStartX = right;
		int rightVerticalLineStartY = rightArcTop
				+ (rightArcBottom - rightArcTop) / 2;

		int rightVerticalLineEndX = right;
		int rightVerticalLineEndY = bottom - (rightArcRight - rightArcLeft) /2;

		int rightBottomArcTop = bottom - (rightArcRight - rightArcLeft);
		int rightBottomArcLeft = right - (rightArcRight - rightArcLeft);
		int rightBottomArcRight = right;
		int rightBottomArcBottom = bottom;

		if (cent > STAGE_1) {
			pathRight.moveTo(rightVerticalLineStartX, rightVerticalLineStartY);
			
			if (cent >= (STAGE_1 + STAGE_2)) {
				pathRight.lineTo(rightVerticalLineEndX, rightVerticalLineEndY);

				pathRight.lineTo(rightVerticalLineEndX, rightBottomArcTop
						+ (rightBottomArcBottom - rightBottomArcTop) / 2);

				// add connect line
//				pathRight.moveTo(rightBottomArcRight, rightBottomArcBottom);

				arcRightBottom.top = rightBottomArcTop;
				arcRightBottom.left = rightBottomArcLeft;
				arcRightBottom.right = rightBottomArcRight;
				arcRightBottom.bottom = rightBottomArcBottom;
				pathRight.addArc(arcRightBottom, 0.0F, 90.0F);

			} else {
				pathRight
						.lineTo(rightVerticalLineEndX,
								rightVerticalLineStartY
										+ ((rightVerticalLineEndY - rightVerticalLineStartY) * ((cent - STAGE_1) / STAGE_2)));
			}
		}

		int bottomLineStartX = rightBottomArcLeft
				+ (rightBottomArcRight - rightBottomArcLeft) / 2;
		int bottomLineStartY = bottom;
		int bottomLineEndX = midX;
		int bottomLineEndY = bottom;

		if (cent > (STAGE_1 + STAGE_2)) {
			pathRight.moveTo(bottomLineStartX, bottomLineStartY);
			if (cent >= (STAGE_1 + STAGE_2 + STAGE_3)) {
				pathRight.lineTo(bottomLineEndX, bottomLineEndY);
			} else {
				pathRight.lineTo(bottomLineStartX + (bottomLineEndX - bottomLineStartX) * ((cent - (STAGE_1 + STAGE_2)) / STAGE_3),
						bottomLineEndY);
			}
		}

	}
	
	
	private void preparedLine1(int left, int top, int right, int bottom,
			int midX, int midY) {
		
		int startX = right + (getRight() - right - MARGIN) / 3 * 2;
		int startY = top + (bottom - top) / 8 ;
		
		int endX = right;
		int endY = top + (bottom - top) / 8 * 2;
		
		line1.reset();
		
		if (cent > STAGE_1) {
			line1.moveTo(startX, startY);
			if (cent >= (STAGE_1 + STAGE_2)) {
				line1.lineTo(endX, endY);
			} else {
				line1.lineTo(endX, startY + (endY - startY) * ((cent- (STAGE_1 + STAGE_2)) / STAGE_3));
			}
		}
		
	}
	
	private void preparedLine2(int left, int top, int right, int bottom,
			int midX, int midY) {
		
		int startX = right + (getRight() - right - MARGIN) / 3 * 2;
		int startY = top + (bottom - top) / 8 ;
		
		int endX = startX;
		int endY = top + (bottom - top) / 8 * 6;
		
		line2.reset();
		
		line2.moveTo(startX, startY);
		if (cent >= STAGE_1) {
			line2.lineTo(endX, endY);
		} else {
			line2.lineTo(endX, startY + (endY - startY) * (cent / STAGE_2));
		}
	}
	
	private void preparedLine3(int left, int top, int right, int bottom,
			int midX, int midY) {
		
		int startX = right + (getRight() - right - MARGIN) / 3 * 2;
		int startY = top + (bottom - top) / 8 * 6 ;
		
		int endX = right;
		int endY = top + (bottom - top) / 8 * 4;
		
		line3.reset();
		
		if (cent > STAGE_1) {
			line3.moveTo(startX, startY);
			
			if (cent >= (STAGE_1 + STAGE_2)) {
				line3.lineTo(endX, endY);
			} else {
				line3.lineTo(endX, startY + (endY - startY) * ((cent- (STAGE_1 + STAGE_2)) / STAGE_3));
			}
		}
		
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int left = MARGIN;
		int top = MARGIN;
		int bottom = getBottom()  - MARGIN;
		int right = (left + getRight() / 3 * 2)   - MARGIN;
		
		int midX = (right - left) / 2;
		int midY = (bottom - top) / 2;
		
		
		prepareLeftPart(left, top, right, bottom, midX, midY);

		prepareRightPart(left, top, right, bottom, midX, midY);
		
		preparedLine1(left, top, right, bottom, midX, midY);
		
		preparedLine2(left, top, right, bottom, midX, midY);
		
		preparedLine3(left, top, right, bottom, midX, midY);

		canvas.drawPath(pathLeft, p);

		canvas.drawPath(pathRight, p);
		
		canvas.drawPath(line1, p);
		
		canvas.drawPath(line2, p);
		
		canvas.drawPath(line3, p);

	}

}
