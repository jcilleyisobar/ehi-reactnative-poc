package com.ehi.enterprise.android.models.profile;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

public class EHICreditCard extends EHIModel{

    @SerializedName("cvc")
    private String mCVV;

    @SerializedName("expirationMonth")
    private String mExpirationMonth;

    @SerializedName("expirationYear")
    private String mExpirationYear;

    @SerializedName("number")
    private String mCreditCardNumber;

    @CreditCard
    @SerializedName("type")
    private String mCardType;

    public EHICreditCard(String CVV, String expirationMonth, String expirationYear, String creditCardNumber, String cardType) {
        mCVV = CVV;
        mExpirationMonth = expirationMonth;
        mExpirationYear = expirationYear;
        mCreditCardNumber = creditCardNumber;
        mCardType = cardType;
    }

    @Retention(RetentionPolicy.CLASS)
    @StringDef({VISA, AMEX, AMERICAN_EXPRESS, DISCOVER, MASTER_CARD})
    public @interface CreditCard{}

    public static final String VISA = "VISA";
    public static final String MASTER_CARD = "MASTERCARD";
    public static final String AMEX = "AMEX";
    public static final String AMERICAN_EXPRESS = "AMERICAN_EXPRESS";
    public static final String DISCOVER = "DISCOVER";

    public String getCVV() {
        return mCVV;
    }

    public String getExpirationMonth() {
        return mExpirationMonth;
    }

    public String getExpirationYear() {
        return mExpirationYear;
    }

    public String getCreditCardNumber() {
        return mCreditCardNumber;
    }

    public String getCardType() {
        return mCardType;
    }

    public void setType(String type) {
        mCardType = type;
    }

    public enum CardType {

        UNKNOWN(null, null),
        VISA("^4[0-9]{12}(?:[0-9]{3})?$", EHICreditCard.VISA),
        MASTERCARD("^5[1-5][0-9]{14}$", EHICreditCard.MASTER_CARD),
        AMERICAN_EXPRESS("^3[47][0-9]{13}$", EHICreditCard.AMEX),
        DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$", EHICreditCard.DISCOVER);

        public final String cardName;
        private final Pattern mPattern;

        CardType(String pattern, String cardName) {
            this.mPattern = pattern == null ? null : Pattern.compile(pattern);
            this.cardName = cardName;
        }

        public static CardType detect(String cardNumber) {

            for (CardType cardType : CardType.values()) {
                if (null == cardType.mPattern) continue;
                if (cardType.mPattern.matcher(cardNumber).matches()) return cardType;
            }

            return UNKNOWN;
        }

    }
}
