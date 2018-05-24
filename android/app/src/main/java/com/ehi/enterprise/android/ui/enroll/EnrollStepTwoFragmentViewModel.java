package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.viewmodel.CountrySelectorViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnrollStepTwoFragmentViewModel extends CountrySelectorViewModel {

    final ReactorViewState submitButton = new ReactorViewState();
    private boolean mDriverFound;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        if (mDriverFound) {
            return;
        }

        final EHIEnrollProfile profile = getEnrollProfile();

        if (profile.getEhiAddressProfile() != null) {
            setCountryCode(profile.getEhiAddressProfile().getCountryCode());
            setSelectedRegion(new EHIRegion(
                    profile.getEhiAddressProfile().getCountrySubdivisionName(),
                    profile.getEhiAddressProfile().getCountrySubdivisionCode()));
        } else {
            setCountryCode(profile.getEhiLicenseProfile().getCountryCode());
            setSelectedRegion(new EHIRegion(
                    profile.getEhiLicenseProfile().getCountrySubdivisionName(),
                    profile.getEhiLicenseProfile().getCountrySubdivisionCode()));
        }
    }


    public void setDriverFound(boolean driverFound) {
        mDriverFound = driverFound;
    }

    private void setCountryCode(String code) {
        for (final EHICountry country : getCountries()) {
            if (country.getCountryCode().equals(code)) {
                setSelectedCountry(country);
                break;
            }
        }
    }

    public void onFormChanged(boolean validation) {
        submitButton.setEnabled(validation && getSelectedCountry() != null);
    }

    public EHIEnrollProfile getEnrollProfile() {
        return getManagers().getLocalDataManager().getEnrollProfile();
    }

    public void persistUpdatedEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        getManagers().getLocalDataManager().setEnrollmentProfile(ehiEnrollProfile);
    }

    public String getState() {
        return getManagers().getLocalDataManager().getAnalyticsState();
    }
}
