package com.ehi.enterprise.android.models.profile.payment.pangui;


import com.google.gson.annotations.SerializedName;

public class EHIPanguiResponse {

    @SerializedName("PaymentMediaReferenceIdentifier")
    private String mPaymentMediaReferenceIdentifier;

    @SerializedName("CommonResponse")
    private EHICommonResponse mCommonResponse;

    @SerializedName("PartialPrimaryAccount")
    private EHIPartialPrimaryAccount mPartialPrimaryAccount;

    public String getPaymentMediaReferenceIdentifier() {
        return mPaymentMediaReferenceIdentifier;
    }

    public String getLocalizedMessage() {
        if (mCommonResponse != null) {
            return mCommonResponse.getLocalizedMessage();
        } else {
            return null;
        }
    }

    public boolean isDebitCard() {
        if (mPartialPrimaryAccount!=null) {
            return mPartialPrimaryAccount.isDebitCard();
        } else {
            return false;
        }
    }
}
