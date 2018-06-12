package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.reservation.EHICancelFeeDetail;
import com.ehi.enterprise.android.models.reservation.EHICancellation;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.ui.confirmation.PrePayCancelDialogViewModel;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrepayCancelDialogViewModelTest extends BaseViewModelTest<PrePayCancelDialogViewModel> {
    @Override
    protected Class<PrePayCancelDialogViewModel> getViewModelClass() {
        return PrePayCancelDialogViewModel.class;
    }

    @Test
    public void testDefaultConfiguration() {
        final String cancelFee = "2";
        final String cancelFeeStyled = "$2.00";
        final String refundedAmount = "$5.00";
        final String originalAmount = "$50.00";

        when(getViewModel().isTravelingBetweenUSAndCanada()).thenReturn(false);
        initializeMocksAndScreen(cancelFee, cancelFeeStyled, refundedAmount, originalAmount);

        assertEquals(cancelFeeStyled, getViewModel().cancellationFeeViewState.text().getRawValue());
        assertEquals(refundedAmount, getViewModel().refundedAmountViewState.textCharSequence().getRawValue());
        assertEquals(originalAmount, getViewModel().originalAmountViewState.textCharSequence().getRawValue());
        assertGone(getViewModel().refundedAsViewState);
        assertGone(getViewModel().travelingBetweenUsAndCanadaViewState);
    }

    @Test
    public void testIsTravelingBetweenUSAndCanadaScenario() {
        final String cancelFee = "2";

        final String cancelFeeStyled = "$2.00";
        final String refundedAmount = "$5.00";
        final String originalAmount = "$50.00";

        when(getViewModel().isTravelingBetweenUSAndCanada()).thenReturn(true);

        initializeMocksAndScreen(cancelFee, cancelFeeStyled, refundedAmount, originalAmount);

        assertVisible(getViewModel().refundedAsViewState);
        assertVisible(getViewModel().travelingBetweenUsAndCanadaViewState);
    }

    private void initializeMocksAndScreen(String cancelFee, String cancelFeeStyled, String refundedAmount, String originalAmount) {
        final EHIPrice cancelFeeMock = mockCancelFeePrice(cancelFee, cancelFeeStyled);

        final EHICancellation ehiCancellation = mock(EHICancellation.class);
        when(ehiCancellation.getCancelFeeAmountView()).thenReturn(cancelFeeMock);

        final EHIPrice priceMock = mock(EHIPrice.class);


        //mocking cancel fee detail
        final EHICancelFeeDetail cancelFeeDetail = mock(EHICancelFeeDetail.class);
        when(ehiCancellation.getCancelFeeDetail()).thenReturn(cancelFeeDetail);

        when(ehiCancellation.getCancelFeeDetail().getOriginalAmountView()).thenReturn(priceMock);
        when(ehiCancellation.getCancelFeeDetail().getOriginalAmountView().getFormattedPrice(false)).thenReturn(originalAmount);

        when(ehiCancellation.getCancelFeeDetail().getRefundAmountPayment()).thenReturn(priceMock);
        when(ehiCancellation.getCancelFeeDetail().getRefundAmountPayment().getCurrencyCode()).thenReturn(EHIPriceSummary.CA_CURRENCY);
        when(ehiCancellation.getCancelFeeDetail().getRefundAmountPayment().getFormattedPrice(false)).thenReturn("CA $5.00");

        when(ehiCancellation.getRefundAmountView()).thenReturn(priceMock);
        when(ehiCancellation.getRefundAmountView().getFormattedPrice(false)).thenReturn(refundedAmount);
        //mocking original value
        getViewModel().setOriginalAmount(originalAmount);

        //mocking res cancel title
        getMockedContext().getMockedResources().addAnswer("getString", "By tapping &quot;Yes, Cancel Reservation&quot;, your reservation will be cancelled and you will receive a refund applied to the original form of payment. This action can\\'t be undone. - #{terms} ");

        getViewModel().setEHICancelation(ehiCancellation);
        getViewModel().onAttachToView();
    }

    @NonNull
    private EHIPrice mockCancelFeePrice(String cancelFee, String cancelFeeStyled) {
        final EHIPrice cancelFeeMock = mock(EHIPrice.class);
        when(cancelFeeMock.getFormattedPrice(false)).thenReturn(cancelFee);
        when(cancelFeeMock.getFormattedPrice(true)).thenReturn(cancelFeeStyled);
        when(cancelFeeMock.getDoubleAmmount()).thenReturn(0.0);
        return cancelFeeMock;
    }
}
