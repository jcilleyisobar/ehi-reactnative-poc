package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.google.gson.annotations.SerializedName;

public class EHICommonResponse {

    @SerializedName("BusinessMessage")
    private EHIBusinessMessage mBusinessMessage;

    public String getLocalizedMessage() {
        if (mBusinessMessage != null) {
            return mBusinessMessage.getLocalizedMessage();
        } else {
            return null;
        }
    }
}
