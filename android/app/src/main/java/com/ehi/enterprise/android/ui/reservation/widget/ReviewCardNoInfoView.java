package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewCardNoInfoViewBinding;
import com.ehi.enterprise.android.ui.reservation.ReviewFragment;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;

//TODO refactor me, add payment method was removed from this view , it's all old code, should work
//TODO only as indicator that CC was aded without info. Ask Egor what to do.
@ViewModel(ReviewCardNoInfoViewModel.class)
public class ReviewCardNoInfoView extends DataBindingViewModelView<ReviewCardNoInfoViewModel, ReviewCardNoInfoViewBinding> {

    @Nullable
    private ReviewPrepayAddPaymentListener mReviewPrepayAddPaymentListener;

    @Nullable
    private CreditCardViewClickListener mAddPrepayListener;

    //region constructors
    public ReviewCardNoInfoView(Context context) {
        this(context, null);
    }

    public ReviewCardNoInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewCardNoInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_card_no_info, null));
            return;
        }

        createViewBinding(R.layout.v_review_card_no_info);
    }
    //endregion

    //region onclick listeners
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().addPaymentMethodButton && mAddPrepayListener != null) {
                mAddPrepayListener.addCreditCard();
                EHIAnalyticsEvent.create()
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_PAYMENT_METHOD.value)
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().creditCardAddedRemoveButton) {
                if (mAddPrepayListener != null) {
                    mAddPrepayListener.removeCreditCard();
                }
            } else if (view == getViewBinding().haveReadConditions) {
                if (mReviewPrepayAddPaymentListener != null) {
                    mReviewPrepayAddPaymentListener.onPrepaymentPolicyClick();
                }
                EHIAnalyticsEvent.create()
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PRE_PAY_POLICY.value)
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                        .tagScreen()
                        .tagEvent();
            }

        }
    };
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visible(getViewModel().addPaymentButton.visible(), getViewBinding().addPaymentMethodButton));
        bind(ReactorView.visible(getViewModel().creditCardAddedButton.visible(), getViewBinding().creditCardAddedContainer));
    }

    public void toggleVisibility() {
        setVisibility(getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE, true);
    }

    public void setVisibility(final int visibility, boolean notify) {
        super.setVisibility(visibility);
        if (notify && mReviewPrepayAddPaymentListener != null) {
            switch (visibility) {
                case VISIBLE:
                    mReviewPrepayAddPaymentListener.isVisible(true);
                    break;
                case GONE:
                    mReviewPrepayAddPaymentListener.isVisible(false);
                    break;
            }
        }
    }

    public void setCreditCardAddedButtonVisible(boolean visible) {
        getViewModel().setCreditCardAddedButtonVisible(visible);
    }

    public void setAddPaymentButtonVisible(boolean isVisible) {
        getViewModel().setAddPaymentButtonVisible(isVisible);
    }

    public void setCreditCardAdded(boolean isAdded) {
        getViewModel().setCreditCardAdded(isAdded);
    }

    public void populateView() {
        getViewBinding().addPaymentMethodButton.setOnClickListener(mOnClickListener);
        getViewBinding().creditCardAddedRemoveButton.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditions.setOnClickListener(mOnClickListener);
    }

    public void setReviewPrepayAddPaymentListener(ReviewPrepayAddPaymentListener reviewPrepayAddPaymentListener) {
        mReviewPrepayAddPaymentListener = reviewPrepayAddPaymentListener;
    }

    public void setAddPrepayListener(CreditCardViewClickListener listener) {
        mAddPrepayListener = listener;
    }

    public interface ReviewPrepayAddPaymentListener {
        void onPrepaymentPolicyClick();

        void isVisible(boolean isVisible);
    }

    public interface CreditCardViewClickListener {
        void addCreditCard();

        void removeCreditCard();

        void editCreditCard();
    }

}