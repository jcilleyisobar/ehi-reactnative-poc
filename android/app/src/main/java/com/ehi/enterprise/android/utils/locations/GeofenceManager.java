package com.ehi.enterprise.android.utils.locations;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.manager.BaseDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeofenceManager extends BaseDataManager
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String GEOFENCES = "GEOFENCES";
    private static GeofenceManager sInstance;
    private GoogleApiClient mGoogleApiClient;
    private boolean mEnabled;
    private List<EHIGeofence> mGeofences;
    private List<EHIGeofence> mGeofencesToRemove;
    private PendingIntent mGeofencePendingIntent;

    public void initialize(@NonNull final Context context, boolean enabled) {
        super.initialize(context);
        mEnabled = enabled;
        sInstance = this;

        if (mEnabled) {
            initLocationProvider();
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;
        if (mEnabled) {
            initLocationProvider();
        }
    }

    public static GeofenceManager getInstance() {
        if (sInstance == null) {
            sInstance = new GeofenceManager();
        }
        return sInstance;
    }

    private void initLocationProvider() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    protected String getSharedPreferencesName() {
        return GEOFENCES;
    }

    @Override
    public void onConnected(final Bundle bundle) {
        if (!ListUtils.isEmpty(mGeofences)) {
            if (!ListUtils.isEmpty(mGeofencesToRemove)) {
                deRegisterGeofences();
            }

            registerGeofences();
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }

    public void addGeofence(EHIGeofence geofence, PendingIntent pendingIntent) {
        if (mGeofencePendingIntent == null) {
            mGeofencePendingIntent = pendingIntent;
        }

        if (mGeofences == null) {
            mGeofences = new ArrayList<>();
        }

        mGeofences.add(geofence);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            registerGeofences();
        }
    }

    public void removeAllGeofences() {
        for (final EHIGeofence ehiGeofence : getAllGeofences()) {
            removeGeofence(ehiGeofence);
        }
    }

    public void removeGeofence(final EHIGeofence geofence) {
        if (!ListUtils.isEmpty(mGeofences) && mGeofences.contains(geofence)) {
            mGeofences.remove(geofence);
        }

        if (mGeofencesToRemove == null) {
            mGeofencesToRemove = new ArrayList<>();
        }

        mGeofencesToRemove.add(geofence);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            deRegisterGeofences();
        }
    }

    private void registerGeofences() {
        if (mGeofences != null && !mGeofences.isEmpty()) {
            final List<Geofence> geofences = new ArrayList<>();
            for (final EHIGeofence ehiGeofence : mGeofences) {
                set(ehiGeofence.getId(), ehiGeofence);
                geofences.add(getGeofence(ehiGeofence));
            }

            final GeofencingRequest geofencingRequest
                    = new GeofencingRequest.Builder().addGeofences(geofences)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build();
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest, mGeofencePendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(final Status status) {
                            DLog.d("Register: " + status.toString());
                        }
                    });
            mGeofences.clear();
        }
    }

    private void deRegisterGeofences() {
        final List<String> requestIds = new ArrayList<>();
        for (final EHIGeofence geofence : mGeofencesToRemove) {
            requestIds.add(geofence.getId());
            remove(geofence.getId());
        }

        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, requestIds)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(final Status status) {
                        DLog.d("Deregister: " + status.toString());
                    }
                });
        mGeofencesToRemove.clear();
    }

    @NonNull
    private Geofence getGeofence(EHIGeofence ehiGeofence) {
        return new Geofence.Builder().setRequestId(ehiGeofence.getId())
                .setCircularRegion(ehiGeofence.getEHILatLng().getLatitude(),
                        ehiGeofence.getEHILatLng().getLongitude(),
                        ehiGeofence.getRadius())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(ehiGeofence.getExpirationMillis())
                .build();
    }

    public EHIGeofence getEHIGeofence(final String id) {
        return getEhiModel(id, EHIGeofence.class);
    }

    public List<EHIGeofence> getAllGeofences() {
        final List<EHIGeofence> ehiGeofences = new ArrayList<>();
        Map<String, ?> all = getAll();
        for (String key : all.keySet()) {
            if (getEHIGeofence(key) != null) {
                ehiGeofences.add(getEHIGeofence(key));
            }
        }

        return ehiGeofences;
    }
}
