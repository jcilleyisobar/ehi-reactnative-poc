package com.ehi.enterprise.android.ui.dashboard.debug;

import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DebugMenuFragmentBinding;
import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.EHIVehicleDetails;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.geofence.GeofenceRegistrationServiceHelper;
import com.ehi.enterprise.android.ui.notification.NotificationSchedulerServiceHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;
import com.google.android.m4b.maps.model.LatLng;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import io.dwak.reactorbinding.activity.ReactorActivity;

@NoExtras
@ViewModel(DebugMenuViewModel.class)
public class DebugMenuFragment extends DataBindingViewModelFragment<DebugMenuViewModel, DebugMenuFragmentBinding> {

    public static boolean FORCE_ISSUING_AUTHORITY = false;

    private final Random mNotificationIdGenerator = new Random();
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case DebugMenuViewModel.CURRENT_NOTIFICATION_BUTTON:
                    EHILocation mockLocation = new EHILocation();
                    mockLocation.setTimeZoneId(TimeZone.getDefault().getDisplayName());
                    mockLocation.setId("1018838");
                    mockLocation.setName("MockName");
                    mockLocation.setGpsCoordinates(new EHILatLng(new LatLng(41.884344, -87.627302)));
                    List<EHIPhone> ehiPhones = new ArrayList<>();
                    ehiPhones.add(new EHIPhone("404-404-4040", "OFFICE"));
                    mockLocation.setPhoneNumbers(ehiPhones);
                    List<EHITripSummary> tripSummaries = new ArrayList<>();
                    final EHITripSummary mockTripSummary = EHITripSummary.getMockTripSummary(true, String.valueOf(mNotificationIdGenerator.nextInt(Integer.MAX_VALUE)));
                    mockTripSummary.setReturnLocation(mockLocation);
                    EHIVehicleDetails ehiVehicleDetails = new EHIVehicleDetails();
                    List<EHIImage> ehiImageList = new ArrayList<>();
                    EHIImage ehiImage = new EHIImage();
                    ehiImage.setPath("https://enterprise-int1-aem.enterprise.com/content/enterprise_cros/data/vehicle/bookingCountries/US/CARS/ICAR.doi.{width}.{quality}.imageSmallSideProfileNodePath.png/1444686979984.png");
                    ehiImageList.add(ehiImage);
                    ehiVehicleDetails.setLicensePlateNumber("AA123456");
                    ehiVehicleDetails.setName("Toyota Corolla");
                    ehiVehicleDetails.setImages(ehiImageList);
                    mockTripSummary.setVehicleDetails(ehiVehicleDetails);
                    tripSummaries.add(mockTripSummary);
                    getActivity().startService(new NotificationSchedulerServiceHelper.Builder().currentRentals(tripSummaries).build(getActivity()));
                    break;

                case DebugMenuViewModel.UPCOMING_NOTIFICATION_BUTTON:
                    EHILocation upcomingMockLocation = new EHILocation();
                    upcomingMockLocation.setTimeZoneId(TimeZone.getDefault().getDisplayName());
                    upcomingMockLocation.setId("1018838");
                    upcomingMockLocation.setName("MockName");
                    upcomingMockLocation.setGpsCoordinates(new EHILatLng(new LatLng(41.884344, -87.627302)));
                    List<EHIPhone> upcomingPhoneNumber = new ArrayList<>();
                    upcomingPhoneNumber.add(new EHIPhone("404-404-4040", "OFFICE"));
                    upcomingMockLocation.setPhoneNumbers(upcomingPhoneNumber);
                    List<EHITripSummary> upcomingTripSummaries = new ArrayList<>();
                    final EHITripSummary upcomingMockTripSummary = EHITripSummary.getMockTripSummary(false, String.valueOf(mNotificationIdGenerator.nextInt(Integer.MAX_VALUE)));
                    upcomingMockTripSummary.setPickupLocation(upcomingMockLocation);
                    EHIVehicleDetails upcomingVehicleDetails = new EHIVehicleDetails();
                    List<EHIImage> upcomingImageList = new ArrayList<>();
                    EHIImage upcomingImage = new EHIImage();
                    upcomingImage.setPath("https://enterprise-int1-aem.enterprise.com/content/enterprise_cros/data/vehicle/bookingCountries/US/CARS/CCAR.doi.{width}.{quality}.imageSmallSideProfileNodePath.png/1435160860168.png");
                    upcomingImageList.add(upcomingImage);
                    upcomingVehicleDetails.setLicensePlateNumber("AA123456");
                    upcomingVehicleDetails.setName("Toyota Corolla");
                    upcomingVehicleDetails.setImages(upcomingImageList);
                    upcomingMockTripSummary.setVehicleDetails(upcomingVehicleDetails);
                    upcomingTripSummaries.add(upcomingMockTripSummary);
                    getActivity().startService(new NotificationSchedulerServiceHelper.Builder().upcomingRentals(upcomingTripSummaries).build(getActivity()));
                    break;

