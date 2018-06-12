package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetInvoiceResponse extends BaseResponse {
    @SerializedName("past_trip_detail")
    private EHITripSummary mInvoice;

    public EHITripSummary getInvoice() {
        return mInvoice;
    }
}
