package com.ehi.enterprise.android.ui.enroll;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollStepOneFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@NoExtras
@ViewModel(EnrollStepOneFragmentViewModel.class)
public class EnrollStepOneFragment extends DataBindingViewModelFragment<EnrollStepOneFragmentViewModel, EnrollStepOneFragmentBinding>
        implements CountryContract.CountryListener, FormContract.FormListener {

    public static final String TAG = "EnrollStepOneFragment";
    private static final int LOGIN_REQUEST_CODE = 1011;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewModel().searchProfile(
                        getViewBinding().enrollStepOneView.getSearchObject(
                                getViewModel().getSelectedCountry(),
                                getViewModel().getSelectedRegion()
                        ));
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_1.value, TAG)
                        .state(EHIAnalytics.State.STATE_NONE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                                EHIAnalytics.State.STATE_NONE.value,
                                getViewModel().getSelectedCountry().getCountryCode()))
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NEXT.value)
                        .tagScreen()
                        .tagEvent();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewBinding().enrollStepOneView.highlightInvalidFields();
                getViewBinding().enrollStepOneView.startHighlightInvalidFieldsOnFormChange();
                getViewBinding().enrollErrorView.setErrorMessageList(
                        getViewBinding().enrollStepOneView.getErrorMessageList()
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_enroll_step_one, container);
        prefillDriverInfo();
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
            IntentUtils.goToHomeScreen(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_1.value, TAG)
                .state(EHIAnalytics.State.STATE_NONE.value)
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_STEP_1.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                        EHIAnalytics.State.STATE_NONE.value,
                        getViewModel().getSelectedCountry() != null ? getViewModel().getSelectedCountry().getCountryCode() : ""))
                .tagScreen()
                .tagMacroEvent()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);
        getViewBinding().enrollStepOneView.setCountryListener(this);
        getViewBinding().enrollStepOneView.setFormListener(this);

        getViewBinding().stepTextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "1")
                .addTokenAndValue(EHIStringToken.STEP_COUNT, EnrollActivity.TOTAL_STEPS)
                .formatString(R.string.enroll_step)
                .format());

        getViewBinding().enrollStepOneView.hideHeader();

        getViewBinding().enrollStepOneView.setPresetData(getViewModel().getEnrollProfile());
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorView.enabled(getViewModel().submitButton.enabled(), getViewBinding().continueButton));

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("COUNTRY_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHICountry selectedCountry = getViewModel().getSelectedCountry();
                if (selectedCountry == null) {
                    return;
                }
                getViewBinding().enrollStepOneView.setCountry(selectedCountry);
                getViewModel().requestRegionsForCountry(selectedCountry,true);
            }
        });

        addReaction("REGION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIRegion selectedRegion = getViewModel().getSelectedRegion();
                if (selectedRegion == null) {
                    return;
                }
                getViewBinding().enrollStepOneView.setRegion(selectedRegion);
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldGoToNextStep()) {
                    getViewModel().setGoneToNextStep();
                    getViewModel().persistUpdatedEnrollProfile(
                            getViewBinding().enrollStepOneView.updateEnrollProfile(
                                    getViewModel().getEnrollProfile()
                            )
                    );
                    if (getViewModel().isDriverFound()) {
                        ((EnrollFlowListener) getActivity()).goToAddressStep(getViewModel().isEmeraldClub());
                    } else {
                        ((EnrollFlowListener) getActivity()).goToStepTwo();
                    }
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldShowEPLogin()) {
                    getViewModel().setEPLoginShowed();
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_1.value, TAG)
                            .state(EHIAnalytics.State.STATE_EP_MATCH.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                                    EHIAnalytics.State.STATE_EP_MATCH.value,
                                    getViewModel().getSelectedCountry() != null ? getViewModel().getSelectedCountry().getCountryCode() : ""))
                            .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_STEP_1.value)
                            .tagScreen()
                            .tagEvent()
                            .tagMacroEvent();
                    final Fragment fragment = new LoginFragmentHelper.Builder()
                            .message(getString(R.string.enroll_account_exists_title))
                            .memberNumber(getViewModel().getMemberNumber())
                            .build();
                    showModalForResult(getActivity(), fragment, LOGIN_REQUEST_CODE);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldNavigateToCompleteEnroll()) {
                    getViewModel().setNavigatedToCompleteEnroll();
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.enroll_partial_profile_found_title))
                            .setMessage(getString(R.string.enroll_partial_profile_found_message))
                            .setPositiveButton(R.string.signin_partial_enrollment_action_button_title, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentUtils.openUrlViaExternalApp(getActivity(), getViewModel().getActivationLink());
                                }
                            })
                            .setNegativeButton(R.string.alert_cancel_title, null)
                            .show();
                }
            }
        });
    }

    @Override
    public void onCountryClick() {

        if (getViewModel().getCountries() == null) {
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);

        for (EHICountry country : getViewModel().getCountries()) {
            arrayAdapter.add(country.getCountryName());
        }
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().setSelectedCountry(getViewModel().getCountries().get(which));
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onRegionClick() {
        if (ListUtils.isEmpty(getViewModel().getSubdivisions())) {
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);
        for (EHIRegion region : getViewModel().getSubdivisions()) {
            arrayAdapter.add(region.getSubdivisionName());
        }
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().setSelectedRegion(getViewModel().getSubdivisions().get(which));
                    }
                });
        builderSingle.show();

    }

    @Override
    public void isValid(boolean validation) {
        getViewModel().onFormChanged(validation);
    }

    private void prefillDriverInfo() {
        getViewModel().prefillDriverInfo();
    }

}
