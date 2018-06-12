package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FlightDetailsViewViewModelTest extends BaseViewModelTest<FlightDetailsViewViewModel> {

    @Before
    public void setUp() throws Exception {
        getViewModel().onAttachToView();
    }

    @Test
    public void testSetCurrentFlightDetails() throws Exception {
        String flightNumber = null;
        getViewModel().setCurrentFlightDetails(null, flightNumber, false);

        EHIAirlineDetails mockedAirLineDetails = Mockito.spy(EHIAirlineDetails.class);
        Mockito.when(mockedAirLineDetails.getCode()).thenReturn("TT");
        Mockito.when(mockedAirLineDetails.getDescription()).thenReturn("TEST");

        flightNumber = "1234";
        getViewModel().setCurrentFlightDetails(mockedAirLineDetails, flightNumber, false);
        Assert.assertTrue(getViewModel().currentFlightDetailsContainer.visible().getRawValue());
        Assert.assertEquals("TEST", getViewModel().currentFlightDetails.text().getRawValue());
        Assert.assertEquals("1234", getViewModel().currentFlightNumber.text().getRawValue());
        Assert.assertFalse(getViewModel().addFlightDetailsContainer.visible().getRawValue());
    }

    @Override
    protected Class<FlightDetailsViewViewModel> getViewModelClass() {
        return FlightDetailsViewViewModel.class;
    }
}