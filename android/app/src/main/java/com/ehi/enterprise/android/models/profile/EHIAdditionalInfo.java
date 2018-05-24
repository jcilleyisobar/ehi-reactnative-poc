package com.ehi.enterprise.android.models.profile;

import com.google.gson.annotations.SerializedName;

public class EHIAdditionalInfo {

    public static final String MODE_UPDATE = "UPDATE";

    @SerializedName("update_profile_mode")
    private String mUpdateProfileMode;

    public EHIAdditionalInfo(String updateProfileMode) {
        mUpdateProfileMode = updateProfileMode;
    }
}
