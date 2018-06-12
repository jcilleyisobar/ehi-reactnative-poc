package com.ehi.enterprise.android.ui.reservation.interfaces;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.ui.reservation.CarClassListFragment;

import java.util.List;

public interface ReservationFlowListener {

    enum PayState {PREPAY, PAY_LATER, REDEMPTION}

    void showItinerary(boolean edit, boolean clearLocation);

    void showAvailableCarClasses(boolean edit);

    void showCarDetails(@NonNull EHICarClassDetails carClassDetails);

    void showCarExtras(@NonNull EHICarClassDetails carClassDetails, boolean edit, final PayState payState, final boolean fromChooseYourRate);

    void showChooseYourRateScreen(@NonNull EHICarClassDetails carClassDetails, boolean edit);

    void carListAnimationInProgress(boolean inProgress, AnimatingViewCallback callback);

    void showDriverInfo(String price, EHIDriverInfo driverInfo, boolean edit);

    void showMultiTerminal(List<EHIAirlineDetails> ehiAirlineDetails, boolean edit);

    void showReview();

    void showDeliveryAndCollection();

    CarClassListFragment.AnimationDataHolder getAnimationData();

    void setAnimationData(CarClassListFragment.AnimationDataHolder data);

    void showRedemption(EHICarClassDetails ehiCarClassDetails, final boolean fromChooseYourRate);

    void showingModal(boolean showingModal);

    interface AnimatingViewCallback {
        void backPressed();
    }

    PayState getPayState();

    void setPayState(final PayState payState);

    boolean needShowRateScreen(@NonNull EHICarClassDetails carClassDetails, String contractType);
}