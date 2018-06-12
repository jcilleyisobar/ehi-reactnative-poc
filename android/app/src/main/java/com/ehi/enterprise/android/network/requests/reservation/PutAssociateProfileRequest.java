package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PutAssociateProfileRequest extends AbstractRequestProvider<EHIReservation> {

    private String mReservationSessionId;
    private String mIndividualId;
    private boolean mLoggedIntoEmeraldClub;

    public PutAssociateProfileRequest(String reservationSessionId, String individualId, boolean loggedIntoEmeraldClub) {
        mReservationSessionId = reservationSessionId;
        mIndividualId = individualId;
        mLoggedIntoEmeraldClub = loggedIntoEmeraldClub;
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PUT;
    }

    @Override
    public Object getRequestBody() {
        // empty body
        return new Object();
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mReservationSessionId)
                .appendSubPath(mLoggedIntoEmeraldClub ? Settings.EMERALD_CLUB : Settings.ENTERPRISE_PLUS)
                .appendSubPath(mIndividualId)
                .build();
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
