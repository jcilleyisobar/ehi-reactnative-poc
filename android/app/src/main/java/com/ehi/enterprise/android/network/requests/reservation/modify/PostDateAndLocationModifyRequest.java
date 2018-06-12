package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.modify.PostDateAndLocationRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Date;
import java.util.Map;

public class PostDateAndLocationModifyRequest extends AbstractRequestProvider<EHIReservation> {

    private String mReservationSessionId;
    private String mPickupLocationId;
    private String mReturnLocationId;
    private Date mPickupDate;
    private Date mReturnDate;

    public PostDateAndLocationModifyRequest(String reservationSessionId,
                                            String pickupLocationId,
                                            String returnLocationId,
                                            Date pickupDate,
                                            Date returnDate) {
        mReservationSessionId = reservationSessionId;
        mPickupLocationId = pickupLocationId;
        mReturnLocationId = returnLocationId;
        mPickupDate = pickupDate;
        mReturnDate = returnDate;
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
                .appendSubPath("modify")
                .appendSubPath(mReservationSessionId)
                .appendSubPath("initiate")
                .build();
    }

    @Override
    public Object getRequestBody() {
        return new PostDateAndLocationRequestParams(mPickupLocationId,
                mReturnLocationId,
                mPickupDate,
                mReturnDate);
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
