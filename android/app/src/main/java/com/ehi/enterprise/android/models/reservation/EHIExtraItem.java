package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHIExtraItem extends EHIModel {

    public static final String ON_REQUEST = "ON_REQUEST";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({INCLUDED, MANDATORY, OPTIONAL, WAIVED, EMPTY, FOOTER})
    public @interface Status {
    }

    public static final String INCLUDED = "INCLUDED";
    public static final String MANDATORY = "MANDATORY";
    public static final String OPTIONAL = "OPTIONAL";
    public static final String WAIVED = "WAIVED";
    public static final String EMPTY = "EMPTY"; //internal status to show placeholder text
    public static final String FOOTER = "FOOTER";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({HOURLY, DAILY, WEEKLY, PERCENT, RENTAL, GALLON})
    public @interface RateType {
    }

    public static final String HOURLY = "HOURLY";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String PERCENT = "PERCENT";
    public static final String RENTAL = "RENTAL";
    public static final String GALLON = "GALLON";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({FREE_SELL, ON_REQUEST})
    public @interface Allocation {
    }

    public static final String FREE_SELL = "FREE_SELL";

    @SerializedName("code")
    private String mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("detailed_description")
    private String mDetailedDescription;

    @SerializedName("status")
    private
    @Status
    String mStatus;

    @SerializedName("max_quantity")
    private Integer mMaxQuantity;

    @SerializedName("selected_quantity")
    private Integer mSelectedQuantity;

    @SerializedName("rate_amount_view")
    private EHIPrice mRateAmountView;

    @SerializedName("rate_amount_payment")
    private EHIPrice mRateAmountPayment;

    @SerializedName("max_amount_view")
    private EHIPrice mMaxAmountView;

    @SerializedName("max_amount_payment")
    private EHIPrice mMaxAmountPayment;

    @SerializedName("total_amount_view")
    private EHIPrice mTotalAmountView;

    @SerializedName("rate_type")
    private
    @RateType
    String mRateType;

    @SerializedName("allocation")
    private
    @Allocation
    String mAllocation;

    private boolean mMerged = false;

    public static EHIExtraItem createPlaceholder(String placeholderText) {
        EHIExtraItem item = new EHIExtraItem();
        item.setStatus(EMPTY);
        item.setName(placeholderText);
        return item;
    }

    public String getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDetailedDescription() {
        return mDetailedDescription;
    }

    public
    @Status
    String getStatus() {
        return mStatus;
    }

    public void setStatus(@Status String status) {
        mStatus = status;
    }

    public Integer getMaxQuantity() {
        if (mMaxQuantity == null
                || mMaxQuantity == 0) {
            return 1;
        }
        return mMaxQuantity;
    }

    public Integer getSelectedQuantity() {
        if (mSelectedQuantity == null) {
            return 0;
        }
        return mSelectedQuantity;
    }

    public
    @Allocation
    String getAllocation() {
        return mAllocation;
    }

    public void setAllocation(@Allocation String allocation) {
        mAllocation = allocation;
    }

    public EHIPrice getRateAmountView() {
        return mRateAmountView;
    }

    public EHIPrice getRateAmountPayment() {
        return mRateAmountPayment;
    }

    public
    @RateType
    String getRateType() {
        return mRateType;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        mSelectedQuantity = selectedQuantity;
    }

    public EHIPrice getMaxAmountView() {
        return mMaxAmountView;
    }

    public EHIPrice getMaxAmountPayment() {
        return mMaxAmountPayment;
    }

    public EHIPrice getTotalAmountView() {
        return mTotalAmountView;
    }

    public void setTotalAmountView(EHIPrice value) {
        mTotalAmountView = value;
    }

}
