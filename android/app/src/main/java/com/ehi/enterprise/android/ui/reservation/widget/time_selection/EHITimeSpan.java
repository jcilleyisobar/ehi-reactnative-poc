package com.ehi.enterprise.android.ui.reservation.widget.time_selection;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.format.DateFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EHITimeSpan {

    private static final String TAG = EHITimeSpan.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_WORKING_TIME,
            TYPE_NOT_WORKING_TIME,
            TYPE_AFTER_HOURS_TIME})
    public @interface SpanType {
    }

    public static final int TYPE_WORKING_TIME = 1;
    public static final int TYPE_NOT_WORKING_TIME = 2;
    public static final int TYPE_AFTER_HOURS_TIME = 3;

    private Date mTime;

    @SpanType
    private int mWorkingSpanType = TYPE_NOT_WORKING_TIME;

    @SpanType
    private int mClosedSpanType = TYPE_NOT_WORKING_TIME;

    public EHITimeSpan(long millis) {
        Calendar gc = Calendar.getInstance();
        gc.setTimeInMillis(millis - TimeZone.getDefault().getRawOffset());
        mTime = gc.getTime();
    }

    public String getFormattedTimeString(Context context) {
        SimpleDateFormat localizedTime = (SimpleDateFormat) DateFormat.getTimeFormat(context);
        return localizedTime.format(mTime);
    }

    public Date getTime() {
        return mTime;
    }

    public
    @SpanType
    int getWorkingSpanType() {
        return mWorkingSpanType;
    }

    public void setWorkingSpanType(@SpanType int workingSpanType) {
        mWorkingSpanType = workingSpanType;
    }

    public int getClosedSpanType() {
        return mClosedSpanType;
    }

    public void setClosedSpanType(int closedSpanType) {
        mClosedSpanType = closedSpanType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHITimeSpan)) return false;

        EHITimeSpan that = (EHITimeSpan) o;

        if (mWorkingSpanType != that.mWorkingSpanType) return false;
        if (mClosedSpanType != that.mClosedSpanType) return false;
        return !(mTime != null ? !mTime.equals(that.mTime) : that.mTime != null);

    }

    @Override
    public int hashCode() {
        int result = mTime != null ? mTime.hashCode() : 0;
        result = 31 * result + mWorkingSpanType;
        result = 31 * result + mClosedSpanType;
        return result;
    }
}
