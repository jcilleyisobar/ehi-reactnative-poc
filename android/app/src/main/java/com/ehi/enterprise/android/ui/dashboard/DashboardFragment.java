package com.ehi.enterprise.android.ui.dashboard;

import android.Manifest;
import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ScrollView;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DashboardFragmentViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.confirmation.ConfirmationActivityHelper;
import com.ehi.enterprise.android.ui.dashboard.interfaces.OnActiveRentalEventsListener;
import com.ehi.enterprise.android.ui.dashboard.interfaces.RootNavigationListener;
import com.ehi.enterprise.android.ui.dashboard.widget.EplusCellViewAuthenticated;
import com.ehi.enterprise.android.ui.dashboard.widget.QuickStartEmptyView;
import com.ehi.enterprise.android.ui.dashboard.widget.QuickStartRowView;
import com.ehi.enterprise.android.ui.dashboard.widget.UpcomingRentalsView;
import com.ehi.enterprise.android.ui.enroll.EnrollActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.geofence.GeofenceRegistrationServiceHelper;
import com.ehi.enterprise.android.ui.location.DirectionsFromTerminalActivityHelper;
import com.ehi.enterprise.android.ui.location.LocationDetailsActivityHelper;
import com.ehi.enterprise.android.ui.location.LocationsOnMapActivityHelper;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerFragment;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerItem;
import com.ehi.enterprise.android.ui.notification.NotificationPromptView;
import com.ehi.enterprise.android.ui.notification.NotificationSchedulerServiceHelper;
import com.ehi.enterprise.android.ui.reservation.ItineraryActivityHelper;
import com.ehi.enterprise.android.ui.reservation.promotions.WeekendSpecialDetailsFragmentHelper;
import com.ehi.enterprise.android.ui.settings.SettingsFragmentHelper;
import com.ehi.enterprise.android.ui.widget.NotifyingScrollView;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.SnackBarUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(DashboardViewModel.class)
public class DashboardFragment extends DataBindingViewModelFragment<DashboardViewModel, DashboardFragmentViewBinding>
        implements NotifyingScrollView.OnScrollChangedListener, IRootMenuScreen {

    //region fields
    public static final String SCREEN_NAME = "DashboardFragment";
    public static final int LOCATION_PERMISSION_REQUEST = 10;
    public static final int GEOFENCE_LOCATION_PERMISSION_REQUEST = 11;
    private static final int ANIMATION_DURATION = 1000;
    public static final String DASHBOARD_IMAGE_REACTION = "DASHBOARD IMAGE REACTION";
    public static final String PARALLAX_CALCULATION_REACTION = "PARALLAX_CALCULATION_REACTION";
    public static final String USER_PROFILE_REACTION = "USER_PROFILE_REACTION";
    public static final String QUICK_START_VIEWS_REACTION = "QUICK_START_VIEWS_REACTION";
    public static final String POPULATE_UPCOMING_ACTIVE_VIEW_REACTION = "POPULATE_UPCOMING_ACTIVE_VIEW_REACTION";
    public static final String RESERVATION_REQUEST_REACTION = "RESERVATION_REQUEST_REACTION";
    public static final String SUPPORT_INFO_ERROR_REACTION = "SUPPORT_INFO_ERROR_REACTION";
    public static final String ERROR_RESPONSE_REACTION = "ERROR_RESPONSE_REACTION";
    public static final String WAY_FINDINGS_REACTION = "WAY_FINDINGS_REACTION";
    public static final String EMBEDDED_PROGRESS_REACTION = "EMBEDDED_PROGRESS_REACTION";
    public static final String NOTIFICATION_LOCATION_REACTION = "NOTIFICATION_LOCATION_REACTION";
    private static final String ANIMATION_ENDED_REACTION = "ANIMATION_ENDED_REACTION";
    private static final String SHOW_WEEKEND_SPECIAL_REACTION = "SHOW_WEEKEND_SPECIAL_REACTION";
    private boolean mAnimating = false;
    private Toolbar mToolbar;
    private int mSearchEditTextWidth = -1;
    private float mNewWidth;
    private AnimationDrawable mGenericSpinnerAnim;
    private int mScreenHeight;
    private int mCurrentContainerHeight = 0;
    private View mFillerView;
    private RentalsUpdateListener mRentalsUpdateListener;
    //endregion

    //region Listeners

    public interface RentalsUpdateListener {
        void updateRentals(boolean hasRentals);
    }

    private DialogInterface.OnClickListener mClearRecentActivitiesDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(@NonNull DialogInterface dialogInterface, int i) {
            getViewModel().clearRecentActivities();
        }
    };

    private PermissionRequester mPermissionRequester = new PermissionRequester() {
        @Override
        public void onRequestPermissionResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
            final boolean permissionsGranted = PermissionUtils.areAllPermissionsGranted(grantResults);
            if (permissionsGranted) {
                switch (requestCode) {
                    case LOCATION_PERMISSION_REQUEST:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_LOCATION.value, DashboardFragment.SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_PROMPT.value)
                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ALLOW.value)
                                .addDictionary(getViewModel().getAnalyticsMap())
                                .tagScreen()
                                .tagEvent();
                        showLocationsActivity();
                        break;
                    case GEOFENCE_LOCATION_PERMISSION_REQUEST:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_NOTIFICATION.value, DashboardFragment.SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_PROMPT.value)
                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ALLOW.value)
                                .addDictionary(getViewModel().getAnalyticsMap())
                                .tagScreen()
                                .tagEvent();
                        getViewModel().notificationPromptConfirmClicked();
                        break;
                }
            } else {
                switch (requestCode) {
                    case LOCATION_PERMISSION_REQUEST:
                        if (getView() != null && getView().getRootView() != null) {
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_LOCATION.value, DashboardFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_PROMPT.value)
                                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DO_NOT_ALLOW.value)
                                    .addDictionary(getViewModel().getAnalyticsMap())
                                    .tagScreen()
                                    .tagEvent();
                            SnackBarUtils.showLocationPermissionSnackBar(getActivity(), getViewBinding().getRoot());
                        }
                        break;
                    case GEOFENCE_LOCATION_PERMISSION_REQUEST:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_NOTIFICATION.value, DashboardFragment.SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_PROMPT.value)
                                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DO_NOT_ALLOW.value)
                                .addDictionary(getViewModel().getAnalyticsMap())
                                .tagScreen()
                                .tagEvent();
                        getViewModel().notificationPromptDenyClicked();
                        break;
                }
            }
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().dashboardSearchClickListener) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                        .state(getViewModel().getScreenState())
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SEARCH_BOX.value)
                        .addDictionary(getViewModel().getAnalyticsMap())
                        .tagScreen()
                        .tagEvent();
                if (!mAnimating) {
                    animateUp();
                }
            } else if ((view == getViewBinding().quickStartView || view == getViewBinding().downArrowIcon)) {
                animateScrollViewUp();
            } else if (view == getViewBinding().eplusJoinNowButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                        .state(getViewModel().getScreenState())
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_JOIN_NOW.value)
                        .addDictionary(getViewModel().getAnalyticsMap())
                        .tagScreen()
                        .tagEvent();
                Intent intent = new Intent(getActivity(), EnrollActivity.class);
                startActivity(intent);
            } else if (view == getViewBinding().eplusCellViewUnauthenticated) {
                showModal(getActivity(), new LoginFragmentHelper.Builder().build());
            } else if (view == getViewBinding().dashboardInputSearchNearbyIcon) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                        .state(getViewModel().getScreenState())
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NEARBY.value)
                        .addDictionary(getViewModel().getAnalyticsMap())
                        .tagScreen()
                        .tagEvent();
                if (getActivity() instanceof PermissionRequestHandler) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_PROMPT.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, null)
                            .addDictionary(getViewModel().getAnalyticsMap())
                            .tagScreen()
                            .tagEvent();
                    PermissionRequestHandler permissionRequestHandler = (PermissionRequestHandler) getActivity();
                    permissionRequestHandler.requestPermissions(LOCATION_PERMISSION_REQUEST,
                            mPermissionRequester,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    throw new IllegalStateException("Activity needs to implement PermissionRequestHandler");
                }
            } else if (view == getViewBinding().clearRecentActivityButton) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dashboard_clear_quickstart_alert_title)
                        .setMessage(getString(R.string.dashboard_clear_quickstart_alert_details))
                        .setPositiveButton(getString(R.string.dashboard_clear_quickstart_alert_remove), mClearRecentActivitiesDialogClickListener)
                        .setNegativeButton(R.string.standard_button_cancel, null)
                        .show();
            }
        }
    };

    private View.OnClickListener mSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View view) {
            Intent intent = new ModalActivityHelper.Builder()
                    .fragmentClass(new SettingsFragmentHelper.Builder().build().getClass())
                    .build(getActivity());

            startActivity(intent);
        }
    };

    private View.OnClickListener mQuickstartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View view) {

            ReservationInformation holder = ((QuickStartRowView) view).getHolder();
            if (holder.getPickupLocation().isFavorite()) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                        .state(getViewModel().getScreenState())
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_FAVORITE.value)
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SELECTED.value)
                        .addDictionary(getViewModel().getAnalyticsMap())
                        .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(holder.getPickupLocation(),
                                holder.getReturnLocation(),
                                holder.getPickupDate(),
                                holder.getPickupTime()))
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
            } else {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                        .state(getViewModel().getScreenState())
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REUSE.value)
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_LOCATION_SELECTED.value)
                        .addDictionary(getViewModel().getAnalyticsMap())
                        .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(holder.getPickupLocation(),
                                holder.getReturnLocation(),
                                holder.getPickupDate(),
                                holder.getPickupTime()))
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
            }
            startActivity(new ItineraryActivityHelper.Builder().abandonedReservtionInformation(holder).build(getActivity()));
        }
    };

    private OnActiveRentalEventsListener mOnActiveRentalEventsListener = new OnActiveRentalEventsListener() {
        @Override
        public void onReturnInstructionsClicked() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_RETURN_INSTRUCTIONS.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();

            final EHIPolicy afterHoursPolicy = getViewModel().getActiveRentalLocationDetails() != null ? getViewModel().getActiveRentalLocationDetails().getAfterHoursPolicy()
                    : null;
            if (afterHoursPolicy != null) {
                showModalDialog(getActivity(), new ModalTextDialogFragmentHelper.Builder().title(afterHoursPolicy.getDescription())
                        .text(afterHoursPolicy.getPolicyText())
                        .buttonText(getString(R.string.modal_default_dismiss_title))
                        .build());
            } else {
                DialogUtils.showDialogWithTitleAndText(getActivity(), getString(R.string.location_services_generic_error), "");
            }
        }

        @Override
        public void onGetDirectionsClicked() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_GET_DIRECTIONS.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();
            final EHILocation returnLocations = getViewModel().getCurrentRentals().get(0).getReturnLocation();
            String returnLocationName = returnLocations.getName();
            double returnLocationLat = returnLocations.getGpsCoordinates().getLatitude();
            double returnLocationLong = returnLocations.getGpsCoordinates().getLongitude();
            IntentUtils.getDirectionsFromCurrentLocation(getActivity().getApplicationContext(),
                    returnLocationLat,
                    returnLocationLong,
                    returnLocationName);
        }

        @Override
        public void onExtendRentalClicked() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EXTEND_RENTAL.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();
            final EHITripSummary ehiTripSummary = getViewModel().getCurrentRentals().get(0);
            String ticketNumber = EHITextUtils.isEmpty(ehiTripSummary.getTicketNumber()) ? "" : ehiTripSummary.getTicketNumber();

            showModalDialog(getActivity(), new ExtendRentalFragmentHelper.Builder()
                    .confirmationNumber(ticketNumber)
                    .phoneNumber(getViewModel().getSupportPhoneNumber())
                    .build());
        }

        @Override
        public void onViewRentalDetailsClicked() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_DETAILS.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();
            final EHITripSummary ehiTripSummary = getViewModel().getCurrentRentals().get(0);
            if (!TextUtils.isEmpty(ehiTripSummary.getConfirmationNumber())) {
                getViewModel().requestReservation(ehiTripSummary);
            }
        }

        @Override
        public void onFindGasStationsClicked(EHILocation returnLocation) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_GAS_STATION.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();
            if (returnLocation != null
                    && returnLocation.getGpsCoordinates() != null) {
                IntentUtils.findNearbyGasStations(getActivity(), returnLocation.getGpsCoordinates());
            }
        }

        @Override
        public void onLocationNameClicked(EHILocation location) {
            startActivity(new LocationDetailsActivityHelper.Builder()
                    .gboLocation(location)
                    .build(getActivity()));
        }

        @Override
        public void onRateMyRideButtonClicked(String rateUrl) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_RATE_VEHICLE.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();

            IntentUtils.openUrlViaCustomTab(getActivity(), rateUrl);
        }
    };

    private UpcomingRentalsView.UpcomingRentalsListener mUpcomingRentalsListener = new UpcomingRentalsView.UpcomingRentalsListener() {
        @Override
        public void onGetDirectionsClicked(EHITripSummary ehiTripSummary) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_GET_DIRECTIONS.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();

            IntentUtils.getDirectionsFromCurrentLocation(getActivity(),
                    ehiTripSummary.getPickupLocation().getGpsCoordinates().getLatitude(),
                    ehiTripSummary.getPickupLocation().getGpsCoordinates().getLongitude(),
                    ehiTripSummary.getPickupLocation().getName());
        }

        @Override
        public void onViewDetailsClicked(EHITripSummary ehiTripSummary) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                    .state(getViewModel().getScreenState())
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_DETAILS.value)
                    .addDictionary(getViewModel().getAnalyticsMap())
                    .tagScreen()
                    .tagEvent();
            getViewModel().requestReservation(getViewModel().getUpcomingRentals().get(0));
        }

        @Override
        public void onLocationNameClicked(EHILocation location) {
            startActivity(new LocationDetailsActivityHelper.Builder()
                    .gboLocation(location)
                    .build(getActivity()));
        }

        @Override
        public void onDirectionsFromTerminalClicked(List<EHIWayfindingStep> wayfindings) {
            startActivity(new DirectionsFromTerminalActivityHelper.Builder().wayfindingSteps(wayfindings).build(getActivity()));
        }
    };

    private QuickStartRowView.QuickStartRowDismissListener mQuickStartRowDismissListener = new QuickStartRowView.QuickStartRowDismissListener() {
        @Override
        public void onDismiss(final View view, final ReservationInformation reservationInformation) {
            final Pair<Integer, View> integerViewPair = removeViewFromQuickStartSubViews(view);
            if (integerViewPair != null) {
                if (integerViewPair.second instanceof QuickStartRowView) {
                    final QuickStartRowView quickStartRowView = (QuickStartRowView) integerViewPair.second;
                    int indexRemovedFromManager = 0;
                    switch (quickStartRowView.getRowType()) {
                        case QuickStartRowView.ABANDONED:
                            indexRemovedFromManager = getViewModel().removeAbandonedReservation(reservationInformation);
                            break;
                        case QuickStartRowView.FAVORITE:
                            getViewModel().removeFavoriteLocation(reservationInformation);
                            break;
                        case QuickStartRowView.RECENT:
                            indexRemovedFromManager = getViewModel().removeRecentReservation(reservationInformation);
                            break;
                    }
                    final int finalIndexRemovedFromManager = indexRemovedFromManager;
                    Snackbar.make(getViewBinding().getRoot(), R.string.dashboard_clear_quickstart_alert_remove, Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.ehi_primary))
                            .setAction(R.string.standard_undo_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//									QuickStartRowView newQuickstartRowView = new QuickStartRowView(getViewBinding().quickStartCells.getContext());
//									newQuickstartRowView.setupView(reservationInformation, quickStartRowView.getRowType());
//									newQuickstartRowView.setCustomOnClickListener(mQuickstartClickListener);
//									newQuickstartRowView.setOnDismissListener(mQuickStartRowDismissListener);
//									addViewToQuickstartSubviews(integerViewPair.first, newQuickstartRowView, true);
                                    switch (quickStartRowView.getRowType()) {
                                        case QuickStartRowView.ABANDONED:
                                            getViewModel().addAbandonedReservation(finalIndexRemovedFromManager, reservationInformation);
                                            break;
                                        case QuickStartRowView.FAVORITE:
                                            getViewModel().addFavoriteLocation(reservationInformation);
                                            break;
                                        case QuickStartRowView.RECENT:
                                            getViewModel().addRecentReservation(reservationInformation);
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            }
        }

        @Override
        public void onDrag(boolean isDragging) {
            getViewBinding().dashboardScrollView.setScrollable(!isDragging);
        }
    };

    private NotificationPromptView.NotificationPromptListener mNotificationPromptViewListener = new NotificationPromptView.NotificationPromptListener() {
        @Override
        public void onConfirmClicked() {
            ((PermissionRequestHandler) getActivity()).requestPermissions(GEOFENCE_LOCATION_PERMISSION_REQUEST,
                    mPermissionRequester,
                    Manifest.permission.ACCESS_FINE_LOCATION);

        }

        @Override
        public void onDenyClicked() {
            getViewModel().notificationPromptDenyClicked();
        }
    };

    private Runnable recalculateParallaxRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                calculateParallaxViewHeight(getViewModel().hasRental.getValue());
            } catch (Exception e) {
                DLog.e("CalculateParallaxVieHeight", e);
            }
        }
    };

    private View.OnClickListener mWeekendSpecialClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showModal(getActivity(), new WeekendSpecialDetailsFragmentHelper.Builder().build());
        }
    };

    private EplusCellViewAuthenticated.onEplusCellListener mEplusCellListener = new EplusCellViewAuthenticated.onEplusCellListener() {
        @Override
        public void onProfileViewRedirect() {
            ((NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity()).selectDrawerItem(NavigationDrawerItem.ID_MY_PROFILE);
        }

        @Override
        public void onRewardsViewRedirect() {
            ((NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity()).selectDrawerItem(NavigationDrawerItem.ID_MY_REWARDS);
        }
    };

//endregion

    //region Callbacks

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mRentalsUpdateListener = (RentalsUpdateListener) getActivity();
        } catch (ClassCastException e) {
            DLog.d(TAG, e);
        }
    }

    @Override
    public void onScrollChanged(ScrollView view, int l, int scrollY, int oldl, int oldScroll) {
        if (getViewBinding().dashboardActiveRentalView.isShown() || getViewBinding().dashboardUpcomingRentalView.isShown()) {
            if (getViewBinding().parallaxView.getTranslationY() != 0) {
                getViewBinding().parallaxView.setTranslationY(0);
            }
            downArrowOpacity();
            return;
        }

        float adjustedScroll = getViewBinding().dashboardScrollView.getScrollY()
                - getViewBinding().eplusCellViewAuthenticated.getHeight()
                - getViewBinding().eplusCellViewUnauthenticated.getHeight();

        if (adjustedScroll > 0 && !getViewBinding().dashboardActiveRentalView.isShown()) {
            getViewBinding().parallaxView.setTranslationY(adjustedScroll * 0.5f);
        } else {
            getViewBinding().parallaxView.setTranslationY(0);
        }

        if (getViewBinding().quickstartContainer.getY() <= (getViewBinding().dashboardScrollView.getScrollY())) {
            getViewBinding().startReservationContainer.setTranslationY(getViewBinding().dashboardScrollView.getScrollY() - getViewBinding().quickstartContainer.getY());
        } else if (getViewBinding().startReservationContainer.getTranslationY() != 0) {
            getViewBinding().startReservationContainer.setTranslationY(0);
        }

        downArrowOpacity();
    }

    private void downArrowOpacity() {
        float ratio;

        float screenDif = (getEplusViewHeight() + getViewBinding().parallaxContainer.getHeight()
                + getViewBinding().quickStartView.getHeight() + getViewBinding().startReservationContainer.getHeight()) - mScreenHeight;
        if (screenDif < 0) {
            screenDif = 0;
        }

        ratio = 1f - ((float) getViewBinding().dashboardScrollView.getScrollY() - screenDif) / ((float) getEplusViewHeight()
                + getViewBinding().parallaxView.getHeight() - screenDif); //fullOpacityHeight - getViewBinding().dashboardScrollView.getScrollY();

        if (ratio < 0) {
            ratio = 0;
        }

        getViewBinding().downArrowIcon.setAlpha(ratio);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_dashboard, container);
        initView();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(getActivity() instanceof NavigationDrawerFragment.NavigationDrawerCallbacks)) {
            throw new NotImplementedException();
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorView.visibility(getViewModel().dashboardActiveRentalViewState.visibility(), getViewBinding().dashboardActiveRentalView));
        bind(ReactorView.visibility(getViewModel().dashboardUpcomingRentalViewState.visibility(), getViewBinding().dashboardUpcomingRentalView));
        bind(ReactorView.visibility(getViewModel().dashboardImageContainerViewState.visibility(), getViewBinding().dashboardDefaultImageContainer));
        bind(ReactorTextView.text(getViewModel().dashboardImageCaptionViewState.text(), getViewBinding().imageCaptionText));
        bind(ReactorView.visibility(getViewModel().eplusAuthenticatedCellViewState.visibility(), getViewBinding().eplusCellViewAuthenticated));
        bind(ReactorView.visibility(getViewModel().eplusUnauthenticatedCellViewState.visibility(), getViewBinding().eplusCellViewUnauthenticated));
        bind(ReactorView.visibility(getViewModel().dashboardEplusExtendedView.visibility(), getViewBinding().eplusExtendedView));
        bind(ReactorView.visibility(getViewModel().notificationPromptViewState.visibility(), getViewBinding().notificationPromptView));
        bind(ReactorView.visibility(getViewModel().clearRecentActivityButtonViewState.visibility(), getViewBinding().clearRecentActivityButton));

        addReaction(DASHBOARD_IMAGE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().dashboardImageViewState.imageResource() != null && getViewModel().dashboardImageViewState.imageResource().getValue() != null) {
                    Picasso.with(getActivity())
                            .load(getViewModel().dashboardImageViewState.imageResource().getValue())
                            .config(Bitmap.Config.RGB_565)
                            .into(getViewBinding().dashboardDefaultImage);
                }
            }
        });

        addReaction(ANIMATION_ENDED_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().hasAnimationEnded()) {
                    ((RootNavigationListener) getActivity()).SearchAnimationEnded();
                }
            }
        });

        addReaction(SHOW_WEEKEND_SPECIAL_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().weekendSpecialViewState.visible().getValue()) {
                    getViewBinding().weekendSpecial.setVisibility(View.VISIBLE);
                    EHIContract contract = getViewModel().getWeekendSpecialContract();
                    getViewBinding().weekendSpecial.setTitle(contract == null ? "" : contract.getContractName());
                } else {
                    getViewBinding().weekendSpecial.setVisibility(View.GONE);
                }
            }
        });

        addReaction(PARALLAX_CALCULATION_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().hasRental.getValue();
                getViewBinding().quickStartCells.post(recalculateParallaxRunnable);
            }
        });

        addReaction(USER_PROFILE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ProfileCollection profileCollection = getViewModel().getUserProfileCollection();
                if (profileCollection != null && getViewModel().isUserLoggedIn()) {
                    getViewBinding().eplusCellViewAuthenticated.setupView(profileCollection);
                }
            }
        });

        addReaction(QUICK_START_VIEWS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<ReservationInformation> reservations = getViewModel().getAbandonedReservations();
                clearQuickStartCells();
                populateQuickStartView(reservations);
            }
        });

        addReaction(POPULATE_UPCOMING_ACTIVE_VIEW_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHITripSummary> activeRentals = getViewModel().getCurrentRentals();
                List<EHITripSummary> upcomingRentals = getViewModel().getUpcomingRentals();
                EHILocation locationDetails = getViewModel().getActiveRentalLocationDetails();
                if (activeRentals != null && !activeRentals.isEmpty()) {
                    EHITripSummary summary = activeRentals.get(0);
                    if (summary != null && locationDetails != null) {
                        summary.setReturnLocation(locationDetails);
                    }
                    getViewBinding().dashboardActiveRentalView.setIsCurrentRentalAfterHours(getViewModel().isCurrentRentalReturnInAfterHours());
                    getViewBinding().dashboardActiveRentalView.setTripSummary(summary);
                    if (mRentalsUpdateListener != null) {
                        mRentalsUpdateListener.updateRentals(!ListUtils.isEmpty(activeRentals));
                    }
                } else if (upcomingRentals != null && !upcomingRentals.isEmpty()) {
                    EHITripSummary summary = upcomingRentals.get(0);
                    if (summary != null && locationDetails != null) {
                        summary.setPickupLocation(locationDetails);
                    }
                    getViewBinding().dashboardUpcomingRentalView.setTrip(summary);
                }
            }
        });

        addReaction(RESERVATION_REQUEST_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRequestReservation() != null) {
                    Intent intent = new ConfirmationActivityHelper.Builder()
                            .extraReservation(getViewModel().getRequestReservation())
                            .isModify(false)
                            .exitGoesHome(false)
                            .build(getActivity());
                    startActivity(intent);
                    getViewModel().setRequestedReservation(null);
                }
            }
        });

        addReaction(SUPPORT_INFO_ERROR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getSupportInfoErrorWrapper() != null) {
                    if ((BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat"))
                            && getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).toggleServicesEndpoint();
                    } else {
                        showModalDialog(getActivity(), new SupportInfoDialogFragmentHelper.Builder().build());
                        getViewModel().setSupportInfoErrorWrapper(null);
                    }
                }
            }
        });

        addReaction(ERROR_RESPONSE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getErrorResponse() != null) {
                    DialogUtils.showErrorDialog(getActivity(), getViewModel().getErrorResponse());
                    getViewModel().setErrorResponse(null);
                }
            }
        });

        addReaction(WAY_FINDINGS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHIWayfindingStep> wayfindings = getViewModel().getWayfindings();
                if (wayfindings != null) {
                    getViewBinding().dashboardUpcomingRentalView.setWayfindings(wayfindings);
                }
            }
        });

        addReaction(EMBEDDED_PROGRESS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                int progress = getViewModel().progressCounter.getValue();
                showProgress(progress > 0);
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().hasCountryListLoaded()) {
                    showStartUpModals();
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isWeekendSpecialContractRequestDone()) {
                    showWeekendSpecialModal();
                }
            }
        });

        addReaction(NOTIFICATION_LOCATION_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHITripSummary> currentRentals = getViewModel().getCurrentRentals();
                List<EHITripSummary> upcomingRentals = getViewModel().getUpcomingRentals();

                if (getViewModel().shouldScheduleReturnNotifications()) {
                    setUpTimeNotifications(currentRentals);
                }

                if (getViewModel().shouldSchedulePickupNotifications()) {
                    setUpTimeNotifications(upcomingRentals);
                }

                if (!PermissionUtils.checkPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION).isEmpty()) {
                    //The user enabled the enterprise rental assistant (thus granting us the ACCESS_FINE_LOCATION permission
                    //but then the user removed the permission, so we're going to disable the rental assistant
                    if (getViewModel().isEnterpriseRentalAssistantEnabled()) {
                        getViewModel().setEnterpriseRentalAssistantEnabled(false);
                    }
                } else {
                    if (!getViewModel().getManagers().getGeofenceManger().isEnabled()) {
                        getViewModel().getManagers().getGeofenceManger().setEnabled(true);
                    }
                    if (getViewModel().isEnterpriseRentalAssistantEnabled()) {
                        setUpGeoNotifications(currentRentals);
                        setUpGeoNotifications(upcomingRentals);
                    }
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldShowSurveyDialog()) {
                    showSurveyModal();
                    getViewModel().setSurveyDialogShowed();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("");
    }

    public void showStartUpModals() {
        getViewModel().generateDefaultPreferredRegion();
        if (getViewModel().needPromptDataTrackingOnFirstRun()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.dashboard_data_tracking_notification))
                    .setPositiveButton(getString(R.string.standard_ok_text), null)
                    .show();
            getViewModel().setFirstStartInGerman(false);
        } else if (getViewModel().needShowDataCollectionReminder()) {
            showModalDialog(
                    getActivity(),
                    new DataTrackingDialogFragmentHelper.Builder().build()
            );
        } else if (getViewModel().needShowRegionsChoice()) {
            showModalDialog(getActivity(), new RegionChoiceDialogFragmentHelper.Builder().build());
        } else if (getViewModel().needToCheckElegibilityToSurvey()) {
            getViewModel().checkIfIsElegibleToTrack();
        }
    }

    private void showWeekendSpecialModal() {
        if (getViewModel().shouldShowWeekendSpecialModal()) {
            showModalDialog(
                    getActivity(),
                    new WeekendSpecialEducationalDialogFragmentHelper.Builder().build()
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScreenHeight = DisplayUtils.getScreenHeight(getActivity());
        getViewBinding().parallaxContainer.post(new Runnable() {
            @Override
            public void run() {
                restoreViewsState();
            }
        });
    }

    @Override
    public void trackScreenChange() {
        getViewModel().trackScreenChange();
    }

//endregion

    //region View State
    private void initView() {
        mToolbar = ((MainActivity) getActivity()).getToolbar();
        getViewBinding().progressSpinner.setBackgroundResource(R.drawable.book_loading_spinner);
        mGenericSpinnerAnim = (AnimationDrawable) getViewBinding().progressSpinner.getBackground();
        getViewBinding().eplusCellViewAuthenticated.setEplusCellListener(mEplusCellListener);
        getViewBinding().eplusCellViewUnauthenticated.setOnClickListener(mOnClickListener);
        getViewBinding().quickStartView.setOnClickListener(mOnClickListener);
        getViewBinding().dashboardSearchClickListener.setOnClickListener(mOnClickListener);
        getViewBinding().downArrowIcon.setOnClickListener(mOnClickListener);
        getViewBinding().dashboardScrollView.setOnScrollChangedListener(this);
        getViewBinding().eplusJoinNowButton.setOnClickListener(mOnClickListener);
        getViewBinding().dashboardInputSearchNearbyIcon.setOnClickListener(mOnClickListener);
        getViewBinding().dashboardActiveRentalView.setOnActiveRentalEventsListener(mOnActiveRentalEventsListener);
        getViewBinding().dashboardUpcomingRentalView.setUpcomingRentalsListener(mUpcomingRentalsListener);
        getViewBinding().notificationPromptView.setListener(mNotificationPromptViewListener);
        getViewBinding().weekendSpecial.setOnGetStartedClickListener(mWeekendSpecialClickListener);
        getViewBinding().clearRecentActivityButton.setOnClickListener(mOnClickListener);
    }

    public void showProgress(boolean showProgress) {
        if (showProgress) {
            getViewBinding().progressSpinnerContainer.setVisibility(View.VISIBLE);
            mGenericSpinnerAnim.start();
        } else {
            getViewBinding().progressSpinnerContainer.setVisibility(View.GONE);
            mGenericSpinnerAnim.stop();
        }
    }

    public boolean isAnimating() {
        return mAnimating;
    }

    private int getEplusViewHeight() {
        if (getViewBinding().eplusCellViewAuthenticated.getVisibility() == View.VISIBLE) {
            return getViewBinding().eplusCellViewAuthenticated.getHeight();
        }

        return getViewBinding().eplusCellViewUnauthenticated.getVisibility() == View.VISIBLE
                ? getViewBinding().eplusCellViewUnauthenticated.getHeight()
                : 0;
    }

    private void resetFromAnimation() {
        getViewBinding().dashboardScrollView.setAlpha(1);
        getViewBinding().animationLayer.setVisibility(View.GONE);

        ((View) getViewBinding().getRoot().getParent()).setY(mToolbar.getHeight());

        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setY(0);

        final View text = getViewBinding().dummySearchEditText;

        getViewBinding().startRentalsDivider.setAlpha(1);

        getViewBinding().dummyDashboardSearchLocationsArea.setX(0);

        getViewBinding().dummySearchWrapper.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        //noinspection deprecation
        getViewBinding().dummySearchWrapper.setBackground(getResources().getDrawable(R.drawable.transparent_to_green_transition));

        ViewGroup.LayoutParams textParams = text.getLayoutParams();
        textParams.width = mSearchEditTextWidth;
        text.setLayoutParams(textParams);

        getViewBinding().dashboardSearchLocationsArea.setVisibility(View.VISIBLE);

        View xView = getViewBinding().searchLocationClearInput;
        xView.setAlpha(0);

        getViewBinding().dummyDashboardInputSearchNearbyIcon.setAlpha(1);
        mSearchEditTextWidth = -1;
    }

    private void animateScrollViewUp() {
        final float destination = getViewBinding().quickstartContainer.getY();
        final float start = getViewBinding().dashboardScrollView.getScrollY();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                float distance = (destination - start) * interpolatedTime;
                getViewBinding().dashboardScrollView.scrollTo(0, (int) (start + distance));
            }
        };

        animation.setDuration(400);

        getViewBinding().dashboardScrollView.startAnimation(animation);
    }


    private void calculateParallaxViewHeight(boolean noRentals) {
        final ViewGroup.LayoutParams parallaxContainerParams = getViewBinding().parallaxContainer.getLayoutParams();
        if (noRentals || getViewModel().progressCounter.getRawValue() > 0) {
            parallaxContainerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getViewBinding().parallaxContainer.setLayoutParams(parallaxContainerParams);
        } else {
            View dashboardView = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);

            int desiredHeight = (dashboardView.getHeight()
                    - mToolbar.getHeight()
                    - getViewBinding().startReservationContainer.getHeight()
                    - getViewBinding().quickStartView.getHeight());

            float yValue = getViewBinding().startReservationContainer.getY()
                    + getViewBinding().quickstartContainer.getY()
                    - getViewBinding().startReservationContainer.getTranslationY();

            if (yValue > desiredHeight) {
                parallaxContainerParams.height = parallaxContainerParams.height - ((int) yValue - desiredHeight);
                getViewBinding().parallaxContainer.setLayoutParams(parallaxContainerParams);
            } else if (yValue < desiredHeight) {
                parallaxContainerParams.height = (desiredHeight - (int) yValue) + parallaxContainerParams.height;
                getViewBinding().parallaxContainer.setLayoutParams(parallaxContainerParams);
            }
        }
    }

    private void restoreViewsState() {
        if (mSearchEditTextWidth != -1 || mToolbar.getVisibility() != View.VISIBLE) {
            resetFromAnimation();
        }
        getViewBinding().downArrowIcon.post(new Runnable() {
            @Override
            public void run() {
                getViewBinding().downArrowIcon.setY(
                        getViewBinding().startReservationContainer.getHeight() - (getViewBinding().downArrowIcon.getHeight() / 2)
                );
            }
        });
    }
