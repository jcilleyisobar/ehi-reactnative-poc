package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

public class DetailsSectionViewModel extends ManagersAccessViewModel {

    private EHIReservation ehiReservation;
    private String tripPurpose;
    private boolean isModify;
    private EHIDriverInfo ehiDriverInfo;
    private List<EHIAdditionalInformation> additionalInformationList;
    private boolean isConfirmation;

    public EHIReservation getEhiReservation() {
        return ehiReservation;
    }

    public void setEhiReservation(EHIReservation ehiReservation) {
        this.ehiReservation = ehiReservation;
    }

    public String getTripPurpose() {
        return tripPurpose;
    }

    public void setTripPurpose(String tripPurpose) {
        this.tripPurpose = tripPurpose;
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    public List<EHIAdditionalInformation> getAdditionalInformationList() {
        return additionalInformationList;
    }

    public void setAdditionalInformationList(List<EHIAdditionalInformation> additionalInformationList) {
        this.additionalInformationList = additionalInformationList;
    }

    public boolean isConfirmation() {
        return isConfirmation;
    }

    public void setConfirmation(boolean confirmation) {
        isConfirmation = confirmation;
    }
}
