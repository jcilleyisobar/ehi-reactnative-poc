package com.ehi.enterprise.android.ui.viewmodel;

import android.content.Context;
import android.text.TextUtils;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIPromotionContract;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.support.EHISupportInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.contract.GetContractRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.contract.GetContractResponse;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.manager.IManagersDelegate;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

public class ManagersAccessViewModel extends BaseViewModel {

    private IManagersDelegate mManagersDelegate;
    private Context context;

    public IManagersDelegate getManagers() {
        return mManagersDelegate;
    }

    public void setManagersDelegate(IManagersDelegate managersDelegate) {
        mManagersDelegate = managersDelegate;
    }

    public String getForgotPasswordURL() {
        EHISupportInfo supportInfo = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale();
        if (supportInfo == null) {
            return null;
        }
        return supportInfo.getForgotPasswordURL();
    }

    public EHIPhone getValidPhoneNumber() {
        EHISupportInfo supportInfo = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale();
        if (supportInfo == null) {
            return null;
        }
        return supportInfo.getValidPhoneNumber();
    }

    public String getSupportPhoneNumber() {
        return getManagers().getSupportInfoManager().getContactUsPhoneNumberForCurrentLocale();
    }

    public String getEcForgotPasswordUrl() {
        EHISupportInfo info = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale();
        if (info != null) {
            return info.getEcForgotPasswordUrl();
        }
        return "";
    }

    public String getDNRPhoneNumber() {
        return getManagers().getSupportInfoManager().getDNRPhoneNumberForCurrentLocale();
    }

    public boolean needToShowDNRDialog() {
        return getManagers().getLoginManager().isLoggedIn()
                && getUserProfileCollection() != null
                && getUserProfileCollection().getLicenseProfile().isDoNotRentIndicator();
    }

    public boolean isUserLoggedIn() {
        return getManagers().getLoginManager().isLoggedIn();
    }

    public ProfileCollection getUserProfileCollection() {
        return getManagers().getLoginManager().getProfileCollection();
    }

    public String getCorporateAccountName(boolean isModify) {
        EHIReservation reservation = getEhiReservation(isModify);
        if (reservation != null
                && reservation.getCorporateAccount() != null
                && !EHITextUtils.isEmpty(reservation.getCorporateAccount().getContractName())) {
            return reservation.getCorporateAccount().getContractName();
        }
        return null;
    }

    public boolean is3rdPartyEmailNotify(boolean isModify) {
        EHIReservation reservation = getEhiReservation(isModify);
        if (reservation != null
                && reservation.getCorporateAccount() != null) {
            return reservation.getCorporateAccount().is3rdPartyEmailNotify();
        }
        return false;
    }

    public String getCorporateContractType(boolean isModify) {
        EHIReservation reservation = getEhiReservation(isModify);
        if (reservation != null
                && reservation.getCorporateAccount() != null
                && !TextUtils.isEmpty(reservation.getCorporateAccount().getContractType())) {
            return reservation.getCorporateAccount().getContractType();
        } else {
            return null;
        }
    }

    public String getCorporateAccountTermsAndConditions(boolean isModify) {
        EHIReservation reservation = getEhiReservation(isModify);
        if (reservation != null
                && reservation.getCorporateAccount() != null) {
            return reservation.getCorporateAccount().getTermsAndConditions();
        }
        return null;
    }

    protected EHIReservation getEhiReservation(boolean isModify) {
        EHIReservation reservation;
        if (isModify) {
            reservation = getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            reservation = getManagers().getReservationManager().getCurrentReservation();
        }
        return reservation;
    }

    public boolean isEmeraldClubDataSaved() {
        return getManagers().getReservationManager().isEmeraldClubDataSaved();
    }

    public boolean isLoggedIntoEmeraldClub() {
        return getManagers().getReservationManager().isLoggedIntoEmeraldClub();
    }

    public ProfileCollection getEmeraldClubProfile() {
        return getManagers().getReservationManager().getEmeraldClubProfile();
    }

    public boolean isFirstStartInGerman() {
        return getManagers().getLocalDataManager().isFirstStartInGerman();
    }

    public void setFirstStartInGerman(boolean firstStart) {
        getManagers().getLocalDataManager().setFirstStartInGerman(firstStart);
    }

    public boolean needShowPoints() {
        return getManagers().getLocalDataManager().needShowPoints()
                && getManagers().getLoginManager().isLoggedIn();
    }

