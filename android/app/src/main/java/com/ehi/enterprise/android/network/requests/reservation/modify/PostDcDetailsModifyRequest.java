package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.PostDcDetailsParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostDcDetailsModifyRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationSessionId;
	private PostDcDetailsParams mRequestParams;

	public PostDcDetailsModifyRequest(String reservationSessionId, EHIDCDetails delivery, EHIDCDetails collection) {
		mReservationSessionId = reservationSessionId;
		mRequestParams = new PostDcDetailsParams(delivery, collection);
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
				.appendSubPath("dcDetails")
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
