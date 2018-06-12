package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHICancellation extends EHIModel {


    @SerializedName("cancel_fee_details")
    private List<EHICancelFeeDetail> mCancellationDetails;

    public EHIPrice getCancelFeeAmountView() {
            EHICancelFeeDetail feeDetail = getCancelFeeDetail();
            if (feeDetail != null) {
                return feeDetail.getFeeAmountView();
            }
        // should show as a zeroed value (currency 0)
        return emptyPrice();
    }

    @NonNull
    private EHIPrice emptyPrice() {
        return new EHIPrice(
                "",
                "",
                0
        );
    }

    public EHIPrice getRefundAmountView() {
            EHICancelFeeDetail feeDetail = getCancelFeeDetail();
            if (feeDetail != null) {
                return feeDetail.getRefundAmountView();
            }
            return emptyPrice();
    }

    public EHICancelFeeDetail getCancelFeeDetail() {
        if (!ListUtils.isEmpty(mCancellationDetails)) {
            for (EHICancelFeeDetail feeDetail : mCancellationDetails) {
                if (feeDetail.feeApplies()) {
                    return feeDetail;
                }
            }
        }
        return null;
    }

    public boolean isCancelFeeApply() {
        if (!ListUtils.isEmpty(mCancellationDetails)) {
            for (EHICancelFeeDetail feeDetail : mCancellationDetails) {
                if (feeDetail.feeApplies()) {
                    //if at least one fee applies return true, else defaulting to root level fee
                    return true;
                }
            }
        }
        return false;
    }
}
