package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class EHICharge extends EHIModel {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PREPAY, PAYLATER, CONTRACT, PROMOTION, RETAIL})
    public @interface ChargeType {
    }

    public static final String PREPAY = "PREPAY";
    public static final String PAYLATER = "PAYLATER";
    public static final String CONTRACT = "CONTRACT";
    public static final String PROMOTION = "PROMOTION";
    public static final String RETAIL = "RETAIL";

    @SerializedName("charge_type")
    private
    @ChargeType
    String mChargeType;
    @SerializedName("total_price_view")
    private EHIPrice mPriceView;
    @SerializedName("total_price_payment")
    private EHIPrice mPricePayment;

    public boolean hasRates() {
        return !ListUtils.isEmpty(mRates);
    }

    @SerializedName("rates")
    private List<EHIRates> mRates;

    public
    @ChargeType
    String getChargeType() {
        return mChargeType;
    }

    public EHIPrice getPricePayment() {
        return mPricePayment;
    }

    public EHIPrice getPriceView() {
        return mPriceView;
    }

    @Override
    public String toString() {
        return "EHICharge{" +
                "mChargeType=\"" + mChargeType + "\"" +
                ", mPriceView=" + mPriceView +
                ", mPricePayment=" + mPricePayment +
                ", mRates=" + mRates +
                "}";
    }

    public boolean isBookingInAnotherCurrency() {
        return !getPricePayment().getCurrencyCode().equals(getPriceView().getCurrencyCode());
    }

    public String getFormattedCurrency() {
        if (mPriceView.getCurrencyCode() != null) {
            if (mPriceView.getCurrencySymbol() == null || mPriceView.getCurrencyCode().equals(mPriceView.getCurrencySymbol())) {
                return String.format("%s", mPriceView.getCurrencyCode());
            } else {
                return String.format("%s (%s)", mPriceView.getCurrencyCode(), mPriceView.getCurrencySymbol());
            }
        }
        return "";
    }

    public static EHICharge getPrePayCharge(List<EHICharge> ehiCharges) {
        return getChargeByType(ehiCharges, PREPAY);
    }

    public static EHICharge getPayLaterCharge(List<EHICharge> ehiCharges) {
        return getChargeByType(ehiCharges, PAYLATER);
    }

    private static EHICharge getChargeByType(List<EHICharge> ehiCharges, @EHICharge.ChargeType String chargeType) {
        if (ehiCharges == null || chargeType == null) {
            return null;
        }

        for (int i = 0, size = ehiCharges.size(); i < size; i++) {
            final EHICharge ehiCharge = ehiCharges.get(i);
            if (ehiCharge.getChargeType().equals(chargeType)) {
                return ehiCharge;
            }
        }

        return null;
    }

}