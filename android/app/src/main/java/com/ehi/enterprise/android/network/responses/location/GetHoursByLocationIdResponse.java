package com.ehi.enterprise.android.network.responses.location;

import com.ehi.enterprise.android.models.location.EHIWorkingHours;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetHoursByLocationIdResponse extends BaseResponse {

	@SerializedName("hours")
	private List<EHIWorkingHours> mWorkingHours;

	public List<EHIWorkingHours> getWorkingHours() {
		return mWorkingHours;
	}
}
