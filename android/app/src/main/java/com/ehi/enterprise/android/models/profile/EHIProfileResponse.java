package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.enroll.EHITermsAndConditions;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class EHIProfileResponse extends BaseResponse implements ProfileCollection {
    @SerializedName("profile")
    protected EHIProfile mProfile;

    @SerializedName("contact_profile")
    protected EHIContactProfile mContactProfile;

    @SerializedName("address_profile")
    protected EHIAddressProfile mAddressProfile;

    @SerializedName("license_profile")
    protected EHILicenseProfile mLicenseProfile;

    @SerializedName("payment_profile")
    protected EHIPaymentProfile mPaymentProfile;

    @SerializedName("preference")
    protected EHIPreference mPreference;

    @SerializedName("additional_data")
    private EHIAdditionalData mAdditionalData;

    @SerializedName("terms_and_conditions")
    private EHITermsAndConditions mTermsAndConditions;

    public EHIProfile getProfile() {
        return mProfile;
    }

    public EHIBasicProfile getBasicProfile() {
        if (mProfile != null) {
            return mProfile.getBasicProfile();
        }
        return null;
    }

    @Override
    public EHIContactProfile getContactProfile() {
        return mContactProfile;
    }

    @Override
    public EHIAddressProfile getAddressProfile() {
        return mAddressProfile;
    }

    @Override
    public EHILicenseProfile getLicenseProfile() {
        return mLicenseProfile;
    }

    @Override
    public EHIPaymentProfile getPaymentProfile() {
        return mPaymentProfile;
    }

    @Override
    public EHIPreference getPreference() {
        return mPreference;
    }

    @Override
    public void setPaymentProfile(EHIPaymentProfile paymentProfile) {
        mPaymentProfile = paymentProfile;
    }

    @Override
    public void setLicenseProfile(EHILicenseProfile licenseProfile) {
        mLicenseProfile = licenseProfile;
    }

    @Override
    public void setAddressProfile(EHIAddressProfile addressProfile) {
        mAddressProfile = addressProfile;
    }

    @Override
    public void setContactProfile(EHIContactProfile contactProfile) {
        mContactProfile = contactProfile;
    }

    @Override
    public void setBasicProfile(EHIBasicProfile basicProfile) {
        if (mProfile != null) {
            mProfile.setBasicProfile(basicProfile);
        }
    }

    @Override
    public void setPreference(EHIPreference preferenceProfile) {
        mPreference = preferenceProfile;
    }

    @Override
    public void setProfile(EHIProfile profile) {
        mProfile = profile;
    }

    @Override
    public EHIAdditionalData getAdditionalData() {
        return mAdditionalData;
    }

    @Override
    public EHITermsAndConditions getTermsAndConditions() {
        return mTermsAndConditions;
    }

    public String getEncryptedAuthData() {
        if (mAdditionalData == null) {
            return null;
        }
        return mAdditionalData.getEncryptedCredential();
    }

    public String getAuthToken() {
        if (mAdditionalData == null) {
            return null;
        }
        return mAdditionalData.getAuthToken();
    }
}
