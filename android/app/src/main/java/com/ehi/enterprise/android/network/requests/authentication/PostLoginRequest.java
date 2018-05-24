package com.ehi.enterprise.android.network.requests.authentication;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.authentication.AuthPairRequestParams;
import com.ehi.enterprise.android.network.request_params.authentication.EncryptedAuthDataRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostLoginRequest extends AbstractRequestProvider<EHIProfileResponse> {

	private static final String mRequestEndpoint = "login";
	private String mNewPassword;
	private String mEncryptedAuthData;
	private String mUserName;
	private String mPassword;
	private boolean mRememberCredentials;
	private String mTermsConditionsVersion;

	public PostLoginRequest(String encryptedAuthData) {
		mEncryptedAuthData = encryptedAuthData;
        mRememberCredentials = true;
	}

	public PostLoginRequest(String userName, String password, boolean rememberCredentials, String termsConditionsVersion) {
		mUserName = userName;
		mPassword = password;
		mRememberCredentials = rememberCredentials;
		mTermsConditionsVersion = termsConditionsVersion;
	}


	public PostLoginRequest(String userName, String oldPassword, boolean rememberCredentials, String termsConditionsVersion, String newPassword) {
		this(userName, oldPassword, rememberCredentials, termsConditionsVersion);
		mNewPassword = newPassword;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public Object getRequestBody() {
		if (mEncryptedAuthData == null) {
			if(mNewPassword != null){
				return new AuthPairRequestParams(mUserName, mPassword, mRememberCredentials, mTermsConditionsVersion, mNewPassword);
			}
			else {
				return new AuthPairRequestParams(mUserName, mPassword, mRememberCredentials, mTermsConditionsVersion);
			}
		}
		else {
			return new EncryptedAuthDataRequestParams(mEncryptedAuthData, true);
		}
	}

	@Override
	public String getRequestUrl() {
		// /api/login"
		return new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("profiles")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath(Settings.ENTERPRISE_PLUS)
				.appendSubPath(mRequestEndpoint)
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