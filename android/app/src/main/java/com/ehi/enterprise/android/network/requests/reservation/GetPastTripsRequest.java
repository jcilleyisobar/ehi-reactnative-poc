package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.profile.PostPastTripsParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.GetTripsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Date;
import java.util.Map;

public class GetPastTripsRequest extends AbstractRequestProvider<GetTripsResponse> {

    private final PostPastTripsParams mRequestParams;

    public GetPastTripsRequest(String userId, Date fromDate, Date toDate) {
        mRequestParams = new PostPastTripsParams(userId, fromDate, toDate);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("trips")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("past")
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
    public Object getRequestBody() {
        return mRequestParams;
    }

    @Override
    public Class<GetTripsResponse> getResponseClass() {
        return GetTripsResponse.class;
    }

}