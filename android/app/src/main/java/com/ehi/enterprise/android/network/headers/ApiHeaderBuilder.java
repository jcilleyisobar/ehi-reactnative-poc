package com.ehi.enterprise.android.network.headers;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;

import java.util.Locale;
import java.util.UUID;

public class ApiHeaderBuilder {

    public static final String EHI_API_KEY = "Ehi-API-Key";
    public static final String EHI_CORRELATION_ID = "CORRELATION_ID";
    public static final String EHI_AUTH_TOKEN = "Ehi-Auth-Token";
    public static final String EHI_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String EHI_COUNTRY_OF_RESIDENCE_CODE = "Country-Of-Residence-Code";
    public static final String EHI_CONTENT_TYPE = "Content-Type";
    public static final String EHI_JSON_UTF_8 = "application/json;charset=UTF-8";

    private ApiHeaders mHeaders = new ApiHeaders();

    private ApiHeaderBuilder() {
    }

    public static ApiHeaderBuilder gboDefaultHeaders() {
        ApiHeaderBuilder builder = new ApiHeaderBuilder();
        if (LocalDataManager.getInstance().shouldForceGboInvalidKey()){
            builder.mHeaders.put(EHI_API_KEY, "FORCE_WRONG_KEY_TEST_EAPP");
        } else {
            builder.mHeaders.put(EHI_API_KEY, Settings.EHI_GBO_API_KEY);
        }
        builder.mHeaders.put(EHI_CORRELATION_ID, UUID.randomUUID().toString());
        if (LoginManager.getInstance().isLoggedIn()) {
            builder.mHeaders.put(EHI_AUTH_TOKEN, LoginManager.getInstance().getUserAuthToken());
        } else if (ReservationManager.getInstance().isLoggedIntoEmeraldClub()) {
            builder.mHeaders.put(EHI_AUTH_TOKEN, ReservationManager.getInstance().getEmeraldClubAuthToken());
        }

        builder.mHeaders.put(EHI_ACCEPT_LANGUAGE, Locale.getDefault().toString());
        builder.mHeaders.put(EHI_COUNTRY_OF_RESIDENCE_CODE, LocalDataManager.getInstance().getPreferredCountryCode());

        return builder;
    }

    public static ApiHeaderBuilder solrDefaultHeaders() {
        ApiHeaderBuilder builder = new ApiHeaderBuilder();
        builder.mHeaders.put("SEARCH-TOKEN", Settings.SOLR_API_KEY);

        return builder;
    }

    public ApiHeaderBuilder addUtf8Encoding(){
        mHeaders.put(EHI_CONTENT_TYPE, EHI_JSON_UTF_8);
        return this;
    }

    public ApiHeaders build() {
        return mHeaders;
    }

}