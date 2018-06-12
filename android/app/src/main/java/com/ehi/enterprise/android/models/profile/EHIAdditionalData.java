package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIAdditionalData extends EHIModel {
    @SerializedName("email_unique")
    boolean mIsEmailUnique;

    @SerializedName("auth_token")
    String mAuthToken;

    @SerializedName("encrypted_credential")
    String mEncryptedCredential;

    @SerializedName("editable")
    boolean mIsEditable;

    @SerializedName("branch_enrolled")
    boolean mIsBranchEnrolled;

    @SerializedName("credit_card_near_expiration")
    boolean mIsCreditCardNearExpiration;

    @SerializedName("credit_card_expired")
    boolean mIsCreditCardExpired;

    @SerializedName("driver_license_expired")
    boolean mIsDriverLicenseExpired;

    public boolean isEmailUnique() {
        return mIsEmailUnique;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public boolean isEditable() {
        return mIsEditable;
    }

    public boolean isCreditCardNearExpiration() {
        return mIsCreditCardNearExpiration;
    }

    public boolean isCreditCardExpired() {
        return mIsCreditCardExpired;
    }

    public boolean isDriverLicenseExpired() {
        return mIsDriverLicenseExpired;
    }

    public String getEncryptedCredential() {
        return mEncryptedCredential;
    }

    public boolean isBranchEnrolled() {
        return mIsBranchEnrolled;
    }
}
