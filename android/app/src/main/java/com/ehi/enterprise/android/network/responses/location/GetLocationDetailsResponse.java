package com.ehi.enterprise.android.network.responses.location;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetLocationDetailsResponse extends BaseResponse {

	@SerializedName("search_type")
	private String mSearchType;

	@SerializedName("search_query")
	private String mSearchQuery;

	@SerializedName("location")
	private EHILocation mLocation;

	public String getSearchType() {
		return mSearchType;
	}

	public String getSearchQuery() {
		return mSearchQuery;
	}

	public EHILocation getLocation() {
		return mLocation;
	}

	public void setLocation(EHILocation location) {
		mLocation = location;
	}
}
