package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FlightDetailViewModel extends ManagersAccessViewModel {

    //region ReactorVars
    final ReactorTextViewState airlineDescription = new ReactorTextViewState();
    final ReactorVar<String> flightNumber = new ReactorVar<>("");
    final ReactorViewState submitButton = new ReactorViewState();

    final ReactorVar<CharSequence> airlineDescriptionError = new ReactorVar<>();
    //endregion

    private List<EHIAirlineDetails> mAirlineDetails;
    EHIAirlineDetails mSelectedAirline;
    private boolean mIsMultiTerminal;
    private EHIAirlineDetails mWalkIn;
    private boolean mIsModify;
    private boolean mEditing;

    final String emptyErrorString = " ";

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        updateSubmitButtonState();
    }

    public void setAirlines(List<EHIAirlineDetails> airlines) {
        removeWalkInDetails(airlines);
        Collections.sort(airlines, new Comparator<EHIAirlineDetails>() {
            @Override
            public int compare(EHIAirlineDetails t1, EHIAirlineDetails t2) {
                return t1.getDescription().compareTo(t2.getDescription());
            }
        });
        mAirlineDetails = airlines;
    }

    public List<EHIAirlineDetails> getAirlines() {
        return mAirlineDetails;
    }

    public String getFlightNumber() {
        return flightNumber.getValue();
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber.setValue(flightNumber);
    }

    public EHIAirlineDetails getCurrentlySelectedDetails() {
        return mSelectedAirline;
    }

    public void updateSubmitButtonState() {
        submitButton.setEnabled(mSelectedAirline != null || !isMultiTerminal());

        if (emptyErrorString.equals(airlineDescriptionError.getRawValue())) {
            highlightInvalidFields();
        }
    }

    public void setSelectedAirline(final EHIAirlineDetails selectedAirline) {
        mSelectedAirline = selectedAirline;
        if (mSelectedAirline != null) {
            if (selectedAirline.getCode().equals(EHIAirlineDetails.WALK_IN_CODE)) {
                airlineDescription.setText("");
            } else {
                airlineDescription.setText(mSelectedAirline.getDescription());
                airlineDescription.textRes().setValue(null);
            }
        }
    }

    public EHIAirlineDetails getWalkInDetails() {
        return mWalkIn;
    }

    private void removeWalkInDetails(List<EHIAirlineDetails> airlines) {
        int index = BaseAppUtils.indexOf(
                EHIAirlineDetails.WALK_IN_CODE,
                airlines,
                new BaseAppUtils.CompareTwo<String, EHIAirlineDetails>() {
                    @Override
                    public boolean equals(String first, EHIAirlineDetails second) {
                        return first.equalsIgnoreCase(second.getCode());
                    }
                });

        if (index > -1) {
            mWalkIn = airlines.remove(index);
        }
    }

    public void saveCurrentlySelectedDetails() {
        EHIAirlineInformation ehiAirlineInformation = new EHIAirlineInformation();

        if (mSelectedAirline != null) {
            ehiAirlineInformation.setCode(mSelectedAirline.getCode());
            ehiAirlineInformation.setFlightNumber(flightNumber.getRawValue());
        }

        getManagers().getReservationManager().setSelectedAirlineInformation(ehiAirlineInformation);
    }

    public EHIReservation getReservationObject() {
        if (isModify()) {
            return getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            return getManagers().getReservationManager().getCurrentReservation();
        }
    }

    public void syncToReservation() {
        final EHIAirlineInformation ehiAirlineInformation = getReservationObject().getAirlineInformation();
        if (ehiAirlineInformation == null
                || ehiAirlineInformation.getCode() == null) {
            return;
        }

        final int flightIndex = getFlightIndex(ehiAirlineInformation);
        if (flightIndex > -1) {
            setSelectedAirline(mAirlineDetails.get(flightIndex));
        }

        if (ehiAirlineInformation.getFlightNumber() != null) {
            setFlightNumber(ehiAirlineInformation.getFlightNumber());
        }
    }

    private int getFlightIndex(@NonNull EHIAirlineInformation ehiAirlineInformation) {
        return BaseAppUtils.indexOf(
                ehiAirlineInformation,
                mAirlineDetails,
                new BaseAppUtils.CompareTwo<EHIAirlineInformation, EHIAirlineDetails>() {
                    @Override
                    public boolean equals(EHIAirlineInformation first, EHIAirlineDetails second) {
                        return first.getCode().equalsIgnoreCase(second.getCode());
                    }
                });
    }

    public void setIsMultiTerminal(boolean value) {
        mIsMultiTerminal = value;
    }

    public boolean isMultiTerminal() {
        return mIsMultiTerminal;
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public boolean isEditing() {
        return mEditing;
    }

    public void setIsEditing(final boolean editing) {
        mEditing = editing;
    }

    public void highlightInvalidFields() {
        airlineDescriptionError.setValue(
                mSelectedAirline != null || !isMultiTerminal() ? null : emptyErrorString
        );
    }
}