//endregion

    //region Quickstart row view
    private void populateQuickStartView(List<ReservationInformation> reservations) {

        final int childCount = getViewBinding().quickStartCells.getChildCount();
        final int index = childCount - 3; // 3 is the number of views we should offset
        QuickStartRowView quickstartRowView;

        if (reservations != null) {
            for (ReservationInformation abandonedReservation : reservations) {
                quickstartRowView = new QuickStartRowView(getViewBinding().quickStartCells.getContext());
                quickstartRowView.setupView(abandonedReservation, QuickStartRowView.ABANDONED);
                quickstartRowView.setCustomOnClickListener(mQuickstartClickListener);
                quickstartRowView.setOnDismissListener(mQuickStartRowDismissListener);
                addViewToQuickstartSubviews(index, quickstartRowView, false);
            }
        }

        List<ReservationInformation> recentRentals = getViewModel().getRecentReservation();
        if (recentRentals != null) {
            for (ReservationInformation recentRental : recentRentals) {
                quickstartRowView = new QuickStartRowView(getViewBinding().quickStartCells.getContext());
                quickstartRowView.setupView(recentRental, QuickStartRowView.RECENT);
                quickstartRowView.setCustomOnClickListener(mQuickstartClickListener);
                quickstartRowView.setOnDismissListener(mQuickStartRowDismissListener);
                addViewToQuickstartSubviews(index, quickstartRowView, false);
            }
        }

        Map<String, EHISolrLocation> favorites = getViewModel().getFavoriteLocations();
        for (Map.Entry mapEntry : favorites.entrySet()) {
            quickstartRowView = new QuickStartRowView(getViewBinding().quickStartCells.getContext());
            quickstartRowView.setupView(
                    new ReservationInformation((EHISolrLocation) mapEntry.getValue(), null, null, null, null, null, null, 25),
                    QuickStartRowView.FAVORITE);
            quickstartRowView.setCustomOnClickListener(mQuickstartClickListener);
            quickstartRowView.setOnDismissListener(mQuickStartRowDismissListener);
            addViewToQuickstartSubviews(index, quickstartRowView, false);

        }

        // if nothing was added
        if (getViewBinding().quickStartCells.getChildCount() == childCount) {
            QuickStartEmptyView quickstartView = new QuickStartEmptyView(getViewBinding().quickStartCells.getContext());
            addViewToQuickstartSubviews(1, quickstartView, false);
            quickstartView.setupView(getViewModel().isTrackingEnabled());
            if (!getViewModel().isTrackingEnabled()) {
                quickstartView.getLinkView().setOnClickListener(mSettingsClickListener);
            }
        }

        getViewModel().updateClearRecentActivitiesButtonVisibility();

        getViewBinding().quickStartCells.post(new Runnable() {
            @Override
            public void run() {
                getViewBinding().quickStartCells.setPadding(0, getViewBinding().startReservationContainer.getHeight(), 0, 0);
            }
        });

        getViewBinding().quickstartContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if (mCurrentContainerHeight != getViewBinding().quickstartContainer.getHeight()) {
                        mCurrentContainerHeight = getViewBinding().quickstartContainer.getHeight();

                        int paddingHeight = (int) (2 * getResources().getDimension(R.dimen.dashboard_box_padding));

                        int totalHeight = getViewBinding().quickstartContainer.getHeight()
                                - (mFillerView == null ? 0 : mFillerView.getHeight())
                                + paddingHeight;
                        int screenHeight = mScreenHeight - mToolbar.getHeight();
                        if (totalHeight < screenHeight) {
                            if (mFillerView != null) {
                                getViewBinding().quickStartCells.removeView(mFillerView);
                            }
                            mFillerView = new View(getActivity());
                            mFillerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    screenHeight - totalHeight));
                            boolean hasAbandonedReservations = getViewModel().getAbandonedReservations() != null && getViewModel().getAbandonedReservations().size() > 0;
                            Drawable background = getViewBinding().quickStartView.getBackground();
                            if (!hasAbandonedReservations && background instanceof ColorDrawable) {
                                mFillerView.setBackgroundColor(((ColorDrawable) background).getColor());
                            }
                            getViewBinding().quickStartCells.addView(mFillerView, getViewBinding().quickStartCells.getChildCount() - 1);

                        }
                        getViewBinding().quickstartContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                } catch (Exception e) {
                    DLog.e("Dashboard population dettached", e);
                }
            }
        });
    }

    /**
     * Used add views and add dividers depending on their position
     *
     * @param index index at which to add the view
     * @param view  view to add
     * @param undo  true if the view is being added because the user has previously removed the view and is undoing their action
     */
    private void addViewToQuickstartSubviews(int index, final View view, boolean undo) {
        boolean undoFirstCell = false;  // Used to track if the user is undo-ing the first cell
        boolean undoLastCell = false;   // Necessary for tracking if the user is undo-ing their last cell

        if (undo && index == 1) {
            undoFirstCell = true;
        }

        if (undo && index < getViewBinding().quickStartCells.getChildCount()) {    //Check if the user is undo-ing a removal, and if it's not the last cell in the list
            getViewBinding().quickStartCells.addView(view, index);
            index++;
        } else if (undo && index >= getViewBinding().quickStartCells.getChildCount()) { // Check if the use is undo-ing a removal, and if it's the last cell in the list
            index = getViewBinding().quickStartCells.getChildCount() - 1;
            undoLastCell = true;
        }

        if (!undoFirstCell && getViewBinding().quickStartCells.getChildCount() > 2) {
            View dividerView = new View(getViewBinding().quickStartCells.getContext());
            dividerView.setLayoutParams(new ActionBar.LayoutParams(DisplayUtils.getScreenWidth(getActivity()), (int) DisplayUtils.dipToPixels(getActivity(), 1f)));
            dividerView.setBackgroundColor(getResources().getColor(R.color.ehi_grey_header_bg));
            getViewBinding().quickStartCells.addView(dividerView, index);
            index++;
        }

        if (!undo || undoLastCell) {
            getViewBinding().quickStartCells.addView(view, index);
        }
    }

    @Nullable
    private Pair<Integer, View> removeViewFromQuickStartSubViews(View view) {
        View iterateView;
        boolean removeDivider = false;
        for (int i = 0; i < getViewBinding().quickStartCells.getChildCount(); i++) {
            iterateView = getViewBinding().quickStartCells.getChildAt(i);

            if (iterateView instanceof QuickStartRowView) {
                final QuickStartRowView quickStartRowView = (QuickStartRowView) iterateView;
                if (quickStartRowView.getContainer().equals(view)) {
                    if (i > 1) {
                        removeDivider = true;
                    }

                    if (removeDivider) {
                        getViewBinding().quickStartCells.removeViews(i - 1, 2);
                    } else {
                        getViewBinding().quickStartCells.removeViewAt(i);
                    }
                    quickStartRowView.getContainer().setX(0);
                    quickStartRowView.getContainer().setTranslationX(0);
                    return new Pair<>(i, iterateView);
                }
            }
        }

        return null;
    }

    private void clearQuickStartCells() {
        for (int i = 0; i < getViewBinding().quickStartCells.getChildCount(); i++) {
            View view = getViewBinding().quickStartCells.getChildAt(i);
            if (view != getViewBinding().eplusExtendedView
                    && view != getViewBinding().quickStartView
                    && view != getViewBinding().weekendSpecial
                    && view != getViewBinding().clearRecentActivityButton) {
                getViewBinding().quickStartCells.removeView(getViewBinding().quickStartCells.getChildAt(i));
                i--;
            }
        }
    }
