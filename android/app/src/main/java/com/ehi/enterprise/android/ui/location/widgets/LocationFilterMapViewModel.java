package com.ehi.enterprise.android.ui.location.widgets;

import android.support.annotation.NonNull;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterMapComponentViewState;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterTextMapComponentViewState;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LocationFilterMapViewModel extends ManagersAccessViewModel {
    public final FilterMapComponentViewState filterViewState = new FilterMapComponentViewState();
    public final FilterTextMapComponentViewState filterTextViewState = new FilterTextMapComponentViewState();

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        setDefaultPickupDate();
        setDefaultPickupTimeText();
        setDefaultReturnDate();
        setDefaultReturnTimeText();
    }

    public void setDefaultPickupTimeText() {
        filterViewState.pickupTimeTitle().setValue(getAnyTimeText());
    }

    public void setDefaultReturnTimeText() {
        filterViewState.dropoffTimeTitle().setValue(getAnyTimeText());
    }

    @NonNull
    private String getAnyTimeText() {
        return getResources().getString(R.string.locations_map_any_time_label);
    }

    public void setPickupDate(String date) {
        filterViewState.pickupDateTitle().setValue(date);
        filterViewState.pickupDefaultState().setValue(false);
    }

    public void setReturnDate(String date) {
        filterViewState.dropoffDateTitle().setValue(date);
        filterViewState.dropoffDefaultState().setValue(false);
    }

    public void setDefaultPickupDate() {
        filterViewState.pickupDateTitle().setValue(getResources().getString(R.string.locations_map_any_day_label));
        filterViewState.pickupDefaultState().setValue(true);
    }

    public void setDefaultReturnDate() {
        filterViewState.dropoffDateTitle().setValue(getResources().getString(R.string.locations_map_any_day_label));
        filterViewState.dropoffDefaultState().setValue(true);
    }

    public void setReturnTimeText(String text) {
        filterViewState.dropoffTimeTitle().setValue(text);
        filterViewState.dropoffDefaultState().setValue(false);
    }

    public void setPickupTimeText(String text) {
        filterViewState.pickupTimeTitle().setValue(text);
        filterViewState.pickupDefaultState().setValue(false);
    }

    public boolean isDefaultDateValue(String value) {
        return value.equals(getResources().getString(R.string.locations_map_any_day_label));
    }

    public boolean isDefaultTimeValue(String  value) {
        return value.equals(getResources().getString(R.string.locations_map_any_time_label));
    }

    public boolean shouldHideDatePickers() {
        return isDefaultDateValue(filterViewState.pickupDateTitle().getValue())
                && isDefaultDateValue(filterViewState.dropoffDateTitle().getValue())
                && isDefaultTimeValue(filterViewState.pickupTimeTitle().getValue())
                && isDefaultTimeValue(filterViewState.dropoffTimeTitle().getValue());
    }

    public boolean shouldShowFiltersView() {
        return filterTextViewState.title().getValue() != null && !filterTextViewState.title().getValue().isEmpty();
    }

    public void updateViewsVisibility() {
        updateDateVisibility();
        updateFiltersVisibility();
    }

    private void updateDateVisibility() {
        if (shouldHideDatePickers()) {
            filterViewState.setVisibility(View.GONE);
            filterViewState.setVisibility(View.GONE);
        } else {
            filterViewState.setVisibility(View.VISIBLE);
            filterViewState.setVisibility(View.VISIBLE);
        }
    }

    private void updateFiltersVisibility() {
        if (shouldShowFiltersView()) {
            filterTextViewState.visibility().setValue(View.VISIBLE);
        } else {
            filterTextViewState.visibility().setValue(View.GONE);
        }
    }

    public void setFiltersText(String text) {
        filterTextViewState.title().setValue(text);
    }
}
