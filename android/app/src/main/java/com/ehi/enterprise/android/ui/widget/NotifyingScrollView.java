package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


/**
 * @author Cyril Mottier
 */
public class NotifyingScrollView extends ScrollView {
    private boolean mScrollable = true;

	/**
	 * @author Cyril Mottier
	 */
	public interface OnScrollChangedListener {
		void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public NotifyingScrollView(Context context) {
		super(context);
	}

	public NotifyingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return mScrollable && super.onInterceptTouchEvent(ev);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		mOnScrollChangedListener = listener;
	}

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    public boolean isScrollable() {
        return mScrollable;
    }
}