    public boolean isWeekendSpecialAvailable() {
        return getManagers().getLocalDataManager().isWeekendSpecialAvailable()
                && getWeekendSpecialContract() != null;
    }

    public EHIContract getWeekendSpecialContract() {
        return getManagers().getLocalDataManager().getWeekendSpecialContract();
    }

    public void setNeedShowPoints(boolean showPoints) {
        getManagers().getLocalDataManager().setNeedShowPoints(showPoints);
    }

    public String getUserFeedbackUrl() {
        final String feedbackUrl = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getFeedbackUrl();
        return !TextUtils.isEmpty(feedbackUrl) ? feedbackUrl : Settings.USER_FEEDBACK_URL;
    }

    public boolean isPrepayEnabled(boolean isModify) {
        EHIReservation reservation;
        if (isModify) {
            reservation = getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            reservation = getManagers().getReservationManager().getCurrentReservation();
        }

        if (reservation != null) {
            return reservation.isPaymentProvidedPopulated()
                    && (reservation.doesCarClassListHasPrepayRates() ||
                        reservation.doesCarClassListHasPrepayCharges());
        } else {
            //no current res, assuming we're in profile
            return true;
        }
    }

    public boolean shouldSchedulePickupNotifications() {
        return getManagers().getSettingsManager().getPickupNotificationTime() != EHINotification.NotificationTime.OFF;
    }

    public EHINotification.NotificationTime getPickupNotificationTime() {
        return getManagers().getSettingsManager().getPickupNotificationTime();
    }

    public boolean shouldScheduleReturnNotifications() {
        return getManagers().getSettingsManager().getReturnNotificationTime() != EHINotification.NotificationTime.OFF;
    }

    public EHINotification.NotificationTime getReturnNotificationTime() {
        return getManagers().getSettingsManager().getReturnNotificationTime();
    }

    public boolean isEnterpriseRentalAssistantEnabled() {
        return getManagers().getSettingsManager().isEnterpriseRentalAssistantEnabled();
    }

    public void setEnterpriseRentalAssistantEnabled(boolean enabled) {
        getManagers().getSettingsManager().setEnterpriseRentalAssistant(enabled);
        if (!enabled) {
            getManagers().getGeofenceManger().removeAllGeofences();
        }
    }

    public void setPreferredRegion(String preferredRegion) {
        final LocalDataManager localDataManager = getManagers().getLocalDataManager();

        localDataManager.setPreferredRegion(preferredRegion);

        clearWeekendSpecialData();

        retrievePreferredRegionWeekendSpecialContract();
    }

    protected void clearWeekendSpecialData() {
        final LocalDataManager localDataManager = getManagers().getLocalDataManager();

        localDataManager.removeWeekendSpecialContract();
        localDataManager.setShouldShowWeekendSpecialModal(true);
    }

    public boolean isWeekendSpecialContractRequestDone() {
        return mIsWeekendSpecialContractRequestDone.getValue();
    }

    protected void retrievePreferredRegionWeekendSpecialContract() {
        final LocalDataManager localDataManager = getManagers().getLocalDataManager();

        if (!localDataManager.isWeekendSpecialAvailable()) {
            mIsWeekendSpecialContractRequestDone.setValue(true);
            return;
        }

        final EHICountry country = getManagers().getLocalDataManager().getPreferredCountry();
        final EHIPromotionContract promotionContract = country.getWeekendSpecialPromotion();

        showProgress(true);

        performRequest(new GetContractRequest(promotionContract.getContractNumber()), new IApiCallback<GetContractResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetContractResponse> response) {
                if (response.isSuccess()) {
                    final EHIContract contract = response.getData().getContractDetails();

                    if (contract != null) {
                        getManagers().getLocalDataManager().setWeekendSpecialContract(contract);
                    }
                }

                showProgress(false);

                mIsWeekendSpecialContractRequestDone.setValue(true);
            }
        });
    }

    public boolean isAvailableAtContractRate() {
        return isAvailableAt(EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE);
    }

    public boolean isAvailableAtPromotionalRate() {
        return isAvailableAt(EHICarClassDetails.AVAILABLE_AT_PROMOTIONAL_RATE);
    }

    private boolean isAvailableAt(@EHICarClassDetails.Availability String availability) {
        final EHICarClassDetails carClass = getManagers().getReservationManager().getSelectedCarClass();

        return carClass != null && carClass.getStatus() != null && carClass.getStatus().equals(availability);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}