package com.ehi.enterprise.android.ui.onboarding.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FirstOnboardingAnimationViewBinding;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class FirstOnboardingAnimationView extends BaseOnboardingView<ManagersAccessViewModel, FirstOnboardingAnimationViewBinding> implements IOnboardingScene {

    public static final int VIEW_NUMBER = 0;

    public FirstOnboardingAnimationView(Context context) {
        this(context, null);
    }

    public FirstOnboardingAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FirstOnboardingAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_first_onboarding_animation);
        initViews();
    }

    @Override
    protected Pair<MultiDrawableAnimationView, Integer> getViewToAnimateWithAnimation() {
        return new Pair<>(getViewBinding().baloonAnimation, R.array.balloon_animation_frames);
    }

    @Override
    public void onScroll(int position, float positionOffset, float positionOffsetPixels) {
        if (position == VIEW_NUMBER) {
            setRightPadding(getViewBinding().layer2, positionOffset, BaseCarAnimationFragment.MAX_OFFSET);
            setRightPadding(getViewBinding().layer3, positionOffset, BaseCarAnimationFragment.MAX_OFFSET);
            setRightPadding(getViewBinding().layer4, positionOffset, BaseCarAnimationFragment.MAX_OFFSET);
            setRightPadding(getViewBinding().layer5, positionOffset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer6, positionOffset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer7, positionOffset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer8, positionOffset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().baloonAnimationContainer, positionOffset, BaseCarAnimationFragment.BASE_OFFSET);
        }

    }

    public void showTitleView() {
        getViewBinding().title.setVisibility(View.VISIBLE);
    }

    public void hideTitleView() {
        getViewBinding().title.setVisibility(View.GONE);
    }

    public int getWelcomeTextTop() {
        return getViewBinding().title.getTop();
    }
}
