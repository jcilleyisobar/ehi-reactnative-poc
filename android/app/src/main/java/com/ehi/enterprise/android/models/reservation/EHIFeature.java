package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIFeature extends EHIModel {

	public static String TRANSMISSION_CODE_AUTOMATIC = "25";
	public static String TRANSMISSION_CODE_MANUAL = "26";

	@SerializedName("code")
	private String mCode;

	@SerializedName("description")
	private String mDescription;

    public EHIFeature(final String code, final String description) {
        mCode = code;
        mDescription = description;
    }

    public String getCode() {
		return mCode;
	}

	public String getDescription() {
		return mDescription;
	}

}