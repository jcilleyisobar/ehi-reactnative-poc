package com.ehi.enterprise.android.models.location.solr;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class EHISolrLocationValidity {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VALID_STANDARD_HOURS, VALID_AFTER_HOURS, INVALID_ALL_DAY, INVALID_AT_THAT_TIME})
    public @interface ValidityType {
    }

    public static final String VALID_STANDARD_HOURS = "VALID_STANDARD_HOURS";
    public static final String VALID_AFTER_HOURS = "VALID_AFTER_HOURS";
    public static final String INVALID_ALL_DAY = "INVALID_ALL_DAY";
    public static final String INVALID_AT_THAT_TIME = "INVALID_AT_THAT_TIME";

    public boolean isLocationInvalid() {
        return mValidityType.equalsIgnoreCase(INVALID_ALL_DAY)
                || mValidityType.equalsIgnoreCase(INVALID_AT_THAT_TIME);
    }

    public boolean isInvalidAtTime() {
        return mValidityType.equalsIgnoreCase(INVALID_AT_THAT_TIME);
    }

    public boolean isAllDayClosed() {
        return mValidityType.equalsIgnoreCase(INVALID_ALL_DAY);
    }

    @SerializedName("validityType")
    private String mValidityType;

    @SerializedName("locationHours")
    private EHISolrWorkingDayInfo mLocationHours;

    public @ValidityType String getValidityType() {
        return mValidityType;
    }

    public EHISolrWorkingDayInfo getLocationHours() {
        return mLocationHours;
    }

    public boolean isAfterHours() {
        return  mValidityType.equalsIgnoreCase(VALID_AFTER_HOURS);
    }


    public void setValidityType(@ValidityType String validityType) {
        mValidityType = validityType;
    }

    public List<EHISolrTimeSpan> getStandardOpenCloseHours() {
        if (mLocationHours == null) {
            return null;
        }
        return mLocationHours.getStandardOpenCloseHours();
    }
}
