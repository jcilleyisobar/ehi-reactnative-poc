package com.ehi.enterprise.android.ui.enroll;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EnrollFullFormFragmentBinding;
import com.ehi.enterprise.android.models.EHIErrorMessage;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.enroll.PostEnrollProfileResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(EnrollFullFormFragmentViewModel.class)
public class EnrollFullFormFragment extends DataBindingViewModelFragment<EnrollFullFormFragmentViewModel, EnrollFullFormFragmentBinding> {

    public static final String TAG = "EnrollFullFormFragment";

    @Extra(value = ArrayList.class, type = String.class)
    public static String ERROR_MESSAGE_LIST = "ERROR_MESSAGE_LIST";

    private ArrayList<String> mErrorMessageList = new ArrayList<>();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_ENROLL_LONG_FORM.value, TAG)
                        .state(getViewModel().getState())
                        .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                                getViewModel().getState(),
                                getViewModel().getSelectedCountryCode()))
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN.value)
                        .tagScreen()
                        .tagEvent();
                continueToConfirmation();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                warnInvalidFields();
            }
        }
    };

    private View.OnClickListener termsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().requestTermsAndConditions();
        }
    };

    private FormContract.FormListener formListener = new FormContract.FormListener() {
        @Override
        public void isValid(boolean validation) {
            getViewModel().onFormChanged(
                    getViewBinding().enrollStepOneView.isValid()
                            && getViewBinding().enrollStepTwoView.isValid()
                            && getViewBinding().enrollStepThreeView.isValid()
            );
        }
    };

    private CountryContract.CountryListener stepOneCountryListener = new CountryContract.CountryListener() {
        @Override
        public void onCountryClick() {
            onCountryView(getViewBinding().enrollStepOneView, true);
        }

        @Override
        public void onRegionClick() {
            onRegionView(getViewBinding().enrollStepOneView);
        }
    };

    private CountryContract.CountryListener stepTwoCountryListener = new CountryContract.CountryListener() {
        @Override
        public void onCountryClick() {
            onCountryView(getViewBinding().enrollStepTwoView,false);
        }

        @Override
        public void onRegionClick() {
            onRegionView(getViewBinding().enrollStepTwoView);
        }
    };

    private DialogInterface.OnClickListener mErrorDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            warnInvalidFields();
        }
    };

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            warnInvalidFields();
        }
    };

    private DialogInterface.OnClickListener mOnExitListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_enroll_full_form, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_LONG_FORM.value, TAG)
                .state(getViewModel().getState())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_LOAD.value)
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_enroll, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enroll_exit_action) {
            showExitDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);

        getViewBinding().enrollStepOneView.setFormListener(formListener);
        getViewBinding().enrollStepOneView.setCountryListener(stepOneCountryListener);
        getViewBinding().enrollStepOneView.setPresetData(getViewModel().getEnrollProfile());

        getViewBinding().enrollStepTwoView.setFormListener(formListener);
        getViewBinding().enrollStepTwoView.setCountryListener(stepTwoCountryListener);
        getViewBinding().enrollStepTwoView.setPresetData(getViewModel().getEnrollProfile());

        getViewBinding().enrollStepThreeView.setFormListener(formListener);
        getViewBinding().enrollStepThreeView.setTermsAndConditionsClickListener(termsClickListener);
        getViewBinding().enrollStepThreeView.setPresetData(getViewModel().getEnrollProfile(), getViewModel().needCheckEmailNotificationsByDefault());
        getViewBinding().enrollStepThreeView.setScrollView(getViewBinding().scrollView);

        // set country on each view
        setCountry(getViewBinding().enrollStepOneView,true);
        setCountry(getViewBinding().enrollStepTwoView,false);

        // errors - uncomment to get the step 3 errors on screen again
