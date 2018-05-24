package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetUpgradeDetailsModifyRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationSessionId;

	public GetUpgradeDetailsModifyRequest(String reservationSessionId) {
		mReservationSessionId = reservationSessionId;
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
		return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("reservations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath("modify")
				.appendSubPath(mReservationSessionId)
				.appendSubPath("getUpgrades")
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