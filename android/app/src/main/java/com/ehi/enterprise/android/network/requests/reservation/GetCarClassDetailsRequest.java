package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.CarClassDetailsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class GetCarClassDetailsRequest extends AbstractRequestProvider<CarClassDetailsResponse> {

	private String mReservationSessionId;
	private String mCarClassCode;
    private int mRedemptionDayCount;

    public GetCarClassDetailsRequest(String reservationSessionId, String carClassCode, int redemptionDayCount) {
        mReservationSessionId = reservationSessionId;
        mCarClassCode = carClassCode;
        mRedemptionDayCount = redemptionDayCount;
    }

	@Override
	protected HostType getHost() {
		return HostType.GBO_RENTAL;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("reservations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath(mReservationSessionId)
				.appendSubPath("carClassDetails")
				.addQueryParam("carClassCode", mCarClassCode)
				.addQueryParam("redemptionDayCount", mRedemptionDayCount)
				.build();
	}

	@Override
	public Object getRequestBody() {
		return null;
	}

	@Override
	public Map<String, String> getHeaders() {
		return ApiHeaderBuilder
				.gboDefaultHeaders()
				.build();
	}

	@Override
	public Class<CarClassDetailsResponse> getResponseClass() {
		return CarClassDetailsResponse.class;
	}

}