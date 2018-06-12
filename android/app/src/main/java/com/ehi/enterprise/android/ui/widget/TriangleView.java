package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class TriangleView extends View {
    final Path mPath = new Path();
    final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final int mBackgroundColor;
    final float mStrokeWidth;

    public TriangleView(Context context) {
        super(context);
        mBackgroundColor = ContextCompat.getColor(context, R.color.white);
        mStrokeWidth = DisplayUtils.dipToPixels(context, 1);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBackgroundColor = ContextCompat.getColor(context, R.color.white);
        mStrokeWidth = DisplayUtils.dipToPixels(context, 1);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundColor = ContextCompat.getColor(context, R.color.white);
        mStrokeWidth = DisplayUtils.dipToPixels(context, 1);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        mPath.moveTo(0, 0);
        mPath.lineTo(w / 2, h);
        mPath.lineTo(w, 0);

        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPoint(w / 2, h, mPaint);
    }
}
