package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;

public class ArrowViewGroup extends LinearLayout {

    //region orientations
    @Retention(RetentionPolicy.CLASS)
    @IntDef({TOP, LEFT, RIGHT, BOTTOM})
    public @interface ArrowPosition {
    }

    public static final int TOP = 0;
    public static final int RIGHT = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 3;
    //endregion
    private int mArrowExtensionLength = 0;
    private float mArrowWidth = 20f;
    private float mArrowHeight = 10f;
    private float mArrowGravity = 0.5f;
    @ArrowPosition
    private int mArrowPosition = TOP;
    @ColorRes
    private int mArrowColor = R.color.ehi_primary;
    @ColorRes
    private int mNookColor = android.R.color.transparent;
    @ColorInt
    private int mExteriorColor = R.color.white;
    private float mExteriorPadding = 0f;
    private float mArrowThickness = 1f;
    private int mArrowDirection = 1;
    private float mStrokeWidth = 5;


    //region state_control
    private boolean mViewValid = false;
    private boolean mArrowVisible = true;
    private Pair<Double, Double> mArrowXY = new Pair<>(0d, 0d);
    private Path mArrowPath = new Path();
    private Paint mExteriorPaint = new Paint();
    private Paint mArrowPaint = new Paint();
    private Path mExteriorPath = new Path();
    private Path mExteriorSubtractionPath = new Path();
    //endregion

    public ArrowViewGroup(Context context) {
        super(context);
        init();
    }

