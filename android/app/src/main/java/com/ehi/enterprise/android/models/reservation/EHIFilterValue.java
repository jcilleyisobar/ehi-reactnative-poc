package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIFilterValue extends EHIModel{

    @SerializedName("code")
    private String mCode;

    @SerializedName("description")
    private String mDescription;

    //Internal use (non-service)
    @SerializedName("active")
    private boolean mActive;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCode() {

        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public boolean isCodeNull(){
        return getCode() == null || getCode().equalsIgnoreCase("null");
    }
}
