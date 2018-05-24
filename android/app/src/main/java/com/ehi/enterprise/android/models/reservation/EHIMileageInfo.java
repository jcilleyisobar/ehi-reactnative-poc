package com.ehi.enterprise.android.models.reservation;

import com.google.gson.annotations.SerializedName;

public class EHIMileageInfo {
    @SerializedName("unlimited_mileage")
    private boolean mUnlimitedMileage;

    @SerializedName("distance_unit")
    private String mDistanceUnit;

    @SerializedName("excess_mileage_rate_payment")
    private EHIPrice mExcessMileageRatePayment;

    @SerializedName("excess_mileage_rate_view")
    private EHIPrice mExcessMileageRateView;

    @SerializedName("total_free_miles")
    private int mTotalFreeMiles;

    public boolean isUnlimitedMileage() {
        return mUnlimitedMileage;
    }

    public String getDistanceUnit() {
        return mDistanceUnit;
    }

    public EHIPrice getExcessMileageRatePayment() {
        return mExcessMileageRatePayment;
    }

    public EHIPrice getExcessMileageRateView() {
        return mExcessMileageRateView;
    }

    public int getTotalFreeMiles() {
        return mTotalFreeMiles;
    }
}
