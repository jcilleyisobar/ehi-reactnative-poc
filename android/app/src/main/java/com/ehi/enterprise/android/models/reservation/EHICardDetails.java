package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.google.gson.annotations.SerializedName;

public class EHICardDetails extends EHIModel {

    @SerializedName("number")
    private String number;
    @SerializedName("expiration_month")
    private String expirationMonth;
    @SerializedName("expiration_year")
    private String expirationYear;
    @SerializedName("card_type")
    private String cardType;

    public EHICardDetails(String number, String expirationMonth, String expirationYear, String cardType) {
        this.number = number;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cardType = cardType;
    }

    public String getNumber() {
        if (!EHITextUtils.isEmpty(number)) {
            return String.format("************%s", number.substring(number.length() - Math.min(number.length(), 4), number.length()));
        }
        return number;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public String getCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        return "EHICardDetails{" +
                "number='" + number + '\'' +
                ", expirationMonth='" + expirationMonth + '\'' +
                ", expirationYear='" + expirationYear + '\'' +
                ", cardType='" + cardType + '\'' +
                '}';
    }

}