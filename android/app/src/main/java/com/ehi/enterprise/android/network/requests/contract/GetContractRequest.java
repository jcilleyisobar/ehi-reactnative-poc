package com.ehi.enterprise.android.network.requests.contract;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.contract.GetContractResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetContractRequest extends AbstractRequestProvider<GetContractResponse> {
    private String mContractId;

    public GetContractRequest(String contractId) {
        mContractId = contractId;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("accounts")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .addQueryParam("contract", mContractId);
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
        return HostType.GBO_RENTAL;
    }

    @Override
    public Class<GetContractResponse> getResponseClass() {
        return GetContractResponse.class;
    }
}
