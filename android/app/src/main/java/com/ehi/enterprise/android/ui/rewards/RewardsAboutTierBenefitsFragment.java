package com.ehi.enterprise.android.ui.rewards;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.animation.LinearInterpolator;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.RewardsAboutTierBenefitsFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.widget.ExpandingHeaderview;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@NoExtras
@ViewModel(EnterprisePlusTermsAndConditionsViewModel.class)
public class RewardsAboutTierBenefitsFragment extends DataBindingViewModelFragment<EnterprisePlusTermsAndConditionsViewModel, RewardsAboutTierBenefitsFragmentBinding> {

    public static final String TAG = "RewardsAboutTierBenefitsFragment";

    private static final long ANIMATION_DURATION = 500;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().termsAndConditionsButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, TAG)
                        .state(EHIAnalytics.State.STATE_ABOUT_TIER_BENEFITS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PROGRAM_DETAILS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new RewardsAboutEnterprisePlusFragmentHelper.Builder().build());
            }
        }
    };

    private ExpandingHeaderview.IExpandingHeaderViewCallBack mHeaderCallbackListener = new ExpandingHeaderview.IExpandingHeaderViewCallBack() {
        @Override
        public void onViewStartChange(boolean expanded, ExpandingHeaderview headerview) {
            View arrow = headerview.findViewById(R.id.arrow);
            float from = arrow.getRotation();
            float to = expanded ? 0 : 180f;

            ObjectAnimator animation = ObjectAnimator.ofFloat(arrow, "rotation", from, to);
            animation.setDuration(ANIMATION_DURATION);
            arrow.setPivotX(arrow.getWidth() / 2);
            arrow.setPivotY(arrow.getHeight() / 2);
            animation.setRepeatCount(0);
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
        }

        @Override
        public void onViewChange(ExpandingHeaderview headerview, float interpolation) {
            float difference = (getViewBinding().scrollView.getHeight() + getViewBinding().scrollView.getScrollY()) - (headerview.getHeight() + headerview.getY());
            if (difference < 0) {
                getViewBinding().scrollView.scrollBy(0, (int) Math.abs(difference));
            }
        }

        @Override
        public void onCollapseExpandAnimationEnd(ExpandingHeaderview headerview) {
            String actionTier;
            switch (headerview.getId()) {
                case R.id.silver_tier:
                    actionTier = EHIAnalytics.Action.ACTION_EXPAND_COLLAPSE_SILVER_TIER.value;
                    break;
                case R.id.gold_tier:
                    actionTier = EHIAnalytics.Action.ACTION_EXPAND_COLLAPSE_GOLD_TIER.value;
                    break;
                case R.id.platinum_tier:
                    actionTier = EHIAnalytics.Action.ACTION_EXPAND_COLLAPSE_PLATINUM_TIER.value;
                    break;
                case R.id.plus_tier:
                default:
                    actionTier = EHIAnalytics.Action.ACTION_EXPAND_COLLAPSE_PLUS_TIER.value;
                    break;
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, TAG)
                    .state(EHIAnalytics.State.STATE_ABOUT_TIER_BENEFITS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, actionTier)
                    .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                    .tagScreen()
                    .tagEvent();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_rewards_about_tier_benefits, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.rewards_about_tier_title));
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, TAG)
                .state(EHIAnalytics.State.STATE_ABOUT_TIER_BENEFITS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().plusTier.setExpandListener(mHeaderCallbackListener);
        getViewBinding().silverTier.setExpandListener(mHeaderCallbackListener);
        getViewBinding().goldTier.setExpandListener(mHeaderCallbackListener);
        getViewBinding().platinumTier.setExpandListener(mHeaderCallbackListener);

        getViewBinding().termsAndConditionsButton.setOnClickListener(mOnClickListener);

        getViewBinding().plusTierRequirementsView.setNeededRentals(getString(R.string.about_e_p_plus_rentals_needed));
        getViewBinding().plusTierRequirementsView.showPlusBenefits();
        getViewBinding().plusTierRequirementsView.hideNeededDays();
        getViewBinding().plusTierRequirementsView.setUpgradePerYear(getString(R.string.about_e_p_plus_upgrades_per_year));

        getViewBinding().silverTierRequirementsView.setNeededRentals(getString(R.string.about_e_p_silver_rentals_needed));
        getViewBinding().silverTierRequirementsView.hideNeededDays();
        getViewBinding().silverTierRequirementsView.setUpgradePerYear(getString(R.string.about_e_p_silver_upgrades_per_year));
        getViewBinding().silverTierRequirementsView.setBonusPoints(getString(R.string.about_e_p_silver_bonus_points));

        getViewBinding().goldTierRequirementsView.setNeededRentals(getString(R.string.about_e_p_gold_rentals_needed));
        getViewBinding().goldTierRequirementsView.setNeededDays(getString(R.string.about_e_p_gold_days_needed));
        getViewBinding().goldTierRequirementsView.setUpgradePerYear(getString(R.string.about_e_p_gold_upgrades_per_year));
        getViewBinding().goldTierRequirementsView.setBonusPoints(getString(R.string.about_e_p_gold_bonus_points));

        getViewBinding().platinumTierRequirementsView.setNeededRentals(getString(R.string.about_e_p_platinum_rentals_needed));
        getViewBinding().platinumTierRequirementsView.setNeededDays(getString(R.string.about_e_p_platinum_days_needed));
        getViewBinding().platinumTierRequirementsView.setUpgradePerYear(getString(R.string.about_e_p_platinum_upgrades_per_year));
        getViewBinding().platinumTierRequirementsView.setBonusPoints(getString(R.string.about_e_p_platinum_bonus_points));

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

    @Override
    protected void initDependencies() {
        super.initDependencies();

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

        addReaction("USER_PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ProfileCollection profile = getViewModel().getUserProfileCollection();
                if (profile != null && profile.getBasicProfile().getLoyaltyData() != null) {
                    String currentTier = profile.getBasicProfile().getLoyaltyData().getLoyaltyTier();

                    getViewBinding().rewardsAboutTierHeader.setupView(currentTier);

                    int plusBackground = getResources().getColor(R.color.ehi_primary);
                    int silverBackground = getResources().getColor(R.color.rewards_silver_benefits_color);
                    int goldBackground = getResources().getColor(R.color.rewards_gold_benefits_color);
                    int platinumBackground = getResources().getColor(R.color.ehi_black);

                    String plusTitle = getString(R.string.about_e_p_tier_plus_title);
                    String silverTitle = getString(R.string.about_e_p_tier_silver_title);
                    String goldTitle = getString(R.string.about_e_p_tier_gold_title);
                    String platinumTitle = getString(R.string.about_e_p_tier_platinum_title);

                    if (currentTier.equalsIgnoreCase(EHILoyaltyData.PLUS_STATUS)) {
                        getViewBinding().plusTierContent.setupView(plusTitle, plusBackground, false, true);
                        getViewBinding().silverTierContent.setupView(silverTitle, silverBackground, false, false);
                        getViewBinding().goldTierContent.setupView(goldTitle, goldBackground, false, false);
                        getViewBinding().platinumTierContent.setupView(platinumTitle, platinumBackground, false, false);
                    } else if (currentTier.equalsIgnoreCase(EHILoyaltyData.SILVER_STATUS)) {
                        getViewBinding().plusTierContent.setupView(plusTitle, plusBackground, true, false);
                        getViewBinding().silverTierContent.setupView(silverTitle, silverBackground, false, true);
                        getViewBinding().goldTierContent.setupView(goldTitle, goldBackground, false, false);
                        getViewBinding().platinumTierContent.setupView(platinumTitle, platinumBackground, false, false);
                    } else if (currentTier.equalsIgnoreCase(EHILoyaltyData.GOLD_STATUS)) {
                        getViewBinding().plusTierContent.setupView(plusTitle, plusBackground, true, false);
                        getViewBinding().silverTierContent.setupView(silverTitle, silverBackground, true, false);
                        getViewBinding().goldTierContent.setupView(goldTitle, goldBackground, false, true);
                        getViewBinding().platinumTierContent.setupView(platinumTitle, platinumBackground, false, false);
                    } else if (currentTier.equalsIgnoreCase(EHILoyaltyData.PLATINUM_STATUS)) {
                        getViewBinding().plusTierContent.setupView(plusTitle, plusBackground, true, false);
                        getViewBinding().silverTierContent.setupView(silverTitle, silverBackground, true, false);
                        getViewBinding().goldTierContent.setupView(goldTitle, goldBackground, true, false);
                        getViewBinding().platinumTierContent.setupView(platinumTitle, platinumBackground, false, true);
                    }
                }
            }
        });
    }

}