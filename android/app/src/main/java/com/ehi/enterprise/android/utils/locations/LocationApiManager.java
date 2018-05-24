package com.ehi.enterprise.android.utils.locations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ehi.enterprise.android.utils.DLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.ref.WeakReference;

public class LocationApiManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationApiManager.class.getSimpleName();

    private static final int REQUEST_CHECK_SETTINGS = 112;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastCurrentLocation;
    private LocationRequest mLocationRequest;
    private WeakReference<OnLastLocationsListener> mWeakListener = new WeakReference<>(null);

    private static LocationApiManager sSingletonInstance;
    private boolean mEnabled;
    private boolean mInitialized = false;
    private LocationApiCallback mCallback;

    public static LocationApiManager getInstance() {
        if (sSingletonInstance == null) {
            sSingletonInstance = new LocationApiManager();
        }
        return sSingletonInstance;
    }

    protected LocationApiManager() {
    }

    public void initialize(Context context, boolean enabled,
                           final LocationApiCallback callback, final OnLastLocationsListener listener) {
        mCallback = callback;
        mContext = context;
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mEnabled = enabled;
        mWeakListener = new WeakReference<>(listener);
        if (mEnabled) {
            initLocationProvider();
            mInitialized = true;
        }
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
    public void onConnected(Bundle bundle) {
        requestLastLocation(mWeakListener.get());
        if (mCallback != null) {
            mCallback.onConnected();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mWeakListener != null
                && mWeakListener.get() != null) {
            mWeakListener.get().onLastLocationFetched(null);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastCurrentLocation = location;
        if (mWeakListener != null
                && mWeakListener.get() != null) {
            mWeakListener.get().onLastLocationFetched(mLastCurrentLocation);
        }
        mGoogleApiClient.disconnect();
    }

    public void requestLastLocation(OnLastLocationsListener listener) {
        mWeakListener = new WeakReference<>(listener);
        if (mGoogleApiClient.isConnected()) {
            try {
                mLastCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e){
                DLog.e(TAG,e);
            }
            if (mLastCurrentLocation == null || System.currentTimeMillis() - mLastCurrentLocation.getTime() > 60 * 1000) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationApiManager.this);
                } catch (SecurityException e){
                    DLog.e(TAG,e);
                }
            } else {
                if (mWeakListener != null
                        && mWeakListener.get() != null) {
                    mWeakListener.get().onLastLocationFetched(mLastCurrentLocation);
                }
            }
        } else {
            mGoogleApiClient.connect();
        }

    }

    public Location getLastCurrentLocation() {
        return mLastCurrentLocation;
    }

    public void checkAvailibilityAndRequestLastLocation(FragmentActivity activity, OnLastLocationsListener listener) {
        mWeakListener = new WeakReference<>(listener);
        checkGpsEnabled(activity);
    }

    private void checkGpsEnabled(final FragmentActivity activity) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        requestLastLocation(mWeakListener.get());
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        requestLastLocation(mWeakListener.get());
                        break;
                    case Activity.RESULT_CANCELED:
                        if (mWeakListener != null
                                && mWeakListener.get() != null) {
                            mWeakListener.get().onLastLocationFetched(null);
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public boolean isInitialized() {
        return mInitialized;
    }


    public interface LocationApiCallback {
        void onConnected();
    }
}
