package com.ehi.enterprise.android.models.enroll;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHITermsAndConditions extends EHIModel {

    public static final String TAG = "EHITermsAndConditions";

    @SerializedName("accept_decline")
    private boolean mAcceptDecline;

    @SerializedName("accept_decline_version")
    private String mAcceptDeclineVersion;

    @SerializedName("accept_decline_date")
    private String mAcceptDeclineDate;

    public boolean isAcceptDecline() {
        return mAcceptDecline;
    }

    public void setAcceptDecline(boolean acceptDecline) {
        mAcceptDecline = acceptDecline;
    }

    public String getAcceptDeclineVersion() {
        return mAcceptDeclineVersion;
    }

    public String getAcceptDeclineDate() {
        return mAcceptDeclineDate;
    }
}
