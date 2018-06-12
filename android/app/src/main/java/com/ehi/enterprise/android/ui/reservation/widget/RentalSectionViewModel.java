package com.ehi.enterprise.android.ui.reservation.widget;

import android.text.TextUtils;

import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;

public class RentalSectionViewModel extends ManagersAccessViewModel {

    private EHIReservation ehiReservation;
    private String screenName;
    private boolean isModify;

    private boolean isNorthAmerica;

    public EHIReservation getEhiReservation() {
        return ehiReservation;
    }

    public void setEhiReservation(EHIReservation ehiReservation) {
        this.ehiReservation = ehiReservation;
    }

    public void setNorthAmerica(boolean northAmerica) {
        isNorthAmerica = northAmerica;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setIsModify(boolean isModify) {
        this.isModify = isModify;
    }

    public boolean isModify() {
        return isModify;
    }

    public String getCorporateAccountId() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getCorporateAccount() != null
                && !EHITextUtils.isEmpty(reservation.getCorporateAccount().getContractNumber())) {
            return reservation.getCorporateAccount().getContractNumber();
        }
        return null;
    }

    public String getCorporateAccountName() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getCorporateAccount() != null
                && !EHITextUtils.isEmpty(reservation.getCorporateAccount().getContractName())) {
            return reservation.getCorporateAccount().getContractName();
        }
        return null;
    }

    public String getCorporateContractType() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getCorporateAccount() != null
                && !TextUtils.isEmpty(reservation.getCorporateAccount().getContractType())) {
            return reservation.getCorporateAccount().getContractType();
        } else {
            return null;
        }
    }

    public String getCorporateAccountTermsAndConditions() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getCorporateAccount() != null) {
            return reservation.getCorporateAccount().getTermsAndConditions();
        }
        return null;
    }

    public boolean is3rdPartyEmailNotify() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getCorporateAccount() != null) {
            return reservation.getCorporateAccount().is3rdPartyEmailNotify();
        }
        return false;
    }

    public boolean shouldShowBlockLocationChangePopup() {
        return isModify() && ehiReservation.shouldBlockModifyPickupLocation();
    }

    public boolean shouldTrackChangePickupDropOffEvents() {
        return isModify() && !ehiReservation.shouldBlockModifyPickupLocation();
    }

    public EHIExtras getExtras() {
        final EHIReservation reservation = getEhiReservation();
        if (reservation != null
                && reservation.getExtras() != null) {
            return reservation.getExtras();
        }
        return null;
    }

}
