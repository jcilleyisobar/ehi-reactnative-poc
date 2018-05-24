package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    private static final String TAG = "TimeUtils";

    public static String getMediumDate(Context context, Date date) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateFormat(context);
        return simpleDateFormat.format(date);
    }

    public static String getMediumDateTime(Context context, Date date, Date time) {
        Calendar dateTimeCalendar = mergeDateTime(date, time);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateFormat(context);
        return simpleDateFormat.format(dateTimeCalendar.getTime());
    }

    public static Calendar mergeDateTime(Date date, Date time) {
        Calendar dateTimeCalendar = Calendar.getInstance();
        Calendar timeCalendar = Calendar.getInstance();

        dateTimeCalendar.setTime(date);
        if (time != null) {
            timeCalendar.setTime(time);
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            dateTimeCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        }
        return dateTimeCalendar;
    }

    public static String getDayYear(Context context, Date date) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        return DateUtils.formatDateTime(context, dateCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
    }

    public static Date[] splitDateAndTime(Date dateAndTime) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String stringDate = dateTimeFormat.format(dateAndTime);
        String[] dtArray = stringDate.split(" ");
        Date[] dateTimeArray = new Date[2];
        for (int i = 0; i < dtArray.length; i++) {
            try {
                if (i == 0) {
                    dateTimeArray[i] = dateFormat.parse(dtArray[i]);
                }
                if (i == 1) {
                    dateTimeArray[i] = timeFormat.parse(dtArray[i]);
                }
            } catch (ParseException e) {
                DLog.w(TAG, e);
            }
        }

        return dateTimeArray;
    }
}
