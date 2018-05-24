package com.ehi.enterprise.android.ui.dashboard;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.models.support.EHIConfigFeed;
import com.ehi.enterprise.android.models.support.EHISupportInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetCountriesRequest;
import com.ehi.enterprise.android.network.requests.location.GetLocationByIdRequest;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrHoursByLocationIdRequest;
import com.ehi.enterprise.android.network.requests.profile.GetProfileRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetCurrentTripsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetRetrieveReservationRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetUpcomingTripsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostEmeraldClubLoginRequest;
import com.ehi.enterprise.android.network.requests.support.SupportInfoRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.android.network.responses.location.GetLocationDetailsResponse;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetTripsResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetUpcomingTripsResponse;
import com.ehi.enterprise.android.network.services.EHIServicesError;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.ForeSeeSurveyManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.ui.dashboard.DashboardFragment.SCREEN_NAME;

@AutoUnbindAll
public class DashboardViewModel extends CountrySpecificViewModel {

    private static final String TAG = "DASHBOARD_VIEW_MODEL";
    //region reactorvars
    final ReactorViewState notificationPromptViewState = new ReactorViewState();
    final ReactorVar<Boolean> mShouldShowNotificationPrompt = new ReactorVar<>(false);
    final ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    final ReactorVar<Boolean> mCurrentRentalsComplete = new ReactorVar<>(false);
    final ReactorVar<EHIReservation> mRequestedReservation = new ReactorVar<>();
    final ReactorVar<ResponseWrapper> mSupportInfoErrorWrapper = new ReactorVar<>();
    final ReactorVar<ProfileCollection> mUserProfileCollection = new ReactorVar<>();
    final ReactorVar<List<EHIWayfindingStep>> mWayfindings = new ReactorVar<>();
    final ReactorVar<Integer> progressCounter = new ReactorVar<>(0);
    final ReactorViewState dashboardUpcomingRentalViewState = new ReactorViewState();
    final ReactorViewState dashboardActiveRentalViewState = new ReactorViewState();
    final ReactorViewState dashboardImageContainerViewState = new ReactorViewState();
    final ReactorTextViewState dashboardImageCaptionViewState = new ReactorTextViewState();
    final ReactorImageViewState dashboardImageViewState = new ReactorImageViewState();
    final ReactorViewState dashboardEplusExtendedView = new ReactorViewState();
    final ReactorVar<EHIConfigFeed> mConfigFeed = new ReactorVar<>();
    final ReactorViewState clearRecentActivityButtonViewState = new ReactorViewState();
    final ReactorViewState weekendSpecialViewState = new ReactorViewState();
    final ReactorViewState eplusAuthenticatedCellViewState = new ReactorViewState();
    final ReactorViewState eplusUnauthenticatedCellViewState = new ReactorViewState();
    final ReactorVar<List<ReservationInformation>> mAbandonedReservations = new ReactorVar<>();
    final ReactorVar<Boolean> mAnimationHasEnded = new ReactorVar<>(false);
    final ReactorVar<Boolean> mCountryListHasLoaded = new ReactorVar<>(false);
    private final ReactorVar<Boolean> mIsCurrentRentalReturnInAfterHours = new ReactorVar<>(false);
    private final ReactorVar<Boolean> mShowSurveyDialog = new ReactorVar<>(false);

    final ReactorVar<List<EHITripSummary>> mCurrentRentals = new ReactorVar<List<EHITripSummary>>(null) {
        @Override
        public void setValue(List<EHITripSummary> value) {
            super.setValue(value);
            updateRentalViewState();
        }
    };
    ReactorVar<List<EHITripSummary>> mUpcomingRentals = new ReactorVar<List<EHITripSummary>>(null) {
        @Override
        public void setValue(List<EHITripSummary> value) {
            super.setValue(value);
            updateRentalViewState();
        }
    };
    private ReactorVar<EHILocation> mActiveRentalLocationDetails = new ReactorVar<>();
    //endregion

