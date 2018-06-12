package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHI3DSAdditionalData extends EHIModel{

    @SerializedName("redirect_url")
    private EHIRedirectUrl mRedirectUrl;

    public EHIRedirectUrl getRedirectUrl() {
        return mRedirectUrl;
    }
}
