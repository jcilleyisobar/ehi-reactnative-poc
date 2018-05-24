package com.ehi.enterprise.android.network.request_params.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class InitiateUnAuthRequestBody {

    @SerializedName("pickup_location_id")
    private String mPickupLocationId;

    @SerializedName("return_location_id")
    private String mReturnLocationId;

    @SerializedName("pickup_time")
    private Date mPickupTime;

    @SerializedName("return_time")
    private Date mReturnTime;

    @SerializedName("renter_age")
    private int mRenterAge;

    @SerializedName("contract_number")
    private String mContractNumber;

    @SerializedName("auth_pin")
    private String mAuthPin;

    @SerializedName("country_of_residence_code")
    private String mCountryOfResidence;

    @SerializedName("travel_purpose")
    private String mTripPurpose;

    @SerializedName("additional_information")
    private List<EHIAdditionalInformation> mAdditionalInformation;

    @SerializedName("enable_north_american_prepay_rates")
    private final boolean mEnablePrepaNARates = true;

    public InitiateUnAuthRequestBody(String pickupLocationId,
                                     String returnLocationId,
                                     Date pickupTime,
                                     Date returnTime,
                                     int renterAge,
                                     String contractNumber,
                                     String authPin,
                                     String countryOfResidence,
                                     String tripPurpose,
                                     List<EHIAdditionalInformation> additionalInformation) {

        mPickupLocationId = pickupLocationId;
        mReturnLocationId = returnLocationId;
        mPickupTime = pickupTime;
        mReturnTime = returnTime;
        mRenterAge = renterAge;
        mContractNumber = contractNumber;
        mAuthPin = authPin;
        mCountryOfResidence = countryOfResidence;
        mAdditionalInformation = additionalInformation;
        mTripPurpose = tripPurpose;
    }

}