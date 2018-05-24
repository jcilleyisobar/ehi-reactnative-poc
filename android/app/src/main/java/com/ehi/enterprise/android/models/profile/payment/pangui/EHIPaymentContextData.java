package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.ehi.enterprise.android.utils.payment.CreditCard;
import com.google.gson.annotations.SerializedName;

public class EHIPaymentContextData {

    @SerializedName("source_system_id")
    private int mSourceSystemId;

    @SerializedName("payment_processor")
    private String mPaymentProcessor;

    @SerializedName("card_submission_url")
    private String mCardSubmissionUrl;

    public int getSourceSystemId() {
        return mSourceSystemId;
    }

    public String getCardSubmissionUrl() {
        return mCardSubmissionUrl;
    }

    @CreditCard.PaymentProcessorType
    public String getPaymentProcessor() {
        return mPaymentProcessor;
    }
}
