package com.ehi.enterprise.android.models.profile;


import com.ehi.enterprise.android.models.enroll.EHITermsAndConditions;

public interface ProfileCollection {
    EHIProfile getProfile();
    EHIBasicProfile getBasicProfile();
    EHIContactProfile getContactProfile();
    EHIAddressProfile getAddressProfile();
    EHILicenseProfile getLicenseProfile();
    EHIPaymentProfile getPaymentProfile();
    EHIPreference getPreference();
    EHIAdditionalData getAdditionalData();
    EHITermsAndConditions getTermsAndConditions();

    void setPaymentProfile(EHIPaymentProfile paymentProfile);
    void setLicenseProfile(EHILicenseProfile licenseProfile);
    void setAddressProfile(EHIAddressProfile addressProfile);
    void setContactProfile(EHIContactProfile contactProfile);
    void setBasicProfile(EHIBasicProfile basicProfile);
    void setPreference(EHIPreference preferenceProfile);
    void setProfile(EHIProfile profile);
}
