package com.ehi.enterprise.android.models.location.solr;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class EHISolrWorkingDayInfo extends EHIModel {

    @SerializedName("STANDARD")
    private EHISolrTimeItem mStandardTime;

    @SerializedName("DROP")
    private EHISolrTimeItem mDropTime;

    @SerializedName("AFTER")
    private EHISolrTimeItem mAfterTime;

    public static EHISolrWorkingDayInfo createOpenAllDay() {
        EHISolrWorkingDayInfo workingDayInfo = new EHISolrWorkingDayInfo();
        workingDayInfo.setOpenAllDay();
        return workingDayInfo;
    }

    public static EHISolrWorkingDayInfo createClosedAllDay() {
        EHISolrWorkingDayInfo workingDayInfo = new EHISolrWorkingDayInfo();
        workingDayInfo.setClosedAllDay();
        return workingDayInfo;
    }

    @Nullable
    public List<EHISolrTimeSpan> getStandardTimeSpan() {
        if (mStandardTime == null) {
            return null;
        }
        return mStandardTime.getTimeSpans();
    }

    public boolean isOpenAtTime(Date time) {
        return mStandardTime.isWithinEffectiveTime(time);
    }


    public boolean isAfterHoursAtTime(Date time) {
        return mDropTime != null && mDropTime.isWithinEffectiveTime(time);
    }

    private void setOpenAllDay() {
        mStandardTime = new EHISolrTimeItem();
        mStandardTime.setEffective24Hours(true);

        mDropTime = new EHISolrTimeItem();
        mDropTime.setEffective24Hours(true);
    }

    public EHISolrTimeItem getAfterTime() {
        return mAfterTime;
    }

    private void setClosedAllDay() {
        mStandardTime = new EHISolrTimeItem();
        mStandardTime.setClosed(true);

        mDropTime = new EHISolrTimeItem();
        mDropTime.setClosed(true);
    }

    public EHISolrTimeItem getStandardTime() {
        return mStandardTime;
    }

    public EHISolrTimeItem getDropTime() {
        return mDropTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHISolrWorkingDayInfo)) return false;

        EHISolrWorkingDayInfo that = (EHISolrWorkingDayInfo) o;

        if (mStandardTime != null ? !mStandardTime.equals(that.mStandardTime) : that.mStandardTime != null)
            return false;
        return !(mDropTime != null ? !mDropTime.equals(that.mDropTime) : that.mDropTime != null);

    }

    @Override
    public int hashCode() {
        int result = mStandardTime != null ? mStandardTime.hashCode() : 0;
        result = 31 * result + (mDropTime != null ? mDropTime.hashCode() : 0);
        return result;
    }

    public List<EHISolrTimeSpan> getStandardOpenCloseHours() {
        if (mStandardTime == null) {
            return null;
        }
        return mStandardTime.getTimeSpans();
    }
}
