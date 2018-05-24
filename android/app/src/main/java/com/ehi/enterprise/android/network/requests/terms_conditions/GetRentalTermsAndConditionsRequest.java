package com.ehi.enterprise.android.network.requests.terms_conditions;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetRentalTermsAndConditionsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetRentalTermsAndConditionsRequest extends AbstractRequestProvider<GetRentalTermsAndConditionsResponse> {

    private String mReservationSessionId;

    public GetRentalTermsAndConditionsRequest(String reservationSessionId) {
        mReservationSessionId = reservationSessionId;
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mReservationSessionId)
                .appendSubPath("rentalTermsAndConditions")
                .build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    public Class<GetRentalTermsAndConditionsResponse> getResponseClass() {
        return GetRentalTermsAndConditionsResponse.class;
    }

}