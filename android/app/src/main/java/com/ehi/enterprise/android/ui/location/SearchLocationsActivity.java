package com.ehi.enterprise.android.ui.location;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SearchLocationsActivityBinding;
import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.location.solr.EHIPostalCodeLocation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelActivity;
import com.ehi.enterprise.android.ui.location.interfaces.ISearchByQueryDelegate;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.reservation.ItineraryActivityHelper;
import com.ehi.enterprise.android.ui.reservation.modify.ModifyItineraryActivityHelper;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.SnackBarUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;

@ViewModel(SearchLocationsActivityViewModel.class)
public class SearchLocationsActivity
        extends DataBindingViewModelActivity<SearchLocationsActivityViewModel, SearchLocationsActivityBinding>
        implements OnSolrLocationInfoClickListener,
        PermissionRequester,
        PermissionRequestHandler {

    public static final String SCREEN_NAME = "SearchLocationsActivity";

    @Extra(int.class)
    public static final String EXTRA_FLOW = "ehi.EXTRA_FLOW";
    @Extra(boolean.class)
    public static final String EXTRA_SHOW_START_RESERVATION = "ehi.EXTRA_SHOW_START_RESERVATION";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
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

    public static final int FLOW_PICKUP_LOCATION_ROUND_TRIP = 1;
    public static final int FLOW_DROP_OFF_LOCATION_ONE_WAY = 2;
    public static final int FLOW_PICKUP_LOCATION_ONE_WAY = 3;
    private static final int LOCATION_PERMISSION_REQUEST = 12;
    private EHISolrLocation mLocationDetailsToOpen;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            FLOW_PICKUP_LOCATION_ROUND_TRIP,
            FLOW_DROP_OFF_LOCATION_ONE_WAY,
            FLOW_PICKUP_LOCATION_ONE_WAY,
    })
    public @interface Flow {
    }

    @Flow
    private int mFlow;

    private Date mPickupDate;
    private Date mPickupTime;
    private Date mDropoffDate;
    private Date mDropoffTime;
    private boolean isFromLDT;

    private boolean mShowStartReservation;

    private TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            getSearchFragment().searchByQuery(s.toString());
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().clearInputButton) {
                getViewBinding().searchInput.setText("");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindingContentView(R.layout.ac_search_locations);

        SearchLocationsActivityHelper.Extractor extractor = new SearchLocationsActivityHelper.Extractor(this);
        //noinspection ResourceType
        mFlow = extractor.extraFlow();

        mPickupDate = extractor.extraPickupDate();
        mPickupTime = extractor.extraPickupTime();
        mDropoffDate = extractor.extraDropoffDate();
        mDropoffTime = extractor.extraDropoffTime();
        isFromLDT = extractor.extraFromLdt();

        //noinspection ResourceType
        mShowStartReservation = extractor.extraShowStartReservation();
        getViewModel().setIsModify(extractor.isModify());

        initViews();

        new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
                .fragment(new SearchLocationsFragmentHelper.Builder().extraFlow(mFlow).build())
                .into(R.id.search_locations_container)
                .commit();
    }

    private void initViews() {
        getViewBinding().toolbarInclude.toolbar.setTitle("");
        setSupportActionBar(getViewBinding().toolbarInclude.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mFlow == FLOW_DROP_OFF_LOCATION_ONE_WAY) {
            getViewBinding().searchInput.setHint(R.string.locations_return_search_placeholder);
        } else {
            getViewBinding().searchInput.setHint(R.string.locations_pickup_search_placeholder);
        }
        getViewBinding().searchInput.addTextChangedListener(mSearchTextWatcher);
        getViewBinding().searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchFragment().searchByQuery(getViewBinding().searchInput.getText().toString());
                }
                return false;
            }
        });

        getViewBinding().searchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    getSearchFragment().searchByQuery(getViewBinding().searchInput.getText().toString());
                }
                return false;
            }
        });
        BaseAppUtils.showKeyboardForView(getViewBinding().searchInput);

        getViewBinding().clearInputButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    private ISearchByQueryDelegate getSearchFragment() {
        return (ISearchByQueryDelegate) getSupportFragmentManager().findFragmentById(R.id.search_locations_container);
    }

    private boolean isZeroResult() {
        SearchLocationsFragment frag = (SearchLocationsFragment) getSupportFragmentManager().findFragmentById(R.id.search_locations_container);
        if (frag != null) {
            frag.isZeroResult();
        }
        return false;
    }

    @Override
    public void onShowLocationDetails(EHISolrLocation location) {
        mLocationDetailsToOpen = location;
        openLocationDetails();
    }

    private void openLocationDetails() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsActivity.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION_DETAIL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mFlow, mLocationDetailsToOpen, getViewBinding().searchInput.getText().toString().trim(), isZeroResult()))
                .tagScreen()
                .tagEvent();
        startActivity(new LocationDetailsActivityHelper.Builder()
                .location(mLocationDetailsToOpen)
                .flow(mFlow)
                .showStartReservation(mShowStartReservation)
                .isModify(getViewModel().isModify())
                .build(this));
    }

    @Override
    public void onSelectLocation(final EHISolrLocation location) {
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
                            IntentUtils.openUrlViaExternalApp(SearchLocationsActivity.this, linkOutUrl);
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_cancel_title), null)
                    .setCancelable(true)
                    .create()
                    .show();
            return;
        }

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsActivity.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCATION.value)
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SELECTED.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(mFlow, location, getViewBinding().searchInput.getText().toString().trim(), isZeroResult()))
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();

        Intent i;
        switch (mFlow) {
            case FLOW_DROP_OFF_LOCATION_ONE_WAY:
                if (getViewModel().isModify()) {
                    i = new ModifyItineraryActivityHelper.Builder().extraReturnLocation(location).build(this);
                } else {
                    i = new ItineraryActivityHelper.Builder()
                            .extraReturnLocation(location)
                            .build(this);
                }
                break;
            default:
            case FLOW_PICKUP_LOCATION_ROUND_TRIP:
                if (getViewModel().isModify()) {
                    i = new ModifyItineraryActivityHelper.Builder()
                            .extraPickupLocation(location)
                            .extraPickupDate(mPickupDate)
                            .extraPickupTime(mPickupTime)
                            .extraReturnDate(mDropoffDate)
                            .extraReturnTime(mDropoffTime)
                            .build(this);
                } else {
                    i = new ItineraryActivityHelper.Builder()
                            .extraPickupLocation(location)
                            .build(this);
                }
                break;
        }
        getViewModel().addRecentLocation(location);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentUtils.startActivityAsModal(this, i);
    }

    @Override
    public void onShowCityLocation(EHICityLocation cityLocation) {
        if (cityLocation == null) {
            requestPermissions(LOCATION_PERMISSION_REQUEST,
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            displayCityMap(cityLocation);
        }
    }

    private void displayCityMap(EHICityLocation cityLocation) {
        if (cityLocation != null) {
            if (getViewModel().isSearchHistoryEnabled()) {
                getViewModel().addRecentCitySearchLocation(cityLocation);
            }
            startActivity(new LocationsOnMapActivityHelper.Builder()
                    .extraLatLng(new EHILatLng(cityLocation.getCenter()))
                    .extraName(cityLocation.getLongName())
                    .extraFlow(mFlow)
                    .extraFromLdt(isFromLDT)
                    .extraPickupDate(mPickupDate)
                    .extraPickupTime(mPickupTime)
                    .extraDropoffDate(mDropoffDate)
                    .extraDropoffTime(mDropoffTime)
                    .isModify(getViewModel().isModify())
                    .build(this));
        } else {
            startActivity(new LocationsOnMapActivityHelper.Builder()
                    .extraFlow(mFlow)
                    .extraFromLdt(isFromLDT)
                    .extraPickupDate(mPickupDate)
                    .extraPickupTime(mPickupTime)
                    .extraDropoffDate(mDropoffDate)
                    .extraDropoffTime(mDropoffTime)
                    .isModify(getViewModel().isModify())
                    .build(this));
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SearchLocationsActivity.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CITY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationSearch(getViewBinding().searchInput.getText().toString().trim(), isZeroResult()))
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onShowPostalLocation(EHIPostalCodeLocation postalCodeLocation) {
        startActivity(new LocationsOnMapActivityHelper.Builder()
                .extraLatLng(new EHILatLng(postalCodeLocation.getCenter()))
                .extraName(postalCodeLocation.getLongName())
                .extraFlow(mFlow)
                .isModify(getViewModel().isModify())
                .build(this));
    }

    @Override
    public void requestPermissions(final int permissionRequestCode, final PermissionRequester requester, final String... permissions) {
        final List<String> checkedPermissions = PermissionUtils.checkPermissions(this, permissions);
        if (checkedPermissions.isEmpty()) {
            requester.onRequestPermissionResult(permissionRequestCode, permissions, new int[]{PackageManager.PERMISSION_GRANTED});
        } else {
            PermissionUtils.requestCheckedPermissions(this, checkedPermissions, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onRequestPermissionResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        final boolean permissionsGranted = PermissionUtils.areAllPermissionsGranted(grantResults);

        if (permissionsGranted) {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST:
                    displayCityMap(null);
                    break;
            }
        } else {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST:
                    SnackBarUtils.showLocationPermissionSnackBar(this, getWindow().getDecorView().getRootView().findViewById(android.R.id.content));
                    break;
            }
        }
    }

}