package com.ehi.enterprise.android.network.request_params.authentication;

import com.google.gson.annotations.SerializedName;

public class AuthPairRequestParams {

	@SerializedName("new_password")
	private String mNewPassword;

	@SerializedName("username")
	private String mUserName;

	@SerializedName("password")
	private String mLogin;

	@SerializedName("remember_credentials")
	private boolean mRememberCredentials;

	@SerializedName("accept_decline_version")
	private String mAcceptDeclineVersion;

	public AuthPairRequestParams(String userName, String login, boolean rememberCredentials, String acceptDeclineVersion) {
		mUserName = userName;
		mLogin = login;
		mRememberCredentials = rememberCredentials;
		mAcceptDeclineVersion = acceptDeclineVersion;
	}

	public AuthPairRequestParams(String userName, String login, boolean rememberCredentials, String termsConditionsVersion, String newPassword) {
		this(userName, login, rememberCredentials, termsConditionsVersion);
		mNewPassword = newPassword;
	}
}