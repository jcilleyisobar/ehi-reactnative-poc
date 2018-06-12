package com.ehi.enterprise.android.ui.reservation.modify;

import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.reservation.ReservationFlowControlViewModel;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PAY_LATER;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PREPAY;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.REDEMPTION;

@AutoUnbindAll
public class ModifyItineraryViewModel extends ReservationFlowControlViewModel {

    public EHIReservation getCurrentModifyReservation() {
        return getManagers().getReservationManager().getCurrentModifyReservation();
    }

    public ReservationFlowListener.PayState getPayState() {
        final EHIReservation currentModifyReservation = getCurrentModifyReservation();

        if (currentModifyReservation.isPrepaySelected()) {
            return PREPAY;
        }

        if (getRedemptionDayCount(currentModifyReservation) > 0
                || getRedemptionPointsUsed(currentModifyReservation) > 0) {
            return REDEMPTION;
        }

        return PAY_LATER;
    }

    private int getRedemptionDayCount(EHIReservation currentModifyReservation) {
        int daysCount = 0;

        if (currentModifyReservation.getCarClassDetails() != null) {
            daysCount = currentModifyReservation.getCarClassDetails().getRedemptionDayCount();
        }

        if (daysCount == 0) {
            daysCount = currentModifyReservation.getRedemptionDayCount();
        }

        return daysCount;
    }

    private int getRedemptionPointsUsed(EHIReservation currentModifyReservation) {
        int pointsUsed = 0;

        if (currentModifyReservation.getCarClassDetails() != null) {
            pointsUsed = currentModifyReservation.getCarClassDetails().getEplusPointsUsed();
        }

        if (pointsUsed == 0) {
            pointsUsed = currentModifyReservation.getEplusPointsUsed();
        }

        return pointsUsed;
    }

}
