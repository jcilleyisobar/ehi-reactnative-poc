package com.ehi.enterprise.android.ui.location;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.utils.reactor_extensions.ReactorCalendar;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterDatePickerFragmentViewModelTest extends BaseViewModelTest<FilterDatePickerFragmentViewModel> {

    @Override
    protected Class<FilterDatePickerFragmentViewModel> getViewModelClass() {
        return FilterDatePickerFragmentViewModel.class;
    }

    @Before
    public void setup() {
        ReactorCalendar pickupDate = mock(ReactorCalendar.class);
        ReactorCalendar returnDate = mock(ReactorCalendar.class);
        getViewModel().setPickupDateCalendar(pickupDate);
        getViewModel().setReturnDateCalendar(returnDate);
    }

    @Test
    public void testValidDateForNormalFlow() {
        Calendar calendar = Calendar.getInstance();
        Date pickupDate = new Date();
        calendar.setTime(pickupDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date returnDate = calendar.getTime();

        assertTrue(getViewModel().isDateValid(pickupDate, returnDate));
    }

    @Test
    public void testValidDateForErrorFlow() {
        Calendar calendar = Calendar.getInstance();
        Date pickupDate = new Date();
        calendar.setTime(pickupDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date returnDate = calendar.getTime();

        assertFalse(getViewModel().isDateValid(pickupDate, returnDate));
    }

    @Test
    public void testIsToastMessageGoneForValidDates() {
        getViewModel().setPickupDate(new Date());
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_RETURN);
        getViewModel().updateToastVisibility(true);
        assertGone(getViewModel().toastView);
    }

    @Test
    public void testIsToastMessageVisibleForInvalidDates() {
        Date date = new Date();
        mockReturnDate(date);
        when(getViewModel().getReturnDate()).thenReturn(date);
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_PICKUP);
        getViewModel().updateToastVisibility(false);
        assertVisible(getViewModel().toastView);
    }

    @NonNull
    private ReactorCalendar mockReturnDate(Date prefilledReturnDate) {
        ReactorCalendar returnDate = mock(ReactorCalendar.class);
        returnDate.setTime(prefilledReturnDate);
        when(returnDate.getTime()).thenReturn(prefilledReturnDate);
        return returnDate;
    }

    @Test
    public void testShouldReturnPrefilledReturnDateForPickupFlow() {
        Date prefilledReturnDate = new Date();
        ReactorCalendar returnDate = mockReturnDate(prefilledReturnDate);

        getViewModel().setReturnDateCalendar(returnDate);
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_PICKUP);
        assertEquals(prefilledReturnDate, getViewModel().getReturnDate());
    }

    @Test
    public void testShouldReturnPrefilledPickupDateForReturnFlow() {
        Date prefilledPickupDate = new Date();
        ReactorCalendar returnDate = mockReturnDate(prefilledPickupDate);

        getViewModel().setPickupDateCalendar(returnDate);
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_RETURN);
        assertEquals(prefilledPickupDate, getViewModel().getPickupDate());
    }
    @Test
    public void testInvalidContinueReturnShouldErasePickupDate() {
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_RETURN);
        when(getViewModel().isDateValid(null, null)).thenReturn(false);
        getViewModel().continueButtonClicked();
        assertFalse(getViewModel().shouldPersistPickupDate());
    }

    @Test
    public void testInvalidContinuePickupShouldEraseReturnDate() {
        getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_PICKUP);
        when(getViewModel().isDateValid(null, null)).thenReturn(false);
        getViewModel().continueButtonClicked();
        assertFalse(getViewModel().shouldPersistReturnDate());
    }
}
