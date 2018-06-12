package com.ehi.enterprise.android.ui.dashboard.widget;

import android.text.TextUtils;
import android.view.View;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.EHIVehicleDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ActiveRentalViewModel extends ManagersAccessViewModel {

    //region ReactorVars
    final ReactorViewState headerContainer = new ReactorViewState();
    final ReactorViewState vehicleNameHeader = new ReactorViewState();
    final ReactorTextViewState vehicleName = new ReactorTextViewState();
    final ReactorTextViewState vehicleColor = new ReactorTextViewState();
    final ReactorViewState vehicleColorHeader = new ReactorViewState();
    final ReactorTextViewState vehiclePlateNumber = new ReactorTextViewState();
    final ReactorViewState vehiclePlateNumberHeader = new ReactorViewState();
    final ReactorTextViewState returnDateTime = new ReactorTextViewState();
    final ReactorTextViewState returnLocation = new ReactorTextViewState();
    final ReactorTextViewState findGasStationButton = new ReactorTextViewState();
    final ReactorTextViewState returnInstructionsButton = new ReactorTextViewState() {
        @Override
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);
        }

        @Override
        public void setVisible(boolean visibility) {
            super.setVisible(visibility);
        }

        @Override
        public ReactorVar<Integer> visibility() {
            return super.visibility();
        }
    };
    final ReactorTextViewState getDirectionsButton = new ReactorTextViewState();
    final ReactorViewState rateMyRideButton = new ReactorViewState();
    //endregion

    private EHITripSummary mTripSummary;

    public void setTripSummary(final EHITripSummary tripSummary) {
        mTripSummary = tripSummary;

        EHIVehicleDetails vehicleDetails = tripSummary.getVehicleDetails();

        if (EHITextUtils.areAllEmpty(vehicleDetails.getName(),
                vehicleDetails.getColor(),
                vehicleDetails.getLicensePlateNumber())) {
            if (EHITextUtils.areAllEmpty(vehicleDetails.getMake(), vehicleDetails.getModel())) {
                headerContainer.setVisibility(View.GONE);
            } else {
                headerContainer.setVisibility(View.VISIBLE);
            }
        } else {
            headerContainer.setVisibility(View.VISIBLE);
        }

        if (!EHITextUtils.isEmpty(vehicleDetails.getMake())
                && !EHITextUtils.isEmpty(vehicleDetails.getModel())) {
            vehicleName.setText(vehicleDetails.getMake() + " " + vehicleDetails.getModel());
            vehicleNameHeader.setVisibility(View.VISIBLE);
            vehicleName.setVisibility(View.VISIBLE);
        } else if ((EHITextUtils.isEmpty(vehicleDetails.getMake()) || EHITextUtils.isEmpty(vehicleDetails.getModel()))
                && !EHITextUtils.isEmpty(vehicleDetails.getName())) {
            vehicleName.setText(vehicleDetails.getName());
            vehicleNameHeader.setVisibility(View.VISIBLE);
            vehicleName.setVisibility(View.VISIBLE);
        } else {
            vehicleNameHeader.setVisibility(View.GONE);
            vehicleName.setVisibility(View.GONE);
        }

        if (EHITextUtils.isEmpty(vehicleDetails.getColor())) {
            vehicleColorHeader.setVisibility(View.GONE);
            vehicleColor.setVisibility(View.GONE);
        } else {
            vehicleColor.setText(vehicleDetails.getColor());
            vehicleColorHeader.setVisibility(View.VISIBLE);
            vehicleColor.setVisibility(View.VISIBLE);
        }

        if (EHITextUtils.isEmpty(vehicleDetails.getLicensePlateNumber())) {
            vehiclePlateNumberHeader.setVisibility(View.GONE);
            vehiclePlateNumber.setVisibility(View.GONE);
        } else {
            vehiclePlateNumber.setText(vehicleDetails.getLicensePlateNumber());
            vehiclePlateNumberHeader.setVisibility(View.VISIBLE);
            vehiclePlateNumber.setVisibility(View.VISIBLE);
        }

        if (mTripSummary.getReturnTime() != null) {
            String returnDateTimeString = getManagers().getDateUtilManager()
                    .formatDateTime(mTripSummary.getReturnTime(),
                            DateUtilManager.FORMAT_SHOW_TIME,
                            DateUtilManager.FORMAT_SHOW_YEAR,
                            DateUtilManager.FORMAT_SHOW_DATE,
                            DateUtilManager.FORMAT_ABBREV_MONTH);
            returnDateTime.setText(returnDateTimeString);
        }

        final EHILocation returnLocation = mTripSummary.getReturnLocation();
        if (returnLocation != null) {
            if (returnLocation.getGpsCoordinates() == null) {
                hideLocationCallToActionButtons();
            }

            if (EHITextUtils.isEmpty(returnLocation.getName())) {
                this.returnLocation.setVisibility(ReactorViewState.GONE);
            } else {
                this.returnLocation.setText(returnLocation.getName());
            }

            if (returnLocation.getGreenLocationCellIconDrawable() > 0) {
                this.returnLocation.setDrawableLeft(returnLocation.getGreenLocationCellIconDrawable());
            }
        }

        if (!TextUtils.isEmpty(tripSummary.getRateMyRideUrl())) {
            rateMyRideButton.setVisibility(ReactorViewState.VISIBLE);
        } else {
            rateMyRideButton.setVisibility(ReactorViewState.GONE);
        }
    }

    private void hideLocationCallToActionButtons() {
        findGasStationButton.setVisibility(ReactorViewState.GONE);
        getDirectionsButton.setVisibility(ReactorViewState.GONE);

    }

    public void setupReturnButtonInstructions(boolean isScheduledForAfterHours) {
        returnInstructionsButton.setVisibility(isScheduledForAfterHours ? View.VISIBLE : View.GONE);
    }

    public EHILocation getReturnLocation() {
        return mTripSummary.getReturnLocation();
    }

    public EHITripSummary getTripSummary() {
        return mTripSummary;
    }
}
