package com.ehi.enterprise.android.models.location;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.solr.EHISolrTimeItem;
import com.ehi.enterprise.android.models.location.solr.EHISolrTimeSpan;
import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EHIWorkingDayInfo extends EHIModel {

    private static final String TAG = EHIWorkingDayInfo.class.getSimpleName();

    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public EHIWorkingDayInfo(EHISolrTimeItem solrItem, String key) {
        mDate = key;
        mOpenAllDay = solrItem.isEffective24Hours();
        mCloseAllDay = solrItem.isClosed();
        setOpenCloseTimes(solrItem.getTimeSpans());
    }

    @SerializedName("day")
    private String mDayName;

    @SerializedName("date")
    private String mDate;

    @SerializedName("open_all_day")
    private boolean mOpenAllDay;

    @SerializedName("closed_all_day")
    private boolean mCloseAllDay;

    @SerializedName("open_close_times")
    private List<EHIOpenCloseTime> mOpenCloseTimes;

    public String getDayName() {
        return mDayName;
    }

    public String getDate() {
        return mDate;
    }

    @Nullable
    public Date getDateObject() {
        try {
            return sDateFormatter.parse(mDate);
        } catch (ParseException e) {
            DLog.w(TAG, e);
            return null;
        }
    }

    public boolean isOpenAllDay() {
        return mOpenAllDay;
    }

    public boolean isCloseAllDay() {
        return mCloseAllDay;
    }

    public List<EHIOpenCloseTime> getOpenCloseTimes() {
        return mOpenCloseTimes;
    }

    private void setOpenCloseTimes(List<EHISolrTimeSpan> timeSpans) {
        mOpenCloseTimes = new ArrayList<>();
        for (EHISolrTimeSpan span : timeSpans) {
            mOpenCloseTimes.add(new EHIOpenCloseTime(span));
        }
    }

}
