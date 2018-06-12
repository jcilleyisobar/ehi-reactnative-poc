package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class EHIKeyFactsPolicy extends EHIModel {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PROTECTIONS, EQUIPMENT, MINIMUM_REQUIREMENTS, ADDITIONAL})
    public @interface KeyFactsSection{}
    public static final String PROTECTIONS = "PROTECTIONS";
    public static final String EQUIPMENT = "EQUIPMENT";
    public static final String MINIMUM_REQUIREMENTS = "MINIMUM_REQUIREMENTS";
    public static final String ADDITIONAL = "ADDITIONAL";
    public static final String QUESTIONS = "QUESTIONS";

    @SerializedName("code")
    private String mCode;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("key_facts_included")
    private boolean mIsIncluded;

    @KeyFactsSection
    @SerializedName("key_facts_section")
    private String mSection;

    @SerializedName("mandatory")
    private boolean mIsMandatory;

    @SerializedName("policy_description")
    private String mPoliciyDescription;

    @SerializedName("policy_exclusions")
    private List<EHIKeyFactsPolicy> mPolicyExclusions;

    @SerializedName("policy_text")
    private String mPolicyText;

    public String getCode() {
        return mCode;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isIncluded() {
        return mIsIncluded;
    }

    @KeyFactsSection
    public String getSection() {
        return mSection;
    }

    public boolean isMandatory() {
        return mIsMandatory;
    }

    public String getPoliciyDescription() {
        return mPoliciyDescription;
    }

    public List<EHIKeyFactsPolicy> getPolicyExclusions() {
        return mPolicyExclusions;
    }

    public String getPolicyText() {
        return mPolicyText;
    }
}
