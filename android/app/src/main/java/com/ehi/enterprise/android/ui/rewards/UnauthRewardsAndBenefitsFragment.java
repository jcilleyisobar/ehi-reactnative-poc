package com.ehi.enterprise.android.ui.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RewardsAndBenefitsFragmentBinding;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
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
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class UnauthRewardsAndBenefitsFragment extends BaseCarAnimationFragment<ManagersAccessViewModel, RewardsAndBenefitsFragmentBinding>
            implements IRootMenuScreen{

    private static final String SCREEN_NAME = "UnauthRewardsAndBenefitsFragment";
    
    private IOnboardingScene mFirstOnboardingView;
    private IOnboardingScene mSecondOnboardingView;
    private IOnboardingScene mThirdOnboardingView;
    private IOnboardingScene mFourthOnboardingView;
    private RewardsPlusBenefitsView mPlusBenefitsView;
    private RewardsJoinNowView mRewardsJoinNowView;

    private PagerAdapter mAdapter;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().signIn) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_UNAUTH.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SIGN_IN.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new LoginFragmentHelper.Builder().build());
            } else if (view == getViewBinding().join) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_UNAUTH.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                        .tagScreen()
                        .tagEvent();

                startActivity(new Intent(getActivity(), EnrollActivity.class));
            } else if (view == getViewBinding().learnMoreAboutEnterprise) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_UNAUTH.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ABOUT_EP.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new RewardsAboutEnterprisePlusFragmentHelper.Builder().build());
            }
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setupCarPositionWhenScrolling(position, positionOffset, positionOffsetPixels);
            setupLearnMoreAboutButtonVisibility(position);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShouldHideCallMenuItem(true);
        super.onCreate(savedInstanceState);
        prepareVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_rewards_and_benefits, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.user_phone_type_eplus);
    }

    private void setupLearnMoreAboutButtonVisibility(int position) {
        if (position == 5) {
            getViewBinding().learnMoreAboutEnterprise.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().learnMoreAboutEnterprise.setVisibility(View.INVISIBLE);
        }
    }

    private void setupCarPositionWhenScrolling(int position, float positionOffset, int positionOffsetPixels) {
        final double carPosition = positionOffset * mAvailableSpaceToMoveCar;
        final int paddingPosition = (int) (positionCarAtSceneStart(position) + carPosition);
        if (position == 3 && positionOffset > 0) {
            getViewBinding().carImage.setVisibility(View.VISIBLE);
            final int paddingPositionBackwards = (int) (paddingPosition - (carPosition * 4 + positionOffsetPixels));
            getViewBinding().carImage.setPadding(paddingPositionBackwards, 0, 0, 0);
        } else if (position <= 3) {
            getViewBinding().carImage.setVisibility(View.VISIBLE);
            getViewBinding().carImage.setPadding(paddingPosition, 0, 0, 0);
        } else {
            getViewBinding().carImage.setVisibility(View.INVISIBLE);
        }
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
        getViewBinding().signIn.setOnClickListener(mOnClickListener);
        getViewBinding().join.setOnClickListener(mOnClickListener);
        getViewBinding().learnMoreAboutEnterprise.setOnClickListener(mOnClickListener);
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public Object instantiateItem(ViewGroup collection, int position) {
                View layout;
                switch (position) {
                    case 0:
                        mFirstOnboardingView = new FirstOnboardingAnimationView(getActivity());
                        ((FirstOnboardingAnimationView) mFirstOnboardingView).hideTitleView();
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
                    case 4:
                        mPlusBenefitsView = new RewardsPlusBenefitsView(getActivity());
                        layout = mPlusBenefitsView;
                        break;
                    case 5:
                        mRewardsJoinNowView = new RewardsJoinNowView(getActivity());
                        layout = mRewardsJoinNowView;
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
        getViewBinding().viewPager.setOffscreenPageLimit(6);
        getViewBinding().viewPager.addOnPageChangeListener(mOnPageChangeListener);
        getViewBinding().viewPager.setAdapter(mAdapter);
        getViewBinding().viewPagerIndicator.setAdapter(mAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        trackScreenChange();
        startScenesAnimation();
    }

    @Override
    public void trackScreenChange() {
        trackEventForPosition(getViewBinding().viewPager.getCurrentItem());
    }

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
                state = EHIAnalytics.State.STATE_HOME_ANIMATION4.value;
                break;
            case 4:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION5.value;
                break;
            case 5:
            default:
                state = EHIAnalytics.State.STATE_HOME_ANIMATION6.value;
                break;
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_UNAUTH.value, SCREEN_NAME)
                .state(state)
                .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onPause() {
        stopScenesAnimation();
        super.onPause();
    }

    private void stopScenesAnimation() {
        mFirstOnboardingView.stopAnimation();
        mSecondOnboardingView.stopAnimation();
        mThirdOnboardingView.stopAnimation();
        mFourthOnboardingView.stopAnimation();
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


}
