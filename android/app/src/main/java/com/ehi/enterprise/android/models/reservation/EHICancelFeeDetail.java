package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHICancelFeeDetail extends EHIModel {

    @SerializedName("fee_amount_view")
    private EHIPrice mFeeAmountView;

    @SerializedName("fee_amount_payment")
    private EHIPrice mFeeAmountPayment;

    @SerializedName("refund_amount_payment")
    private EHIPrice mRefundAmountPayment;

    @SerializedName("refund_amount_view")
    private EHIPrice mRefundAmountView;

    @SerializedName("original_amount_payment")
    private EHIPrice mOriginalAmountPayment;

    @SerializedName("original_amount_view")
    private EHIPrice mOriginalAmountView;

    @SerializedName("fee_apply")
    private boolean mFeeApply;

    @SerializedName("fee_dead_line_in_hours")
    private int mFeeDeadlineInHours;

    public boolean feeApplies() {
        return mFeeApply;
    }

    public EHIPrice getRefundAmountPayment() {
        return mRefundAmountPayment;
    }

    public EHIPrice getRefundAmountView() {
        return mRefundAmountView;
    }

    public int getFeeDeadlineInDays() {
        return mFeeDeadlineInHours/24;
    }

    public int getFeeDeadlineInHours(){
        return mFeeDeadlineInHours;
    }

    public EHIPrice getOriginalAmountPayment() {
        return mOriginalAmountPayment;
    }

    public EHIPrice getOriginalAmountView() {
        return mOriginalAmountView;
    }

    public EHIPrice getFeeAmountView() {
        return mFeeAmountView;
    }

    public EHIPrice getFeeAmountPayment() {
        return mFeeAmountPayment;
    }
}