//        EnrollFullFormFragmentHelper.Extractor extractor = new EnrollFullFormFragmentHelper.Extractor(this);
//        mErrorMessageList = extractor.errorMessageList();
//        getViewBinding().enrollErrorView.setErrorMessageList(mErrorMessageList);
    }

    private void onCountryView(final CountryContract.CountryView countryView, final boolean isForLicenceProfile) {
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
                        final EHICountry ehiCountry = getViewModel().getCountries().get(which);
                        if (countryView.getCountry() != null && countryView.getCountry().getCountryCode().equals(ehiCountry.getCountryCode())) {
                            return;
                        }

                        countryView.setCountry(ehiCountry);

                        getViewModel().populateRegionsFor(countryView, isForLicenceProfile);
                    }
                });
        builderSingle.show();
    }

    private void onRegionView(final CountryContract.CountryView countryView) {
        if (ListUtils.isEmpty(countryView.getRegionList())) {
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);
        for (EHIRegion region : countryView.getRegionList()) {
            arrayAdapter.add(region.getSubdivisionName());
        }
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EHIRegion ehiRegion = countryView.getRegionList().get(which);
                        countryView.setRegion(ehiRegion);
                    }
                });
        builderSingle.show();

    }

    private void continueToConfirmation() {
        EHIEnrollProfile ehiEnrollProfile = getViewModel().getEnrollProfile();

        ehiEnrollProfile = getViewBinding().enrollStepOneView.updateEnrollProfile(
                ehiEnrollProfile);

        ehiEnrollProfile = getViewBinding().enrollStepTwoView.updateEnrollProfile(
                ehiEnrollProfile
        );

        ehiEnrollProfile = getViewBinding().enrollStepThreeView.updateEnrollProfile(
                ehiEnrollProfile
        );

        getViewModel().persistUpdatedEnrollProfile(ehiEnrollProfile);

        getViewModel().commitEnroll();
    }

    private void warnInvalidFields() {
        getViewBinding().enrollStepOneView.highlightInvalidFields();
        getViewBinding().enrollStepOneView.startHighlightInvalidFieldsOnFormChange();

        getViewBinding().enrollStepTwoView.highlightInvalidFields();
        getViewBinding().enrollStepTwoView.startHighlightInvalidFieldsOnFormChange();

        getViewBinding().enrollStepThreeView.highlightInvalidFields();
        getViewBinding().enrollStepThreeView.startHighlightInvalidFieldsOnFormChange();

        // to add the ORCH errors into the error list, just add mErrorMessageList into this list
        List<String> errorMessageList = new ArrayList<>();
        errorMessageList.addAll(getViewBinding().enrollStepOneView.getErrorMessageList());
        errorMessageList.addAll(getViewBinding().enrollStepTwoView.getErrorMessageList());
        errorMessageList.addAll(getViewBinding().enrollStepThreeView.getErrorMessageList());

        getViewBinding().enrollErrorView.setErrorMessageList(
                errorMessageList
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

    private void showErrorDialog(ResponseWrapper<PostEnrollProfileResponse> errorResponse) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            stringBuilder.append(errorResponse.getCodes())
                    .append(":");
        }

        stringBuilder.append(errorResponse.getMessage());

        final List<EHIErrorMessage> ehiErrorMessages = errorResponse.getData().getMessages();

        mErrorMessageList = new ArrayList<>();

        for (final EHIErrorMessage ehiErrorMessage : ehiErrorMessages) {
            mErrorMessageList.add(ehiErrorMessage.getErrorMessage());
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(stringBuilder.toString())
                .setTitle(R.string.enroll_title)
                .setPositiveButton(getString(R.string.alert_okay_title), mErrorDialogListener)
                .setOnDismissListener(mOnDismissListener)
                .create()
                .show();
    }

    private void setCountry(final CountryContract.CountryView countryView, boolean isForLicenceProfile) {
        if (EHITextUtils.isEmpty(countryView.getCountryCode())) {
            return;
        }

        final List<EHICountry> countryList = getViewModel().getCountries();

        for (final EHICountry ehiCountry : countryList) {
            if (ehiCountry.getCountryCode().equals(countryView.getCountryCode())) {
                countryView.setCountry(ehiCountry);
                break;
            }
        }

        getViewModel().populateRegionsFor(countryView, isForLicenceProfile);
    }

    private void showExitDialog() {
        DialogUtils.showOkCancelDialog(
                getActivity(),
                getString(R.string.enroll_exit_dialog_title),
                getString(R.string.enroll_exit_dialog_message),
                mOnExitListener
        );
    }
}
