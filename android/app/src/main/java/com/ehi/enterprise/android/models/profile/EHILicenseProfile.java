package com.ehi.enterprise.android.models.profile;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EHILicenseProfile extends EHIModel {

    private static final String TAG = "EHILicenseProfile";

    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @SerializedName("license_number")
    private String mLicenseNumber;

    @SerializedName("country_subdivision_code")
    private String mCountrySubdivisionCode;

    @SerializedName("country_subdivision_name")
    private String mCountrySubdivisionName;

    @SerializedName("issuing_authority")
    private String mIssuingAuthority;

    @SerializedName("country_code")
    private String mCountryCode;

    @SerializedName("country_name")
    private String mCountryName;

    @SerializedName("license_issue_date")
    private String mLicenseIssue;

    @SerializedName("license_expiration_date")
    private String mLicenseExpiry;

    @SerializedName("birth_date")
    private String mBirthDate;

    @SerializedName("unmasked_date_of_birth")
    private String mUnmaskedBirthDate;

    @SerializedName("do_not_rent_indicator")
    private boolean mDoNotRentIndicator;

    public EHILicenseProfile(String licenseNumber, String countrySubdivisionCode, String countryCode, String licenseIssue, String licenseExpiry, String birthDate, boolean doNotRentIndicator) {
        mLicenseNumber = licenseNumber;
        mCountrySubdivisionCode = countrySubdivisionCode;
        mCountryCode = countryCode;
        mLicenseIssue = licenseIssue;
        mLicenseExpiry = licenseExpiry;
        mBirthDate = birthDate;
        mDoNotRentIndicator = doNotRentIndicator;
    }

    public EHILicenseProfile() {
    }

    public String getLicenseNumber() {
        return mLicenseNumber;
    }

    public String getCountrySubdivisionCode() {
        return mCountrySubdivisionCode;
    }

    public String getIssuingAuthorityOrSubdivisionCodeForCountry(EHICountry country){
        if (country.isSpecialIssuingAuthorityRequired()){
            return mIssuingAuthority;
        } else {
            return mCountrySubdivisionCode;
        }
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getLicenseIssue() {
        if (EHITextUtils.isMaskedField(mLicenseIssue)) {
            return DateUtilManager.getInstance().formatMaskedDate(mLicenseIssue);
        }
        return mLicenseIssue;
    }

    public String getLicenseExpiry() {
        if (EHITextUtils.isMaskedField(mLicenseExpiry)) {
            return DateUtilManager.getInstance().formatMaskedDate(mLicenseExpiry);
        }
        return mLicenseExpiry;
    }

    public void setLicenseNumber(String licenseNumber) {
        mLicenseNumber = licenseNumber;
    }

    public void setCountrySubdivisionCode(String countrySubdivisionCode) {
        mCountrySubdivisionCode = countrySubdivisionCode;
    }

    public String getCountrySubdivisionName() {
        return mCountrySubdivisionName;
    }

    public void setCountrySubdivisionName(String countrySubdivisionName) {
        mCountrySubdivisionName = countrySubdivisionName;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setLicenseIssue(String licenseIssue) {
        mLicenseIssue = licenseIssue;
    }

    public void setLicenseExpiry(String licenseExpiry) {
        mLicenseExpiry = licenseExpiry;
    }

    public void setBirthDate(Date date) {
        if (date != null) {
            mBirthDate = sDateFormatter.format(date);
        } else {
            mBirthDate = null;
        }
    }

    public String getBirth() {
        return mBirthDate;
    }

    @Nullable
    public Date getBirthDate() {
        if (mBirthDate != null) {
            try {
                return sDateFormatter.parse(mBirthDate);
            } catch (ParseException e) {
                DLog.w(TAG, e);
                return null;
            }
        }
        return null;
    }

    public void setDoNotRentIndicator(boolean doNotRentIndicator) {
        mDoNotRentIndicator = doNotRentIndicator;
    }

    @Nullable
    public Date getExpiryDate() {
        if (mLicenseExpiry != null) {
            try {
                return sDateFormatter.parse(mLicenseExpiry);
            } catch (ParseException e) {
                DLog.w(TAG, e);
                return null;
            }
        }
        return null;
    }

    @Nullable
    public Date getIssueDate() {
        if (mLicenseIssue != null) {
            try {
                return sDateFormatter.parse(mLicenseIssue);
            } catch (ParseException e) {
                DLog.w(TAG, e);
                return null;
            }
        }
        return null;
    }

    public void setExpiryDate(Date date) {
        if (date != null) {
            mLicenseExpiry = sDateFormatter.format(date);
        } else {
            mLicenseExpiry = null;
        }
    }

    public boolean isDoNotRentIndicator() {
        return mDoNotRentIndicator;
    }

    public void setIssueDate(Date date) {
        if (date != null) {
            mLicenseIssue = sDateFormatter.format(date);
        } else {
            mLicenseIssue = null;
        }
    }

    public String getUnmaskedBirthDate() {
        return mUnmaskedBirthDate;
    }

    public EHIRegion getCountrySubdivisionRegion() {
        return new EHIRegion(mCountrySubdivisionName, mCountrySubdivisionCode);
    }

    public EHIRegion getCountryIssuingAuthorityRegion() {
        return new EHIRegion(mIssuingAuthority, mIssuingAuthority);
    }

    public String getIssuingAuthority() {
        return mIssuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        mIssuingAuthority = issuingAuthority;
    }
}
