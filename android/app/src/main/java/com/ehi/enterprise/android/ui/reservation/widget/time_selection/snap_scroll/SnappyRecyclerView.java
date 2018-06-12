package com.ehi.enterprise.android.ui.reservation.widget.time_selection.snap_scroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SnappyRecyclerView extends RecyclerView {

    private static final String TAG = "SnappyRecyclerView";

    private Canvas mChildCanvas;
    private Bitmap mChildBitmap;
    private Paint mPaint;
    private Path mUpRect;
    private Path mDownRect;

    // "caching" control
    private int mCachedWidth;
    private int mCachedHeight;

    private boolean mTouchEnabled = true;
    private int mCutSize;

    public SnappyRecyclerView(Context context) {
        super(context);
    }

    public SnappyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnappyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mChildBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mChildCanvas = new Canvas(mChildBitmap);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAlpha(0xFF);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
    }

    public void setCutToSize(int size) {
        mCutSize = size;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        final LayoutManager lm = getLayoutManager();

        if (lm instanceof ISnappyLayoutManager) {
            super.smoothScrollToPosition(((ISnappyLayoutManager) getLayoutManager())
                    .getPositionForVelocity(velocityX, velocityY));
            return true;
        }
        return super.fling(velocityX, velocityY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return mTouchEnabled && super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // We want the parent to handle all touch events--there's a lot going on there,
        // and there is no reason to overwrite that functionality--bad things will happen.
        final boolean ret = super.onTouchEvent(e);
        final LayoutManager lm = getLayoutManager();

        if (lm instanceof ISnappyLayoutManager
                && (e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_CANCEL)
                && getScrollState() == SCROLL_STATE_IDLE) {
            // The layout manager is a SnappyLayoutManager, which means that the
            // children should be snapped to a grid at the end of a drag or
            // fling. The motion event is either a user lifting their finger or
            // the cancellation of a motion events, so this is the time to take
            // over the scrolling to perform our own functionality.
            // Finally, the scroll state is idle--meaning that the resultant
            // velocity after the user's gesture was below the threshold, and
            // no fling was performed, so the view may be in an unaligned state
            // and will not be flung to a proper state.

            smoothScrollToPosition(((ISnappyLayoutManager) lm).getFixScrollPos());
        }

        return mTouchEnabled && ret;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mCutSize == 0) {
            super.dispatchDraw(canvas);
            return;
        }

        if (mChildCanvas == null) {
            init();
        }

        mChildCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        super.dispatchDraw(mChildCanvas);

        mChildCanvas.save();

        final int width = getWidth();
        final int height = getHeight();

        if (mCachedWidth != width || mCachedHeight != height) {
            mCachedWidth = width;
            mCachedHeight = height;

            mUpRect = new Path();
            mUpRect.addRect(
                    0,
                    0,
                    width,
                    height / 2 - mCutSize / 2,
                    Path.Direction.CW
            );
            mUpRect.setFillType(Path.FillType.EVEN_ODD);

            mDownRect = new Path();
            mDownRect.addRect(
                    0,
                    height / 2 + mCutSize / 2,
                    width,
                    height,
                    Path.Direction.CW
            );
            mDownRect.setFillType(Path.FillType.EVEN_ODD);
        }

        mChildCanvas.drawPath(mUpRect, mPaint);
        mChildCanvas.drawPath(mDownRect, mPaint);

        mChildCanvas.restore();

        canvas.drawBitmap(mChildBitmap, 0, 0, null);
    }
}
