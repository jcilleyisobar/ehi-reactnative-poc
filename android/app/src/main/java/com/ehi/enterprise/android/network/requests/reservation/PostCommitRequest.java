package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.List;
import java.util.Map;

public class PostCommitRequest extends AbstractRequestProvider<EHIReservation> {

    private CommitRequestParams mRequestParams;
    private String mReservationSessionId;

    public PostCommitRequest(String reservationSessionId,
                             EHIDriverInfo driverInfo,
                             List<EHIAdditionalInformation> additionalInformation,
                             List paymentIds,
                             String billingAccount,
                             String billingType,
                             EHIAirlineInformation airlineInformation,
                             String tripPurpose, String paRes) {
        mReservationSessionId = reservationSessionId;
        mRequestParams = new CommitRequestParams(driverInfo, additionalInformation, paymentIds, billingAccount, billingType, airlineInformation, tripPurpose, paRes);
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
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mReservationSessionId)
                .appendSubPath("commit")
                .build();
    }

    @Override
    public Object getRequestBody() {
        return mRequestParams;
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