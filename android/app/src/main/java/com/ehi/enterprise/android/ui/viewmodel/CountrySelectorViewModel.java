package com.ehi.enterprise.android.ui.viewmodel;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetCountriesRequest;
import com.ehi.enterprise.android.network.requests.location.GetRegionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.android.network.responses.location.GetRegionsResponse;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

import java.util.LinkedList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CountrySelectorViewModel extends ManagersAccessViewModel {

    protected final ReactorVar<EHICountry> mSelectedCountry = new ReactorVar<>();
    protected final ReactorVar<EHIRegion> mSelectedRegion = new ReactorVar<>();

    private List<EHICountry> mCountries;
    private List<EHIRegion> mSubdivisions;
    protected final ReactorVar<EHILicenseProfile> mLicenseProfile = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        if (mLicenseProfile.getValue() == null) {
            mLicenseProfile.setValue(new EHILicenseProfile());
        }

        final LocalDataManager localDataManager = getManagers().getLocalDataManager();
        if (ListUtils.isEmpty(localDataManager.getCountriesList())) {
            showProgress(true);
            performRequest(new GetCountriesRequest(), new IApiCallback<GetCountriesResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetCountriesResponse> response) {
                    if (response.isSuccess()) {
                        localDataManager.setCountriesList(response.getData().getCountries());
                        refreshCountries();
                    }

                    showProgress(false);
                }
            });
        } else {
            refreshCountries();
        }
    }

    public void requestRegionsForCountry(EHICountry country, boolean isForLicenceProfile) {
        if (isForLicenceProfile
                && country.isSpecialIssuingAuthorityRequired()) {
            EHIRegion specialIssuingAuthority = new EHIRegion(country.getIssuingAuthorityName(), country.getIssuingAuthorityName());

            mSubdivisions = new LinkedList<>();
            mSubdivisions.add(specialIssuingAuthority);

            final EHILicenseProfile licenseProfile = mLicenseProfile.getValue();
            if (EHITextUtils.isMaskedField(licenseProfile.getIssuingAuthority())) {
                mSelectedRegion.setValue(specialIssuingAuthority);
                return;
            }
            prepopulateSelectedSubdivision();
        } else {
            showProgress(true);
            performRequest(new GetRegionsRequest(country.getCountryCode()), new IApiCallback<GetRegionsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetRegionsResponse> response) {
                    showProgress(false);
                    if (response.isSuccess()) {
                        mSubdivisions = response.getData().getRegions();
                        final EHILicenseProfile licenseProfile = mLicenseProfile.getValue();
                        if (EHITextUtils.isMaskedField(licenseProfile.getCountrySubdivisionCode())) {
                            mSelectedRegion.setValue(new EHIRegion(licenseProfile.getCountrySubdivisionName(), licenseProfile.getCountrySubdivisionCode()));
                            return;
                        }
                        prepopulateSelectedSubdivision();
                    } else {
                        mSelectedRegion.setValue(new EHIRegion());
                        errorResponse.setValue(response);
                    }
                }
            });
        }
    }

    private void prepopulateSelectedSubdivision(){
        final EHILicenseProfile licenseProfile = mLicenseProfile.getValue();
        if (mSubdivisions != null && mSubdivisions.size() > 0) {
            if (mSelectedCountry.getValue().getCountryCode().equalsIgnoreCase(licenseProfile.getCountryCode())) {
                for (EHIRegion region : mSubdivisions) {
                    if (region.getSubdivisionCode().equalsIgnoreCase(
                            licenseProfile.getIssuingAuthorityOrSubdivisionCodeForCountry(mSelectedCountry.getRawValue()))) {
                        mSelectedRegion.setValue(region);
                        return;
                    }
                }
            }
            mSelectedRegion.setValue(mSubdivisions.get(0));
        } else {
            mSelectedRegion.setValue(new EHIRegion());
        }
    }

    public void setSelectedRegion(EHIRegion selectedRegion) {
        mSelectedRegion.setValue(selectedRegion);
        mLicenseProfile.getValue().setCountrySubdivisionCode(mSelectedRegion.getRawValue().getSubdivisionCode());
        mLicenseProfile.getValue().setCountrySubdivisionName(mSelectedRegion.getRawValue().getSubdivisionName());
    }

    public EHIRegion getSelectedRegion() {
        return mSelectedRegion.getValue();
    }

    public void setLicenseProfile(@NonNull EHILicenseProfile licenseProfile) {
        mLicenseProfile.setValue(licenseProfile);
        if (mCountries == null) {
            mCountries = getManagers().getLocalDataManager().getCountriesList();
        }
        for (EHICountry c : mCountries) {
            if (c.getCountryCode().equalsIgnoreCase(licenseProfile.getCountryCode())) {
                mSelectedCountry.setValue(c);
                break;
            }
        }
    }

    public EHICountry getSelectedCountry() {
        return mSelectedCountry.getValue();
    }

    public EHILicenseProfile getLicenseProfile() {
        return mLicenseProfile.getValue();
    }

    public List<EHIRegion> getSubdivisions() {
        return mSubdivisions;
    }

    public List<EHICountry> getCountries() {
        return mCountries;
    }

    public void refreshCountries() {
        if (mCountries == null) {
            mCountries = getManagers().getLocalDataManager().getCountriesList();
        }

        if (mSelectedCountry.getValue() == null) {
            final EHICountry preferredCountry = getManagers().getLocalDataManager().getPreferredCountry();
            if (preferredCountry != null) {
                setSelectedCountry(preferredCountry);
            }
        }
    }

    public void setSelectedCountry(EHICountry selectedCountry) {
        if (mSelectedCountry.getValue() == null
                || !selectedCountry.getCountryCode().equalsIgnoreCase(mSelectedCountry.getRawValue().getCountryCode())) {
            mSelectedCountry.setValue(selectedCountry);
            mLicenseProfile.getValue().setCountryCode(mSelectedCountry.getRawValue().getCountryCode());
            mLicenseProfile.getValue().setCountryName(mSelectedCountry.getRawValue().getCountryName());
        }
    }
}
