package com.ehi.enterprise.android.ui.rewards;

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

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.RewardsAboutEnterprisePlusAuthFragmentBinding;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
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
public class RewardsAboutEnterprisePlusFragment extends DataBindingViewModelFragment<EnterprisePlusTermsAndConditionsViewModel, RewardsAboutEnterprisePlusAuthFragmentBinding> {

    public static final String SCREEN_NAME = "RewardsAboutEnterprisePlusFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().termsAndConditionsButton) {
                requestTermsAndConditions();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_rewards_about_enterprise_plus, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_PROGRAM_DETAILS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.rewards_learn_more_title));
    }

    private void initViews() {
        getViewBinding().termsAndConditionsButton.setOnClickListener(mOnClickListener);

        getViewBinding().plusTierRequirementsView.showPlusBenefits();
        getViewBinding().plusTierRequirementsView.hideNeededDays();
        getViewBinding().plusTierRequirementsView.setNeededRentals(getString(R.string.about_e_p_plus_rentals_needed));
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
                requestTermsAndConditions();
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
    }

    private void requestTermsAndConditions() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_PROGRAM_DETAILS.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TERMS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                .tagScreen()
                .tagEvent();

        getViewModel().requestTermsAndConditions();
    }

}