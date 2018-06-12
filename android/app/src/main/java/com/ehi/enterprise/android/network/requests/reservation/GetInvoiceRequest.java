package com.ehi.enterprise.android.network.requests.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.GetInvoiceResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetInvoiceRequest extends AbstractRequestProvider<GetInvoiceResponse> {

    @NonNull
    private String mInvoiceNumber;

    public GetInvoiceRequest(@NonNull String invoiceNumber) {
        mInvoiceNumber = invoiceNumber;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("trips")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("past")
                .appendLastSubPath("invoice")
                .addQueryParam("invoiceNumber", mInvoiceNumber)
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
    public Class<GetInvoiceResponse> getResponseClass() {
        return GetInvoiceResponse.class;
    }

}