package com.ehi.enterprise.android.ui.onboarding.animation;

public interface IOnboardingScene {
    void onScroll(int position, float positionOffset, float positionOffsetPixels);

    void startAnimation();

    void stopAnimation();
}