    public ArrowViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArrowViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewValid = false;
        mExteriorPadding = mArrowHeight;
        setWillNotDraw(false);
        invalidate();
    }

    private void calculateView() {
        int givenLength = 0;
        float x = 0;
        float y = 0;
        float reducedExtensionLength = mArrowExtensionLength / 2;
        mArrowPaint.setColor(getResources().getColor(mArrowColor));
        mArrowPaint.setStrokeWidth(mStrokeWidth);
        mArrowPaint.setStyle(Paint.Style.STROKE);
        mExteriorPaint.setStyle(Paint.Style.FILL);
        mExteriorPaint.setColor(mExteriorColor);

        mArrowPath.rewind();
        mExteriorPath.rewind();
        Path subtractionPath;


        float reducedArrowWidth = mArrowWidth / 2;
        float innermostPosition = 0;
        switch (mArrowPosition) {
            case TOP:
                givenLength = getArrowLength(false);
                x = getWidth() * mArrowGravity - reducedArrowWidth;
                y = mExteriorPadding + mArrowThickness;
                y++;
                innermostPosition = Math.max(y + (mArrowHeight * mArrowDirection), y);
                mArrowPath.moveTo(x - givenLength / 2, y);
                mArrowPath.lineTo(x, y);
                mArrowPath.lineTo(x + reducedArrowWidth, y + (mArrowHeight * mArrowDirection));
                mArrowPath.lineTo(x + mArrowWidth, y);
                mArrowPath.lineTo(x + givenLength / 2 + mArrowWidth, y);
                subtractionPath = new Path(mArrowPath);

                if (mArrowDirection == 1) {
                    subtractionPath.lineTo(getWidth(), y);
                    subtractionPath.lineTo(getWidth(), innermostPosition);
                    subtractionPath.lineTo(0, innermostPosition);
                    subtractionPath.lineTo(0, y);
                }
                subtractionPath.close();
//
                mExteriorPath.addRect(0, 0, getWidth(), innermostPosition, Path.Direction.CW);
                mExteriorPath.setFillType(Path.FillType.EVEN_ODD);
                mExteriorPath.addPath(subtractionPath);

                break;
            case RIGHT:
                givenLength = getArrowLength(true);
                x = getWidth() - mExteriorPadding - mArrowThickness;
                y = getHeight() * mArrowGravity - reducedArrowWidth;
                x--;
                innermostPosition = Math.min(x, x + (mArrowHeight * -mArrowDirection));

                mArrowPath.moveTo(x, y - givenLength / 2);
                mArrowPath.lineTo(x, y);
                mArrowPath.lineTo(x + (mArrowHeight * -mArrowDirection), y + reducedArrowWidth);
                mArrowPath.lineTo(x, y + mArrowWidth);
                mArrowPath.lineTo(x, y + givenLength / 2 + mArrowWidth);
                subtractionPath = new Path(mArrowPath);
                if (mArrowDirection == 1) {
                    subtractionPath.lineTo(x, getHeight());
                    subtractionPath.lineTo(innermostPosition, getHeight());
                    subtractionPath.lineTo(innermostPosition, 0);
                    subtractionPath.lineTo(x, 0);
                }
                subtractionPath.close();
                mExteriorPath.addRect(innermostPosition, 0, getWidth(), getHeight(), Path.Direction.CW);
                mExteriorPath.setFillType(Path.FillType.EVEN_ODD);
                mExteriorPath.addPath(subtractionPath);

                break;
            case LEFT:
                givenLength = getArrowLength(true);
                x = mExteriorPadding + mArrowThickness;
                y = getHeight() * mArrowGravity - reducedArrowWidth;
                x++;
                innermostPosition = Math.max(x, x + (mArrowHeight * mArrowDirection));
                mArrowPath.moveTo(x, y - givenLength / 2);
                mArrowPath.lineTo(x, y);
                mArrowPath.lineTo(x + (mArrowHeight * mArrowDirection), y + reducedArrowWidth);
                mArrowPath.lineTo(x, y + mArrowWidth);
                mArrowPath.lineTo(x, y + givenLength / 2 + mArrowWidth);

                subtractionPath = new Path(mArrowPath);
                if (mArrowDirection == 1) {
                    subtractionPath.lineTo(x, getHeight());
                    subtractionPath.lineTo(innermostPosition, getHeight());
                    subtractionPath.lineTo(innermostPosition, 0);
                    subtractionPath.lineTo(x, 0);
                }
                subtractionPath.close();
                mExteriorPath.addRect(innermostPosition, 0, getWidth(), getHeight(), Path.Direction.CW);
                mExteriorPath.setFillType(Path.FillType.EVEN_ODD);
                mExteriorPath.addPath(subtractionPath);
                break;
            case BOTTOM:
                givenLength = getArrowLength(false);
                x = getWidth() * mArrowGravity - reducedArrowWidth;
                y = getHeight() - mExteriorPadding - mArrowThickness;
                y--;
                innermostPosition = Math.min(y + (-mArrowHeight * mArrowDirection), y);
                mArrowPath.moveTo(x - givenLength / 2, y);
                mArrowPath.lineTo(x, y);
                mArrowPath.lineTo(x + reducedArrowWidth, y + (-mArrowHeight * mArrowDirection));
                mArrowPath.lineTo(x + mArrowWidth, y);
                mArrowPath.lineTo(x + givenLength / 2 + mArrowWidth, y);
                subtractionPath = new Path(mArrowPath);

                if (mArrowDirection == 1) {
                    subtractionPath.lineTo(getWidth(), y);
                    subtractionPath.lineTo(getWidth(), innermostPosition);
                    subtractionPath.lineTo(0, innermostPosition);
                    subtractionPath.lineTo(0, y);
                }
                subtractionPath.close();
//
                mExteriorPath.addRect(0, getHeight(), getWidth(), innermostPosition, Path.Direction.CW);
                mExteriorPath.setFillType(Path.FillType.EVEN_ODD);
                mExteriorPath.addPath(subtractionPath);
                break;
        }
    }

    private int getArrowLength(boolean height) {
        float max = height ? getHeight() : getWidth() - mArrowWidth;
        if (mArrowExtensionLength > max || mArrowExtensionLength == MATCH_PARENT) {
            return (int) max;
        }
        return mArrowExtensionLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mViewValid) {
            calculateView();
        }
        canvas.drawPath(mExteriorPath, mExteriorPaint);
        canvas.drawPath(mArrowPath, mArrowPaint);
    }

    @Override
    public void invalidate() {
        calculateView();
        super.invalidate();
    }

    //region setters_and_getters

    public ArrowViewGroup setArrowPointedIn(boolean in) {
        mArrowDirection = in ? 1 : -1;
        return this;
    }

    public float getArrowThickness() {
        return mArrowThickness;
    }

    public ArrowViewGroup setArrowThickness(float arrowThickness) {
        mArrowThickness = arrowThickness;
        return this;
    }

    public float getExteriorPadding() {
        return mExteriorPadding;
    }

    public ArrowViewGroup setExteriorPadding(float exteriorPadding) {
        mExteriorPadding = exteriorPadding;
        return this;
    }

    public void setArrowVisibility(boolean visible) {
        mArrowVisible = visible;
    }

    public int getArrowExtensionLength() {
        return mArrowExtensionLength;
    }

    public ArrowViewGroup setArrowExtensionLength(int arrowExtensionLength) {
        mArrowExtensionLength = arrowExtensionLength;
        return this;
    }

    public float getArrowWidth() {
        return mArrowWidth;
    }

    public ArrowViewGroup setArrowWidth(float arrowWidth) {
        mArrowWidth = arrowWidth;
        return this;
    }

    public float getArrowHeight() {
        return mArrowHeight;
    }

    public ArrowViewGroup setArrowHeight(float arrowHeight) {
        mArrowHeight = arrowHeight;
        return this;
    }

    public double getArrowGravity() {
        return mArrowGravity;
    }

    public ArrowViewGroup setArrowGravity(float arrowGravity) {
        mArrowGravity = arrowGravity;
        return this;
    }

    public ArrowViewGroup setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
    }

    public int getArrowPosition() {
        return mArrowPosition;
    }

    public ArrowViewGroup setArrowPosition(@ArrowPosition int arrowOrientation) {
        mArrowPosition = arrowOrientation;
        return this;
    }

    public int getArrowColor() {
        return mArrowColor;
    }

    public ArrowViewGroup setArrowColor(@ColorRes int arrowColor) {
        mArrowColor = arrowColor;
        return this;
    }

    public int getNookColor() {
        return mNookColor;
    }

    public ArrowViewGroup setNookColor(@ColorRes int nookColor) {
        mNookColor = nookColor;
        return this;
    }

    public int getExteriorColor() {
        return mExteriorColor;
    }

    public ArrowViewGroup setExteriorColor(@ColorInt int exteriorColor) {
        mExteriorColor = exteriorColor;
        return this;
    }

    //endregion
}
