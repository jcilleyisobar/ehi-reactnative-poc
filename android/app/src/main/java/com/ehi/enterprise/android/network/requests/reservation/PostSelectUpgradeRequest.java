package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.SelectUpgradeRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostSelectUpgradeRequest extends AbstractRequestProvider<EHIReservation> {


	private String mReservationSessionId;
	private String mCarClassCode;

	public PostSelectUpgradeRequest(String reservationSessionId, String carClassCode) {
		mReservationSessionId = reservationSessionId;
		mCarClassCode = carClassCode;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.PUT;
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
				.appendSubPath("selectUpgrade")
				.build();
	}

	@Override
	public Object getRequestBody() {
		return new SelectUpgradeRequestParams(mCarClassCode);
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