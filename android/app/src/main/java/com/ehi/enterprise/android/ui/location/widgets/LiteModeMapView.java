package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LiteModeMapViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.google.android.m4b.maps.CameraUpdateFactory;
import com.google.android.m4b.maps.GoogleMap;
import com.google.android.m4b.maps.MapsInitializer;
import com.google.android.m4b.maps.OnMapReadyCallback;
import com.google.android.m4b.maps.model.BitmapDescriptorFactory;
import com.google.android.m4b.maps.model.CameraPosition;
import com.google.android.m4b.maps.model.LatLng;
import com.google.android.m4b.maps.model.Marker;
import com.google.android.m4b.maps.model.MarkerOptions;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(LiteModeMapViewModel.class)
public class LiteModeMapView extends DataBindingViewModelView<LiteModeMapViewModel, LiteModeMapViewBinding>
        implements OnMapReadyCallback {

    private static final String TAG = "LiteModeMapView";

    private LatLng mLocation;

    @DrawableRes
    private int mPinIcon;

    private Marker mMarker;
    private GoogleMap mGoogleMap;

    public LiteModeMapView(Context context) {
        this(context, null, 0);
    }

    public LiteModeMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiteModeMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_lite_mode_map);
            initViews();
        }
    }

    private void initViews() {
        getViewBinding().mapView.onCreate(null);
    }

    public void showLocationOnMap(@NonNull LatLng location, @DrawableRes int pinIcon) {
        mLocation = location;
        mPinIcon = pinIcon;
        MapsInitializer.initialize(getContext());
        if (mGoogleMap == null) {
            getViewBinding().mapView.getMapAsync(this);
        } else {
            addMarkerForLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setPadding(0, (int) getResources().getDimension(R.dimen.toolbar_height), 0, 0);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        addMarkerForLocation();

    }

    private void addMarkerForLocation() {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(mPinIcon))
                .position(mLocation));

        CameraPosition cp = new CameraPosition.Builder()
                .target(mMarker.getPosition())
                .zoom(15)
                .build();

        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    public void onCreate(Bundle bundle) {
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onCreate(bundle);
        }
    }

    public void onResume() {
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onResume();
        }
    }

    public void onPause() {
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onPause();
        }
    }

    public void onDestroy() {
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onDestroy();
        }
    }


}
