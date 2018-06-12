package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIFeature;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CarClassDetailViewModelTest extends BaseViewModelTest<CarClassDetailViewModel> {

    protected Class<CarClassDetailViewModel> getViewModelClass(){
        return CarClassDetailViewModel.class;
    }

    @Test
    public void testInitialLoggedInState(){
        getMockedDelegate().getMockedLoginManager().addAnswer("isLoggedIn", true);
        getViewModel().onAttachToView();
        Assert.assertTrue(getViewModel().pointsContainerVisibility.getRawValue());
        Assert.assertFalse(getViewModel().showPointsOnHeader.getRawValue());
        Assert.assertTrue(getViewModel().ePointsHeaderVisibility.getRawValue());
        Assert.assertFalse(getViewModel().ePointsHeaderShouldShowPoints.getRawValue());

        getMockedDelegate().getMockedLoginManager().addAnswer("isLoggedIn", false);
        getViewModel().onAttachToView();
        Assert.assertFalse(getViewModel().pointsContainerVisibility.getRawValue());
        Assert.assertTrue(getViewModel().showPointsOnHeader.getRawValue());
        Assert.assertFalse(getViewModel().ePointsHeaderVisibility.getRawValue());
        Assert.assertTrue(getViewModel().ePointsHeaderShouldShowPoints.getRawValue());

        Assert.assertFalse(getViewModel().negotiatedRateVisibility.getRawValue());
        Assert.assertTrue(getViewModel().priceHeaderVisibility.getRawValue());
        Assert.assertTrue(getViewModel().priceEstimatedTotalContainerVisibility.getRawValue());
    }

    @Test
    public void testNeedShowPoints(){
        getMockedDelegate().getMockedLocalDataManager().addAnswer("needShowPoints", true);
        getMockedDelegate().getMockedLoginManager().addAnswer("isLoggedIn", true);

        Assert.assertTrue(getViewModel().isUserLoggedIn());
        Assert.assertTrue(getViewModel().needShowPoints());


        getViewModel().mNeedShowPointsWrapper.setRawValue(null);
        getMockedDelegate().getMockedLocalDataManager().addAnswer("needShowPoints", true);
        getMockedDelegate().getMockedLoginManager().addAnswer("isLoggedIn", false);

        Assert.assertFalse(getViewModel().isUserLoggedIn());
        Assert.assertFalse(getViewModel().needShowPoints());


        getViewModel().mNeedShowPointsWrapper.setRawValue(true);
        getMockedDelegate().getMockedLocalDataManager().addAnswer("needShowPoints", true);
        getMockedDelegate().getMockedLoginManager().addAnswer("isLoggedIn", false);
        Assert.assertFalse(getViewModel().needShowPoints());
    }

//    @Test
//    public void testSetCarClassDetailsNoCurrentRes() {
//        EHICarClassDetails mockedCarClassDetails = Mockito.spy(EHICarClassDetails.class);
//        mockedCarClassDetails.setStatus("no status");
//        getMockedDelegate().getMockedSupportInfoManager().addAnswer("getSupportInfoForCurrentLocale", Mockito.spy(EHISupportInfo.class));
//        testSetCarClass(mockedCarClassDetails);
//        Assert.assertFalse(getViewModel().negotiatedRateVisibility.getRawValue());
//        Assert.assertNull(getViewModel().negotiatedRateText.getRawValue());
//
//        Assert.assertFalse(getViewModel().headerTotalVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().headerRentalRangeVisibility.getRawValue());
//        Assert.assertTrue(getViewModel().noPriceAvailableVisibility.getRawValue());
//
//        final EHIPriceSummary mockedPriceSummary = Mockito.spy(EHIPriceSummary.class);
//        final EHIPrice mockedEstimatedTotalView = Mockito.spy(new EHIPrice("USD", "$", 10.0));
//        EHIPrice mockedEstimatedTotalPayment = Mockito.spy(new EHIPrice("USD", "$", 10.0));
//
//        when(mockedPriceSummary.getEstimatedTotalView())
//               .thenReturn(mockedEstimatedTotalView);
//
//        when(mockedPriceSummary.isDifferentPaymentCurrency()).thenReturn(true);
//        when(mockedPriceSummary.getEstimatedTotalPayment())
//               .thenReturn(mockedEstimatedTotalPayment);
//
//        when(mockedCarClassDetails.getPaylaterPriceSummary())
//               .thenReturn(mockedPriceSummary);
//        when(mockedCarClassDetails.isPrepayRateAvailable())
//                .thenReturn(false);
//
//        when(mockedEstimatedTotalView.getFormattedPrice(true))
//               .then(new Answer<CharSequence>() {
//                   @Override
//                   public CharSequence answer(final InvocationOnMock invocation) throws Throwable {
//                       if (((boolean) invocation.getArguments()[0])) {
//                           return mockedEstimatedTotalView.getFormattedPrice(false);
//                       }
//                       return (CharSequence) invocation.callRealMethod();
//                   }
//               });
//
//        testSetCarClass(mockedCarClassDetails);
//
//        Assert.assertTrue(getViewModel().classDetailsConversionAreaVisibility.getRawValue());
//        Assert.assertEquals("$10.00", getViewModel().classDetailsConversionTotalText.getRawValue());
//        Assert.assertEquals("$10.00", getViewModel().estimatedTotalText.getRawValue());
//
//        Assert.assertTrue(getViewModel().headerTotalVisibility.getRawValue());
//        Assert.assertTrue(getViewModel().headerRentalRangeVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().priceUnavailableVisibility.getRawValue());
//        Assert.assertEquals("$10.00", getViewModel().headerTotalText.getRawValue());
//        Assert.assertTrue(getViewModel().selectThisClassButtonVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().callLocationButtonVisibility.getRawValue());
//    }

//    @Test
//    public void testSetCarClassDetailsCurrentRes() {
//        EHIReservation mockedReservation = Mockito.spy(EHIReservation.class);
//        EHISupportInfo mockedSupportInfo = Mockito.spy(EHISupportInfo.class);
//        EHIPromotionContract mockedCorporateAccount = Mockito.spy(EHIPromotionContract.class);
//        final String mockedContractName = "CONTRACT-NAME";
//        final String mockedPreCid = "PRE-CID";
//        final String mockedContractNumber = "CONTRACT-NUMBER";
//        final String mockedContractType = "CORPORATE";
//
//        when(mockedCorporateAccount.getContractName()).thenReturn(mockedContractName);
//        when(mockedCorporateAccount.getContractNumber()).thenReturn(mockedContractNumber);
//        when(mockedCorporateAccount.getContractType()).thenReturn(mockedContractType);
//        when(mockedReservation.getCorporateAccount()).thenReturn(mockedCorporateAccount);
//        when(mockedSupportInfo.getPreCid()).thenReturn(mockedPreCid);
//        getMockedDelegate().getMockedSupportInfoManager().addAnswer("getSupportInfoForCurrentLocale", mockedSupportInfo);
//        getMockedDelegate().getMockedReservationManager().addAnswer("getCurrentReservation", mockedReservation);
//
//        EHICarClassDetails mockedCarClassDetails = Mockito.spy(EHICarClassDetails.class);
//        mockedCarClassDetails.setStatus(EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE);
//        testSetCarClass(mockedCarClassDetails);
//        Assert.assertTrue(getViewModel().negotiatedRateVisibility.getRawValue());
//        Assert.assertEquals(getViewModel().negotiatedRateText.getRawValue().intValue(), R.string.car_class_cell_negotiated_rate_title);
//        Assert.assertFalse(getViewModel().classDetailsConversionAreaVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().priceHeaderVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().priceEstimatedTotalContainerVisibility.getRawValue());
//
//        Assert.assertFalse(getViewModel().headerTotalVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().headerRentalRangeVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().noPriceAvailableVisibility.getRawValue());
//        Assert.assertTrue(getViewModel().priceUnavailableVisibility.getRawValue());
//        Assert.assertFalse(getViewModel().selectThisClassButtonVisibility.getRawValue());
//        Assert.assertTrue(getViewModel().callLocationButtonVisibility.getRawValue());
//    }

    private void testSetCarClass(final EHICarClassDetails mockedCarClassDetails) {
        final String carName = "Car Name";
        final String carDescription = "Car Description";
        final Number peopleCapacity = 2;
        final Number luggageCapacity = 3;
        final List<EHIFeature> ehiFeatures = new ArrayList<>();
        ehiFeatures.add(new EHIFeature(EHIFeature.TRANSMISSION_CODE_AUTOMATIC, "Automatic"));
        getMockedContext().getMockedResources().addAnswer("getString", "#{make_model} or similar");

        when(mockedCarClassDetails.getMakeModelOrSimilarText()).thenReturn(carName);
        when(mockedCarClassDetails.getName()).thenReturn(carName);
        when(mockedCarClassDetails.getDescription()).thenReturn(carDescription);
        when(mockedCarClassDetails.getPeopleCapacity()).thenReturn(peopleCapacity);
        when(mockedCarClassDetails.getLuggageCapacity()).thenReturn(luggageCapacity);
        when(mockedCarClassDetails.getFeatures()).thenReturn(ehiFeatures);

        getViewModel().setCarClassDetails(mockedCarClassDetails);
        Assert.assertEquals(getViewModel().nameOfClass.getRawValue(), carName);
        Assert.assertEquals(getViewModel().carClassDescription.getRawValue(), carDescription);
        Assert.assertEquals(getViewModel().peopleCapacityText.getRawValue(), String.valueOf(peopleCapacity));
        Assert.assertEquals(getViewModel().luggageCapacityText.getRawValue(), String.valueOf(luggageCapacity));
        Assert.assertEquals(getViewModel().nameOfCar.getRawValue(), carName + " or similar");
    }
}