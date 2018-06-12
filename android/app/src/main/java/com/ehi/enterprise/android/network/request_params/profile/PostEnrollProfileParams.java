package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.google.gson.annotations.SerializedName;

public class PostEnrollProfileParams extends EHIEnrollProfile {

    @SerializedName("request_origin_channel")
    private final String mRequestOriginChannel;

    public PostEnrollProfileParams(String requestOriginChannel, EHIEnrollProfile profile) {
        super(profile);
        mRequestOriginChannel = requestOriginChannel;
    }
}
