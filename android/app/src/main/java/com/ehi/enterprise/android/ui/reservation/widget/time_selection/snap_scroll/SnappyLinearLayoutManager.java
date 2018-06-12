package com.ehi.enterprise.android.ui.reservation.widget.time_selection.snap_scroll;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewConfiguration;

import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;

public class SnappyLinearLayoutManager extends LinearLayoutManager implements ISnappyLayoutManager {

	// These variables are from android.widget.Scroller, which is used, via ScrollerCompat, by
	// Recycler View. The scrolling distance calculation logic originates from the same place. Want
	// to use their variables so as to approximate the look of normal Android scrolling.
	// Find the Scroller fling implementation in android.widget.Scroller.fling().
	private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
	private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
	private static final double FRICTION = 0.9;

	private double deceleration;

	private RecyclerView mRecyclerView;

	private boolean mAnimateScroll = true;

	public SnappyLinearLayoutManager(Context context) {
		super(context);
		calculateDeceleration(context);
	}

	public SnappyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
		calculateDeceleration(context);
	}

	public void setAnimateScroll(boolean animateScroll) {
		mAnimateScroll = animateScroll;
	}

	public boolean isAnimateScroll() {
		return mAnimateScroll;
	}

	private void calculateDeceleration(Context context) {
		deceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.3700787 // inches per meter
				// pixels per inch. 160 is the "default" dpi, i.e. one dip is one pixel on a 160 dpi
				// screen
				* context.getResources().getDisplayMetrics().density * 160.0f * FRICTION;
	}

	@Override
	public int getPositionForVelocity(int velocityX, int velocityY) {
		if (getChildCount() == 0) {
			return 0;
		}
		View centerView = getCenterView();
		if (centerView != null) {
			if (getOrientation() == HORIZONTAL) {
				return calcPosForVelocity(velocityX, getChildAt(0).getLeft(), getChildAt(0).getWidth(),
						getPosition(getChildAt(0)));
			}
			else {
				return calcPosForVelocity(velocityY, centerView.getTop(), centerView.getHeight(), getPosition(centerView));
			}
		}
		else {
			return 0;
		}
	}

	private int calcPosForVelocity(int velocity, int scrollPos, int childSize, int currPos) {
		final double v = Math.sqrt(velocity * velocity);
		final double dist = getSplineFlingDistance(v);

		if (velocity < 0) {
			// Not sure if I need to lower bound this here.
			return (int) Math.max(currPos - dist / childSize + 1, 0);
		}
		else {
			return (int) (currPos + dist / childSize);
		}
	}

	@Override
	public void onAttachedToWindow(final RecyclerView view) {
		super.onAttachedToWindow(view);
		mRecyclerView = view;
	}

	@Override public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
		super.onDetachedFromWindow(view, recycler);
		mRecyclerView = null;
	}

	@Override
	public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, int position) {
		smoothScrollToPosition(recyclerView, state, position, mAnimateScroll);
		if (mAnimateScroll) {
			mAnimateScroll = false;
		}
	}

	public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, int position, final boolean animated) {
		final LinearSmoothScroller linearSmoothScroller =
				new LinearSmoothScroller(recyclerView.getContext()) {

					// I want a behavior where the scrolling always snaps to the beginning of
					// the list. Snapping to end is also trivial given the default implementation.
					// If you need a different behavior, you may need to override more
					// of the LinearSmoothScrolling methods.


					@Override
					protected int getHorizontalSnapPreference() {
						return SNAP_TO_START;
					}

					@Override
					protected int getVerticalSnapPreference() {
						return SNAP_TO_START;
					}

					@Override
					protected int calculateTimeForDeceleration(int dx) {
//						if (animated) {
						return (int) Math.ceil(calculateTimeForScrolling(dx) * 3 / .3356);
//						}
//						else {
//							return 1;
//						}
					}


					@Override
					protected int calculateTimeForScrolling(int dx) {
						return super.calculateTimeForScrolling(dx);
					}

					@Override
					public PointF computeScrollVectorForPosition(int targetPosition) {
						return SnappyLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
					}

					@Override
					public int calculateDyToMakeVisible(View view, int snapPreference) {
						final RecyclerView.LayoutManager layoutManager = getLayoutManager();
						if (!layoutManager.canScrollVertically()) {
							return 0;
						}
						final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
								view.getLayoutParams();
						final int top = layoutManager.getDecoratedTop(view) - params.topMargin;
						final int bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin;
						final int start = layoutManager.getPaddingTop();
						final int end = layoutManager.getHeight() - layoutManager.getPaddingBottom();
						return calculateDtToFit(top, bottom, start, end, snapPreference);
					}

					@Override
					public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
						int boxCenter = (boxEnd - boxStart) / 2;
						int viewCenter = viewStart + (viewEnd - viewStart) / 2;
						return boxCenter - viewCenter;
					}
				};
		int inRangePosition = position;
		int overscrollItemsCount = 0;
		if (recyclerView.getAdapter() instanceof IOverscrollAdapter) {
			overscrollItemsCount = ((IOverscrollAdapter) recyclerView.getAdapter()).getOverscrollItemsCount();
		}
		else {
			throw new NotImplementedException();
		}
		if (position < overscrollItemsCount / 2) {
			inRangePosition = overscrollItemsCount / 2;
		}

		if (position > recyclerView.getAdapter().getItemCount() - overscrollItemsCount / 2 - 1) {
			inRangePosition = recyclerView.getAdapter().getItemCount() - overscrollItemsCount / 2 - 1;
		}
		linearSmoothScroller.setTargetPosition(inRangePosition);
		startSmoothScroll(linearSmoothScroller);
	}


	@Override
	public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
		super.onItemsAdded(recyclerView, positionStart, itemCount);
	}

	private double getSplineFlingDistance(double velocity) {
		final double l = getSplineDeceleration(velocity);
		final double decelMinusOne = DECELERATION_RATE - 1.0;
		return ViewConfiguration.getScrollFriction() * deceleration
				* Math.exp(DECELERATION_RATE / decelMinusOne * l);
	}

	private double getSplineDeceleration(double velocity) {
		return Math.log(INFLEXION * Math.abs(velocity)
				/ (ViewConfiguration.getScrollFriction() * deceleration));
	}

	/**
	 * This implementation obviously doesn't take into account the direction of the
	 * that preceded it, but there is no easy way to get that information without more
	 * hacking than I was willing to put into it.
	 */
	@Override
	public int getFixScrollPos() {
		if (this.getChildCount() == 0) {
			return 0;
		}

		final View child = getChildAt(0);
		final int childPos = getPosition(child);

		if (getOrientation() == HORIZONTAL
				&& Math.abs(child.getLeft()) > child.getMeasuredWidth() / 2) {
			// Scrolled first view more than halfway offscreen
			return childPos + 1;
		}
		else if (getOrientation() == VERTICAL) {
			// Scrolled first view more than halfway offscreen
			return getCenterElementPosition();
		}
		return childPos;
	}

	public View getCenterView() {
		int center = getHeight() / 2;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child.getTop() < center
					&& child.getBottom() > center) {
				return child;
			}
		}
		return null;
	}

	public int getCenterElementPosition() {
		View child = getCenterView();
		if (child == null) {
			return 0;
		}
		else {
			return getPosition(child);
		}
	}

}
