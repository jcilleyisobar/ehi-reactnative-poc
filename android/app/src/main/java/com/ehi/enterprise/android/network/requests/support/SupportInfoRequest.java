package com.ehi.enterprise.android.network.requests.support;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.support.EHISupportInfo;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class SupportInfoRequest extends AbstractRequestProvider<EHISupportInfo>{

    private final String mCountry;

    public SupportInfoRequest(String userCountry) {
        mCountry = userCountry;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                            .appendSubPath("reservations")
                            .appendSubPath(Settings.BRAND)
                            .appendSubPath(Settings.CHANNEL)
                            .appendSubPath("support")
                            .appendSubPath("contact")
                            .appendSubPath(mCountry)
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
        return HostType.GBO_RENTAL;
    }

    @Override
    public Class<EHISupportInfo> getResponseClass() {
        return EHISupportInfo.class;
    }

}