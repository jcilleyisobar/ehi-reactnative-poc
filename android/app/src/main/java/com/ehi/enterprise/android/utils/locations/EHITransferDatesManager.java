package com.ehi.enterprise.android.utils.locations;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;

import java.util.Date;

public class EHITransferDatesManager {

    private EHISolrLocation solrLocation;
    private Date pickupDate;
    private Date dropoffDate;
    private Date pickupTime;
    private Date dropoffTime;
    private @SearchLocationsActivity.Flow int flow;

    public EHITransferDatesManager() {

    }

    public EHITransferDatesManager(@Nullable EHISolrLocation solrLocation,
                                   @Nullable Date pickupDate,
                                   @Nullable Date dropoffDate,
                                   @Nullable Date pickupTime,
                                   @Nullable Date dropoffTime,
                                   @SearchLocationsActivity.Flow int flow) {
        this.solrLocation = solrLocation;
        this.pickupDate = pickupDate;
        this.dropoffDate = dropoffDate;
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
        this.flow = flow;
    }

    public void setSolrLocation(EHISolrLocation solrLocation) {
        this.solrLocation = solrLocation;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setDropoffDate(Date dropoffDate) {
        this.dropoffDate = dropoffDate;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public void setDropoffTime(Date dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public boolean shouldSendDropoffTime() {
        return solrLocation != null && shouldSendDropoffDate() && !solrLocation.isInvalidAtTimeForDropoff();
    }

    public boolean shouldSendDropoffDate() {
        final boolean isDropoffValid = solrLocation != null && !solrLocation.isAllDayClosedForDropoff();
        switch (flow) {
            case SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY:
                return false;
            case SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY:
                return isDropoffValid;
            default:
            case SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP:
                final boolean hasDropoffInfo = dropoffDate != null || dropoffTime != null;
                final boolean hasPickupInfo = pickupDate != null || pickupTime != null;
                final boolean hasOnlyDropoffLocationInfo = hasDropoffInfo && !hasPickupInfo;
                return !hasOnlyDropoffLocationInfo && isDropoffValid;
        }
    }

    public boolean shouldSendPickupTime() {
        return solrLocation != null && shouldSendPickupDate() && !solrLocation.isInvalidAtTimeForPickup() ;
    }

    public boolean shouldSendPickupDate() {
        return solrLocation != null && !solrLocation.isAllDayClosedForPickup() && flow != SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY;
    }
}
