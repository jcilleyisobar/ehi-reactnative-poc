package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.payment.pangui.EHIPanguiResponse;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class PostPanguiAddCreditCardResponse extends BaseResponse {

    @SerializedName("ProcessPaymentMediaIdentificationRS")
    private EHIPanguiResponse mProcessPaymentMediaIdentificationRS;

    public String getPaymentMediaReferenceIdentifier() {
        return mProcessPaymentMediaIdentificationRS.getPaymentMediaReferenceIdentifier();
    }

    public String getErrorMessage() {
        if (mProcessPaymentMediaIdentificationRS != null) {
            return mProcessPaymentMediaIdentificationRS.getLocalizedMessage();
        }
        return null;
    }

    public boolean isDebitCard() {
        if (mProcessPaymentMediaIdentificationRS != null) {
            return mProcessPaymentMediaIdentificationRS.isDebitCard();
        } else {
            return false;
        }
    }
}
