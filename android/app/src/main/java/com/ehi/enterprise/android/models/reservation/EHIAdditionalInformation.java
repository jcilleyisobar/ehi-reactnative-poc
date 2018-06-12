package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHIAdditionalInformation extends EHIModel {

    @SerializedName("id")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("value")
    private String mValue;

    @SerializedName("required_status")
    private String mRequiredStatus;

    @SerializedName("placeholder_text")
    private String mPlaceHolderText;

    @SerializedName("helper_text")
    private String mHelperText;

    @SerializedName("type")
    private String mType;

    @SerializedName("required")
    private Boolean mRequired;

    @SerializedName("modifiable")
    private Boolean mModifiable;

    @SerializedName("sequence")
    private Integer mSequence;

    @SerializedName("display_on_splash")
    private Boolean mDisplayOnDash;

    @SerializedName("supported_values")
    private List<EHISupportedValues> mSupportedValues;

    @SerializedName("validate_additional_info")
    private Boolean mPreRateInfo;

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    public String getType() {
        return mType;
    }

    public boolean isRequired() {
        return mRequired != null ? mRequired : false;
    }

    public boolean isModifiable() {
        return mModifiable != null ? mModifiable : false;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public Integer getSequence() {
        return mSequence != null ? mSequence : 0;
    }

    public boolean isDisplayOnDash() {
        return mDisplayOnDash != null ? mDisplayOnDash : false;
    }

    public String getRequiredStatus() {
        return mRequiredStatus;
    }

    public String getPlaceHolderText() {
        return mPlaceHolderText;
    }

    public List<EHISupportedValues> getSupportedValues() {
        return mSupportedValues;
    }

    public String getHelperText() {
        return mHelperText;
    }

    public void setHelperText(String helperText) {
        mHelperText = helperText;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean isPreRateInfo() {
        return mPreRateInfo != null ? mPreRateInfo : false;
    }
}