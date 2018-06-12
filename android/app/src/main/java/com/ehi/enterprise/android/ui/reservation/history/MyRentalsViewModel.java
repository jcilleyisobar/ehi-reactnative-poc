package com.ehi.enterprise.android.ui.reservation.history;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetCurrentTripsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetPastTripsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetRetrieveReservationRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetUpcomingTripsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetTripsResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetUpcomingTripsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class MyRentalsViewModel extends ManagersAccessViewModel {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPCOMING, PAST})
    public @interface ViewState {
    }

    public static final int UPCOMING = 0;
    public static final int PAST = 1;
    public static final int RECORD_COUNT = 5;

    ReactorVar<EHIReservation> mRequestedReservation = new ReactorVar<>();
    ReactorVar<List<EHITripSummary>> mUpcomingRentals = new ReactorVar<>();
    ReactorVar<List<EHITripSummary>> mPastTripSummaries = new ReactorVar<>();
    ReactorVar<List<EHITripSummary>> mCurrentRentals = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorResponseWrapper = new ReactorVar<>();
    ReactorVar<Integer> mViewState = new ReactorVar<>(UPCOMING);

    private boolean mMoreRecordsAvailable;
    private int mStartRecordNumber = 1;

    private Comparator<EHITripSummary> mTripSummaryComparator = new Comparator<EHITripSummary>() {
        @Override
        public int compare(EHITripSummary lhs, EHITripSummary rhs) {
            return lhs.getPickupTime().before(rhs.getPickupTime()) ? -1 : 1;
        }
    };

    public Date getEndDate() {
        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 6);
        return endDate.getTime();
    }

    public void requestCurrentRentals(@NonNull String userId) {
        if (isUserLoggedIn() || isLoggedIntoEmeraldClub()) {
            performRequest(new GetCurrentTripsRequest(userId), new IApiCallback<GetTripsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetTripsResponse> response) {
                    if (response.isSuccess()) {
                        GetTripsResponse getTripsResponse = response.getData();
                        setCurrentRentals(getTripsResponse.getTripSummariesList());
                    } else {
                        setErrorResponse(response);
                    }
                }
            });
        } else {
            setCurrentRentals(null);
        }
    }

    void requestPastRentals(@NonNull String userid, @NonNull Date fromDate, @NonNull Date toDate) {
        showProgress(true);
        if (getPastTripSummaries() == null) {
            performRequest(new GetPastTripsRequest(userid, fromDate, toDate), new IApiCallback<GetTripsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetTripsResponse> response) {
                    if (response.isSuccess()) {
                        GetTripsResponse getTripsResponse = response.getData();
                        setPastTripSummaries(getTripsResponse.getTripSummariesList());
                        showProgress(false);
                    } else {
                        showProgress(false);
                        setErrorResponse(response);
                    }
                }
            });
        }
    }

    void requestUpcomingRentals(@NonNull String userId, @NonNull Date searchEndDate) {
        showProgress(true);
        if (getUpcomingRentals() == null) {
            performRequest(new GetUpcomingTripsRequest(userId, searchEndDate, true, mStartRecordNumber, RECORD_COUNT), new IApiCallback<GetUpcomingTripsResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetUpcomingTripsResponse> response) {
                    if (response.isSuccess()) {
                        GetUpcomingTripsResponse getTripsResponse = response.getData();
                        setUpcomingRentals(getTripsResponse.getFullSortedTripList());
                        setMoreRecordsAvailable(getTripsResponse.isMoreRecordsAvailable());
                        mStartRecordNumber += RECORD_COUNT;
                        showProgress(false);
                    } else {
                        showProgress(false);
                        setErrorResponse(response);
                    }
                }
            });
        }
    }

    void requestLoadMoreUpcomingRentals(@NonNull String userId, @NonNull Date searchEndDate) {
        showProgress(true);
        performRequest(new GetUpcomingTripsRequest(userId, searchEndDate, false, mStartRecordNumber, RECORD_COUNT), new IApiCallback<GetUpcomingTripsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetUpcomingTripsResponse> response) {
                if (response.isSuccess()) {
                    GetUpcomingTripsResponse tripsResponse = response.getData();
                    final List<EHITripSummary> upcomingTripSummaries = getUpcomingRentals();
                    upcomingTripSummaries.addAll(tripsResponse.getUpcomingReservations());

                    Collections.sort(upcomingTripSummaries, mTripSummaryComparator);
                    setUpcomingRentals(upcomingTripSummaries);
                    setMoreRecordsAvailable(tripsResponse.isMoreRecordsAvailable());
                    mStartRecordNumber += RECORD_COUNT;
                    showProgress(false);
                } else {
                    showProgress(false);
                    setErrorResponse(response);
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
                        if (response.isSuccess()) {
                            setRequestedReservation(response.getData());
                            showProgress(false);
                        } else {
                            showProgress(false);
                            setErrorResponse(response);
                        }
                    }
                });
    }

    void setErrorResponse(ResponseWrapper response) {
        mErrorResponseWrapper.setValue(response);
    }

    ResponseWrapper getErrorResponse() {
        return mErrorResponseWrapper.getValue();
    }

    List<EHITripSummary> getUpcomingRentals() {
        return mUpcomingRentals.getValue();
    }

    void setUpcomingRentals(List<EHITripSummary> tripSummaries) {
        mUpcomingRentals.setValue(tripSummaries);
    }

    List<EHITripSummary> getPastTripSummaries() {
        return mPastTripSummaries.getValue();
    }

    void setPastTripSummaries(List<EHITripSummary> pastTripSummaries) {
        mPastTripSummaries.setValue(pastTripSummaries);
    }

    @ViewState
    int getViewState() {
        //noinspection ResourceType
        return mViewState.getValue();
    }

    public void setCurrentRentals(List<EHITripSummary> tripSummaries) {
        mCurrentRentals.setValue(tripSummaries);
    }

    public List<EHITripSummary> getCurrentRentals() {
        return mCurrentRentals.getValue();
    }

    void setViewState(@ViewState int viewState) {
        mViewState.setValue(viewState);
    }

    public String getLoyaltyNumber() {
        ProfileCollection profile;
        if (isUserLoggedIn()) {
            profile = getUserProfileCollection();
        } else if (isLoggedIntoEmeraldClub()) {
            profile = getEmeraldClubProfile();
        } else {
            return null;
        }

        EHILoyaltyData ehiLoyaltyData = profile.getBasicProfile().getLoyaltyData();
        if (ehiLoyaltyData == null) {
            return null;
        }

        return ehiLoyaltyData.getLoyaltyNumber();
    }

    public String getUserAuthToken() {
        return getManagers().getLoginManager().getUserAuthToken();
    }

    public boolean areMoreRecordsAvailable() {
        return mMoreRecordsAvailable;
    }

    public void setMoreRecordsAvailable(boolean moreRecordsAvailable) {
        mMoreRecordsAvailable = moreRecordsAvailable;
    }

    public void setRequestedReservation(EHIReservation requestedReservation) {
        mRequestedReservation.setValue(requestedReservation);
    }

    public EHIReservation getRequestReservation() {
        return mRequestedReservation.getValue();
    }

}

