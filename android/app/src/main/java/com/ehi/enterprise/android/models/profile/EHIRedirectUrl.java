package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIRedirectUrl extends EHIModel {

    @SerializedName("web_redirect_url")
    private String mWebRedirectUrl;

    @SerializedName("mobile_redirect_url")
    private String mMobileRedirectUrl;

    public String getWebRedirectUrl() {
        return mWebRedirectUrl;
    }

    public String getMobileRedirectUrl() {
        return mMobileRedirectUrl;
    }

}
