package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.utils.DisplayUtils;

public class CrossView extends View {

    private int mCrossX = 0;
    private int mCrossY = 0;
    private int mResetX = 0;
    private int mResetY = 0;
    private int mCrossLineHeight = 0;
    private int mCrossLineWidth = 0;
    private float mCrossStrokeWidth = 0;
    @ColorRes
    private int mBackgroundC = 0;
    private Paint mFillPaint;
    private Path mPath;
    private int mStrokeColor = 0;
    private boolean mUnmeassuredInit = false;

    public CrossView(Context context) {
        super(context);
        init();
    }

    public CrossView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CrossView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPath = new Path();
    }

    /**
     * Used if you have a preset height/width for the layout
     * @param strokeColor
     */
    public void initAll(@ColorRes int strokeColor, int strokeWidth){
        mCrossStrokeWidth = (int) DisplayUtils.dipToPixels(getContext(), strokeWidth);
        mStrokeColor = strokeColor;
        mUnmeassuredInit = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mUnmeassuredInit && getMeasuredHeight() != 0 && getMeasuredWidth() != 0){
            mUnmeassuredInit = false;
            int shortest = getMeasuredHeight();
            shortest = shortest < getMeasuredWidth() ? shortest : getMeasuredWidth();
            initAll((int)(shortest/2 - mCrossStrokeWidth/2), 0, shortest, shortest, mCrossStrokeWidth, 0, mStrokeColor);
        }
    }

    public void initAll(int crossX, int crossY, int crossLineHeight, int crossLineWidth,
                        float crossStrokeWidth, @ColorRes int backgroundC, @ColorRes int strokeColor){
        mStrokeColor = strokeColor;
        initAll(crossX, crossY, crossLineHeight, crossLineWidth, crossStrokeWidth, backgroundC);
    }

    /**
     *
     * @param crossX Reperesents the left edge of the middle line
     * @param crossY Represents the starting point of the cross in terms of Y axis
     * @param crossLineHeight height of cross
     * @param crossLineWidth width of cross
     * @param crossStrokeWidth
     * @param backgroundC
     */
    public void initAll(int crossX, int crossY, int crossLineHeight, int crossLineWidth,
                        float crossStrokeWidth, @ColorRes int backgroundC){
        mCrossLineHeight = crossLineHeight;
        mCrossLineWidth = crossLineWidth;
        mCrossX = crossX;
        mCrossY = crossY;
        mResetX = crossX;
        mResetY = crossY;
        mBackgroundC = backgroundC;
        mCrossStrokeWidth = crossStrokeWidth;
        mFillPaint = new Paint();

        if(mBackgroundC != 0) {
            mFillPaint.setColor(getResources().getColor(mBackgroundC));
        }
        else if(mStrokeColor != 0){
            mFillPaint.setColor(getResources().getColor(mStrokeColor));
        }
        invalidate();
    }

    public void translateCrossX(int amount){
        mCrossX = mResetX + amount;
        invalidate();
    }

    public int getCrossX() {
        return mCrossX;
    }

    public int getResetX() {
        return mResetX;
    }

    public int getResetY() {
        return mResetY;
    }

    public int getCrossY() {
        return mCrossY;
    }

    public void translateCrossY(int amount){
        mCrossY = mResetY + amount;
        invalidate();
    }

    public void setBackgroundC(int backgroundC) {
        mBackgroundC = backgroundC;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mBackgroundC == 0 && mStrokeColor == 0){
            return;
        }

        mPath.rewind();
        if(mBackgroundC != 0) {
            mPath.addRect(0, 0, canvas.getWidth(), canvas.getHeight(), Path.Direction.CW);

            mPath.close();

        }

        mPath.addRect(mCrossX, mCrossY,
                mCrossX + mCrossStrokeWidth,
                mCrossY + mCrossLineHeight, Path.Direction.CW);

        int middleX = mCrossX - (mCrossLineWidth / 2) + (int) (mCrossStrokeWidth / 2);
        int middleY = mCrossY + (mCrossLineHeight / 2) - (int) (mCrossStrokeWidth / 2);

        mPath.addRect(middleX, middleY,
                middleX + (mCrossLineWidth / 2) - (mCrossStrokeWidth / 2),
                middleY + mCrossStrokeWidth, Path.Direction.CW);

        mPath.addRect(middleX + (mCrossLineWidth / 2) + (mCrossStrokeWidth / 2),
                middleY,
                middleX + mCrossLineWidth,
                middleY + mCrossStrokeWidth, Path.Direction.CW);

        mPath.close();

        if (mStrokeColor == 0) {
            mPath.setFillType(Path.FillType.EVEN_ODD);
        }


        canvas.drawPath(mPath, mFillPaint);
    }
}
