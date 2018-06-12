package com.ehi.enterprise.android.ui.reservation.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIPayment;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.List;

public class PaymentMethodConfirmationViewModel extends ManagersAccessViewModel {
    private EHIReservation ehiReservation;

    public ReactorTextViewState paymentMethodDetailsView = new ReactorTextViewState();
    public ReactorTextViewState paymentMethodView = new ReactorTextViewState();
    public ReactorViewState termsAndConditionsView = new ReactorViewState();
    public ReactorViewState rootView = new ReactorViewState();

    public EHIReservation getEhiReservation() {
        return ehiReservation;
    }

    private void setEhiReservation(EHIReservation ehiReservation) {
        this.ehiReservation = ehiReservation;
    }

    public void updateViews(EHIReservation ehiReservation, boolean isModify, boolean isConfirmation) {
        setEhiReservation(ehiReservation);
        rootView.setVisibility(View.VISIBLE);

        if (hasPaymentInfo() && isConfirmation) {
            final List<EHIPayment> payments = ehiReservation.getPayments();
            termsAndConditionsView.setVisibility(View.VISIBLE);
            paymentMethodDetailsView.setVisibility(View.VISIBLE);
            paymentMethodView.setText(getResources().getString(R.string.reservation_confirmation_prepay_payment_title));
            paymentMethodDetailsView.setText(payments.get(0).getCard().getNumber());
            paymentMethodDetailsView.setDrawableLeft(BaseAppUtils.getCardIconByType(payments.get(0).getCard().getCardType()));
            return;
        } else if (hasCorporateContractAttached()) {
            if (isUsingBillingAccountToPay() && (isConfirmation || isModify) ) {
                paymentMethodDetailsView.setVisibility(View.VISIBLE);
                termsAndConditionsView.setVisibility(View.GONE);
                paymentMethodDetailsView.setText(ehiReservation.getBillingAccount().getBillingAccountNumber());
                paymentMethodView.setText(getResources().getString(R.string.reservation_confirmation_payment_billing_title));
                return;
            } else if (isConfirmation || isModify) {
                paymentMethodDetailsView.setVisibility(View.GONE);
                termsAndConditionsView.setVisibility(View.GONE);
                paymentMethodView.setText(getResources().getString(R.string.review_payment_options_payment_subtitle));
                return;
            }
        }
        rootView.setVisibility(View.GONE);
    }

    protected boolean hasPaymentInfo() {
        return ehiReservation != null && !ListUtils.isEmpty(ehiReservation.getPayments());
    }

    protected boolean hasCorporateContractAttached() {
        return ehiReservation != null && ehiReservation.hasCorporateContract();
    }

    protected boolean isUsingBillingAccountToPay() {
        return ehiReservation != null && ehiReservation.getBillingAccount() != null;
    }
}
