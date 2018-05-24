 package com.ehi.enterprise.android.ui.reservation;


 import android.support.annotation.NonNull;

 import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
 import com.ehi.enterprise.android.models.reservation.EHIContract;
 import com.ehi.enterprise.android.models.reservation.EHIReservation;
 import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
 import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

 import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReservationFlowControlViewModel extends ManagersAccessViewModel {

    public boolean needShowPaymentScreen(@NonNull EHICarClassDetails carClassDetails, ReservationFlowListener.PayState payState, String contractType, boolean isModify) {
        if (isPrepayAvailable(isModify) && !EHIContract.CONTRACT_TYPE_CORPORATE.equalsIgnoreCase(contractType)) {
            // if we have only one option of payment we should skip
            switch (payState) {
                case PAY_LATER:
                    return carClassDetails.isPrepayRateAvailable() || carClassDetails.getRedemptionPoints() > 0;
                case PREPAY:
                    return carClassDetails.isPayLaterRateAvailable();
                case REDEMPTION:
                    return carClassDetails.isPayLaterRateAvailable() || carClassDetails.isPrepayRateAvailable();
            }
        }
        return false;
    }

    public boolean isPrepayAvailable(boolean isModify) {
        final EHIReservation reservation = getEhiReservation(isModify);
        return reservation.isPaymentProvidedPopulated() && reservation.doesCarClassListHasPrepayCharges();
    }
}
