package com.ehi.enterprise.android.network.requests.location;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetCountriesRequest extends AbstractRequestProvider<GetCountriesResponse> {

	private static final String TAG = GetCountriesRequest.class.getSimpleName();

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
				.appendSubPath("countries");
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
	public Class<GetCountriesResponse> getResponseClass() {
		return GetCountriesResponse.class;
	}

	@Override
	public void handleResponse(ResponseWrapper<GetCountriesResponse> wrapper) {
		super.handleResponse(wrapper);
		if (wrapper.isSuccess()) {
			wrapper.getData().toOrderedList();
		}
	}

}