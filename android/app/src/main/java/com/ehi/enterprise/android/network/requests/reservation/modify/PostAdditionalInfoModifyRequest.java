package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.modify.PostAdditionalInfoModifyRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.List;
import java.util.Map;

public class PostAdditionalInfoModifyRequest extends AbstractRequestProvider<EHIReservation> {

	private String mReservationSessionId;
	private PostAdditionalInfoModifyRequestParams mRequestParams;

	public PostAdditionalInfoModifyRequest(String reservationSessionId, List<EHIAdditionalInformation> info) {
		mReservationSessionId = reservationSessionId;
		mRequestParams = new PostAdditionalInfoModifyRequestParams(info);
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
		return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
				.appendSubPath("reservations")
				.appendSubPath(Settings.BRAND)
				.appendSubPath(Settings.CHANNEL)
				.appendSubPath("modify")
				.appendSubPath(mReservationSessionId)
				.appendSubPath("additionalInfo")
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
