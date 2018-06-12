package com.ehi.enterprise.android.ui.enroll;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollStepThreeFragmentBinding;
import com.ehi.enterprise.android.models.EHIErrorMessage;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.enroll.PostEnrollProfileResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@NoExtras
@ViewModel(EnrollStepThreeViewModel.class)
public class EnrollStepThreeFragment extends DataBindingViewModelFragment<EnrollStepThreeViewModel, EnrollStepThreeFragmentBinding> {

    public static final String TAG = "EnrollStepThreeFragment";

    private ArrayList<String> mErrorMessageList;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewModel().persistUpdatedEnrollProfile(
                        getViewBinding().enrollStepThreeView.updateEnrollProfile(
                                getViewModel().getEnrollProfile()
                        )
                );
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_3.value, TAG)
                        .state(getViewModel().getState())
                        .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                                getViewModel().getState(),
                                getViewModel().getSelectedCountryCode()))
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN.value)
                        .tagScreen()
                        .tagEvent();
                getViewModel().commitEnroll();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewBinding().enrollStepThreeView.highlightInvalidFields();
                getViewBinding().enrollStepThreeView.startHighlightInvalidFieldsOnFormChange();
                getViewBinding().enrollErrorView.setErrorMessageList(
                        getViewBinding().enrollStepThreeView.getErrorMessageList()
                );
                BaseAppUtils.hideKeyboard(getActivity());

                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                        getViewBinding().scrollView,
                        "scrollY",
                        0
                );
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }
        }
    };

    private View.OnClickListener termsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().requestTermsAndConditions();
        }
    };

    private DialogInterface.OnClickListener mErrorDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            ((EnrollFlowListener) getActivity()).goToFullFormStep(mErrorMessageList);
        }
    };

    private FormContract.FormListener formListener = new FormContract.FormListener() {
        @Override
        public void isValid(boolean validation) {
            getViewModel().onFormChanged(validation);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_enroll_step_three, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_3.value, TAG)
                .state(getViewModel().getState())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_STEP_3.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                        getViewModel().getState(),
                        getViewModel().getSelectedCountryCode()))
                .tagScreen()
                .tagMacroEvent()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().termsError, getActivity()));

        bind(ReactorView.enabled(getViewModel().continueButton.enabled(), getViewBinding().continueButton));

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

        addReaction("COMMIT_ENROLL_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().commitSuccessful.getValue()) {
                    ((EnrollFlowListener) getActivity()).goToConfirmationStep(
                            getViewModel().getLoyaltyNumber(),
                            getViewBinding().enrollStepThreeView.getPassword()
                    );
                    getViewModel().commitSuccessful.setValue(false);
                }
            }
        });

        addReaction("COMMIT_ENROLL_ERROR_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().errorResponse.getValue() == null) {
                    return;
                }

                showErrorDialog(getViewModel().errorResponse.getValue());

                getViewModel().errorResponse.setValue(null);
            }
        });
    }

    private void initViews() {
        getViewBinding().stepTextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "3")
                .addTokenAndValue(EHIStringToken.STEP_COUNT, EnrollActivity.TOTAL_STEPS)
                .formatString(R.string.enroll_step)
                .format());

        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);

        getViewBinding().enrollStepThreeView.hideHeader();

        getViewBinding().enrollStepThreeView.setFormListener(formListener);
        getViewBinding().enrollStepThreeView.setTermsAndConditionsClickListener(termsClickListener);
        getViewBinding().enrollStepThreeView.setScrollView(getViewBinding().scrollView);

        getViewBinding().enrollStepThreeView.setPresetData(getViewModel().getEnrollProfile(), getViewModel().needCheckEmailNotificationsByDefault());
        getViewBinding().enrollStepThreeView.setState(getViewModel().getState());
        getViewBinding().enrollStepThreeView.setCountryCode(getViewModel().getSelectedCountryCode());
    }

    private void showErrorDialog(ResponseWrapper<PostEnrollProfileResponse> errorResponse) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            stringBuilder.append(errorResponse.getCodes())
                    .append(":\n\n");
        }

        stringBuilder.append(errorResponse.getMessage());

        final List<EHIErrorMessage> ehiErrorMessages = errorResponse.getData().getMessages();

        mErrorMessageList = new ArrayList<>(ehiErrorMessages.size());

        for (final EHIErrorMessage ehiErrorMessage : ehiErrorMessages) {
            mErrorMessageList.add(ehiErrorMessage.getErrorMessage());
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(stringBuilder.toString())
                .setTitle(R.string.enroll_title)
                .setPositiveButton(getString(R.string.alert_okay_title), mErrorDialogListener)
                .create()
                .show();
    }
}
