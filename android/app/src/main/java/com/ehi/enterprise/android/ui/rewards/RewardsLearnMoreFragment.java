package com.ehi.enterprise.android.ui.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RewardsLearnMoreFragmentBinding;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.rewards.widget.ExpandingHeaderview;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class RewardsLearnMoreFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, RewardsLearnMoreFragmentBinding> implements ExpandingHeaderview.IExpandingHeaderViewCallBack {

    private static final long ANIMATION_DURATION = 500;
    public static final String TAG = "RewardsLearnMoreFragment";
    @Extra(boolean.class)
    public static final String LOGGED_IN = "LOGGED IN";
    private boolean mLoggedIn = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_REWARDS.value, TAG)
                    .state(EHIAnalytics.State.STATE_UNAUTH.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ENROLLMENT.value)
                    .tagScreen()
                    .tagEvent();
            Intent intent = new Intent(getActivity(), EnrollActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.rewards_learn_more_title));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_rewards_learn_more, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        final RewardsLearnMoreFragmentHelper.Extractor extractor = new RewardsLearnMoreFragmentHelper.Extractor(this);
        mLoggedIn = extractor.loggedIn();
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewBinding().plusInformation.post(new Runnable() {
            @Override
            public void run() {
                getViewBinding().plusInformation.setupView(getString(R.string.rewards_loyalty_tier_plus_title), getString(R.string.rewards_learn_more_tiers_plus_benefit), R.color.ehi_primary);
                getViewBinding().silverInformation.setupView(getString(R.string.rewards_loyalty_tier_silver_title), getString(R.string.rewards_learn_more_tiers_silver_benefit), R.color.rewards_silver_benefits_color);
                getViewBinding().goldInformation.setupView(getString(R.string.rewards_loyalty_tier_gold_title), getString(R.string.rewards_learn_more_tiers_gold_benefit), R.color.rewards_gold_benefits_color);
                getViewBinding().platinumInformation.setupView(getString(R.string.rewards_loyalty_tier_platinum_title), getString(R.string.rewards_learn_more_tiers_platinum_benefit), R.color.ehi_black);
            }
        });
    }

    private void initViews() {
        getViewBinding().topRightCross.initAll(R.color.ehi_primary, 8);
        getViewBinding().quickTip1.setExpandListener(this);
        getViewBinding().quickTip2.setExpandListener(this);
        getViewBinding().quickTip3.setExpandListener(this);
        getViewBinding().quickTip4.setExpandListener(this);

        if (mLoggedIn) {
            getViewBinding().eplusJoinNowButton.setVisibility(View.GONE);
        } else {
            getViewBinding().eplusJoinNowButton.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public void onViewStartChange(boolean expanded, ExpandingHeaderview headerview) {
        float from = expanded ? 180f : 0f;
        float to = expanded ? 0 : 180f;
        View arrow = headerview.findViewById(R.id.arrow);
        Animation animation = new RotateAnimation(from, to, arrow.getWidth() / 2, arrow.getHeight() / 2);
        animation.setFillAfter(true);
        animation.setDuration(ANIMATION_DURATION);
        arrow.startAnimation(animation);
    }

    @Override
    public void onViewChange(ExpandingHeaderview headerview, float inerpolation) {
        float difference = (getViewBinding().scrollView.getHeight() + getViewBinding().scrollView.getScrollY()) - (headerview.getHeight() + headerview.getY());
        if (difference < 0) {
            getViewBinding().scrollView.scrollBy(0, (int) Math.abs(difference));
        }
    }

    @Override
    public void onCollapseExpandAnimationEnd(ExpandingHeaderview headerview) {

    }
}
