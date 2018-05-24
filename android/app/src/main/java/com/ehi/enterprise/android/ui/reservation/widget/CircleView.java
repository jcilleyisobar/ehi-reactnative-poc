package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    private Paint mPaint = new Paint();
    private float mRadius;
    private Pair<Float, Float> mXY = null;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mXY != null){
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(mXY.first, mXY.second, mRadius, mPaint);
        }
    }


    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public void setXY(Pair<Float, Float> XY) {
        mXY = XY;
    }

}