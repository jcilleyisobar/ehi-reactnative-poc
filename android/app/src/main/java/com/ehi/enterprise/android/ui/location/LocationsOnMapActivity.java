package com.ehi.enterprise.android.ui.location;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationOnMapActivityBinding;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.location.interfaces.IMapDragDelegate;
import com.ehi.enterprise.android.ui.location.interfaces.ISearchByLocationDelegate;
import com.ehi.enterprise.android.ui.location.view_holders.LocationOnMapCellViewHolder;
import com.ehi.enterprise.android.ui.location.widgets.AfterHoursReturnDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterMapComponentView;
import com.ehi.enterprise.android.ui.reservation.ItineraryActivityHelper;
import com.ehi.enterprise.android.ui.reservation.modify.ModifyItineraryActivityHelper;
import com.ehi.enterprise.android.ui.widget.LocationMapBottomSheetBehavior;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.filters.EHIFilterList;
import com.ehi.enterprise.android.utils.locations.EHITransferDatesManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;
import com.ehi.enterprise.android.utils.locations.OnLastLocationsListener;
import com.google.android.m4b.maps.model.LatLng;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(LocationsOnMapViewModel.class)
public class LocationsOnMapActivity
        extends DataBindingViewModelActivity<LocationsOnMapViewModel, LocationOnMapActivityBinding>
        implements
        ISearchByLocationDelegate,
        IMapDragDelegate,
        MapWrapperFragment.BubbleDetailsListener {

    private static final String TAG = LocationsOnMapActivity.class.getSimpleName();
    public static final String SCREEN_NAME = "LocationsOnMapActivity";

    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    @Extra(int.class)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = String.class, required = false)
    public static final String EXTRA_NAME = "EXTRA_NAME";
    @Extra(value = EHILatLng.class, required = false)
    public static final String EXTRA_LAT_LNG = "ehi.EXTRA_LAT_LNG";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_TIME = "ehi.EXTRA_RETURN_TIME";
    @Extra(value = boolean.class)
    public static final String EXTRA_FROM_LDT = "ehi.EXTRA_FROM_LDT";

    private static final int FILTER_RESULT_KEY = 311;
    private static final int NO_LOCATIONS_DIALOG = 312;
    public static final int ANIMATION_DURATION = 350;
    private static final int SELECT_DATE_CODE = 1223;
    private static final int SELECT_TIME_PICKUP = 1224;
    private static final int SELECT_TIME_RETURN = 1225;
    public static final String CITY_REACTION = "CITY_REACTION";
    public static final String SEARCH_LOCATIONS_REACTION = "SEARCH_LOCATIONS_REACTION";
    public static final String ERROR_REACTION = "ERROR_REACTION";

    public static final int PERMISSION_REQUEST_CODE = 10;

    private Runnable mPendingRunnable; //Used as a workaround to google issue https://code.google.com/p/android/issues/detail?id=190966&q=label%3AReportedBy-Developer&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars

    private
    @SearchLocationsActivity.Flow
    int mFlow;

    private boolean mNalamoDialogShowed = false;
    private boolean mIsDragging = false;

    private View.OnClickListener mFilterButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getViewModel().closeFilterTip();
            showFilterDetailsScreen();
        }
    };

    private LocationMapBottomSheetBehavior mBehavior;
    private LocationsOnMapListAdapter mSearchLocationsAdapter;

    private FilterMapComponentView.FilterMapViewListener mFilterClickListener = new FilterMapComponentView.FilterMapViewListener() {
        @Override
        public void onPickupDateClick() {
            Fragment datePicker = new FilterDatePickerFragmentHelper.Builder()
                    .extraIsSelectPickup(true)
                    .extraPickupDate(getViewModel().getPickupDate())
                    .extraReturnDate(getViewModel().getDropoffDate())
                    .build();
            showModalForResult(datePicker, SELECT_DATE_CODE);
        }

        @Override
        public void onPickupTimeClick() {
            Fragment timePicker = new FilterTimePickerFragmentHelper.Builder()
                    .extraIsPickup(true)
                    .extraFilters(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()))
                    .build();
            showModalForResult(timePicker, SELECT_TIME_PICKUP);
        }

        @Override
        public void onReturnDateClick() {
            Fragment datePicker = new FilterDatePickerFragmentHelper.Builder()
                    .extraIsSelectPickup(false)
                    .extraPickupDate(getViewModel().getPickupDate())
                    .extraReturnDate(getViewModel().getDropoffDate())
                    .build();
            showModalForResult(datePicker, SELECT_DATE_CODE);
        }

        @Override
        public void onReturnTimeClick() {
            Fragment timePicker = new FilterTimePickerFragmentHelper.Builder()
                    .extraIsPickup(false)
                    .extraFilters(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()))
                    .build();
            showModalForResult(timePicker, SELECT_TIME_RETURN);
        }
    };

    private View.OnClickListener mClearFilterButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getViewModel().resetFilters();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_MAP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CLEAR_ALL_FILTERS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()), getResources()))
                    .tagScreen()
                    .tagEvent();
        }
    };

    private OnLastLocationsListener mOnLastLocationsListener = new OnLastLocationsListener() {

        @Override
        public void onLastLocationFetched(Location location) {
            DLog.e("EVOS", "LocationFetched!!!!");
            if (location != null) {
                setCurrentLocation(location);
                if (getViewModel().isSearchingNearby()) {
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    getViewModel().setLatLong(latlng);
                }
            }
        }
    };
    private LocationOnMapCellViewHolder.LocationMapListListener mLocationListListener = new LocationOnMapCellViewHolder.LocationMapListListener() {
        @Override
        public void onMoreInfoMapDetailsClick(EHISolrLocation location) {
            createEventWithLocationMapDictionary(location)
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_MAP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION_DETAIL.value)
                    .tagScreen()
                    .tagEvent();

            LocationsOnMapActivity.this.onShowLocationDetails(location);
        }

        @Override
        public void onLocationSelectedClick(EHISolrLocation location) {
            createEventWithLocationMapDictionary(location)
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_MAP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_LOCATION.value)
                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SELECTED.value)
                    .tagScreen()
                    .tagEvent()
                    .tagMacroEvent();

            LocationsOnMapActivity.this.onSelectLocation(location);
        }

        @Override
        public void onAfterHoursClick(EHISolrLocation location) {
            createEventWithLocationMapDictionary(location)
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_LIST.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ABOUT_AFTER_HOURS.value)
                    .tagScreen()
                    .tagEvent();
            showDialog(new AfterHoursReturnDialogFragmentHelper.Builder().build());
        }
    };

    private View.OnClickListener mFilterTipCloseBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().closeFilterTip();
        }
    };

    public Pair<Boolean, String> getReadableFilterString() {
        String readableString = "";
        boolean nalmo = getViewModel().containsNalmo();
        if (!nalmo) {
            for (int i = 0; i < getViewModel().getFilters().size(); i++) {
                readableString += getViewModel().getFilters().valueAt(i).getTitle() + ((i + 1 < getViewModel().getFilters().size()) ? ", " : "");
            }
        } else {
            readableString = getResources().getString(R.string.locations_offbrand_message);
        }
        return new Pair<>(nalmo, readableString);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPendingRunnable != null) {
            mPendingRunnable.run();
            mPendingRunnable = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_locations_on_map);
        List<String> permissions = PermissionUtils.checkPermissions(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        setupBottomSheetBehavior();
        if (!permissions.isEmpty()) {
            PermissionUtils.requestCheckedPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            if (!LocationApiManager.getInstance().isInitialized()) {
                FragmentUtils.addProgressFragment(LocationsOnMapActivity.this);
                LocationApiManager.getInstance().initialize(getApplicationContext(), true, new LocationApiManager.LocationApiCallback() {
                    @Override
                    public void onConnected() {
                        FragmentUtils.removeProgressFragment(LocationsOnMapActivity.this);
                        onLocationAPIConnectedInitialization();
                    }
                }, mOnLastLocationsListener);
            } else {
                permissionsAlreadyGrantedInitialization();
            }
        }
    }

    private void setupBottomSheetBehavior() {
        mBehavior = LocationMapBottomSheetBehavior.from(getViewBinding().bottomSheet);
        mBehavior.setBottomSheetCallback(new LocationMapBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (mSearchLocationsAdapter != null) {
                    mSearchLocationsAdapter.setFilterButtonVisible(newState == BottomSheetBehavior.STATE_EXPANDED);
                    if (newState != BottomSheetBehavior.STATE_SETTLING) {
                        createEventWithLocationMapDictionary(null)
                                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_LIST.value)
                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION_LIST_HEADER.value)
                                .tagScreen()
                                .tagEvent();
                    }
                    if (!mSearchLocationsAdapter.isEmpty() && newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        getViewBinding().bottomSheet.scrollTo(0, 0);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (mSearchLocationsAdapter != null && slideOffset > 0) {
                    mSearchLocationsAdapter.onAnimateHeader(slideOffset);
                    if (!mIsDragging) {
                        setFilterAlpha(1f - slideOffset);
                    }
                }
            }
        });
    }

    private void onLocationAPIConnectedInitialization() {
        initialize(null);
    }

    private void permissionsAlreadyGrantedInitialization() {
        Location location = LocationApiManager.getInstance().getLastCurrentLocation();
        LocationApiManager.getInstance().checkAvailibilityAndRequestLastLocation(this, mOnLastLocationsListener);
        setCurrentLocation(location);
        initialize(location);
    }

    private void permissionsNotGrantedInitialization() {
        initialize(null);
    }

    private void initialize(@Nullable Location currentLocation) {
        initDependencies(); // why we manually calling this? leaving this here for now to do not break something
        initViews();
        LocationsOnMapActivityHelper.Extractor extractor = new LocationsOnMapActivityHelper.Extractor(this);
        getViewModel().setIsModify(extractor.isModify());

        getViewModel().setPickupDate(extractor.extraPickupDate());
        getViewModel().setPickupTime(extractor.extraPickupTime());
        getViewModel().setDropoffDate(extractor.extraDropoffDate());
        getViewModel().setDropoffTime(extractor.extraDropoffTime());
        getViewModel().setIsFromLDT(extractor.extraFromLdt());

        //noinspection ResourceType
        mFlow = extractor.extraFlow();
        getViewModel().setFlow(mFlow);
        getViewModel().setFilterTipViewVisibility();

        if (extractor.extraLatLng() != null
                && extractor.extraName() != null) {
            getViewModel().setLocationName(extractor.extraName());
            getViewModel().setLatLong(extractor.extraLatLng().getLatLng());
            showLocations(getViewModel().getLatLong());
        } else {
            getViewModel().setSearchingNearby(true);
            LatLng latlng = null;
            if (currentLocation != null) {
                latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                getViewModel().setLatLong(latlng);
            }
            showNearbyLocations(latlng);
        }
    }

    private void initViews() {
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        getViewBinding().filterButton.setVisibility(View.VISIBLE);
        getViewModel().setLocationFilterViewVisibility();
        getViewBinding().filterButton.setOnClickListener(mFilterButtonOnClickListener);
        getViewBinding().locationFilterMapView.setOnClearFilterClickListener(mClearFilterButton);
        getViewBinding().locationFilterMapView.setFilterViewClickListener(mFilterClickListener);
        getViewBinding().bottomSheetLocationList.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().filterTipView.setCloseBtnListener(mFilterTipCloseBtnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        createEventWithLocationMapDictionary(null)
            .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
            .state(EHIAnalytics.State.STATE_MAP.value)
            .tagScreen()
            .tagEvent();
    }

    public void onFilterButtonClicked(View v) {
        showFilterDetailsScreen();
    }

    public void onListHeaderClicked() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            getViewModel().closeFilterTip();
        } else {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void showLocations(LatLng cityCenterLatLng) {
        showTitleForLocation(getViewModel().getLocationName());

        final Fragment frag = new MapWrapperFragmentHelper.Builder()
                .extraPickupDate(getViewModel().getPickupDate())
                .extraDropoffDate(getViewModel().getDropoffDate())
                .extraSearchNearby(false)
                .extraStartPoint(cityCenterLatLng)
                .extraFlow(mFlow)
                .extraSearchArea(getViewModel().getLocationName())
                .build();

        try {
            commitMapAndLocationFragments(frag);
        } catch (Exception e) {
            mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        commitMapAndLocationFragments(frag);
                    } catch (Exception e1) {
                        DLog.e(TAG, "", e1);
                    }
                }
            };
            DLog.e(TAG, "", e);
        }
    }

    private void commitMapAndLocationFragments(Fragment frag) throws Exception {
        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(frag)
                .into(R.id.ac_locations_on_map_map_container)
                .createTransaction()
                .commit();
    }

    private void showNearbyLocations(LatLng currentLocation) {
        getViewBinding().toolbarInclude.title.setText(R.string.locations_nearby_title);

        Fragment frag = new MapWrapperFragmentHelper.Builder()
                .extraSearchNearby(true)
                .extraStartPoint(currentLocation)
                .extraFlow(mFlow)
                .extraSearchArea(getViewModel().getLocationName())
                .build();

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.ADD)
                .fragment(frag)
                .into(R.id.ac_locations_on_map_map_container)
                .commit();
    }

    @Override
    protected void initDependencies() {
        bind(ReactorView.visibility(getViewModel().locationFilterView.visibility(), getViewBinding().locationFilterMapView));
        bind(ReactorView.visibility(getViewModel().filterTipView.visibility(), getViewBinding().filterTipView));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().locationFilterView.visibility().getValue() != null &&
                        getViewModel().locationFilterView.visibility().getValue() == View.VISIBLE) {
                    getViewBinding().filterButtonLayout.setBackgroundColor(Color.WHITE);
                    getViewBinding().separatorView.setVisibility(View.VISIBLE);
                } else {
                    getViewBinding().filterButtonLayout.setBackgroundColor(Color.TRANSPARENT);
                    getViewBinding().separatorView.setVisibility(View.GONE);
                }
            }
        });

        addReaction(CITY_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getLatLong() != null) {
                    getViewModel().searchSolrLocationsForCoordinates(getViewModel().getLatLong());
                }
            }
        });
        addReaction(SEARCH_LOCATIONS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getSolrLocations() != null && !getViewModel().isLoading()) {
                    showLocations(getViewModel().getSolrLocations(), getViewModel().getSearchRadius());
                }
            }
        });
        addReaction(ERROR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getErrorResponseWrapper() != null) {
                    showLocations(getViewModel().getSolrLocations(), getViewModel().getSearchRadius());
                    DialogUtils.showErrorDialog(LocationsOnMapActivity.this, getViewModel().getErrorResponseWrapper());
                    getViewModel().setErrorWrapper(null);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isLoading()) {
                    getViewBinding().toolbarInclude.progress.setVisibility(View.VISIBLE);
                } else {
                    getViewBinding().toolbarInclude.progress.setVisibility(View.GONE);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewBinding().locationFilterMapView.setPickupDateText(getViewModel().getPickupDate());
                getViewBinding().locationFilterMapView.setReturnDateText(getViewModel().getDropoffDate());
                getViewBinding().locationFilterMapView.setPickupTimeText(getViewModel().getPickupTime());
                getViewBinding().locationFilterMapView.setReturnTimeText(getViewModel().getDropoffTime());
                getViewModel().setLocationFilterViewVisibility();
                updateDatesOnMap(getViewModel().getPickupDate(), getViewModel().getDropoffDate());
                updateDatesOnList();
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().needShowNoFilteredLocationsDialog()) {
                    minimizeListPanel();
                    showModalDialogForResult(
                            new NoLocationsFoundDialogFragmentHelper.Builder()
                                    .extraFilters(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()))
                                    .build(), NO_LOCATIONS_DIALOG);
                    getViewModel().setSkipOneSearch(true);
                    getViewModel().setShowNoFilteredLocationsDialog(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LocationApiManager.getInstance().onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILTER_RESULT_KEY) {
                ArrayList<Integer> filterTypes = data.getExtras().getIntegerArrayList(SearchLocationsFilterFragment.FILTER_LIST_KEY);
                getViewModel().setFilters(EHIFilterList.translateToSparse(filterTypes, getResources()));
                getViewModel().setPickupDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_PICKUP_DATE));
                getViewModel().setDropoffDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_RETURN_DATE));
                getViewModel().setPickupTime((Date) data.getSerializableExtra(SearchLocationsFilterFragment.EXTRA_PICKUP_TIME));
                getViewModel().setDropoffTime((Date) data.getSerializableExtra(SearchLocationsFilterFragment.EXTRA_RETURN_TIME));
            } else if (requestCode == SELECT_DATE_CODE) {
                getViewModel().setPickupDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_PICKUP_DATE));
                getViewModel().setDropoffDate((Date) data.getSerializableExtra(FilterDatePickerFragment.EXTRA_RETURN_DATE));
            } else if (requestCode == SELECT_TIME_PICKUP) {
                getViewModel().setPickupTime((Date) data.getSerializableExtra(SearchLocationsFilterFragment.EXTRA_TIME_SELECT));
            } else if (requestCode == SELECT_TIME_RETURN) {
                getViewModel().setDropoffTime((Date) data.getSerializableExtra(SearchLocationsFilterFragment.EXTRA_TIME_SELECT));
            } else if (requestCode == NO_LOCATIONS_DIALOG) {
                showFilterDetailsScreen();
            }
        }
        if (resultCode == Activity.RESULT_FIRST_USER) {
            if (requestCode == NO_LOCATIONS_DIALOG) {
                getViewModel().setSkipOneSearch(false);
                getViewModel().resetFilters();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showLocations(List<EHISolrLocation> solrLocations, long searchRadius) {
        if (getViewModel().containsNalmo()) {
            getViewBinding().filterButton.setVisibility(View.GONE);
            if (!mNalamoDialogShowed) {
                showModalDialog(new NalamoDialogFragmentHelper.Builder().build());
                mNalamoDialogShowed = true;
            }
        } else {
            getViewBinding().filterButton.setVisibility(View.VISIBLE);
        }
        getViewBinding().locationFilterMapView.setFiltersText(getReadableFilterString().second);
        showLocationsOnMap(solrLocations, searchRadius);
        showLocationsInList(solrLocations);
    }

    private void showLocationsInList(List<EHISolrLocation> solrLocations) {
        if (mSearchLocationsAdapter == null) {
            initAdapter();
        }
        if (!getViewModel().willShowNoFilteredLocationsDialog()) {
            if (!ListUtils.isEmpty(solrLocations)) {
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            mSearchLocationsAdapter.setData(solrLocations);
            if (ListUtils.isEmpty(solrLocations)) {
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            getViewBinding().bottomSheet.scrollTo(0, 0);
        }
    }

    private void initAdapter() {
        mSearchLocationsAdapter = new LocationsOnMapListAdapter(this, null, getViewModel().getPickupDate(), getViewModel().getDropoffDate(), getViewModel().getLocationName(), mFlow);
        mSearchLocationsAdapter.setLocationListListener(getLocationListListener());
        mSearchLocationsAdapter.setOnReturnToTopClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewBinding().bottomSheet.scrollTo(0, 0);
            }
        });

        mSearchLocationsAdapter.setOnHeaderClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListHeaderClicked();
            }
        });

        mSearchLocationsAdapter.setOnFilterButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFilterButtonClicked(view);
            }
        });

        getViewBinding().bottomSheetLocationList.setAdapter(mSearchLocationsAdapter);
        getViewBinding().bottomSheetLocationList.setNestedScrollingEnabled(false);
    }

    private void showLocationsOnMap(List<EHISolrLocation> solrLocations, long searchRadius) {
        MapWrapperFragment frag = (MapWrapperFragment) getSupportFragmentManager().findFragmentById(R.id.ac_locations_on_map_map_container);
        if (frag != null) {
            frag.showLocations(solrLocations, searchRadius, getViewModel().getLatLong());
        }
    }

    private void updateDatesOnMap(Date pickupDate, Date dropoffDate) {
        MapWrapperFragment frag = (MapWrapperFragment) getSupportFragmentManager().findFragmentById(R.id.ac_locations_on_map_map_container);
        if (frag != null) {
            frag.updateDates(pickupDate, dropoffDate);
        }
    }

    private void updateDatesOnList() {
        if (mSearchLocationsAdapter != null) {
            mSearchLocationsAdapter.setFilterDates(getViewModel().getPickupDate(), getViewModel().getDropoffDate());
        }
    }

    private void setCurrentLocation(Location location) {
        MapWrapperFragment frag = (MapWrapperFragment) getSupportFragmentManager().findFragmentById(R.id.ac_locations_on_map_map_container);
        if (frag != null) {
            frag.setUserCurrentLocation(location);
        }
    }

    private void showTitleForLocation(String locationName) {
        getViewBinding().toolbarInclude.title.setText(locationName);
    }

    private void onShowLocationDetails(EHISolrLocation location) {
        startActivity(new LocationDetailsActivityHelper.Builder()
                .location(location)
                .flow(mFlow)
                .extraPickupDate(getViewModel().getPickupDate())
                .extraDropoffDate(getViewModel().getDropoffDate())
                .extraPickupTime(getViewModel().getPickupTime())
                .extraDropoffTime(getViewModel().getDropoffTime())
                .showStartReservation(false)
                .isModify(getViewModel().isModify())
                .build(this));
    }

    private void onSelectLocation(final EHISolrLocation location) {
        if (location.isNalmo()) {
            final String linkOutUrl;
            if (location.getBrand().contains(EHILocation.BRAND_ALAMO)) {
                linkOutUrl = getViewModel().getValidAlamoReservationUrl();
            } else {
                linkOutUrl = getViewModel().getValidNationalReservationUrl();
            }
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.alert_open_browser_text))
                    .setPositiveButton(getString(R.string.standard_ok_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            IntentUtils.openUrlViaExternalApp(LocationsOnMapActivity.this, linkOutUrl);
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_cancel_title), null)
                    .setCancelable(true)
                    .create()
                    .show();
            return;
        }
        redirectToLDT(location);
    }

    private void redirectToLDT(EHISolrLocation location) {
        final Intent intent;
        final EHITransferDatesManager ehiTransferDatesManager = new EHITransferDatesManager(location,
                getViewModel().getPickupDate(),
                getViewModel().getDropoffDate(),
                getViewModel().getPickupTime(),
                getViewModel().getDropoffTime(),
                mFlow);

        if (getViewModel().isModify()) {
            final ModifyItineraryActivityHelper.Builder builder = new ModifyItineraryActivityHelper.Builder();
            if (ehiTransferDatesManager.shouldSendPickupDate()) {
                builder.extraPickupDate(getViewModel().getPickupDate());
                if (ehiTransferDatesManager.shouldSendPickupTime()) {
                    builder.extraPickupTime(getViewModel().getPickupTime());
                }
            }
            if (ehiTransferDatesManager.shouldSendDropoffDate()) {
                builder.extraReturnDate(getViewModel().getDropoffDate());
                if (ehiTransferDatesManager.shouldSendDropoffTime()) {
                    builder.extraReturnTime(getViewModel().getDropoffTime());
                }
            }

            if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
                builder.extraPickupLocation(location);
            } else {
                builder.extraReturnLocation(location);
            }
            intent = builder.extraFlow(mFlow).build(this);
        } else {
            final ItineraryActivityHelper.Builder builder = new ItineraryActivityHelper.Builder();
            if (ehiTransferDatesManager.shouldSendPickupDate()) {
                builder.extraPickupDate(getViewModel().getPickupDate());
                if (ehiTransferDatesManager.shouldSendPickupTime()) {
                    builder.extraPickupTime(getViewModel().getPickupTime());
                }
            }
            if (ehiTransferDatesManager.shouldSendDropoffDate()) {
                builder.extraDropoffDate(getViewModel().getDropoffDate());
                if (ehiTransferDatesManager.shouldSendDropoffTime()) {
                    builder.extraDropoffTime(getViewModel().getDropoffTime());
                }
            }

            if (mFlow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
                builder.extraPickupLocation(location);
            } else {
                builder.extraReturnLocation(location);
            }
            intent = builder.extraFlow(mFlow).build(this);
        }

        getViewModel().addRecentLocation(location);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!isListCollapsed() && !isLocationListEmpty()) {
            minimizeListPanel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapStartDrag() {
        mIsDragging = true;
        hideFilterView();
        minimizeListPanel();
        hideLocationList();
    }

    @Override
    public void onMapStopDrag() {
        showFilterView();
        showLocationList();
        mIsDragging = false;
    }

    private void hideLocationList() {
        mBehavior.setHideable(true);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showLocationList() {
        mBehavior.setHideable(false);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void minimizeListPanel() {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showFilterView() {
        getViewBinding().filterButtonLayout.setClickable(true);
        setFilterAlphaWithAnimation(1f);
    }

    private void hideFilterView() {
        getViewBinding().filterButtonLayout.setClickable(false);
        setFilterAlphaWithAnimation(0f);
    }

    private void setFilterAlphaWithAnimation(float alpha) {
        getViewBinding().filterTipView.animate().alpha(alpha)
                .setInterpolator(new LinearInterpolator())
                .setDuration(LocationsOnMapActivity.ANIMATION_DURATION);
        getViewBinding().locationFilterMapView.animate().alpha(alpha)
                .setInterpolator(new LinearInterpolator())
                .setDuration(ANIMATION_DURATION);
        getViewBinding().filterButtonLayout.animate().alpha(alpha)
                .setInterpolator(new LinearInterpolator())
                .setDuration(LocationsOnMapActivity.ANIMATION_DURATION);
    }

    private void setFilterAlpha(float alpha) {
        getViewBinding().filterTipView.setAlpha(alpha);
        getViewBinding().locationFilterMapView.setAlpha(alpha);
        getViewBinding().filterButtonLayout.setAlpha(alpha);
    }

    @Override
    public void onMarkerClick() {
        minimizeListPanel();
    }

    @Override
    public void onMapTouch(MotionEvent e) {
    }

    private boolean isListCollapsed() {
        return mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED;
    }

    private boolean isLocationListEmpty() {
        return mSearchLocationsAdapter != null && mSearchLocationsAdapter.isEmpty();
    }

    @Override
    public void searchByLocation(LatLng latLong, int radius) {
        getViewModel().setLatLong(latLong);
        DLog.e(TAG, "Radius=" + radius);
        getViewModel().setScreenAreaRadius(radius);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (PermissionUtils.areAllPermissionsGranted(grantResults)) {
                    if (!LocationApiManager.getInstance().isInitialized()) {
                        FragmentUtils.addProgressFragment(LocationsOnMapActivity.this);
                        LocationApiManager.getInstance().initialize(getApplicationContext(), true, new LocationApiManager.LocationApiCallback() {
                            @Override
                            public void onConnected() {
                                FragmentUtils.removeProgressFragment(LocationsOnMapActivity.this);
                                onLocationAPIConnectedInitialization();
                            }
                        }, mOnLastLocationsListener);
                    } else {
                        permissionsAlreadyGrantedInitialization();
                    }
                } else {
                    LocationsOnMapActivityHelper.Extractor extractor = new LocationsOnMapActivityHelper.Extractor(this);
                    if (extractor.extraLatLng() != null && extractor.extraName() != null) {
                        permissionsNotGrantedInitialization();
                    } else {
                        finish();
                    }
                }
        }
    }

    public LocationOnMapCellViewHolder.LocationMapListListener getLocationListListener() {
        return mLocationListListener;
    }

    private void showFilterDetailsScreen() {
        Fragment fragment = new SearchLocationsFilterFragmentHelper.Builder()
                .filterListKey(EHIFilterList.fetchActiveFilterTypes(getViewModel().getFilters()))
                .extraPickupDate(getViewModel().getPickupDate())
                .extraReturnDate(getViewModel().getDropoffDate())
                .extraPickupTime(getViewModel().getPickupTime())
                .extraReturnTime(getViewModel().getDropoffTime())
                .build();

        showModalForResult(fragment, FILTER_RESULT_KEY);
    }

    @Override
    public void onLocationDetails(EHISolrLocation solrLocation) {
        createEventWithLocationMapDictionary(solrLocation)
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_LIST.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION_DETAIL_MODAL.value)
                .tagScreen()
                .tagEvent();

        onShowLocationDetails(solrLocation);
    }

    @Override
    public void onLocationSelected(EHISolrLocation solrLocation) {
        createEventWithLocationMapDictionary(solrLocation)
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_LIST.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_LOCATION_MODAL.value)
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SELECTED.value)
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();

        onSelectLocation(solrLocation);
    }

    public EHIAnalyticsEvent createEventWithLocationMapDictionary(EHISolrLocation solrLocation) {
        return EHIAnalyticsEvent.create()
                .addDictionary(EHIAnalyticsDictionaryUtils.locationMap(
                        mFlow, solrLocation, getViewModel().getLocationName(),
                        mSearchLocationsAdapter != null ? mSearchLocationsAdapter.getItemCount() : 0,
                        mSearchLocationsAdapter != null ? mSearchLocationsAdapter.getClosedLocationsCount() : 0,
                        getViewBinding().locationFilterMapView.getFilterTypes()));
    }
}