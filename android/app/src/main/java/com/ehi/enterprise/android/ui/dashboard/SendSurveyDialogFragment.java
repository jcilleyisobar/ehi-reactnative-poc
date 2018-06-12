package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.databinding.SendSurveyDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.interfaces.ToolbarNavigationListener;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.ForeSeeSurveyManager;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(SendSurveyDialogViewModel.class)
public class SendSurveyDialogFragment extends DataBindingViewModelFragment<SendSurveyDialogViewModel, SendSurveyDialogFragmentBinding> implements ToolbarNavigationListener{

    private static final String SCREEN_NAME = "SendSurveyDialogFragment";

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().sendButton) {
                if (getViewModel().sendButton.enabled().getValue()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_SURVEY_INVITE.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SEND_SURVEY.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                            .tagScreen()
                            .tagEvent();

                    getViewModel().onSendSurveyClicked();
                } else {
                    ToastUtils.showToast(getActivity(), R.string.survey_invalid_contact_info_message);
                }
            } else if (view == getViewBinding().privacyPolicy) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SURVEY_INVITE.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PRIVACY_POLICY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                        .tagScreen()
                        .tagEvent();

                IntentUtils.openUrlViaCustomTab(getActivity(), Settings.FORESEE_PRIVACY_POLICE);
            }
        }
    };

    private final ForeSeeSurveyManager.InviteListener mInviteListener = new ForeSeeSurveyManager.InviteListener() {

        @Override
        public void onInviteShow() {
            try {
                getViewModel().stopProgress();
            } catch (Exception e) {
                //CATCH THEM ALL
            }
        }

        @Override
        public void onInviteAccept() {
            try {
                getViewModel().stopProgress();
                ToastUtils.showToast(getContext(), R.string.survey_success_message);
                getActivity().finish();
            } catch (Exception e) {
                //CATCH THEM ALL
            }
        }

        @Override
        public void onError() {
            try {
                getViewModel().stopProgress();
                ToastUtils.showToast(getActivity(), R.string.survey_invalid_contact_info_message);
            } catch (Exception e) {
                //CATCH THEM ALL
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_send_survey_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SURVEY_INVITE.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().sendButton.setOnClickListener(mOnClickListener);
        getViewBinding().sendButton.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().privacyPolicy.setOnClickListener(mOnClickListener);
        getViewModel().getManagers().getForeseeSurveyManager().setInviteListener(mInviteListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorTextView.bindText(getViewModel().contactInfo.text(), getViewBinding().emailOrPhone));
        bind(ReactorView.enabled(getViewModel().sendButton.enabled(), getViewBinding().sendButton));
    }

    @Override
    public void onNavigationItemClicked() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_FORESEE.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SURVEY_INVITE.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_BACK.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.termsAndConditions(getViewModel().getCountryCode()))
                .tagScreen()
                .tagEvent();
        getActivity().finish();
    }
}
