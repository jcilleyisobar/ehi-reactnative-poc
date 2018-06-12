package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.google.gson.annotations.SerializedName;

public class EHIProfile extends EHIModel {

    @SerializedName("basic_profile")
    private EHIBasicProfile mBasicProfile;

    @SerializedName("individual_id")
    private String mIndividualId;

    @SerializedName("default_contract")
    private boolean mDefaultContract;

    @SerializedName("customer_details")
    private EHIContract mCorporateAccount;

    public EHIBasicProfile getBasicProfile() {
        return mBasicProfile;
    }

    public void setBasicProfile(EHIBasicProfile basicProfile) {
        mBasicProfile = basicProfile;
    }

    public String getIndividualId() {
        return mIndividualId;
    }

    public boolean isDefaultContract() {
        return mDefaultContract;
    }

    public EHIContract getCorporateAccount() {
        return mCorporateAccount;
    }

    public void setCorporateAccount(EHIContract corporateAccount) {
        mCorporateAccount = corporateAccount;
    }

    public boolean hasCorporateAccount() {
        return getCorporateAccount() != null;
    }
}
