package com.ehi.enterprise.android.network.request_params.profile;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PostPastTripsParams {

    @SerializedName("membership_number")
    private final String mLoyaltyNumber;

    @SerializedName("from_date")
    private final Date mFromDate;

    @SerializedName("to_date")
    private final Date mToDate;

    public PostPastTripsParams(String loyaltyNumber, Date fromDate, Date toDate) {
        mLoyaltyNumber = loyaltyNumber;
        mFromDate = fromDate;
        mToDate = toDate;
    }
}
