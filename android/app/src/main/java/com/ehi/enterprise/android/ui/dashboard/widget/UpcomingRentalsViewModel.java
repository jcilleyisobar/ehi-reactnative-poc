package com.ehi.enterprise.android.ui.dashboard.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Calendar;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class UpcomingRentalsViewModel extends ManagersAccessViewModel {

    //region ReactorVars
    final ReactorTextViewState seeYouMessage = new ReactorTextViewState();
    final ReactorTextViewState confirmationNumber = new ReactorTextViewState();
    final ReactorTextViewState pickupDateTime = new ReactorTextViewState();
    final ReactorTextViewState pickupLocation = new ReactorTextViewState();
    final ReactorVar<List<EHIImage>> vehicleImageUrls = new ReactorVar<>();
    final ReactorImageViewState locationPin = new ReactorImageViewState();
    final ReactorViewState upcomingRentalsDirectionsFromTerminal = new ReactorViewState();
    final ReactorVar<String> mRentalPickupLocationName = new ReactorVar<>();
    final ReactorVar<String> mRentalPickupAirportCode = new ReactorVar<>();
    //endregion

    final @EHIImageUtils.ImageType int vehicleImageType = EHIImageUtils.IMAGE_TYPE_THREE_QUARTER;
    private EHITripSummary mTripSummary;
    private List<EHIWayfindingStep> mWayfindings;

    public EHITripSummary getTripSummary() {
        return mTripSummary;
    }

    public List<EHIWayfindingStep> getWayfindings() {
        return mWayfindings;
    }

    public void setTripSummary(EHITripSummary tripSummary) {
        mTripSummary = tripSummary;
        confirmationNumber.setText("#" + mTripSummary.getConfirmationNumber());

        vehicleImageUrls.setValue(mTripSummary.getVehicleDetails().getImages());
        if(mTripSummary.getPickupLocation().getGreenLocationCellIconDrawable() != -1){
            locationPin.setImageResource(mTripSummary.getPickupLocation().getGreenLocationCellIconDrawable());
        } else {
            locationPin.setVisibility(View.GONE);
        }

        mRentalPickupLocationName.setValue(mTripSummary.getPickupLocation().getName());
        mRentalPickupAirportCode.setValue(mTripSummary.getPickupLocation().getAirportCode());

        setPickupTime();
        formatDateTime();
    }

    private void formatDateTime(){
        String pickupDate = getManagers().getDateUtilManager()
                                         .formatDateTime(mTripSummary.getPickupTime(),
                                                         DateUtilManager.FORMAT_SHOW_YEAR,
                                                         DateUtilManager.FORMAT_SHOW_DATE,
                                                         DateUtilManager.FORMAT_ABBREV_MONTH);
        String pickupTime = getManagers().getDateUtilManager()
                                         .formatDateTime(mTripSummary.getPickupTime(),
                                                         DateUtilManager.FORMAT_SHOW_TIME);
        CharSequence tokenizedPickupDateTime = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.DATE, pickupDate)
                .addTokenAndValue(EHIStringToken.TIME, pickupTime)
                .formatString(R.string.user_rental_display_time)
                .format();
        pickupDateTime.setText(tokenizedPickupDateTime);
    }

    public String getRentalPickupLocationName(){
        return mRentalPickupLocationName.getValue();
    }

    public String getRentalPickupAirportCode(){
        return mRentalPickupAirportCode.getValue();
    }

    public void setWayfindings(List<EHIWayfindingStep> wayfindings) {
        mWayfindings = wayfindings;

        if(mWayfindings != null && !mWayfindings.isEmpty()){
            upcomingRentalsDirectionsFromTerminal.setVisibility(ReactorViewState.VISIBLE);
        }
        else {
            upcomingRentalsDirectionsFromTerminal.setVisibility(ReactorViewState.GONE);
        }
    }

    private void setPickupTime() {
        Calendar pickupCal = Calendar.getInstance();
        pickupCal.setTime(mTripSummary.getPickupTime());
        Calendar today = Calendar.getInstance();
        int dayDifference = pickupCal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
        CharSequence statusMessage;
        switch (dayDifference) {
            case 1:
                statusMessage = getResources().getString(R.string.dashboard_upcoming_rental_cell_tomorrow);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                statusMessage = new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.dashboard_upcoming_rental_cell_days_until_rental)
                        .addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS, String.valueOf(dayDifference))
                        .format();
                break;
            default:
                statusMessage = getResources().getString(R.string.dashboard_upcoming_rental_cell_soon);
        }

        seeYouMessage.setText(statusMessage);
    }

}
