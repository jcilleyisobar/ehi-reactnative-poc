package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.utils.EHIPhoneNumberUtils;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.gson.annotations.SerializedName;

public class EHIDriverInfo extends EHIModel {

    public static final String TAG = "EHIDriverInfo";

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("email_address")
    private String mEmailAddress;

    @SerializedName("mask_email_address")
    private String mMaskEmailAddress;

    @SerializedName("phone")
    private EHIPhone mPhone;

    @SerializedName("request_email_promotions")
    private Boolean mRequestEmailPromotions;

    @SerializedName("loyalty_program_type")
    private String mLoyaltyProgramType;

    @SerializedName("source_code")
    private String mSourceCode;

    public EHIDriverInfo() {
    }

    public void setSourceCode(String value) {
        mSourceCode = value;
    }

    enum LoyaltyType {
        EPLUS("EPLUS"),
        EMERALDCLUB("EMERALDCLUB");

        private String mValue;

        LoyaltyType(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    public EHIDriverInfo(String emailAddress,
                         String maskEmailAddress,
                         String firstName,
                         String lastName,
                         String phoneNumber,
                         Boolean requestEmailPromotions) {
        mEmailAddress = emailAddress;
        mMaskEmailAddress = maskEmailAddress;
        mFirstName = firstName;
        mLastName = lastName.trim();    //Note: this is because of an ORCH bug, fixing it client side for now
        mPhone = new EHIPhone(phoneNumber, EHIPhone.PhoneType.HOME.getValue());
        mRequestEmailPromotions = requestEmailPromotions;
    }

    public EHIDriverInfo(String emailAddress,
                         String maskEmailAddress,
                         String firstName,
                         String lastName,
                         EHIPhone phoneNumber,
                         Boolean requestEmailPromotions) {
        mEmailAddress = emailAddress;
        mMaskEmailAddress = maskEmailAddress;
        mFirstName = firstName;
        mLastName = lastName.trim();    //Note: this is because of an ORCH bug, fixing it client side for now
        mPhone = phoneNumber;
        mRequestEmailPromotions = requestEmailPromotions;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public String getMaskEmailAddress() {
        return mMaskEmailAddress;
    }

    public EHIPhone getPhone() {
        return mPhone;
    }

    public String getFormattedPhoneNumber(boolean withFormatting) {
        if (getPhone() != null
                && getPhone().getPhoneNumber() != null) {
            return EHIPhoneNumberUtils.formatNumberForMobileDialing(getPhone().getPhoneNumber(),
                    LocalDataManager.getInstance().getPreferredCountryCode(),
                    withFormatting);
        } else {
            return "";
        }

    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setMaskEmailAddress(String emailAddress) {
        mMaskEmailAddress = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (mPhone == null) {
            mPhone = new EHIPhone(phoneNumber, EHIPhone.PhoneType.HOME.getValue());
        } else {
            mPhone.setPhoneNumber(phoneNumber);
        }
    }

    public void setPhone(EHIPhone phone) {
        mPhone = phone;
    }

    public boolean hasRequestedEmailPromotions() {
        return mRequestEmailPromotions == null ? false : mRequestEmailPromotions;
    }

    public void setRequestEmailPromotions(boolean isSelectedByDefault, boolean selected) {
        // logic necessary to keep 3 states:
        // opt-in: option is checked
        // opt-out: option was checked before and user unchecks
        // opt-null: unchecked and user didn't interact with the checkbox
        Boolean opt = null;
        if (isSelectedByDefault) {
            opt = selected;
        } else if (selected) {
            opt = true;
        }
        mRequestEmailPromotions = opt;
    }

    public boolean isEmeraldClubAccount() {
        return mLoyaltyProgramType.equals(LoyaltyType.EMERALDCLUB.getValue());
    }

    public void clearMaskedFields() {
        setMaskEmailAddress(null);
        if (mPhone != null) {
            mPhone.setMaskPhoneNumber(null);
        }
    }

}