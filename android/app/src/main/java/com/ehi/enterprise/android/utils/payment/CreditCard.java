package com.ehi.enterprise.android.utils.payment;

import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.network.responses.profile.GetCardSubmissionKeyResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CreditCard {

    public static final String FARE_OFFICE = "FOSPS";
    public static final String PANGUI = "PANGUI";

    @Retention(RetentionPolicy.CLASS)
    @StringDef({FARE_OFFICE, PANGUI})
    public @interface PaymentProcessorType {
    }

    @CreditCard.PaymentProcessorType
    private String paymentProcessorType;

    private String id;

    private String individualId;

    private EHICreditCard card;

    private String holderName;

    private boolean saveToProfile;

    private GetCardSubmissionKeyResponse submissionKeyResponse;

    public CreditCard(String id, String individualId, EHICreditCard card, String holderName,
                      @PaymentProcessorType String paymentProcessorType, boolean saveToProfile) {
        this.id = id;
        this.individualId = individualId;
        this.card = card;
        this.holderName = holderName;
        this.saveToProfile = saveToProfile;
        this.paymentProcessorType = paymentProcessorType;

        if (paymentProcessorType.equals(PANGUI)) {
            fixCardTypeForPangui();
        }
    }

    private void fixCardTypeForPangui() {
        if (EHICreditCard.AMEX.equals(card.getCardType())) {
            card.setType(EHICreditCard.AMERICAN_EXPRESS);
        }
    }

    @CreditCard.PaymentProcessorType
    public String getPaymentProcessorType() {
        return paymentProcessorType;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public String getIndividualId() {
        return individualId;
    }

    public EHICreditCard getCard() {
        return card;
    }

    public String getHolderName() {
        return holderName;
    }

    public boolean shouldSaveToProfile() {
        return saveToProfile;
    }

    public GetCardSubmissionKeyResponse getSubmissionKeyResponse() {
        return submissionKeyResponse;
    }

    public void setSubmissionKeyResponse(GetCardSubmissionKeyResponse submissionKeyResponse) {
        this.submissionKeyResponse = submissionKeyResponse;
    }
}
