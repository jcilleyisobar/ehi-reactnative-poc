package com.ehi.enterprise.android.ui.profile;

import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIEmailPreference;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIPreference;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.request_params.profile.PutProfileParams;
import com.ehi.enterprise.android.network.requests.location.GetRegionsRequest;
import com.ehi.enterprise.android.network.requests.profile.PutProfileRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetRegionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.LinkedList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class MemberInfoViewModel extends ManagersAccessViewModel {

    ReactorVar<EHIAddressProfile> mAddressProfile = new ReactorVar<>();
    ReactorVar<EHIProfile> mProfile = new ReactorVar<>();
    ReactorVar<EHIContactProfile> mContactProfile = new ReactorVar<>();
    ReactorVar<EHIEmailPreference> mEmailPreferences = new ReactorVar<>();
    ReactorVar<EHICountry> mSelectedCountry = new ReactorVar<>();
    ReactorVar<EHIRegion> mSelectedRegion = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mGeneralSuccessWrapper = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mGeneralErrorWrapper = new ReactorVar<>();
    //region OVERRIDE REACTORVARS
    ReactorVar<String> email = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mContactProfile.getValue().setEmail(value);
        }
    };

    ReactorVar<String> street1 = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mAddressProfile.getValue().setStreetAddress(0, value);
        }
    };

    ReactorVar<String> street2 = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mAddressProfile.getValue().setStreetAddress(1, value);
        }
    };

    ReactorVar<String> city = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mAddressProfile.getValue().setCity(value);
        }
    };

    ReactorVar<String> zip = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            mAddressProfile.getValue().setPostal(value);
        }
    };
    //endregion
    private List<EHICountry> mCountries;
    private List<EHIRegion> mSubdivisions;

    public EHIProfile getProfile() {
        return mProfile.getValue();
    }

    public void setProfile(EHIProfile profile) {
        mProfile.setValue(profile);
    }

    public EHIAddressProfile getAddressProfile() {
        return mAddressProfile.getValue();
    }

    public void setAddressProfile(EHIAddressProfile addressProfile) {
        mAddressProfile.setValue(addressProfile);
        if (!isAddressEmpty()) {
            if (!isStreet1Empty()) {
                street1.setValue(getAddressProfile().getStreetAddresses().get(0));
            }
            if (!isStreet2Empty()) {
                street2.setValue(getAddressProfile().getStreetAddresses().get(1));
            }
        }
        city.setValue(getAddressProfile().getCity());
        zip.setValue(getAddressProfile().getPostal());

        mCountries = getManagers().getLocalDataManager().getCountriesList();
        for (EHICountry c : mCountries) {
            if (c.getCountryCode().equalsIgnoreCase(addressProfile.getCountryCode())) {
                mSelectedCountry.setValue(c);
                break;
            }
        }
    }

    public EHIContactProfile getContactProfile() {
        return mContactProfile.getValue();
    }

    public void setContactProfile(EHIContactProfile contactProfile) {
        mContactProfile.setValue(contactProfile);
        email.setValue(getContactProfile().getMaskEmail());
    }

    public ResponseWrapper getGeneralSuccessWrapper() {
        return mGeneralSuccessWrapper.getValue();
    }

    public void setGeneralSuccessWrapper(ResponseWrapper generalSuccessWrapper) {
        mGeneralSuccessWrapper.setValue(generalSuccessWrapper);
    }

    public ResponseWrapper getGeneralErrorWrapper() {
        return mGeneralErrorWrapper.getValue();
    }

    public void setGeneralErrorWrapper(ResponseWrapper generalErrorWrapper) {
        mGeneralErrorWrapper.setValue(generalErrorWrapper);
    }

    public EHICountry getSelectedCountry() {
        return mSelectedCountry.getValue();
    }

    public void setSelectedCountry(EHICountry selectedCountry) {
        mSelectedCountry.setValue(selectedCountry);
    }

    public EHIRegion getSelectedRegion() {
        return mSelectedRegion.getValue();
    }

    public void setSelectedRegion(EHIRegion selectedRegion) {
        mSelectedRegion.setValue(selectedRegion);
    }

    public List<EHICountry> getCountries() {
        return mCountries;
    }

    public void setCountries(List<EHICountry> countries) {
        mCountries = countries;
    }

    public List<EHIRegion> getSubdivisions() {
        return mSubdivisions;
    }

    public void setSubdivisions(List<EHIRegion> subdivisions) {
        mSubdivisions = subdivisions;
    }

    public EHIEmailPreference getEmailPreferences() {
        return mEmailPreferences.getValue();
    }

    public void setEmailPreferences(EHIEmailPreference emailPreferences) {
        mEmailPreferences.setValue(emailPreferences);
    }

    public void requestRegionsForCountry(String country) {
        performRequest(new GetRegionsRequest(country), new IApiCallback<GetRegionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetRegionsResponse> response) {
                if (response.isSuccess()) {
                    mSubdivisions = response.getData().getRegions();
                    mSelectedRegion.setValue(new EHIRegion(getAddressProfile().getCountrySubdivisionName(), getResources().getString(R.string.bullet_prefix)));
                }
            }
        });
    }

    public void saveChanges() {
        final PutProfileParams profileParams = new PutProfileParams.Builder()
                .setPreference(getUpdatedPreference())
                .setLoyaltyNumber(getLoyaltyNumber())
                .setAddress(getUpdatedAddressProfile())
                .setContactProfile(getUpdatedContactProfile()).build();

        performRequest(new PutProfileRequest(getProfileNoCache().getProfile().getIndividualId(), profileParams), new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                if (response.isSuccess()) {
                    mGeneralSuccessWrapper.setValue(response);
                    getManagers().getLoginManager().setProfile(response.getData());
                } else {
                    mGeneralErrorWrapper.setValue(response);
                }
            }
        });
    }

    private EHIPreference getUpdatedPreference() {
        return new EHIPreference(getEmailPreferences(), Settings.SOURCE_CODE);
    }

    private EHIAddressProfile getUpdatedAddressProfile() {
        final EHIAddressProfile addressProfile = mAddressProfile.getValue();
        EHICountry selectedCountry = mSelectedCountry.getValue();
        EHIRegion selectedRegion = mSelectedRegion.getValue();
        if (selectedCountry != null) {
            addressProfile.setCountryCode(mSelectedCountry.getValue().getCountryCode());
            addressProfile.setCountryName(mSelectedCountry.getValue().getCountryName());
        }
        if (selectedCountry != null
                && selectedCountry.hasSubdivisions()
                && selectedRegion != null) {
            addressProfile.setCountrySubdivisionCode(selectedRegion.getSubdivisionCode());
        } else {
            addressProfile.setCountrySubdivisionCode("");
        }
        return addressProfile;
    }

    private EHIContactProfile getUpdatedContactProfile() {
        List<EHIPhone> numbers = mContactProfile.getValue().getPhones();
        List<EHIPhone> notEmptyNumbers = new LinkedList<>();
        for (EHIPhone number : numbers) {
            if (!TextUtils.isEmpty(number.getPhoneNumber())) {
                notEmptyNumbers.add(number);
            }
        }
        mContactProfile.getValue().setPhones(notEmptyNumbers);

        return mContactProfile.getValue();
    }

    public boolean isEmailEmpty() {
        return TextUtils.isEmpty(mContactProfile.getValue().getEmail());
    }

    public boolean isMainPhoneNumberEmpty() {
        return TextUtils.isEmpty(mContactProfile.getValue().getPhone(0).getPhoneNumber());
    }

    public boolean isAddressEmpty() {
        return mAddressProfile.getValue().getStreetAddresses().size() == 0;
    }

    public boolean isStreet1Empty() {
        if (mAddressProfile.getValue().getStreetAddresses().size() != 0) {
            return TextUtils.isEmpty(mAddressProfile.getValue().getStreetAddresses().get(0));
        }
        else {
            return true;
        }
    }

    public boolean isStreet2Empty() {
        if (mAddressProfile.getValue().getStreetAddresses().size() > 1) {
            return TextUtils.isEmpty(mAddressProfile.getValue().getStreetAddresses().get(1));
        }
        else {
            return true;
        }
    }

    public boolean isCityEmpty() {
        return TextUtils.isEmpty(mAddressProfile.getValue().getCity());
    }

    public boolean isZipEmpty() {
        return TextUtils.isEmpty(mAddressProfile.getValue().getPostal());
    }

    public ProfileCollection getProfileNoCache() {
        return getManagers().getLoginManager().getProfileNoCache();
    }

    public boolean hasSubdivisions(String countruCode) {
        if (mCountries != null) {
            for (EHICountry c : mCountries) {
                if (c.getCountryCode().equalsIgnoreCase(countruCode)) {
                    return c.hasSubdivisions();
                }
            }
        }
        return false;
    }

    public String getLoyaltyNumber() {
        final EHILoyaltyData ehiLoyaltyData = mProfile.getValue().getBasicProfile().getLoyaltyData();
        if (ehiLoyaltyData == null) {
            return null;
        }
        return ehiLoyaltyData.getLoyaltyNumber();
    }
}
