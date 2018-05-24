package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.google.gson.annotations.SerializedName;

public class PostSearchProfileResponse extends EHIProfileResponse {

    @SerializedName("editable")
    private boolean mEditable;

    @SerializedName("branch_enrolled")
    private boolean mBranchEnrolled;

    public boolean isBranchEnrolled() {
        return mBranchEnrolled;
    }
}