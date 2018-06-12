package com.ehi.enterprise.android.models.reservation;

import android.content.res.Resources;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHIPaymentLineItem extends EHIModel {

    private static final String DISCOUNT = "Discount";
    private static final String ACCOUNT_ADJUSTMENT = "Account Adjustment";

    public static final String STATUS_INCLUDED = "INCLUDED";
    public static final String STATUS_WAIVED = "WAIVED";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VEHICLE_RATE, UNKNOWN, FEE, COVERAGE, EQUIPMENT, SAVINGS, EPLUS_REDEMPTION_SAVINGS})
    public @interface PaymentLineItemCategory {
    }

    public static final String UNKNOWN = "UNKNOWN";
    public static final String VEHICLE_RATE = "VEHICLE_RATE";
    public static final String FEE = "FEE";
    public static final String COVERAGE = "COVERAGE";
    public static final String EQUIPMENT = "EQUIPMENT";
    public static final String SAVINGS = "SAVINGS";
    public static final String EPLUS_REDEMPTION_SAVINGS = "EPLUS_REDEMPTION_SAVINGS";
    public static final String CHARGED = "CHARGED";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({HOURLY, DAILY, DAY, WEEKLY, PERCENT, MONTHLY, MILES, RENTAL})
    public @interface PaymentLineItemRateType {
    }

    public static final String MONTHLY = "MONTHLY";
    public static final String WEEKLY = "WEEKLY";
    public static final String DAILY = "DAILY";
    public static final String HOURLY = "HOURLY";
    public static final String RENTAL = "RENTAL";
    public static final String DAY = "DAY";

    public static final String PERCENT = "PERCENT";
    public static final String MILES = "MILES";

    @SerializedName("category")
    private
    @PaymentLineItemCategory
    String mCategory;
    @SerializedName("total_amount_payment")
    private EHIPrice mTotalAmountPayment;
    @SerializedName("total_amount_view")
    private EHIPrice mTotalAmountView;
    @SerializedName("rate_type")
    private
    @PaymentLineItemRateType
    String mRateType;
    @SerializedName("rate_amount_payment")
    private EHIPrice mRateAmountPayment;
    @SerializedName("rate_amount_view")
    private EHIPrice mRateAmountView;
    @SerializedName("rate_quantity")
    private Double mRateQuantity;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("code")
    private String mCode; //code for extras payment item
    @SerializedName("status")
    private String mStatus;

    public
    @PaymentLineItemCategory
    String getCategory() {
        return mCategory;
    }

    public EHIPrice getTotalAmountPayment() {
        return mTotalAmountPayment;
    }

    public EHIPrice getTotalAmountView() {
        return mTotalAmountView;
    }

    public
    @PaymentLineItemRateType
    String getRateType() {
        return mRateType;
    }

    public EHIPrice getRateAmountView() {
        return mRateAmountView;
    }

    public Double getRateQuantity() {
        return mRateQuantity;
    }

    public String getDescription(Resources resources) {
        if (mDescription.trim().equalsIgnoreCase(DISCOUNT)) {
            return resources.getString(R.string.class_line_item_discount);
        } else if (mDescription.trim().equalsIgnoreCase(ACCOUNT_ADJUSTMENT)) {
            return resources.getString(R.string.class_line_item_account_adjustment);
        } else {
            return mDescription;
        }
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setRateAmountPayment(EHIPrice rateAmountPayment) {
        mRateAmountPayment = rateAmountPayment;
    }

    public String getCode() {
        return mCode;
    }

    public CharSequence getRentalAmountText(Resources resources) {
        TokenizedString.Formatter formatter = new TokenizedString.Formatter<EHIStringToken>(resources)
                .formatString(R.string.reservation_line_item_rental_duration_title)
                .addTokenAndValue(EHIStringToken.DURATION, String.valueOf(getRateQuantity() == null ? "" : getRateQuantity().intValue()));
        if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.HOURLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_hourly_unit_plural)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.DAILY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_daily_unit_plural)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.WEEKLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_weekly_unit_plural)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.MONTHLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_monthly_unit_plural)).format();
        } else {
            return "";
        }

        return formatter.format();
    }

    public CharSequence getRentalRateText(Resources resources) {
        if (getRateAmountView() == null
                || TextUtils.isEmpty(getRateAmountView().getCurrencyCode())
                || TextUtils.isEmpty(getRateAmountView().getCurrencySymbol())) {

            return "";
        }

        TokenizedString.Formatter formatter = new TokenizedString.Formatter<EHIStringToken>(resources)
                .formatString(R.string.reservation_line_item_rental_rate_title)
                .addTokenAndValue(EHIStringToken.PRICE,
                        String.valueOf(getRateAmountView().getFormattedPrice(false)));
        if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.HOURLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_hourly_unit)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.DAILY) ||
                getRateType().equalsIgnoreCase(EHIPaymentLineItem.DAY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_daily_unit)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.WEEKLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_weekly_unit)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.MONTHLY)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_monthly_unit)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.MILES)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_mile_unit)).format();
        } else if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.RENTAL)) {
            formatter.addTokenAndValue(EHIStringToken.UNIT, resources.getString(R.string.reservation_rate_rental_unit)).format();
        } else {
            if (getRateType().equalsIgnoreCase(EHIPaymentLineItem.PERCENT)) {
                return String.format("%.2f %%", getRateAmountView().getDoubleAmmount());

            } else {
                return "";
            }
        }
        return formatter.format();
    }

    public String getStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        return "EHIPaymentLineItems{" +
                "mCategory='" + mCategory + '\'' +
                ", mTotalAmountPayment='" + mTotalAmountPayment + '\'' +
                ", mTotalAmountView='" + mTotalAmountView + '\'' +
                ", mRateType='" + mRateType + '\'' +
                ", mRateAmountView='" + mRateAmountView + '\'' +
                ", mRateQuantity=" + mRateQuantity +
                ", mDescription='" + mDescription + '\'' +
                '}';
    }
}