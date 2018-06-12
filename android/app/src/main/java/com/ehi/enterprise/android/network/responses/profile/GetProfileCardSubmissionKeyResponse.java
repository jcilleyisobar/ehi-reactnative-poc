package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.payment.pangui.EHIPaymentContextData;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetProfileCardSubmissionKeyResponse extends BaseResponse {

    @SerializedName("card_submission_key")
    private String mCardSubmissionKey;

    @SerializedName("payment_context_data")
    private EHIPaymentContextData mPaymentContextData;

    @SerializedName("CallingInterfaceVersion")
    private String mCallingInterfaceVersion;

    @SerializedName("CallingApplicationName")
    private String mCallingApplicationName;

    public String getCardSubmissionKey() {
        return mCardSubmissionKey;
    }

    public EHIPaymentContextData getPaymentContextData() {
        return mPaymentContextData;
    }

    public String getCallingInterfaceVersion() {
        return mCallingInterfaceVersion;
    }

    public String getCallingApplicationName() {
        return mCallingApplicationName;
    }
}
