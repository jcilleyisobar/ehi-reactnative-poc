package com.ehi.enterprise.android.network.responses.terms_conditions;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetEPlusTermsAndConditionsResponse extends BaseResponse {

    @SerializedName("terms_and_conditions")
    private String mTermsAndConditions;

    @SerializedName("terms_and_conditions_version")
    private String mTermsAndConditionsVersion;

    public String getTermsAndConditions() {
        return mTermsAndConditions;
    }

    public String getTermsAndConditionsVersion() {
        return mTermsAndConditionsVersion;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        mTermsAndConditions = termsAndConditions;
    }

}