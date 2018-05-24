package com.ehi.enterprise.android.network.responses.location;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCityLocationsResponse extends BaseResponse {

	@SerializedName("search_type")
	private String mSearchType;

	@SerializedName("search_query")
	private String mSearchQuery;

	@SerializedName("city_locations")
	private ArrayList<EHICityLocation> mCityLocations;

	public String getSearchType() {
		return mSearchType;
	}

	public String getSearchQuery() {
		return mSearchQuery;
	}

	public ArrayList<EHICityLocation> getCityLocations() {
		return mCityLocations;
	}
}
