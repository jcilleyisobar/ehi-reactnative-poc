package com.ehi.enterprise.android.network.requests;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.utils.BaseAppUtils;

import java.io.Reader;
import java.io.Serializable;
import java.util.Map;

public abstract class AbstractRequestProvider<T> implements Serializable {

    public enum RequestType {
        GET, POST, PUT, PATCH, DELETE
    }

    public enum HostType {
        GBO_LOCATION, GBO_PROFILE, GBO_RENTAL
    }

    //Request part

    public abstract RequestType getRequestType();

    protected HostType getHost() {
        return null;
    }

    public String getEndpointUrl() {
        switch (getHost()) {
            case GBO_LOCATION:
                return Settings.EHI_GBO_LOCATION;
            case GBO_PROFILE:
                return Settings.EHI_GBO_PROFILE;
            case GBO_RENTAL:
                return Settings.EHI_GBO_RENTAL;

        }
        return null;
    }

    public abstract String getRequestUrl();

    public abstract Map<String, String> getHeaders();

    public Object getRequestBody() {
        return null;
    }

    //Response handling part

    public abstract Class<T> getResponseClass();

    /**
     * You can override this method and and parse raw response as you want to.
     * Method was added to parse SOLR response to CROS data structures
     */
    public T parseRawResponse(Reader rawResponse) throws Exception {
        return BaseAppUtils.getDefaultGson().fromJson(rawResponse, getResponseClass());
    }

    /**
     * Used inform {@link com.ehi.enterprise.android.network.services.EnterpriseNetworkService} that there will be no response body to parse
     *
     * @return
     */
    public boolean isExpectingBody() {
        return true;
    }

    /**
     * Performed on the background thread. May be used for writing to DB.
     */

    public void handleResponse(ResponseWrapper<T> wrapper) {
    }

    public String getCorrelationId() {
        if (getHeaders() != null) {
            return getHeaders().get(ApiHeaderBuilder.EHI_CORRELATION_ID);
        }
        return null;
    }

}
