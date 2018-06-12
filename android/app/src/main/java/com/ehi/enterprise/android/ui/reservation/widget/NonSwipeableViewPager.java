package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.ehi.enterprise.android.utils.DLog;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {

	private static final String TAG = NonSwipeableViewPager.class.getSimpleName();

	public NonSwipeableViewPager(Context context) {
		super(context);
		init();
	}

	public NonSwipeableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// Never allow swiping to switch between pages
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Never allow swiping to switch between pages
		return false;
	}

	private void init() {
		try {
			Field mScroller;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), new DecelerateInterpolator());
			// scroller.setFixedDuration(5000);
			mScroller.set(this, scroller);
		} catch (NoSuchFieldException e) {
			DLog.w(TAG, e);
		} catch (IllegalArgumentException e) {
			DLog.w(TAG, e);
		} catch (IllegalAccessException e) {
			DLog.w(TAG, e);
		}
	}

	private static class FixedSpeedScroller extends Scroller {

		private int mDuration = 350;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}


		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}
}
