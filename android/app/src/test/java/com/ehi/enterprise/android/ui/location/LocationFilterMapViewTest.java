package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.ui.location.widgets.LocationFilterMapViewModel;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class LocationFilterMapViewTest extends BaseViewModelTest<LocationFilterMapViewModel> {

    @Override
    protected Class<LocationFilterMapViewModel> getViewModelClass() {
        return LocationFilterMapViewModel.class;
    }

    @Before
    public void setUp() {
        getViewModel().setReturnTimeText("");
        getViewModel().setPickupTimeText("");
        getViewModel().setPickupDate("");
        getViewModel().setReturnDate("");
    }

    @Test
    public void isDatesContainerGoneWhenThereAreNoDatesFilter() {
        doReturn(true).when(getViewModel()).isDefaultDateValue("");
        doReturn(true).when(getViewModel()).isDefaultTimeValue("");
        when(getViewModel().shouldHideDatePickers()).thenReturn(true);
        getViewModel().updateViewsVisibility();
        assertGone(getViewModel().filterViewState);
    }

    @Test
    public void areDatesContainerVisibleWhenThereAreSomeDate() {
        doReturn(true).when(getViewModel()).isDefaultDateValue("");
        doReturn(true).when(getViewModel()).isDefaultTimeValue("");
        when(getViewModel().shouldHideDatePickers()).thenReturn(false);
        getViewModel().updateViewsVisibility();
        assertVisible(getViewModel().filterViewState);
    }

    @Test
    public void isFilterVisibleWhenThereAreFilters() {
        doReturn(true).when(getViewModel()).shouldShowFiltersView();
        getViewModel().updateViewsVisibility();
        assertVisible(getViewModel().filterTextViewState.visibility().getValue());
    }

    @Test
    public void isFilterGoneWhenThereAreNoFilters() {
        doReturn(false).when(getViewModel()).shouldShowFiltersView();
        getViewModel().updateViewsVisibility();
        assertGone(getViewModel().filterTextViewState.visibility().getValue());
    }

    @Test
    public void testHideDateAndTimePickersMethod() {
        doReturn(true).when(getViewModel()).isDefaultDateValue("");
        doReturn(true).when(getViewModel()).isDefaultTimeValue("");
        assertTrue(getViewModel().shouldHideDatePickers());
    }

    @Test
    public void isPickupAndReturnTimeGoneWhenNoPickupAndReturnDateAreSelected() {
        getViewModel().setDefaultPickupDate();
        getViewModel().setDefaultReturnDate();
        assertTrue(getViewModel().filterViewState.dropoffDefaultState().getValue());
        assertTrue(getViewModel().filterViewState.pickupDefaultState().getValue());
    }

    @Test
    public void arePickupAndReturnTimeVisibleWhenThereAreDatesInReturnAndPickup() {
        getViewModel().setPickupDate("some date");
        getViewModel().setReturnDate("some date");
        assertFalse(getViewModel().filterViewState.dropoffDefaultState().getValue());
        assertFalse(getViewModel().filterViewState.pickupDefaultState().getValue());
    }
}