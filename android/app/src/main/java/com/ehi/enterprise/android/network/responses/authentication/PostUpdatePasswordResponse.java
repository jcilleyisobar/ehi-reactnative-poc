package com.ehi.enterprise.android.network.responses.authentication;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class PostUpdatePasswordResponse extends BaseResponse {

    @SerializedName("ec")
    private String mEncryptedCredentials;

    @SerializedName("auth_token")
    private String mAuthToken;


    public String getEncryptedCredentials() {
        return mEncryptedCredentials;
    }

    public String getAuthToken() {
        return mAuthToken;
    }
}
