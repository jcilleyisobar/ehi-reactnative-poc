package com.ehi.enterprise.android.network.requests.reservation.modify;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.modify.SelectCarClassModifyRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostSelectCarClassModifyRequest extends AbstractRequestProvider<EHIReservation> {

    private String mReservationSessionId;
    private String mCarClassCode;
    private int mRedemptionDayCount;
    private boolean mPrepaySelected = false;

    public PostSelectCarClassModifyRequest(String reservationSessionId, String carClassCode, int redemptionDayCount, boolean prepaySelected) {
        mReservationSessionId = reservationSessionId;
        mCarClassCode = carClassCode;
        mRedemptionDayCount = redemptionDayCount;
        mPrepaySelected = prepaySelected;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.POST;
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder().appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("reservations")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath("modify")
                .appendSubPath(mReservationSessionId)
                .appendSubPath("selectCarClass")
                .build();
    }

    @Override
    public Object getRequestBody() {
        return new SelectCarClassModifyRequestParams(mCarClassCode, mRedemptionDayCount, mPrepaySelected);
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