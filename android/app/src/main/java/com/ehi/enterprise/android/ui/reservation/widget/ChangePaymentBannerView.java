package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ChangePaymentBannerViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ChangePaymentBannerViewModel.class)
public class ChangePaymentBannerView extends DataBindingViewModelView<ChangePaymentBannerViewModel, ChangePaymentBannerViewBinding> {

    private ChangePaymentBannerViewListener mListener;

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().infoIcon) {
                mListener.onPaymentInfoClicked();
            } else {
                mListener.onChangePaymentClicked();
            }
        }
    };

    public ChangePaymentBannerView(Context context) {
        this(context, null, 0);
    }

    public ChangePaymentBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangePaymentBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_change_payment_banner, null));
            return;
        }
        createViewBinding(R.layout.v_change_payment_banner);
        initViews();
    }

    private void initViews() {
        getViewBinding().infoIcon.setOnClickListener(mOnClickListener);
        getViewBinding().bannerContainer.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.text(getViewModel().messageText.textCharSequence(), getViewBinding().message));
        bind(ReactorView.visibility(getViewModel().creditCardIcon.visibility(), getViewBinding().creditCardIcon));
    }

    public void populateView(EHICarClassDetails carClassDetails, ReservationFlowListener.PayState payState) {
        getViewModel().setup(carClassDetails, payState);
    }

    public void setPaymentBannerViewListener(ChangePaymentBannerViewListener listener) {
        mListener = listener;
    }

    public interface ChangePaymentBannerViewListener {
        void onPaymentInfoClicked();
        void onChangePaymentClicked();
    }
}
