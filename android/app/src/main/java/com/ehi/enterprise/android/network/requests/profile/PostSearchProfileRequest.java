package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIRenterSearchCriteria;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.profile.PostSearchProfileResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostSearchProfileRequest extends AbstractRequestProvider<PostSearchProfileResponse> {

    private final EHIRenterSearchCriteria mBody;

    public PostSearchProfileRequest(EHIRenterSearchCriteria searchCriteria) {
        mBody = searchCriteria;
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
                .appendSubPath("searchProfile")
                .appendLastSubPath("driverLicenseCriteria")
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
    public Object getRequestBody() {
        return mBody;
    }

    @Override
    public Class<PostSearchProfileResponse> getResponseClass() {
        return PostSearchProfileResponse.class;
    }
}
