package com.ehi.enterprise.android.ui.onboarding.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FourthOnboardingAnimationViewBinding;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;


@ViewModel(ManagersAccessViewModel.class)
public class FourthOnboardingAnimationView extends BaseOnboardingView<ManagersAccessViewModel, FourthOnboardingAnimationViewBinding> implements IOnboardingScene {

    private static final int VIEW_NUMBER = 3;

    public FourthOnboardingAnimationView(Context context) {
        this(context, null);
    }

    public FourthOnboardingAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourthOnboardingAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_fourth_onboarding_animation);
        initViews();
    }

    @Override
    protected Pair<MultiDrawableAnimationView, Integer> getViewToAnimateWithAnimation() {
        return new Pair<>(getViewBinding().ferrieWheel, R.array.ferris_wheel_animation_frames);
    }

    @Override
    public void onScroll(int position, float offset, float positionOffsetPixels) {
        if (position == VIEW_NUMBER - 1) {
            setLeftPadding(getViewBinding().ferrieWheelContainer, offset, BaseCarAnimationFragment.MAX_OFFSET);
            setLeftPadding(getViewBinding().layer2, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer3, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer4, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer5, offset, BaseCarAnimationFragment.BASE_OFFSET);
        }

    }
}
