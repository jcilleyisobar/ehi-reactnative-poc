package com.ehi.enterprise.android.models.profile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class EHICountry extends EHIModel implements Comparable<EHICountry> {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MODE_MANDATORY,
            MODE_UNSUPPORTED,
            MODE_OPTIONAL
    })
    public @interface AttributeMode {

    }

    public static final String MODE_MANDATORY = "MANDATORY";
    public static final String MODE_UNSUPPORTED = "UNSUPPORTED";
    public static final String MODE_OPTIONAL = "OPTIONAL";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({COUNTRY_GERMANY, COUNTRY_FRANCE, COUNTRY_US, COUNTRY_CANADA})
    public @interface CountryCode {


    }
    public static final String COUNTRY_FRANCE = "FR";

    public static final String COUNTRY_GERMANY = "DE";
    public static final String COUNTRY_US = "US";
    public static final String COUNTRY_CANADA = "CA";
    @SerializedName("country_code")
    private String mCountryCode = "";

    @SerializedName("country_name")
    private String mCountryName = "";

    @SerializedName("country_content")
    private EHICountryContent mCountryContent;

    @SerializedName("key_facts_dispute_email")
    private String mKeyFactsDisputeEmail;

    @SerializedName("key_facts_dispute_phone")
    private String mKeyFactsDisputePhone;

    @SerializedName("prepay_enabled")
    private boolean mPrepayEnabled;

    @SerializedName("contracts")
    private List<EHIPromotionContract> mContractsList = new ArrayList<>();

    public EHICountry() {
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public boolean hasSubdivisions() {
        return mCountryContent != null && mCountryContent.hasSubdivisions();
    }

    public boolean isDefaultEmailOptIn() {
        return mCountryContent != null && mCountryContent.isDefaultEmailOptIn();
    }

    public String getLicenseExpiryDate() {
        if (mCountryContent != null) {
            return mCountryContent.getLicenseExpiryDate();
        }
        return "";
    }

    public String getKeyFactsDisputeEmail() {
        return mKeyFactsDisputeEmail;
    }

    public String getKeyFactsDisputePhone() {
        return mKeyFactsDisputePhone;
    }

    public boolean shouldShowExpiryDateOnProfile() {
        final String licenseExpiryDate = getLicenseExpiryDate();
        return !EHITextUtils.isEmpty(licenseExpiryDate)
                && (licenseExpiryDate.equalsIgnoreCase(MODE_OPTIONAL)
                || licenseExpiryDate.equalsIgnoreCase(MODE_MANDATORY));
    }

    public boolean shouldShowExpiryDateOnEditScreen() {
        final String licenseExpiryDate = getLicenseExpiryDate();
        return !EHITextUtils.isEmpty(licenseExpiryDate)
                && (licenseExpiryDate.equalsIgnoreCase(MODE_OPTIONAL)
                || licenseExpiryDate.equalsIgnoreCase(MODE_MANDATORY));
    }

    public boolean isLicenseExpiryDateRequired() {
        return MODE_MANDATORY.equalsIgnoreCase(getLicenseExpiryDate());
    }

    public boolean isLicenseExpiryDateOptional() {
        return MODE_OPTIONAL.equalsIgnoreCase(getLicenseExpiryDate());
    }

    public String getLicenseIssueDate() {
        if (mCountryContent.getLicenseIssueDate() != null) {
            return mCountryContent.getLicenseIssueDate();
        }
        return "";
    }

    public boolean shouldShowIssueDate() {
        final String licenseIssueDate = getLicenseIssueDate();
        return !EHITextUtils.isEmpty(licenseIssueDate)
                && (licenseIssueDate.equalsIgnoreCase(MODE_OPTIONAL)
                || licenseIssueDate.equalsIgnoreCase(MODE_MANDATORY));
    }

    public boolean isLicenseIssueDateRequired() {
        return MODE_MANDATORY.equalsIgnoreCase(getLicenseIssueDate());
    }

    public boolean isLicenseIssueDateOptional() {
        return MODE_OPTIONAL.equalsIgnoreCase(getLicenseIssueDate());
    }

    public boolean isEuropeanAddressFlag() {
        return mCountryContent != null && mCountryContent.isEuropeanAddressFlag();
    }

    public EHIPromotionContract getWeekendSpecialPromotion() {
        if (mCountryContent == null || ListUtils.isEmpty(mContractsList)) {
            return null;
        }

        for (EHIPromotionContract contractModel : mContractsList) {
            if (contractModel.isWeekendSpecial()) {
                return contractModel;
            }
        }

        return null;
    }

    public boolean isCountry(@CountryCode String countryCode) {
        return mCountryCode.equals(countryCode);
    }

    public boolean isLicenseIssuingAuthorityRequired() {
        return mCountryContent != null && mCountryContent.isLicenseIssuingAuthorityRequired();
    }

    public boolean isSpecialIssuingAuthorityRequired() {
        return mCountryContent != null
                && mCountryContent.isLicenseIssuingAuthorityRequired()
                && getIssuingAuthorityName() != null;
    }

    @Nullable
    public String getIssuingAuthorityName() {
        return mCountryContent != null ? mCountryContent.getIssuingAuthorityName() : null;
    }

    @Override
    public int compareTo(@NonNull EHICountry country) {
        if (mCountryName != null && country.getCountryName() != null) {
            return mCountryName.compareTo(country.getCountryName());
        } else {
            return 0;
        }
    }
}
