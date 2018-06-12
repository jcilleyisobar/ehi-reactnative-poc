package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIRenterSearchCriteria;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.profile.PostSearchProfileRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.profile.PostSearchProfileResponse;
import com.ehi.enterprise.android.ui.viewmodel.CountrySelectorViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnrollStepOneFragmentViewModel extends CountrySelectorViewModel {

    final ReactorViewState submitButton = new ReactorViewState();
    private final ReactorVar<Boolean> shouldGoToNextStep = new ReactorVar<>(false);
    private final ReactorVar<Boolean> shouldShowEPLogin = new ReactorVar<>(false);
    private final ReactorVar<Boolean> shouldNavigateToCompleteEnroll = new ReactorVar<>(false);
    private String memberNumber;
    private boolean driverFound;
    private boolean emeraldClub;

    public void onFormChanged(boolean validation) {
        submitButton.setEnabled(validation && getSelectedCountry() != null);
    }

    public void searchProfile(EHIRenterSearchCriteria searchObject) {
        showProgress(true);
        performRequest(new PostSearchProfileRequest(searchObject), new IApiCallback<PostSearchProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<PostSearchProfileResponse> response) {
                showProgress(false);
                if (response.isSuccess()
                        // ignoring parse error case response body is null
                        || response.getStatus() == ResponseWrapper.JSON_PARSING_ERROR) {
                    PostSearchProfileResponse data = response.getData();
                    driverFound = false;
                    if (data != null && data.getProfile() != null) {
                        if (data.isBranchEnrolled()) {
                            shouldNavigateToCompleteEnroll.setValue(true);
                            return;
                        }

                        int code = 0;
                        if (data.getProfile() != null && data.getProfile().getBasicProfile() != null) {
                            code = data.getProfile().getBasicProfile().getLoyaltyData().getLoyaltyProgramCode();
                        }

                        switch (code) {
                            case EHILoyaltyData.ENTERPRISE_PLUS:
                                setState(EHIAnalytics.State.STATE_EP_MATCH);
                                memberNumber = data.getProfile().getBasicProfile().getLoyaltyData().getLoyaltyNumber();
                                shouldShowEPLogin.setValue(true);
                                break;
                            case EHILoyaltyData.EMERALD_CLUB:
                                populateEnrollProfile(data);
                                driverFound = true;
                                emeraldClub = true;
                                setState(EHIAnalytics.State.STATE_EMERALD_CLUB);
                                shouldGoToNextStep.setValue(true);
                                break;
                            case EHILoyaltyData.NON_LOYALTY:
                                // account not created but has data from previous reservations
                                populateEnrollProfile(data);
                                driverFound = true;
                                setState(EHIAnalytics.State.STATE_NON_LOYALTY);
                                shouldGoToNextStep.setValue(true);
                                break;
                            default:
                                setState(EHIAnalytics.State.STATE_NO_MATCH);
                                shouldGoToNextStep.setValue(true);
                                break;
                        }
                    } else {
                        setState(EHIAnalytics.State.STATE_NO_MATCH);
                        shouldGoToNextStep.setValue(true);
                    }
                } else {
                    errorResponse.setValue(response);
                }
            }
        });
    }

    private void populateEnrollProfile(PostSearchProfileResponse data) {
        if (data == null) return;
        final EHIEnrollProfile enrollProfile = getEnrollProfile();
        enrollProfile.setIndividualId(data.getBasicProfile().getLoyaltyData().getId());
        if (data.getProfile() != null) {
            enrollProfile.setEhiAddressProfile(data.getAddressProfile());
            enrollProfile.setEhiLicenseProfile(data.getLicenseProfile());
            enrollProfile.setPreference(data.getPreference());
            persistUpdatedEnrollProfile(enrollProfile);
        }
        final EHIContactProfile contactProfile = data.getContactProfile();
        if (contactProfile != null) {
            enrollProfile.setPhoneNumberList(contactProfile.getPhones());
            enrollProfile.setEmail(EHITextUtils.isEmpty(contactProfile.getMaskEmail()) ?
                    contactProfile.getEmail() :
                    contactProfile.getMaskEmail());
        }
    }

    public String getActivationLink() {
        return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getActivateUrl();
    }

    public boolean shouldGoToNextStep() {
        return shouldGoToNextStep.getValue();
    }

    public void setGoneToNextStep() {
        shouldGoToNextStep.setValue(false);
    }

    public void prefillDriverInfo() {
        final EHIDriverInfo ehiDriverInfo = getManagers().getReservationManager().getDriverInfo();
        if (ehiDriverInfo == null) {
            return;
        }

        final EHIEnrollProfile enrollProfile = getEnrollProfile();

        enrollProfile.setFirstName(ehiDriverInfo.getFirstName());
        enrollProfile.setLastName(ehiDriverInfo.getLastName());
        enrollProfile.setEmail(ehiDriverInfo.getEmailAddress());

        List<EHIPhone> phoneNumberList = new ArrayList<>();
        phoneNumberList.add(ehiDriverInfo.getPhone());

        enrollProfile.setPhoneNumberList(phoneNumberList);

        persistUpdatedEnrollProfile(enrollProfile);
    }

    public EHIEnrollProfile getEnrollProfile() {
        return getManagers().getLocalDataManager().getEnrollProfile();
    }

    public void persistUpdatedEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        getManagers().getLocalDataManager().setEnrollmentProfile(ehiEnrollProfile);
    }

    public boolean shouldShowEPLogin() {
        return shouldShowEPLogin.getValue();
    }

    public void setEPLoginShowed() {
        shouldShowEPLogin.setValue(false);
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public boolean shouldNavigateToCompleteEnroll() {
        return shouldNavigateToCompleteEnroll.getValue();
    }

    public void setNavigatedToCompleteEnroll() {
        shouldNavigateToCompleteEnroll.setValue(false);
    }

    public boolean isDriverFound() {
        return driverFound;
    }

    private void setState(EHIAnalytics.State state) {
        getManagers().getLocalDataManager().setAnalyticsState(state);
    }

    public boolean isEmeraldClub() {
        return emeraldClub;
    }
}
