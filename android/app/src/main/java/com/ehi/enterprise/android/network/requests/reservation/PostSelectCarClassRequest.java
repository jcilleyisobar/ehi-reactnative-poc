package com.ehi.enterprise.android.network.requests.reservation;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.request_params.reservation.CarClassDetailsRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Map;

public class PostSelectCarClassRequest extends AbstractRequestProvider<EHIReservation> {

    private String mReservationSessionId;
    private String mCarClassCode;
    private boolean mSkipUpgradeSearch;
    private int mRedemptionDayCount;
    private boolean mPrepaySelected = false;

    public PostSelectCarClassRequest(String reservationSessionId, String carClassCode, boolean skipUpgradeSearch) {
        this(reservationSessionId, carClassCode, skipUpgradeSearch, 0);
    }

    public PostSelectCarClassRequest(String reservationSessionId, String carClassCode, boolean skipUpgradeSearch, int redemptionDayCount) {
        mReservationSessionId = reservationSessionId;
        mCarClassCode = carClassCode;
        mSkipUpgradeSearch = skipUpgradeSearch;
        mRedemptionDayCount = redemptionDayCount;
    }

    public PostSelectCarClassRequest(String reservationSessionId, String carClassCode, boolean skipUpgradeSearch, int redemptionDayCount, boolean prepaySelected) {
        mReservationSessionId = reservationSessionId;
        mCarClassCode = carClassCode;
        mSkipUpgradeSearch = skipUpgradeSearch;
        mRedemptionDayCount = redemptionDayCount;
        mPrepaySelected = prepaySelected;
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
                .appendSubPath(mReservationSessionId)
                .appendSubPath("selectCarClass")
                .build();
    }

    @Override
    public Object getRequestBody() {
        return new CarClassDetailsRequestParams(mCarClassCode, mSkipUpgradeSearch, mRedemptionDayCount, mPrepaySelected);
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