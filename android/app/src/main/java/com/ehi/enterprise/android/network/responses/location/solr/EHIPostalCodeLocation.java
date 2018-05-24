package com.ehi.enterprise.android.network.responses.location.solr;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.android.m4b.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class EHIPostalCodeLocation extends EHIModel {
	@SerializedName("countryCode") private String mCountryCode;
	@SerializedName("locationName") private String mLocationName;
	@SerializedName("postalCode") private String mPostalCode;
	@SerializedName("latitude") private double mLatitude;
	@SerializedName("longitude") private double mLongitude;
	@SerializedName("longName") private String mLongName;

	public String getCountryCode() {
		return mCountryCode;
	}

	public String getLocationName() {
		return mLocationName;
	}

	public LatLng getCenter() {
		return new LatLng(mLatitude, mLongitude);
	}

	public String getPostalCode() {
		return mPostalCode;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public String getLongName() {
		return mLongName;
	}
}