                case DebugMenuViewModel.CLEAR_NOTIFICATIONS_BUTTON:
                    EHINotification.unscheduleNotifications(getActivity(), getViewModel().getManagers().getNotificationManager().getAllNotifications());
                    break;

                case DebugMenuViewModel.GEOFENCE_UPCOMING_TRIP_ENTERED_BUTTON:
                    EHILocation geofenceUpcomingLocation = new EHILocation();
                    geofenceUpcomingLocation.setId("1018781");
                    geofenceUpcomingLocation.setName("Midway International Airport");
                    final Location upcomingLocation = LocationApiManager.getInstance().getLastCurrentLocation();
                    geofenceUpcomingLocation.setGpsCoordinates(new EHILatLng(new LatLng(upcomingLocation.getLatitude(), upcomingLocation.getLongitude())));
                    List<EHIPhone> geofencePhoneNumber = new ArrayList<>();
                    geofencePhoneNumber.add(new EHIPhone("773-735-8860", "OFFICE"));
                    geofenceUpcomingLocation.setPhoneNumbers(geofencePhoneNumber);
                    List<EHITripSummary> geofenceTripSummaries = new ArrayList<>();
                    final EHITripSummary geofenceMockTripSummary = EHITripSummary.getMockTripSummary(false, String.valueOf(mNotificationIdGenerator.nextInt(Integer.MAX_VALUE)));
                    geofenceMockTripSummary.setPickupLocation(geofenceUpcomingLocation);
                    geofenceTripSummaries.add(geofenceMockTripSummary);
                    getActivity().startService(new GeofenceRegistrationServiceHelper.Builder().upcomingRentals(geofenceTripSummaries).build(getActivity()));
                    break;

