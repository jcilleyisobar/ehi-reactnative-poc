package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHIVehicleRate;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.android.network.requests.reservation.PostCommitRequest;
import com.ehi.enterprise.android.network.requests.reservation.PutAssociateProfileRequest;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.helpers.MockableObject;
import com.ehi.enterprise.mock.network.MockResponseWrapper;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ReviewViewModelTest extends BaseViewModelTest<ReviewViewModel> {
    @Override
    protected Class<ReviewViewModel> getViewModelClass() {
        return ReviewViewModel.class;
    }

    private static final String PAY_AT_PICKUP = "Pay At Pickup";
    private static final String BILL_TO_ACCOUNT = "Bill to #{account}";

    private MockableObject.TestAnswer mStringAnswer = new MockableObject.TestAnswer() {
        @Override
        public Object provideAnswer(final InvocationOnMock invocation) {
            final Object[] arguments = invocation.getArguments();
            int stringResId = (int) arguments[0];

            switch (stringResId) {
                case R.string.reservations_review_book_button_billing_subtitle:
                    return BILL_TO_ACCOUNT;
                case R.string.reservations_review_book_button_subtitle:
                    return PAY_AT_PICKUP;
            }

            return null;
        }
    };

    @Test
    public void testIsUserInCorpFlowPayingWithBillingNumber() {
        getViewModel().onBillingMethodChanged("1234", null, null, CommitRequestParams.BILLING_ACCOUNT);
        assertEquals(getViewModel().getBillingNumber(), "1234");
        Assert.assertNull(getViewModel().getPaymentIds());
    }

    @Test
    public void testIsUserInCorpFlowPayingWithCreditCard() {
        List<String> paymentIds = Collections.singletonList("41111111111111111");
        getViewModel().onBillingMethodChanged(null, null, paymentIds, CommitRequestParams.BILLING_ACCOUNT);
        assertEquals(getViewModel().getPaymentIds().get(0), "41111111111111111");
        Assert.assertNull(getViewModel().getBillingNumber());
    }

    @Test
    public void testIsUserInCorpFlowPayingAtCounter() {
        List<String> paymentIds = Collections.singletonList("41111111111111111");
        getViewModel().onBillingMethodChanged(null, null, paymentIds, CommitRequestParams.PAY_AT_COUNTER);
        assertEquals(getViewModel().getPaymentIds().get(0), "41111111111111111");
        Assert.assertNull(getViewModel().getBillingNumber());
    }

    @Test
    public void testUserAddedCreditCardAndPaymentShouldNotBeErased() {
        getViewModel().setPaymentReferenceId("4111111111111111");
        getViewModel().onBillingMethodChanged(null, null, null, null);
        assertEquals(getViewModel().getPaymentIds().get(0), "4111111111111111");
        Assert.assertNull(getViewModel().getBillingNumber());
    }

    @Test
    public void testNetRateShouldShowCorrectlyOnBookButton() {
        EHIReservation reservation = mock(EHIReservation.class);
        EHICarClassDetails details = mock(EHICarClassDetails.class);
        EHIVehicleRate rate = mock(EHIVehicleRate.class);
        when(details.isSecretRateAfterCarSelected()).thenReturn(true);
        when(reservation.getCarClassDetails()).thenReturn(details);
        when(details.getVehicleRates()).thenReturn(Collections.singletonList(rate));

        getMockedContext().getMockedResources().addAnswer(mStringAnswer);

        getViewModel().setRawReservationObject(reservation);
        getViewModel().onBillingMethodChanged("1234", null, null, CommitRequestParams.BILLING_ACCOUNT);
        getViewModel().updateContinueButton();

        assertVisible(getViewModel().continueButton.netRateVisibility().getRawValue());
        assertGone(getViewModel().continueButton.priceVisibility().getRawValue());
        assertEquals("Bill to 1234", getViewModel().continueButton.subtitle().getRawValue());
    }

    @Test
    public void testPayAtPickupShouldFillBookButton() {
        EHIReservation reservation = mock(EHIReservation.class);
        EHICarClassDetails details = mock(EHICarClassDetails.class);
        EHIVehicleRate rate = mock(EHIVehicleRate.class);
        EHIPriceSummary priceSummary = mock(EHIPriceSummary.class);
        when(reservation.getCarClassDetails()).thenReturn(details);
        when(details.getVehicleRates()).thenReturn(Collections.singletonList(rate));
        when(details.getPaylaterPriceSummary()).thenReturn(priceSummary);
        when(priceSummary.getEstimatedTotalView()).thenReturn(mock(EHIPrice.class));

        getMockedContext().getMockedResources().addAnswer(mStringAnswer);

        getViewModel().setRawReservationObject(reservation);
        getViewModel().updateContinueButton();

        assertVisible(getViewModel().continueButton.priceVisibility().getRawValue());
        assertGone(getViewModel().continueButton.netRateVisibility().getRawValue());
        assertEquals(PAY_AT_PICKUP, getViewModel().continueButton.subtitle().getRawValue());
    }

    @Test
    public void testShouldBillToCorporateAccountOnBookButton() {
        final String contractName = "corporate account";
        EHIReservation reservation = mock(EHIReservation.class);
        EHIContract contract = mock(EHIContract.class);
        when(contract.getContractName()).thenReturn(contractName);
        when(reservation.getCorporateAccount()).thenReturn(contract);

        EHICarClassDetails details = mock(EHICarClassDetails.class);
        EHIVehicleRate rate = mock(EHIVehicleRate.class);
        when(reservation.getCarClassDetails()).thenReturn(details);
        when(details.getVehicleRates()).thenReturn(Collections.singletonList(rate));

        getMockedContext().getMockedResources().addAnswer(mStringAnswer);

        getViewModel().setRawReservationObject(reservation);
        getViewModel().updateContinueButton();

        assertEquals("Bill to " + contractName, getViewModel().continueButton.subtitle().getRawValue());
    }

    @Test
    public void testECLoggedAfterStartReservationShouldAssociateAccountAndCommit() {

        final String resId = "123";
        final String individualId = "abc";

        EHIReservation reservation = mock(EHIReservation.class);
        when(reservation.getResSessionId()).thenReturn(resId);

        ProfileCollection profileCollection = mock(ProfileCollection.class);
        EHIProfile profile = mock(EHIProfile.class);
        when(getViewModel().isLoggedIntoEmeraldClub()).thenReturn(true); // EC
        when(getViewModel().getEmeraldClubProfile()).thenReturn(profileCollection);
        when(profileCollection.getProfile()).thenReturn(profile);
        when(profile.getIndividualId()).thenReturn(individualId);

        getViewModel().setRawReservationObject(reservation);
        getViewModel().setIsLoginAfterStart(true);
        getViewModel().commitReservation();

        checkRequest(getMockRequestService().getPendingRequests().get(PutAssociateProfileRequest.class).getRequestUrl(),
                Arrays.asList(individualId, "EC", resId));
        getMockRequestService().addAndExecuteMockResponse(PutAssociateProfileRequest.class, new MockResponseWrapper<>(
                reservation, true, null
        ));
        checkRequest(getMockRequestService().getPendingRequests().get(PostCommitRequest.class).getRequestUrl(),
                Arrays.asList("commit", resId));

    }

    @Test
    public void testEPLoggedAfterStartReservationShouldAssociateAccountAndCommit() {

        final String resId = "123";
        final String individualId = "abc";

        EHIReservation reservation = mock(EHIReservation.class);
        when(reservation.getResSessionId()).thenReturn(resId);

        ProfileCollection profileCollection = mock(ProfileCollection.class);
        EHIProfile profile = mock(EHIProfile.class);
        when(getViewModel().isLoggedIntoEmeraldClub()).thenReturn(false); // EP
        when(getMockedDelegate().getLoginManager().getProfileCollection()).thenReturn(profileCollection);
        when(profileCollection.getProfile()).thenReturn(profile);
        when(profile.getIndividualId()).thenReturn(individualId);

        getViewModel().setRawReservationObject(reservation);
        getViewModel().setIsLoginAfterStart(true);
        getViewModel().commitReservation();

        checkRequest(getMockRequestService().getPendingRequests().get(PutAssociateProfileRequest.class).getRequestUrl(),
                Arrays.asList(individualId, "EP", resId));
        getMockRequestService().addAndExecuteMockResponse(PutAssociateProfileRequest.class, new MockResponseWrapper<>(
                reservation, true, null
        ));

        checkRequest(getMockRequestService().getPendingRequests().get(PostCommitRequest.class).getRequestUrl(),
                Arrays.asList("commit", resId));

    }

    private void checkRequest(String request, List<String> params) {
        final String[] split = request.split("/");
        int size = split.length;
        int i = 1;
        for (String param : params) {
            assertEquals(param, split[size - i++]);
        }
    }
}
