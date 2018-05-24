package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIPreference extends EHIModel {

    @SerializedName("source_code")
    private final String mSourceCode;

    @SerializedName("email_preference")
    private EHIEmailPreference mEmailPreference;


    public EHIEmailPreference getEmailPreference() {
        return mEmailPreference;
    }

    public void setEmailPreference(EHIEmailPreference emailPreference) {
        mEmailPreference = emailPreference;
    }

    public EHIPreference(EHIEmailPreference emailPreference, String sourceCode) {
        mEmailPreference = emailPreference;
        mSourceCode = sourceCode;
    }
}
