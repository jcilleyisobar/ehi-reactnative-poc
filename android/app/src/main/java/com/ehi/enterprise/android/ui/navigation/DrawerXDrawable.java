/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ehi.enterprise.android.ui.navigation;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;

import com.ehi.enterprise.android.utils.DisplayUtils;

/**
 * A drawable that can draw a "Drawer hamburger" menu or an Arrow and animate between them.
 */
abstract class DrawerXDrawable extends Drawable {

	private static final String TAG = DrawerXDrawable.class.getSimpleName();
	public static final float BAR_GAP = 3.0f;
	public static final float BAR_THICKNESS = 2.0f;
	public static final float TOP_BOTTOM_ARROW_SIZE = 17.4f;
	public static final float BAR_SIZE = 18f;
	public static final int SIZE = 24;
	// The angle in degrees that the arrow head is inclined at.
	private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45);
	public static final boolean SPIN = true;

	private final Paint mPaint = new Paint();

	private final float mBarThickness;
	// The length of top and bottom bars when they merge into an arrow
	private final float mTopBottomArrowSize;
	// The length of middle bar
	private final float mBarSize;
	// The space between bars when they are parallel
	private final float mBarGap;
	// Whether bars should spin or not during progress
	private final boolean mSpin;
	// Use Path instead of canvas operations so that if color has transparency, overlapping sections
	// wont look different
	private final Path mPath = new Path();
	private final Path mMiddlePath = new Path();
	// The reported intrinsic size of the drawable.
	private final int mSize;
	private final Paint mMiddlePaint = new Paint();
	// Whether we should mirror animation when animation is reversed.
	private boolean mVerticalMirror = true;
	// The interpolated version of the original progress
	private float mProgress;

	/**
	 * @param context used to get the configuration for the drawable from
	 */
	DrawerXDrawable(Context context) {
		final TypedArray typedArray = context.getTheme()
				.obtainStyledAttributes(null, R.styleable.DrawerArrowToggle,
						R.attr.drawerArrowStyle,
						R.style.Base_Widget_AppCompat_DrawerArrowToggle);
		mPaint.setAntiAlias(true);
		mPaint.setColor(typedArray.getColor(R.styleable.DrawerArrowToggle_color, 0));
		mMiddlePaint.setAntiAlias(true);
		mMiddlePaint.setColor(typedArray.getColor(R.styleable.DrawerArrowToggle_color, 0));

		mSize = (int) DisplayUtils.dipToPixels(context, SIZE);
		mBarSize = DisplayUtils.dipToPixels(context, BAR_SIZE);
		mTopBottomArrowSize = DisplayUtils.dipToPixels(context, TOP_BOTTOM_ARROW_SIZE);
		mBarThickness = DisplayUtils.dipToPixels(context, BAR_THICKNESS);
		mBarGap = DisplayUtils.dipToPixels(context, BAR_GAP);
		mSpin = SPIN;
		typedArray.recycle();

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setStrokeWidth(mBarThickness);

		mMiddlePaint.setStyle(Paint.Style.STROKE);
		mMiddlePaint.setStrokeJoin(Paint.Join.ROUND);
		mMiddlePaint.setStrokeCap(Paint.Cap.SQUARE);
		mMiddlePaint.setStrokeWidth(mBarThickness);
	}

	abstract boolean isLayoutRtl();

	/**
	 * If set, canvas is flipped when progress reached to end and going back to start.
	 */
	protected void setVerticalMirror(boolean verticalMirror) {
		mVerticalMirror = verticalMirror;
	}

	@Override
	public void draw(Canvas canvas) {
		Rect bounds = getBounds();
		final boolean isRtl = isLayoutRtl();

		// Interpolated widths of arrow bars
		final float arrowSize = lerp(mBarSize, mTopBottomArrowSize, mProgress);

		// Interpolated size of middle bar
		final float middleBarSize = lerp(mBarSize, 0, mProgress) / 2;

		// The rotation of the top and bottom bars (that make the arrow head)
		final float rotation = lerp(0, ARROW_HEAD_ANGLE, mProgress);

		// The whole canvas rotates as the transition happens
		final float canvasRotate = lerp(isRtl ? 0 : -180, isRtl ? 180 : 0, mProgress);

		float distanceFromCenter = mBarGap + mBarThickness + (mBarThickness / 2);
		final float topBar = lerp(distanceFromCenter, -distanceFromCenter, mProgress);
		final float bottomBar = lerp(-distanceFromCenter, distanceFromCenter, mProgress);

		int middlePaintAlpha = (int) lerp(255, 0, mProgress * 2);
		if (middlePaintAlpha < 0) {
			middlePaintAlpha = 0;
		}

		mPath.rewind();
		mMiddlePath.rewind();

		final float arrowEdge = -(mBarSize / 2);

		if (middleBarSize > 0) {
			// draw middle bar's first half
			mMiddlePath.moveTo(0, 0);
			mMiddlePath.rLineTo(-middleBarSize, 0);

			// draw middle bar's second half
			mMiddlePath.moveTo(0, 0);
			mMiddlePath.rLineTo(middleBarSize, 0);
		}

		final float arrowWidth = Math.round(arrowSize * Math.cos(rotation));
		final float arrowHeight = Math.round(arrowSize * Math.sin(rotation));

		// top bar
		mPath.moveTo(arrowEdge, topBar);
		mPath.rLineTo(arrowWidth, arrowHeight);

		// bottom bar
		mPath.moveTo(arrowEdge, bottomBar);
		mPath.rLineTo(arrowWidth, -arrowHeight);
		mPath.close();

		canvas.save();
		// Rotate the whole canvas if spinning, if not, rotate it 180 to get
		// the arrow pointing the other way for RTL.
		if (mSpin) {
			canvas.rotate(canvasRotate * ((mVerticalMirror ^ isRtl) ? -1 : 1),
					bounds.centerX(), bounds.centerY());
		}
		else if (isRtl) {
			canvas.rotate(180, bounds.centerX(), bounds.centerY());
		}
		canvas.translate(bounds.centerX(), bounds.centerY());

		mMiddlePaint.setAlpha(middlePaintAlpha);
		canvas.drawPath(mMiddlePath, mMiddlePaint);
		canvas.drawPath(mPath, mPaint);

		canvas.restore();
	}

	@Override
	public void setAlpha(int i) {
		mPaint.setAlpha(i);
	}

	// override
	public boolean isAutoMirrored() {
		// Draws rotated 180 degrees in RTL mode.
		return true;
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		mPaint.setColorFilter(colorFilter);
	}

	@Override
	public int getIntrinsicHeight() {
		return mSize;
	}

	@Override
	public int getIntrinsicWidth() {
		return mSize;
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	public float getProgress() {
		return mProgress;
	}

	public void setProgress(float progress) {
		mProgress = progress;
		invalidateSelf();
	}

	/**
	 * Linear interpolate between a and b with parameter t.
	 */
	private static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

}