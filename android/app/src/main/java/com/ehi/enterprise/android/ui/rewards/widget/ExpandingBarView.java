package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ExpandingViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class ExpandingBarView extends DataBindingViewModelView<ManagersAccessViewModel, ExpandingViewBinding> {

    private static final long ANIMATION_DURATION = 500;
    private static final long ANIMATION_BASE_FADE_IN_DELAY = 500;
    List<WeakReference<View>> mAddedItems;

    private boolean mExpanded = false;
    private boolean mSetup = false;
    private IExpandListener mViewChangedListener = null;
    private List<String> mStrings;
    private int mColor;
    private int mStrokeWidth;
    private boolean mCrossAdded = false;
    private int mLeftCrossBuffer = 0;
    private boolean mDisabled = false;
    private boolean mShowCross;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mDisabled) {
                return;
            }

            if (view == getViewBinding().topBar) {
                toggleView();
                if (mViewChangedListener != null) {
                    mViewChangedListener.viewStartedExpanding(ExpandingBarView.this);
                }
            }
        }
    };


    public ExpandingBarView(Context context) {
        this(context, null, 0);
    }

    public ExpandingBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_expanding_view);
        initView();
    }

    public void setViewChangedListener(IExpandListener viewChangedListener) {
        mViewChangedListener = viewChangedListener;
    }


    public void setupView(CharSequence title, @ColorRes int color, List<String> strings, boolean showCheckMark, boolean showCross) {
        if (mSetup) {
            return;
        }
        mSetup = true;


        mShowCross = showCross;
        getViewBinding().checkMark.setVisibility(showCheckMark ? VISIBLE : GONE);

        getViewBinding().title.setText(title);

        mColor = color;
        mStrings = strings;
        ArrayList<WeakReference<View>> items = new ArrayList<>();
        TextView textView;
        boolean firstAdd = true;
        for (String s : mStrings) {
            textView = new TextView(getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_regular));
            textView.setTextColor(getResources().getColor(R.color.ehi_black));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.rewards_expanding_text_size));
            textView.setLayoutParams(params);
            textView.setText(s);

            int padding = (int) DisplayUtils.dipToPixels(getContext(), 7);
            textView.setPadding(
                    (int) getResources().getDimension(R.dimen.rewards_outer_margin),
                    padding + ((firstAdd) ? padding : 0),
                    0,
                    padding
            );
            items.add(new WeakReference<View>(textView));
            getViewBinding().container.addView(textView);
            firstAdd = false;
        }
        mAddedItems = items;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    private void toggleView() {


        int desiredHeight =
                mExpanded
                        ? getViewBinding().topBar.getHeight()
                        : (DisplayUtils.calculateViewSizeWithText(getViewBinding().container));
        ExpandViewAnimation animation = new ExpandViewAnimation(
                desiredHeight,
                getViewBinding().container,
                mViewChangedListener
        );

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDisabled = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.setDuration(ANIMATION_DURATION);
        getViewBinding().container.startAnimation(
                animation);


        Animation crossAnimation = new CrossAnimation(mExpanded);
        crossAnimation.setDuration(ANIMATION_DURATION);
        crossAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mViewChangedListener != null) {
                    if (!mExpanded) {
                        mViewChangedListener.viewFinishedClosing();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation a = getViewBinding().crossView.getAnimation();
        if (a != null) {
            a.cancel();
        }
        /*mCrossView.startAnimation(crossAnimation); //TODO R1.1 cross animation


        if (mExpanded) {
            mTitle.animate()
                    .translationX(0)
                    .setDuration(ANIMATION_DURATION);
        }
        else {
            mTitle.animate()
                    .translationX(mTopBar.getHeight())
                    .setDuration(ANIMATION_DURATION);
        }
        */
        View view;
        float delay;
        for (int i = 0; i < mAddedItems.size(); i++) {

            view = mAddedItems.get(i).get();
            if (view == null) {
                continue;
            }
            delay = (i / (getViewBinding().container.getChildCount() - 1)) * ANIMATION_DURATION;
            view.animate()
                    .alpha(mExpanded ? 0 : 1)
                    .setStartDelay((long) delay)
                    .setDuration((long) (ANIMATION_DURATION - delay) + ANIMATION_BASE_FADE_IN_DELAY);
        }
        mExpanded = !mExpanded;
    }

    @Override
    public boolean callOnClick() {
        return getViewBinding().topBar.callOnClick();
    }

    public void closeViewTemporaryDisable() {
        if (mExpanded) {
            mDisabled = true;
            toggleView();
        }
    }

    public void openIfCurrent() {
        if (mShowCross) {
            mDisabled = true;
            toggleView();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!mCrossAdded && getViewBinding().topBar.getHeight() != 0) {
                            mCrossAdded = true;
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    int height = getViewBinding().topBar.getHeight();
                                    mStrokeWidth = (int) DisplayUtils.dipToPixels(getContext(), 12);
                                    mLeftCrossBuffer = (int) DisplayUtils.dipToPixels(getContext(), 10);
                                    int leftX;
                                    if (mShowCross) {
                                        leftX = mLeftCrossBuffer + mStrokeWidth;
                                        getViewBinding().title.setTranslationX(getViewBinding().topBar.getHeight());
                                    } else {
                                        leftX = -(height / 2) - (mStrokeWidth / 2) - mLeftCrossBuffer;
                                    }
                                    getViewBinding().crossView.initAll(leftX,
                                            0,
                                            height,
                                            height,
                                            mStrokeWidth,
                                            mColor
                                    );

                                }
                            });
                        }
                    }
                }
        );
    }

    private void initView() {
        getViewBinding().topBar.setOnClickListener(mOnClickListener);
    }

    public class ExpandViewAnimation extends Animation {
        private final int mOriginalHeight;
        private int mDesiredHeight;
        private View mView;
        private IExpandListener mViewChangedListener;
        private boolean mGrowing = false;

        public ExpandViewAnimation(int desiredHeight, View view, @Nullable IExpandListener viewChangedListener) {
            mDesiredHeight = desiredHeight;
            mView = view;
            mViewChangedListener = viewChangedListener;
            mGrowing = mView.getHeight() < mDesiredHeight;
            mOriginalHeight = mView.getHeight();
        }


        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            ViewGroup.LayoutParams params = mView.getLayoutParams();
            int addition = (int) (((mOriginalHeight < mDesiredHeight) ? 1 : -1)
                    * Math.abs(mDesiredHeight - mOriginalHeight) * interpolatedTime);
            params.height = mOriginalHeight + addition;

            mView.setLayoutParams(params);
        }
    }

    public class CrossAnimation extends Animation {

        private final boolean mIsExpanded;
        private int mDistance = 0;
        private float mTotal = 0;

        public CrossAnimation(boolean expanded) {
            int height = getViewBinding().topBar.getHeight();

            if (expanded) {
                mDistance = getViewBinding().crossView.getResetX() - getViewBinding().crossView.getCrossX();
            } else {
                mDistance = height + mStrokeWidth + mLeftCrossBuffer;
            }
            mIsExpanded = expanded;
        }

        private float mLastInterpolatedTime = 0;

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            if (!mIsExpanded
                    && mDistance + getViewBinding().crossView.getResetX() <= getViewBinding().crossView.getCrossX()) {
                return;
            }

            float ratio = interpolatedTime - mLastInterpolatedTime;
            float translateX = getViewBinding().crossView.getCrossX() - getViewBinding().crossView.getResetX();

            translateX += mDistance * ratio;

            getViewBinding().crossView.translateCrossX((int) translateX);
            mTotal += mDistance * ratio;
            mLastInterpolatedTime = interpolatedTime;

            if (mViewChangedListener != null) {
                mViewChangedListener.onViewChanged(ExpandingBarView.this, ratio);
            }
        }

    }

    public interface IExpandListener {
        void onViewChanged(View changedView, float differenceInInterpolation);

        void viewFinishedClosing();

        void viewStartedExpanding(ExpandingBarView expandingBarView);
    }
}
