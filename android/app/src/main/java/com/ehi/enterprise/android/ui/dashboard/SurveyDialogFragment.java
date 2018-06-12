package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SurveyDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(CountrySpecificViewModel.class)
public class SurveyDialogFragment extends DataBindingViewModelFragment<CountrySpecificViewModel, SurveyDialogFragmentBinding> {

    private static final String SCREEN_NAME = "SurveyDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().confirmButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_BANNER.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new SendSurveyDialogFragmentHelper.Builder().build());
                getActivity().finish();
            } else if (view == getViewBinding().skipButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_BANNER.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NO.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                        .tagScreen()
                        .tagEvent();

                getViewModel().getManagers().getForeseeSurveyManager().denyInvite();
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_survey_dialog, container);

        initViews();

        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_BANNER.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().confirmButton.setOnClickListener(mOnClickListener);
        getViewBinding().skipButton.setOnClickListener(mOnClickListener);
    }
}