//endregion

//region animation

    /**
     * Logic for transforming the search bar
     */
    private void transformSearch() {
        final ViewGroup content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        getViewBinding().dummyDashboardInputSearchNearbyIcon
                .animate()
                .setDuration(ANIMATION_DURATION / 2)
                .alpha(0);

        View xView = getViewBinding().searchLocationClearInput;

        xView.animate()
                .setStartDelay(ANIMATION_DURATION / 2)
                .alpha(1)
                .setDuration(ANIMATION_DURATION / 2);

        getViewBinding().dummyDashboardSearchLocationsArea.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final View text = getViewBinding().dummySearchEditText;

                    TransitionDrawable transitionDrawable = (TransitionDrawable) text.getBackground();
                    transitionDrawable.startTransition(ANIMATION_DURATION);

                    mNewWidth = (content.getWidth()
                            - getResources().getDimension(R.dimen.search_screen_back_button_width)
                            - getResources().getDimension(R.dimen.search_screen_input_margin_right));

                    getViewBinding().dummyDashboardSearchLocationsArea.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mSearchEditTextWidth = text.getWidth();

                                final Animation shrink = new WidthTransformColorAnimation(getViewBinding().dummyDashboardSearchLocationsArea,
                                        ((mSearchEditTextWidth - mNewWidth - DisplayUtils.dipToPixels(getActivity(), 4))),
                                        text, (int) mNewWidth);

                                shrink.setDuration(ANIMATION_DURATION / 2);

                                text.startAnimation(shrink);

                                TransitionDrawable drawable = ((TransitionDrawable) getViewBinding().dummySearchWrapper.getBackground());
                                drawable.startTransition(ANIMATION_DURATION / 2);
                            } catch (Exception e) {
                                handleAnimationFailure(e);
                            }
                        }
                    }, ANIMATION_DURATION / 2);
                } catch (Exception e) {
                    handleAnimationFailure(e);
                }
            }
        });
    }

    private void handleAnimationFailure(Exception e) {
        DLog.e("Dashboard Animation Error", e);
        getViewModel().onAnimationEnd();
    }

    /**
     * Logic for animating the dashboard to alpha out while pushing the toolbar up
     */
    private void animateUp() {
        transformSearch();

        float toolbarHeight = mToolbar.getHeight();
        ViewGroup content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        View check = getViewBinding().dashboardSearchLocationsArea;

        getViewBinding().animationLayer.setVisibility(View.VISIBLE);
        getViewBinding().animationLayer.bringToFront();
        getViewBinding().dashboardSearchLocationsArea.setVisibility(View.INVISIBLE);

        getViewBinding().dummySearchWrapper.setY((check.getY() + getViewBinding().startReservationContainer.getY() + getViewBinding().quickstartContainer.getY())
                - getViewBinding().dashboardScrollView.getScrollY());

        getViewBinding().startRentalsDivider
                .animate()
                .alpha(0)
                .setDuration(ANIMATION_DURATION / 2);

        ((View) getViewBinding().getRoot().getParent()).animate()
                .setStartDelay(ANIMATION_DURATION / 2)
                .setDuration(ANIMATION_DURATION / 2)
                .translationY(-1 * toolbarHeight);

        mToolbar.animate()
                .setDuration(ANIMATION_DURATION / 2)
                .y(-1 * toolbarHeight)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        mToolbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

        getViewBinding().dashboardScrollView.animate()
                .setDuration(ANIMATION_DURATION / 2)
                .alpha(0);


        getViewBinding().dummySearchWrapper.animate()
                .setStartDelay(ANIMATION_DURATION / 2)
                .setDuration(ANIMATION_DURATION / 2)
                .translationYBy(-1 * (getViewBinding().dummySearchWrapper.getY()))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        getViewModel().onAnimationEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
    }
