package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class NoFirstItemTouchRecyclerView extends RecyclerView {

	private OnTouchListener mOnFirstElementTouchListener;

	private boolean mStartedUnderFirstChild = true;

	public NoFirstItemTouchRecyclerView(Context context) {
		super(context);
	}

	public NoFirstItemTouchRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoFirstItemTouchRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mStartedUnderFirstChild
						&& !isUnderFirstChild(e)) {
					mStartedUnderFirstChild = false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mStartedUnderFirstChild
						&& !isUnderFirstChild(e)) {
					mStartedUnderFirstChild = false;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mStartedUnderFirstChild = true;
				break;
		}
		boolean underFirst = isUnderFirstChild(e);
		if (mStartedUnderFirstChild
				&& underFirst) {
			if (mOnFirstElementTouchListener != null) {
				mOnFirstElementTouchListener.onTouch(null, e);
			}
			return true;
		}
		else {
			return super.onTouchEvent(e);
		}
	}

	public void setOnFirstElementTouchListener(OnTouchListener onFirstElementTouchListener) {
		mOnFirstElementTouchListener = onFirstElementTouchListener;
	}

	private boolean isUnderFirstChild(MotionEvent event) {
        View firstChild = getChildAt(0);
        return (firstChild == null) || ((event.getX() >= firstChild.getLeft())
                && (event.getX() <= firstChild.getRight())
                && (event.getY() <= firstChild.getBottom()));
    }
}
