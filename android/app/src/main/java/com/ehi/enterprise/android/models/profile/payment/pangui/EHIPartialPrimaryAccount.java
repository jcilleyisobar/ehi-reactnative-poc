package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.google.gson.annotations.SerializedName;

public class EHIPartialPrimaryAccount {

    @SerializedName("DebitCardIndicator")
    private boolean mDebitCardIndicator;

    public boolean isDebitCard() {
        return mDebitCardIndicator;
    }
}
