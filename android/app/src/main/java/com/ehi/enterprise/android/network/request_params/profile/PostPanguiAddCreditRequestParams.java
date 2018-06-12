package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.google.gson.annotations.SerializedName;

public class PostPanguiAddCreditRequestParams {

    @SerializedName("ProcessPaymentMediaIdentificationRQ")
    private PostPanguiAddCreditRequestBodyParams mProcessPaymentMediaIdentificationRQ;

    public PostPanguiAddCreditRequestParams(String token, int sourceSystemCode, String callingApplicationName, EHICreditCard creditCard, String holderName) {
        mProcessPaymentMediaIdentificationRQ = new PostPanguiAddCreditRequestBodyParams(token, sourceSystemCode, callingApplicationName, creditCard, holderName);
    }
}
