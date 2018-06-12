package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.UpdateExtrasRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.List;
import java.util.Map;

public class PostUpdateExtrasRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationSessionId;
	private UpdateExtrasRequestParams mRequestParams;

	public PostUpdateExtrasRequest(String reservationSessionId, List<EHIExtraItem> extras) {
		mReservationSessionId = reservationSessionId;
		mRequestParams = new UpdateExtrasRequestParams(extras);
	}

	@Override
	protected HostType getHost() {
		return HostType.GBO_RENTAL;
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
				.appendSubPath(mReservationSessionId)
				.appendSubPath("extras")
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
	public Class<EHIReservation> getResponseClass() {
		return EHIReservation.class;
	}

}