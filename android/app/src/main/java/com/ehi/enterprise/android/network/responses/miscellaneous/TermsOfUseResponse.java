package com.ehi.enterprise.android.network.responses.miscellaneous;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class TermsOfUseResponse extends BaseResponse {

    @SerializedName("terms_of_use")
    private String mTermsOfUse;

    public String getTermsOfUse() {
        return mTermsOfUse;
    }

    public void setTermsOfUse(String termsAndConditions) {
        mTermsOfUse = termsAndConditions;
    }
}
