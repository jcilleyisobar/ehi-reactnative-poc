package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ehi.enterprise.android.R;

public class RewardsProgressView extends FrameLayout {
    private int mColor;
    RectF arch = new RectF();
    private Float mPercent = 0f;
    private Paint mPaint;

    public RewardsProgressView(Context context) {
        super(context);
    }

    public RewardsProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray defaultTypedArray = getContext().obtainStyledAttributes(attrs,
                new int[]{android.R.attr.background});

        setWillNotCacheDrawing(false);
        mColor = defaultTypedArray.getColor(0, Color.TRANSPARENT);
        defaultTypedArray.recycle();
        mPaint = new Paint();
    }

    public RewardsProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setPercent(Float percent) {
        mPercent = percent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(getResources().getColor(R.color.ehi_primary));

        int strokeWidth = 25;
        int radius = ((canvas.getHeight() < canvas.getWidth())
                ? canvas.getHeight()
                : canvas.getWidth());


        int width = canvas.getWidth() / 2 + radius;

        int x = canvas.getWidth() / 2 - radius;
        int y = 0;
        arch.set(x, y + strokeWidth, width, radius *2);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);

        canvas.drawArc(arch, 180, 180 * mPercent, false, mPaint);
        mPaint.setColor(getResources().getColor(R.color.ehi_grey_header_bg));
        canvas.drawArc(arch, 180 + (180 * mPercent), 180 * (1 - mPercent), false, mPaint);

    }
}
