package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class DeletePaymentRequest extends AbstractRequestProvider<EHIProfileResponse> {

    private final String mIndividualId;
    private final String mPaymentReferenceId;

    public DeletePaymentRequest(String userId, String paymentReferenceId) {
        mIndividualId = userId;
        mPaymentReferenceId = paymentReferenceId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE;
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
                .addQueryParam("individualId", mIndividualId)
                .addQueryParam("paymentReferenceId", mPaymentReferenceId);
        return bld.toString();
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
    public Class<EHIProfileResponse> getResponseClass() {
        return EHIProfileResponse.class;
    }
}
