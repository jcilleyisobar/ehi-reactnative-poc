package com.ehi.enterprise.android.models.profile;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EHIPaymentMethod extends EHIModel implements Comparable<EHIPaymentMethod> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat CC_DATE_FORMAT = new SimpleDateFormat("MM/yy");
    private static final String TAG = "EHIPaymentMethod";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_CREDIT_CARD, TYPE_BILLING_CODE})
    public @interface PaymentType {
    }

    public static final String TYPE_CREDIT_CARD = "CREDIT_CARD";
    public static final String TYPE_BILLING_CODE = "BILLING_CODE";

    private static final int BILLING_LAST_DIGITS = 2;

    @SerializedName("payment_type")
    private String mPaymentType;

    @SerializedName("card_type")
    private String mCardType;

    @SerializedName("preferred")
    private boolean mPreferred;

    @SerializedName("expiration_date")
    private String mExpirationDate;

    @SerializedName("payment_service_context_reference_identifier")
    private String mPaymentServiceContextReferenceIdentifier;

    @SerializedName("payment_reference_id")
    private String mPaymentReferenceId;

    @SerializedName("alias")
    private String mAlias;

    @SerializedName("first_six")
    private String mFirstSix;

    @SerializedName("last_four")
    private String mLastFour;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("credit_card_expired")
    private boolean mIsCreditCardExpired;

    @SerializedName("credit_card_near_expiration")
    private boolean mIsCreditCardNearExpiration;

    @SerializedName("billing_number")
    private String mBillingNumber;

    @SerializedName("mask_billing_number")
    private String mMaskBillingNumber;

    public static final String VISA = "VISA";
    public static final String MASTERCARD = "MASTERCARD";
    public static final String AMEX = "AMERICAN_EXPRESS";

    public EHIPaymentMethod(String paymentReferenceId, EHICreditCard card) {
        mPaymentReferenceId = paymentReferenceId;
        mPaymentType = TYPE_CREDIT_CARD;
        mCardType = card.getCardType();
        mExpirationDate = String.format("%s-%s", card.getExpirationYear(), card.getExpirationMonth());
        mFirstSix = EHITextUtils.getFirstN(card.getCreditCardNumber(), 6);
        mLastFour = EHITextUtils.getLastN(card.getCreditCardNumber(), 4);
    }

    public EHIPaymentMethod() {
    }

    @Override
    public int compareTo(@NonNull EHIPaymentMethod other) {
        if (this.isPreferred()) {
            return -1;
        }

        if (other.isPreferred()) {
            return 1;
        }

        final String first = getSortableName(this);
        final String second = getSortableName(other);

        return first.compareToIgnoreCase(second);
    }

    private String getSortableName(EHIPaymentMethod method) {
        return !EHITextUtils.isEmpty(method.getAlias()) ? method.getAlias() : method.getCardType();
    }

    public boolean isCreditCard() {
        return TYPE_CREDIT_CARD.equalsIgnoreCase(getPaymentType());
    }

    @PaymentType
    public String getPaymentType() {
        return mPaymentType;
    }

    public String getMaskedCreditCardNumber() {
        return (getAlias() + " " + getMaskedNumber()).trim();
    }

    public String getMaskedNumber() {
        return getPaymentType().equals(TYPE_CREDIT_CARD) ?
             String.format(Locale.getDefault(), "************%s", mLastFour) :
             mMaskBillingNumber;
    }

    public String getAlias() {
        if (mAlias == null || mAlias.length() == 0) {
            return "";
        } else {
            return mAlias;
        }
    }

    public String getAliasOrType(Resources resources) {
        return (mAlias == null || mAlias.length() == 0)
                ? getReadableType(mCardType, resources)
                : mAlias;
    }

    private String getReadableType(String cardType, Resources resources) {
        if (cardType == null) {
            return "";
        }

        switch (cardType) {
            case MASTERCARD:
                return resources.getString(R.string.user_payment_card_type_mastercard);
            case AMEX:
                return resources.getString(R.string.user_payment_card_type_amex);
            case VISA:
                return resources.getString(R.string.user_payment_card_type_visa);
            default:
                return "";
        }
    }

    public boolean isPreferred() {
        return mPreferred;
    }

    public String getExpirationDate() {
        return mExpirationDate;
    }

    public String getPaymentServiceContextReferenceIdentifier() {
        return mPaymentServiceContextReferenceIdentifier;
    }

    public String getPaymentReferenceId() {
        return mPaymentReferenceId;
    }

    public void setPreferred(boolean preferred) {
        this.mPreferred = preferred;
    }

    public void setExpirationDate(String expirationDate) {
        this.mExpirationDate = expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        mExpirationDate = DATE_FORMAT.format(expirationDate);
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public void setPaymentServiceContextReferenceIdentifier(String paymentServiceContextReferenceIdentifier) {
        mPaymentServiceContextReferenceIdentifier = paymentServiceContextReferenceIdentifier;
    }

    public void setFirstSix(String firstSix) {
        mFirstSix = firstSix;
    }

    public void setLastFour(String lastFour) {
        mLastFour = lastFour;
    }

    public boolean isExpired() {
        final Date expirationDate = getExpirationDateAsDate();
        if (expirationDate == null) {
            return true;
        }

        final Calendar expirationCalendar = Calendar.getInstance();
        expirationCalendar.setTimeInMillis(expirationDate.getTime());

        final Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(new Date().getTime());

        if (todayCalendar.get(Calendar.YEAR) != expirationCalendar.get(Calendar.YEAR)) {
            return todayCalendar.get(Calendar.YEAR) > expirationCalendar.get(Calendar.YEAR);
        }

        return todayCalendar.get(Calendar.MONTH) > expirationCalendar.get(Calendar.MONTH);
    }

    public String getCardType() {
        return mCardType;
    }

    @Nullable
    public Date getExpirationDateAsDate() {
        try {
            return DATE_FORMAT.parse(mExpirationDate);
        } catch (ParseException | NullPointerException e) {
            DLog.e(TAG, e);
            return null;
        }
    }

    public String getExpirationDateAsLocalizedString() {
        Date expirationDate = getExpirationDateAsDate();
        if (expirationDate == null) {
            return getExpirationDate();
        }
        return CC_DATE_FORMAT.format(expirationDate);
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isCreditCardExpired() {
        return mIsCreditCardExpired;
    }

    public boolean isCreditCardNearExpiration() {
        return mIsCreditCardNearExpiration;
    }
}
