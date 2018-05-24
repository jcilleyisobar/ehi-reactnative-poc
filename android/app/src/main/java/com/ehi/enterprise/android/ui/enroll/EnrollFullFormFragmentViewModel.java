package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.enroll.PostCloneEnrollProfileRequest;
import com.ehi.enterprise.android.network.requests.enroll.PostEnrollProfileRequest;
import com.ehi.enterprise.android.network.requests.location.GetRegionsRequest;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetEPlusTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.enroll.PostEnrollProfileResponse;
import com.ehi.enterprise.android.network.responses.location.GetRegionsResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.LinkedList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnrollFullFormFragmentViewModel extends CountrySpecificViewModel {

    final ReactorViewState continueButton = new ReactorViewState();
    final ReactorVar<Boolean> commitSuccessful = new ReactorVar<>(false);
    final ReactorVar<GetEPlusTermsAndConditionsResponse> mTermsAndConditions = new ReactorVar<>();
    final ReactorVar<ResponseWrapper> termsError = new ReactorVar<>();

    private String loyaltyNumber;

    public void onFormChanged(boolean validation) {
        continueButton.setEnabled(validation);
    }

    public EHIEnrollProfile getEnrollProfile() {
        return getManagers().getLocalDataManager().getEnrollProfile();
    }

    public void persistUpdatedEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        getManagers().getLocalDataManager().setEnrollmentProfile(ehiEnrollProfile);
    }

    IApiCallback<PostEnrollProfileResponse> mCommitCallback = new IApiCallback<PostEnrollProfileResponse>() {
        @Override
        public void handleResponse(ResponseWrapper<PostEnrollProfileResponse> response) {
            showProgress(false);

            if (!response.isSuccess()) {
                errorResponse.setValue(response);
                return;
            }

            final ProfileCollection ehiProfile = response.getData();
            getManagers().getLoginManager().setProfile(ehiProfile);
            loyaltyNumber = ehiProfile.getBasicProfile().getLoyaltyData().getLoyaltyNumber();
            commitSuccessful.setValue(true);
        }
    };

    public void commitEnroll() {
        showProgress(true);
        if (getEnrollProfile().isDriverFound()) {
            performRequest(new PostCloneEnrollProfileRequest(getEnrollProfile()), mCommitCallback);
        } else {
            performRequest(new PostEnrollProfileRequest(getEnrollProfile()), mCommitCallback);
        }
    }

    public String getLoyaltyNumber() {
        return loyaltyNumber;
    }

    public void requestTermsAndConditions() {
        showProgress(true);

        performRequest(new GetEPlusTermsAndConditionsRequest(), new IApiCallback<GetEPlusTermsAndConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetEPlusTermsAndConditionsResponse> response) {
                showProgress(false);

                if (!response.isSuccess()) {
                    termsError.setValue(response);
                    return;
                }

                mTermsAndConditions.setValue(response.getData());
            }
        });
    }

    public GetEPlusTermsAndConditionsResponse getTermsAndConditionsResponse() {
        return mTermsAndConditions.getValue();
    }

    public void setTermsAndConditions(GetEPlusTermsAndConditionsResponse termsAndConditions) {
        mTermsAndConditions.setValue(termsAndConditions);
    }

    // country stuff
    private List<EHICountry> mCountries;

    public List<EHICountry> getCountries() {
        if (mCountries == null) {
            mCountries = getManagers().getLocalDataManager().getCountriesList();
        }

        return mCountries;
    }

    public void populateRegionsFor(final CountryContract.CountryView countryView, boolean isForLicenceProfile) {
        showProgress(true);
        if (isForLicenceProfile
                && countryView.getCountry().isSpecialIssuingAuthorityRequired()) {
            List<EHIRegion> regions = new LinkedList<>();
            regions.add(new EHIRegion(countryView.getCountry().getIssuingAuthorityName(),
                    countryView.getCountry().getIssuingAuthorityName()));
            countryView.setRegionList(regions);
        } else {
            performRequest(new GetRegionsRequest(countryView.getCountryCode()), new IApiCallback<GetRegionsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetRegionsResponse> response) {
                    showProgress(false);

                    if (!response.isSuccess()) {
                        // error
                        return;
                    }

                    countryView.setRegionList(response.getData().getRegions());
                }
            });
        }
    }

    public String getSelectedCountryCode() {
        return getEnrollProfile().getEhiAddressProfile().getCountryCode();
    }

    public String getState() {
        return getManagers().getLocalDataManager().getAnalyticsState();
    }
}
