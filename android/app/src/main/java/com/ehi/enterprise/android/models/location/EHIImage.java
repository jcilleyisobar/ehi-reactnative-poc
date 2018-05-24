package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIImage extends EHIModel {

	@SerializedName("name")
	private String mName;

	@SerializedName("path")
	private String mPath;

	@SerializedName("supported_widths")
	private List<String> mSupportedWidth;

	@SerializedName("supported_qualities")
	private List<String> mSupportedQualities;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getPath() {
		return mPath;
	}

	public List<String> getSupportedWidth() {
		return mSupportedWidth;
	}

	public List<String> getSupportedQualities() {
		return mSupportedQualities;
	}

	public void setPath(String path) {
		mPath = path;
	}
}