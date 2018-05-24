package com.ehi.enterprise.android.ui.rewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ScrollView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.RewardsFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.widget.DoubleTierView;
import com.ehi.enterprise.android.ui.rewards.widget.IAnimatedTier;
import com.ehi.enterprise.android.ui.rewards.widget.SimpleTierView;
import com.ehi.enterprise.android.ui.widget.NotifyingScrollView;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

import static com.ehi.enterprise.android.ui.rewards.widget.SimpleTierViewModel.TOTAL_DAYS;
import static com.ehi.enterprise.android.ui.rewards.widget.SimpleTierViewModel.TOTAL_RENTAL;

@NoExtras
@ViewModel(RewardsViewModel.class)
public class RewardsFragment extends DataBindingViewModelFragment<RewardsViewModel, RewardsFragmentBinding>
        implements NotifyingScrollView.OnScrollChangedListener, IRootMenuScreen {

    public static final String SCREEN_NAME = "RewardsFragment";
    private static final int REQUEST_CODE_LOG_OUT = 3333;
    private static final int ANIMATION_DURATION = 1000;

    private boolean mFirstAnimation;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getViewBinding().learnMoreButton == view) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, RewardsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EP_PROGRAM_DETAILS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new RewardsAboutEnterprisePlusFragmentHelper.Builder().build());
            } else if (getViewBinding().pointsView == view) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, RewardsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ABOUT_POINTS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new AboutPointsFragmentHelper.Builder().build());
            } else if (getViewBinding().tierView == view) {
                showModal(getActivity(), new RewardsAboutTierBenefitsFragmentHelper.Builder().build());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_rewards_fragment, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setShouldHideCallMenuItem(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOG_OUT && resultCode == Activity.RESULT_OK) {
            getViewModel().logOut();
        }
    }

    private void initViews() {
        getViewBinding().learnMoreButton.setOnClickListener(mOnClickListener);
        getViewBinding().pointsView.setOnClickListener(mOnClickListener);
        getViewBinding().tierView.setOnClickListener(mOnClickListener);

        setupLegalCopyText();
    }

    private void setupLegalCopyText() {
        SpannableString textToShow = new SpannableString(getResources().getString(R.string.eplus_terms_and_conditions_navigation_title));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                getViewModel().requestTermsAndConditions();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        textToShow.setSpan(clickableSpan, 0, textToShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
        textToShow.setSpan(
                typefaceSpan,
                0,
                textToShow.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        textToShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, textToShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getViewBinding().legalCopyText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.rewards_legal_points_long_info)
                .addTokenAndValue(EHIStringToken.TERMS, textToShow)
                .format());

        getViewBinding().legalCopyText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupTierView(EHILoyaltyData ehiLoyaltyData) {
        if (getViewModel().shouldHideGaugeView()) {
            getViewBinding().gaugeSection.setVisibility(View.GONE);
            getViewBinding().gaugeTitle.setVisibility(View.GONE);
            getViewBinding().gaugeViewContainer.setVisibility(View.GONE);
        } else {
            if (getViewBinding().gaugeViewContainer.getChildCount() == 0) {
                if (getViewModel().shouldUseDoubleTier()) {
                    getViewBinding().gaugeViewContainer.addView(new DoubleTierView(getContext(), ehiLoyaltyData, TOTAL_DAYS, TOTAL_RENTAL));
                } else {
                    getViewBinding().gaugeTitle.setTextAppearance(getActivity(), R.style.TextViewSize26);
                    getViewBinding().gaugeViewContainer.addView(new SimpleTierView(getContext(), TOTAL_RENTAL, ehiLoyaltyData));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreenChange();
        getViewBinding().gaugeViewContainer.post(new Runnable() {
            @Override
            public void run() {
                startGaugeAnimation();
            }
        });
    }

    @Override
    public void trackScreenChange() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, RewardsFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void animateArch(float interpolatedTime) {
        if (getViewBinding().gaugeViewContainer.getChildAt(0) instanceof IAnimatedTier) {
            ((IAnimatedTier) getViewBinding().gaugeViewContainer.getChildAt(0)).setPercentage(interpolatedTime);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirstAnimation = true;
        getActivity().setTitle(getString(R.string.rewards_title));
        populateView(getViewModel().getUserProfileCollection());
        getViewBinding().scrollView.setOnScrollChangedListener(this);
    }

    @Override
    public void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.text(getViewModel().headerTitle.textCharSequence(), getViewBinding().headerTitle));
        bind(ReactorTextView.text(getViewModel().headerTitle.text(), getViewBinding().headerTitle));
        bind(ReactorTextView.text(getViewModel().gaugeTitle.textCharSequence(), getViewBinding().gaugeTitle));
        bind(ReactorTextView.textRes(getViewModel().headerTitle.textRes(), getViewBinding().headerTitle));
        bind(ReactorTextView.text(getViewModel().headerSubtitle.textCharSequence(), getViewBinding().headerSubtitle));
        bind(ReactorTextView.text(getViewModel().headerSubtitle.text(), getViewBinding().headerSubtitle));
        bind(ReactorTextView.textRes(getViewModel().headerSubtitle.textRes(), getViewBinding().headerSubtitle));
        bind(ReactorView.visibility(getViewModel().legalCopyText.visibility(), getViewBinding().legalCopyText));
        bind(ReactorImageView.imageResource(getViewModel().headerImage.imageResource(), getViewBinding().headerImage));

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("TERMS_AND_CONDITIONS_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                GetEPlusTermsAndConditionsResponse response = getViewModel().getTermsAndConditionsResponse();
                if (response != null) {
                    showModal(getActivity(),
                            new DisclaimerFragmentHelper.Builder()
                                    .keyTitle(getString(R.string.terms_and_conditions_title))
                                    .keyBody(response.getTermsAndConditions())
                                    .build());
                    getViewModel().setTermsAndConditions(null);
                }
            }
        });
    }

    private void populateView(ProfileCollection profile) {
        if (profile == null || profile.getProfile() == null || profile.getProfile().getBasicProfile() == null) {
            return;
        }


        final EHILoyaltyData ehiLoyaltyData = profile.getBasicProfile().getLoyaltyData();
        if (ehiLoyaltyData == null || ehiLoyaltyData.getLoyaltyTier() == null) {
            return;
        }

        getViewModel().setUpHeader();

        getViewBinding().pointsText.setText(String.valueOf(ehiLoyaltyData.getFormattedPointsToDate()));

        final String loyaltyTier = ehiLoyaltyData.getLoyaltyTier();
        SpannableString text = new SpannableString(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.rewards_welcome_current_tier)
                .addTokenAndValue(EHIStringToken.TIER, loyaltyTier)
                .format());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(getColorFromStatus(loyaltyTier))),
                0,
                text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getViewBinding().tierText.setText(text);

        setupTierView(ehiLoyaltyData);
    }

    public int getColorFromStatus(String color) {
        switch (color.toLowerCase()) {
            case EHILoyaltyData.GOLD_STATUS:
                return R.color.rewards_gold_benefits_color;
            case EHILoyaltyData.SILVER_STATUS:
                return R.color.rewards_silver_benefits_color;
            case EHILoyaltyData.PLATINUM_STATUS:
                return R.color.ehi_black;
            default:
                return R.color.ehi_primary;
        }
    }

    @Override
    public void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt) {
        startGaugeAnimation();
    }

    private void startGaugeAnimation() {
        final float gaugeBottomPosition = getViewBinding().gaugeViewContainer.getY() + getViewBinding().gaugeViewContainer.getHeight() / 2;
        final float scrollLength =  getViewBinding().scrollView.getScrollY() + getViewBinding().scrollView.getHeight();
        final boolean isGaugeVisibleOnTheScreen = gaugeBottomPosition <= scrollLength;

        if (mFirstAnimation && isGaugeVisibleOnTheScreen) {
            mFirstAnimation = false;
        } else {
            return;
        }

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                animateArch(interpolatedTime);
            }
        };

        animation.setDuration(ANIMATION_DURATION);
        getViewBinding().gaugeViewContainer.startAnimation(animation);
    }


}