package com.ehi.enterprise.android.ui.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.ui.dashboard.MainActivityHelper;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import static android.support.v4.app.NotificationCompat.Action;

public class NotificationAlarmReceiverService extends IntentService {

    private static final String TAG = "NotificationAlarmReceiverService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationAlarmReceiverService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        DLog.d(EHIBundle.fromBundle(intent.getExtras()).<EHINotification>getEHIModel(EHINotification.NOTIFICATION, EHINotification.class).getId());
        final EHINotification notification = EHIBundle.fromBundle(intent.getExtras())
                                                .getEHIModel(EHINotification.NOTIFICATION, EHINotification.class);

        if (notification != null && notification.getId() != null) {
            Random randomActionId = new Random();
            Intent contentIntent;
            Action action0;
            Action action1;
            TokenizedString.Formatter<EHIStringToken> tokenFormatter = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .addTokenAndValue(EHIStringToken.NAME, notification.getUserFirstName())
                    .addTokenAndValue(EHIStringToken.LOCATION_NAME, notification.getLocationName())
                    .addTokenAndValue(EHIStringToken.TIME, formatScheduledTime(notification.getTripTime()));

            @StringRes int formatString;
            String ticker;
            String title;

            RemoteViews currentView = new RemoteViews(getPackageName(), R.layout.v_notifications_current_trip_wear);
            RemoteViews upcomingView = new RemoteViews(getPackageName(), R.layout.v_notifications_upcoming_trip_wear);
            if (notification.isCurrentTrip()) {
                formatString = R.string.notifications_current_rental_alert_message;
                ticker = getResources().getString(R.string.notifications_alert_current_title);
                title = getResources().getString(R.string.notifications_alert_current_title);
                currentView.setTextViewText(R.id.license_plate_text, EHITextUtils.isEmpty(notification.getCarLicensePlate())
                                                                      ? getResources().getString(R.string.location_hours_unavailable_label)
                                                                      : notification.getCarLicensePlate());


                currentView.setTextViewText(R.id.car_make_model_text, EHITextUtils.isEmpty(notification.getCarName())
                                                                       ? getResources().getString(R.string.location_hours_unavailable_label)
                                                                       : notification.getCarName());


                contentIntent = createIntent(notification, null);
                action0 = new Action(R.drawable.icon_notifications_directions,
                                     getString(R.string.notification_get_directions_button),
                                     PendingIntent.getActivity(this,
                                             randomActionId.nextInt(999999),
                                             createIntent(notification, IntentUtils.NotificationIntents.GET_DIRECTIONS),
                                             PendingIntent.FLAG_CANCEL_CURRENT));
                action1 = new Action(R.drawable.icon_notifications_gas,
                                     getString(R.string.notification_gas_station_button),
                                     PendingIntent.getActivity(this,
                                             randomActionId.nextInt(999999),
                                             createIntent(notification, IntentUtils.NotificationIntents.GAS_STATIONS),
                                             PendingIntent.FLAG_CANCEL_CURRENT));
            }
            else {
                formatString = R.string.notifications_upcoming_rental_alert_message;
                ticker = getResources().getString(R.string.notifications_alert_upcoming_title);
                title = getResources().getString(R.string.notifications_alert_upcoming_title);

                upcomingView.setTextViewText(R.id.confirmation_number_text, notification.getId());

                contentIntent = createIntent(notification, null);
                action0 = new Action(R.drawable.icon_notifications_call,
                                     getString(R.string.notifications_call_branch_button),
                                     PendingIntent.getActivity(this,
                                             randomActionId.nextInt(999999),
                                             createIntent(notification, IntentUtils.NotificationIntents.CALL),
                                             PendingIntent.FLAG_CANCEL_CURRENT));
                action1 = new Action(R.drawable.icon_notifications_directions,
                                     getString(R.string.notification_get_directions_button),
                                     PendingIntent.getActivity(this,
                                             randomActionId.nextInt(999999),
                                             createIntent(notification, IntentUtils.NotificationIntents.GET_DIRECTIONS),
                                             PendingIntent.FLAG_CANCEL_CURRENT));
            }

            final Integer pendingIntentId = randomActionId.nextInt(Integer.MAX_VALUE);
            final CharSequence content = tokenFormatter.formatString(formatString).format();
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, pendingIntentId, contentIntent, PendingIntent.FLAG_ONE_SHOT);

            //Second page for wear
            Notification secondPage = new NotificationCompat.Builder(this, EHINotificationManager.RESERVATION_CHANNEL)
                    .setContent(notification.isCurrentTrip() ? currentView: upcomingView)
                    .build();

            //Wearable extender used for adding extra feature to notifications on wear
            // Commented for now because Wear support isn't being included in this version.
            // But will be reabled later.
//            final NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
//                    .addPage(secondPage);

//            final Target target = new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    wearableExtender.setBackground(bitmap);
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//                    wearableExtender.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.eapp_icon));
//                    DLog.e("Could not load the bitmap");
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            };

            Bitmap b = null;
            String path = "";

            try {
                if(!ListUtils.isEmpty(notification.getImageList())) {
                    path = EHIImageUtils.getCarClassImageForWear(notification.getImageList().get(0).getPath(), 480, "high");
                    b = Picasso.with(NotificationAlarmReceiverService.this).load(path).placeholder(R.drawable.eapp_icon).get();
                }
                
            } catch (IOException e) {
                DLog.e("...", e);
            }
//            target.onBitmapLoaded(b, Picasso.LoadedFrom.NETWORK);

            Notification tripNotification =
                    new NotificationCompat.Builder(this, EHINotificationManager.RESERVATION_CHANNEL)
                            .setTicker(ticker)
                            .setSmallIcon(R.drawable.icon_notifications)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setContentIntent(contentPendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setColor(ContextCompat.getColor(this, R.color.ehi_primary))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .addAction(action0)
                            .addAction(action1)
//                          .extend(wearableExtender)
                            .build();

            final Random random = new Random();
            NotificationManagerCompat.from(this).notify(random.nextInt(Integer.MAX_VALUE), tripNotification);
            EHINotificationManager.getInstance().setNotificationWasShown(notification);
            EHINotificationManager.getInstance().removeNotification(notification);
        }
    }

    private Intent createIntent(EHINotification notification, IntentUtils.NotificationIntents intentType) {
        Intent intent;
        if (intentType != null) {
            intent = new Intent(new MainActivityHelper.Builder()
                                        .notification(notification)
                                        .intentAction(intentType)
                                        .build(getApplicationContext()));
        }
        else {
            intent = new Intent(new MainActivityHelper.Builder()
                    .notification(notification)
                    .build(getApplicationContext()));
        }

        return intent;
    }

    private String formatScheduledTime(Date scheduledDate) {
        return DateUtilManager.getInstance().formatDateTime(scheduledDate, DateUtilManager.FORMAT_SHOW_TIME);
    }

}