package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetKeyFactsInformationResponse extends BaseResponse{
    @SerializedName("key_facts") String mContent;

    public String getContent() {
        return mContent;
    }
}
