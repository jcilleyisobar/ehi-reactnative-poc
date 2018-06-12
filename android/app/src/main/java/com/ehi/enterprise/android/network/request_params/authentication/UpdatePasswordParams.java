package com.ehi.enterprise.android.network.request_params.authentication;

import com.google.gson.annotations.SerializedName;

public class UpdatePasswordParams {

    @SerializedName("new_password")
    private String mNewPassword;

    @SerializedName("confirm_new_password")
    private String mConfirmPassword;

    @SerializedName("remember_credentials")
    private boolean mRememberCredentials;

    public UpdatePasswordParams(String newPassword, String confirmPassword, boolean rememberCredentials) {
        mNewPassword = newPassword;
        mConfirmPassword = confirmPassword;
        mRememberCredentials = rememberCredentials;
    }
}
