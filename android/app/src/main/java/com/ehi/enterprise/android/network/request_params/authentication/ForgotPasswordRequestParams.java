package com.ehi.enterprise.android.network.request_params.authentication;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequestParams {

    @SerializedName("first_name")
    private final String firstName;
    @SerializedName("last_name")
    private final String lastName;
    @SerializedName("email")
    private final String email;

    public ForgotPasswordRequestParams(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}