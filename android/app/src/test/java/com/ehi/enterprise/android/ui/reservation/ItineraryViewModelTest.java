package com.ehi.enterprise.android.ui.reservation;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHIAgeOption;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrAgeOptionsRequest;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrAgeOptionsResponse;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.mock.network.MockResponseWrapper;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItineraryViewModelTest extends BaseViewModelTest<ItineraryViewModel> {

    @Override
    protected Class<ItineraryViewModel> getViewModelClass() {
        return ItineraryViewModel.class;
    }


    //region population of data
    @Test
    public void testPopulatedFromReservationInformationNoContract() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        EHISolrLocation pickUpLocation = mock(EHISolrLocation.class);
        String pickupLocationId = "pickupLocationId";
        when(pickUpLocation.getPeopleSoftId()).thenReturn(pickupLocationId);
        when(resInfo.getPickupLocation()).thenReturn(pickUpLocation);

        EHISolrLocation returnLocation = mock(EHISolrLocation.class);
        String returnLocationId = "returnLocationId";
        when(returnLocation.getPeopleSoftId()).thenReturn(returnLocationId);
        when(resInfo.getReturnLocation()).thenReturn(returnLocation);

        Date pickupDate = mock(Date.class);
        long pickupDateInMillis = 1000001;
        when(pickupDate.getTime()).thenReturn(pickupDateInMillis);
        when(resInfo.getPickupDate()).thenReturn(pickupDate);

        Date pickupTime = mock(Date.class);
        long pickupTimeInMillis = 1000002;
        when(pickupTime.getTime()).thenReturn(pickupTimeInMillis);
        when(resInfo.getPickupTime()).thenReturn(pickupTime);

        Date returnDate = mock(Date.class);
        long returnDateInMillis = 1000003;
        when(returnDate.getTime()).thenReturn(returnDateInMillis);
        when(resInfo.getReturnDate()).thenReturn(returnDate);

        Date returnTime = mock(Date.class);
        long returnTimeInMillis = 1000004;
        when(returnTime.getTime()).thenReturn(returnTimeInMillis);
        when(resInfo.getReturnTime()).thenReturn(returnTime);

        int renterAge = 273;
        when(resInfo.getRenterAge()).thenReturn(renterAge);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        verify(getViewModel(), atMost(1)).setPickUpLocation(pickUpLocation);
        assertEquals(getViewModel().getPickUpLocation().getPeopleSoftId(), pickupLocationId);

        verify(getViewModel(), atMost(1)).setReturnLocation(pickUpLocation);
        assertEquals(getViewModel().getReturnLocation().getPeopleSoftId(), returnLocationId);

        verify(getViewModel(), atMost(1)).setPickupDate(pickupDate);
        assertEquals(getViewModel().getPickupDate(), pickupDate);

        verify(getViewModel(), atMost(1)).setPickupTime(pickupTime);
        assertEquals(getViewModel().getPickupTime(), pickupTime);

        verify(getViewModel(), atMost(1)).setDropoffDate(returnDate);
        assertEquals(getViewModel().getReturnDate(), returnDate);

        verify(getViewModel(), atMost(1)).setDropoffTime(returnTime);
        assertEquals(getViewModel().getReturnTime(), returnTime);

        verify(getViewModel(), atMost(1)).setRenterAge(renterAge);
        assertEquals(getViewModel().getRenterAge(), renterAge);

        assertEquals(getViewModel().getManuallyEnteredCid(), "");
        assertNull(getViewModel().getCorpAccountFromProfile());
    }

    @Test
    public void testPopulatedFromReservationInformationManualCid() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        when(resInfo.getPickupLocation()).thenReturn(mock(EHISolrLocation.class));

        EHIContract manualCidAccount = mock(EHIContract.class);
        final String contractNumber = "iso111";
        final String contractName = "ISO111";
        when(manualCidAccount.getContractNumber()).thenReturn(contractNumber);
        when(manualCidAccount.getContractOrBillingName()).thenReturn(contractName);

        when(resInfo.getCorpAccount()).thenReturn(manualCidAccount);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        verify(getViewModel(), atMost(1)).setPickUpLocation(any(EHISolrLocation.class));
        assertNotNull(getViewModel().getManuallyEnteredCid());
        assertTrue(getViewModel().getManuallyEnteredCid().trim().length() > 0);
        assertEquals(getViewModel().getManuallyEnteredCid(), contractNumber);
        assertNull(getViewModel().getCorpAccountFromProfile());
    }

    @Test
    public void testPopulatedFromReservationInformationCorpContract() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        when(resInfo.getPickupLocation()).thenReturn(mock(EHISolrLocation.class));

        EHIContract corpAccount = mock(EHIContract.class);
        String contractNumber = "iso111";
        String contractName = "Isobar corp.";
        when(corpAccount.getContractNumber()).thenReturn(contractNumber);
        when(corpAccount.getContractOrBillingName()).thenReturn(contractName);

        when(resInfo.getCorpAccount()).thenReturn(corpAccount);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        verify(getViewModel(), atMost(1)).setPickUpLocation(any(EHISolrLocation.class));
        assertTrue(getViewModel().getManuallyEnteredCid().trim().length() == 0);
        assertEquals(getViewModel().getCorpAccountFromProfile(), corpAccount);
    }

    //endregion

    //region locations
    @Test
    public void testPickupLocationWithIcon() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);

        String pickupLocationName = "pickup location name";
        when(pickupLocation.getTranslatedLocationName()).thenReturn(pickupLocationName);
        when(pickupLocation.getPeopleSoftId()).thenReturn("pickup location ID");

        int drawableId = 102;
        when(pickupLocation.getGrayLocationCellIconDrawable()).thenReturn(drawableId);
        when(pickupLocation.isOneWaySupported()).thenReturn(false);

        EHISolrLocation returnLocation = mock(EHISolrLocation.class);
        when(returnLocation.getTranslatedLocationName()).thenReturn("return location name");
        when(returnLocation.getPeopleSoftId()).thenReturn("return location ID");

        getViewModel().setReturnLocation(returnLocation);
        getViewModel().setPickUpLocation(pickupLocation);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickUpLocation(), pickupLocation);
        assertEquals(getViewModel().pickupLocationTextView.text().getRawValue(), pickupLocationName);
        assertEquals(getViewModel().pickupLocationTextView.drawableLeft().getRawValue().intValue(), drawableId);
        assertTrue(getViewModel().pickupLocationTextView.compoundDrawablePaddingInDp().getRawValue() > 0);

        assertNull(getViewModel().getReturnLocation());

        assertEquals(getViewModel().clientErrorDialogDialogTextRes.getRawValue().intValue(), R.string.alert_one_way_reservation_text);
        assertEquals(getViewModel().pickupLocationHeader.textRes().getRawValue().intValue(), R.string.reservation_location_selection_pickup_header_fallback_title);
        assertGone(getViewModel().returnLocationHeader);

        assertGone(getViewModel().removeReturnLocation);
        assertVisible(getViewModel().addReturnLocationButton);
        assertGone(getViewModel().returnLocationTextView);
    }

    @Test
    public void testReturnLocationWithIcon() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);

        String pickupLocationName = "pickup location name";
        when(pickupLocation.getTranslatedLocationName()).thenReturn(pickupLocationName);
        when(pickupLocation.getPeopleSoftId()).thenReturn("pickup location ID");
        when(pickupLocation.isOneWaySupported()).thenReturn(true);

        EHISolrLocation returnLocation = mock(EHISolrLocation.class);
        String returnLocationName = "return location name";
        when(returnLocation.getTranslatedLocationName()).thenReturn(returnLocationName);
        when(returnLocation.getPeopleSoftId()).thenReturn("return location ID");

        int drawableId = 102;
        when(returnLocation.getGrayLocationCellIconDrawable()).thenReturn(drawableId);

        getViewModel().setPickUpLocation(pickupLocation);
        getViewModel().setReturnLocation(returnLocation);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickUpLocation(), pickupLocation);
        assertEquals(getViewModel().pickupLocationTextView.text().getRawValue(), pickupLocationName);

        assertEquals(getViewModel().getReturnLocation(), returnLocation);

        assertVisible(getViewModel().removeReturnLocation);
        assertGone(getViewModel().addReturnLocationButton);
        assertVisible(getViewModel().returnLocationTextView);

        assertEquals(getViewModel().returnLocationTextView.text().getRawValue(), returnLocationName);
        assertEquals(getViewModel().returnLocationTextView.drawableLeft().getRawValue().intValue(), drawableId);
        assertTrue(getViewModel().returnLocationTextView.compoundDrawablePaddingInDp().getRawValue() > 0);
    }

    //endregion

    //region date/time
    @Test
    public void testNoDateProvided() {
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertNull(getViewModel().getPickupDate());
        assertNull(getViewModel().getPickupTime());
        assertNull(getViewModel().getReturnDate());
        assertNull(getViewModel().getReturnTime());

        assertNull(getViewModel().selectPickupDateView.selectedDate().getRawValue());
        assertNull(getViewModel().selectPickupDateView.selectedTime().getRawValue());
        assertEquals(getViewModel().selectPickupDateView.timeEnabled().getRawValue().booleanValue(), false);

        assertNull(getViewModel().selectReturnDateView.selectedDate().getRawValue());
        assertNull(getViewModel().selectReturnDateView.selectedTime().getRawValue());
        assertEquals(getViewModel().selectReturnDateView.timeEnabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().selectReturnDateView.pickerIconTextAlpha().getRawValue().floatValue(), 0.5f);
        assertEquals(getViewModel().selectReturnDateView.pickerBackgroundDrawableRes().getRawValue().intValue(), R.drawable.time_selector_gray_button_touch_overlay);

        assertEquals(getViewModel().continueButton.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().continueButton.textRes().getRawValue().intValue(), R.string.reservation_itinerary_action_button);
    }

    @Test
    public void testAllDateTimeProvided() {
        Date pickupDate = mock(Date.class);
        Date pickupTime = mock(Date.class);
        Date returnDate = mock(Date.class);
        Date returnTime = mock(Date.class);

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setPickupTime(pickupTime);
        getViewModel().setDropoffDate(returnDate);
        getViewModel().setDropoffTime(returnTime);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickupDate(), pickupDate);
        assertEquals(getViewModel().getPickupTime(), pickupTime);
        assertEquals(getViewModel().getReturnDate(), returnDate);
        assertEquals(getViewModel().getReturnTime(), returnTime);

        assertEquals(getViewModel().selectPickupDateView.selectedDate().getRawValue(), pickupDate);
        assertEquals(getViewModel().selectPickupDateView.selectedTime().getRawValue(), pickupTime);
        assertEquals(getViewModel().selectPickupDateView.timeEnabled().getRawValue().booleanValue(), true);
        assertEquals(getViewModel().selectReturnDateView.selectedDate().getRawValue(), returnDate);
        assertEquals(getViewModel().selectReturnDateView.selectedTime().getRawValue(), returnTime);
        assertEquals(getViewModel().selectReturnDateView.timeEnabled().getRawValue().booleanValue(), true);

        assertEquals(getViewModel().continueButton.enabled().getRawValue().booleanValue(), true);
        assertEquals(getViewModel().continueButton.textRes().getRawValue().intValue(), R.string.reservation_itinerary_action_button);
    }

    @Test
    public void testPickupDateOnly() {
        Date pickupDate = mock(Date.class);

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().setPickupDate(pickupDate);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickupDate(), pickupDate);
        assertEquals(getViewModel().getPickupTime(), null);
        assertEquals(getViewModel().getReturnDate(), null);
        assertEquals(getViewModel().getReturnTime(), null);

        assertEquals(getViewModel().selectPickupDateView.selectedDate().getRawValue(), pickupDate);
        assertEquals(getViewModel().selectPickupDateView.selectedTime().getRawValue(), null);
        assertEquals(getViewModel().selectPickupDateView.timeEnabled().getRawValue().booleanValue(), false);

        assertEquals(getViewModel().selectReturnDateView.selectedDate().getRawValue(), null);
        assertEquals(getViewModel().selectReturnDateView.selectedTime().getRawValue(), null);
        assertEquals(getViewModel().selectReturnDateView.timeEnabled().getRawValue().booleanValue(), false);

        assertEquals(getViewModel().selectReturnDateView.pickerIconTextAlpha().getRawValue().floatValue(), 1.0f);
        assertEquals(getViewModel().selectReturnDateView.pickerBackgroundDrawableRes().getRawValue().intValue(), R.drawable.green_button_touch_overlay);

        assertEquals(getViewModel().continueButton.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().continueButton.textRes().getRawValue().intValue(), R.string.reservation_itinerary_action_button);
    }

    @Test
    public void testPickupReturnDateOnly() {
        Date pickupDate = mock(Date.class);
        Date returnDate = mock(Date.class);

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setDropoffDate(returnDate);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickupDate(), pickupDate);
        assertEquals(getViewModel().getPickupTime(), null);
        assertEquals(getViewModel().getReturnDate(), returnDate);
        assertEquals(getViewModel().getReturnTime(), null);

        assertEquals(getViewModel().selectPickupDateView.selectedDate().getRawValue(), pickupDate);
        assertEquals(getViewModel().selectPickupDateView.selectedTime().getRawValue(), null);
        assertEquals(getViewModel().selectPickupDateView.timeEnabled().getRawValue().booleanValue(), true);

        assertEquals(getViewModel().selectReturnDateView.selectedDate().getRawValue(), returnDate);
        assertEquals(getViewModel().selectReturnDateView.selectedTime().getRawValue(), null);
        assertEquals(getViewModel().selectReturnDateView.timeEnabled().getRawValue().booleanValue(), false);

        assertEquals(getViewModel().selectReturnDateView.pickerIconTextAlpha().getRawValue().floatValue(), 1.0f);
        assertEquals(getViewModel().selectReturnDateView.pickerBackgroundDrawableRes().getRawValue().intValue(), R.drawable.green_button_touch_overlay);

        assertEquals(getViewModel().continueButton.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().continueButton.textRes().getRawValue().intValue(), R.string.reservation_itinerary_action_button);
    }

    //endregion

    //region corp account from profile

    @Test
    public void testNoCorpAccountAdded() {
        testPopulatedFromReservationInformationManualCid();

        assertNull(getViewModel().getCorpAccountFromProfile());
        assertEquals(getViewModel().contractFromProfileName.text().getRawValue(), "");
        assertGone(getViewModel().contractFromProfileContainer);
    }

    @Test
    public void testCorpAccountAdded() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        when(resInfo.getPickupLocation()).thenReturn(mock(EHISolrLocation.class));

        EHIContract corpAccount = mock(EHIContract.class);
        String contractNumber = "iso111";
        String contractName = "Isobar corp.";
        when(corpAccount.getContractNumber()).thenReturn(contractNumber);
        when(corpAccount.getContractOrBillingName()).thenReturn(contractName);
        when(resInfo.getCorpAccount()).thenReturn(corpAccount);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getCorpAccountFromProfile(), corpAccount);
        assertEquals(getViewModel().contractFromProfileName.text().getRawValue(), contractName);
        assertVisible(getViewModel().contractFromProfileContainer);
        assertEquals(getViewModel().contractFromProfileSwitch.checked().getRawValue().booleanValue(), true);
        assertEquals(getViewModel().contractFromProfileSwitch.enabled().getRawValue().booleanValue(), true);
        assertEquals(getViewModel().contractFromProfileContainer.alpha().getRawValue().floatValue(), 1.0f);
    }

    @Test
    public void testCorpAccountAddedModifyFlow() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        when(resInfo.getPickupLocation()).thenReturn(mock(EHISolrLocation.class));

        EHIContract corpAccount = mock(EHIContract.class);
        String contractNumber = "iso111";
        String contractName = "Isobar corp.";
        when(corpAccount.getContractNumber()).thenReturn(contractNumber);
        when(corpAccount.getContractOrBillingName()).thenReturn(contractName);
        when(resInfo.getCorpAccount()).thenReturn(corpAccount);

        getViewModel().setIsModify(true);
        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getCorpAccountFromProfile(), corpAccount);
        assertEquals(getViewModel().contractFromProfileName.text().getRawValue(), contractName);
        assertVisible(getViewModel().contractFromProfileContainer);
        assertEquals(getViewModel().contractFromProfileSwitch.checked().getRawValue().booleanValue(), true);
        assertEquals(getViewModel().contractFromProfileSwitch.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().contractFromProfileContainer.alpha().getRawValue().floatValue(), 0.5f);
    }


    //endregion

    @Test
    public void testContinueButtonTextModify() {
        Date pickupDate = mock(Date.class);

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setIsModify(true);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().continueButton.textRes().getRawValue().intValue(), R.string.reservation_itinerary_in_modify_action_button);
    }

    //region emerald club

    @Test
    public void testECButtonECLoggedIn() {
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();
        getViewModel().setECWasLogedIn(true);

        assertVisible(getViewModel().ecEnabled);
        assertGone(getViewModel().ecSignInButton);
    }

    @Test
    public void testECButtonUnauth() {
        getViewModel().setIsModify(false);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();
        when(getViewModel().isUserLoggedIn()).thenReturn(false);

        assertGone(getViewModel().ecEnabled);
        assertVisible(getViewModel().ecSignInButton);
    }

    @Test
    public void testECButtonAuth() {
        when(getViewModel().isUserLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLoginManager().getProfileCollection()).thenReturn(mock(EHIProfileResponse.class));

        getViewModel().setIsModify(false);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertGone(getViewModel().ecEnabled);
        assertGone(getViewModel().ecSignInButton);
    }

    @Test
    public void testECButtonModify() {
        when(getViewModel().isUserLoggedIn()).thenReturn(false);

        getViewModel().setIsModify(true);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertGone(getViewModel().ecEnabled);
        assertGone(getViewModel().ecSignInButton);
    }

    //endregion

    //region age selector

    private List<EHIAgeOption> getAgeOptions() {
        List<EHIAgeOption> options = new LinkedList<>();
        options.add(new EHIAgeOption(18, "18"));
        options.add(new EHIAgeOption(19, "19"));
        options.add(new EHIAgeOption(20, "20"));
        options.add(new EHIAgeOption(21, "21"));
        options.add(new EHIAgeOption(22, "22"));
        options.add(new EHIAgeOption(23, "23"));
        options.add(new EHIAgeOption(24, "24"));
        options.add(new EHIAgeOption(25, "25+"));
        return options;
    }

    private GetSolrAgeOptionsResponse[] getAgeOptionsArray() {
        GetSolrAgeOptionsResponse[] options = new GetSolrAgeOptionsResponse[8];
        options[0] = new GetSolrAgeOptionsResponse(18, "18");
        options[1] = new GetSolrAgeOptionsResponse(19, "19");
        options[2] = new GetSolrAgeOptionsResponse(20, "20");
        options[3] = new GetSolrAgeOptionsResponse(21, "21");
        options[4] = new GetSolrAgeOptionsResponse(22, "22");
        options[5] = new GetSolrAgeOptionsResponse(23, "23");
        options[6] = new GetSolrAgeOptionsResponse(24, "24");
        options[7] = new GetSolrAgeOptionsResponse(25, "25+");
        return options;
    }

    @Test
    public void testAgeSpinnerAuth() {
        when(getViewModel().isUserLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLoginManager().getProfileCollection()).thenReturn(mock(EHIProfileResponse.class));

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertGone(getViewModel().driversAgeHeader);
        assertGone(getViewModel().driversAgeSpinner);
    }

    @Test
    public void testAgeSpinnerEC() {
        when(getViewModel().isUserLoggedIn()).thenReturn(false);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();
        getViewModel().setECWasLogedIn(true);

        assertGone(getViewModel().driversAgeHeader);
        assertGone(getViewModel().driversAgeSpinner);
    }

    @Test
    public void testAgeSpinnerModify() {
        when(getViewModel().isUserLoggedIn()).thenReturn(false);
        getViewModel().setIsModify(true);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertGone(getViewModel().driversAgeHeader);
        assertGone(getViewModel().driversAgeSpinner);
    }

    @Test
    public void testAgeSpinnerFromPickupLocation() {
        String title = "age spinner title";
        getMockedContext().getMockedResources().addAnswer("getString", title);
        List<EHIAgeOption> ageOptions = getAgeOptions();

        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        when(pickupLocation.getAgeOptions()).thenReturn(ageOptions);

        getViewModel().setPickUpLocation(pickupLocation);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().driversAgeHeader);
        assertVisible(getViewModel().driversAgeSpinner);
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getItemsToShow(), getViewModel().getDisplayAgeOptions());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getSelectedValueIndex().intValue(), getViewModel().getRentersAgeIndex());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getTitle(), title);
        assertEquals(getViewModel().getAgeOptions(), ageOptions);
        assertEquals(getViewModel().getRentersAgeIndex(), ageOptions.size() - 1);
        assertEquals(getViewModel().getRenterAge(), getViewModel().getAgeOptions().get(getViewModel().getRentersAgeIndex()).getValue());

        int selectedIndex = 3;
        EHIAgeOption selectedAgeModel = ageOptions.get(selectedIndex);

        getViewModel().setRenterAge(selectedAgeModel.getValue());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getItemsToShow(), getViewModel().getDisplayAgeOptions());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getSelectedValueIndex().intValue(), selectedIndex);
        assertEquals(getViewModel().getAgeOptions(), ageOptions);
        assertEquals(getViewModel().getRentersAgeIndex(), selectedIndex);
        assertEquals(getViewModel().getRenterAge(), selectedAgeModel.getValue());
    }

    @Test
    public void testAgeSpinnerFromSolr() {
        GetSolrAgeOptionsResponse[] response = getAgeOptionsArray();

        getMockRequestService().addMockResponse(GetSolrAgeOptionsRequest.class, new MockResponseWrapper<>(response, true, null));
        getViewModel().setApiService(getMockRequestService());
        List<EHIAgeOption> ageOptions = getAgeOptions();

        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        when(pickupLocation.getAgeOptions()).thenReturn(null);

        ReservationInformation resInfo = mock(ReservationInformation.class);
        when(resInfo.getPickupLocation()).thenReturn(pickupLocation);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().driversAgeHeader);
        assertVisible(getViewModel().driversAgeSpinner);
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getItemsToShow(), getViewModel().getDisplayAgeOptions());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getSelectedValueIndex().intValue(), getViewModel().getRentersAgeIndex());
        assertEquals(getViewModel().getAgeOptions(), ageOptions);
        assertEquals(getViewModel().getRentersAgeIndex(), ageOptions.size() - 1);
        assertEquals(getViewModel().getRenterAge(), getViewModel().getAgeOptions().get(getViewModel().getRentersAgeIndex()).getValue());

        int selectedIndex = 3;
        EHIAgeOption selectedAgeModel = ageOptions.get(selectedIndex);

        getViewModel().setRenterAge(selectedAgeModel.getValue());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getItemsToShow(), getViewModel().getDisplayAgeOptions());
        assertEquals(getViewModel().driversAgeSpinner.getPopulateMethodParamsBox().getSelectedValueIndex().intValue(), selectedIndex);
        assertEquals(getViewModel().getAgeOptions(), ageOptions);
        assertEquals(getViewModel().getRentersAgeIndex(), selectedIndex);
        assertEquals(getViewModel().getRenterAge(), selectedAgeModel.getValue());
    }


    //endregion

    //region cid field

    @Test
    public void testCidModifyNoCid() {
        getMockedContext().getMockedResources().addAnswer("getColor", 123);

        getViewModel().setIsModify(true);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().cidEditText.setText("");
        getViewModel().onAttachToView();

        assertVisible(getViewModel().cidContainer);
        assertGone(getViewModel().cidInputArea);
        assertGone(getViewModel().cidInputHeader);
        assertGone(getViewModel().cidEditText);
        assertGone(getViewModel().clearCidButton);
        assertVisible(getViewModel().addCidButton);
        assertEquals(getViewModel().addCidButton.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().addCidButton.textColor().getRawValue().intValue(), 123);
        assertEquals(getViewModel().addCidButton.drawableLeft().getRawValue().intValue(), R.drawable.icon_add_disable);
    }

    @Test
    public void testCidModifyWithCid() {
        String manuallyEnteredCid = "iso111";

        getViewModel().setIsModify(true);
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().cidEditText.setText(manuallyEnteredCid);
        getViewModel().onAttachToView();

        assertVisible(getViewModel().cidContainer);
        assertGone(getViewModel().cidInputHeader);
        assertVisible(getViewModel().cidInputArea);
        assertEquals(getViewModel().cidEditText.enabled().getRawValue().booleanValue(), false);
        assertEquals(getViewModel().cidEditText.text().getRawValue(), manuallyEnteredCid);
        assertVisible(getViewModel().cidEditText);
        assertGone(getViewModel().clearCidButton);
        assertGone(getViewModel().addCidButton);
    }

    @Test
    public void testCidWithEC() {
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().ecEnabled.setVisibility(View.VISIBLE);
        getViewModel().onAttachToView();
        getViewModel().setECWasLogedIn(true);

        assertVisible(getViewModel().cidContainer);

        getViewModel().ecEnabled.setVisibility(View.GONE);

        assertVisible(getViewModel().cidContainer);
    }

    @Test
    public void testCidWithCorpAccount() {
        ReservationInformation resInfo = mock(ReservationInformation.class);

        when(resInfo.getPickupLocation()).thenReturn(mock(EHISolrLocation.class));

        EHIContract corpAccount = mock(EHIContract.class);
        String contractNumber = "iso111";
        String contractName = "Isobar corp.";
        when(corpAccount.getContractNumber()).thenReturn(contractNumber);
        when(corpAccount.getContractOrBillingName()).thenReturn(contractName);

        when(resInfo.getCorpAccount()).thenReturn(corpAccount);

        getViewModel().populateFromReservationInformation(resInfo);
        getViewModel().contractFromProfileSwitch.setChecked(true);
        getViewModel().onAttachToView();

        assertGone(getViewModel().cidContainer);

        getViewModel().contractFromProfileSwitch.setChecked(false);

        assertVisible(getViewModel().cidContainer);
    }

    @Test
    public void testCidCollapsedState() {
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();

        assertGone(getViewModel().cidInputArea);
        assertGone(getViewModel().cidInputHeader);
        assertGone(getViewModel().cidEditText);
        assertGone(getViewModel().clearCidButton);
        assertVisible(getViewModel().addCidButton);
    }

    @Test
    public void testCidExtendedState() {
        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().onAttachToView();
        getViewModel().addCidButtonClicked();

        assertVisible(getViewModel().cidInputArea);
        assertVisible(getViewModel().cidInputHeader);
        assertVisible(getViewModel().cidEditText);
        assertVisible(getViewModel().clearCidButton);
        assertGone(getViewModel().addCidButton);

        getViewModel().clearCidButtonClicked();

        assertGone(getViewModel().cidInputArea);
        assertGone(getViewModel().cidInputHeader);
        assertGone(getViewModel().cidEditText);
        assertGone(getViewModel().clearCidButton);
        assertVisible(getViewModel().addCidButton);
    }

    //endregion

    //region network part

    @Test
    public void testPickupLocationForInitiate() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        String pickupLocationId = "pickup location id";
        when(pickupLocation.getPeopleSoftId()).thenReturn(pickupLocationId);

        getViewModel().setPickUpLocation(pickupLocation);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getPickupLocationIdForInitiate(), pickupLocationId);
    }

    @Test
    public void testReturnLocationForInitiate() {
        EHISolrLocation returnLocation = mock(EHISolrLocation.class);
        String returnLocationId = "return location id";

        when(returnLocation.getPeopleSoftId()).thenReturn(returnLocationId);

        getViewModel().setPickUpLocation(mock(EHISolrLocation.class));
        getViewModel().setReturnLocation(returnLocation);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getReturnLocationIdForInitiate(), returnLocationId);
    }

    @Test
    public void testNoReturnLocationForInitiate() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        String pickupLocationId = "pickup location id";
        when(pickupLocation.getPeopleSoftId()).thenReturn(pickupLocationId);

        getViewModel().setPickUpLocation(pickupLocation);
        getViewModel().onAttachToView();

        assertEquals(getViewModel().getReturnLocationIdForInitiate(), pickupLocationId);
    }

    @Test
    public void testCIDFromCorpAccountForInitiate() {
        testPopulatedFromReservationInformationCorpContract();

        assertEquals(getViewModel().getCidForInitiate(), getViewModel().getCorpAccountFromProfile().getContractNumber());

        getViewModel().contractFromProfileSwitch.setChecked(false);

        assertEquals(getViewModel().getCidForInitiate(), null);
    }

    @Test
    public void testManualCIDForInitiate() {
        testPopulatedFromReservationInformationManualCid();

        assertEquals(getViewModel().getCidForInitiate(), getViewModel().getManuallyEnteredCid());

        getViewModel().cidEditText.setText("");

        assertEquals(getViewModel().getCidForInitiate(), null);
    }

    @Test
    public void testShouldReturnPickupRoundTripFlowWhenNoLocationsSet() {
        assertEquals(getViewModel().getFlow(true), SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
    }

    @Test
    public void testShouldReturnDropoffOneWayWhenDropoffSelected() {
        assertEquals(getViewModel().getFlow(false), SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
    }

    @Test
    public void testShouldReturnPickupOneWayWhenThereArePickupAndDropoff() {
        getViewModel().setReturnLocation(mock(EHISolrLocation.class));
        assertEquals(getViewModel().getFlow(true), SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
    }

    private void setupOldDates() {
        Date pickupDate = new Date(1343805819051L);
        Date dropoffDate = new Date(1343805819052L);
        Date pickupTime = new Date(1343805819053L);
        Date dropoffTime = new Date(1343805819054L);
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setDropoffDate(dropoffDate);
        getViewModel().setPickupTime(pickupTime);
        getViewModel().setDropoffTime(dropoffTime);
    }

    @Test
    public void testRoundTripDatesSet() {
        setupOldDates();

        Date newPickupDate = new Date(1343805819061L);
        Date newDropoffDate = new Date(1343805819062L);
        Date newPickupTime = new Date(1343805819063L);
        Date newDropoffTime = new Date(1343805819064L);
        getViewModel().updateDatesFromFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP, newPickupDate, newDropoffDate, newPickupTime, newDropoffTime);

        //then
        assertEquals(getViewModel().getPickupDate(), newPickupDate);
        assertEquals(getViewModel().getReturnDate(), newDropoffDate);
        assertEquals(getViewModel().getPickupTime(), newPickupTime);
        assertEquals(getViewModel().getReturnTime(), newDropoffTime);
    }

    @Test
    public void testPickupOneWayDatesSet() {
        //setting up old dates
        Date pickupDate = new Date(1343805819051L);
        Date oldDropoffDate = new Date(1343805819062L);
        Date pickupTime = new Date(1343805819053L);
        Date oldDropoffTime = new Date(1343805819064L);
        getViewModel().setPickupDate(pickupDate);
        getViewModel().setDropoffDate(oldDropoffDate);
        getViewModel().setPickupTime(pickupTime);
        getViewModel().setDropoffTime(oldDropoffTime);

        //new dates
        Date newPickupDate = new Date(1343805819061L);
        Date newDropoffDate = new Date(1343805819052L);
        Date newPickupTime = new Date(1343805819063L);
        Date newDropoffTime = new Date(1343805819054L);
        getViewModel().updateDatesFromFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY, newPickupDate, newDropoffDate, newPickupTime, newDropoffTime);

        assertEquals(getViewModel().getPickupDate(), newPickupDate);
        assertEquals(getViewModel().getReturnDate(), oldDropoffDate);
        assertEquals(getViewModel().getPickupTime(), newPickupTime);
        assertEquals(getViewModel().getReturnTime(), oldDropoffTime);
    }

    @Test
    public void testDropoffOneWayDatesSet() {
        //setting up old dates
        Date oldPickupDate = new Date(1343805819051L);
        Date oldDropoffDate = new Date(1343805819052L);
        Date oldPickupTime = new Date(1343805819053L);
        Date oldDropoffTime = new Date(1343805819054L);
        getViewModel().setPickupDate(oldPickupDate);
        getViewModel().setDropoffDate(oldDropoffDate);
        getViewModel().setPickupTime(oldPickupTime);
        getViewModel().setDropoffTime(oldDropoffTime);

        //new dates
        Date newPickupDate = new Date(1343805819061L);
        Date newDropoffDate = new Date(1343805819062L);
        Date newPickupTime = new Date(1343805819063L);
        Date newDropoffTime = new Date(1343805819064L);
        getViewModel().updateDatesFromFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY, newPickupDate, newDropoffDate, newPickupTime, newDropoffTime);

        assertEquals(getViewModel().getPickupDate(), oldPickupDate);
        assertEquals(getViewModel().getReturnDate(), newDropoffDate);
        assertEquals(getViewModel().getPickupTime(), oldPickupTime);
        assertEquals(getViewModel().getReturnTime(), newDropoffTime);
    }

    @Test
    public void testShouldClearReturnWhenBeforePickupOnDropoff() {

        Date oldPickupDate = new Date(1343805819061L);
        Date oldDropoffDate = new Date(1343805819062L);
        Date oldPickupTime = new Date(1343805819053L);
        Date oldDropoffTime = new Date(1343805819054L);
        Date newPickupDate = new Date(1343805819063L);
        Date newPickupTime = new Date(1343805819064L);
        Date newDropoffDate = new Date(1343805819051L);
        Date newDropoffTime = new Date(1343805819052L);

        getViewModel().setPickupDate(oldPickupDate);
        getViewModel().setDropoffDate(oldDropoffDate);
        getViewModel().setPickupTime(oldPickupTime);
        getViewModel().setDropoffTime(oldDropoffTime);

        getViewModel().updateDatesFromFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY, newPickupDate, newDropoffDate, newPickupTime, newDropoffTime);

        assertEquals(getViewModel().getPickupDate(), oldPickupDate);
        assertNull(getViewModel().getReturnDate());
        assertEquals(getViewModel().getPickupTime(), oldPickupTime);
        assertNull(getViewModel().getReturnTime());
    }

    @Test
    public void testShouldClearReturnWhenBeforePickupOnPickup() {

        Date oldPickupDate = new Date(1343805819051L);
        Date oldDropoffDate = new Date(1343805819052L);
        Date oldPickupTime = new Date(1343805819053L);
        Date oldDropoffTime = new Date(1343805819054L);
        Date newPickupDate = new Date(1343805819062L);
        Date newDropoffDate = new Date(1343805819061L);
        Date newPickupTime = new Date(1343805819063L);
        Date newDropoffTime = new Date(1343805819064L);

        getViewModel().setPickupDate(oldPickupDate);
        getViewModel().setDropoffDate(oldDropoffDate);
        getViewModel().setPickupTime(oldPickupTime);
        getViewModel().setDropoffTime(oldDropoffTime);

        getViewModel().updateDatesFromFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP, newPickupDate, newDropoffDate, newPickupTime, newDropoffTime);

        assertEquals(getViewModel().getPickupDate(), newPickupDate);
        assertNull(getViewModel().getReturnDate());
        assertEquals(getViewModel().getPickupTime(), newPickupTime);
        assertNull(getViewModel().getReturnTime());
    }


    //endregion

}
