package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.ui.dashboard.debug.DebugMenuFragment;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHICountryContent extends EHIModel {

    @SerializedName("enable_country_sub_division")
    private boolean mEnableCountrySubDivision;

    @SerializedName("default_email_opt_in")
    private boolean mDefaultEmailOptIn;

    @SerializedName("license_expiry_date")
    private @EHICountry.AttributeMode String mLicenseExpiryDate;

    @SerializedName("license_issue_date")
    private @EHICountry.AttributeMode String mLicenseIssueDate;

    @SerializedName("european_address_flag")
    private boolean mEuropeanAddressFlag;

    @SerializedName("license_issuing_authority_required")
    private boolean mLicenseIssuingAuthorityRequired;

    @SerializedName("issuing_authority_name")
    private String mIssuingAuthorityName;

    public boolean hasSubdivisions() {
        return mEnableCountrySubDivision;
    }

    public boolean isDefaultEmailOptIn() {
        return mDefaultEmailOptIn;
    }

    public String getLicenseExpiryDate() {
        return mLicenseExpiryDate;
    }

    public String getLicenseIssueDate() {
        return mLicenseIssueDate;
    }

    public boolean isEuropeanAddressFlag() {
        return mEuropeanAddressFlag;
    }

    public boolean isLicenseIssuingAuthorityRequired() {
        if (DebugMenuFragment.FORCE_ISSUING_AUTHORITY) {
            return true;
        } else {
            return mLicenseIssuingAuthorityRequired;
        }
    }

    public String getIssuingAuthorityName() {
        return mIssuingAuthorityName;
    }
}
