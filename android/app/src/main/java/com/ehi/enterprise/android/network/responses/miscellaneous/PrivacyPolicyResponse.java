package com.ehi.enterprise.android.network.responses.miscellaneous;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class PrivacyPolicyResponse extends BaseResponse{
    @SerializedName("privacy_policy")
    private String mPrivacyPolicy;

    public String getPrivacyPolicy() {
        return mPrivacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        mPrivacyPolicy = privacyPolicy;
    }
}
