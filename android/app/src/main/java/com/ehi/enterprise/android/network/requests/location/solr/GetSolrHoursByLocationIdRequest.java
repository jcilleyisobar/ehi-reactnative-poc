package com.ehi.enterprise.android.network.requests.location.solr;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GetSolrHoursByLocationIdRequest extends AbstractRequestProvider<GetSolrHoursResponse> {

	private static final String TAG = GetSolrHoursByLocationIdRequest.class.getSimpleName();

	private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	private final String mPeopleSoftId;
	private Date mFromDate;
	private final Date mToDate;

	public GetSolrHoursByLocationIdRequest(String peopleSoftId, Date fromDate) {
		this(peopleSoftId, fromDate, fromDate);
	}

	public GetSolrHoursByLocationIdRequest(String peopleSoftId, Date fromDate, Date toDate){
		mPeopleSoftId = peopleSoftId;
		mFromDate = fromDate;
		mToDate = toDate;
	}

	@Override
	public String getEndpointUrl() {
		return Settings.SOLR_ENDPOINT_URL;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder()
				.appendSubPath("hours")
				.appendSubPath(mPeopleSoftId)
				.addQueryParam("from", sDateFormatter.format(mFromDate))
				.addQueryParam("to", sDateFormatter.format(mToDate))
				.addQueryParam("fallback",Settings.SOLR_FALLBACK_LANGUAGE)
				.build();
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder.solrDefaultHeaders()
				.build();
	}

	@Override
	public Class<GetSolrHoursResponse> getResponseClass() {
		return GetSolrHoursResponse.class;
	}

}
