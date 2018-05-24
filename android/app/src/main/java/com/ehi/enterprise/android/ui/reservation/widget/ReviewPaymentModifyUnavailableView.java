package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewPaymentModifyUnavailableBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ReviewPaymentModifyUnavailableView extends DataBindingViewModelView<ManagersAccessViewModel, ReviewPaymentModifyUnavailableBinding> {
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mPaymentModifyUnavailableListener.onPrepaymentPolicyClick();
        }
    };

    @Nullable
    private PaymentModifyUnavailableListener mPaymentModifyUnavailableListener;

    public ReviewPaymentModifyUnavailableView(Context context) {
        this(context, null);
    }

    public ReviewPaymentModifyUnavailableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewPaymentModifyUnavailableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_review_payment_modify_unavailable);
        initViews();
    }

    private void initViews() {
        getViewBinding().termsAndConditions.setOnClickListener(mOnClickListener);
    }

    public void setPaymentModifyUnavailableListener(@Nullable PaymentModifyUnavailableListener paymentModifyUnavailableListener) {
        mPaymentModifyUnavailableListener = paymentModifyUnavailableListener;
    }

    public interface PaymentModifyUnavailableListener {
        void onPrepaymentPolicyClick();
    }
}
