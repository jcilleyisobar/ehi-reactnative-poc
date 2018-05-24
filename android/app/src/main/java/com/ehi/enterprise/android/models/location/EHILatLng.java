package com.ehi.enterprise.android.models.location;

import android.location.Location;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.android.m4b.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class EHILatLng extends EHIModel {

	@SerializedName("latitude")
	private double mLatitude;

	@SerializedName("longitude")
	private double mLongitude;

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	public EHILatLng(LatLng latLng) {
		if(latLng != null) {
			mLatitude = latLng.latitude;
			mLongitude = latLng.longitude;
		}
	}

	public EHILatLng(){

	}

	@Nullable
	public Location getLocation() {
		if (Double.valueOf(mLatitude).equals(0.0d)
				&& Double.valueOf(mLongitude).equals(0.0d)) {
			return null;
		}
		Location l = new Location("generated");
		l.setLatitude(mLatitude);
		l.setLongitude(mLongitude);
		return l;
	}

	@Nullable
	public LatLng getLatLng() {
		if (Double.valueOf(mLatitude).equals(0.0d)
				&& Double.valueOf(mLongitude).equals(0.0d)) {
			return null;
		}
		return new LatLng(mLatitude, mLongitude);
	}

    @Override
    public String toString() {
        return "EHILatLng{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                "}";
    }
}