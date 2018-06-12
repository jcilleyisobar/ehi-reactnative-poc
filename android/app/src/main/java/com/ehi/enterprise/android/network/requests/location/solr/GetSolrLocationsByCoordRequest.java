package com.ehi.enterprise.android.network.requests.location.solr;

import android.util.SparseArray;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.headers.ApiHeaderBuilder;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsResponse;
import com.ehi.enterprise.android.network.util.EHIUrlBuilder;
import com.ehi.enterprise.android.utils.DistanceUtils;
import com.ehi.enterprise.android.utils.filters.EHIFilter;
import com.ehi.enterprise.android.utils.filters.EHIFilterList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class GetSolrLocationsByCoordRequest extends AbstractRequestProvider<GetSolrLocationsResponse> {
    private Date mDropoffDate;
    private Date mDropoffTime;
    private Date mPickupDate;
    private Date mPickupTime;
    private double mLatitude;
    private double mLongitude;
    private long mRadius; // in km
    private boolean mDropOffLocation = false;
    private SparseArray<EHIFilter> mFilters;

    private static final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public GetSolrLocationsByCoordRequest(double latitude,
                                          double longitude,
                                          long radius,
                                          boolean dropOffLocation,
                                          SparseArray<EHIFilter> filters,
                                          Date pickupDate,
                                          Date pickupTime,
                                          Date dropoffDate,
                                          Date dropoffTime) {
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mDropOffLocation = dropOffLocation;
        mFilters = filters;
        mPickupDate = pickupDate;
        mPickupTime = pickupTime;
        mDropoffDate = dropoffDate;
        mDropoffTime = dropoffTime;
    }

    @Override
    public String getEndpointUrl() {
        return Settings.SOLR_ENDPOINT_URL;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.GET;
    }

    @Override
    public String getRequestUrl() {

        EHIUrlBuilder bld = new EHIUrlBuilder()
                .appendSubPath("spatial")
                .appendSubPath(DistanceUtils.round(mLatitude, 2))
                .appendSubPath(DistanceUtils.round(mLongitude, 2))
                .addQueryParam("fallback", Settings.SOLR_FALLBACK_LANGUAGE)
                .addQueryParam("locale", Locale.getDefault().toString())
                .addQueryParam("includeExotics", true);

        if (mPickupDate != null) {
            bld.addQueryParam("pickupDate", getFormattedDate(mPickupDate));
        }

        if (mPickupTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            mPickupTime = cal.getTime();
        }
        bld.addQueryParam("pickupTime", getFormattedTime(mPickupTime));

        if (mDropoffDate != null) {
            bld.addQueryParam("dropoffDate", getFormattedDate(mDropoffDate));
        }

        if (mDropoffTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            mDropoffTime = cal.getTime();
        }
        bld.addQueryParam("dropoffTime", getFormattedTime(mDropoffTime));

        if (mFilters.size() != 0) {
            bld.addQueryParam("brand", Settings.SOLR_BRAND);
        }

        if (mFilters.get(EHIFilterList.LOC_FILTER_OPEN_SUNDAY) != null) {
            bld.addQueryParam("openSundays", true);
        }

        if (mFilters.get(EHIFilterList.LOC_FILTER_OPEN_24) != null) {
            bld.addQueryParam("only24Hour", true);
        }

        if (mRadius > 0) {
            bld.addQueryParam("radius", mRadius);
        }


        StringBuilder locationBuilder = new StringBuilder();

        locationBuilder.append((mFilters.get(EHIFilterList.LOC_FILTER_PORT_STATION) != null) ? "PORT_OF_CALL" : "");

        locationBuilder.append((mFilters.get(EHIFilterList.LOC_FILTER_RAIL_STATION) != null) ?
                (((locationBuilder.length() != 0) ? "," : "") + "RAIL") : "");

        locationBuilder.append((mFilters.get(EHIFilterList.LOC_FILTER_TYPE_AIRPORT) != null) ?
                (((locationBuilder.length() != 0) ? "," : "") + "AIRPORT") : "");
        if (locationBuilder.length() != 0) {
            bld.addQueryParam("locationTypes", locationBuilder.toString());
        }

        if (mFilters.get(EHIFilterList.LOC_FILTER_TYPE_EXOTIC) != null) {
            bld.addQueryParam("attributes", "exotics");
        } else if (mFilters.get(EHIFilterList.LOC_FILTER_TYPE_TRUCK_RENTALS) != null) {
            bld.addQueryParam("attributes", "trucks");
        }
        if (mDropOffLocation) {
            bld.addQueryParam("oneWay", mDropOffLocation);
        }
        return bld.build();
    }

    private String getFormattedDate(Date date) {
        return mDateFormat.format(date);
    }

    private String getFormattedTime(Date time) {
        return mTimeFormat.format(time);
    }

    @Override
    public Map<String, String> getHeaders() {
        return ApiHeaderBuilder.solrDefaultHeaders()
                .build();
    }

    @Override
    public Class<GetSolrLocationsResponse> getResponseClass() {
        return GetSolrLocationsResponse.class;
    }

}
