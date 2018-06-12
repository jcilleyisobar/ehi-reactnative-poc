package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.reservation.DatePickerViewModel;
import com.ehi.enterprise.android.ui.reservation.widget.time_selection.TimeSelectionView;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatePickerViewModelTest extends BaseViewModelTest<DatePickerViewModel> {

    @Override
    protected Class<DatePickerViewModel> getViewModelClass() {
        return DatePickerViewModel.class;
    }

    @Test
    public void shouldSendPickupLocationWhenInRoundTrip(){
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        getViewModel().setPickupLocation(solrLocation);
        assertTrue(getViewModel().shouldSendPickupLocation(TimeSelectionView.MODE_PICKUP_TIME));
    }

    @Test
    public void shouldSendPickupLocationWhenInOneWayPickup() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        getViewModel().setPickupLocation(solrLocation);
        EHISolrLocation dropoffLocation = mock(EHISolrLocation.class);
        getViewModel().setReturnLocation(dropoffLocation);
        assertTrue(getViewModel().shouldSendPickupLocation(TimeSelectionView.MODE_PICKUP_TIME));
    }

    @Test
    public void shouldSendDropoffLocationWhenInOneWayDropoff() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        getViewModel().setPickupLocation(solrLocation);
        EHISolrLocation dropoffLocation = mock(EHISolrLocation.class);
        getViewModel().setReturnLocation(dropoffLocation);
        assertFalse(getViewModel().shouldSendPickupLocation(TimeSelectionView.MODE_RETURN_TIME));
    }

    @Test
    public void shouldReturnRoundTripFlowForPickupTimeSelect() {
        mockEqualPickupAndDropoffLocations();
        assertEquals(getViewModel().getFlow(TimeSelectionView.MODE_PICKUP_TIME), SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
    }

    @Test
    public void shouldReturnRoundTripFlowForReturnTimeSelect() {
        mockEqualPickupAndDropoffLocations();
        assertEquals(getViewModel().getFlow(TimeSelectionView.MODE_RETURN_TIME), SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
    }

    @Test
    public void shouldReturnPickupOneWayFlow() {
        mockDifferentPickupAndDropoffLocations();
        assertEquals(getViewModel().getFlow(TimeSelectionView.MODE_PICKUP_TIME), SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
    }

    @Test
    public void shouldReturnDropoffOneWayFlow() {
        mockDifferentPickupAndDropoffLocations();
        assertEquals(getViewModel().getFlow(TimeSelectionView.MODE_RETURN_TIME), SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
    }

    @Test
    public void shouldSendConflictTimeForPickup() {
        assertTrue(getViewModel().shouldSendConflictingPickupTimeSelectedInTimePicker(TimeSelectionView.MODE_PICKUP_TIME));
    }

    @Test
    public void shouldSendConflictTimeForDropoff() {
        assertFalse(getViewModel().shouldSendConflictingPickupTimeSelectedInTimePicker(TimeSelectionView.MODE_RETURN_TIME));
    }

    private void mockDifferentPickupAndDropoffLocations() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        when(pickupLocation.getPeopleSoftId()).thenReturn("123");
        getViewModel().setPickupLocation(pickupLocation);
        EHISolrLocation dropoffLocation = mock(EHISolrLocation.class);
        when(dropoffLocation.getPeopleSoftId()).thenReturn("234");
        getViewModel().setReturnLocation(dropoffLocation);
    }

    private void mockEqualPickupAndDropoffLocations() {
        EHISolrLocation pickupLocation = mock(EHISolrLocation.class);
        when(pickupLocation.getPeopleSoftId()).thenReturn("123");
        getViewModel().setPickupLocation(pickupLocation);
        EHISolrLocation dropoffLocation = mock(EHISolrLocation.class);
        when(dropoffLocation.getPeopleSoftId()).thenReturn("123");
        getViewModel().setReturnLocation(dropoffLocation);
    }
}
