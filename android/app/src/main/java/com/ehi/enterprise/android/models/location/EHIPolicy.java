package com.ehi.enterprise.android.models.location;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHIPolicy extends EHIModel {

	public static final String AdditionalDriver = "ADDR";
	public static final String AgeRequirements = "AGE";
	public static final String CodePayment = "PYMT";
	public static final String AfterHours = "AFHR";
	public static final String DamageWaiver = "CDW";
	public static final String Exclusive = "EXCL";
	public static final String Insurance = "INS";
	public static final String PersonalCoverage = "PAC";
	public static final String PersonalInsurance = "PAI";
	public static final String RoadsideProtection = "RAP";
	public static final String RenterRequirements = "RQMT";
	public static final String Shuttle = "SHTL";
	public static final String SupplementalLiability = "SLP";
	public static final String TollConvenience = "TCC";
	public static final String Miscellaneous = "MISC";

	@Retention(RetentionPolicy.CLASS)
	@StringDef({AdditionalDriver, AgeRequirements,
			CodePayment, AfterHours, DamageWaiver,
			Exclusive, Insurance, PersonalCoverage,
			PersonalInsurance, RoadsideProtection, RenterRequirements,
			Shuttle, SupplementalLiability, TollConvenience, Miscellaneous})
	public @interface PolicyCode {
	}

	@SerializedName("code")
	private String mCode;

	@SerializedName("description")
	private String mDescription;

	@SerializedName("mandatory")
	private boolean mMandatory;

	@SerializedName("policy_description")
	private String mPolicyDescription;
	@SerializedName("policy_text")
	private String mPolicyText;

	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		mCode = code;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public boolean isMandatory() {
		return mMandatory;
	}

	public void setMandatory(boolean mandatory) {
		mMandatory = mandatory;
	}

	public String getPolicyDescription() {
		return mPolicyDescription;
	}

	public void setPolicyDescription(String policyDescription) {
		mPolicyDescription = policyDescription;
	}

	public String getPolicyText() {
		return mPolicyText;
	}

	public void setPolicyText(String policyText) {
		mPolicyText = policyText;
	}

	@Override
	public String toString() {
		return "EHIPolicy{" +
				"mCode=\"" + mCode + "\"" +
				", mDescription=\"" + mDescription + "\"" +
				", mMandatory=" + mMandatory +
				", mPolicyDescription=\"" + mPolicyDescription + "\"" +
				", mPolicyText=\"" + mPolicyText + "\"" +
				"}";
	}

	public boolean isPolicy(@PolicyCode String policy) {
		return policy.equalsIgnoreCase(this.getCode());
	}
}
