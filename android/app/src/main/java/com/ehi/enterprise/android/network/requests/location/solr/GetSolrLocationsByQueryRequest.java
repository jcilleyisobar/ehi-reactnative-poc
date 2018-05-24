package com.ehi.enterprise.android.network.requests.location.solr;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsByQueryResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

import java.util.Locale;
import java.util.Map;

public class GetSolrLocationsByQueryRequest extends AbstractRequestProvider<GetSolrLocationsByQueryResponse> {

    private static final String TAG = "GetSolrLocationsByQueryRequest";

    @NonNull
    private String mQuery;
    private boolean mDropOffLocation = false;

    public GetSolrLocationsByQueryRequest(@NonNull String query, boolean dropOffLocation) {
        mQuery = query;
        mDropOffLocation = dropOffLocation;
    }

    @Override
    public String getEndpointUrl() {
        return Settings.SOLR_ENDPOINT_URL;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath("text")
                .appendSubPath(mQuery)
                .addQueryParam("fallback", Settings.SOLR_FALLBACK_LANGUAGE)
                .addQueryParam("locale", Locale.getDefault().toString())
                .addQueryParam("brand", Settings.SOLR_BRAND)
                .addQueryParam("countryCode", LocalDataManager.getInstance().getPreferredCountryCode());

        if (mDropOffLocation) {
            bld.addQueryParam("oneWay", true);
        }

        return bld.build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder.solrDefaultHeaders()
                .build();
    }

    @Override
    public Class<GetSolrLocationsByQueryResponse> getResponseClass() {
        return GetSolrLocationsByQueryResponse.class;
    }

}
