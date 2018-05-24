package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.authentication.EncryptedAuthDataRequestParams;
import com.ehi.enterprise.android.network.request_params.reservation.PostEmeraldClubLoginRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostEmeraldClubLoginRequest extends AbstractRequestProvider<EHIProfileResponse>{

    private PostEmeraldClubLoginRequestParams mRequestParams;
    private String mEncryptedData;
    private String mTCVersion;

    public PostEmeraldClubLoginRequest(final String password,
                                       final String username,
                                       final boolean rememberCredentials) {
        mRequestParams = new PostEmeraldClubLoginRequestParams(username, password, rememberCredentials);
    }

    public PostEmeraldClubLoginRequest(final String encryptedData){
        mEncryptedData = encryptedData;
    }

    public PostEmeraldClubLoginRequest(String userName, String oldPassword, boolean rememberCredentials, String tcVersions) {
        mTCVersion = tcVersions;
        mRequestParams = new PostEmeraldClubLoginRequestParams(userName, oldPassword, rememberCredentials, mTCVersion);

    }

    @Override
    public Object getRequestBody() {
        if(mEncryptedData == null) {
            return mRequestParams;
        }
        else {
            return new EncryptedAuthDataRequestParams(mEncryptedData, true);
        }
    }

    @Override
    public AbstractRequestProvider.RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("profiles")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(Settings.EMERALD_CLUB)
                .appendSubPath("login")
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
    public Class<EHIProfileResponse> getResponseClass() {
        return EHIProfileResponse.class;
    }

}