package com.ehi.enterprise.android.network.responses.reservation;


import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class CarClassDetailsResponse extends BaseResponse{

    @SerializedName("car_class_details")
    private EHICarClassDetails details;

    public EHICarClassDetails getDetails() {
        return details;
    }
}
