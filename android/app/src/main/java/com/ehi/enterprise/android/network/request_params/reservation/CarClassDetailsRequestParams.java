package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class CarClassDetailsRequestParams {

    @SerializedName("car_class_code")
    private final String mCarClassCode;

    @SerializedName("skip_upgrade_search")
    private final boolean mSkipUpgradeSearch;

    @SerializedName("redemption_day_count")
    private final int mRedemptionDayCount;

    @SerializedName("prepay_selected")
    private final boolean mPrepaySelected;

    public CarClassDetailsRequestParams(String carClassCode, boolean skipUpgradeSearch, int redemptionDayCount, boolean prepaySelected) {
        mCarClassCode = carClassCode;
        mSkipUpgradeSearch = skipUpgradeSearch;
        mRedemptionDayCount = redemptionDayCount;
        mPrepaySelected = prepaySelected;
    }

}