                case DebugMenuViewModel.GEOFENCE_CURRENT_TRIP_ENTERED_BUTTON:
                    EHILocation geofenceCurrentLocation = new EHILocation();
                    geofenceCurrentLocation.setId("1018781");
                    geofenceCurrentLocation.setName("Midway International Airport");
                    final Location currentLocation = LocationApiManager.getInstance().getLastCurrentLocation();
                    geofenceCurrentLocation.setGpsCoordinates(new EHILatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                    List<EHIPhone> geofenceCurrentPhoneNumber = new ArrayList<>();
                    geofenceCurrentPhoneNumber.add(new EHIPhone("773-735-8860", "OFFICE"));
                    geofenceCurrentLocation.setPhoneNumbers(geofenceCurrentPhoneNumber);
                    List<EHITripSummary> geofenceCurrentTripSummaries = new ArrayList<>();
                    final EHITripSummary geofenceCurrentMockTripSummary = EHITripSummary.getMockTripSummary(true, String.valueOf(mNotificationIdGenerator.nextInt(Integer.MAX_VALUE)));
                    geofenceCurrentMockTripSummary.setReturnLocation(geofenceCurrentLocation);
                    geofenceCurrentTripSummaries.add(geofenceCurrentMockTripSummary);
                    getActivity().startService(new GeofenceRegistrationServiceHelper.Builder().currentRentals(geofenceCurrentTripSummaries).build(getActivity()));
                    break;

                case DebugMenuViewModel.GEOFENCE_OFFICE_ENTERED_BUTTON:
                    EHILocation geofenceOfficeLocation = new EHILocation();
                    geofenceOfficeLocation.setId("1018781");
                    geofenceOfficeLocation.setName("Isobar office");
                    geofenceOfficeLocation.setGpsCoordinates(new EHILatLng(new LatLng(41.8848508, -87.6199154)));
                    List<EHIPhone> officePhoneNumber = new ArrayList<>();
                    officePhoneNumber.add(new EHIPhone("312-529-2400", "OFFICE"));
                    geofenceOfficeLocation.setPhoneNumbers(officePhoneNumber);
                    List<EHITripSummary> officeTripSummaries = new ArrayList<>();
                    final EHITripSummary officeMockTripSummary = EHITripSummary.getMockTripSummary(false, String.valueOf(mNotificationIdGenerator.nextInt(Integer.MAX_VALUE)));
                    officeMockTripSummary.setPickupLocation(geofenceOfficeLocation);
                    officeTripSummaries.add(officeMockTripSummary);
                    getActivity().startService(new GeofenceRegistrationServiceHelper.Builder().upcomingRentals(officeTripSummaries).build(getActivity()));
                    break;
                case DebugMenuViewModel.CLEAR_REGISTERED_GEOFENCES_BUTTON:
                    for (EHIGeofence ehiGeofence : getViewModel().getManagers().getGeofenceManger().getAllGeofences()) {
                        getViewModel().getManagers().getGeofenceManger().removeGeofence(ehiGeofence);
                    }
                    break;
                case DebugMenuViewModel.CLEAR_LAST_LOGIN_TIME_BUTTON:
                    final Calendar instance = Calendar.getInstance();
                    instance.add(Calendar.MINUTE, -30);
                    getViewModel().getManagers().getLoginManager().setLastLoginTime(instance.getTimeInMillis());
                    break;
                case DebugMenuViewModel.CHANGE_FR_DATA_COLLECTION_NOTIFICATION_TIME_BUTTON:
                    Calendar calendar = Calendar.getInstance();
                    getViewModel().getManagers().getLocalDataManager()
                            .setDataCollectionReminderNextShowTimestamp(calendar.getTimeInMillis());
                    Toast.makeText(getActivity(), "Time for next FR data tracking dialog changed!!!", Toast.LENGTH_SHORT).show();
                    break;

                case DebugMenuViewModel.FORCE_FORESEE_TRACK:
                    getViewModel().getManagers().getForeseeSurveyManager().forceDebugTrack();
                    Toast.makeText(getActivity(), "Go to dashboard and the foresee popup will appear", Toast.LENGTH_SHORT).show();
                    break;

                case DebugMenuViewModel.PUSH_NOTIFICATION:
                    String debugKey =
                            Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID)
                            .substring(0, 4);
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_DEBUG.value, EHIAnalytics.Screen.SCREEN_DEBUG.value)
                            .state(EHIAnalytics.State.STATE_PUSH_NOTIFICATION_MARKER.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.debugPushDict(debugKey))
                            .tagScreen()
                            .tagEvent();
                    Localytics.upload();
                    DialogUtils.showDialogWithTitleAndText(getContext(), debugKey, "Debug Key");
                    break;

                case DebugMenuViewModel.FORCE_ISSUING_AUTHOROTY_REQUIRED:
                    FORCE_ISSUING_AUTHORITY = !FORCE_ISSUING_AUTHORITY;
                    Toast.makeText(getActivity(), "OK Issuing authority is" + (FORCE_ISSUING_AUTHORITY ? " " : " NOT ") + "required now for ALL countries", Toast.LENGTH_LONG).show();
                    break;
                case DebugMenuViewModel.FORCE_WRONG_API_KEY:
                    getViewModel().getManagers().getLocalDataManager().toggleForceGboInvalidKey();
                    Toast.makeText(getActivity(), "OK will send " + (getViewModel().getManagers().getLocalDataManager().shouldForceGboInvalidKey() ? "WRONG" : "CORRECT") + " api key to GBO", Toast.LENGTH_LONG).show();
                    break;
                case DebugMenuViewModel.GDRP_OPT_OUT_STATUS_BUTTON:
                    showModalDialog(getActivity(), new GDPRDebugFragment());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_debug_menu, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        for (Pair<Integer, String> action : getViewModel().debugActions) {
            final Button debugButton = new Button(getActivity());
            debugButton.setId(action.first);
            debugButton.setText(action.second);
            debugButton.setOnClickListener(mOnClickListener);
            debugButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            getViewBinding().container.addView(debugButton);
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        ReactorActivity.title(getViewModel().title, getActivity());
    }
}
