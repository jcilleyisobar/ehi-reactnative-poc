package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PaymentMethodDetailsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(PaymentMethodConfirmationViewModel.class)
public class PaymentMethodConfirmationView extends DataBindingViewModelView<PaymentMethodConfirmationViewModel, PaymentMethodDetailsViewBinding> {

    private static final String TAG = "PaymentMethodDetailsView";
    //region constructors
    public PaymentMethodConfirmationView(Context context) {
        this(context, null, 0);
    }

    public PaymentMethodConfirmationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodConfirmationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_payment_method_details, null));
            return;
        }

        createViewBinding(R.layout.v_payment_method_details);
    }

    public void setTermsAndConditionsClickListener(OnClickListener onClickListener) {
        getViewBinding().payNowTermsConditionsContainer.setOnClickListener(onClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().rootView.visibility(), getViewBinding().getRoot()));

        bind(ReactorView.visibility(getViewModel().paymentMethodDetailsView.visibility(), getViewBinding().paymentMethodDetails));
        bind(ReactorTextView.text(getViewModel().paymentMethodDetailsView.text(), getViewBinding().paymentMethodDetails));
        bind(ReactorTextView.drawableLeft(getViewModel().paymentMethodDetailsView.drawableLeft(), getViewBinding().paymentMethodDetails));

        bind(ReactorTextView.text(getViewModel().paymentMethodView.text(), getViewBinding().paymentMethod));

        bind(ReactorView.visibility(getViewModel().termsAndConditionsView.visibility(), getViewBinding().payNowTermsConditionsContainer));
    }

    public void setupView(EHIReservation ehiReservation, boolean isModify, boolean isConfirmation) {
        getViewModel().updateViews(ehiReservation, isModify, isConfirmation);
    }
}