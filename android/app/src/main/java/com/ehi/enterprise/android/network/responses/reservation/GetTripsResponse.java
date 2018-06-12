package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTripsResponse extends BaseResponse {
	@SerializedName("trip_summaries")
	private List<EHITripSummary> mTripSummaries;

	public List<EHITripSummary> getTripSummariesList() {
		return mTripSummaries;
	}

}