    private boolean mCurrentRentalsRequestInProgress;
    private boolean mUpcomingRentalsInProgress;
    private String mScreenState = null;
    private Map<String, String> mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(getScreenState(), null);
    //used to track the state of our request for active rental details
    private boolean mLocationDetailsRequestInitiated = false;
    public ReactorVar<Boolean> hasRental = new ReactorVar<>(false);

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        getManagers().getForeseeSurveyManager().setInviteListener(new ForeSeeSurveyManager.InviteListener() {
            @Override
            public void onInviteShow() {
                mShowSurveyDialog.setValue(true);
            }

            @Override
            public void onInviteAccept() {

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        mAnimationHasEnded.setValue(false);
        mLocationDetailsRequestInitiated = false;
        progressCounter.setRawValue(0);
        hasRental.setValue(false);
        mCurrentRentals.setRawValue(null); // Won't trigger a reaction
        mUpcomingRentals.setRawValue(null);
        dashboardUpcomingRentalViewState.setVisibility(View.GONE);
        dashboardActiveRentalViewState.setVisibility(View.GONE);
        dashboardImageContainerViewState.setVisibility(View.VISIBLE);
        weekendSpecialViewState.setVisible(isWeekendSpecialAvailable());

        if (isUserLoggedIn()) {
            dashboardEplusExtendedView.setVisibility(View.GONE);

            if (getManagers().getNotificationManager().shouldShowNotificationPrompt()) {
                mShouldShowNotificationPrompt.setValue(true);
            }
        } else {
            dashboardEplusExtendedView.setVisibility(View.VISIBLE);
        }
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());

        requestSupportInfo();
        requestCountriesList();
        refreshECToken();
        setUpEplusBanner();
        trackScreenChange();
        getRandomImage();
        setUpRentals();
    }

    private void setUpEplusBanner() {
        if (isUserLoggedIn()) {
            eplusUnauthenticatedCellViewState.setVisibility(View.GONE);
            eplusAuthenticatedCellViewState.setVisibility(View.VISIBLE);
            updateUserProfile();
            if (getScreenState() == null) {
                setScreenState(EHIAnalytics.State.STATE_NONE.value);
                mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(EHIAnalytics.State.STATE_NONE.value, null);
            }
        } else {
            setScreenState(EHIAnalytics.State.STATE_UNAUTH.value);
            mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(getScreenState(), null);

            eplusUnauthenticatedCellViewState.setVisibility(View.VISIBLE);
            eplusAuthenticatedCellViewState.setVisibility(View.GONE);
        }
    }

    private void setUpRentals() {
        final String loyaltyNumber = getLoyaltyNumber();
        if (loyaltyNumber != null) {
            requestCurrentRentals(loyaltyNumber);
            requestUpcomingRentals(loyaltyNumber);
            mActiveRentalLocationDetails.setValue(null);
        } else {
            setCurrentRentals(null);
            setUpcomingRentals(null);
            decreaseDashboardProgress();
        }
    }

    private void refreshECToken() {
        // try to refresh the token on app start
        if (getManagers().getReservationManager().getEmeraldClubAuthToken() == null &&
                isEmeraldClubDataSaved()) {
            showProgress(true);
            performRequest(new PostEmeraldClubLoginRequest(getManagers().getReservationManager().getEmeraldClubAuthData()),
                    new IApiCallback<EHIProfileResponse>() {
                        @Override
                        public void handleResponse(final ResponseWrapper<EHIProfileResponse> response) {
                            if (response.isSuccess()) {
                                getManagers().getReservationManager().setEmeraldClubProfile(response.getData());
                                getManagers().getReservationManager().setEmeraldClubAuthToken(response.getData().getAuthToken());
                                getManagers().getReservationManager().saveEmeraldClubAuthData(response.getData().getEncryptedAuthData());
                                setUpRentals();
                            } else {
                                getManagers().getReservationManager().removeEmeraldClubAccount();
                            }
                            showProgress(false);
                        }
                    });
        }
    }

    public void requestSupportInfo() {
        final String countryCode = getManagers().getLocalDataManager().getPreferredCountryCode();
        performRequest(new SupportInfoRequest(countryCode), new IApiCallback<EHISupportInfo>() {
            @Override
            public void handleResponse(ResponseWrapper<EHISupportInfo> response) {
                //do not need to react or do something, this is background request
                if (response.isSuccess()) {
                    getManagers().getSupportInfoManager().saveSupportInfo(countryCode, response.getData());
                } else {
                    if (!getManagers().getSupportInfoManager().hasSupportInfoForCountry(countryCode)) {
                        mSupportInfoErrorWrapper.setValue(response);
                    }
                    if (response.getErrorCode() == EHIServicesError.ErrorCode.INVALID_API_KEY) {
                        setErrorResponse(response);
                    }
                }
            }
        });
    }

    public void requestCountriesList() {
        final LocalDataManager localDataManager = getManagers().getLocalDataManager();
        if (ListUtils.isEmpty(localDataManager.getCountriesList())
                || !localDataManager.havePreferredRegion()) {
            showProgress(true);
        }
        performRequest(new GetCountriesRequest(), new IApiCallback<GetCountriesResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetCountriesResponse> response) {
                //do not need to react or do something, this is background request
                if (response.isSuccess()) {
                    getManagers().getLocalDataManager().setCountriesList(response.getData().getCountries());
                }

                showProgress(false);
                mCountryListHasLoaded.setValue(true);
            }
        });
    }

    private void showDashboardProgress() {
        progressCounter.setValue(progressCounter.getRawValue() + 1);
    }

    private void decreaseDashboardProgress() {
        if (progressCounter.getRawValue() > 0) {
            progressCounter.setValue(progressCounter.getRawValue() - 1);
        }
    }

    private void requestCurrentRentals(@NonNull String userId) {
        showDashboardProgress();
        mCurrentRentalsRequestInProgress = true;
        performRequest(new GetCurrentTripsRequest(userId), new IApiCallback<GetTripsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetTripsResponse> response) {
                decreaseDashboardProgress();
                mCurrentRentalsRequestInProgress = false; //needs to set before next calls due to vm observing setValue
                if (response.isSuccess()) {
                    GetTripsResponse getTripsResponse = response.getData();
                    setCurrentRentals(getTripsResponse.getTripSummariesList());
                } else {
                    setErrorResponse(response);
                    updateRentalViewState();
                }
            }
        });
    }

    private void requestUpcomingRentals(@NonNull String userId) {
        showDashboardProgress();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH, 6);
        mUpcomingRentalsInProgress = true;
        performRequest(new GetUpcomingTripsRequest(userId, endCalendar.getTime(), true, 5), new IApiCallback<GetUpcomingTripsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetUpcomingTripsResponse> response) {
                decreaseDashboardProgress();
                mUpcomingRentalsInProgress = false; //needs to set before next calls due to vm observing setValue
                if (response.isSuccess()) {
                    List<EHITripSummary> sortedList = response.getData().getFullSortedTripList();
                    setUpcomingRentals(sortedList);
                } else {
                    setErrorResponse(response);
                    updateRentalViewState();
                }
            }
        });
    }

    public void requestReservation(@NonNull EHITripSummary ehiTripSummary) {
        showProgress(true);
        performRequest(new GetRetrieveReservationRequest(ehiTripSummary.getConfirmationNumber(),
                        ehiTripSummary.getCustomerFirstName(),
                        ehiTripSummary.getCustomerLastName()),
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            setRequestedReservation(response.getData());
                        } else {
                            setErrorResponse(response);
                        }
                    }
                });
    }

    public ResponseWrapper getErrorResponse() {
        return mErrorWrapper.getValue();
    }

    public void setErrorResponse(ResponseWrapper errorWrapper) {
        // verify invalid EC token at startup
        if (errorWrapper != null) {
            if (errorWrapper.getErrorCode() != null && EHIServicesError.ErrorCode.CROS_INVALID_AUTH_TOKEN.equals(errorWrapper.getErrorCode())) {
                getManagers().getReservationManager().removeEmeraldClubAccount();
            } else {
                mErrorWrapper.setValue(errorWrapper);
            }
        } else {
            mErrorWrapper.setValue(null);
        }
    }

    public ResponseWrapper getSupportInfoErrorWrapper() {
        return mSupportInfoErrorWrapper.getValue();
    }

    public void setSupportInfoErrorWrapper(ResponseWrapper supportInfoErrorWrapper) {
        mSupportInfoErrorWrapper.setValue(supportInfoErrorWrapper);
    }

    @Override
    public ProfileCollection getUserProfileCollection() {
        return mUserProfileCollection.getValue();
    }

    @Nullable
    public String getLoyaltyNumber() {
        ProfileCollection profile = null;
        if (isUserLoggedIn()) {
            profile = getUserProfileCollection();
        } else if (isLoggedIntoEmeraldClub()) {
            profile = getEmeraldClubProfile();
        }

        if (profile == null) {
            return null;
        }

        EHIBasicProfile basicProfile = profile.getBasicProfile();
        if (basicProfile == null) {
            return null;
        }
        EHILoyaltyData ehiLoyaltyData = basicProfile.getLoyaltyData();
        if (ehiLoyaltyData == null) {
            return null;
        }

        return ehiLoyaltyData.getLoyaltyNumber();
    }

    public List<EHITripSummary> getCurrentRentals() {
        return mCurrentRentals.getValue();
    }

    public void setCurrentRentals(List<EHITripSummary> tripSummaries) {
        getManagers().getForeseeSurveyManager().setCurrentRentals(tripSummaries);
        mCurrentRentals.setValue(tripSummaries);
    }

    public void updateRentalViewState() {
        if ((isCurrentRentalsRequestInProgress()
                || isUpcomingRentalsInProgress())) {
            return;
        }

        if (getManagers().getLocalDataManager().shouldShowGeofenceNotificationIntro()) {
            hasRental.setValue(true);
            notificationPromptViewState.setVisibility(View.VISIBLE);
            return;
        } else {
            notificationPromptViewState.setVisibility(View.GONE);
        }


        if (getCurrentRentals() != null && !getCurrentRentals().isEmpty()) {
            if (!mLocationDetailsRequestInitiated && mActiveRentalLocationDetails.getRawValue() == null) {
                requestLocationDetails(getCurrentRentals(), true);
                return;
            }
            hasRental.setValue(true);

            mScreenState = EHIAnalytics.State.STATE_CURRENT.value;
            mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(mScreenState, null);

            dashboardImageContainerViewState.setVisibility(View.GONE);
            dashboardActiveRentalViewState.setVisibility(View.VISIBLE);
            dashboardUpcomingRentalViewState.setVisibility(View.GONE);
        } else if (getUpcomingRentals() != null && !getUpcomingRentals().isEmpty()) {
            requestLocationDetails(getUpcomingRentals(), false);
            hasRental.setValue(true);
            mScreenState = EHIAnalytics.State.STATE_UPCOMING.value;
            mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(mScreenState, getUpcomingRentals().get(0).getPickupTime());

            dashboardImageContainerViewState.setVisibility(View.GONE);
            dashboardUpcomingRentalViewState.setVisibility(View.VISIBLE);
        } else {
            hasRental.setValue(false);
            if (isUserLoggedIn() || isLoggedIntoEmeraldClub()) {
                mScreenState = EHIAnalytics.State.STATE_NONE.value;
                mAnalyticsMap = EHIAnalyticsDictionaryUtils.dashboard(mScreenState, null);
            }
            getRandomImage();
            dashboardImageContainerViewState.setVisibility(View.VISIBLE);
            dashboardActiveRentalViewState.setVisibility(View.GONE);
            dashboardUpcomingRentalViewState.setVisibility(View.GONE);
        }
    }


    public boolean isTrackingEnabled() {
        return getManagers().getSettingsManager().isSearchHistoryEnabled();
    }

    public boolean hasSavedLocations() {
        return getManagers().getLocationManager().hasSavedLocations();
    }

    public Map<String, EHISolrLocation> getFavoriteLocations() {
        return getManagers().getLocationManager().getFavoriteLocations();
    }

    public void removeFavoriteLocation(ReservationInformation reservationInformation) {
        getManagers().getLocationManager().removeFavoriteLocation(reservationInformation.getPickupLocation());
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
    }

    public void addFavoriteLocation(ReservationInformation reservationInformation) {
        getManagers().getLocationManager().addFavoriteLocation(reservationInformation.getPickupLocation());
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
    }

    public List<ReservationInformation> getRecentReservation() {
        return getManagers().getLocationManager().getRecentReservations();
    }

    public int removeRecentReservation(ReservationInformation reservationInformation) {
        int index = getManagers().getLocationManager().removeRecentReservation(reservationInformation);
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
        return index;
    }

    public void addRecentReservation(ReservationInformation reservationInformation) {
        getManagers().getLocationManager().saveRecentReservation(reservationInformation);
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
    }

    public List<ReservationInformation> getAbandonedReservations() {
        return mAbandonedReservations.getValue();
    }

    public int removeAbandonedReservation(ReservationInformation reservationInformation) {
        int index = getManagers().getLocationManager().removeAbandonedReservation(reservationInformation);
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
        return index;
    }

    public void addAbandonedReservation(int index, ReservationInformation reservationInformation) {
        getManagers().getLocationManager().saveAbandonReservation(index, reservationInformation);
        mAbandonedReservations.setValue(getManagers().getLocationManager().commitAndGetAbandonedReservations());
    }

    public void clearRecentActivities() {
        getManagers().getLocationManager().clearRecentActivities();
        mAbandonedReservations.setValue(null);
    }

    public List<EHITripSummary> getUpcomingRentals() {
        return mUpcomingRentals.getValue();
    }

    public void setUpcomingRentals(List<EHITripSummary> upcomingRentals) {
        getManagers().getForeseeSurveyManager().setUpcomingRentals(upcomingRentals);
        mUpcomingRentals.setValue(upcomingRentals);
    }

    public void setRequestedReservation(EHIReservation requestedReservation) {
        mRequestedReservation.setValue(requestedReservation);
    }

    public EHIReservation getRequestReservation() {
        return mRequestedReservation.getValue();
    }


    public boolean shouldShowWeekendSpecialModal() {
        return isWeekendSpecialAvailable()
                && getManagers().getLocalDataManager().shouldShowWeekendSpecialModal();
    }


    public void updateUserProfile() {
        final ProfileCollection profile = getManagers().getLoginManager().getProfileCollection();
        if (profile != null && profile.getProfile() != null) {
            final String individualId = profile.getProfile().getIndividualId();
            if (individualId == null) {
                return;
            }

            mUserProfileCollection.setValue(profile);
            showDashboardProgress();

            performRequest(new GetProfileRequest(individualId),
                    new IApiCallback<EHIProfileResponse>() {
                        @Override
                        public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                            decreaseDashboardProgress();
                            if (response.isSuccess()) {
                                final EHIProfileResponse profile = response.getData();
                                final ProfileCollection mergedProfile = getManagers().getLoginManager().smartUpdateProfileCollection(profile);
                                mUserProfileCollection.setValue(mergedProfile);
                            }
                        }
                    });
        } else {
            getManagers().getLoginManager().logOut();
            setUpEplusBanner();
        }
    }

    private void requestLocationDetails(List<EHITripSummary> sortedList, final boolean isActiveRental) {
        if (sortedList != null
                && sortedList.size() > 0) {
            final EHITripSummary trip = sortedList.get(0);
            final String id = isActiveRental && trip.getReturnLocation().getId() != null
                    ? trip.getReturnLocation().getId()
                    : trip.getPickupLocation().getId();
            if (isActiveRental) {
                mLocationDetailsRequestInitiated = true;
            }
            performRequest(new GetLocationByIdRequest(id), new IApiCallback<GetLocationDetailsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetLocationDetailsResponse> response) {
                    if (response.isSuccess()) {
                        EHILocation location = response.getData().getLocation();
                        mActiveRentalLocationDetails.setValue(location);
                        if (isActiveRental) {
                            updateRentalViewState();
                        } else {
                            mWayfindings.setValue(location.getWayfindings());
                        }

                        requestLocationDropHours(trip, id);
                    }
                }
            });
        }
    }

    private void requestLocationDropHours(final EHITripSummary tripSummary, final String locationId) {
        if (tripSummary.getReturnTime() == null) {
            return;
        }

        performRequest(new GetSolrHoursByLocationIdRequest(locationId, tripSummary.getReturnTime()),
                new IApiCallback<GetSolrHoursResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<GetSolrHoursResponse> response) {
                        if (response.isSuccess()) {
                            try {
                                final Map<String, EHISolrWorkingDayInfo> map = response.getData().getDaysInfo();
                                final EHISolrWorkingDayInfo returnWorkingDayInfo = new ArrayList<>(map.values()).get(0);
                                mIsCurrentRentalReturnInAfterHours.setValue(returnWorkingDayInfo.isAfterHoursAtTime(tripSummary.getReturnTime()));
                            } catch (NullPointerException | IndexOutOfBoundsException e) {
                                DLog.w(TAG, e);
                            }
                        }
                    }
                });
    }

    public boolean isCurrentRentalReturnInAfterHours() {
        return mIsCurrentRentalReturnInAfterHours.getValue();
    }

    public List<EHIWayfindingStep> getWayfindings() {
        return mWayfindings.getValue();
    }

    private void getRandomImage() {
        TypedArray dashboardImages = getResources().obtainTypedArray(R.array.dashboard_images);
        TypedArray dashboardImagesStrings = getResources().obtainTypedArray(R.array.dashboard_images_strings);
        int randomDashImage = (int) (Math.random() * dashboardImages.length());
        int image = dashboardImages.getResourceId(randomDashImage, R.drawable.dash_01_france);
        int imageCaption = dashboardImagesStrings.getResourceId(randomDashImage, R.string.dashboard_image_martinique_title);
        dashboardImageViewState.setImageResource(image);
        dashboardImageCaptionViewState.setText(getResources().getString(imageCaption));
        dashboardImages.recycle();
        dashboardImagesStrings.recycle();
    }

    public boolean isCurrentRentalsRequestInProgress() {
        return mCurrentRentalsRequestInProgress;
    }

    public boolean isUpcomingRentalsInProgress() {
        return mUpcomingRentalsInProgress;
    }

    public EHITripSummary getDeterminedTripSummary() {
        return getCurrentRentals() != null && getCurrentRentals().size() != 0
                ? getCurrentRentals().get(0) : null;
    }

    public String getScreenState() {
        return mScreenState;
    }

    public void setScreenState(String screenState) {
        mScreenState = screenState;
    }

    public Map<String, String> getAnalyticsMap() {
        return mAnalyticsMap;
    }

    public EHILocation getActiveRentalLocationDetails() {
        return mActiveRentalLocationDetails.getValue();
    }

    public void enableNotifications(boolean enable) {
        getManagers().getSettingsManager()
                .setPickupNotificationTime(enable ? EHINotification.NotificationTime.TWO_HOURS_BEFORE
                        : EHINotification.NotificationTime.OFF);
        getManagers().getSettingsManager()
                .setReturnNotificationTime(enable ? EHINotification.NotificationTime.TWO_HOURS_BEFORE
                        : EHINotification.NotificationTime.OFF);
    }

    public void notificationPromptConfirmClicked() {
        trackNotificationClick(EHIAnalytics.Action.ACTION_ENABLED);
        notificationPromptViewState.setVisibility(View.GONE);
        setEnterpriseRentalAssistantEnabled(true);
        enableNotifications(true);
        getManagers().getLocalDataManager().setShouldShowGeofenceNotificationIntro(false);
        updateRentalViewState();
    }

    public void notificationPromptDenyClicked() {
        trackNotificationClick(EHIAnalytics.Action.ACTION_NOT_NOW);
        notificationPromptViewState.setVisibility(View.GONE);
        setEnterpriseRentalAssistantEnabled(false);
        enableNotifications(false);
        getManagers().getLocalDataManager().setShouldShowGeofenceNotificationIntro(false);
        updateRentalViewState();
    }

    public void onAnimationEnd() {
        if (!mAnimationHasEnded.getValue()) {
            mAnimationHasEnded.setValue(true);
        }
    }

    public boolean hasAnimationEnded() {
        return mAnimationHasEnded.getValue();
    }

    public boolean hasCountryListLoaded() {
        return mCountryListHasLoaded.getValue();
    }

    public void updateClearRecentActivitiesButtonVisibility() {
        int size = 0;

        if (getAbandonedReservations() != null) {
            size = getAbandonedReservations().size();
        }

        if (getRecentReservation() != null) {
            size += getRecentReservation().size();
        }

        // note: don't count favorites

        clearRecentActivityButtonViewState.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
    }

    public void setSurveyDialogShowed() {
        mShowSurveyDialog.setValue(false);
    }

    public boolean shouldShowSurveyDialog() {
        return mShowSurveyDialog.getValue();
    }

    public void checkIfIsElegibleToTrack() {
        getManagers().getForeseeSurveyManager().checkForSurvey();
    }

    public boolean needToCheckElegibilityToSurvey() {
        return !mShowSurveyDialog.getRawValue();
    }

    public void trackScreenChange() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, SCREEN_NAME)
                .state(getScreenState())
                .addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions())
                .addDictionary(getAnalyticsMap())
                .tagScreen()
                .tagEvent();
    }

    private void trackNotificationClick(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, SCREEN_NAME)
                .state(getScreenState())
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.dashboard(getScreenState(), null))
                .tagScreen()
                .tagEvent();
    }
}
