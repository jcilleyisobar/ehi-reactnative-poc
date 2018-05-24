package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Currency;

public class EHIPaymentDetail extends EHIModel {

    @SerializedName("payment_method")
    private String mPaymentMethod;

    @SerializedName("payment_type")
    private String mPaymentType;

    @SerializedName("amount_charged")
    private String mAmountCharged;

    @SerializedName("currency_code")
    private String mCurrencyCode;

    @SerializedName("masked_credit_card_number")
    private String mMaskedCreditCardNumber;

    public String getPaymentType() {
        String paymentMethod = mPaymentMethod;
        if (mPaymentType.equals("CC")){
            paymentMethod += " " + mMaskedCreditCardNumber;
        }
        return paymentMethod;
    }

    public String getFormattedPriceView() {
        if (mCurrencyCode == null && mAmountCharged != null) {
            return mAmountCharged;
        }
        else if (mAmountCharged == null) {
            return null;
        }
        else {
            Currency currency = Currency.getInstance(mCurrencyCode);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setCurrency(currency);
            return format.format(Double.parseDouble(mAmountCharged));
        }
    }
}
