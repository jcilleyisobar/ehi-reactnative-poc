package com.ehi.enterprise.android.ui.dashboard.debug;

import android.support.v4.util.Pair;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;

public class DebugMenuViewModel extends ManagersAccessViewModel{
    final ReactorVar<String> title = new ReactorVar<>();
    public static final String CURRENT_NOTIFICATION = "Schedule current trip notification in 5 seconds";
    public static final String UPCOMING_NOTIFICATION = "Schedule upcoming trip notification in 5 seconds";
    public static final String GEOFENCE_UPCOMING_TRIP_ENTERED = "Register upcoming trip geofence @ MDW";
    public static final String GEOFENCE_CURRENT_TRIP_ENTERED = "Register current trip geofence @ MDW";
    public static final String GEOFENCE_OFFICE_ENTERED = "Register office geofence @ 300 E Randolph";
    public static final String CLEAR_NOTIFICATIONS = "Clear scheduled notifications";
    public static final String CLEAR_REGISTERED_GEOFENCES = "Clear registered geofences";
    public static final String CLEAR_LAST_LOGIN_TIME = "Clear last login time (profile lockout)";
    public static final String CHANGE_FR_DATA_COLLECTION_NOTIFICATION_TIME = "Simulate 12 month passed situation for FR data collection dialog";
    public static final String FORCE_FORESEE_POPUP = "Force Foresee Popup";
    public static final String PUSH_NOTIFICATION_POPUP = "Push Notification";
    public static final String FORCE_ISSUING_AUTHOROTY_REQUIRED_TEXT = "Force Issuing Authority Required";
    public static final String FORCE_WRONG_API_KEY_TEXT = "Force send Wrong API key to GBO";
    public static final String GDRP_OPT_OUT_STATUS = "GDPR (SDK Opt Out Statuses)";

    public static final int CURRENT_NOTIFICATION_BUTTON = 0;
    public static final int UPCOMING_NOTIFICATION_BUTTON = 1;
    public static final int GEOFENCE_UPCOMING_TRIP_ENTERED_BUTTON = 2;
    public static final int GEOFENCE_CURRENT_TRIP_ENTERED_BUTTON = 3;
    public static final int CLEAR_NOTIFICATIONS_BUTTON = 4;
    public static final int CLEAR_REGISTERED_GEOFENCES_BUTTON = 5;
    public static final int GEOFENCE_OFFICE_ENTERED_BUTTON = 6;
    public static final int CLEAR_LAST_LOGIN_TIME_BUTTON = 7;
    public static final int CHANGE_FR_DATA_COLLECTION_NOTIFICATION_TIME_BUTTON = 8;
    public static final int FORCE_FORESEE_TRACK = 9;
    public static final int PUSH_NOTIFICATION = 10;
    public static final int FORCE_ISSUING_AUTHOROTY_REQUIRED = 11;
    public static final int FORCE_WRONG_API_KEY= 12;
    public static final int GDRP_OPT_OUT_STATUS_BUTTON = 13;
    public final List<Pair<Integer, String>> debugActions = new ArrayList<>();

    public DebugMenuViewModel() {
        debugActions.add(new Pair<>(CURRENT_NOTIFICATION_BUTTON, CURRENT_NOTIFICATION));
        debugActions.add(new Pair<>(UPCOMING_NOTIFICATION_BUTTON, UPCOMING_NOTIFICATION));
        debugActions.add(new Pair<>(CLEAR_NOTIFICATIONS_BUTTON, CLEAR_NOTIFICATIONS));
        debugActions.add(new Pair<>(GEOFENCE_UPCOMING_TRIP_ENTERED_BUTTON, GEOFENCE_UPCOMING_TRIP_ENTERED));
        debugActions.add(new Pair<>(GEOFENCE_CURRENT_TRIP_ENTERED_BUTTON, GEOFENCE_CURRENT_TRIP_ENTERED));
        debugActions.add(new Pair<>(GEOFENCE_OFFICE_ENTERED_BUTTON, GEOFENCE_OFFICE_ENTERED));
        debugActions.add(new Pair<>(CLEAR_REGISTERED_GEOFENCES_BUTTON, CLEAR_REGISTERED_GEOFENCES));
        debugActions.add(new Pair<>(CLEAR_LAST_LOGIN_TIME_BUTTON, CLEAR_LAST_LOGIN_TIME));
        debugActions.add(new Pair<>(CHANGE_FR_DATA_COLLECTION_NOTIFICATION_TIME_BUTTON, CHANGE_FR_DATA_COLLECTION_NOTIFICATION_TIME));
        debugActions.add(new Pair<>(FORCE_FORESEE_TRACK, FORCE_FORESEE_POPUP));
        debugActions.add(new Pair<>(PUSH_NOTIFICATION, PUSH_NOTIFICATION_POPUP));
        debugActions.add(new Pair<>(FORCE_ISSUING_AUTHOROTY_REQUIRED, FORCE_ISSUING_AUTHOROTY_REQUIRED_TEXT));
        debugActions.add(new Pair<>(FORCE_WRONG_API_KEY, FORCE_WRONG_API_KEY_TEXT));
        debugActions.add(new Pair<>(GDRP_OPT_OUT_STATUS_BUTTON, GDRP_OPT_OUT_STATUS));
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue("Debug Menu");

    }


}
