package com.ehi.enterprise.android.utils;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.utils.locations.EHITransferDatesManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EHITransferDatesManagerTest {

    private EHITransferDatesManager transferDatesManager;

    @Before
    public void setup() {
        transferDatesManager = new EHITransferDatesManager();
    }

    //****************
    //****************
    // Round trip
    //****************
    //****************
    @Test
    public void testShouldSendPickupDateToLDTOnRoundTrip() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        when(ehiSolrLocation.isAllDayClosedForPickup()).thenReturn(false);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendPickupDate());
    }

    @Test
    public void testShouldSendPickupTimeToLDTOnRoundTrip() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendPickupTime());
    }

    @Test
    public void testShouldNotSendConflictingPickupTimeToLDTOnRoundTrip() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidAtTimeForPickup()).thenReturn(true);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        transferDatesManager.setSolrLocation(solrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendPickupTime());
    }

    @Test
    public void testShouldSendDropOffDatesToLDTOnRoundTrip() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setDropoffTime(new Date());
        transferDatesManager.setDropoffDate(new Date());
        transferDatesManager.setPickupDate(new Date());
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendDropoffTime());
    }

    @Test
    public void testShouldNotSendDropOffDatesToLDTOnRoundTrip() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        when(ehiSolrLocation.isAllDayClosedForDropoff()).thenReturn(true);
        transferDatesManager.setDropoffTime(new Date());
        transferDatesManager.setDropoffDate(new Date());
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendDropoffDate());
    }

    @Test
    public void testShouldSendDropoffTimeToLDTOnRoundTrip() {
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isInvalidAtTimeForDropoff()).thenReturn(false);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendDropoffTime());
    }

    @Test
    public void testShouldNotSendDropoffTimeToLDTOnRoundTrip() {
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isInvalidAtTimeForDropoff()).thenReturn(true);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendDropoffTime());
    }

    //****************
    //****************
    // Pickup one way
    //****************
    //****************

    @Test
    public void testShouldSendPickupDatesToLDTOnOneWay() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isAllDayClosedForPickup()).thenReturn(false);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendPickupDate());
    }

    @Test
    public void testShouldNotSendPickupDatesToLDTOnOneWay() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isAllDayClosedForPickup()).thenReturn(true);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendPickupDate());
    }

    @Test
    public void testShouldSendPickupTimeToLDTOnOneWay() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidAtTimeForPickup()).thenReturn(false);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(solrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendPickupTime());
    }

    @Test
    public void testShouldNotSendConflictingPickupTimeToLDTOnOneWay() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidAtTimeForPickup()).thenReturn(true);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(solrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendPickupTime());
    }

    @Test
    public void testShouldNeverSendDropOffTimeToLDTOnPickupOneWay() {
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(mock(EHISolrLocation.class));
        Assert.assertFalse(transferDatesManager.shouldSendDropoffTime());
    }

    @Test
    public void testShouldNeverSendDropOffDateToLDTOnPickupOneWay() {
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(mock(EHISolrLocation.class));
        Assert.assertFalse(transferDatesManager.shouldSendDropoffDate());
    }



    //****************
    //****************
    // Dropoff one way
    //****************
    //****************

    @Test
    public void testShouldNeverSendPickupDatesToLDTOnDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendPickupDate());
    }

    @Test
    public void testShouldNeverSendPickupTimeToLDTOnDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendPickupTime());
    }

    @Test
    public void testShouldSendDropOffDateToLDTDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isAllDayClosedForDropoff()).thenReturn(false);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendDropoffDate());
    }

    @Test
    public void testShouldNotSendDropOffDateToLDTDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isAllDayClosedForDropoff()).thenReturn(true);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendDropoffDate());
    }

    @Test
    public void testShouldSendDropOffTimeToLDTDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isInvalidAtTimeForDropoff()).thenReturn(false);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertTrue(transferDatesManager.shouldSendDropoffTime());
    }

    @Test
    public void testShouldNotSendDropOffTimeToLDTDropoff() {
        EHISolrLocation ehiSolrLocation = mock(EHISolrLocation.class);
        when(ehiSolrLocation.isInvalidAtTimeForDropoff()).thenReturn(true);
        transferDatesManager.setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        transferDatesManager.setSolrLocation(ehiSolrLocation);
        Assert.assertFalse(transferDatesManager.shouldSendDropoffTime());
    }
}
