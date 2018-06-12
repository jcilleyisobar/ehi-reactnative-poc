package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIPriceDifferences extends EHIModel {

    public static final String PREPAY = "PREPAY";
    public static final String UPGRADE = "UPGRADE";
    public static final String UPGRADE_PREPAY = "UPGRADE_PREPAY_TO_PREPAY";
    public static final String UPGRADE_PAYLATER = "UPGRADE_PAYLATER_TO_PAYLATER";
    public static final String UNPAID_REFUND_AMOUNT = "UNPAID_REFUND_AMOUNT";

    @SerializedName("difference_amount_payment")
    private EHIPrice mDifferenceAmountPayment;

    @SerializedName("difference_amount_view")
    private EHIPrice mDifferenceAmountView;

    @SerializedName("difference_type")
    private String mDifferenceType;

    public EHIPrice getDifferenceAmountPayment() {
        return mDifferenceAmountPayment;
    }

    public EHIPrice getDifferenceAmountView() {
        return mDifferenceAmountView;
    }

    public String getDifferenceType() {
        return mDifferenceType;
    }

}