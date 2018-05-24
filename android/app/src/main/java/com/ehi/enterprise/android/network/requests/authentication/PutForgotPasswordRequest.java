package com.ehi.enterprise.android.network.requests.authentication;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.authentication.ForgotPasswordRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PutForgotPasswordRequest extends AbstractRequestProvider<BaseResponse> {

    private final String firstName;
    private final String lastName;
    private final String email;

    public PutForgotPasswordRequest(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PUT;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("profiles")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("profile")
                .appendSubPath("password")
                .appendSubPath("reset")
                .build();
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_PROFILE;
    }

    @Override
    public Object getRequestBody() {
        return new ForgotPasswordRequestParams(firstName, lastName, email);
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

}