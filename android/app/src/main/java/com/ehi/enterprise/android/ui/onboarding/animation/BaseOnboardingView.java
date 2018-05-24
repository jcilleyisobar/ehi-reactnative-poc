package com.ehi.enterprise.android.ui.onboarding.animation;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;


public abstract class BaseOnboardingView<M extends ManagersAccessViewModel, T extends ViewDataBinding>
        extends DataBindingViewModelView<M, T> {

    private Pair<MultiDrawableAnimationView, Integer> mViewAndAnimation;

    protected BaseOnboardingView(Context context) {
        this(context, null);
    }

    protected BaseOnboardingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected BaseOnboardingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected Pair<MultiDrawableAnimationView, Integer> getViewToAnimateWithAnimation() {
        return null;
    }

    protected void initViews() {
        mViewAndAnimation = getViewToAnimateWithAnimation();
        if (mViewAndAnimation != null) {
            mViewAndAnimation.first.setCanvasColor(getResources().getColor(R.color.white));
            mViewAndAnimation.first.addDrawable(mViewAndAnimation.second, null, 44, false);
        }
    }

    public void startAnimation() {
        if (mViewAndAnimation != null) {
            if (mViewAndAnimation.first.isRunning()) {
                if (mViewAndAnimation.first.isSuspended())
                    mViewAndAnimation.first.suspend(false);
            } else
                mViewAndAnimation.first.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewAndAnimation.first.run();
                    }
                });
        }
    }

    public void stopAnimation() {
        if (mViewAndAnimation != null) {
            if (mViewAndAnimation.first.isRunning()) {
                mViewAndAnimation.first.suspend(true);
            }
        }
    }

    protected void setLeftPadding(View imageView, float positionOffset, float constant) {
        final int paddingLeft = (int) (constant * (1 - positionOffset));
        imageView.setPadding(paddingLeft, 0, 0, 0);
    }

    protected void setRightPadding(View imageView, float positionOffset, float constant) {
        final int paddingRight = (int) (constant * positionOffset);
        imageView.setPadding(0, 0, paddingRight, 0);
    }
}
