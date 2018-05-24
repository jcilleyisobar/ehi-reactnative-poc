package com.ehi.enterprise.android.ui.geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.geofence.EHIGeofence;
import com.ehi.enterprise.android.ui.dashboard.MainActivityHelper;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.support.v4.app.NotificationCompat.Action;
import static android.support.v4.app.NotificationCompat.BigTextStyle;
import static android.support.v4.app.NotificationCompat.Builder;

public class GeofenceIntentService extends IntentService {
    private static final String TAG = "GeofenceIntentService";
    private static final long GEOFENCE_NOTIFICATION_WINDOW = TimeUnit.HOURS.toMillis(2);
    private static final long FIVE_SECOND_ERROR_WINDOW = TimeUnit.SECONDS.toMillis(5); //give a 5 minute window

    public GeofenceIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        DLog.d("geofence intent received");
        GeofencingEvent geoEvent = GeofencingEvent.fromIntent(intent);
        if(geoEvent.hasError()){
            return;
        }

        final Random idGenerator = new Random();
        final int geofenceTransition = geoEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            if(!GeofenceManager.getInstance().isInitialized()) {
                GeofenceManager.getInstance().initialize(getApplicationContext(), true);
            }
            final Date now = new Date();
            final List<Geofence> triggeringGeofences = geoEvent.getTriggeringGeofences();

            for (Geofence geo : triggeringGeofences) {
                DLog.d(geo.getRequestId());
                final EHIGeofence ehiGeofence = GeofenceManager.getInstance().getEHIGeofence(geo.getRequestId());

                if (ehiGeofence != null) {
                    //If the trip is within two hours of entering the geofence.
                    if (ehiGeofence.getScheduledTime() - GEOFENCE_NOTIFICATION_WINDOW < now.getTime()
                            && ehiGeofence.getScheduledTime() - GEOFENCE_NOTIFICATION_WINDOW < now.getTime() + FIVE_SECOND_ERROR_WINDOW) {
                        String title = getString(R.string.notifications_rental_assistant_title);
                        CharSequence content;
                        Action action0;

                        if (ehiGeofence.isCurrent() && ehiGeofence.isAfterHours()) {
                            action0 = new Action(R.drawable.icon_notifications_directions,
                                    getString(R.string.notifications_after_hours_button),
                                    PendingIntent.getActivity(this,
                                            idGenerator.nextInt(Integer.MAX_VALUE),
                                            createIntent(ehiGeofence, IntentUtils.NotificationIntents.RETURN_INSTRUCTIONS),
                                            PendingIntent.FLAG_CANCEL_CURRENT));
                            content = getString(R.string.notifications_after_hours_alert_message);
                        } else if (ehiGeofence.isAirport()) {
                            action0 = new Action(R.drawable.icon_notifications_directions,
                                    getString(R.string.notifications_wayfinding_button),
                                    PendingIntent.getActivity(this,
                                            idGenerator.nextInt(Integer.MAX_VALUE),
                                            createIntent(ehiGeofence, IntentUtils.NotificationIntents.TERMINAL_DIRECTIONS),
                                            PendingIntent.FLAG_CANCEL_CURRENT));
                            content = new TokenizedString.Formatter<EHIStringToken>(getResources()).formatString(R.string.notifications_wayfinding_alert_message)
                                    .addTokenAndValue(EHIStringToken.NAME, ehiGeofence.getLocationName())
                                    .format();

                        } else {
                            action0 = null;
                            content = null;
                        }

                        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
                            content+=" " + ehiGeofence.getId();
                        }

                        //noinspection ConstantConditions
                        if (content != null && action0 != null) {
                            PendingIntent contentIntent = PendingIntent.getActivity(this,
                                    idGenerator.nextInt(Integer.MAX_VALUE),
                                    createIntent(ehiGeofence, IntentUtils.NotificationIntents.OPEN_APP),
                                    PendingIntent.FLAG_ONE_SHOT);
                            final Notification notification = new Builder(this, EHINotificationManager.RESERVATION_CHANNEL)
                                    .setTicker(content)
                                    .setSmallIcon(R.drawable.icon_notifications)
                                    .setContentTitle(title)
                                    .addAction(action0)
                                    .setContentText(content)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true)
                                    .setColor(ContextCompat.getColor(this, R.color.ehi_primary))
                                    .setStyle(new BigTextStyle().bigText(content))
                                    .build();


                            NotificationManagerCompat.from(this).notify(idGenerator.nextInt(Integer.MAX_VALUE), notification);
                            GeofenceManager.getInstance().removeGeofence(ehiGeofence);
                        }

                    }
                    //if it is in future
                    else {
                        ehiGeofence.register(this, GeofenceManager.getInstance());
                    }
                }
            }
        }
    }

    private Intent createIntent(final EHIGeofence ehiGeofence, final IntentUtils.NotificationIntents notificationIntent) {
        return new Intent(new MainActivityHelper.Builder().geofence(ehiGeofence).intentAction(notificationIntent).build(this));
    }
}
