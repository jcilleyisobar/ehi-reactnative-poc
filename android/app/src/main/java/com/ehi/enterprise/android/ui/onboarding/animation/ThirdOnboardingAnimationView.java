package com.ehi.enterprise.android.ui.onboarding.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ThirdOnboardingAnimationViewBinding;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ThirdOnboardingAnimationView extends BaseOnboardingView<ManagersAccessViewModel, ThirdOnboardingAnimationViewBinding> implements IOnboardingScene {

    public static final int VIEW_NUMBER = 2;

    public ThirdOnboardingAnimationView(Context context) {
        this(context, null);
    }

    public ThirdOnboardingAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThirdOnboardingAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_third_onboarding_animation);
        initViews();
    }

    @Override
    protected Pair<MultiDrawableAnimationView, Integer> getViewToAnimateWithAnimation() {
        return new Pair<>(getViewBinding().layer5, R.array.sun_animation_frames);
    }

    @Override
    public void onScroll(int position, float offset, float positionOffsetPixels) {
        if (position == VIEW_NUMBER) {
            setRightPadding(getViewBinding().layer0, offset, BaseCarAnimationFragment.BASE_OFFSET);
            setRightPadding(getViewBinding().layer2, offset, BaseCarAnimationFragment.MAX_OFFSET);
            setRightPadding(getViewBinding().layer3, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer4, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer5Container, offset, BaseCarAnimationFragment.BASE_OFFSET);
        }
        if (position == VIEW_NUMBER - 1) {
            setLeftPadding(getViewBinding().layer0, offset, BaseCarAnimationFragment.BASE_OFFSET);
            setLeftPadding(getViewBinding().layer2, offset, BaseCarAnimationFragment.MAX_OFFSET);
            setLeftPadding(getViewBinding().layer3, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer4, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer5Container, offset, BaseCarAnimationFragment.BASE_OFFSET);
        }
    }
}
