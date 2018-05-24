package com.ehi.enterprise.android.ui.enroll;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollStepTwoFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(EnrollStepTwoFragmentViewModel.class)
public class EnrollStepTwoFragment extends DataBindingViewModelFragment<EnrollStepTwoFragmentViewModel, EnrollStepTwoFragmentBinding>
        implements CountryContract.CountryListener, FormContract.FormListener {

    @Extra(Boolean.class)
    public static final String DRIVER_FOUND = "DRIVER_FOUND";

    public static final String TAG = "EnrollStepTwoFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewModel().persistUpdatedEnrollProfile(
                        getViewBinding().enrollStepTwoView.updateEnrollProfile(
                                getViewModel().getEnrollProfile()
                        )
                );
                final String state = getViewModel().getState();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_2.value, TAG)
                        .state(state)
                        .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                                state,
                                getViewModel().getSelectedCountry().getCountryCode()))
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NEXT.value)
                        .tagScreen()
                        .tagEvent();
                ((EnrollFlowListener) getActivity()).goToStepThree();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                getViewBinding().enrollStepTwoView.highlightInvalidFields();
                getViewBinding().enrollStepTwoView.startHighlightInvalidFieldsOnFormChange();
                getViewBinding().enrollErrorView.setErrorMessageList(
                        getViewBinding().enrollStepTwoView.getErrorMessageList()
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
        createViewBinding(inflater, R.layout.fr_enroll_step_two, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_2.value, TAG)
                .state(getViewModel().getState())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_ENROLLMENT_STEP_2.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(
                        getViewModel().getState(),
                        getViewModel().getSelectedCountry().getCountryCode()))
                .tagScreen()
                .tagMacroEvent()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);
        getViewBinding().enrollStepTwoView.setCountryListener(this);
        getViewBinding().enrollStepTwoView.setFormListener(this);

        getViewBinding().stepTextView.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "2")
                .addTokenAndValue(EHIStringToken.STEP_COUNT, EnrollActivity.TOTAL_STEPS)
                .formatString(R.string.enroll_step)
                .format());

        getViewBinding().enrollStepTwoView.hideHeader();

        if (getViewModel().getEnrollProfile().getEhiAddressProfile() != null) {
            EnrollStepTwoFragmentHelper.Extractor extractor = new EnrollStepTwoFragmentHelper.Extractor(this);
            getViewModel().setDriverFound(extractor.driverFound());
            if (extractor.driverFound()) {
                getViewBinding().title.setText(R.string.enroll_driver_profile_found_title);
            }
        }
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
                getViewBinding().enrollStepTwoView.setCountry(selectedCountry);
                getViewModel().requestRegionsForCountry(selectedCountry, false);
            }
        });

        addReaction("REGION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIRegion selectedRegion = getViewModel().getSelectedRegion();
                if (selectedRegion == null) {
                    return;
                }
                getViewBinding().enrollStepTwoView.setRegion(selectedRegion);
            }
        });
    }

    @Override
    public void onCountryClick() {
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
}
