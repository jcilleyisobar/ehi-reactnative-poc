package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetRetrieveReservationRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationConfirmationNumber;
	private String mFirstName;
	private String mLastName;

	public GetRetrieveReservationRequest(String reservationConfirmationNumber, String firstName, String lastName) {
		mReservationConfirmationNumber = reservationConfirmationNumber;
		mFirstName = firstName;
		mLastName = lastName;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	protected HostType getHost() {
		return HostType.GBO_RENTAL;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("reservations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath(mReservationConfirmationNumber)
				.addQueryParam("firstName", mFirstName.trim())
				.addQueryParam("lastName", mLastName.trim())
				.addQueryParam("enableNorthAmericanPrepayRates",true)
				.build();
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
				.build();
	}

	@Override
	public Class<EHIReservation> getResponseClass() {
		return EHIReservation.class;
	}

}