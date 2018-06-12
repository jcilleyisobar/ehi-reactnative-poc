package com.ehi.enterprise.android.network.responses.location.solr;


import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSolrLocationsResponse {

    private static final String TAG = "GetSolrLocationsResponse";

    @SerializedName("radiusUsedInKilometers")
    private String mRadiusInKilometers;

    @SerializedName("brandsInResult")
    private List<String> mBrands;

    @SerializedName("locationsResult")
    private List<EHISolrLocation> mSolrLocationList;

    public GetSolrLocationsResponse(List<EHISolrLocation> solrLocationList) {
        mSolrLocationList = solrLocationList;
    }

    public List<EHISolrLocation> getSolrLocationList() {
        return mSolrLocationList;
    }

    public void setSolrLocationList(List<EHISolrLocation> solrLocationList) {
        mSolrLocationList = solrLocationList;
    }

    public List<String> getBrands() {
        return mBrands;
    }

    public long getRadiusInMeters() {
        try {
            return Double.valueOf(mRadiusInKilometers).longValue() * 1000;
        } catch (NumberFormatException e) {
            DLog.w(TAG, e);
            return 0;
        }

    }
}
