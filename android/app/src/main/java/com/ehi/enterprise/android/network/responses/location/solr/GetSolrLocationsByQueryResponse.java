package com.ehi.enterprise.android.network.responses.location.solr;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSolrLocationsByQueryResponse extends BaseResponse {

    @SerializedName("airports")
    private List<EHISolrLocation> mAirportLocationsList;
    @SerializedName("branches")
    private List<EHISolrLocation> mBranchLocationsList;
    @SerializedName("cities")
    private List<EHICityLocation> mCityLocations;
    @SerializedName("postalCodes")
    private List<EHIPostalCodeLocation> mPostalCodeLocations;

    public List<EHISolrLocation> getAirportLocationsList() {
        return mAirportLocationsList;
    }

    public List<EHISolrLocation> getBranchLocationsList() {
        return mBranchLocationsList;
    }

    public List<EHICityLocation> getCityLocations() {
        return mCityLocations;
    }

    public List<EHIPostalCodeLocation> getPostalCodeLocations() {
        return mPostalCodeLocations;
    }

    public void setAirportLocationsList(List<EHISolrLocation> locations) {
        mAirportLocationsList = locations;
    }

}