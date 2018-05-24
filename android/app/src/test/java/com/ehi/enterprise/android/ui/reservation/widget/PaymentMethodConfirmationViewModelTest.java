package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.models.reservation.EHIBillingAccount;
import com.ehi.enterprise.android.models.reservation.EHICardDetails;
import com.ehi.enterprise.android.models.reservation.EHIPayment;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentMethodConfirmationViewModelTest extends BaseViewModelTest<PaymentMethodConfirmationViewModel> {

    final EHIReservation ehiReservation = mock(EHIReservation.class);

    @Override
    protected Class<PaymentMethodConfirmationViewModel> getViewModelClass() {
        return PaymentMethodConfirmationViewModel.class;
    }

    private void mockCorporateContract() {
        when(ehiReservation.hasCorporateContract()).thenReturn(true);
    }

    @Test
    public void testShouldHideMainViewAtMainReservationFlow() {
        getViewModel().updateViews(ehiReservation, false, false);
        assertGone(getViewModel().rootView);
    }

    // *****************
    // CONFIRMATION FLOW TESTS
    // *****************

    @Test
    public void testShouldShowPaymentInfo() {
        final EHIPayment ehiPayment = mock(EHIPayment.class);
        when(ehiPayment.getCard()).thenReturn(mock(EHICardDetails.class));
        when(ehiPayment.getCard().getNumber()).thenReturn("4988893423334343");
        when(ehiReservation.getPayments()).thenReturn(new ArrayList<EHIPayment>() {{
            add(ehiPayment);
        }});
        getViewModel().updateViews(ehiReservation, false, true);
        Assert.assertTrue(getViewModel().hasPaymentInfo());
        Assert.assertEquals(getViewModel().paymentMethodDetailsView.text().getRawValue(), "4988893423334343");
        assertVisible(getViewModel().paymentMethodDetailsView);
        assertVisible(getViewModel().termsAndConditionsView);
    }

    @Test
    public void testHasCorporateContractsAttached() {
        mockCorporateContract();
        getViewModel().updateViews(ehiReservation, false, true);
        Assert.assertTrue(getViewModel().hasCorporateContractAttached());
    }

    @Test
    public void testShouldShowBillingAccountInfo() {
        mockCorporateContract();
        when(getViewModel().isUsingBillingAccountToPay()).thenReturn(true);
        when(getViewModel().hasPaymentInfo()).thenReturn(false);

        EHIBillingAccount ehiBillingAccount = mock(EHIBillingAccount.class);
        when(ehiBillingAccount.getBillingAccountNumber()).thenReturn("123");
        when(ehiReservation.getBillingAccount()).thenReturn(ehiBillingAccount);

        getViewModel().updateViews(ehiReservation, false, true);
        Assert.assertEquals(getViewModel().paymentMethodDetailsView.text().getRawValue(), "123");
        assertVisible(getViewModel().paymentMethodDetailsView);
        assertGone(getViewModel().termsAndConditionsView);
    }

    @Test
    public void testShouldShowReviewPaymentOptionsTextSubtitleInConfirmationFlow() {
        when(ehiReservation.getPayments()).thenReturn(new ArrayList<EHIPayment>());
        mockCorporateContract();
        when(getViewModel().isUsingBillingAccountToPay()).thenReturn(false);
        when(getViewModel().hasPaymentInfo()).thenReturn(false);
        getViewModel().updateViews(ehiReservation, false, true);
        assertGone(getViewModel().paymentMethodDetailsView);
        assertGone(getViewModel().termsAndConditionsView);
    }


    // *****************
    // MODIFY FLOW TESTS
    // *****************

    @Test
    public void testShouldHideMainViewHavingPaymentInfoInModify() {
        final EHIPayment ehiPayment = mock(EHIPayment.class);
        when(ehiReservation.getPayments()).thenReturn(new ArrayList<EHIPayment>() {{
            add(ehiPayment);
        }});
        getViewModel().updateViews(ehiReservation, true, false);
        assertGone(getViewModel().rootView);
    }

    @Test
    public void testShouldShowReviewPaymentOptionsTextNotHavingBillingInModify() {
        mockCorporateContract();
        when(getViewModel().isUsingBillingAccountToPay()).thenReturn(false);
        when(getViewModel().hasPaymentInfo()).thenReturn(false);

        EHIBillingAccount ehiBillingAccount = mock(EHIBillingAccount.class);
        when(ehiBillingAccount.getBillingAccountNumber()).thenReturn("123");
        when(ehiReservation.getBillingAccount()).thenReturn(ehiBillingAccount);

        getViewModel().updateViews(ehiReservation, true, false);
        assertGone(getViewModel().paymentMethodDetailsView);
        assertGone(getViewModel().termsAndConditionsView);
    }

    @Test
    public void testShouldShowBillingAccountInfoOnModify() {
        mockCorporateContract();
        when(getViewModel().isUsingBillingAccountToPay()).thenReturn(true);
        when(getViewModel().hasPaymentInfo()).thenReturn(false);

        EHIBillingAccount ehiBillingAccount = mock(EHIBillingAccount.class);
        when(ehiBillingAccount.getBillingAccountNumber()).thenReturn("123");
        when(ehiReservation.getBillingAccount()).thenReturn(ehiBillingAccount);

        getViewModel().updateViews(ehiReservation, true, false);
        Assert.assertEquals(getViewModel().paymentMethodDetailsView.text().getRawValue(), "123");
        assertGone(getViewModel().termsAndConditionsView);
        assertVisible(getViewModel().paymentMethodDetailsView);
    }

    @Test
    public void testShouldShowReviewPaymentOptionsTextInModifyFlow() {
        when(ehiReservation.getPayments()).thenReturn(new ArrayList<EHIPayment>());
        mockCorporateContract();
        when(getViewModel().isUsingBillingAccountToPay()).thenReturn(false);
        when(getViewModel().hasPaymentInfo()).thenReturn(false);
        getViewModel().updateViews(ehiReservation, true, false);
        assertGone(getViewModel().paymentMethodDetailsView);
        assertGone(getViewModel().termsAndConditionsView);
    }
}
