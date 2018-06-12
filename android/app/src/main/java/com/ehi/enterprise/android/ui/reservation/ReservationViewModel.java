package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;

import io.dwak.reactor.ReactorVar;

public class ReservationViewModel extends CountrySpecificViewModel {

    private ReactorVar<EHIReservation> ehiReservationReactorVar = new ReactorVar<EHIReservation>() {
        @Override
        public void setValue(EHIReservation value) {
            super.setValue(value);
            if (value != null) {
                addOrUpdateOngoingReservation(value);
                onEHIReservationUpdate();
            }
        }
    };

    protected void onEHIReservationUpdate() {
    }

    public void setReservationObject(EHIReservation ehiReservation) {
        ehiReservationReactorVar.setValue(ehiReservation);
    }

    public EHIReservation getReservationObject() {
        return ehiReservationReactorVar.getValue();
    }

    public void setRawReservationObject(EHIReservation ehiReservation) {
        ehiReservationReactorVar.setRawValue(ehiReservation);
    }

    public EHIReservation getRawReservationObject() {
        return ehiReservationReactorVar.getRawValue();
    }

    public void setIsModify(Boolean isModify) {
        getManagers().getReservationManager().setModify(isModify != null && isModify);
    }

    public boolean isModify() {
        return getManagers().getReservationManager().isModify();
    }

    public EHIReservation getOngoingReservation() {
        if (isModify()) {
            return getManagers().getReservationManager().getCurrentModifyReservation();
        }

        return getManagers().getReservationManager().getCurrentReservation();
    }

    public void addOrUpdateOngoingReservation(@NonNull EHIReservation ehiReservation) {
        if (isModify()) {
            getManagers().getReservationManager().addOrUpdateModifyReservation(ehiReservation);
        } else {
            getManagers().getReservationManager().addOrUpdateReservation(ehiReservation);
        }
    }
}
