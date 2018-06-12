package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetProfileRequest extends AbstractRequestProvider<EHIProfileResponse> {

	private String mIndividualId;

	public GetProfileRequest(String userId) {
		mIndividualId = userId;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("profiles")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath(Settings.ENTERPRISE_PLUS)
				.appendSubPath("profile")
				.addQueryParam("individualId", mIndividualId)
				.build();
	}

	@Override
	protected HostType getHost() {
		return HostType.GBO_PROFILE;
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
				.build();
	}

	@Override
	public Class<EHIProfileResponse> getResponseClass() {
		return EHIProfileResponse.class;
	}

	@Override
	public void handleResponse(ResponseWrapper<EHIProfileResponse> wrapper) {

	}

}