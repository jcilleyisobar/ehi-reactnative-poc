package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Currency;

public class EHIPrice extends EHIModel {
    @SerializedName("code")
    private String mCurrencyCode;
    @SerializedName("symbol")
    private String mCurrencySymbol;
    @SerializedName("amount")
    private String mAmount;

    public EHIPrice(final String currencyCode, final String currencySymbol, final double amount) {
        mCurrencyCode = currencyCode;
        mCurrencySymbol = currencySymbol;
        mAmount = Double.valueOf(amount).toString();
    }

    public CharSequence getPositiveFormattedPrice(boolean withStyling) {
        float amount = Float.valueOf(getAmmount());
        return getFormattedPrice(withStyling, String.valueOf(Math.abs(amount)));
    }

    public CharSequence getFormattedPrice(boolean withStyling) {
        return getFormattedPrice(withStyling, mAmount);
    }

    private CharSequence getFormattedPrice(boolean withStyling, String amount) {
        if (EHITextUtils.isEmpty(mCurrencyCode)) {
            return amount;
        }
        Currency currency = Currency.getInstance(mCurrencyCode);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(currency);

        if (withStyling) {
            return priceToSpannable(format.format(Double.parseDouble(amount)));
        } else {
            return format.format(Math.abs(Double.valueOf(amount)));
        }
    }

    public static CharSequence priceToSpannable(String price) {
        int decimalIndex;
        int decimalIndexPoint = -1;
        int decimalIndexComma = -1;

        if (price.contains(".")) {
            decimalIndexPoint = price.indexOf(".");
        }
        if (price.contains(",")) {
            decimalIndexComma = price.indexOf(",");
        }
        if (decimalIndexPoint > decimalIndexComma) {
            decimalIndex = decimalIndexPoint;
        } else {
            decimalIndex = decimalIndexComma;
        }
        if (decimalIndex > 0) {
            final SpannableString spannableString = new SpannableString(price);
            spannableString.setSpan(new RelativeSizeSpan(0.6f), decimalIndex, price.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            return price;
        }
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public double getDoubleAmmount() {
        if (TextUtils.isEmpty(mAmount)){
            return 0.0d;
        } else {
            return Double.parseDouble(mAmount);
        }
    }

    public String getAmmount() {
        return mAmount;
    }

    public String getFormattedCurrency() {
        if (mCurrencyCode != null) {
            if (mCurrencySymbol == null || mCurrencyCode.equals(mCurrencySymbol)) {
                return String.format("%s", mCurrencyCode);
            } else {
                return String.format("%s (%s)", mCurrencyCode, mCurrencySymbol);
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "EHIPrice{" +
                "mAmount=" + mAmount +
                ", mCurrencyCode=\"" + mCurrencyCode + "\"" +
                ", mCurrencySymbol=\"" + mCurrencySymbol + "\"" +
                "}";
    }

    public static boolean arePricesUSAndCanadaCurrency(@Nullable EHIPrice homePrice, @Nullable EHIPrice destinationPrice) {
        if (homePrice == null || destinationPrice == null) {
            return false;
        }
        final String destinationCurrency = destinationPrice.getCurrencyCode();
        final String homeCurrecy = homePrice.getCurrencyCode();
        return homeCurrecy.equals(EHIPriceSummary.US_CURRENCY) && destinationCurrency.equals(EHIPriceSummary.CA_CURRENCY)
                || homeCurrecy.equals(EHIPriceSummary.CA_CURRENCY) && destinationCurrency.equals(EHIPriceSummary.US_CURRENCY);
    }

}