package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.ListUtils;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class MyProfileViewModel extends CountrySpecificViewModel {

    ReactorVar<ProfileCollection> mUserProfile = new ReactorVar<>();

    public ProfileCollection getUserProfileCollection() {
        return mUserProfile.getValue();
    }

    public String getLicenceProfileCountryCode() {
        return mUserProfile.getRawValue().getLicenseProfile().getCountryCode();
    }

    public void setUserProfile(ProfileCollection userProfile) {
        mUserProfile.setValue(userProfile);
    }

    public boolean isNeedToRelogin() {
        return getManagers().getLoginManager().isNeedToRelogin();
    }

    public List<EHICountry> getCountries() {
        return getManagers().getLocalDataManager().getCountriesList();
    }

    public ProfileCollection getProfileFromManager() {
        return getManagers().getLoginManager().getProfileCollection();
    }

    public boolean shouldShowPaymentManagementActions() {
        return isUS() || isCanada();
    }

    public boolean shouldShowEditPaymentOptions() {
        boolean hasPaymentMethods = !ListUtils.isEmpty(mUserProfile.getRawValue().getPaymentProfile().getAllPaymentMethods());
        return shouldShowPaymentManagementActions() && hasPaymentMethods;
    }
}
