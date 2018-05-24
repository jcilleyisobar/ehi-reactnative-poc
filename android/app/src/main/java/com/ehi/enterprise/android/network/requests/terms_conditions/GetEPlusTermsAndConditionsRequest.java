package com.ehi.enterprise.android.network.requests.terms_conditions;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetEPlusTermsAndConditionsRequest extends AbstractRequestProvider<GetEPlusTermsAndConditionsResponse> {

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("content")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("terms");
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
    public Class<GetEPlusTermsAndConditionsResponse> getResponseClass() {
        return GetEPlusTermsAndConditionsResponse.class;
    }
}
