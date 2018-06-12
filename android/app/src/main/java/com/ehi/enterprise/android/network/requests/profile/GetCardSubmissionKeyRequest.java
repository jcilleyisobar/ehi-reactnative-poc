package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.profile.GetCardSubmissionKeyResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetCardSubmissionKeyRequest extends AbstractRequestProvider<GetCardSubmissionKeyResponse> {

    final private String mReservationId;

    public GetCardSubmissionKeyRequest(String reservationId) {
        mReservationId = reservationId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mReservationId)
                .appendSubPath("cardSubmissionKey")
                .build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    public Class<GetCardSubmissionKeyResponse> getResponseClass() {
        return GetCardSubmissionKeyResponse.class;
    }
}
