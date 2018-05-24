package com.ehi.enterprise.android.models;

import com.google.gson.annotations.SerializedName;

public class EHIErrorMessage extends EHIModel {

	@SerializedName("code")
	private String mErrorCode;

	@SerializedName("message")
	private String mErrorMessage;

	@SerializedName("priority")
	private String mPriority;

    @SerializedName("display_as")
    private String mDisplayAs;

	public String getErrorCode() {
		return mErrorCode;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public String getPriority() {
		return mPriority;
	}

    public String getDisplayAs() {
        return mDisplayAs;
    }
}
