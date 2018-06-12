package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.profile.GetCardSubmissionKeyResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostProfileCardSubmissionKeyRequest extends AbstractRequestProvider<GetCardSubmissionKeyResponse> {

    private final String mIndividualId;

    public PostProfileCardSubmissionKeyRequest(String individualId) {
        mIndividualId = individualId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("profiles")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("profile")
                .appendSubPath("payment")
                .appendSubPath("cardSubmissionKey")
                .addQueryParam("individualId", mIndividualId);
        return bld.build();
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
    public Object getRequestBody() {
        return new Object();
    }

    @Override
    public Class<GetCardSubmissionKeyResponse> getResponseClass() {
        return GetCardSubmissionKeyResponse.class;
    }
}
