package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.google.gson.annotations.SerializedName;

public class SelectCarClassModifyRequestParams {

    @SerializedName("car_class_code")
    private final String mCarClassCode;

    @SerializedName("redemption_day_count")
    private final int mRedemptionDayCount;

    @SerializedName("prepay_selected")
    private final boolean mPrepaySelected;

    public SelectCarClassModifyRequestParams(String carClassCode, int redemptionDayCount, boolean prepaySelected) {
        mCarClassCode = carClassCode;
        mRedemptionDayCount = redemptionDayCount;
        mPrepaySelected = prepaySelected;
    }

}