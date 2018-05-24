package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIBillingAccount extends EHIModel{

    @SerializedName("billing_account_type")
    private String mBillingAccountType;

    @SerializedName("billing_account_number")
    private String mBillingAccountNumber;

    public String getBillingAccountType() {
        return mBillingAccountType;
    }

    public void setBillingAccountType(String billingAccountType) {
        mBillingAccountType = billingAccountType;
    }

    public String getBillingAccountNumber() {
        return mBillingAccountNumber;
    }

    public void setBillingAccountNumber(String billingAccountNumber) {
        mBillingAccountNumber = billingAccountNumber;
    }
}
