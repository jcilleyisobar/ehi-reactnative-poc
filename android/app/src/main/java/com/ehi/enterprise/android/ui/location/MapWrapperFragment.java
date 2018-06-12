package com.ehi.enterprise.android.ui.location;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MapWrapperFragmentBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.interfaces.IMapDragDelegate;
import com.ehi.enterprise.android.ui.location.interfaces.ISearchByLocationDelegate;
import com.ehi.enterprise.android.ui.location.widgets.AfterHoursReturnDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.widgets.components.LocationMapBubbleDetailsView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.google.android.m4b.maps.CameraUpdate;
import com.google.android.m4b.maps.CameraUpdateFactory;
import com.google.android.m4b.maps.GoogleMap;
import com.google.android.m4b.maps.MapsInitializer;
import com.google.android.m4b.maps.OnMapReadyCallback;
import com.google.android.m4b.maps.Projection;
import com.google.android.m4b.maps.model.BitmapDescriptorFactory;
import com.google.android.m4b.maps.model.CameraPosition;
import com.google.android.m4b.maps.model.LatLng;
import com.google.android.m4b.maps.model.Marker;
import com.google.android.m4b.maps.model.MarkerOptions;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ViewModel(ManagersAccessViewModel.class)
public class MapWrapperFragment
        extends DataBindingViewModelFragment<ManagersAccessViewModel, MapWrapperFragmentBinding>
        implements OnMapReadyCallback {

    public static final String SCREEN_NAME = "MapWrapperFragment";
    private static final String TAG = MapWrapperFragment.class.getSimpleName();

    @Extra(value = boolean.class, required = false)
    public static final String EXTRA_SEARCH_NEARBY = "ehi.EXTRA_SEARCH_NEARBY";
    @Extra(value = LatLng.class, required = false)
    public static final String EXTRA_START_POINT = "ehi.EXTRA_START_POINT";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_DATE = "ehi.EXTRA_DROPOFF_DATE";
    @Extra(int.class)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = String.class, required = false)
    public static final String EXTRA_SEARCH_AREA = "ehi.EXTRA_SEARCH_AREA";

    private static final int MARKER_MAX_ZOOM_LEVEL = 12;
    private static final int NEAREST_MARKERS_COUNT = 5;

    private static final float MIN_PIN_DISTANCE = 0.003f;
    private static final int ANIM_DURATION = 200;

    private IMapDragDelegate mListener;
    private BubbleDetailsListener mMapBubbleDetailsListener;

    private GoogleMap mGoogleMap;
    private boolean mSearchNearbyLocations = false;
    private boolean mZoomToMarkerGroup = true;
    private Date mPickupDate;
    private Date mDropoffDate;
    private int mFlow;
    private String mSearchArea;
    private HashMap<Marker, EHISolrLocation> mMarkersHashMap = new HashMap<>();
    private Pair<Marker, EHISolrLocation> mSelectedPlace;

    private ArrayList<EHISolrLocation> mLocationsToShow = new ArrayList<>();
    private LatLng mCoordinatesToMoveCameraTo;

    private Marker mCurrentLocationMarker;
    private Location mCurrentLocation;
    private boolean isAnimatingMarkerDetails;

    private long mSearchRadius;

    private Timer mSearchTimer = new Timer();

    private Handler mHandler = new Handler();


    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.equals(mCurrentLocationMarker)) {
                return true;
            }

            EHISolrLocation locationForMarker = mMarkersHashMap.get(marker);
            deselectActiveMarker();
            mSelectedPlace = new Pair<>(addMarkerForLocation(locationForMarker, true), locationForMarker);

            ((LocationsOnMapActivity) getActivity()).createEventWithLocationMapDictionary(mSelectedPlace.second)
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_MAP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION_PIN.value)
                    .tagScreen()
                    .tagEvent();

            marker.remove();
            moveCameraToMarker(mSelectedPlace.first);
            mMapDragDelegate.onMarkerClick();
            showBubbleView(mSelectedPlace.second);
            return true;
        }
    };

    private LocationMapBubbleDetailsView.MapDetailsListener mLocationMapDetailsListener = new LocationMapBubbleDetailsView.MapDetailsListener() {
        @Override
        public void onMoreInfoMapDetailsClick() {
            mMapBubbleDetailsListener.onLocationDetails(mSelectedPlace.second);
        }

        @Override
        public void onLocationSelectedClick() {
            mMapBubbleDetailsListener.onLocationSelected(mSelectedPlace.second);
        }

        @Override
        public void onAfterHoursClick() {
            ((LocationsOnMapActivity) getActivity()).createEventWithLocationMapDictionary(mSelectedPlace.second)
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_MAP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ABOUT_AFTER_HOURS.value)
                    .tagScreen()
                    .tagEvent();
            showDialog(new AfterHoursReturnDialogFragmentHelper.Builder().build());
        }
    };

    private IMapDragDelegate mMapDragDelegate = new IMapDragDelegate() {

        @Override
        public void onMapStartDrag() {
            collapseBubbleView(new Runnable() {
                @Override
                public void run() {
                    try {
                        mListener.onMapStartDrag();
                    } catch (Exception e) {
                        DLog.e(TAG, e);
                    }
                }
            });
            mSearchTimer.cancel();
        }

        @Override
        public void onMapStopDrag() {
            mListener.onMapStopDrag();
            mSearchTimer = new Timer();
            mSearchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LatLng ne = mGoogleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
                                LatLng sw = mGoogleMap.getProjection().getVisibleRegion().latLngBounds.southwest;

                                float[] result = new float[1];
                                Location.distanceBetween(ne.latitude,
                                        ne.longitude,
                                        sw.latitude,
                                        sw.longitude,
                                        result);

                                int radius = (int) (result[0] / 2) / 1000;

                                ((ISearchByLocationDelegate) getActivity()).searchByLocation(mGoogleMap.getCameraPosition().target, radius);
                            } catch (Exception e) {
                                DLog.w(TAG, e);
                            }
                        }
                    });
                }
            }, LocationsOnMapActivity.ANIMATION_DURATION);
        }

        @Override
        public void onMarkerClick() {
            mListener.onMarkerClick();
        }

        @Override
        public void onMapTouch(MotionEvent e) {
            if(!(DisplayUtils.wasViewClicked(getViewBinding().locationDetailsView, (int) e.getX(), (int) e.getY()))) {
                collapseBubbleView(null);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_map_wrapper, container);
        initViews();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
             mListener = (IMapDragDelegate) getActivity();
             mMapBubbleDetailsListener = (BubbleDetailsListener) getActivity();
        } catch (ClassCastException e) {
            DLog.e("Your activity should implement IMapDragDelegate or MapDetailsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mMapBubbleDetailsListener = null;
    }

    private void initViews() {
        getViewBinding().mapView.setMapTouchListener(mMapDragDelegate);
        getViewBinding().mapView.onCreate(null);
        MapsInitializer.initialize(getActivity());
        getViewBinding().locationDetailsView.setMapDetailsListener(mLocationMapDetailsListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(getActivity() instanceof ISearchByLocationDelegate)
                || !(getActivity() instanceof IMapDragDelegate)) {
            throw new NotImplementedException();
        }

        setHasOptionsMenu(true);

        MapWrapperFragmentHelper.Extractor extractor = new MapWrapperFragmentHelper.Extractor(this);

        if (extractor.extraSearchNearby() != null) {
            //noinspection ConstantConditions
            mSearchNearbyLocations = extractor.extraSearchNearby();
        }

        if (extractor.extraPickupDate() != null) {
            mPickupDate = extractor.extraPickupDate();
        }

        if (extractor.extraDropoffDate() != null) {
            mDropoffDate = extractor.extraDropoffDate();
        }

        if (extractor.extraStartPoint() != null) {
            mCoordinatesToMoveCameraTo = extractor.extraStartPoint();
        }

        mFlow = extractor.extraFlow();
        mSearchArea = extractor.extraSearchArea();

        getViewBinding().mapView.getMapAsync(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getViewBinding().mapView != null) {
            getViewBinding().mapView.onDestroy();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initMap();
        if (mCurrentLocation != null) {
            setUserCurrentLocation(mCurrentLocation);
        }
        if (mCoordinatesToMoveCameraTo != null) {
            moveCameraToCoordinates(mCoordinatesToMoveCameraTo);
            mCoordinatesToMoveCameraTo = null;
        }
        if (mLocationsToShow.size() > 0) {
            showLocations(mLocationsToShow, mSearchRadius, mCoordinatesToMoveCameraTo);
        }
    }

    public void moveCameraToCoordinates(LatLng coordinates) {
        if (mGoogleMap == null) {
            mCoordinatesToMoveCameraTo = coordinates;
        } else {
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(coordinates, 12, 0, 0));
            mGoogleMap.moveCamera(cu);
            mCoordinatesToMoveCameraTo = null;
        }
    }

    public void setUserCurrentLocation(Location current) {
        mCurrentLocation = current;
        if (mGoogleMap != null
                && mCurrentLocation != null) {
            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
            }
            LatLng ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            int px = (int) DisplayUtils.dipToPixels(getActivity(), 16);
            Bitmap dotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(dotMarkerBitmap);
            Drawable shape = getResources().getDrawable(R.drawable.sh_user_location_pin);
            shape.setBounds(0, 0, dotMarkerBitmap.getWidth(), dotMarkerBitmap.getHeight());
            shape.draw(canvas);

            mCurrentLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(dotMarkerBitmap))
                    .position(ll)
                    .flat(true)
                    .title("")
                    .snippet("")
                    .anchor(0.5f, 0.5f));
        }
    }

    private void initMap() {
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.setOnMarkerClickListener(mOnMarkerClickListener);
        getViewBinding().mapView.post(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.sliding_panel_height_initial));
            }
        });
    }

    public void showLocations(List<EHISolrLocation> locations, long searchRadius, LatLng ll) {
        mSearchRadius = searchRadius;
        if (mGoogleMap == null) {
            mLocationsToShow.clear();
            mLocationsToShow.addAll(locations);
            //will show marker in onMapReady callback then
            return;
        }

        for (Marker m : mMarkersHashMap.keySet()) {
            m.remove();
        }
        mMarkersHashMap.clear();

        if (locations == null) {
            return;
        }
        locations = offsetMarkersIfNeeded(locations);
        for (EHISolrLocation location : locations) {
            if (mSelectedPlace != null
                    && mSelectedPlace.second.getPeopleSoftId().equals(location.getPeopleSoftId())) {
                mSelectedPlace = new Pair<>(addMarkerForLocation(location, true), location);
            } else {
                addMarkerForLocation(location, false);
            }
        }

        if (mMarkersHashMap.size() > 0) {
            if (mZoomToMarkerGroup) {
                //First run for city or for nearby location
                mZoomToMarkerGroup = false;
                CameraUpdate cu;
                if (ll != null) {
                    cu = CameraUpdateFactory.newLatLngZoom(ll, calculateZoomLevel());
                } else {
                    cu = CameraUpdateFactory.newLatLngZoom(mGoogleMap.getCameraPosition().target, calculateZoomLevel());
                }
                mGoogleMap.animateCamera(cu);
            }
        }
    }

    private List<EHISolrLocation> offsetMarkersIfNeeded(List<EHISolrLocation> locations) {
        for (EHISolrLocation locationFrom : locations) {
            for (EHISolrLocation locationTo : locations) {
                if (locationFrom == locationTo) {
                    continue;
                }
                Location from = locationFrom.getLocation();
                Location to = locationTo.getLocation();
                if (from != null
                        && to != null) {
                    double distance = (int) locationFrom.getLocation().distanceTo(locationTo.getLocation());
                    if (distance < 300) {
                        locationFrom.setLatitude(locationFrom.getLatitude() - MIN_PIN_DISTANCE);
                    }
                }
            }
        }
        return locations;
    }

    private Marker addMarkerForLocation(EHISolrLocation location, boolean selected) {
        if (location != null
                && location.getLatitude() != null
                && location.getLongitude() != null) {
            int drawable = location.getMapPinDrawable(selected);
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(drawable))
                    .position(location.getLatLng())
                    .anchor(0.4f, 0.94f)
                    .title(location.getTranslatedLocationName())
                    .snippet(location.getReadableAddress()));

            if (selected) {
                marker.setZIndex(2);
            } else if (location.isLocationInvalid()) {
                marker.setZIndex(0);
            } else {
                marker.setZIndex(1);
            }
            mMarkersHashMap.put(marker, location);
            return marker;
        }
        return null;
    }


    private void moveCameraToMarker(final Marker marker) {

        float targetZoomLevel = Math.max(mGoogleMap.getCameraPosition().zoom, MARKER_MAX_ZOOM_LEVEL);
        float originalZoomLevel = mGoogleMap.getCameraPosition().zoom;

        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(targetZoomLevel));
        Projection mapProjection = mGoogleMap.getProjection();
        Point p = mapProjection.toScreenLocation(marker.getPosition());
        p.set(p.x, (int) (p.y - DisplayUtils.dipToPixels(getActivity(), 52)));
        LatLng ll = mapProjection.fromScreenLocation(p);
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoomLevel));

        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(ll,
                targetZoomLevel,
                0, 0));

        mGoogleMap.animateCamera(cu, LocationsOnMapActivity.ANIMATION_DURATION, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //callback don't work properly, fires right away and do not wait till animation ends
            }

            @Override
            public void onCancel() {
                deselectActiveMarker();
            }
        });
    }

    private void deselectActiveMarker() {
        if (mSelectedPlace != null) {
            EHISolrLocation selected = mMarkersHashMap.get(mSelectedPlace.first);
            mSelectedPlace.first.remove();
            mSelectedPlace = null;
            addMarkerForLocation(selected, false);
        }
    }

    private float calculateZoomLevel() {
        double equatorLength = 6378140; // in meters
        double widthInPixels = DisplayUtils.getScreenWidth(getActivity());
        double metersPerPixel = equatorLength / 256;
        float zoomLevel = 0;
        while ((metersPerPixel * widthInPixels) > (mSearchRadius * 2)) { //2 because of radius + 0.1 to fit pins inside of visible map
            metersPerPixel *= 0.5;
            zoomLevel = zoomLevel + 1;
        }
        return zoomLevel - 0.2f;
    }

    private void showBubbleView(EHISolrLocation selectedLocation) {
        getViewBinding().locationDetailsView.setSolrLocation(mPickupDate, mDropoffDate, selectedLocation, mSearchArea, mFlow);
        final float bottomViewHeight = DisplayUtils.dipToPixels(getContext(), 56);
        final int startingY = getViewBinding().getRoot().getBottom() -  (int)bottomViewHeight;
        getViewBinding().locationDetailsView.setY(startingY);
        getViewBinding().locationDetailsView
                .animate()
                .setDuration(ANIM_DURATION)
                .translationYBy(-1 * (getViewBinding().locationDetailsView.getHeight()))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        getViewBinding().locationDetailsView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
    }

    private void collapseBubbleView(@Nullable final Runnable endAnimationRunnable) {
        deselectActiveMarker();
        if (getViewBinding().locationDetailsView.getVisibility() == View.VISIBLE && !isAnimatingMarkerDetails) {
            final float bottomViewHeight = DisplayUtils.dipToPixels(getContext(), getViewBinding().locationDetailsView.getHeight());
            getViewBinding().locationDetailsView
                    .animate()
                    .setDuration(ANIM_DURATION)
                    .translationYBy(bottomViewHeight)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            isAnimatingMarkerDetails = true;
                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            getViewBinding().locationDetailsView.setVisibility(View.GONE);
                            getViewBinding().locationDetailsView.onViewCollapsed();
                            isAnimatingMarkerDetails = false;
                            if (endAnimationRunnable != null) {
                                endAnimationRunnable.run();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
        } else if (endAnimationRunnable != null) {
            endAnimationRunnable.run();
        }
    }

    public void updateDates(Date pickupDate, Date dropoffDate) {
        mPickupDate = pickupDate;
        mDropoffDate = dropoffDate;
    }

    public interface BubbleDetailsListener {
        void onLocationDetails(EHISolrLocation solrLocation);
        void onLocationSelected(EHISolrLocation solrLocation);
    }
}
