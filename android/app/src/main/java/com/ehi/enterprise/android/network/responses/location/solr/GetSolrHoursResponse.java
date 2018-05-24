package com.ehi.enterprise.android.network.responses.location.solr;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GetSolrHoursResponse extends EHIModel {

    @SerializedName("peopleSoftId")
    private String mLocationId;

    @SerializedName("data")
    private Map<String, EHISolrWorkingDayInfo> mDaysInfo;

    public String getLocationId() {
        return mLocationId;
    }

    public Map<String, EHISolrWorkingDayInfo> getDaysInfo() {
        return mDaysInfo;
    }
}