//endregion

    private void showLocationsActivity() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, DashboardFragment.SCREEN_NAME)
                .state(getViewModel().getScreenState())
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_WKND_GET_STARTED.value)
                .addDictionary(getViewModel().getAnalyticsMap())
                .tagScreen()
                .tagEvent();
        startActivity(new LocationsOnMapActivityHelper.Builder()
                .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                .isModify(false)
                .build(getActivity()));
    }

    private void showSurveyModal() {
        showModalDialog(getActivity(),
                new SurveyDialogFragmentHelper.Builder().build()
        );
    }

    private void setUpTimeNotifications(List<EHITripSummary> trips) {
        if (ListUtils.isEmpty(trips)) return;
        // sending in small sizes to avoid android.os.TransactionTooLargeException
        final int size = 5;
        for (int i = 0; i < trips.size(); i += size) {
            try {
                getActivity().startService(
                        new NotificationSchedulerServiceHelper.Builder()
                                .upcomingRentals(trips.subList(i, Math.min(i + size, trips.size()) - 1))
                                .build(getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpGeoNotifications(List<EHITripSummary> trips) {
        if (ListUtils.isEmpty(trips)) return;
        // sending in small sizes to avoid android.os.TransactionTooLargeException
        final int size = 5;
        for (int i = 0; i < trips.size(); i += size) {
            try {
                getActivity().startService(
                        new GeofenceRegistrationServiceHelper.Builder()
                                .upcomingRentals(trips.subList(i, Math.min(i + size, trips.size()) - 1))
                                .build(getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}