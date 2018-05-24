package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.CancelReservationRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.CancelReservationResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostCancelReservationRequest extends AbstractRequestProvider<CancelReservationResponse> {

	private String mReservationSessionId;
	private String mReservationConfirmationNumber;

	public PostCancelReservationRequest(String reservationSessionId, String reservationConfirmationNumber) {
		mReservationSessionId = reservationSessionId;
		mReservationConfirmationNumber = reservationConfirmationNumber;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
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
				.appendSubPath(mReservationSessionId)
				.appendSubPath("cancel")
				.build();
	}

	@Override
	public Object getRequestBody() {
		return new CancelReservationRequestParams(mReservationConfirmationNumber);
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
				.build();
	}

	@Override
	public Class<CancelReservationResponse> getResponseClass() {
		return CancelReservationResponse.class;
	}

}