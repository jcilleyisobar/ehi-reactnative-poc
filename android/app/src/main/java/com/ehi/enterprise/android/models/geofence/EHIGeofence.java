package com.ehi.enterprise.android.models.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.geofence.GeofenceIntentService;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class EHIGeofence extends EHIModel {
    private static final long DEFAULT_RADIUS_IN_METERS = 1000;
    private static final long DEFAULT_EXPIRATION_DELAY = TimeUnit.HOURS.toMillis(2);
    @SerializedName("id")
    private String mId;
    @SerializedName("latlng")
    private EHILatLng mEHILatLng;
    @SerializedName("isCurrent")
    private boolean mIsCurrent;
    @SerializedName("radius")
    private float mRadius;
    @SerializedName("expirationMillis")
    private long mExpirationMillis;
    @SerializedName("locationName")
    private String mLocationName;
    @SerializedName("scheduledTime")
    private long mScheduledTime;
    @SerializedName("afterHoursPolicy")
    private EHIPolicy mAfterHoursPolicy;
    @SerializedName("isAfterHours")
    private boolean mIsAfterHours;
    @SerializedName("isAirport")
    private boolean mIsAirport;
    @SerializedName("wayfindings")
    private List<EHIWayfindingStep> mWayfindingSteps;


    private EHIGeofence(final String id,
                        final String locationName,
                        final EHILatLng EHILatLng,
                        final long expirationMillis,
                        final long scheduledTime,
                        final EHIPolicy afterHoursPolicy,
                        final boolean isAirport,
                        final List<EHIWayfindingStep> wayfindingSteps,
                        final boolean isAfterHours,
                        final float radius,
                        final boolean isCurrent) {
        mEHILatLng = EHILatLng;
        mExpirationMillis = expirationMillis;
        mAfterHoursPolicy = afterHoursPolicy;
        mIsAirport = isAirport;
        mWayfindingSteps = wayfindingSteps;
        mIsAfterHours = isAfterHours;
        mIsCurrent = isCurrent;
        mId = id;
        mRadius = radius;
        mLocationName = locationName;
        mScheduledTime = scheduledTime;
    }

    public EHILatLng getEHILatLng() {
        return mEHILatLng;
    }

    public boolean isCurrent() {
        return mIsCurrent;
    }

    public String getId() {
        return mId;
    }

    public float getRadius() {
        return mRadius;
    }

    public long getExpirationMillis() {
        return mExpirationMillis;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public long getScheduledTime() {
        return mScheduledTime;
    }

    public EHIPolicy getAfterHoursPolicy() {
        return mAfterHoursPolicy;
    }

    public boolean isAirport() {
        return mIsAirport;
    }

    public boolean isAfterHours() {
        return mIsAfterHours;
    }

    public List<EHIWayfindingStep> getWayfindingSteps() {
        return mWayfindingSteps;
    }

    public void register(@NonNull Context context, final GeofenceManager geofenceManager) {
        DLog.d("Begin geofence registration: " + toString());

        if (isInvalid()) {
            return;
        }

        deRegister(geofenceManager);
        geofenceManager.addGeofence(this, getGeofencePendingIntent(context));
    }

    public void deRegister(final GeofenceManager geofenceManager) {
        geofenceManager.removeGeofence(this);
    }

    public PendingIntent getGeofencePendingIntent(@NonNull Context context) {
        return PendingIntent.getService(context, 0, new Intent(context, GeofenceIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public boolean isInvalid() {
        return TextUtils.isEmpty(mId);
    }

    public static final class Builder {
        private EHITripSummary mTripSummary;
        private boolean mIsCurrent;
        private float mRadius = DEFAULT_RADIUS_IN_METERS;
        private EHILocation mLocation;
        private long mExpirationDelay = DEFAULT_EXPIRATION_DELAY;
        private boolean mIsAfterHours;

        public final Builder forCurrentTrip(final EHITripSummary tripSummary) {
            mTripSummary = tripSummary;
            mIsCurrent = true;
            return this;
        }

        public final Builder forUpcomingTrip(final EHITripSummary tripSummary) {
            mTripSummary = tripSummary;
            mIsCurrent = false;
            return this;
        }

        public final Builder withRadius(final float radius) {
            mRadius = radius;
            return this;
        }

        public final Builder setExpirationDelay(final long expirationDelay) {
            mExpirationDelay = expirationDelay;
            return this;
        }

        public final Builder atLocation(final EHILocation location) {
            mLocation = location;
            return this;
        }

        public final Builder isAfterHours(final boolean isAfterHours) {
            mIsAfterHours = isAfterHours;
            return this;
        }

        public final EHIGeofence build() {
            final Date now = new Date();
            if (mLocation == null || mTripSummary == null) {
                throw new IllegalStateException("Location and Trip Summary cannot be null!");
            }
            return new EHIGeofence(
                    mIsCurrent ? mTripSummary.getTicketNumber() : mTripSummary.getConfirmationNumber(),
                    mIsCurrent ? mTripSummary.getReturnLocation().getName() : mTripSummary.getPickupLocation().getName(),
                    mIsCurrent ? mTripSummary.getReturnLocation().getGpsCoordinates() : mTripSummary.getPickupLocation().getGpsCoordinates(),
                    (mIsCurrent ? mTripSummary.getReturnTime().getTime() - now.getTime() : mTripSummary.getPickupTime().getTime() - now.getTime()) + mExpirationDelay,
                    mIsCurrent ? mTripSummary.getReturnTime().getTime() : mTripSummary.getPickupTime().getTime(),
                    mLocation.getAfterHoursPolicy(),
                    mLocation.isAirport(),
                    mLocation.getWayfindings(),
                    mIsAfterHours,
                    mRadius,
                    mIsCurrent
            );
        }

    }

    @Override
    public String toString() {
        return "EHIGeofence{" +
                "mId='" + mId + '\'' +
                ", mEHILatLng=" + mEHILatLng +
                ", mIsCurrent=" + mIsCurrent +
                ", mRadius=" + mRadius +
                ", mExpirationMillis=" + mExpirationMillis +
                ", mLocationName='" + mLocationName + '\'' +
                ", mScheduledTime=" + mScheduledTime +
                ", mAfterHoursPolicy=" + mAfterHoursPolicy +
                ", mIsAfterHours=" + mIsAfterHours +
                ", mIsAirport=" + mIsAirport +
                ", mWayfindingSteps=" + mWayfindingSteps +
                '}';
    }
}
