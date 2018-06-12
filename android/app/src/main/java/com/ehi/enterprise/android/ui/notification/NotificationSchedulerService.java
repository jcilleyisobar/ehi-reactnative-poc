package com.ehi.enterprise.android.ui.notification;

import android.content.Intent;
import android.os.Message;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetLocationByIdRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.ui.service.BaseApiWorkerService;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.SettingsManager;
import com.google.gson.reflect.TypeToken;
import com.isobar.android.newinstancer.Extra;

import java.util.List;

public class NotificationSchedulerService extends BaseApiWorkerService {
    private static final String TAG = "NotificationScheduler";

    @Extra(value = List.class, type = EHITripSummary.class, required = false)
    public static final String CURRENT_RENTALS = "CURRENT_RENTALS";

    @Extra(value = List.class, type = EHITripSummary.class, required = false)
    public static final String UPCOMING_RENTALS = "UPCOMING_RENTALS";

    private boolean mIsCurrent;

    public NotificationSchedulerService() {
        super();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null && intent.getExtras() != null) {
            NotificationSchedulerServiceHelper.Extractor extractor
                    = new NotificationSchedulerServiceHelper.Extractor(intent);

            if (extractor.currentRentals() != null) {
                sendMessage(extractor.currentRentals());
                mIsCurrent = true;
            } else {
                sendMessage(extractor.upcomingRentals());
                mIsCurrent = false;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void onMessage(final Message message) {
        DLog.d(TAG, "onMessage() called with: " + "message = [" + message + "]");
        List<EHITripSummary> tripSummaries = ((List<EHITripSummary>) message.obj);
        for (EHITripSummary summary : tripSummaries) {
            requestLocationsAndSchedule(summary, mIsCurrent);
        }
    }

    private void requestLocationsAndSchedule(final EHITripSummary tripSummary, final boolean isCurrent) {
        final EHILocation location = isCurrent ? tripSummary.getReturnLocation() : tripSummary.getPickupLocation();

        if (!EHITextUtils.isEmpty(location.getTimeZoneId())) {
            scheduleNotification(tripSummary, location, isCurrent);
            return;
        }

        EHILocation requestedLocation = LocalDataManager.getInstance()
                .getObjectFromCache(location.getId(), new TypeToken<EHILocation>() {
                }.getType());
        if (requestedLocation != null) {
            scheduleNotification(tripSummary, requestedLocation, isCurrent);
        } else {
            getApiService().performRequest(new GetLocationByIdRequest(location.getId()), new IApiCallback<GetLocationDetailsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetLocationDetailsResponse> response) {
                    if (response.isSuccess()) {
                        LocalDataManager.getInstance().addObjectToCache(response.getData().getLocation().getId(), response.getData().getLocation());
                        scheduleNotification(tripSummary, response.getData().getLocation(), isCurrent);
                    }
                }
            });
        }
    }

    private void scheduleNotification(final EHITripSummary tripSummary, final EHILocation ehiLocation, boolean isCurrent) {
        EHINotification.Builder builder = new EHINotification.Builder();
        if (isCurrent) {
            builder.forCurrentTrip(tripSummary);
            builder.notificationTime(SettingsManager.getInstance().getReturnNotificationTime());
        } else {
            builder.forUpcomingTrip(tripSummary);
            builder.notificationTime(SettingsManager.getInstance().getPickupNotificationTime());
        }

        if (LoginManager.getInstance().isLoggedIn()) {
            final EHINotification notification = builder.forUserFirstName(LoginManager.getInstance().getProfileCollection().getBasicProfile().getFirstName())
                    .forUserLastName(LoginManager.getInstance().getProfileCollection().getBasicProfile().getLastName())
                    .atLocation(ehiLocation)
                    .build();

            if (EHINotificationManager.getInstance().wasNotificationShown(notification)) {
                return;
            }

            notification.unschedule(NotificationSchedulerService.this, EHINotificationManager.getInstance());
            notification.schedule(NotificationSchedulerService.this, EHINotificationManager.getInstance());
        }
    }
}
