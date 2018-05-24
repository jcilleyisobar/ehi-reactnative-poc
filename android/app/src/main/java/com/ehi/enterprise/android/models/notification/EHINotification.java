package com.ehi.enterprise.android.models.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.notification.NotificationAlarmReceiverService;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class EHINotification extends EHIModel {

    public static final String NOTIFICATION = "NOTIFICATION";

    public static final String INTENT_GET_DIRECTIONS = "INTENT_GET_DIRECTIONS";
    public static final String INTENT_GAS_STATIONS = "INTENT_GAS_STATIONS";
    public static final String INTENT_CALL = "INTENT_CALL";

    public enum NotificationTime {
        OFF(0, 0, R.string.notification_setting_option_do_not_notify),
        THIRTY_MINUTES_BEFORE(Calendar.MINUTE, -30, R.string.notification_setting_option_thirty_minutes_before),
        TWO_HOURS_BEFORE(Calendar.HOUR_OF_DAY, -2, R.string.notification_setting_option_two_hours_before),
        TWENTY_FOUR_HOURS_BEFORE(Calendar.HOUR_OF_DAY, -24, R.string.notification_setting_option_one_day_before);

        public final int calendarField;
        public final int time;
        public final int stringResId;

        NotificationTime(int calendarField, int time, final int stringResId) {
            this.calendarField = calendarField;
            this.time = time;
            this.stringResId = stringResId;
        }
    }

    @SerializedName("locationId")
    private String mLocationId;
    @SerializedName("images")
    private List<EHIImage> mImageList;
    @SerializedName("locationName")
    private String mLocationName;
    @SerializedName("locationPhone")
    private String mLocationPhone;
    @SerializedName("locationLatLng")
    private EHILatLng mLocationLatLng;
    @SerializedName("date")
    private Date mScheduledDate;
    @SerializedName("id")
    private String mId;
    @SerializedName("tripTime")
    private Date mTripTime;
    @SerializedName("isCurrent")
    private boolean mIsCurrentTrip;
    @SerializedName("timezone")
    private String mTimeZone;
    @SerializedName("userFirstName")
    private String mUserFirstName;
    @SerializedName("userLastName")
    private String mUserLastName;
    @SerializedName("carName")
    private String mCarName;
    @SerializedName("carLicensePlate")
    private String mCarLicensePlate;
    private boolean mFailed;

    public EHINotification() {
        mFailed = true;
    }

    private EHINotification(final String locationId,
                            final List<EHIImage> imageList,
                            final String locationName,
                            final String locationPhone,
                            final EHILatLng locationLatLng,
                            final Date scheduledDate,
                            final String id,
                            final Date tripTime,
                            final boolean isCurrentTrip,
                            final String timezone,
                            final String userFirstName,
                            final String userLastName,
                            final String carName,
                            final String carLicensePlate) {
        mLocationId = locationId;
        mImageList = imageList;
        mLocationName = locationName;
        mLocationPhone = locationPhone;
        mLocationLatLng = locationLatLng;
        mScheduledDate = scheduledDate;
        mId = id;
        mTripTime = tripTime;
        mIsCurrentTrip = isCurrentTrip;
        mTimeZone = timezone;
        mFailed = false;
        mUserFirstName = userFirstName;
        mUserLastName = userLastName;
        mCarName = carName;
        mCarLicensePlate = carLicensePlate;
    }

    public static void unscheduleNotifications(Context context, EHINotification... ehiNotifications) {
        for (EHINotification n : ehiNotifications) {
            n.unschedule(context, EHINotificationManager.getInstance());
        }
    }

    public static void unscheduleNotifications(Context context, List<EHINotification> ehiNotificationList) {
        EHINotification[] notificationArray = new EHINotification[ehiNotificationList.size()];
        ehiNotificationList.toArray(notificationArray);
        EHINotification.unscheduleNotifications(context, notificationArray);
    }

    public String getLocationId() {
        return mLocationId;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public String getLocationPhone() {
        return mLocationPhone;
    }

    public EHILatLng getLocationLatLng() {
        return mLocationLatLng;
    }

    public Date getScheduledDate() {
        return mScheduledDate;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public boolean isCurrentTrip() {
        return mIsCurrentTrip;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public String getUserFirstName() {
        return mUserFirstName;
    }

    public String getUserLastName() {
        return mUserLastName;
    }

    public Date getTripTime() {
        return mTripTime;
    }

    public List<EHIImage> getImageList() {
        return mImageList;
    }

    public String getCarName() {
        return mCarName;
    }

    public String getCarLicensePlate() {
        return mCarLicensePlate;
    }

    @Override
    public String toString() {
        return "EHINotification{" +
                "mLocationId='" + mLocationId + '\'' +
                ", mImageList=" + mImageList + '\'' +
                ", mLocationName='" + mLocationName + '\'' +
                ", mLocationPhone='" + mLocationPhone + '\'' +
                ", mLocationLatLng=" + mLocationLatLng +
                ", mScheduledDate=" + mScheduledDate +
                ", mId='" + mId + '\'' +
                ", mTripTime=" + mTripTime +
                ", mIsCurrentTrip=" + mIsCurrentTrip +
                ", mTimeZone='" + mTimeZone + '\'' +
                ", mUserFirstName='" + mUserFirstName + '\'' +
                ", mUserLastName='" + mUserLastName + '\'' +
                ", mCarName='" + mCarName + '\'' +
                ", mCarLicensePlate='" + mCarLicensePlate + '\'' +
                ", mFailed=" + mFailed +
                '}';
    }

    public final void schedule(final Context context, final EHINotificationManager ehiNotificationManager) {
        if (mFailed) {
            DLog.d("Notification scheduling failed");
            DLog.d(toString());
            return;
        }

        final int notificationTimeZoneOffset = TimeZone.getTimeZone(getTimeZone())
                .getOffset(getScheduledDate().getTime());
        final TimeZone currentTimeZone = TimeZone.getDefault();
        final long currentOffset = currentTimeZone.getOffset(new Date().getTime());
        final long notificationOffset = currentOffset - notificationTimeZoneOffset;
        final Date notificationOffsetDate = new Date(getScheduledDate().getTime() + notificationOffset);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = createPendingIntent(context, this);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationOffsetDate.getTime(), alarmIntent);
        ehiNotificationManager.addNotification(this);
    }

    public final void unschedule(final Context context, final EHINotificationManager ehiNotificationManager) {
        if (mFailed) {
            return;
        }
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createPendingIntent(context, this));
        ehiNotificationManager.removeNotification(this);
    }

    private static PendingIntent createPendingIntent(final Context context, final EHINotification notification) {
        final Intent notificationIntent = new Intent(context, NotificationAlarmReceiverService.class);
        notificationIntent.putExtras(new EHIBundle.Builder().putEHIModel(NOTIFICATION, notification).createBundle());
        notificationIntent.setData(Uri.parse("notification://" + notification.getId())); //this is used as the identifier for cancelling from the alarm manager
        return PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    public static class Builder {

        private EHITripSummary mTrip;
        private boolean mIsCurrentTrip = false;
        private NotificationTime mNotificationTime;
        private EHILocation mLocation;
        private String mUserFirstName;
        private String mUserLastName;

        /**
         * Use this to schedule a notification for an current trip
         * When using this, the notification will be scheduled against the return time on the trip object
         *
         * @param trip Trip object to schedule a notification for
         * @return This builder, to continue building
         */
        public final Builder forCurrentTrip(EHITripSummary trip) {
            if (!EHITextUtils.isEmpty(trip.getTicketNumber())) {
                mTrip = trip;
                mIsCurrentTrip = true;
            }
            return this;
        }

        /**
         * Use this to schedule a notification for an upcoming trip
         * When using this, the notification will be scheduled against the pickup time on the trip object
         *
         * @param trip Trip object to schedule a notification for
         * @return This builder, to continue building
         */
        public final Builder forUpcomingTrip(EHITripSummary trip) {
            if (!EHITextUtils.isEmpty(trip.getConfirmationNumber())) {
                mTrip = trip;
                mIsCurrentTrip = false;
            }
            return this;
        }

        /**
         * Use this to pass the location in, we need this for time zone data.
         *
         * @param ehiLocation EHILocation containing timezone data
         * @return This builder, to continue building
         */
        public final Builder atLocation(EHILocation ehiLocation) {
            if (!EHITextUtils.isEmpty(ehiLocation.getTimeZoneId())) {
                mLocation = ehiLocation;
            }

            return this;
        }

        /**
         * The time that the notificaton should be scheduled for, in relation to the pickup/return time on the trip
         *
         * @param notificationTime the value
         * @return This builder, to continue building
         */
        public final Builder notificationTime(NotificationTime notificationTime) {
            mNotificationTime = notificationTime;
            return this;
        }

        /**
         * Used to pass in the userFirstName.
         *
         * @param userFirstName the value
         * @return This builder, to continue building
         */
        public final Builder forUserFirstName(String userFirstName) {
            mUserFirstName = userFirstName;
            return this;
        }

        /**
         * Used to pass in the userLastName.
         *
         * @param userLastName the value
         * @return This builder, to continue building
         */
        public final Builder forUserLastName(String userLastName) {
            mUserLastName = userLastName;
            return this;
        }

        public final EHINotification build() {
            DLog.d(toString());
            if (mTrip != null && mLocation != null && mNotificationTime != null) {
                if (mNotificationTime == NotificationTime.OFF) {
                    return new EHINotification();
                }
                final Calendar tripTime = Calendar.getInstance();
                tripTime.setTime(mIsCurrentTrip ? mTrip.getReturnTime() : mTrip.getPickupTime());
                final Calendar notificationTime = Calendar.getInstance();
                notificationTime.setTime(tripTime.getTime());
                notificationTime.add(mNotificationTime.calendarField, mNotificationTime.time);

                return new EHINotification(mLocation.getId(),
                        (mTrip.getVehicleDetails() != null) ? mTrip.getVehicleDetails().getImages()
                                : null,
                        mLocation.getName(),
                        mLocation.getPrimaryPhoneNumber(),
                        mLocation.getGpsCoordinates(),
                        notificationTime.getTime(),
                        mIsCurrentTrip ? mTrip.getTicketNumber()
                                : mTrip.getConfirmationNumber(),
                        tripTime.getTime(),
                        mIsCurrentTrip,
                        mLocation.getTimeZoneId(),
                        mUserFirstName,
                        mUserLastName,
                        (mTrip.getVehicleDetails().getName() != null) ? mTrip.getVehicleDetails().getName()
                                : null,
                        (mTrip.getVehicleDetails().getLicensePlateNumber() != null) ? mTrip.getVehicleDetails().getLicensePlateNumber()
                                : null);
            }

            return new EHINotification();
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "mTrip=" + mTrip +
                    ", mIsCurrentTrip=" + mIsCurrentTrip +
                    ", mNotificationTime=" + mNotificationTime +
                    ", mLocation=" + mLocation +
                    ", mUserFirstName='" + mUserFirstName + '\'' +
                    ", mUserLastName='" + mUserLastName + '\'' +
                    '}';
        }

    }

}