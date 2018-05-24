package com.ehi.enterprise.android.ui.onboarding.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SecondOnboardingAnimationViewBinding;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class SecondOnboardingAnimationView extends BaseOnboardingView<ManagersAccessViewModel, SecondOnboardingAnimationViewBinding> implements IOnboardingScene {
    public static final int VIEW_NUMBER = 1;

    public SecondOnboardingAnimationView(Context context) {
        this(context, null);
    }

    public SecondOnboardingAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondOnboardingAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_second_onboarding_animation);
        initViews();
    }

    @Override
    protected Pair<MultiDrawableAnimationView, Integer> getViewToAnimateWithAnimation() {
        return null;
    }

    @Override
    public void onScroll(int position, float positionOffset, float positionOffsetPixels) {
        float offset = positionOffset;
        if (position == VIEW_NUMBER) {
            setRightPadding(getViewBinding().layer0, offset, BaseCarAnimationFragment.MAX_OFFSET);
            setRightPadding(getViewBinding().layer1, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setRightPadding(getViewBinding().layer2, offset, BaseCarAnimationFragment.BASE_OFFSET);
        }
        if (position == VIEW_NUMBER - 1) {
            setLeftPadding(getViewBinding().layer0, offset, BaseCarAnimationFragment.MAX_OFFSET);
            setLeftPadding(getViewBinding().layer1, offset, BaseCarAnimationFragment.MEDIUM_OFFSET);
            setLeftPadding(getViewBinding().layer2, offset, BaseCarAnimationFragment.BASE_OFFSET);
        }
    }
}
