package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.google.gson.annotations.SerializedName;

public class PutLicenseProfileParams extends EHIModel {

	@SerializedName("license_profile")
	private EHILicenseProfile mLicenseProfile;

	public PutLicenseProfileParams(EHILicenseProfile licenseProfile) {
		mLicenseProfile = licenseProfile;
	}
}
