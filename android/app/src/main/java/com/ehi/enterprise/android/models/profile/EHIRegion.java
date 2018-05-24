package com.ehi.enterprise.android.models.profile;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIRegion extends EHIModel implements Comparable<EHIRegion> {

	@SerializedName("country_subdivision_name")
	private String mSubdivisionName;

	@SerializedName("country_subdivision_code")
	private String mSubdivisionCode;

	public EHIRegion(String subdivisionName, String subdivisionCode) {
		mSubdivisionName = subdivisionName;
		mSubdivisionCode = subdivisionCode;
	}

	public EHIRegion() {
	}

	public String getSubdivisionName() {
		return mSubdivisionName;
	}

	public String getSubdivisionCode() {
		return mSubdivisionCode;
	}

	@Override
	public int compareTo(@NonNull EHIRegion region) {
		if (mSubdivisionName != null && region.getSubdivisionName() != null) {
			return mSubdivisionName.compareTo(region.getSubdivisionName());
		} else {
			return 0;
		}
	}
}
