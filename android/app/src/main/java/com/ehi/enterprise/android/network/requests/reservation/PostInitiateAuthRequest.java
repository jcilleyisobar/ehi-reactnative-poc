package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.InitiateAuthRequestBody;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostInitiateAuthRequest extends AbstractRequestProvider<EHIReservation> {

    private String mPickupLocationId;
    private String mReturnLocationId;
    private Date mPickupTime;
    private Date mReturnTime;
    private String mContractNumber;
    private String mAuthPin;
    private String mCountryOfResidenceCode;
    private String mTripPurpose;
    private List<EHIAdditionalInformation> mEhiAdditionalInformationList;

    public PostInitiateAuthRequest(String pickupLocationId,
                                   String returnLocationId,
                                   Date pickupTime,
                                   Date returnTime,
                                   String contractNumber,
                                   String authPin,
                                   String countryOfResidenceCode,
                                   String tripPurpose,
                                   List<EHIAdditionalInformation> ehiAdditionalInformationList) {
        mPickupLocationId = pickupLocationId;
        mReturnLocationId = returnLocationId;
        mPickupTime = pickupTime;
        mReturnTime = returnTime;
        mContractNumber = contractNumber;
        mAuthPin = authPin;
        mCountryOfResidenceCode = countryOfResidenceCode;
        mTripPurpose = tripPurpose;
        mEhiAdditionalInformationList = ehiAdditionalInformationList;
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("initiate")
                .build();
    }

    @Override
    public Object getRequestBody() {
        return new InitiateAuthRequestBody(
                mPickupLocationId,
                mReturnLocationId,
                mPickupTime,
                mReturnTime,
                mContractNumber,
                mAuthPin,
                mCountryOfResidenceCode,
                mTripPurpose,
                mEhiAdditionalInformationList
        );
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    public Class<EHIReservation> getResponseClass() {
        return EHIReservation.class;
    }

}