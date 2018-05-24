package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIWorkingHours extends EHIModel {

	public static final String TYPE_STANDARD = "STANDARD";
	public static final String TYPE_DROP = "DROP";

	@SerializedName("type")
	private String mType;

	@SerializedName("days")
	private List<EHIWorkingDayInfo> mWorkingDays;

	public EHIWorkingHours(List<EHIWorkingDayInfo> workingDays) {
		mType = TYPE_STANDARD;
		mWorkingDays = workingDays;
	}

	public String getType() {
		return mType;
	}

	public List<EHIWorkingDayInfo> getWorkingDays() {
		return mWorkingDays;
	}
}
