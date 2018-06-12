package com.ehi.enterprise.android.ui.location;

import android.location.Location;

import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrLocationsByCoordRequest;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrLocationsResponse;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.mock.network.MockResponseWrapper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationsOnMapViewModelTest extends BaseViewModelTest<LocationsOnMapViewModel>{

    private ArrayList<EHISolrLocation> mMockedSolrLocations;
    @Mock
    private Location mockedLocation;

    @Override
    protected Class<LocationsOnMapViewModel> getViewModelClass() {
        return LocationsOnMapViewModel.class;
    }

    @Before
    public void setup() {
        super.setup();
        mMockedSolrLocations = new ArrayList<>();
    }

    private void setLocationMocksUp() {
        when(mockedLocation.distanceTo(Mockito.any(Location.class)))
                .thenReturn(2100f);
        getMockedDelegate().getMockedLocationApiManager().addAnswer("getLastCurrentLocation", mockedLocation);

        EHISolrLocation mockedSolr = Mockito.spy(EHISolrLocation.class);
        when(mockedSolr.getLocation()).thenReturn(mockedLocation);
        mMockedSolrLocations.add(mockedSolr);
    }

    @Test
    public void searchSolrLocationsForCoordinatesTest() {
        GetSolrLocationsResponse responseSpy = Mockito.spy(new GetSolrLocationsResponse(null));
        List<String> listOfBrands = new ArrayList<>(1);
        listOfBrands.add(EHILocation.BRAND_ALAMO);

        when(responseSpy.getBrands()).thenReturn(listOfBrands);
        ArrayList<EHISolrLocation> listSpy = Mockito.spy(new ArrayList<EHISolrLocation>());
        responseSpy.setSolrLocationList(listSpy);
        when(listSpy.size()).thenReturn(9001);

        //Required syntax for methods that may throw exception on run when spied on
        Mockito.doReturn(9002L).when(responseSpy).getRadiusInMeters();

        getMockRequestService().addMockResponse(GetSolrLocationsByCoordRequest.class, new MockResponseWrapper<>(responseSpy, true, null));
        getViewModel().setApiService(getMockRequestService());

        getViewModel().searchSolrLocationsForCoordinates(new EHILatLng(null));

        Assert.assertTrue(getViewModel().getSolrLocations().size() == 9001);
        Assert.assertTrue(getViewModel().containsNalmo());
        Assert.assertTrue(getViewModel().getSearchRadius() == 9002);
    }

    @Test
    public void setSolrLocationsTest_US() {
        getMockedContext().getMockedResources().addAnswer("getString", "m");

        Locale.setDefault(Locale.US);
        setLocationMocksUp();
        getViewModel().setSearchingNearby(true);
        getViewModel().setSolrLocations(mMockedSolrLocations);

        String usDistance = getViewModel().getSolrLocations().get(0).getDistanceToUserLocation();
        //2100 meters -> ~1.3 mi
        int index = usDistance.indexOf("m");
        Assert.assertTrue(index != -1);
        Assert.assertTrue(Math.abs(Double.parseDouble(usDistance.substring(0, index)) - 1.30488) < 0.1);
    }

    @Test
    public void setSolrLocationsTest_EU() {

        getMockedContext().getMockedResources().addAnswer("getString", "k");

        Locale.setDefault(Locale.FRANCE);
        setLocationMocksUp();
        getViewModel().setSearchingNearby(true);
        getViewModel().setSolrLocations(mMockedSolrLocations);

        String euDistance = getViewModel().getSolrLocations().get(0).getDistanceToUserLocation();
        //2100 meters -> 2.1 km
        int index = euDistance.indexOf("k");
        Assert.assertTrue(index != -1);
        euDistance = euDistance.replace(',', '.');
        Assert.assertTrue(Math.abs(Double.parseDouble(euDistance.substring(0, index)) - 2.1) < 0.1);
    }

    @Test
    public void testShouldShowFilterViewWhenDatesAreSet() {
        when(getViewModel().getDropoffDate()).thenReturn(new Date());
        Assert.assertTrue(getViewModel().areFiltersApplied());
    }

    @Test public void testIfFilterViewIsVisibleWhenThereIsFilterContent() {
        when(getViewModel().areFiltersApplied()).thenReturn(true);
        getViewModel().setLocationFilterViewVisibility();
        assertVisible(getViewModel().locationFilterView.visibility().getValue());
    }

    @Test public void testIfFilterViewIsGoneWhenThereIsNoFilterContent() {
        when(getViewModel().areFiltersApplied()).thenReturn(false);
        getViewModel().setLocationFilterViewVisibility();
        assertGone(getViewModel().locationFilterView);
    }

    @Test
    public void testShouldShowFilterViewWithDatesSetInOnStart() {
        when(getViewModel().getDropoffDate()).thenReturn(new Date());
        getViewModel().onAttachToView();
        assertVisible(getViewModel().locationFilterView);
    }

    @Test
    public void testTimeIsEmptyWhenThereIsNoDate() {
        getViewModel().setPickupTime(new Date());
        getViewModel().setDropoffTime(new Date());
        getViewModel().setDropoffDate(null);
        getViewModel().setPickupDate(null);
        Assert.assertNull(getViewModel().getPickupTime());
        Assert.assertNull(getViewModel().getDropoffTime());
    }

    @Test
    public void testTimeIsNotSetWhenThereIsNoDate() {
        getViewModel().setPickupTime(new Date());
        getViewModel().setDropoffTime(new Date());
        Assert.assertNull(getViewModel().getPickupTime());
        Assert.assertNull(getViewModel().getDropoffTime());
    }

    @Test
    public void testShouldReturnDropoffDateAndTimeInPickupRoundTripFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        getViewModel().setDropoffDate(new Date());
        getViewModel().setDropoffTime(new Date());
        Assert.assertNotNull(getViewModel().getDropoffDateForFilter());
        Assert.assertNotNull(getViewModel().getDropoffTimeForFilter());
    }

    @Test
    public void testShouldReturnPickupDateAndTimeWhenInPickupRoundTripFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP);
        getViewModel().setPickupDate(new Date());
        getViewModel().setPickupTime(new Date());
        Assert.assertNotNull(getViewModel().getPickupDateForFilter());
        Assert.assertNotNull(getViewModel().getPickupTimeForFilter());
    }

    @Test
    public void testShouldReturnPickupDateAndTimeForFilterQueryWhenInPickupOneWayFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        getViewModel().setPickupDate(new Date());
        getViewModel().setPickupTime(new Date());
        Assert.assertNotNull(getViewModel().getPickupDateForFilter());
        Assert.assertNotNull(getViewModel().getPickupTimeForFilter());
    }

    @Test
    public void testShouldNotReturnDropoffDateAndTimeForFilterQueryInPickupOneWayFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY);
        getViewModel().setDropoffDate(new Date());
        getViewModel().setDropoffTime(new Date());
        Assert.assertNull(getViewModel().getDropoffDateForFilter());
        Assert.assertNull(getViewModel().getDropoffTimeForFilter());
    }

    @Test
    public void testShouldNotReturnPickupDateAndTimeWhenInDropoffFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        getViewModel().setPickupDate(new Date());
        getViewModel().setPickupTime(new Date());
        Assert.assertNull(getViewModel().getPickupDateForFilter());
        Assert.assertNull(getViewModel().getPickupTimeForFilter());
    }

    @Test
    public void testShouldReturnDropoffDateAndTimeInDropoffFlow() {
        getViewModel().setFlow(SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY);
        getViewModel().setDropoffDate(new Date());
        getViewModel().setDropoffTime(new Date());
        Assert.assertNotNull(getViewModel().getDropoffDateForFilter());
        Assert.assertNotNull(getViewModel().getDropoffTimeForFilter());
    }

    @Test
    public void testShouldShowFilterTipWhenFirstTimeOnMap() {
        when(getMockedDelegate().getLocalDataManager().isFirstTimeOnMapScreen()).thenReturn(true);
        getViewModel().setFilterTipViewVisibility();
        assertVisible(getViewModel().filterTipView);
    }

    @Test
    public void testShouldShowFilterTipWhenFromLDTAndDatesNotSet() {
        when(getMockedDelegate().getLocalDataManager().isFirstTimeOnMapScreen()).thenReturn(false);
        getViewModel().setIsFromLDT(true);
        getViewModel().setFilterTipViewVisibility();
        assertVisible(getViewModel().filterTipView);
    }

    @Test
    public void testShouldHideFilterTipWhenNotFirstTimeOnMap() {
        when(getMockedDelegate().getLocalDataManager().isFirstTimeOnMapScreen()).thenReturn(false);
        getViewModel().setFilterTipViewVisibility();
        assertGone(getViewModel().filterTipView);
    }

    @Test
    public void testShouldHideFilterTipWhenNotFromLDT() {
        when(getMockedDelegate().getLocalDataManager().isFirstTimeOnMapScreen()).thenReturn(false);
        getViewModel().setIsFromLDT(false);
        getViewModel().setFilterTipViewVisibility();
        assertGone(getViewModel().filterTipView);
    }

    @Test
    public void testShouldHideFilterTipWhenFromLDTAndDatesAreSet() {
        when(getMockedDelegate().getLocalDataManager().isFirstTimeOnMapScreen()).thenReturn(false);
        getViewModel().setIsFromLDT(true);
        getViewModel().setPickupDate(mock(Date.class));
        getViewModel().setDropoffDate(mock(Date.class));
        getViewModel().setFilterTipViewVisibility();
        assertGone(getViewModel().filterTipView);
    }

}
