package com.ehi.enterprise.android.models.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.android.m4b.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EHICityLocation extends EHIModel {
	@SerializedName("market_information") private String mMarketInformation;
	@SerializedName("city") private String mCity;
	@SerializedName("country_subdivision") private String mCountrySubdivision;
	@SerializedName("country") private String mCountry;
	@SerializedName("locations") private List<EHILocation> mLocations;
	@SerializedName("cityId") private String mCityId;
	@SerializedName("numberOfLocations") private int mNumberOfLocations;
	@SerializedName("latitude") private double mLatitude;
	@SerializedName("longitude") private double mLongitude;
	@SerializedName("longName") private String mLongName;
	@SerializedName("shortName") private String mShortName;

	public String getMarketInformation() {
		return mMarketInformation;
	}

	public String getCity() {
		return mCity;
	}

	public String getCountrySubdivision() {
		return mCountrySubdivision;
	}

	public String getCountry() {
		return mCountry;
	}

	public List<EHILocation> getLocations() {
		return mLocations;
	}

	public int getNumberOfLocations() {
		return mNumberOfLocations;
	}

	public String getCityId() {
		return mCityId;
	}

	public void setCityId(String cityId) {
		mCityId = cityId;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public @NonNull Location getCityCenterLocation() {
		Location l = new Location("");
		l.setLatitude(mLatitude);
		l.setLongitude(mLongitude);
		return l;
	}

	public @NonNull LatLng getCenter() {
        return new LatLng(mLatitude, mLongitude);
	}

	public String getLongName() {
		return mLongName;
	}

	public String getLocationName() {
		return getCity();
	}

	public String getShortName() {
		return mShortName;
	}

	public String getFullCityName() {
		StringBuilder bld = new StringBuilder();
		if (mCity != null
				&& !mCity.isEmpty()) {
			bld.append(mCity);
		}

		if (mCountrySubdivision != null
				&& !mCountrySubdivision.isEmpty()) {
			bld.append(", ");
			bld.append(mCountrySubdivision);
		}

		if (mCountry != null
				&& !mCountry.isEmpty()) {
			bld.append(", ");
			bld.append(mCountry);
		}
		return bld.toString();
	}

}
