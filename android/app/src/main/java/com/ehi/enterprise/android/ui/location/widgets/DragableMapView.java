package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ehi.enterprise.android.ui.location.interfaces.IMapDragDelegate;
import com.google.android.m4b.maps.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class DragableMapView extends MapView {

	private static final int DELAY = 350;

	private IMapDragDelegate mListener;
	private boolean mIsScrolling = false;
	private GestureDetector mGestureDetector;

	private Timer mTimer;
	private DelayDragTask mDelayedStopDrag;

	private class DelayDragTask extends TimerTask {

		@Override
		public void run() {
			post(new Runnable() {
				@Override
				public void run() {
					if (mListener != null) {
						mListener.onMapStopDrag();
					}
				}
			});
		}
	}

	public DragableMapView(Context context) {
		super(context);
		init();
	}

	public DragableMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragableMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(getContext(), new ScrollGestureDetector());
		mTimer = new Timer("ehi.timer");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (mGestureDetector.onTouchEvent(ev)) {
			return super.dispatchTouchEvent(ev);
		}

		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (mIsScrolling && mListener != null) {
					if (mDelayedStopDrag != null) {
						mDelayedStopDrag.cancel();
						mDelayedStopDrag = null;
					}
					mDelayedStopDrag = new DelayDragTask();
					mTimer.schedule(mDelayedStopDrag, DELAY);
				}
				mIsScrolling = false;
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public void setMapTouchListener(IMapDragDelegate listener) {
		mListener = listener;
	}

	private class ScrollGestureDetector extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mListener.onMapTouch(e);
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (!mIsScrolling && mListener != null) {
				mListener.onMapStartDrag();
				if (mDelayedStopDrag != null) {
					mDelayedStopDrag.cancel();
					mDelayedStopDrag = null;
				}
			}
			mIsScrolling = true;
			return true;
		}
	}
}
