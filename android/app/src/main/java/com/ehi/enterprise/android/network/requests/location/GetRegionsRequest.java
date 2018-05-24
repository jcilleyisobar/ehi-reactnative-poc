package com.ehi.enterprise.android.network.requests.location;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetRegionsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetRegionsRequest extends AbstractRequestProvider<GetRegionsResponse> {

	private static final String TAG = GetRegionsRequest.class.getSimpleName();

	private String mCountryCode;

	public GetRegionsRequest(String countryCode) {
		mCountryCode = countryCode;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getRequestUrl() {
		EHIUrlBuilder bld = new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("locations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath("countries")
				.appendSubPath(mCountryCode)
				.appendSubPath("statesorprovinces");
		return bld.build();
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
				.build();
	}

	@Override
	protected HostType getHost() {
		return HostType.GBO_LOCATION;
	}

	@Override
	public Class<GetRegionsResponse> getResponseClass() {
		return GetRegionsResponse.class;
	}

	@Override
	public void handleResponse(ResponseWrapper<GetRegionsResponse> wrapper) {
		super.handleResponse(wrapper);
		if (wrapper.isSuccess()) {
			if (wrapper.getData() != null) {
				wrapper.getData().toOrderedList();
			}
		}
	}

}