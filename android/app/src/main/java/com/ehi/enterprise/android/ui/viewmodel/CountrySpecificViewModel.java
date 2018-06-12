package com.ehi.enterprise.android.ui.viewmodel;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;

import java.util.Date;
import java.util.Locale;

public class CountrySpecificViewModel extends ManagersAccessViewModel {

    //general things

    public ReservationFlowListener.PayState getDefaultPayState(boolean isModify) {
        if (isUS() || isCanada() || !isPrepayEnabled(isModify)) {
            return ReservationFlowListener.PayState.PAY_LATER;
        }
        return ReservationFlowListener.PayState.PREPAY;
    }

    public boolean isGerman() {
        return isFromCountry(EHICountry.COUNTRY_GERMANY);
    }

    public boolean isFrench() {
        return isFromCountry(EHICountry.COUNTRY_FRANCE);
    }

    public boolean isUS() {
        return isFromCountry(EHICountry.COUNTRY_US);
    }

    public boolean isNorthAmerica() {
        return isUS() || isCanada();
    }

    public boolean isNorthAmericaPrepayAvailable(boolean isModify) {
        return isNorthAmerica() && isPrepayEnabled(isModify);
    }

    public boolean isCanada() {
        return isFromCountry(EHICountry.COUNTRY_CANADA);
    }

    private boolean isFromCountry(@EHICountry.CountryCode String country) {
        final String countryCode = getManagers().getLocalDataManager().getPreferredCountryCode();
        return countryCode != null && countryCode.equalsIgnoreCase(country);
    }

    //shared with iOs

    public boolean needShowFeedbackMenu() {
        return (isFromCountry(EHICountry.COUNTRY_US) && Locale.getDefault().getLanguage().equals("en"))
                || (isFromCountry(EHICountry.COUNTRY_CANADA) && Locale.getDefault().getLanguage().equals("en"));
    }

    public boolean needCacheDriverInfoByDefault() {
        return isFromCountry(EHICountry.COUNTRY_US);
    }

    public boolean needPromptDataTrackingOnFirstRun() {
        return isGerman() && isFirstStartInGerman();
    }

    public boolean needShowDataCollectionReminder() {
        long timestamp = getManagers().getLocalDataManager().getDataCollectionReminderNextShowTimestamp();
        return !isNorthAmerica() &&
                (timestamp == 0 || isFrench() && new Date().after(new Date(timestamp)));
    }

    public boolean needCheckEmailNotificationsByDefault() {
        //provided by ORCH
        return getManagers().getLocalDataManager().isPreferredRegionEmailOptInEnabled();
    }

    public boolean needCheckRememberMeByDefault() {
        return isFromCountry(EHICountry.COUNTRY_US);
    }

    //android specific things

    public boolean needShowRegionsChoice() {
        return !getManagers().getLocalDataManager().havePreferredRegion()
                && !(isFromCountry(EHICountry.COUNTRY_US)
                || isFromCountry(EHICountry.COUNTRY_CANADA));
    }

    public void generateDefaultPreferredRegion() {
        if (!getManagers().getLocalDataManager().havePreferredRegion()) {
            setPreferredRegion(Locale.getDefault().getCountry());
        } else {
            mIsWeekendSpecialContractRequestDone.setValue(true);
        }
    }

    public String getSupportWebsite() {
        if (isFromCountry(EHICountry.COUNTRY_US)) {
            return "http://www.enterprise.com/";
        } else {
            return "http://www.enterprise.co.uk/";
        }
    }

    public String getCountryCode() {
        return getManagers().getLocalDataManager().getPreferredCountryCode();
    }
}
