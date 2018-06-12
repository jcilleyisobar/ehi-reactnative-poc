package com.ehi.enterprise.android.network.requests.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetMorePrepayTermsConditionsRequest extends AbstractRequestProvider<GetMorePrepayTermsConditionsResponse> {

	private String mCountryCode;

	public GetMorePrepayTermsConditionsRequest(@NonNull String countryCode) {
		mCountryCode = countryCode;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("content")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath("prepayTermsAndConditions")
				.appendSubPath(mCountryCode)
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
	public Class<GetMorePrepayTermsConditionsResponse> getResponseClass() {
		return GetMorePrepayTermsConditionsResponse.class;
	}

}
