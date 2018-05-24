package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHIVehicleRate;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChooseYourRateViewModelTest extends BaseViewModelTest<ChooseYourRateViewModel> {

    @Mock
    Context mockedContext;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockedContext.isRestricted()).thenReturn(true);
        getViewModel().setContext(mockedContext);
    }

    @Test
    public void testLoggedOutPrepayAvailableForNorthAmerica() {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(false);
        mockNorthAmericaPrepay(true);
        final String prepayView = "$130.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(prepayView, payLaterView);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);
        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertEquals(prepayView, getViewModel().secondPaymentButton.price().getRawValue());
        assertVisible(getViewModel().secondPaymentButton);
        assertVisible(getViewModel().firstPaymentButton);
        assertEquals(payLaterView, getViewModel().firstPaymentButton.price().getRawValue());
        assertGone(getViewModel().redeemPointsButton);
        assertGone(getViewModel().ePointsHeader);
    }

    @Test
    public void testLoggedOutPrepayAvailableOutsideNorthAmerica() {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(false);
        mockNorthAmericaPrepay(false);
        final String prepayView = "$130.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(prepayView, payLaterView);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);
        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertEquals(prepayView, getViewModel().firstPaymentButton.price().getRawValue());
        assertVisible(getViewModel().firstPaymentButton);
        assertVisible(getViewModel().secondPaymentButton);
        assertEquals(payLaterView, getViewModel().secondPaymentButton.price().getRawValue());
        assertGone(getViewModel().redeemPointsButton);
        assertGone(getViewModel().ePointsHeader);
    }

    @Test
    public void testLoggedInPrepayUnavailableNotEnoughPoints() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);
        EHICarClassDetails carClassDetails = mock(EHICarClassDetails.class, RETURNS_DEEP_STUBS);
        when(carClassDetails.isPrepayRateAvailable()).thenReturn(false);
        when(carClassDetails.getRedemptionPoints()).thenReturn(2.0f);
        when(carClassDetails.isRedemptionAvailable()).thenReturn(true);
        carClassDetails.setVehicleRates(Collections.singletonList(createPayLaterRate()));
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);

        final String payLaterView = "$140.00";
        when(carClassDetails.getPaylaterPriceSummary().getEstimatedTotalView().getFormattedPrice(false)).thenReturn(payLaterView);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(1L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertFalse(getViewModel().firstPaymentButton.enabled().getRawValue());
        assertVisible(getViewModel().secondPaymentButton);
        assertEquals(payLaterView, getViewModel().secondPaymentButton.price().getRawValue());
        assertFalse(getViewModel().redeemPointsButton.enabled().getRawValue().booleanValue());
        assertFalse(getViewModel().redeemPointsButton.warningIcon().getRawValue().booleanValue());
    }

    @Test
    public void testLoggedInPrepayUnavailableEnoughPoints() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);
        EHICarClassDetails carClassDetails = mock(EHICarClassDetails.class, RETURNS_DEEP_STUBS);
        when(carClassDetails.isPrepayRateAvailable()).thenReturn(false);
        when(carClassDetails.getRedemptionPoints()).thenReturn(1.0f);
        when(carClassDetails.isRedemptionAvailable()).thenReturn(true);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);
        carClassDetails.setVehicleRates(Collections.singletonList(createPayLaterRate()));
        final String payLaterView = "$140.00";
        when(carClassDetails.getPaylaterPriceSummary().getEstimatedTotalView().getFormattedPrice(false)).thenReturn(payLaterView);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(2L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);
        getMockedContext().getMockedResources().addAnswer("getString", "#{points} Points Per Day - Redeem #{days} Days Max");

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertFalse(getViewModel().firstPaymentButton.enabled().getRawValue());
        assertVisible(getViewModel().secondPaymentButton);
        assertEquals(payLaterView, getViewModel().secondPaymentButton.price().getRawValue());
        assertVisible(getViewModel().redeemPointsButton);
        assertTrue(getViewModel().redeemPointsButton.enabled().getRawValue().booleanValue());
        assertFalse(getViewModel().redeemPointsButton.warningIcon().getRawValue().booleanValue());
    }

    @Test
    public void testLoggedInPrepayAvailableNotEnoughPointsForNorthAmerica() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);
        final String payNowView = "$120.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(payNowView, payLaterView);
        when(carClassDetails.getRedemptionPoints()).thenReturn(2.0f);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(1L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);

        mockNorthAmericaPrepay(true);

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().secondPaymentButton);
        assertEquals(payNowView, getViewModel().secondPaymentButton.price().getRawValue());
        assertVisible(getViewModel().firstPaymentButton);
        assertEquals(payLaterView, getViewModel().firstPaymentButton.price().getRawValue());

        assertEquals(false, getViewModel().redeemPointsButton.enabled().getRawValue().booleanValue());
    }

    @Test
    public void testLoggedInPrepayAvailableNotEnoughPointsOutsideNorthAmerica() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);
        final String payNowView = "$120.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(payNowView, payLaterView);
        when(carClassDetails.getRedemptionPoints()).thenReturn(2.0f);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(1L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);

        mockNorthAmericaPrepay(false);

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().firstPaymentButton);
        assertEquals(payNowView, getViewModel().firstPaymentButton.price().getRawValue());
        assertVisible(getViewModel().secondPaymentButton);
        assertEquals(payLaterView, getViewModel().secondPaymentButton.price().getRawValue());

        assertEquals(false, getViewModel().redeemPointsButton.enabled().getRawValue().booleanValue());
    }


    @Test
    public void testLoggedInPrepayAvailableEnoughPointsNorthAmerica() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);

        final String payNowView = "$120.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(payNowView, payLaterView);
        mockPoints(carClassDetails, 1.0f, 2);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(2L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);
        getMockedContext().getMockedResources().addAnswer("getString", "#{points} Points Per Day - Redeem #{days} Days Max");
        mockNorthAmericaPrepay(true);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().secondPaymentButton);
        assertVisible(getViewModel().firstPaymentButton);
        assertEquals(payNowView, getViewModel().secondPaymentButton.price().getRawValue());
        assertEquals(payLaterView, getViewModel().firstPaymentButton.price().getRawValue());

        assertVisible(getViewModel().redeemPointsButton);
    }

    @Test
    public void testRedeemPointsFormat() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);


        final String payNowView = "$120.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(payNowView, payLaterView);
        mockPoints(carClassDetails, 1200.0f, 1);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(2000L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);


        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().redeemPointsButton);
        assertEquals("1,200", getViewModel().redeemPointsButton.price().getRawValue());
    }

    @Test
    public void testLoggedInPrepayAvailableEnoughPointsOutsideNorthAmerica() throws Exception {
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().isLoggedIn()).thenReturn(true);

        final String payNowView = "$120.00";
        final String payLaterView = "$140.00";
        EHICarClassDetails carClassDetails = mockCarClassDetailsForPrepay(payNowView, payLaterView);
        when(carClassDetails.isPayLaterChargesAvailable()).thenReturn(true);
        mockPoints(carClassDetails, 1.0f, 2);

        EHIProfileResponse profile = mock(EHIProfileResponse.class, RETURNS_DEEP_STUBS);
        when(profile.getBasicProfile().getLoyaltyData().getPointsToDate()).thenReturn(2000L);
        when(getMockedDelegate().getMockedLoginManager().getMockedObject().getProfileCollection()).thenReturn(profile);
        getMockedContext().getMockedResources().addAnswer("getString", "#{points} Points Per Day - Redeem #{days} Days Max");
        mockNorthAmericaPrepay(false);

        getViewModel().setCarClassDetails(carClassDetails);
        getViewModel().onAttachToView();

        if (getViewModel().isNorthAmericaPrepayAvailable(false)) {
            assertVisible(getViewModel().secondPaymentButton);
            assertVisible(getViewModel().firstPaymentButton);
            assertEquals(payNowView, getViewModel().secondPaymentButton.price().getRawValue());
            assertEquals(payLaterView, getViewModel().firstPaymentButton.price().getRawValue());
        } else {
            assertVisible(getViewModel().firstPaymentButton);
            assertVisible(getViewModel().secondPaymentButton);
            assertEquals(payNowView, getViewModel().firstPaymentButton.price().getRawValue());
            assertEquals(payLaterView, getViewModel().secondPaymentButton.price().getRawValue());
        }

        assertVisible(getViewModel().redeemPointsButton);
    }

    private EHICarClassDetails mockCarClassDetailsForPrepay(String prepayView, String payLaterView) {
        EHICarClassDetails carClassDetails = mock(EHICarClassDetails.class, RETURNS_DEEP_STUBS);
        when(carClassDetails.isPrepayRateAvailable()).thenReturn(true);
        when(carClassDetails.isPrepayChargesAvailable()).thenReturn(true);
        carClassDetails.setVehicleRates(Arrays.asList(createPayLaterRate(), createPrepayRate()));
        setupCarClassPrices(carClassDetails, prepayView, payLaterView);
        return carClassDetails;
    }

    private void setupCarClassPrices(EHICarClassDetails carClassDetails, String prepayView, String payLaterView) {
        when(carClassDetails.getPrepayPriceSummary().getEstimatedTotalView().getFormattedPrice(false)).thenReturn(prepayView);
        when(carClassDetails.getPaylaterPriceSummary().getEstimatedTotalView().getFormattedPrice(false)).thenReturn(payLaterView);
    }

    private void mockNorthAmericaPrepay(boolean isPrepay) {
        EHICountry ehiCountry = mock(EHICountry.class, RETURNS_DEEP_STUBS);
        when(getMockedDelegate().getMockedLocalDataManager().getMockedObject().getPreferredCountry()).thenReturn(ehiCountry);
        when(getMockedDelegate().getMockedLocalDataManager().getMockedObject().getPreferredCountryCode()).thenReturn(EHICountry.COUNTRY_US);
        EHIReservation reservation = mock(EHIReservation.class, RETURNS_DEEP_STUBS);
        getMockedDelegate().getMockedReservationManager().addAnswer("getCurrentReservation", reservation);
        when(reservation.isPaymentProvidedPopulated()).thenReturn(isPrepay);
        when(reservation.doesCarClassListHasPrepayRates()).thenReturn(isPrepay);
    }

    private void mockPoints(EHICarClassDetails carClassDetails, float cost, int days) {
        when(carClassDetails.getRedemptionPoints()).thenReturn(cost);
        when(carClassDetails.getRedemptionDayCount()).thenReturn(days);
        when(carClassDetails.isRedemptionAvailable()).thenReturn(true);
    }

    @Override
    protected Class<ChooseYourRateViewModel> getViewModelClass() {
        return ChooseYourRateViewModel.class;
    }

    @NonNull
    private EHIVehicleRate createPayLaterRate() {
        EHIVehicleRate payLaterRate = mock(EHIVehicleRate.class, RETURNS_DEEP_STUBS);
        when(payLaterRate.getChargeType()).thenReturn(EHIVehicleRate.PAYLATER);
        return payLaterRate;
    }

    @NonNull
    private EHIVehicleRate createPrepayRate() {
        EHIVehicleRate prepayRate = mock(EHIVehicleRate.class, RETURNS_DEEP_STUBS);
        when(prepayRate.getChargeType()).thenReturn(EHIVehicleRate.PREPAY);
        return prepayRate;
    }
}