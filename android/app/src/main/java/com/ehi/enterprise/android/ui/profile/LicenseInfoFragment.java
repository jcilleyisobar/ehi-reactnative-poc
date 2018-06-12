package com.ehi.enterprise.android.ui.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LicenseInfoFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(LicenseInfoViewModel.class)
public class LicenseInfoFragment extends DataBindingViewModelFragment<LicenseInfoViewModel, LicenseInfoFragmentBinding> {

    public static final String SCREEN_NAME = "LicenseInfoFragment";
    private static final String TAG = LicenseInfoFragment.class.getSimpleName();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().country) {
                showCountrySelector();
            } else if (view == getViewBinding().subdivision) {
                showSubdivisionSelector();
            } else if (view == getViewBinding().expirationDate) {
                showExpirationDatePickerDialog();
            } else if (view == getViewBinding().issueDate) {
                showIssueDatePickerDialog();
            } else if (view == getViewBinding().saveChanges) {
                getViewModel().saveChanges();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().saveChanges) {
                getViewModel().highlightInvalidFields();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileCollection profile = getViewModel().getProfileNoCache();
        if (profile == null) {
            getActivity().finish();
            return;
        }
        getViewModel().setLicenseProfile(profile.getLicenseProfile());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_license_info, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, LicenseInfoFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_EDIT_DRIVER_INFO.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().country.setOnClickListener(mOnClickListener);
        getViewBinding().subdivision.setOnClickListener(mOnClickListener);
        getViewBinding().expirationDate.setOnClickListener(mOnClickListener);
        getViewBinding().issueDate.setOnClickListener(mOnClickListener);
        getViewBinding().saveChanges.setOnClickListener(mOnClickListener);
        getViewBinding().saveChanges.setOnDisabledClickListener(mOnDisabledClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().licenseNumber, getViewBinding().driverLicense));
        bind(ReactorTextView.bindText(getViewModel().issueDate, getViewBinding().issueDate));
        bind(ReactorTextView.bindText(getViewModel().expiryDate, getViewBinding().expirationDate));

        bind(ReactorView.enabled(getViewModel().submitButton.enabled(), getViewBinding().saveChanges));

        bind(ReactorTextInputLayout.error(getViewModel().licenseNumberError, getViewBinding().driverLicenseLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseIssueDateError, getViewBinding().issueDateLayout));
        bind(ReactorTextInputLayout.error(getViewModel().licenseExpiryDateError, getViewBinding().expirationDateLayout));

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        addReaction("LICENCE_PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHILicenseProfile profile = getViewModel().getLicenseProfile();
                if (profile != null) {
                    updateDataFromModel(profile);
                }
            }
        });

        addReaction("SUCCESS_UPDATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getSuccessWrapper() != null) {
                    getActivity().finish();
                }
            }
        });

        addReaction("COUNTRY_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHICountry selectedCountry = getViewModel().getSelectedCountry();
                if (selectedCountry == null) {
                    return;
                }

                getViewBinding().country.setText(selectedCountry.getCountryName());
                if (selectedCountry.getCountryCode() != null
                        && selectedCountry.getCountryCode().length() > 0) {
                    if (selectedCountry.hasSubdivisions()) {
                        getViewBinding().subdivisionArea.setVisibility(View.VISIBLE);
                    } else {
                        getViewBinding().subdivisionArea.setVisibility(View.GONE);
                    }

                    if (selectedCountry.shouldShowIssueDate()) {
                        getViewBinding().issueDateArea.setVisibility(View.VISIBLE);
                    } else {
                        getViewBinding().issueDateArea.setVisibility(View.GONE);
                    }

                    if (selectedCountry.shouldShowExpiryDateOnEditScreen()) {
                        getViewBinding().expirationDateArea.setVisibility(View.VISIBLE);
                    } else {
                        getViewBinding().expirationDateArea.setVisibility(View.GONE);
                    }

                    getViewModel().requestRegionsForCountry(selectedCountry, true);
                } else {
                    getViewBinding().subdivisionArea.setVisibility(View.GONE);
                }
            }
        });

        addReaction("REGION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIRegion selectedRegion = getViewModel().getSelectedRegion();
                if (selectedRegion == null) {
                    return;
                }

                if (selectedRegion.getSubdivisionName() != null) {
                    getViewBinding().subdivision.setText(selectedRegion.getSubdivisionName());
                } else {
                    getViewBinding().subdivision.setText("");
                }
            }
        });
    }

    private void updateDataFromModel(EHILicenseProfile profile) {
        getViewBinding().country.setText(profile.getCountryName());
        if (getViewModel().getSelectedCountry()!=null
                && getViewModel().getSelectedCountry().isLicenseIssuingAuthorityRequired()){
            getViewBinding().subdivision.setText(profile.getIssuingAuthority());
        } else {
            getViewBinding().subdivision.setText(profile.getCountrySubdivisionName());
        }
        getViewBinding().driverLicense.setText(profile.getLicenseNumber());
        getViewBinding().expirationDate.setText(profile.getLicenseExpiry());
        getViewBinding().issueDate.setText(profile.getLicenseIssue());
    }

    private void showExpirationDatePickerDialog() {
        DialogUtils.showDatePicker(
                getActivity(),
                R.string.profile_license_expiration_date_title,
                getViewModel().getLicenseProfile().getExpiryDate(),
                new DialogUtils.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        getViewModel().setLicenseExpiryDate(selectedDate);
                    }
                }
        );
    }

    private void showIssueDatePickerDialog() {
        DialogUtils.showDatePicker(
                getActivity(),
                R.string.profile_license_issue_date,
                getViewModel().getLicenseProfile().getIssueDate(),
                new DialogUtils.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        getViewModel().setLicenseIssueDate(selectedDate);
                    }
                }
        );
    }

    private void showSubdivisionSelector() {
        if (getViewModel().getSubdivisions().size() == 0) {
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

    private void showCountrySelector() {
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

}
