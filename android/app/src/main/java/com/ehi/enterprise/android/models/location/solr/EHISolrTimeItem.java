package com.ehi.enterprise.android.models.location.solr;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class EHISolrTimeItem extends EHIModel {

    @SerializedName("open24Hours")
    private boolean mEffective24Hours;

    @SerializedName("closed")
    private boolean mClosed;

    @SerializedName("hours")
    private List<EHISolrTimeSpan> mTimeSpans;

    public boolean isEffective24Hours() {
        return mEffective24Hours;
    }

    public boolean isClosed() {
        return mClosed;
    }

    public List<EHISolrTimeSpan> getTimeSpans() {
        return mTimeSpans;
    }

    public boolean isWithinEffectiveTime(Date time) {
        if (mEffective24Hours) {
            return true;
        }

        if (mClosed) {
            return false;
        }

        if (mTimeSpans != null) {
            for (EHISolrTimeSpan effectiveSpan : mTimeSpans) {
                if (effectiveSpan.contains(time)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setEffective24Hours(boolean effective24Hours) {
        mEffective24Hours = effective24Hours;
    }

    public void setClosed(boolean closed) {
        mClosed = closed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHISolrTimeItem)) return false;

        EHISolrTimeItem that = (EHISolrTimeItem) o;

        if (mEffective24Hours != that.mEffective24Hours) return false;
        if (mClosed != that.mClosed) return false;
        return !(mTimeSpans != null ? !mTimeSpans.equals(that.mTimeSpans) : that.mTimeSpans != null);

    }

    @Override
    public int hashCode() {
        int result = (mEffective24Hours ? 1 : 0);
        result = 31 * result + (mClosed ? 1 : 0);
        result = 31 * result + (mTimeSpans != null ? mTimeSpans.hashCode() : 0);
        return result;
    }
}
