package com.ehi.enterprise.android.network.request_params.authentication;

import com.google.gson.annotations.SerializedName;

public class EncryptedAuthDataRequestParams {
    @SerializedName("ec")
    private String mEncryptedData;
    @SerializedName("remember_credentials")
    private boolean mRememberCredentials;

    public EncryptedAuthDataRequestParams(String encryptedData, final boolean rememberCredentials) {
        mEncryptedData = encryptedData;
        mRememberCredentials = rememberCredentials;
    }
}
