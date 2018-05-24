package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.ui.location.widgets.LocationFilterViewModel;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Before;
import org.junit.Test;

import io.dwak.reactor.ReactorVar;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class LocationFilterViewTest extends BaseViewModelTest<LocationFilterViewModel> {

    @Override
    protected Class<LocationFilterViewModel> getViewModelClass() {
        return LocationFilterViewModel.class;
    }

    @Test
    public void isPickupTimePickerGoneAfterPickupDateReset() {
        getViewModel().resetPickupDate();
        assertGone(getViewModel().pickupTimeViewState);
    }

    @Test
    public void isPickupTimePickerValuePersistedAfterPickupDateReset() {
        String time = "one time";
        getViewModel().setPickupTimeText(time);
        getViewModel().resetPickupDate();
        assertEquals(time, getViewModel().pickupTimeViewState.title().getValue());
    }

    @Test
    public void isReturnTimePickerValuePersistedAfterReturnDateReset() {
        String time = "one time";
        getViewModel().setReturnTimeText(time);
        getViewModel().resetReturnDate();
        assertEquals(time, getViewModel().returnTimeViewState.title().getValue());
    }

    @Test
    public void isReturnTimePickerGoneAfterReturnDateReset() {
        getViewModel().resetReturnDate();
        assertGone(getViewModel().returnTimeViewState);
    }

    @Test
    public void isPickupAndReturnTimeGoneWhenNoPickupAndReturnDateAreSelected() {
        getViewModel().setPickupDate("ANY DAY");
        getViewModel().setReturnDate("ANY DAY");
        doReturn(false).when(getViewModel()).isPickupDateSet();
        doReturn(false).when(getViewModel()).isReturnDateSet();
        getViewModel().onAttachToView();
        assertGone(getViewModel().pickupTimeViewState);
        assertGone(getViewModel().returnTimeViewState);
    }

    @Test
    public void arePickupAndReturnTimeVisibleWhenThereAreDatesInReturnAndPickup() {
        getViewModel().setPickupDate("some date");
        getViewModel().setReturnDate("some date");
        doReturn(true).when(getViewModel()).isPickupDateSet();
        doReturn(true).when(getViewModel()).isReturnDateSet();
        getViewModel().onAttachToView();
        assertVisible(getViewModel().pickupTimeViewState);
        assertVisible(getViewModel().returnTimeViewState);
    }
}
