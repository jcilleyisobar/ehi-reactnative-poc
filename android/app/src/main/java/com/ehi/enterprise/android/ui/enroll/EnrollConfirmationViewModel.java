package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.authentication.PostLoginRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;

public class EnrollConfirmationViewModel extends CountrySpecificViewModel {

    private String memberNumber;
    private String password;
    private Integer currentDrawer;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        attemptLogin();
    }

    private void attemptLogin() {
        showProgress(true);
        PostLoginRequest postLoginRequest = new PostLoginRequest(
                memberNumber,
                password,
                true,
                getManagers().getLoginManager().getTermsConditionsVersion()
        );

        performRequest(postLoginRequest, new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    final ProfileCollection profileCollection = response.getData();
                    getManagers().getLoginManager().login(
                            response.getData().getEncryptedAuthData(),
                            response.getData().getAuthToken(),
                            memberNumber,
                            response.getData(),
                            needCheckRememberMeByDefault(),
                            currentDrawer);

                    getManagers().getLoginManager().setLastLoginTime(System.currentTimeMillis());
                    getManagers().getLoginManager().setSavingData(needCheckRememberMeByDefault());
                    getManagers().getReservationManager().addOrUpdateDriverInfo(getDriverInfo(profileCollection));
                }
            }
        });
    }

    private EHIDriverInfo getDriverInfo(ProfileCollection profileCollection) {
        EHIDriverInfo driverInfo = new EHIDriverInfo();
        if (profileCollection.getContactProfile() != null) {
            EHIContactProfile contactProfile = profileCollection.getContactProfile();
            if (contactProfile.getEmail() != null) {
                driverInfo.setEmailAddress(contactProfile.getEmail());
            }
            if (contactProfile.getPhones().size() != 0) {
                driverInfo.setPhoneNumber(contactProfile.getPhones().get(0).getPhoneNumber());
            } else {
                driverInfo.setPhoneNumber("00000");
            }
        }
        if (profileCollection.getBasicProfile() != null) {
            driverInfo.setFirstName(profileCollection.getBasicProfile().getFirstName());
            driverInfo.setLastName(profileCollection.getBasicProfile().getLastName());
        }

        if (profileCollection.getPreference() != null && profileCollection.getPreference().getEmailPreference() != null) {
            driverInfo.setRequestEmailPromotions(needCheckEmailNotificationsByDefault(), profileCollection.getPreference().getEmailPreference().isSpecialOffers());
        }
        return driverInfo;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getState() {
        return getManagers().getLocalDataManager().getAnalyticsState();
    }

    public String getSelectedCountryCode() {
        return getEnrollProfile().getEhiLicenseProfile().getCountryCode();
    }

    private EHIEnrollProfile getEnrollProfile() {
        return getManagers().getLocalDataManager().getEnrollProfile();
    }

    public void setCurrentDrawer(Integer currentDrawer) {
        this.currentDrawer = currentDrawer;
    }
}
