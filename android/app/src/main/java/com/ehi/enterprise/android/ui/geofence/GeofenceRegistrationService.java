package com.ehi.enterprise.android.ui.geofence;

import android.content.Intent;
import android.os.Message;

import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetLocationByIdRequest;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrHoursByLocationIdRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.ui.service.BaseApiWorkerService;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.isobar.android.newinstancer.Extra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceRegistrationService extends BaseApiWorkerService {
    public static final String TAG = "GEOFENCE SERVICE";

    @Extra(value = List.class, type = EHITripSummary.class, required = false)
    public static final String CURRENT_RENTALS = "CURRENT_RENTALS";

    @Extra(value = List.class, type = EHITripSummary.class, required = false)
    public static final String UPCOMING_RENTALS = "UPCOMING_RENTALS";
    public static final String CACHE_PREFIX_DETAILS = "details";
    public static final String CACHE_PREFIX_HOURS = "hours";

    private boolean mIsCurrent;
    private boolean mLocationDetailRequestComplete;
    private boolean mSolrHoursRequestComplete;
    private Map<String, EHITripSummary> mTripMap = new HashMap<>();
    private Map<String, EHILocation> locationMap = new HashMap<>();
    private Map<String, GetSolrHoursResponse> hoursMap = new HashMap<>();

    public GeofenceRegistrationService() {
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null && intent.getExtras() != null) {
            GeofenceRegistrationServiceHelper.Extractor extractor =
                    new GeofenceRegistrationServiceHelper.Extractor(intent);

            if (extractor.currentRentals() != null) {
                sendMessage(extractor.currentRentals());
                mIsCurrent = true;
            } else if (extractor.upcomingRentals() != null) {
                sendMessage(extractor.upcomingRentals());
                mIsCurrent = false;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onMessage(final Message message) {
        DLog.d(TAG, "onMessage() called with: " + "message = [" + message + "]");
        final List<EHITripSummary> trips = (List<EHITripSummary>) message.obj;

        for (final EHITripSummary trip : trips) {
            requestLocationAndRegister(trip, mIsCurrent);
        }
    }

    private void requestLocationAndRegister(final EHITripSummary trip, final boolean isCurrent) {
        final String tripKey = isCurrent ? trip.getTicketNumber() : trip.getConfirmationNumber();

        mTripMap.put(tripKey, trip);

        final EHILocation location = isCurrent
                ? trip.getReturnLocation()
                : trip.getPickupLocation();

        if (!EHITextUtils.isEmpty(location.getTimeZoneId())) {
            onNextLocationDetail(tripKey, location, isCurrent);
        } else {
            final EHILocation requestedLocation = LocalDataManager.getInstance()
                    .getObjectFromCache(CACHE_PREFIX_DETAILS + location.getId(), EHILocation.class);

            if (requestedLocation != null) {
                onNextLocationDetail(tripKey, requestedLocation, isCurrent);
            } else {
                requestLocationDetails(isCurrent, tripKey, location);
            }
        }

        final GetSolrHoursResponse hoursResponse = LocalDataManager.getInstance()
                .getObjectFromCache(CACHE_PREFIX_HOURS + location.getId(), GetSolrHoursResponse.class);

        if (hoursResponse != null) {
            onNextSolrHours(tripKey, hoursResponse, isCurrent);
        } else {
            requestHours(trip, isCurrent, tripKey, location);
        }
    }

    private void requestLocationDetails(final boolean isCurrent, final String tripKey, final EHILocation location) {
        getApiService().performRequest(new GetLocationByIdRequest(location.getId()),
                new IApiCallback<GetLocationDetailsResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<GetLocationDetailsResponse> response) {
                        if (response.isSuccess()) {
                            onNextLocationDetail(tripKey, response.getData().getLocation(), isCurrent);
                        } else {
                            onError(response);
                        }
                    }
                });
    }

    private void requestHours(final EHITripSummary trip, final boolean isCurrent, final String tripKey, final EHILocation location) {
        getApiService().performRequest(new GetSolrHoursByLocationIdRequest(location.getId(), isCurrent ? trip.getReturnTime() : trip.getPickupTime()),
                new IApiCallback<GetSolrHoursResponse>() {
                    @Override
                    public void handleResponse(final ResponseWrapper<GetSolrHoursResponse> response) {
                        if (response.isSuccess()) {
                            onNextSolrHours(tripKey, response.getData(), isCurrent);
                        } else {
                            onError(response);
                        }
                    }
                });
    }

    private void onComplete(final String tripKey, final boolean isCurrent) {
        if (mLocationDetailRequestComplete && mSolrHoursRequestComplete) {
            final EHITripSummary ehiTripSummary = mTripMap.get(tripKey);
            final EHILocation fenceLocation = isCurrent ? ehiTripSummary.getReturnLocation() : ehiTripSummary.getPickupLocation();
            final Date fenceTime = isCurrent ? ehiTripSummary.getReturnTime() : ehiTripSummary.getPickupTime();
            final EHILocation fullLocationDetails = locationMap.get(fenceLocation.getId());
            final GetSolrHoursResponse locationHours = hoursMap.get(fenceLocation.getId());


            boolean isAfterHours = false;
            if (locationHours != null && locationHours.getDaysInfo() != null) {

                ArrayList<EHISolrWorkingDayInfo> ehiSolrWorkingDayInfos = new ArrayList<>(locationHours.getDaysInfo().values());
                if (!ehiSolrWorkingDayInfos.isEmpty()) {
                    isAfterHours = ehiSolrWorkingDayInfos.get(0).isAfterHoursAtTime(fenceTime);
                }
            }
            registerGeofence(ehiTripSummary, fullLocationDetails, isCurrent, isAfterHours);

            mLocationDetailRequestComplete = false;
            mSolrHoursRequestComplete = false;
        }
    }

    private void onNextLocationDetail(final String tripKey, EHILocation location, final boolean isCurrent) {
        LocalDataManager.getInstance().addObjectToCache(CACHE_PREFIX_DETAILS + location.getId(), location);
        locationMap.put(location.getId(), location);
        mLocationDetailRequestComplete = true;
        onComplete(tripKey, isCurrent);
    }

    private void onNextSolrHours(final String tripKey, GetSolrHoursResponse response, final boolean isCurrent) {
        if (response.getDaysInfo() == null) {
            onError("Hours is null for " + tripKey);
            return;
        }
        LocalDataManager.getInstance().addObjectToCache(CACHE_PREFIX_HOURS + response.getLocationId(), response);
        hoursMap.put(response.getLocationId(), response);
        mSolrHoursRequestComplete = true;
        onComplete(tripKey, isCurrent);
    }

    private void onError(final ResponseWrapper responseWrapper) {
        onError(responseWrapper.getMessage());
    }

    private void onError(final String message) {
        DLog.e(message);
    }

    private void registerGeofence(final EHITripSummary trip,
                                  final EHILocation location,
                                  final boolean isCurrent,
                                  final boolean isAfterHours) {
        EHIGeofence.Builder builder = new EHIGeofence.Builder();
        if (isCurrent) {
            builder.forCurrentTrip(trip);
        } else {
            builder.forUpcomingTrip(trip);
        }

        builder.atLocation(location)
                .withRadius(location.isAirport() && !isCurrent ? 5000 : 100)
                .isAfterHours(isAfterHours)
                .build()
                .register(this, GeofenceManager.getInstance());
    }
}
