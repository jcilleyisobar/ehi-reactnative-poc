package com.ehi.enterprise.android.ui.reservation.widget;

import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FlightDetailsViewViewModel extends ManagersAccessViewModel {
    //region ReactorVars
    final ReactorTextViewState currentFlightDetails = new ReactorTextViewState();
    final ReactorTextViewState currentFlightNumber = new ReactorTextViewState();
    final ReactorTextViewState flightNumber = new ReactorTextViewState();
    final ReactorVar<EHIAirlineDetails> mCurrentAirline = new ReactorVar<>();
    final ReactorViewState currentFlightDetailsContainer = new ReactorViewState();
    final ReactorViewState addFlightDetailsContainer = new ReactorViewState();
    final ReactorTextViewState addFlightDetailsTitle = new ReactorTextViewState();
    final ReactorTextViewState rootView = new ReactorTextViewState();
    final ReactorViewState greenArrow = new ReactorViewState();
    //endregion

    private void setUpFlightDetails(String flightNumber) {

        EHIAirlineDetails details = mCurrentAirline.getRawValue();

        if (details != null && !EHITextUtils.isEmpty(details.getDescription())) {
            if (details.getCode().equals(EHIAirlineDetails.WALK_IN_CODE)) {
                currentFlightDetails.setText(R.string.flight_details_no_flight);
                currentFlightNumber.setVisible(false);
            } else {
                currentFlightDetails.setText(details.getDescription());
                if (!TextUtils.isEmpty(flightNumber)) {
                    currentFlightNumber.setText(flightNumber);
                    currentFlightNumber.setVisible(true);
                } else {
                    currentFlightNumber.setVisible(false);
                }
            }
            currentFlightDetailsContainer.setVisible(true);
            addFlightDetailsContainer.setVisible(false);

        } else {
            currentFlightDetailsContainer.setVisible(false);
            addFlightDetailsContainer.setVisible(true);
        }
    }

    public void setCurrentFlightDetails(final EHIAirlineDetails value, final String flightNumber, boolean isMultiTerminal) {
        mCurrentAirline.setValue(value);
        setUpFlightDetails(flightNumber);
    }

    public void hideGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.GONE);
    }

    public void showGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.VISIBLE);
    }

    public void setVisibilityForContent() {
        if (addFlightDetailsContainer.visible().getRawValue()) {
            rootView.setVisible(false);
        } else {
            rootView.setVisible(true);
        }
    }
}
