package com.ehi.enterprise.android.models.profile.payment.pangui;


import com.google.gson.annotations.SerializedName;

public class EHIBusinessMessage {

    @SerializedName("LocalizedMessage")
    private String mLocalizedMessage;

    public String getLocalizedMessage() {
        return mLocalizedMessage;
    }
}
