package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.profile.EHIAdditionalInfo;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHIPreference;
import com.google.gson.annotations.SerializedName;

import static com.ehi.enterprise.android.models.profile.EHIAdditionalInfo.MODE_UPDATE;

public class PutProfileParams {
    @SerializedName("loyalty_number")
    private final String mLoyaltyNumber;

    @SerializedName("contact")
    private EHIContactProfile mContactProfile;

    @SerializedName("address")
    private EHIAddressProfile mAddress;

    @SerializedName("preference")
    private EHIPreference mPreference;

    @SerializedName("drivers_license")
    private EHILicenseProfile mLicenseProfile;

    @SerializedName("payment")
    private PaymentRequestParams mPaymentRequestParam;

    @SerializedName("additional_info")
    private EHIAdditionalInfo mAdditionalInfo;

    private PutProfileParams(EHIContactProfile contactProfile, EHIAddressProfile address, EHIPreference preference, EHILicenseProfile licenseProfile, PaymentRequestParams paymentRequestParam, String loyaltyNumber) {
        mContactProfile = contactProfile;
        mAddress = address;
        mPreference = preference;
        mLicenseProfile = licenseProfile;
        mPaymentRequestParam = paymentRequestParam;
        mLoyaltyNumber = loyaltyNumber;
        mAdditionalInfo = new EHIAdditionalInfo(MODE_UPDATE);
    }

    public static class Builder {
        private EHIContactProfile mContactProfile;
        private EHIAddressProfile mAddress;
        private EHIPreference mPreference;
        private EHILicenseProfile mLicenseProfile;
        private PaymentRequestParams mPaymentRequestParam;
        private String mLoyaltyNumber;

        public Builder setContactProfile(EHIContactProfile contactProfile) {
            mContactProfile = contactProfile;
            return this;
        }

        public Builder setAddress(EHIAddressProfile address) {
            mAddress = address;
            return this;
        }

        public Builder setPreference(EHIPreference preference) {
            mPreference = preference;
            return this;
        }

        public Builder setLicenseProfile(EHILicenseProfile licenseProfile) {
            mLicenseProfile = licenseProfile;
            return this;
        }

        public Builder setPaymentRequestParam(PaymentRequestParams paymentRequestParam) {
            mPaymentRequestParam = paymentRequestParam;
            return this;
        }

        public Builder setLoyaltyNumber(String loyaltyNumber) {
            mLoyaltyNumber = loyaltyNumber;
            return this;
        }

        public PutProfileParams build() {
            return new PutProfileParams(mContactProfile, mAddress, mPreference, mLicenseProfile, mPaymentRequestParam, mLoyaltyNumber);
        }
    }
}
