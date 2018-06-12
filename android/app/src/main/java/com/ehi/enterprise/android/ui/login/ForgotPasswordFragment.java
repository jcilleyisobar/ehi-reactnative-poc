package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ForgotPasswordFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(ForgotPasswordViewModel.class)
public class ForgotPasswordFragment extends DataBindingViewModelFragment<ForgotPasswordViewModel, ForgotPasswordFragmentBinding> {

    public static final String SCREEN_NAME = "ForgotPasswordFragment";

    private View.OnClickListener mOnSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().sendRequest();

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_FORGOT_PASSWORD.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SUBMIT.value)
                    .tagScreen()
                    .tagEvent();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_forgot_password, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        bind(ReactorTextView.bindText(getViewModel().firstNameInput, getViewBinding().firstName));
        bind(ReactorTextView.bindText(getViewModel().lastNameInput, getViewBinding().lastName));
        bind(ReactorTextView.bindText(getViewModel().emailInput, getViewBinding().emailAddress));


        bind(ReactorTextInputLayout.error(getViewModel().firstNameError, getViewBinding().firstNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().lastNameError, getViewBinding().lastNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().emailError, getViewBinding().emailLayout));

        bind(ReactorView.enabled(getViewModel().isValidForm, getViewBinding().submitButton));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().success.getValue()) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });
    }

    private void initViews() {
        getViewBinding().submitButton.setOnClickListener(mOnSubmitClickListener);
        getViewBinding().submitButton.setOnDisabledClickListener(mOnSubmitClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, ForgotPasswordFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_FORGOT_PASSWORD.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }
}
