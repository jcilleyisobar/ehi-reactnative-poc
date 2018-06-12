package com.ehi.enterprise.android.ui.reservation.modify;

import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.reservation.ReservationFlowControlViewModel;

public class ModifyReviewActivityViewModel extends ReservationFlowControlViewModel {

    private boolean mBackButtonBlocked;

    public boolean isBackButtonBlocked() {
        return mBackButtonBlocked;
    }

    public void setBackButtonBlocked(boolean backButtonBlocked) {
        mBackButtonBlocked = backButtonBlocked;
    }

    public String getPrePayOriginalAmount() {
        final EHIReservation ehiReservation = getModifyReservationObject();

        if (ehiReservation == null || !ehiReservation.isPrepaySelected()) {
            return null;
        }

        return ehiReservation.getCarClassDetails().getPrepayPriceSummary()
                .getEstimatedTotalView().getFormattedPrice(true).toString();
    }

    private EHIReservation getModifyReservationObject() {
        return getManagers().getReservationManager().getCurrentModifyReservation();
    }
}
