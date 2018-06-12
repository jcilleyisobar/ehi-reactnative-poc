package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHI3DSData extends EHIModel{

    @SerializedName("acs_url")
    private String mACSUrl;

    @SerializedName("pa_req")
    private String mPAReq;

    @SerializedName("perform3_ds")
    private boolean mPerform3DS;

    public String getACSUrl() {
        return mACSUrl;
    }

    public String getPAReq() {
        return mPAReq;
    }

    public boolean isPerform3DS() {
        return mPerform3DS;
    }
}
