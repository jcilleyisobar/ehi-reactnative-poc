package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetMoreTaxesInformationResponse extends BaseResponse{
    @SerializedName("taxes_fees_and_surcharges") String mContent;

    public String getContent() {
        return mContent;
    }
}
