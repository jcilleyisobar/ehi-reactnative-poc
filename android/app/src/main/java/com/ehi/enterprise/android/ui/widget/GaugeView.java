package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.rewards.widget.EHIArchGeometry;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class GaugeView extends View {
    private RectF innerArch = new RectF();
    private RectF outerArch = new RectF();
    private Paint mPaint;
    private float mInnerArchPercentage = 0;
    private @ColorRes int mSectionColor = R.color.ehi_table_cell_color;
    private @ColorRes int mLineColor = R.color.light_grey;
    private @ColorRes int mInnerArchBackgroundColor = R.color.light_grey;
    private @ColorRes int mGrayBackgroundColor = R.color.white_grey;
    private @ColorRes int mFillColor;

    private static int INNER_POINT_CONSTANT = 20; //just a placeholder, constant will be defined getting value from dimens
    private static int OUTER_POINT_CONSTANT = 40;
    private static int MINIMUM_VIEW_HEIGHT = 150;
    private static int BETWEEN_LINE_ARCH_CONSTANT = 15;
    private static int OUTER_LINE_ARCH_CONSTANT = 10;

    private int mRadius;
    private int mNumberOfSections;
    private int mFilledSections;
    private boolean mAreSectionsVisible = true;

    public GaugeView(Context context) {
        super(context);
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        INNER_POINT_CONSTANT = (int) getResources().getDimension(R.dimen.inner_point_constant);
        OUTER_POINT_CONSTANT = (int) getResources().getDimension(R.dimen.outer_point_constant);
        MINIMUM_VIEW_HEIGHT = (int) getResources().getDimension(R.dimen.minimum_gauge_size);
        BETWEEN_LINE_ARCH_CONSTANT = (int) getResources().getDimension(R.dimen.between_arch_line_constant);
        OUTER_LINE_ARCH_CONSTANT = (int) getResources().getDimension(R.dimen.outer_arch_line_constant);
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFillColor(@ColorRes int color) {
        mFillColor = color;
    }

    public void setFilledSections(int filledSections) {
        mFilledSections = filledSections;
    }

    public void setNumberOfSections(int numberOfSections) {
        mNumberOfSections = numberOfSections;
    }

    public void setAreSectionsVisible(boolean areSectionsVisible) {
        mAreSectionsVisible = areSectionsVisible;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int deviceWidth = DisplayUtils.getScreenWidth(getContext());
        final int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int gaugeHeight;
        if (screenWidth == deviceWidth && (screenWidth / 4) > MINIMUM_VIEW_HEIGHT) {
            //case where we have one  single gauge
            gaugeHeight = screenWidth / 4;
        } else if (screenWidth < deviceWidth ) {
            //this is the case where whe have two gauges in one linear layout
            gaugeHeight = screenWidth / 2;
        } else {
            //case where the screen width is too small
            gaugeHeight = MINIMUM_VIEW_HEIGHT;
        }
        setMeasuredDimension(screenWidth, gaugeHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mNumberOfSections == 0) {
            return;
        }
        final int strokeWidth = (int) getResources().getDimension(R.dimen.arch_stroke_width);
        final int strokeOuterArchWidth = (int) getResources().getDimension(R.dimen.arch_stroke_outer_arch);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);

        final int archExtraSpace = strokeWidth + OUTER_POINT_CONSTANT;
        mRadius =  canvas.getHeight() - archExtraSpace;

        final int yCoordinate = canvas.getHeight();
        final int xCoordinate = getXCoordinate(canvas, mRadius);
        final Point centerPoint = new Point(xCoordinate, yCoordinate);

        //drawing inner arch
        drawInnerArch(canvas, mFilledSections, mNumberOfSections, centerPoint, mRadius);

        //drawing outer arch
        mPaint.setStrokeWidth(strokeWidth/2);
        final int outerRadius = mRadius + strokeWidth;
        drawArch(canvas, centerPoint, outerRadius, mGrayBackgroundColor);
        mPaint.setStrokeWidth(strokeOuterArchWidth);

        //drawing arch lines
        final int betweenArchLineRadius = mRadius + BETWEEN_LINE_ARCH_CONSTANT;
        final int outerArchLineRadius = outerRadius + OUTER_LINE_ARCH_CONSTANT;
        drawArch(canvas, centerPoint, outerArchLineRadius, mLineColor);
        drawArch(canvas, centerPoint, betweenArchLineRadius, mLineColor);

        //drawing segments
        if (mAreSectionsVisible) {
            drawSections(canvas, mRadius, mRadius + strokeWidth, centerPoint, mNumberOfSections);
        }
    }

    private int getXCoordinate(Canvas canvas, int radius) {
        return ((canvas.getHeight() < canvas.getWidth())
                ? canvas.getWidth() / 2
                : radius);
    }

    private void drawArch(Canvas canvas, Point centerPoint, int radius, @ColorRes int color) {
        mPaint.setColor(getContext().getResources().getColor(color));
        outerArch.set(centerPoint.x - radius, centerPoint.y - radius, centerPoint.x + radius, centerPoint.y + radius);
        canvas.drawArc(outerArch, 180, 180, false, mPaint);
    }

    private void drawInnerArch(Canvas canvas, int filledSections, int numberOfSections, Point centerPoint, int archRadius) {
        EHIArchGeometry innerArchGeometry = new EHIArchGeometry(centerPoint, archRadius, numberOfSections);
        innerArch.set(centerPoint.x - archRadius, centerPoint.y - archRadius, centerPoint.x + archRadius, centerPoint.y + archRadius);

        mPaint.setColor(getContext().getResources().getColor(mInnerArchBackgroundColor));
        canvas.drawArc(innerArch, 180, 180, false, mPaint);
        mPaint.setColor(getContext().getResources().getColor(mFillColor));
        canvas.drawArc(innerArch, 180, innerArchGeometry.getSectionAngle(filledSections) * mInnerArchPercentage, false, mPaint);
    }

    private void drawSections(Canvas canvas, int innerRadius, int outerRadius, Point centerPoint, int numberOfSections) {
        mPaint.setColor(getContext().getResources().getColor(mSectionColor));
        Point[] innerPointsArray = new EHIArchGeometry(centerPoint, innerRadius - INNER_POINT_CONSTANT, numberOfSections).getArchPoints();
        Point[] outterPointsArray = new EHIArchGeometry(centerPoint, outerRadius + OUTER_POINT_CONSTANT, numberOfSections).getArchPoints();
        for (int i = 0; i < innerPointsArray.length; i++) {
            canvas.drawLine(innerPointsArray[i].x, innerPointsArray[i].y, outterPointsArray[i].x, outterPointsArray[i].y, mPaint);
        }
    }

    public void setInnerArchPercentage(float percentage) {
        mInnerArchPercentage = percentage;
        invalidate();
    }
}
