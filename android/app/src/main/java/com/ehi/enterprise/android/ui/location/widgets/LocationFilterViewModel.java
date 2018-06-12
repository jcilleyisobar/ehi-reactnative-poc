package com.ehi.enterprise.android.ui.location.widgets;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterComponentViewState;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationFilterViewModel extends ManagersAccessViewModel {
    public final FilterComponentViewState pickupDateViewState = new FilterComponentViewState();
    public final FilterComponentViewState returnDateViewState = new FilterComponentViewState();
    public final FilterComponentViewState pickupTimeViewState = new FilterComponentViewState();
    public final FilterComponentViewState returnTimeViewState = new FilterComponentViewState();

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        setDefaultPickupDate();
        setDefaultPickupTimeText();
        setDefaultReturnDate();
        setDefaultReturnTimeText();
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        checkPickupTimeVisibility();
        checkReturnTimeVisibility();
    }

    private void checkPickupTimeVisibility() {
        if (isPickupDateSet()) {
            pickupTimeViewState.setVisibility(View.VISIBLE);
        } else {
            pickupTimeViewState.setVisibility(View.GONE);
        }
    }

    private void checkReturnTimeVisibility() {
        if (isReturnDateSet()) {
            returnTimeViewState.setVisibility(View.VISIBLE);
        } else {
            returnTimeViewState.setVisibility(View.GONE);
        }
    }

    public void setDefaultPickupTimeText() {
        pickupTimeViewState.title().setValue(getAnyTimeText());
        pickupTimeViewState.resetDefaultState().setValue(true);
    }

    public void setDefaultReturnTimeText() {
        returnTimeViewState.title().setValue(getAnyTimeText());
        returnTimeViewState.resetDefaultState().setValue(true);
    }

    public void resetPickupDate() {
        setDefaultPickupDate();
    }

    public void resetReturnDate() {
        setDefaultReturnDate();
    }

    public String getAnyTimeText() {
        return getResources().getString(R.string.locations_map_any_time_label);
    }

    public void setPickupDate(String date) {
        pickupDateViewState.setTitle(date);
        pickupDateViewState.resetDefaultState().setValue(false);
        checkPickupTimeVisibility();
    }

    public void setReturnDate(String date) {
        returnDateViewState.setTitle(date);
        returnDateViewState.resetDefaultState().setValue(false);
        checkReturnTimeVisibility();
    }

    public void setDefaultPickupDate() {
        pickupDateViewState.setTitle(getResources().getString(R.string.locations_map_any_day_label));
        pickupDateViewState.resetDefaultState().setValue(true);
        checkPickupTimeVisibility();
    }

    public void setDefaultReturnDate() {
        returnDateViewState.setTitle(getResources().getString(R.string.locations_map_any_day_label));
        returnDateViewState.resetDefaultState().setValue(true);
        checkReturnTimeVisibility();
    }

    public boolean isPickupDateSet() {
        final String pickupDateText = this.pickupDateViewState.title().getRawValue();
        return !pickupDateText.equalsIgnoreCase(getResources().getString(R.string.locations_map_any_day_label));
    }

    public boolean isReturnDateSet() {
        final String returnDateText = this.returnDateViewState.title().getRawValue();
        return !returnDateText.equalsIgnoreCase(getResources().getString(R.string.locations_map_any_day_label));
    }

    public void setReturnTimeText(String text) {
        returnTimeViewState.title().setValue(text);
        returnTimeViewState.resetDefaultState().setValue(false);
    }

    public void setPickupTimeText(String text) {
        pickupTimeViewState.title().setValue(text);
        pickupTimeViewState.resetDefaultState().setValue(false);
    }

    public void resetPickupTime() {
        setDefaultPickupTimeText();
    }

    public void resetReturnTime() {
        setDefaultReturnTimeText();
    }
}
