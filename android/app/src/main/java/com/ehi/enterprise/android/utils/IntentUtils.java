package com.ehi.enterprise.android.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.ui.dashboard.MainActivity;
import com.ehi.enterprise.android.ui.dashboard.MainActivityHelper;
import com.ehi.enterprise.android.ui.splash.SplashActivity;
import com.google.android.m4b.maps.model.LatLng;

import java.util.Locale;

public final class IntentUtils {
    public static final String TAG = "IntentUtils";
    private static final String EXTRA_CUSTOM_TABS_SESSION = "EXTRA_CUSTOM_TABS_SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";

    public static void sendMessageToEmail(Context context, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    public static void shareViaChooser(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.location_address_share_prompt)));
    }

    public static void openUrlViaExternalApp(Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
        //TODO R1.1(or when tabs will be included in prod Chrome version) customize chrome tabs
    }

    public static void openUrlViaCustomTab(@NonNull Activity context, @NonNull String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.ehi_primary));
        builder.setStartAnimations(context, R.anim.modal_slide_in, R.anim.modal_stay);
        builder.setExitAnimations(context, R.anim.modal_stay, R.anim.modal_slide_out);
        builder.setShowTitle(true);
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(context, Uri.parse(url));
    }

    public static void showDirectionsToPlace(Context context, LatLng coordinates, String address) {
        String uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=" + address, coordinates.latitude, coordinates.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.error_no_app_found), Toast.LENGTH_SHORT).show();
        }
    }

    public static void callNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.error_no_app_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open activity with a modal animation
     *
     * @param context current Activity context
     * @param intent  Intent to start
     */
    public static void startActivityAsModal(@NonNull Activity context, @NonNull Intent intent) {
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.modal_slide_in, R.anim.modal_stay);
    }

    public static void goToHomeScreen(@NonNull Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void goToHomeScreenAndShowJoinModal(@NonNull Context context, Intent modalCalendarIntent) {
        Intent intent = new MainActivityHelper.Builder().showJoinModal(modalCalendarIntent).build(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public static void goToSplashScreen(@NonNull Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void getDirectionsFromCurrentLocation(@NonNull Context context, double destinationLat, double destinationLong, String destinationName) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLat, destinationLong, destinationName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.error_no_app_found), Toast.LENGTH_SHORT).show();
        }
    }

    public static void findNearbyGasStations(Context context, EHILatLng latLng) {
        Uri googleMapsGasIntentUri = Uri.parse(String.format("geo:%f,%f?q=gas stations",
                latLng.getLatitude(),
                latLng.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsGasIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            ToastUtils.showToast(context, context.getString(R.string.dashboard_google_maps_not_found));
        }
    }

    public static void goToAppSettings(@NonNull Context context) {
        String packageName = context.getPackageName();

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            //e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context.startActivity(intent);
        }
    }

    public static void goToPlayStoreApp(@NonNull Context context) {
        final String packageName = context.getPackageName();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));

        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
        }

        context.startActivity(intent);
    }

    public enum NotificationIntents {
        RETURN_INSTRUCTIONS("RETURN_INSTRUCTIONS"),
        TERMINAL_DIRECTIONS("TERMINAL_DIRECTIONS"),
        GET_DIRECTIONS("GET_DIRECTIONS"),
        GAS_STATIONS("GAS_STATIONS"),
        CALL("CALL"),
        OPEN_APP("OPEN_APP");

        public final String intentAction;

        NotificationIntents(final String intentAction) {
            this.intentAction = intentAction;
        }
    }
}
