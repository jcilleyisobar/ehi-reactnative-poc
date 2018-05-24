package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class ExpandingHeaderview extends LinearLayout {

    private static final long ANIMATION_DURATION = 500;
    private int mExpandingID;
    private int mHeaderID;
    private View mExpandingView;
    private View mHeader;
    private boolean mExpanded = false;
    private boolean mDisabled = false;
    private IExpandingHeaderViewCallBack mIExpandingHeaderViewCallBack = null;
    private OnClickListener mExpandingClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mDisabled) {
                return;
            }
            toggleView();
        }
    };


    private int mOriginalHeight;
    private int mExpandedHeight = 0;

    public ExpandingHeaderview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public ExpandingHeaderview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Context context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandingHeaderview, 0, 0);

        try {
            mHeaderID = array.getResourceId(R.styleable.ExpandingHeaderview_headerViewId, 0);
            mExpandingID = array.getResourceId(R.styleable.ExpandingHeaderview_expandingViewId, 0);
        } finally {
            array.recycle();
        }
        setOrientation(VERTICAL);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(new Runnable() {
            @Override
            public void run() {
                initViews();
            }
        });
    }

    private void initViews() {
        if (mExpandingID != 0) {
            mHeader = findViewById(mHeaderID);
            mExpandingView = findViewById(mExpandingID);
        }
        else {
            mHeader = getChildAt(0);
            mExpandingView = getChildAt(1);
        }

        mExpandingView.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenHeight(getContext()), View.MeasureSpec.AT_MOST));

        mExpandedHeight = mExpandingView.getMeasuredHeight();

        mHeader.setOnClickListener(mExpandingClickListener);
        mOriginalHeight = getHeight();
    }

    public void setExpandListener(IExpandingHeaderViewCallBack callBack) {
        mIExpandingHeaderViewCallBack = callBack;
    }

    public boolean isExpanded() {
        return mExpanded;
    }



    private void toggleView() {
        final int difference = (mExpanded ? mOriginalHeight : (mExpandedHeight + mHeader.getHeight())) - getHeight();

        final int startingHeight = getHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewGroup.LayoutParams params = getLayoutParams();

                params.height = startingHeight + (int) (interpolatedTime * difference);
                setLayoutParams(params);
                invalidate();
                if(mIExpandingHeaderViewCallBack != null){
                    mIExpandingHeaderViewCallBack.onViewChange(ExpandingHeaderview.this, interpolatedTime);
                }
            }
        };
        animation.setDuration(ANIMATION_DURATION);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIExpandingHeaderViewCallBack.onCollapseExpandAnimationEnd(ExpandingHeaderview.this);
                mDisabled = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mHeader.startAnimation(animation);

        if (mIExpandingHeaderViewCallBack != null) {
            mIExpandingHeaderViewCallBack.onViewStartChange(mExpanded, ExpandingHeaderview.this);
        }

        mExpanded = !mExpanded;
    }

    public void disableAndClose() {
        mDisabled = true;
        toggleView();
    }

    public interface IExpandingHeaderViewCallBack {
        void onViewStartChange(boolean expanded, ExpandingHeaderview headerview);
        void onViewChange(ExpandingHeaderview headerview, float inerpolation);
        void onCollapseExpandAnimationEnd(ExpandingHeaderview headerview);
    }
}
