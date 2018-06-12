package com.ehi.enterprise.android.network.responses.contract;

import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetContractResponse extends BaseResponse {

    @SerializedName("contract_details")
    private EHIContract mContractDetails;

    public EHIContract getContractDetails() {
        return mContractDetails;
    }
}
