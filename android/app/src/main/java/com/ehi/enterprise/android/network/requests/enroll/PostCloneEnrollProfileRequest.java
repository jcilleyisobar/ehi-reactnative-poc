package com.ehi.enterprise.android.network.requests.enroll;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.profile.PostEnrollProfileParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.enroll.PostEnrollProfileResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostCloneEnrollProfileRequest extends AbstractRequestProvider<PostEnrollProfileResponse> {

    private final PostEnrollProfileParams mEhiEnrollProfile;

    public PostCloneEnrollProfileRequest(EHIEnrollProfile ehiEnrollProfile) {
        mEhiEnrollProfile = new PostEnrollProfileParams("NONEXPEDITED", ehiEnrollProfile);
    }

    @Override
    public Object getRequestBody() {
        return mEhiEnrollProfile;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("profiles")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(Settings.ENTERPRISE_PLUS)
                .appendSubPath("profile")
                .appendSubPath("clone")
                .build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_PROFILE;
    }

    @Override
    public Class<PostEnrollProfileResponse> getResponseClass() {
        return PostEnrollProfileResponse.class;
    }
}
