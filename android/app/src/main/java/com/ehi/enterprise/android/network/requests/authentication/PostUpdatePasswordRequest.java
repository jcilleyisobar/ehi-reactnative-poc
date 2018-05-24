package com.ehi.enterprise.android.network.requests.authentication;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.authentication.UpdatePasswordParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.authentication.PostUpdatePasswordResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostUpdatePasswordRequest extends AbstractRequestProvider<PostUpdatePasswordResponse> {

    private String mNewPassword;
    private boolean mRememberMe;
    private String mIndividualId;
    private String mConfirmNewPassword;

    public PostUpdatePasswordRequest(String newPassword, String confirmPassword, boolean rememberMe, String individualId) {
        mNewPassword = newPassword;
        mRememberMe = rememberMe;
        mIndividualId = individualId;
        mConfirmNewPassword = confirmPassword;
    }

    @Override
    public UpdatePasswordParams getRequestBody() {
        return new UpdatePasswordParams(mNewPassword, mConfirmNewPassword, mRememberMe);
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
                .addQueryParam("individualId", mIndividualId)
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
        return HostType.GBO_PROFILE;
    }

    @Override
    public Class<PostUpdatePasswordResponse> getResponseClass() {
        return PostUpdatePasswordResponse.class;
    }
}