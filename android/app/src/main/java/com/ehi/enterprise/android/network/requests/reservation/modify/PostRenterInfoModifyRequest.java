package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.modify.PostRenterInfoRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostRenterInfoModifyRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationSessionId;
	private PostRenterInfoRequestParams mRequestParams;

	public PostRenterInfoModifyRequest(String reservationSessionId,
	                                   EHIDriverInfo driverInfo,
	                                   EHIAirlineInformation flightInfo) {
		mReservationSessionId = reservationSessionId;
		mRequestParams = new PostRenterInfoRequestParams(driverInfo, flightInfo);
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public String getRequestUrl() {
		return new EHIUrlBuilder()
				.appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("reservations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath("modify")
				.appendSubPath(mReservationSessionId)
				.appendSubPath("renter")
				.build();
	}

	@Override
	public Object getRequestBody() {
		return mRequestParams;
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
		return HostType.GBO_RENTAL;
	}

	@Override
	public Class<EHIReservation> getResponseClass() {
		return EHIReservation.class;
	}
}
