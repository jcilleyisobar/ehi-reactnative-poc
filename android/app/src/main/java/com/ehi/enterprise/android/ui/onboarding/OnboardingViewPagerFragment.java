package com.ehi.enterprise.android.ui.onboarding;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.OnboradingViewPagerFragmentBinding;
import com.ehi.enterprise.android.ui.onboarding.animation.BaseCarAnimationFragment;
import com.ehi.enterprise.android.ui.onboarding.animation.FirstOnboardingAnimationView;
import com.ehi.enterprise.android.ui.onboarding.animation.FourthOnboardingAnimationView;
import com.ehi.enterprise.android.ui.onboarding.animation.IOnboardingScene;
import com.ehi.enterprise.android.ui.onboarding.animation.SecondOnboardingAnimationView;
import com.ehi.enterprise.android.ui.onboarding.animation.ThirdOnboardingAnimationView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Locale;

@ViewModel(ManagersAccessViewModel.class)
public class OnboardingViewPagerFragment extends BaseCarAnimationFragment<ManagersAccessViewModel, OnboradingViewPagerFragmentBinding> {
    public static final String SCREEN_NAME = "OnboardingViewPagerFragment";

    private IOnboardingScene mFirstOnboardingView;
    private IOnboardingScene mSecondOnboardingView;
    private IOnboardingScene mThirdOnboardingView;
    private IOnboardingScene mFourthOnboardingView;

    private PagerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_onboarding_view_pager, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareVariables();
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setupCarPositionWhenScrolling(position, positionOffset);
            broadcastScrollEvent(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            trackEventForPosition(position);
            getViewBinding().viewPagerIndicator.updatePageIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                animateCar();
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//                stopAnimatingCar();
            }
        }
    };

    private void trackEventForPosition(int position) {
        final String state;
        switch (position) {
            case 0:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION1.value;
                break;
            case 1:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION2.value;
                break;
            case 2:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION3.value;
                break;
            case 3:
            default:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION4.value;
                break;
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_WELCOME.value, SCREEN_NAME)
                .state(state)
                .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(Locale.getDefault().getCountry()))
                .tagScreen()
                .tagEvent();
    }

    private void setupCarPositionWhenScrolling(int position, float positionOffset) {
        final double carPosition = positionOffset * mAvailableSpaceToMoveCar;
        final int paddingPosition = (int) (positionCarAtSceneStart(position) + carPosition);
        getViewBinding().carImage.setPadding(paddingPosition, 0, 0, 0);
    }

    @Override
    protected int getPageNumber() {
        return getViewBinding().viewPager.getCurrentItem();
    }

    @Override
    protected Pair<ImageView, Integer> getViewToAnimateWithAnimation() {
        return new Pair<>(getViewBinding().carImage, R.drawable.car_animation);
    }

    protected void initViews() {
        super.initViews();
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                View layout;
                switch (position) {
                    case 0:
                        mFirstOnboardingView = new FirstOnboardingAnimationView(getActivity());
                        ((FirstOnboardingAnimationView) mFirstOnboardingView).showTitleView();
                        layout = (View) mFirstOnboardingView;
                        break;
                    case 1:
                        mSecondOnboardingView = new SecondOnboardingAnimationView(getActivity());
                        layout = (View) mSecondOnboardingView;
                        break;
                    case 2:
                        mThirdOnboardingView = new ThirdOnboardingAnimationView(getActivity());
                        layout = (View) mThirdOnboardingView;
                        break;
                    case 3:
                        mFourthOnboardingView = new FourthOnboardingAnimationView(getActivity());
                        layout = (View) mFourthOnboardingView;
                        break;
                    default:
                        mFirstOnboardingView = new FirstOnboardingAnimationView(getActivity());
                        mFirstOnboardingView.startAnimation();
                        layout = (View) mFirstOnboardingView;
                        break;
                }

                collection.addView(layout);
                return layout;
            }

            @Override
            public void destroyItem(ViewGroup collection, int position, Object view) {
                collection.removeView((View) view);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

        };
        getViewBinding().viewPager.setOffscreenPageLimit(4);
        getViewBinding().viewPager.addOnPageChangeListener(mOnPageChangeListener);
        getViewBinding().viewPager.setAdapter(mAdapter);
        getViewBinding().viewPagerIndicator.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackEventForPosition(getViewBinding().viewPager.getCurrentItem());
        setupEplusIcon();
        startScenesAnimation();
    }

    @Override
    public void onPause() {
        stopScenesAnimation();
        super.onPause();
    }

    private void stopScenesAnimation() {
        if (mFirstOnboardingView != null) {
            mFirstOnboardingView.stopAnimation();
        }
        if (mSecondOnboardingView != null) {
            mSecondOnboardingView.stopAnimation();
        }
        if (mThirdOnboardingView != null) {
            mThirdOnboardingView.stopAnimation();
        }
        if (mFourthOnboardingView != null) {
            mFourthOnboardingView.stopAnimation();
        }
    }

    private void startScenesAnimation() {
        getViewBinding().viewPager.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mFirstOnboardingView.startAnimation();
                    mSecondOnboardingView.startAnimation();
                    mThirdOnboardingView.startAnimation();
                    mFourthOnboardingView.startAnimation();
                } catch (Exception e) {
                    DLog.e("Failing to stop animation for onboarding scenes", e);
                }
            }
        });
    }

    private void setupEplusIcon() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int welcomeTop = ((FirstOnboardingAnimationView) mFirstOnboardingView).getWelcomeTextTop();
                    if (welcomeTop < getViewBinding().eplusImage.getBottom()) {
                        int currentPadding = getViewBinding().eplusImage.getPaddingTop();
                        int newPadding = currentPadding - (getViewBinding().eplusImage.getBottom() - welcomeTop);
                        newPadding = (int) (newPadding - getResources().getDimension(R.dimen.padding_xsmall));
                        if (newPadding < 0) {
                            newPadding = 0;
                        }
                        getViewBinding().eplusImage.setPadding(0, newPadding, 0, 0);
                    }

                } catch (Exception e) {
                    DLog.e("Unexpected error when trying to change interface element position in runtime", e);
                }
            }
        }, 500);
    }


    private void broadcastScrollEvent(int position, float positionOffset, int positionOffsetPixels) {
        sendScrollEvent(mFirstOnboardingView, position, positionOffset, positionOffsetPixels);
        sendScrollEvent(mSecondOnboardingView, position, positionOffset, positionOffsetPixels);
        sendScrollEvent(mThirdOnboardingView, position, positionOffset, positionOffsetPixels);
        sendScrollEvent(mFourthOnboardingView, position, positionOffset, positionOffsetPixels);
    }

    private void sendScrollEvent(IOnboardingScene onboardingScene, int position, float positionOffset, int positionOffsetPixels) {
        if (onboardingScene != null) {
            onboardingScene.onScroll(position, positionOffset, positionOffsetPixels);
        }
    }
}
