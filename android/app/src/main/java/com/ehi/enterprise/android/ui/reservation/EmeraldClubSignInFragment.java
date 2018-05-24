package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EmeraldClubSignInFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.ChangePasswordFragment;
import com.ehi.enterprise.android.ui.login.ChangePasswordFragmentHelper;
import com.ehi.enterprise.android.ui.login.ForceChangePasswordFragment;
import com.ehi.enterprise.android.ui.login.ForceChangePasswordFragmentHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(EmeraldClubSignInViewModel.class)
public class EmeraldClubSignInFragment extends DataBindingViewModelFragment<EmeraldClubSignInViewModel, EmeraldClubSignInFragmentBinding> {

    public static final String SCREEN_NAME = "EmeraldClubSignInFragment";
    public static final String PASSWORD_RESET_REQUIRED = "PASSWORD_RESET_REQUIRED";

    //region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getViewBinding().ecLogInButton == v) {
                getViewModel().loginToEmeraldClub();
            } else if (getViewBinding().loginScreenShowPasswordContainer == v) {
                if (getViewBinding().ecPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    getViewBinding().ecPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    getViewBinding().ecPassword.setTypeface(getViewBinding().ecMemberId.getTypeface());
                    getViewBinding().loginScreenShowPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show_02));
                } else {
                    getViewBinding().ecPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    getViewBinding().ecPassword.setTypeface(getViewBinding().ecMemberId.getTypeface());
                    getViewBinding().loginScreenShowPasswordContainer.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
                }
                getViewBinding().ecPassword.setSelection(getViewBinding().ecPassword.getText().toString().length());
            } else if (getViewBinding().ecForgotPasswordButton == v) {
                IntentUtils.openUrlViaExternalApp(getActivity(), getViewModel().getEcForgotPasswordUrl());

                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_EC_SIGN_IN.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EC_FORGOT_PW_USERNAME.value)
                        .tagScreen()
                        .tagEvent();
            } else if (getViewBinding().rememberInfoCheckBox == v) {
                if (getViewBinding().rememberInfoCheckBox.isChecked()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, EmeraldClubSignInFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_EC_SIGN_IN.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMEMBER_ON.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                            .tagScreen()
                            .tagEvent();
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, EmeraldClubSignInFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_EC_SIGN_IN.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMEMBER_OFF.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                            .tagScreen()
                            .tagEvent();
                }

            }
        }
    };
    //endregion

    //region lifecycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_emerald_club_sign_in, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, EmeraldClubSignInFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_EC_SIGN_IN.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }
    //endregion

    private void initViews() {
        getViewBinding().ecLogInButton.setOnClickListener(mOnClickListener);
        getViewBinding().loginScreenShowPasswordContainer.setOnClickListener(mOnClickListener);
        getViewBinding().ecForgotPasswordButton.setOnClickListener(mOnClickListener);
        getViewBinding().rememberInfoCheckBox.setOnClickListener(mOnClickListener);

        if ("dev".equals(BuildConfig.FLAVOR)) {
            getViewModel().memberId.setValue("vsltest");
            getViewModel().usernameText.setText("vsltest");
            getViewModel().password.setValue("enterprise1");
            getViewModel().passwordText.setText("enterprise1");
        }
    }


    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(ReactorTextView.bindText(getViewModel().memberId, getViewBinding().ecMemberId));
        bind(ReactorTextView.bindText(getViewModel().password, getViewBinding().ecPassword));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorView.enabled(getViewModel().isValidForm, getViewBinding().ecLogInButton));
        bind(ReactorCompoundButton.bindChecked(getViewModel().rememberInfo.checked(), getViewBinding().rememberInfoCheckBox));
        ReactorTextView.textChanges(getViewBinding().ecPassword, getViewModel().passwordText.text());
        ReactorTextView.textChanges(getViewBinding().ecMemberId, getViewModel().usernameText.text());

        addReaction("EC_LOGIN_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isLoginComplete()) {
                    ToastUtils.showToast(getActivity(), R.string.reservation_emerald_sign_in_confirmation_toast_message);
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        addReaction("PASSWORD_RESET_REQUIRED_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().passwordResetRequired();
                if (!reactorComputation.isFirstRun()) {
                    showModalDialogForResult(getActivity(), new ForceChangePasswordFragmentHelper.Builder().build(), ForceChangePasswordFragment.REQUEST_CODE);
                }
            }
        });

        addReaction("TERMS_CONDITIONS_MISMATCH", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isTermsAndConditionsVersionMismatched.getValue()) {
                    if (TextUtils.isEmpty(getViewModel().getTermsAndConditionsString())) {
                        getViewModel().getTermsConditions();
                    } else {
                        showTermsConditionsDialog(getViewModel().getTermsAndConditionsString(), getViewModel().getTermsConditionsVersion());
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ForceChangePasswordFragment.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    showModalForResult(getActivity(), new ChangePasswordFragmentHelper.Builder()
                            .changePasswordOldPassword(getViewModel().passwordText.text().getRawValue())
                            .changePasswordIsEcFlow(true)
                            .changePasswordRememberMe(getViewModel().rememberInfo.checked().getRawValue())
                            .changePasswordUserName(getViewModel().usernameText.text().getRawValue()).build(), ChangePasswordFragment.REQUEST_CODE);
                }
                break;
            case ChangePasswordFragment.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                break;
            default:
                break;
        }

    }

    private void showTermsConditionsDialog(String termsConditionsString, final String versionNumber) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.terms_and_conditions_updated_title)
                .setMessage(Html.fromHtml(termsConditionsString))
                .setPositiveButton(R.string.terms_and_conditions_accept_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getViewModel().setTermsConditionsVersion(versionNumber);
                        getViewModel().loginToEmeraldClub();
                    }
                })
                .setNegativeButton(R.string.cancel_button_title_key, null)
                .setCancelable(true)
                .create()
                .show();
    }
}
