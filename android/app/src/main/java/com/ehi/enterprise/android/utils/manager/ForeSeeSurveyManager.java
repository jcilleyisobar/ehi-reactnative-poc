package com.ehi.enterprise.android.utils.manager;

import android.app.Application;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.LocaleUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.foresee.sdk.ForeSee;
import com.foresee.sdk.common.configuration.MeasureConfiguration;
import com.foresee.sdk.cxMeasure.tracker.listeners.CustomContactInviteListener;

import java.util.List;

public class ForeSeeSurveyManager {

    private static final String SURVEY_DEBUG_TAG = "IsobarTest";

    private static ForeSeeSurveyManager foreSeeSurveyManager;
    private List<EHITripSummary> mCurrentRentals;
    private List<EHITripSummary> mUpcomingRentals;

    public ForeSeeSurveyManager() {
    }

    public static ForeSeeSurveyManager getInstance() {
        if (foreSeeSurveyManager == null) {
            foreSeeSurveyManager = new ForeSeeSurveyManager();
        }
        return foreSeeSurveyManager;
    }

    public void initialize(Application application) {
        ForeSee.start(application);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")) {
            ForeSee.setDebugLogEnabled(true);
        }
    }

    public void checkForSurvey() {
        if (LocaleUtils.isEnglishLanguageDefault()) {
            ForeSee.checkIfEligibleForSurvey();
        }
    }

    public void forceDebugTrack() {
        ForeSee.setSkipPoolingCheck(true);
        ForeSee.resetState();
    }

    public void denyInvite() {
        ForeSee.customInviteDeclined();
    }

    public void setInviteListener(final InviteListener listener) {
        ForeSee.setInviteListener(new CustomContactInviteListener() {
            @Override
            public void showInvite(MeasureConfiguration measureConfiguration) {
                listener.onInviteShow();
            }

            @Override
            public void onContactFormatError() {
                listener.onError();
            }

            @Override
            public void onContactMissing() {
                listener.onError();
            }

            @Override
            public void onInviteCancelledWithNetworkError() {
                listener.onError();
            }

            @Override
            public void onInviteCompleteWithAccept() {
                listener.onInviteAccept();
            }

            @Override
            public void onInviteCompleteWithDecline() {
            }

            @Override
            public void onInviteNotShownWithNetworkError(MeasureConfiguration measureConfiguration) {
                listener.onError();
            }

            @Override
            public void onInviteNotShownWithEligibilityFailed(MeasureConfiguration measureConfiguration) {
                listener.onError();
            }

            @Override
            public void onInviteNotShownWithSamplingFailed(MeasureConfiguration measureConfiguration) {
                listener.onError();
            }
        });
    }

    private void acceptInvite() {
        ForeSee.customInviteAccepted();
    }

    public void sendSurvey(String contactInfo) {
        ForeSee.setContactDetails(contactInfo);
        addCppParameters();
        acceptInvite();
    }


////populated if state is current or upcoming (user have current or upcoming rental)

//    cpp[rental_length]=2

    private void addCppParameters() {
        //debug tag
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            ForeSee.addCPPValue(SURVEY_DEBUG_TAG, "true");
        } else {
            ForeSee.addCPPValue(SURVEY_DEBUG_TAG, "false");
        }
        //profile release cpps
        boolean isLoggedIn = LoginManager.getInstance().isLoggedIn();
        if (isLoggedIn) {
            final ProfileCollection profileCollection = LoginManager.getInstance().getProfileCollection();

            if (profileCollection != null && profileCollection.getBasicProfile() != null) {
                if (profileCollection.getBasicProfile().getLoyaltyData() != null) {
                    ForeSee.addCPPValue("loyalty_id", profileCollection.getBasicProfile().getLoyaltyData().getLoyaltyNumber());
                    ForeSee.addCPPValue("loyalty_tier", profileCollection.getBasicProfile().getLoyaltyData().getLoyaltyTier());
                    ForeSee.addCPPValue("current_points_balance", profileCollection.getBasicProfile().getLoyaltyData().getFormattedPointsToDate());
                }
                if (profileCollection.getProfile().getCorporateAccount()!= null) {
                    ForeSee.addCPPValue("customer_attached_cid", profileCollection.getProfile().getCorporateAccount().getContractNumber());
                }
            }
        }
        //rentals related cpps
        String rentalState = "none";
        EHITripSummary summary = null;
        if (!ListUtils.isEmpty(mCurrentRentals)) {
            summary = mCurrentRentals.get(0);
            rentalState = "current";

        } else if (!ListUtils.isEmpty(mUpcomingRentals)) {
            summary = mUpcomingRentals.get(0);
            rentalState = "upcoming";
        }
        ForeSee.addCPPValue("rental_state", rentalState);
        if (summary != null) {
            if (summary.getPickupLocation() != null
                    && summary.getPickupLocation().isAirport()) {
                ForeSee.addCPPValue("location_type", "airport");
            } else {
                ForeSee.addCPPValue("location_type", "home_city");
            }
            ForeSee.addCPPValue("rental_length", String.valueOf(
                    EHIAnalyticsDictionaryUtils.lengthOfRental(
                            summary.getPickupTime(),
                            summary.getReturnTime())));
        }
    }


    public void setCurrentRentals(List<EHITripSummary> currentRentals) {
        mCurrentRentals = currentRentals;
    }

    public void setUpcomingRentals(List<EHITripSummary> upcomingRentals) {
        mUpcomingRentals = upcomingRentals;
    }

    public interface InviteListener {
        void onInviteShow();

        void onInviteAccept();

        void onError();
    }
}
