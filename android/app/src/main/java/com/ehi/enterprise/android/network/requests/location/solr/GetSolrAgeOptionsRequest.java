package com.ehi.enterprise.android.network.requests.location.solr;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrAgeOptionsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetSolrAgeOptionsRequest extends AbstractRequestProvider<GetSolrAgeOptionsResponse[]> {

    private EHISolrLocation mEHISolrLocation;

    public GetSolrAgeOptionsRequest(EHISolrLocation EHISolrLocation) {
        mEHISolrLocation = EHISolrLocation;
    }

    @Override
    public Class<GetSolrAgeOptionsResponse[]> getResponseClass() {
        return GetSolrAgeOptionsResponse[].class;
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder.solrDefaultHeaders()
                .build();
    }

    @Override
    public String getEndpointUrl() {
        return Settings.SOLR_ENDPOINT_URL;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath("renterage")
                .appendSubPath(mEHISolrLocation.getPeopleSoftId())
                .build();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }
}
