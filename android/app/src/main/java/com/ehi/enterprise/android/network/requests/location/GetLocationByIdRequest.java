package com.ehi.enterprise.android.network.requests.location;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetLocationByIdRequest extends AbstractRequestProvider<GetLocationDetailsResponse> {

    private static final String TAG = "GetLocationByIdRequest";

    private String mId;

    public GetLocationByIdRequest(String id) {
        mId = id;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("locations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mId);
        return bld.build();
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_LOCATION;
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    public Class<GetLocationDetailsResponse> getResponseClass() {
        return GetLocationDetailsResponse.class;
    }

}