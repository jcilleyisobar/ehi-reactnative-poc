package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIAirlineInformation extends EHIModel {

    @SerializedName("code")
    private String mCode;

    @SerializedName("flight_number")
    private String mFlightNumber;

    public String getCode() {
        return mCode;
    }

    public String getFlightNumber() {
        return mFlightNumber;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public void setFlightNumber(String flightNumber) {
        mFlightNumber = flightNumber;
    }
}