package com.ehi.enterprise.android.ui.profile;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.request_params.profile.PutProfileParams;
import com.ehi.enterprise.android.network.requests.profile.PutProfileRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.CountrySelectorViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Date;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LicenseInfoViewModel extends CountrySelectorViewModel {

    private static final String TAG = LicenseInfoViewModel.class.getSimpleName();

    final ReactorVar<ResponseWrapper> mSuccessWrapper = new ReactorVar<>();
    final ReactorViewState submitButton = new ReactorViewState();

    final ReactorVar<CharSequence> licenseNumberError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseIssueDateError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseExpiryDateError = new ReactorVar<>();

    public final ReactorVar<String> licenseNumber = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mLicenseProfile.getValue().setLicenseNumber(value);
            invalidateFieldsValues();
        }
    };

    public final ReactorVar<String> issueDate = new ReactorVar<String>(null) {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            if (value.isEmpty()) {
                value = null;
            }
            mLicenseProfile.getValue().setLicenseIssue(value);
            invalidateFieldsValues();
        }
    };

    public final ReactorVar<String> expiryDate = new ReactorVar<String>(null) {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            if (value.isEmpty()) {
                value = null;
            }
            mLicenseProfile.getValue().setLicenseExpiry(value);
            invalidateFieldsValues();
        }
    };

    public void setLicenseIssueDate(Date date) {
        EHILicenseProfile ehiLicenseProfile = mLicenseProfile.getRawValue();
        ehiLicenseProfile.setIssueDate(date);
        mLicenseProfile.setValue(ehiLicenseProfile);
        invalidateFieldsValues();
    }

    public void setLicenseExpiryDate(Date date) {
        EHILicenseProfile ehiLicenseProfile = mLicenseProfile.getRawValue();
        ehiLicenseProfile.setExpiryDate(date);
        mLicenseProfile.setValue(ehiLicenseProfile);
        invalidateFieldsValues();
    }

    public void setLicenseProfile(@NonNull EHILicenseProfile licenseProfile) {
        super.setLicenseProfile(licenseProfile);
        licenseNumber.setValue(licenseProfile.getLicenseNumber());
    }

    public void setSelectedRegion(EHIRegion selectedRegion) {
        mSelectedRegion.setValue(selectedRegion);
    }

    public EHIRegion getSelectedRegion() {
        return mSelectedRegion.getValue();
    }

    public ResponseWrapper getSuccessWrapper() {
        return mSuccessWrapper.getValue();
    }

    public void saveChanges() {

        if (!invalidateFieldsValues()) {
            return;
        }

        final EHILoyaltyData ehiLoyaltyData = getManagers().getLoginManager().getProfileCollection().getBasicProfile().getLoyaltyData();
        final String individualId = getManagers().getLoginManager().getProfileCollection().getProfile().getIndividualId();

        if (EHITextUtils.isEmpty(individualId) || ehiLoyaltyData == null) {
            return;
        }

        final EHILicenseProfile licenseProfile = mLicenseProfile.getValue();
        EHICountry selectedCountry = mSelectedCountry.getValue();
        EHIRegion selectedRegion = mSelectedRegion.getValue();
        if (selectedCountry != null) {
            licenseProfile.setCountryCode(mSelectedCountry.getValue().getCountryCode());
        }
        if (selectedCountry != null
                && selectedCountry.hasSubdivisions()
                && selectedRegion != null) {
            if (selectedCountry.isLicenseIssuingAuthorityRequired()){
                licenseProfile.setIssuingAuthority(selectedRegion.getSubdivisionCode());
                licenseProfile.setCountrySubdivisionCode(null);
                licenseProfile.setCountrySubdivisionName(null);
            } else {
                licenseProfile.setCountrySubdivisionName(selectedRegion.getSubdivisionName());
                licenseProfile.setCountrySubdivisionCode(selectedRegion.getSubdivisionCode());
                licenseProfile.setIssuingAuthority(null);
            }
        } else {
            licenseProfile.setCountrySubdivisionCode(null);
            licenseProfile.setCountrySubdivisionName(null);
            licenseProfile.setIssuingAuthority(null);
        }

        final PutProfileParams profileParams = new PutProfileParams.Builder()
                .setLoyaltyNumber(ehiLoyaltyData.getLoyaltyNumber())
                .setLicenseProfile(licenseProfile)
                .build();

        showProgress(true);
        performRequest(new PutProfileRequest(individualId, profileParams),
                new IApiCallback<EHIProfileResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            ProfileCollection profileCollection = getUserProfileCollection();
                            profileCollection.setLicenseProfile(response.getData().getLicenseProfile());
                            getManagers().getLoginManager().setProfile(profileCollection);
                            mSuccessWrapper.setValue(response);
                        } else {
                            setError(response);
                        }
                    }
                });
    }

    public boolean isLicenceNumberEmpty() {
        return TextUtils.isEmpty(mLicenseProfile.getValue().getLicenseNumber());
    }

    public ProfileCollection getProfileNoCache() {
        return getManagers().getLoginManager().getProfileNoCache();
    }

    public void highlightInvalidFields() {
        licenseNumberError.setValue(isLicenceNumberEmpty() ? " " : null);

        EHICountry ehiCountry = mSelectedCountry.getRawValue();
        if (ehiCountry == null) {
            return;
        }

        EHILicenseProfile ehiLicenseProfile = mLicenseProfile.getValue();

        if (ehiCountry.isLicenseIssueDateRequired()) {
            licenseIssueDateError.setValue(!isValid(ehiLicenseProfile.getLicenseIssue()) ? " " : null);
        } else {
            licenseIssueDateError.setValue(null);
        }

        if (ehiCountry.isLicenseExpiryDateRequired()) {
            licenseExpiryDateError.setValue(!isValid(ehiLicenseProfile.getLicenseExpiry()) ? " " : null);
        } else {
            licenseExpiryDateError.setValue(null);
        }

        if (!isNA() && !isAtLeastOneOptionalIssueAndExpiryFilled(ehiCountry, ehiLicenseProfile)) {
            licenseIssueDateError.setValue(" ");
            licenseExpiryDateError.setValue(" ");
        }

    }

    private boolean invalidateFieldsValues() {
        boolean formValid = !isLicenceNumberEmpty();

        EHICountry ehiCountry = mSelectedCountry.getRawValue();
        EHILicenseProfile ehiLicenseProfile = mLicenseProfile.getValue();

        if (ehiCountry != null && ehiCountry.isLicenseIssueDateRequired()) {
            formValid &= isValid(ehiLicenseProfile.getLicenseIssue());
        }

        if (ehiCountry != null && ehiCountry.isLicenseExpiryDateRequired()) {
            formValid &= isValid(ehiLicenseProfile.getLicenseExpiry());
        }

        if (!isNA()) {
            formValid &= isAtLeastOneOptionalIssueAndExpiryFilled(ehiCountry, ehiLicenseProfile);
        }

        highlightInvalidFields();
        submitButton.setEnabled(formValid);
        return formValid;
    }

    private boolean isValid(String field) {
        return EHITextUtils.isMaskedField(field) || !EHITextUtils.isEmpty(field);
    }

    private boolean isAtLeastOneOptionalIssueAndExpiryFilled(EHICountry ehiCountry, EHILicenseProfile ehiLicenseProfile) {
        return ehiCountry.isLicenseIssueDateOptional() && ehiCountry.isLicenseExpiryDateOptional()
                && isValid(ehiLicenseProfile.getLicenseIssue()) || isValid(ehiLicenseProfile.getLicenseExpiry());
    }

    private boolean isNA() {
        return EHICountry.COUNTRY_US.equalsIgnoreCase(getSelectedCountry().getCountryCode())
        || EHICountry.COUNTRY_CANADA.equalsIgnoreCase(getSelectedCountry().getCountryCode());
    }
}
