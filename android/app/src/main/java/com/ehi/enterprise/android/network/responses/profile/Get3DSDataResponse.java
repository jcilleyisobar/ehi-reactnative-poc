package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHI3DSAdditionalData;
import com.ehi.enterprise.android.models.profile.EHI3DSData;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class Get3DSDataResponse extends BaseResponse{

    @SerializedName("prepay3_dsdata")
    private EHI3DSData m3DSData;


    @SerializedName("additional_data")
    private EHI3DSAdditionalData mAdditionalData;

    public EHI3DSData get3DSData() {
        return m3DSData;
    }

    public EHI3DSAdditionalData getAdditionalData() {
        return mAdditionalData;
    }
}
