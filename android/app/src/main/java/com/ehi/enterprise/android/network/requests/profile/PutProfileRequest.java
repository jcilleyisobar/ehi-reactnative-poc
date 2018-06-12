package com.ehi.enterprise.android.network.requests.profile;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.profile.PutProfileParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PutProfileRequest extends AbstractRequestProvider<EHIProfileResponse> {

	private final String mIndividualId;
	private final PutProfileParams mRequestParams;

	public PutProfileRequest(String individualId,
                             PutProfileParams profileParams) {
		mIndividualId = individualId;
		mRequestParams = profileParams;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.PUT;
	}

	@Override
	public String getRequestUrl() {
		EHIUrlBuilder bld = new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("profiles")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath(Settings.ENTERPRISE_PLUS)
				.appendLastSubPath("profile")
				.addQueryParam("individualId", mIndividualId);
		return bld.build();
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
                .addUtf8Encoding()
				.build();
	}

	@Override
	protected HostType getHost() {
		return HostType.GBO_PROFILE;
	}

	@Override
	public Object getRequestBody() {
		return mRequestParams;
	}

	@Override
	public Class<EHIProfileResponse> getResponseClass() {
		return EHIProfileResponse.class;
	}

}