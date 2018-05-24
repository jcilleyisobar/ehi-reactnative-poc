package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class PostEmeraldClubLoginRequestParams {
    @SerializedName("username")
    private final String mUsername;

    @SerializedName("password")
    private final String mPassword;

    @SerializedName("remember_credentials")
    private final boolean mRememberCredentials;

    @SerializedName("accept_decline_version")
    private String mTCVersion;

    public PostEmeraldClubLoginRequestParams(final String password,
                                             final String username,
                                             final boolean rememberCredentials) {
        mPassword = password;
        mUsername = username;
        mRememberCredentials = rememberCredentials;
    }

    public PostEmeraldClubLoginRequestParams(String userName, String oldPassword, boolean rememberCredentials, String tcVersion) {
        this(oldPassword, userName, rememberCredentials);
        mTCVersion = tcVersion;
    }
}
