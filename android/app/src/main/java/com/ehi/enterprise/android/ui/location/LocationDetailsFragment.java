package com.ehi.enterprise.android.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationDetailsFragmentViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.models.location.EHIWorkingDayInfo;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.interfaces.IToolbarContainer;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.location.interfaces.OnPoliciesInfoClickListener;
import com.ehi.enterprise.android.ui.location.interfaces.OnSolrLocationInfoClickListener;
import com.ehi.enterprise.android.ui.location.widgets.AfterHoursReturnDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.widgets.LocationDetailsConflictMessageView;
import com.ehi.enterprise.android.ui.widget.NotifyingScrollView;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(LocationDetailsFragmentViewModel.class)
public class LocationDetailsFragment extends DataBindingViewModelFragment<LocationDetailsFragmentViewModel, LocationDetailsFragmentViewBinding>
        implements NotifyingScrollView.OnScrollChangedListener {

    public static final String SCREEN_NAME = "LocationDetailsFragment";

    @Extra(value = EHILocation.class, required = false)
    public static final String DETAILS_LOCATION = "ehi.EXTRA_DETAILS_LOCATION";
    @Extra(value = EHISolrLocation.class, required = false)
    public static final String LOCATION = "ehi.EXTRA_LOCATION";
    @Extra(value = int.class, required = false)
    public static final String FLOW = "ehi.EXTRA_FLOW";
    @Extra(value = boolean.class, required = false)
    public static final String SHOW_START_RESERVATION = "ehi.EXTRA_SHOW_START_RESERVATION";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_DROPOFF_DATE = "ehi.EXTRA_DROPOFF_DATE";

    private boolean mShowingFirstTime = true;

    private
    @SearchLocationsActivity.Flow
    int mFlow;

    private boolean mShowStartReservation;

    private OnLocationDetailEventsListener mOnLocationDetailEventsListener = new OnLocationDetailEventsListener() {

        @Override
        public void onFavoriteStateChanged() {
            getViewModel().updateFavoriteState();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationDetailsFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_DETAILS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_FAVORITE.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationDetails(mFlow, getViewModel().getSolrLocation()))
                    .tagScreen()
                    .tagEvent();
        }

        @Override
        public void onCallLocation(String phone) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationDetailsFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_DETAILS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_US.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationDetails(mFlow, getViewModel().getSolrLocation()))
                    .tagScreen()
                    .tagEvent();
            if (getViewModel().getSelectedLocation() != null
                    && getViewModel().getSelectedLocation().getFormattedPhoneNumber(false) != null
                    && !getViewModel().getSelectedLocation().getFormattedPhoneNumber(false).isEmpty()) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSelectedLocation().getFormattedPhoneNumber(false));
            }
        }

        @Override
        public void onShowDirection() {
            if (getViewModel().getSelectedLocation() != null
                    && getViewModel().getSelectedLocation().getGpsCoordinates().getLatLng() != null) {
                IntentUtils.showDirectionsToPlace(getActivity(),
                        getViewModel().getSelectedLocation().getGpsCoordinates().getLatLng(),
                        getViewModel().getSelectedLocation().getAddress().getReadableAddress());

                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationDetailsFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_DETAILS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_GET_DIRECTIONS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.locationDetails(mFlow, getViewModel().getSolrLocation()))
                        .tagScreen()
                        .tagEvent();
            }
        }

        @Override
        public void onShowDirectionFromTerminal() {
            startActivity(new DirectionsFromTerminalActivityHelper.Builder().wayfindingSteps(getViewModel().getSelectedLocation().getWayfindings())
                    .build(getActivity()));
        }

        @Override
        public void onShowLocationDetails(EHILocation location) {

        }

        @Override
        public void onShowAfterHoursDialog() {
            showDialog(new AfterHoursReturnDialogFragmentHelper.Builder().build());
        }
    };

    private OnPoliciesInfoClickListener mOnPoliciesInfoClickListener = new OnPoliciesInfoClickListener() {
        @Override
        public void onClickPolicy(EHIPolicy policy) {
            Intent intent = new PolicyDetailsActivityHelper.Builder()
                    .extraPolicy(policy)
                    .build(getActivity());

            startActivity(intent);
        }

        @Override
        public void onShowMorePolicies() {
            Intent intent = new LocationPoliciesListActivityHelper.Builder()
                    .extraPolicies(getViewModel().getSelectedLocation().getPolicies())
                    .build(getActivity());

            startActivity(intent);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().selectLocationButton) {
                ((OnSolrLocationInfoClickListener) getActivity()).onSelectLocation(getViewModel().getSolrLocation());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_location_details, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().observableScrollView.setOnScrollChangedListener(this);
        getViewBinding().locationDetailsView.setOnLocationDetailEventsListener(mOnLocationDetailEventsListener);
        getViewBinding().policiesView.setOnPoliciesInfoClickListener(mOnPoliciesInfoClickListener);
        getViewBinding().selectLocationButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getViewBinding().liteMapView != null) {
            getViewBinding().liteMapView.onResume();
        }
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationDetailsFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_DETAILS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationDetails(mFlow, getViewModel().getSolrLocation()))
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getViewBinding().liteMapView != null) {
            getViewBinding().liteMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getViewBinding().liteMapView != null) {
            getViewBinding().liteMapView.onDestroy();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDependencies();
        LocationDetailsFragmentHelper.Extractor extractor = new LocationDetailsFragmentHelper.Extractor(this);

        if (extractor.location() != null
                && extractor.showStartReservation() != null) {
            EHILocation ehiLocation = EHILocation.fromSolrLocation(extractor.location());
            //noinspection ResourceType
            mFlow = extractor.flow() != null ? extractor.flow() : SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP;
            //noinspection ResourceType
            if (extractor.showStartReservation() != null) {
                mShowStartReservation = extractor.showStartReservation().booleanValue();
            } else {
                mShowStartReservation = false;
            }
            updateSelectButtonText();
            //will update map in plain (not reactive) way here, since there are some delay which
            //slow down the map
            updateMapView(ehiLocation);
            getViewModel().setSolrLocation(extractor.location());
            getViewModel().loadSelectedLocation(ehiLocation);
            FragmentUtils.addProgressFragment(getActivity());
        }
        if (extractor.detailsLocation() != null) {
            getViewBinding().selectLocationButton.setVisibility(View.GONE);
            getViewModel().setEHILocationToFavorite(extractor.detailsLocation());
            getViewModel().loadSelectedLocation(extractor.detailsLocation());
            FragmentUtils.addProgressFragment(getActivity());
        }
        if (extractor.extraPickupDate() != null) {
            getViewModel().setPickupDate(extractor.extraPickupDate());
        }
        if (extractor.extraDropoffDate() != null) {
            getViewModel().setDropoffDate(extractor.extraDropoffDate());
        }

        if (!(getActivity() instanceof OnSolrLocationInfoClickListener)
                || !(getActivity() instanceof IToolbarContainer)) {
            throw new NotImplementedException();
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().conflictMessageView.visibility(), getViewBinding().locationDetailsConflictMessageView));
        bind(LocationDetailsConflictMessageView.extraSubtitle(getViewModel().conflictMessageView.extraSubtitle(), getViewBinding().locationDetailsConflictMessageView));
        bind(LocationDetailsConflictMessageView.title(getViewModel().conflictMessageView.title(), getViewBinding().locationDetailsConflictMessageView));
        bind(LocationDetailsConflictMessageView.subtitle(getViewModel().conflictMessageView.subtitle(), getViewBinding().locationDetailsConflictMessageView));

        addReaction("SELECTED_LOCATION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHILocation selectedLocation = getViewModel().getSelectedLocation();
                if (selectedLocation != null) {
                    if (mShowingFirstTime) {
                        mShowingFirstTime = false;
                    } else {
                        FragmentUtils.removeProgressFragment(getActivity());
                        updatedWorkingHoursView(selectedLocation.getWeekAfterDate(new Date()));
                    }
                    updateMapView(selectedLocation);
                    updateLocationMainInfo(selectedLocation);
                    updatePoliciesView(selectedLocation.getPolicies());
                    updateWillPickYouUp(selectedLocation);
                }
            }
        });
        addReaction("ERROR_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getErrorWrapper() != null) {
                    FragmentUtils.removeProgressFragment(getActivity());
                    DialogUtils.showErrorDialog(getActivity(), getViewModel().getErrorWrapper());
                    getViewModel().setErrorWrapper(null);
                }
            }
        });
    }

    private void updateWillPickYouUp(EHILocation selectedLocation) {
        if (!selectedLocation.isAirport()
                && getViewModel().getSolrLocation() != null
                && !getViewModel().getSolrLocation().isNalmo()) {
            getViewBinding().pickYouUpArea.getRoot().setVisibility(View.VISIBLE);
        } else {
            getViewBinding().pickYouUpArea.getRoot().setVisibility(View.GONE);
        }
    }

    private void updatePoliciesView(List<EHIPolicy> policies) {
        if (policies != null && policies.size() > 0) {
            getViewBinding().policiesView.setPolicies(policies);
            getViewBinding().policiesView.setVisibility(View.VISIBLE);
            getViewBinding().policiesHeaderView.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().policiesView.setVisibility(View.GONE);
            getViewBinding().policiesHeaderView.setVisibility(View.GONE);
        }
    }

    private void updateLocationMainInfo(EHILocation selectedLocation) {
        getViewBinding().locationDetailsView.setLocation(selectedLocation, getViewModel().shouldShowAfterHoursDropoff());
    }

    private void updatedWorkingHoursView(List<EHIWorkingDayInfo> days) {
        if (days != null && days.size() > 0) {
            getViewBinding().hoursHeaderView.setVisibility(View.VISIBLE);
            getViewBinding().workingHoursView.setVisibility(View.VISIBLE);
            getViewBinding().workingHoursView.setWorkingHoursInfo(days);
        } else {
            getViewBinding().hoursHeaderView.setVisibility(View.GONE);
            getViewBinding().workingHoursView.setVisibility(View.GONE);
        }
    }

    private void updateMapView(EHILocation location) {
        if (location.getGpsCoordinates() != null
                && location.getGpsCoordinates().getLatLng() != null) {
            getViewBinding().liteMapView.showLocationOnMap(location.getGpsCoordinates().getLatLng(),
                    location.getMapPinDrawable(true));
        }
    }

    private void updateSelectButtonText() {
        if (!mShowStartReservation) {
            getViewBinding().selectLocationButton.setText(getString(R.string.location_details_select_location_title));
        } else {
            getViewBinding().selectLocationButton.setText(getString(R.string.location_details_start_reservation_title));
        }
    }

    @Override
    public void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt) {
        final int headerHeight = getViewBinding().liteMapView.getHeight() - ((IToolbarContainer) getActivity()).getToolbarHeight();
        final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);

        ((IToolbarContainer) getActivity()).setToolbarBackgroundAlpha(newAlpha);
        ((IToolbarContainer) getActivity()).setToolbarTitleAlpha(ratio);
        getViewBinding().liteMapView.setTranslationY(view.getScrollY() * 0.5f);
    }


}