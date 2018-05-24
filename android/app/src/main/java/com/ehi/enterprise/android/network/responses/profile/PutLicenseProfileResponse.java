package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class PutLicenseProfileResponse extends BaseResponse {

	@SerializedName("license_profile")
	private EHILicenseProfile mLicenseProfile;

	public EHILicenseProfile getLicenseProfile() {
		return mLicenseProfile;
	}
}
