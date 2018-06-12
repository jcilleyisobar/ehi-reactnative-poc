package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIWayfindingStep extends EHIModel{

	@SerializedName("path")
	private String mIconPath;

	@SerializedName("text")
	private String mText;

	public String getText() {
		return mText;
	}

	public String getIconPath() {
		return mIconPath;
	}
}
