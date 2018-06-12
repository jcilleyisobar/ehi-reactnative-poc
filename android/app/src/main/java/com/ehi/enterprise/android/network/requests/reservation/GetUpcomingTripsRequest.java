package com.ehi.enterprise.android.network.requests.reservation;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.reservation.GetUpcomingTripsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;

import java.util.Date;
import java.util.Map;

public class GetUpcomingTripsRequest extends AbstractRequestProvider<GetUpcomingTripsResponse> {
    public static final int DEFAULT_START_RECORD_NUMBER = 1;
    @NonNull private String mUserId;
    @NonNull private final Date mSearchEndDate;
    private boolean mPreWriteTicketRequested;
    private int mStartRecordNumber;
    private int mRecordCount;

    public GetUpcomingTripsRequest(@NonNull String userId,
                                   @NonNull Date searchEndDate,
                                   boolean preWriteTicketRequested,
                                   int recordCount) {
        this(userId, searchEndDate, preWriteTicketRequested, DEFAULT_START_RECORD_NUMBER, recordCount);
    }

    public GetUpcomingTripsRequest(@NonNull String userId,
                                   @NonNull Date searchEndDate,
                                   boolean preWriteTicketRequested,
                                   int startRecordNumber,
                                   int recordCount) {
        mUserId = userId;
        mSearchEndDate = searchEndDate;
        mPreWriteTicketRequested = preWriteTicketRequested;
        mStartRecordNumber = startRecordNumber;
        mRecordCount = recordCount;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {
        return new EHIUrlBuilder()
                .appendSubPath(Settings.EHI_GBO_ENDPOINT_API)
                .appendSubPath("trips")
                .appendSubPath(Settings.BRAND)
                .appendSubPath(Settings.CHANNEL)
                .appendSubPath(mUserId)
                .appendLastSubPath("upcoming")
                .addQueryParam("preWriteTicketRequested", mPreWriteTicketRequested)
                .addQueryParam("searchEndDate", mSearchEndDate)
                .addQueryParam("startRecordNumber", mStartRecordNumber)
                .addQueryParam("recordCount", mRecordCount)
                .build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder
                .gboDefaultHeaders()
                .build();
    }

    @Override
    protected HostType getHost() {
        return HostType.GBO_RENTAL;
    }

    @Override
    public Class<GetUpcomingTripsResponse> getResponseClass() {
        return GetUpcomingTripsResponse.class;
    }

}