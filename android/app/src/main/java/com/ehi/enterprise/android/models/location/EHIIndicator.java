package com.ehi.enterprise.android.models.location;

import com.google.gson.annotations.SerializedName;

public class EHIIndicator {

    @SerializedName("code")
    private String mCode;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }
}
