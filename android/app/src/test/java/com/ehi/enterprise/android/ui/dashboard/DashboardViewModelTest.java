package com.ehi.enterprise.android.ui.dashboard;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.requests.location.GetLocationByIdRequest;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrHoursByLocationIdRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetCurrentTripsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetUpcomingTripsRequest;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetTripsResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetUpcomingTripsResponse;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.ehi.enterprise.mock.network.MockResponseWrapper;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DashboardViewModelTest extends BaseViewModelTest<DashboardViewModel> {


    @Override
    public void setup() {
        EHILoyaltyData data = mock(EHILoyaltyData.class);
        EHIBasicProfile basicProfile = mock(EHIBasicProfile.class);
        ProfileCollection profileCollection = mock(ProfileCollection.class);
        EHIProfile profile = mock(EHIProfile.class);

        String loyaltyNumber = "9001";
        when(data.getLoyaltyNumber()).thenReturn(loyaltyNumber);
        when(basicProfile.getLoyaltyData()).thenReturn(data);
        when(getMockedDelegate().getLoginManager().getProfileCollection()).thenReturn(profileCollection);
        when(profileCollection.getProfile()).thenReturn(profile);
        when(profileCollection.getBasicProfile()).thenReturn(basicProfile);
        when(profile.getIndividualId()).thenReturn("0");
        TypedArray dashboardImages = mock(TypedArray.class);
        when(dashboardImages.length()).thenReturn(99);
        getMockedContext().getMockedResources().addAnswer("obtainTypedArray", dashboardImages);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(false);
                return null;
            }})
            .when(getMockedDelegate().getLoginManager()).logOut();
        super.setup();
    }

    @Test
    public void testInitial_Logged_In_State(){
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        assertVisible(getViewModel().eplusAuthenticatedCellViewState.visibility().getRawValue());
        assertGone(getViewModel().eplusUnauthenticatedCellViewState.visibility().getRawValue());
        assertGone(getViewModel().dashboardEplusExtendedView.visibility().getRawValue());
    }

    @Test
    public void testInitial_Logged_Out_State() {
        getViewModel().onAttachToView();
        assertGone(getViewModel().eplusAuthenticatedCellViewState.visibility().getRawValue());
        assertVisible(getViewModel().eplusUnauthenticatedCellViewState.visibility().getRawValue());
        assertVisible(getViewModel().dashboardEplusExtendedView.visibility().getRawValue());
        assertEquals(getViewModel().progressCounter.getRawValue().intValue(), 0);
    }

    @Test
    public void testUpcoming_Trip_State(){
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries.add(mockTripSummary);
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(null);
        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));
        //don't need to type-cast but for is here for readability as we're checking if a chained request goes off
        GetLocationByIdRequest request = (GetLocationByIdRequest) getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class);
        Assert.assertTrue(request.getRequestUrl().contains(id));
        assertVisible(getViewModel().dashboardUpcomingRentalViewState);
        assertGone(getViewModel().dashboardActiveRentalViewState);
    }


    @Test
    public void testUpcoming_With_Active_Trip_State(){
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries.add(mockTripSummary);
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries = new ArrayList<>(1);
        tripSummaries.add(mockTripSummary);
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);
        when(mockTripSummary.getReturnLocation().getId()).thenReturn(id);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));
        //don't need to type-cast but for is here for readability as we're checking if a chained request goes off
        GetLocationByIdRequest request = (GetLocationByIdRequest) getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class);
        Assert.assertTrue(request.getRequestUrl().contains(id));

        GetSolrHoursResponse hoursResponse = mock(GetSolrHoursResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetSolrHoursByLocationIdRequest.class, new MockResponseWrapper<GetSolrHoursResponse>(
                hoursResponse, true, null
        ));

        GetLocationDetailsResponse detailsResponse = mockDetailsResponse();
        getMockRequestService().addAndExecuteMockResponse(GetLocationByIdRequest.class, new MockResponseWrapper<GetLocationDetailsResponse>(
            detailsResponse, true, null
        ));

        assertGone(getViewModel().dashboardUpcomingRentalViewState);
        assertVisible(getViewModel().dashboardActiveRentalViewState);
    }

    @Test
    public void testActive_No_Upcomming_Trip_State(){
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        //size 0 tripsummaries
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        Assert.assertNull(getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class));

        mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        when(mockTripSummary.getReturnLocation().getId()).thenReturn("");
        tripSummaries = new ArrayList<>(1);
        tripSummaries.add(mockTripSummary);
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);


        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));
        Assert.assertNotNull(getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class));

        GetSolrHoursResponse hoursResponse = mock(GetSolrHoursResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetSolrHoursByLocationIdRequest.class, new MockResponseWrapper<GetSolrHoursResponse>(
                hoursResponse, true, null
        ));

        GetLocationDetailsResponse detailsResponse = mockDetailsResponse();
        getMockRequestService().addAndExecuteMockResponse(GetLocationByIdRequest.class, new MockResponseWrapper<GetLocationDetailsResponse>(
                detailsResponse, true, null
        ));


        assertGone(getViewModel().dashboardUpcomingRentalViewState);
        assertVisible(getViewModel().dashboardActiveRentalViewState);
    }

    @NonNull
    private GetLocationDetailsResponse mockDetailsResponse() {
        GetLocationDetailsResponse detailsResponse = mock(GetLocationDetailsResponse.class);
        EHILocation location = mock(EHILocation.class);
        when(detailsResponse.getLocation()).thenReturn(location);
        when(detailsResponse.getLocation().getId()).thenReturn("10819");
        return detailsResponse;
    }

    @Test
    public void testNoRentalsReturned(){
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        //size 0 tripsummaries
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        Assert.assertNull(getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class));
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));
        assertGone(getViewModel().dashboardUpcomingRentalViewState);
        assertGone(getViewModel().dashboardActiveRentalViewState);
        assertVisible(getViewModel().dashboardImageContainerViewState);
    }

    @Test
    public void testShowIntro() throws Exception {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(true);

        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries.add(mockTripSummary);
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries = new ArrayList<>(1);
        tripSummaries.add(mockTripSummary);
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);
        when(mockTripSummary.getReturnLocation().getId()).thenReturn(id);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));

        GetSolrHoursResponse hoursResponse = mock(GetSolrHoursResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetSolrHoursByLocationIdRequest.class, new MockResponseWrapper<GetSolrHoursResponse>(
                hoursResponse, true, null
        ));

        GetLocationDetailsResponse detailsResponse = mock(GetLocationDetailsResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetLocationByIdRequest.class, new MockResponseWrapper<GetLocationDetailsResponse>(
                detailsResponse, true, null
        ));

        assertVisible(getViewModel().notificationPromptViewState);
        assertGone(getViewModel().dashboardActiveRentalViewState);
        assertGone(getViewModel().dashboardUpcomingRentalViewState);
    }

    @Test
    public void testDontShowIntroLoggedOut() throws Exception {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(false);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(true);

        getViewModel().onAttachToView();
        assertVisible(getViewModel().notificationPromptViewState.visibility().getRawValue());
    }

    @Test
    public void testDontShowIntroLoggedIn() throws Exception {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(false);

        getViewModel().onAttachToView();
        assertGone(getViewModel().notificationPromptViewState.visibility().getRawValue());
    }

    @Test
    public void testDontShowIntroNoTrips() throws Exception {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(true);

        getViewModel().onAttachToView();
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        //size 0 tripsummaries
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        Assert.assertNull(getMockRequestService().getPendingRequests().get(GetLocationByIdRequest.class));
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));
        assertVisible(getViewModel().notificationPromptViewState.visibility().getRawValue());
    }

    @Test
    public void testNotificationPromptConfirmAction() {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(true);

        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries.add(mockTripSummary);
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries = new ArrayList<>(1);
        tripSummaries.add(mockTripSummary);
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);
        when(mockTripSummary.getReturnLocation().getId()).thenReturn(id);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));

        GetSolrHoursResponse hoursResponse = mock(GetSolrHoursResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetSolrHoursByLocationIdRequest.class, new MockResponseWrapper<GetSolrHoursResponse>(
                hoursResponse, true, null
        ));

        GetLocationDetailsResponse detailsResponse = mockDetailsResponse();
        getMockRequestService().addAndExecuteMockResponse(GetLocationByIdRequest.class, new MockResponseWrapper<GetLocationDetailsResponse>(
                detailsResponse, true, null
        ));

        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(false);
        getViewModel().notificationPromptConfirmClicked();

        assertGone(getViewModel().notificationPromptViewState);
        assertVisible(getViewModel().dashboardActiveRentalViewState);
        verify(getMockedDelegate().getSettingsManager()).setEnterpriseRentalAssistant(true);
        verify(getMockedDelegate().getSettingsManager()).setPickupNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
        verify(getMockedDelegate().getSettingsManager()).setReturnNotificationTime(EHINotification.NotificationTime.TWO_HOURS_BEFORE);
        verify(getMockedDelegate().getLocalDataManager()).setShouldShowGeofenceNotificationIntro(false);
    }

    @Test
    public void testNotificationPromptDenyAction() {
        when(getMockedDelegate().getLoginManager().isLoggedIn()).thenReturn(true);
        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(true);

        getViewModel().onAttachToView();
        GetUpcomingTripsResponse upcomingResponse = mock(GetUpcomingTripsResponse.class);
        List<EHITripSummary> tripSummaries = new ArrayList<>(1);
        EHITripSummary mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries.add(mockTripSummary);
        when(upcomingResponse.getFullSortedTripList()).thenReturn(tripSummaries);
        String id = "322";
        when(mockTripSummary.getPickupLocation().getId()).thenReturn(id);
        getMockRequestService().addAndExecuteMockResponse(GetUpcomingTripsRequest.class, new MockResponseWrapper<>(
                upcomingResponse, true, null
        ));
        mockTripSummary = mock(EHITripSummary.class, RETURNS_DEEP_STUBS);
        tripSummaries = new ArrayList<>(1);
        tripSummaries.add(mockTripSummary);
        GetTripsResponse activeResponse = mock(GetTripsResponse.class);
        when(activeResponse.getTripSummariesList()).thenReturn(tripSummaries);
        when(mockTripSummary.getReturnLocation().getId()).thenReturn(id);

        getMockRequestService().addAndExecuteMockResponse(GetCurrentTripsRequest.class, new MockResponseWrapper<>(activeResponse, true, null));

        GetSolrHoursResponse hoursResponse = mock(GetSolrHoursResponse.class);
        getMockRequestService().addAndExecuteMockResponse(GetSolrHoursByLocationIdRequest.class, new MockResponseWrapper<GetSolrHoursResponse>(
                hoursResponse, true, null
        ));

        GetLocationDetailsResponse detailsResponse = mockDetailsResponse();
        getMockRequestService().addAndExecuteMockResponse(GetLocationByIdRequest.class, new MockResponseWrapper<GetLocationDetailsResponse>(
                detailsResponse, true, null
        ));

        when(getMockedDelegate().getLocalDataManager().shouldShowGeofenceNotificationIntro()).thenReturn(false);
        getViewModel().notificationPromptDenyClicked();

        assertGone(getViewModel().notificationPromptViewState);
        assertVisible(getViewModel().dashboardActiveRentalViewState);
        verify(getMockedDelegate().getSettingsManager()).setEnterpriseRentalAssistant(false);
        verify(getMockedDelegate().getSettingsManager()).setPickupNotificationTime(EHINotification.NotificationTime.OFF);
        verify(getMockedDelegate().getSettingsManager()).setReturnNotificationTime(EHINotification.NotificationTime.OFF);
        verify(getMockedDelegate().getLocalDataManager()).setShouldShowGeofenceNotificationIntro(false);
    }

    @Override
    protected Class<DashboardViewModel> getViewModelClass() {
        return DashboardViewModel.class;
    }
}
