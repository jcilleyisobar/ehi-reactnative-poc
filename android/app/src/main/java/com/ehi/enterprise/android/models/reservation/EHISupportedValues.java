package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHISupportedValues extends EHIModel {

    @SerializedName("display_text")
    private String mName;

    @SerializedName("value")
    private String mValue;

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }
}