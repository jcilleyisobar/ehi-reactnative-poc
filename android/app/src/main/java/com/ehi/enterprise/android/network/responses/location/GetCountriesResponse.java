package com.ehi.enterprise.android.network.responses.location;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetCountriesResponse extends BaseResponse {

	@SerializedName("countries")
	private Map<String, EHICountry> mCountriesMap;

	private List<EHICountry> mCountriesList;

	public List<EHICountry> getCountries() {
		if (mCountriesList == null) {
			return Collections.emptyList();
		}
		return mCountriesList;
	}

	public void setCountries(List<EHICountry> countriesList) {
		mCountriesList = countriesList;
	}

	public void toOrderedList() {
		if (mCountriesMap != null) {
			final List<EHICountry> list = new ArrayList<>(mCountriesMap.values());
			Collections.sort(list);
			setCountries(list);
		}
	}
}
