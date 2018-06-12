package com.ehi.enterprise.android.models.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHICarFilter extends EHIModel{

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TRANSMISSION_NAME, PASSENGER_NAME})
    public @interface FILTER_NAMES{}

    public static final String TRANSMISSION_NAME = "TRANSMISSION";
    public static final String PASSENGER_NAME = "PASSENGERS";

    public static final String TRANSMISSION_AUTOMATIC = "25";
    public static final String TRANSMISSION_MANUAL = "26";

    @FILTER_NAMES
    @SerializedName("filter_name")
    private String mFilterName;

    @SerializedName("filter_code")
    private String mFilterCode;

    public String getFilterName() {
        return mFilterName;
    }

    public String getFilterCode() {
        return mFilterCode;
    }

}
