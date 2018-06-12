package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.Nullable;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHI3DSData;
import com.ehi.enterprise.android.models.profile.EHIEmailPreference;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPreference;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIPriceDifferences;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.request_params.profile.PutProfileParams;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.profile.Get3DSDataRequest;
import com.ehi.enterprise.android.network.requests.profile.PutProfileRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetMoreTaxesInformationRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetUpgradeDetailsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostCommitRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectCarClassRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectUpgradeRequest;
import com.ehi.enterprise.android.network.requests.reservation.PutAssociateProfileRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.GetUpgradeDetailsModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostAdditionalInfoModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostCommitModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostPaymentMethodModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostRenterInfoModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectCarClassModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectUpgradeModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.profile.Get3DSDataResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetMoreTaxesInformationResponse;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.BillingAccountView;
import com.ehi.enterprise.android.ui.reservation.widget.ReactorBookRentalButtonState;
import com.ehi.enterprise.android.ui.reservation.widget.ReviewCardNoInfoView;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Collections;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.REDEMPTION;

@AutoUnbindAll
public class ReviewViewModel extends ReservationViewModel implements BillingAccountView.IBillingCallBack, ReviewCardNoInfoView.CreditCardViewClickListener {

    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorViewState modifyPrePayBanner = new ReactorViewState();
    final ReactorBookRentalButtonState continueButton = new ReactorBookRentalButtonState();
    final ReactorVar<Boolean> shouldShowPrepayTermsAndConditions = new ReactorVar<>(true);
    final ReactorVar<EHIReservation> mCommitReservationResult = new ReactorVar<>();
    final ReactorVar<Boolean> mRequiresTravelPurpose = new ReactorVar<>(false);
    final ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    final ReactorVar<EHIAirlineInformation> mAirlineInformation = new ReactorVar<>();
    final ReactorVar<Boolean> isUpdated = new ReactorVar<>(false);
    final ReactorVar<String> toastMessage = new ReactorVar<>();
    final ReactorVar<String> mBillingType = new ReactorVar<>();
    final ReactorVar<String> mBillingMethod = new ReactorVar<>();
    final ReactorVar<String> mTaxesAndFeesMoreInformation = new ReactorVar<>();
    final ReactorVar<String> mPrepayTermsAndConditions = new ReactorVar<>();
    final ReactorVar<Integer> successfulUpgradeMessage = new ReactorVar<>(View.GONE);
    final ReactorVar<EHI3DSData> mEHI3DSData = new ReactorVar<>();
    final ReactorVar<Boolean> mCreditCardAddedSuccessfuly = new ReactorVar<>(false);
    private final ReactorVar<Boolean> mShouldShowCommitAnimation = new ReactorVar<>(false);

    private String mTripPurpose;

    private List<String> mPaymentIds;
    private String mBillingNumber;
    private boolean mRedemptionWasCanceled;
    private boolean mUpgradeRequestedOnce = false;
    private boolean mModifyAdditionalInfoSuccess = false;
    private boolean mModifyFlightInfoSuccess = false;
    private boolean mModifyPaymentInfoSuccess = false;
    private boolean mCollectedNewPaymentCardInModify = false;
    private ReviewCardNoInfoView.CreditCardViewClickListener mCreditCardViewClickListener;
    private EHIDriverInfo mDriverInfo;
    private boolean mIsLoginAfterStart;

    private ReactorVar<ReservationFlowListener.PayState> mPayState = new ReactorVar<ReservationFlowListener.PayState>() {
        @Override
        public void setValue(final ReservationFlowListener.PayState value) {
            super.setValue(value);
            updateContinueButton();
        }
    };

    private List<EHIAdditionalInformation> mAdditionalInformation;
    private boolean mShowTermsAndConditions = true;

    @Override
    protected void onEHIReservationUpdate() {
        updateUpgradeViewState();
        syncAirlineInfo();
    }

    public void check3DSAndCommitReservation() {
        // clean promotion applied
        getManagers().getReservationManager().setWeekendSpecial(false);

        // only check for 3ds validation if prepay is selected
        if (mPayState.getRawValue() == ReservationFlowListener.PayState.PREPAY
                && !isNorthAmerica()
                && (!isModify() || isModify() && getReservationObject().shouldCollectNewPaymentCardInModify())) {
            performRequest(new Get3DSDataRequest(getRawReservationObject().getResSessionId()),
                    new IApiCallback<Get3DSDataResponse>() {
                        @Override
                        public void handleResponse(ResponseWrapper<Get3DSDataResponse> response) {
                            showProgress(false);
                            if (response.isSuccess()) {
                                EHI3DSData threeDSData = response.getData().get3DSData();

                                if (threeDSData.isPerform3DS()) {
                                    mEHI3DSData.setValue(threeDSData);
                                } else {
                                    // no 3ds validation necessary
                                    threeDSAuthorizationCompleted(null);
                                }
                            } else {
                                setErrorWrapper(response);
                            }
                        }
                    });
        } else {
            commitReservation();
        }
    }

    public void commitReservation() {

        mShouldShowCommitAnimation.setValue(true);

        // reset flags in case something goes wrong with one of the service calls
        mModifyAdditionalInfoSuccess = false;
        mModifyFlightInfoSuccess = false;
        mModifyPaymentInfoSuccess = false;

        final String reservationSessionId = getRawReservationObject().getResSessionId();

        if (mIsLoginAfterStart) {
            // associate account
            String individualId =
                    isLoggedIntoEmeraldClub() ?
                            getEmeraldClubProfile().getProfile().getIndividualId() :
                            getManagers().getLoginManager().getProfileCollection().getProfile().getIndividualId();

            performRequest(new PutAssociateProfileRequest(
                            reservationSessionId,
                            individualId, isLoggedIntoEmeraldClub()), new IApiCallback<EHIReservation>() {
                        @Override
                        public void handleResponse(ResponseWrapper<EHIReservation> response) {
                            if (response.isSuccess()) {
                                mIsLoginAfterStart = false;
                                commitReservation();
                            } else {
                                setErrorWrapper(response);
                            }
                        }
                    }
            );
            return;
        }

        final EHIDriverInfo driverInfo = getLocalDriverInfo();
        final List<EHIAdditionalInformation> additionalInformation = getAdditionalInformation();
        final String tripPurpose = getTripPurpose();
        if (!isModify()) {
            performRequest(new PostCommitRequest(reservationSessionId,
                    driverInfo,
                    additionalInformation,
                    mPaymentIds,
                    mBillingNumber,
                    mBillingType.getValue(),
                    mAirlineInformation.getRawValue(),
                    tripPurpose,
                    getManagers().getReservationManager().getPARes()), new IApiCallback<EHIReservation>() {

                @Override
                public void handleResponse(ResponseWrapper<EHIReservation> response) {
                    if (response.isSuccess()) {
                        getManagers().getReservationManager().clearSelectedAirlineInformation();
                        mCommitReservationResult.setValue(response.getData());
                        if (getManagers().getSettingsManager().isSearchHistoryEnabled()) {
                            getManagers().getLocationManager().commitRecentReservation();
                        }

                        getManagers().getReservationManager().addOrUpdateReservation(response.getData());

                        reservationCommittedSuccessfully();
                    } else {
                        setErrorWrapper(response);
                    }
                }
            });
        } else {
            //flight info
            if (mAirlineInformation.getRawValue() != null
                    && getManagers().getReservationManager().getSelectedAirlineInformation() != null
                    && getRawReservationObject().getDriverInfo() != null) {
                performRequest(new PostRenterInfoModifyRequest(reservationSessionId, getRawReservationObject().getDriverInfo(), mAirlineInformation.getRawValue()),
                        new IApiCallback<EHIReservation>() {
                            @Override
                            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                                if (response.isSuccess()) {
                                    commitModifyReservation(reservationSessionId, null, null, true);
                                } else {
                                    setErrorWrapper(response);
                                }
                            }
                        });
            } else {
                commitModifyReservation(reservationSessionId, null, null, true);
            }
            //additional info
            if (additionalInformation != null
                    && !additionalInformation.isEmpty()) {
                performRequest(new PostAdditionalInfoModifyRequest(reservationSessionId, additionalInformation), new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        if (response.isSuccess()) {
                            commitModifyReservation(reservationSessionId, true, null, null);
                        } else {
                            setErrorWrapper(response);
                        }
                    }
                });
            } else {
                commitModifyReservation(reservationSessionId, true, null, null);
            }
            //payment info
            if (mPaymentIds != null
                    && mBillingNumber != null
                    && mBillingType.getRawValue() != null) {
                performRequest(new PostPaymentMethodModifyRequest(reservationSessionId, mPaymentIds, mBillingNumber, mBillingType.getRawValue()),
                        new IApiCallback<EHIReservation>() {
                            @Override
                            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                                if (response.isSuccess()) {
                                    commitModifyReservation(reservationSessionId, null, true, null);
                                } else {
                                    setErrorWrapper(response);
                                }
                            }
                        });
            } else {
                commitModifyReservation(reservationSessionId, null, true, null);
            }
        }
    }

    private void commitModifyReservation(String reservationSessionId, Boolean additionalInfoSuccess, Boolean paymentInfoSuccess, Boolean flightInfoSuccess) {
        if (additionalInfoSuccess != null) {
            mModifyAdditionalInfoSuccess = additionalInfoSuccess;
        }
        if (paymentInfoSuccess != null) {
            mModifyPaymentInfoSuccess = paymentInfoSuccess;
        }
        if (flightInfoSuccess != null) {
            mModifyFlightInfoSuccess = flightInfoSuccess;
        }
        if (mModifyAdditionalInfoSuccess
                && mModifyPaymentInfoSuccess
                && mModifyFlightInfoSuccess) {

            String mPaymentId = null;
            if (!ListUtils.isEmpty(mPaymentIds)) {
                mPaymentId = mPaymentIds.get(0);
            }

            performRequest(new PostCommitModifyRequest(reservationSessionId,
                    mPaymentId,
                    getManagers().getReservationManager().getPARes()), new IApiCallback<EHIReservation>() {
                @Override
                public void handleResponse(ResponseWrapper<EHIReservation> response) {
                    if (response.isSuccess()) {
                        getManagers().getReservationManager().clearSelectedAirlineInformation();
                        getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                        mCommitReservationResult.setValue(response.getData());
                        reservationCommittedSuccessfully();
                    } else {
                        setErrorWrapper(response);
                    }
                }
            });
        }
    }

    private void reservationCommittedSuccessfully() {
        updatePreferencesIfNeeded();
    }

    private void updatePreferencesIfNeeded() {
        if (getUserProfileCollection() == null
                || getUserProfileCollection().getProfile() == null
                || getUserProfileCollection().getPreference() == null
                || getUserProfileCollection().getPreference().getEmailPreference() == null
                || getUserProfileCollection().getBasicProfile() == null
                || getUserProfileCollection().getBasicProfile().getLoyaltyData() == null) {
            return;
        }

        final EHIPreference preference = getUserProfileCollection().getPreference();
        final EHIEmailPreference emailPreference = preference.getEmailPreference();
        final EHILoyaltyData ehiLoyaltyData = getUserProfileCollection().getBasicProfile().getLoyaltyData();

        if (isUserLoggedIn()) {
            final boolean requestedEmailNotifications = getLocalDriverInfo() != null && getLocalDriverInfo().hasRequestedEmailPromotions();
            final boolean specialOffers = emailPreference.isSpecialOffers();
            final boolean shouldUpdate = requestedEmailNotifications && !specialOffers;
            if (shouldUpdate) {
                preference.getEmailPreference().setSpecialOffers(true, true);

                PutProfileParams profileParams = new PutProfileParams.Builder()
                        .setLoyaltyNumber(ehiLoyaltyData.getLoyaltyNumber())
                        .setPreference(preference)
                        .build();

                performRequest(new PutProfileRequest(getUserProfileCollection().getProfile().getIndividualId(), profileParams),
                        new IApiCallback<EHIProfileResponse>() {
                            @Override
                            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                                if (response.isSuccess()) {
                                    getManagers().getLoginManager().setProfile(response.getData());
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (isModify()) {
            title.setValue(R.string.reservation_modify_review_navigation_title);
        } else {
            title.setValue(R.string.reservation_review_navigation_title);
        }
        updateUpgradeViewState();
        syncAirlineInfo();

        mEHI3DSData.setValue(null);
        mUpgradeRequestedOnce = false;

        if (getReservationObject() == null) {
            populateReservationObject();
        }
        if (mDriverInfo == null || isModify()) {
            mDriverInfo = getReservationObject() != null && getReservationObject().getDriverInfo() != null
                    ? getReservationObject().getDriverInfo()
                    : getLocalDriverInfo();
        }
    }

    private void updateUpgradeViewState() {
        //TODO validate this!
        if (getManagers().getReservationManager().getCarUpgradeSelection() != null && !getManagers().getReservationManager().vehicleUpgradePrompted()) {
            toastMessage.setValue(getResources().getString(R.string.review_reservation_upgrade_success));
            getManagers().getReservationManager().vehicleUpgradeWasPrompted(true);
        }
//		todo remove if decision is kept to not have upgrade header
//		if(getManagers().getReservationManager().getCarUpgradeSelection() != null){
//			successfulUpgradeMessage.setValue(View.VISIBLE);
//		}
//		else{
//			successfulUpgradeMessage.setValue(View.GONE);
//		}
    }

    @Override
    public void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, @CommitRequestParams.BillingMethods String method) {
        mBillingNumber = billingNumber;
        setBillingType(billingType);
        //only erase payment ids when user is in corporate flow and is not paying with billing credit card
        if (method != null && method.equalsIgnoreCase(CommitRequestParams.BILLING_ACCOUNT) && paymentIds == null) {
            mPaymentIds = null;
        } else if (paymentIds != null) {
            mPaymentIds = paymentIds;
        }
        mBillingMethod.setValue(method);
        updateContinueButton();
    }

    public List<String> getPaymentIds() {
        return mPaymentIds;
    }

    public String getBillingType() {
        return mBillingType.getValue();
    }

    public void setBillingType(String billingType) {
        mBillingType.setValue(billingType);
    }

    public EHICarClassDetails getCarClassDetailsWithPoints() {
        final EHIReservation ehiReservation = getRawReservationObject();
        final EHICarClassDetails details = ehiReservation.getCarClassDetails();
        if (details.getEplusPointsUsed() == 0) {
            details.setEplusPointsUsed(ehiReservation.getEplusPointsUsed());
        }
        if (details.getRedemptionDayCount() == 0) {
            details.setRedemptionDayCount(ehiReservation.getRedemptionDayCount());
        }
        return details;
    }

    @Nullable
    public EHIReservation getCommitReservationResult() {
        return mCommitReservationResult.getValue();
    }

    public EHIDriverInfo getLocalDriverInfo() {
        return getManagers().getReservationManager().getDriverInfo();
    }

    public EHIDriverInfo getDriverInfo() {
        return mDriverInfo;
    }

    public void setDriverInfo(EHIDriverInfo driverInfo) {
        mDriverInfo = driverInfo;
    }

    public boolean getRequiresTravelPurpose() {
        return mRequiresTravelPurpose.getValue();
    }

    public void setRequiresTravelPurpose(boolean requiresTravelPurpose) {
        mRequiresTravelPurpose.setValue(requiresTravelPurpose);
    }


    public String getTripPurpose() {
        return mTripPurpose;
    }

    public void setTripPurpose(String tripPurpose) {
        mTripPurpose = tripPurpose;
    }

    public List<EHIAdditionalInformation> getAdditionalInformation() {
        if (mAdditionalInformation == null) {
            mAdditionalInformation = getRawReservationObject().getAdditionalInformation();
        }
        return mAdditionalInformation;
    }

    public void setAdditionalInformation(List<EHIAdditionalInformation> additionalInformation) {
        mAdditionalInformation = additionalInformation;
    }

    @Nullable
    public ResponseWrapper getErrorWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    public ProfileCollection getUserProfileCollection() {
        ProfileCollection profileCollection = getManagers().getLoginManager().getProfileCollection();
        return profileCollection == null ? getManagers().getReservationManager().getEmeraldClubProfile() : profileCollection;
    }

    public EHIAirlineDetails getCurrentAirlineDetail() {
        if (mAirlineInformation.getValue() != null) {
            final EHIReservation ehiReservation = getRawReservationObject();
            if (!ListUtils.isEmpty(ehiReservation.getPickupLocation().getEHIAirlineDetails())) {
                final int flightIndex = getFlightIndex(mAirlineInformation.getRawValue());
                if (flightIndex != -1) {
                    return ehiReservation.getPickupLocation().getEHIAirlineDetails().get(flightIndex);
                }
            }
        }
        return null;

    }

    public String getFlightNumber() {
        if (mAirlineInformation.getRawValue() != null) {
            return mAirlineInformation.getRawValue().getFlightNumber();
        }
        return null;
    }

    private void syncAirlineInfo() {
        if (getManagers().getReservationManager().getSelectedAirlineInformation() != null) {
            //that mean airline was previously selected or changed so manager has latest info
            mAirlineInformation.setValue(getManagers().getReservationManager().getSelectedAirlineInformation());
        } else if (getRawReservationObject() != null && getRawReservationObject().getAirlineInformation() != null) {
            //if have nothing on manager then reservation object contain up to date info
            mAirlineInformation.setValue(getRawReservationObject().getAirlineInformation());
        } else {
            mAirlineInformation.setValue(null);
        }
    }

    public List<EHIAirlineDetails> getAirlineDetailsList() {
        return getRawReservationObject().getPickupLocation().getEHIAirlineDetails();
    }

    public boolean isTripPurposePreRate() {
        return getManagers().getReservationManager().isTripPurposePreRate();
    }

    public void requestLearnMore() {
        showProgress(true);
        performRequest(new GetMoreTaxesInformationRequest(), new IApiCallback<GetMoreTaxesInformationResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetMoreTaxesInformationResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    setTaxesAndFeesMoreInformation(response.getData().getContent());
                } else {
                    setErrorWrapper(response);
                }
            }
        });
    }

    public void requestPrepaymentPolicy() {
        showProgress(true);
        performRequest(new GetMorePrepayTermsConditionsRequest(getManagers().getLocalDataManager().getPreferredCountryCode()), new IApiCallback<GetMorePrepayTermsConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetMorePrepayTermsConditionsResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    setPrepayTermsAndConditions(response.getData().getContent());
                } else {
                    setErrorWrapper(response);
                }
            }
        });
    }

    public String getTaxesAndFeesMoreInformation() {
        return mTaxesAndFeesMoreInformation.getValue();
    }

    public void setTaxesAndFeesMoreInformation(String taxesAndFeesMoreInformation) {
        mTaxesAndFeesMoreInformation.setValue(taxesAndFeesMoreInformation);
    }

    public String getPrepayTermsAndConditions() {
        return mPrepayTermsAndConditions.getValue();
    }

    public void setPrepayTermsAndConditions(String prepayTermsAndConditions) {
        mPrepayTermsAndConditions.setValue(prepayTermsAndConditions);
    }

    public int getRedemptionPoints() {
        final EHIReservation ehiReservation = getRawReservationObject();
        int pointsUsed = ehiReservation.getCarClassDetails().getEplusPointsUsed();
        if (pointsUsed == 0) {
            pointsUsed = ehiReservation.getEplusPointsUsed();
        }
        return pointsUsed;
    }

    public int getRedemptionPointsRate() {
        return Float.valueOf(getRawReservationObject().getCarClassDetails().getRedemptionPoints()).intValue();
    }

    public int getRedemptionDayCount() {
        final EHIReservation ehiReservation = getRawReservationObject();
        int daysCount = ehiReservation.getCarClassDetails().getRedemptionDayCount();
        if (daysCount == 0) {
            daysCount = ehiReservation.getRedemptionDayCount();
        }
        return daysCount;
    }

    public boolean wasRedemptionCanceled() {
        return mRedemptionWasCanceled;
    }

    public void removeRedemptionDays() {
        showProgress(true);
        AbstractRequestProvider request;
        final EHIReservation ehiReservation = getRawReservationObject();
        if (isModify()) {
            request = new PostSelectCarClassModifyRequest(getManagers().getReservationManager().getCurrentModifyReservationId(),
                    ehiReservation.getCarClassDetails().getCode(),
                    0,
                    getPayState().equals(ReservationFlowListener.PayState.PREPAY));
        } else {
            request = new PostSelectCarClassRequest(getManagers().getReservationManager().getCurrentReservationId(),
                    ehiReservation.getCarClassDetails().getCode(),
                    true,
                    0);
        }
        performRequest(request,
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            mRedemptionWasCanceled = true;
                            if (isModify()) {
                                getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                            } else {
                                getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                            }
                            mPayState.setRawValue(ReservationFlowListener.PayState.PAY_LATER);
                            setReservationObject(response.getData());
                            updateContinueButton();
                        } else {
                            mErrorWrapper.setValue(response);
                        }
                    }
                });
    }

    public boolean shouldShowRedemption() {
        return isUserLoggedIn()
                && !isLoggedIntoEmeraldClub()
                && getRawReservationObject() != null;
    }


    public String getBillingNumber() {
        return mBillingNumber;
    }

    public String getSelectedPaymentName() {
        String billingAccountName = getBillingNumber();
        return EHITextUtils.isEmpty(billingAccountName) ? getAccountName() : billingAccountName;
    }

    public boolean isBillingAccountChosen() {
        return CommitRequestParams.BILLING_ACCOUNT.equals(mBillingMethod.getRawValue());
    }

    public boolean isPayAtPickupChosen() {
        return CommitRequestParams.PAY_AT_COUNTER.equals(mBillingMethod.getRawValue());
    }

    public void populateReservationObject() {
        setReservationObject(getOngoingReservation());
        updateContinueButton();
    }

    public boolean is3rdPartyEmailNotify() {
        return super.is3rdPartyEmailNotify(isModify());
    }

    public String getCorporateContractType() {
        return super.getCorporateContractType(isModify());
    }

    public int getFlightIndex(EHIAirlineInformation info) {
        if (info == null || info.getCode() == null) {
            return -1;
        }

        return BaseAppUtils.indexOf(
                info,
                getRawReservationObject().getPickupLocation().getEHIAirlineDetails(),
                new BaseAppUtils.CompareTwo<EHIAirlineInformation, EHIAirlineDetails>() {
                    @Override
                    public boolean equals(EHIAirlineInformation first, EHIAirlineDetails second) {
                        return first.getCode().equalsIgnoreCase(second.getCode());
                    }
                });
    }

    public void carUpgradeClicked(String carId) {
        showProgress(true);
        final EHIReservation ehiReservation = getRawReservationObject();
        AbstractRequestProvider selectUpgradeRequest;
        if (isModify()) {
            selectUpgradeRequest = new PostSelectUpgradeModifyRequest(ehiReservation.getResSessionId(), carId);
        } else {
            selectUpgradeRequest = new PostSelectUpgradeRequest(ehiReservation.getResSessionId(), carId);
        }

        performRequest(selectUpgradeRequest, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    getManagers().getReservationManager().setCarUpgradeSelection(ehiReservation.getUpgradeCarClassDetails().get(0));
                    setReservationObject(response.getData());
                    updateContinueButton();
                } else {
                    setError(response);
                }
            }
        });
    }

    public void setUpgradeAmount(List<EHICarClassDetails> upgradeReservation) {
        EHIPriceDifferences priceDifference = upgradeReservation.get(0).getUpgradePriceDifference(getRawReservationObject().isPrepaySelected());
        if (priceDifference != null) {
            String subPrice = String.valueOf(priceDifference.getDifferenceAmountView().getAmmount());
            getManagers().getReservationManager().setUpgradeAmount(subPrice);
        }
    }

    public boolean shouldShowUpgrade() {
        final EHIReservation ehiReservation = getRawReservationObject();

        boolean hideUpgradesInModify = isModify()
                && ehiReservation.getCorporateAccount() != null
                && !EHITextUtils.isEmpty(ehiReservation.getCorporateAccount().getContractName());

        boolean shouldShowCarUpgrades = getRedemptionDayCount() == 0
                && ehiReservation.isUpgradeVechiclePossible()
                && !hideUpgradesInModify;

        if (shouldShowCarUpgrades
                && ListUtils.isEmpty(ehiReservation.getUpgradeCarClassDetails())
                && !mUpgradeRequestedOnce) {

            fetchUpgradeDetails();
        }

        return shouldShowCarUpgrades;
    }

    public boolean isPrePayAvailable() {
        return getManagers().getReservationManager().getSelectedCarClass().isPrepayRateAvailable();
    }

    @Override
    public void removeCreditCard() {
        if (mCreditCardViewClickListener != null) {
            mCreditCardViewClickListener.removeCreditCard();
        }
        if (mPaymentIds != null && mPaymentIds.size() > 0) {
            mPaymentIds = null;
        }

        if (isModify()) {
            mCollectedNewPaymentCardInModify = false;
        }

        getManagers().getReservationManager().setPARes(null); // cleaning 3ds key

        if (mPayState.getRawValue() == ReservationFlowListener.PayState.PAY_LATER) {
            return;
        }
        if (!mCreditCardAddedSuccessfuly.getRawValue()) {
            return;
        }

        mCreditCardAddedSuccessfuly.setValue(false);
        updateContinueButton();
    }

    @Override
    public void addCreditCard() {
        if (mPayState.getRawValue() != ReservationFlowListener.PayState.PAY_LATER) {
            mCreditCardViewClickListener.addCreditCard();
            return;
        }

        showProgress(true);
        postCarClass(new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                if (response.isSuccess()) {
                    mCreditCardViewClickListener.addCreditCard();
                    updateContinueButton();
                }
            }
        });
    }

    public void postCarClass(final IApiCallback<EHIReservation> callback) {
        showProgress(true);

        final EHIReservation ehiReservation = getRawReservationObject();

        AbstractRequestProvider request;
        if (isModify()) {
            request = new PostSelectCarClassModifyRequest(getRawReservationObject().getResSessionId(),
                    ehiReservation.getCarClassDetails().getCode(),
                    0,
                    getPayState().equals(ReservationFlowListener.PayState.PREPAY));
        } else {
            request = new PostSelectCarClassRequest(getRawReservationObject().getResSessionId(),
                    ehiReservation.getCarClassDetails().getCode(),
                    false,
                    0,
                    getPayState().equals(ReservationFlowListener.PayState.PREPAY));
        }
        performRequest(request,
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            setReservationObject(response.getData());
                            shouldShowUpgrade();
                        } else {
                            mErrorWrapper.setValue(response);
                        }
                        if (callback != null) {
                            callback.handleResponse(response);
                        }
                    }
                });
    }

    public void fetchUpgradeDetails() {
        showProgress(true);

        if (isModify()) {
            performRequest(new GetUpgradeDetailsModifyRequest(getRawReservationObject().getResSessionId()), new IApiCallback<EHIReservation>() {
                @Override
                public void handleResponse(ResponseWrapper<EHIReservation> response) {
                    showProgress(false);
                    if (response.isSuccess()) {
                        mUpgradeRequestedOnce = true;
                        getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                        setReservationObject(response.getData());
                    }
                }
            });
        } else {
            performRequest(new GetUpgradeDetailsRequest(getRawReservationObject().getResSessionId()), new IApiCallback<EHIReservation>() {
                @Override
                public void handleResponse(ResponseWrapper<EHIReservation> response) {
                    showProgress(false);
                    if (response.isSuccess()) {
                        mUpgradeRequestedOnce = true;
                        getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                        setReservationObject(response.getData());
                    }
                }
            });
        }
    }

    @Override
    public void editCreditCard() {
        addCreditCard();
    }

    public void setAddCreditCardListener(ReviewCardNoInfoView.CreditCardViewClickListener creditCardViewClickListener) {
        mCreditCardViewClickListener = creditCardViewClickListener;
    }

    public void creditAddedSuccessfully(boolean enabled) {
        mCreditCardAddedSuccessfuly.setValue(enabled);
    }

    public void setPaymentReferenceId(String paymentReferenceId) {
        if (paymentReferenceId != null) {
            mPaymentIds = Collections.singletonList(paymentReferenceId);
        }
    }

    public EHIPaymentMethod getSelectedPaymentMethod() {
        if (isUserLoggedIn()
                && mPaymentIds != null
                && mPaymentIds.size() > 0
                && getManagers().getLoginManager().getProfileCollection().getPaymentProfile() != null) {
            String paymentReferenceId = mPaymentIds.get(0);
            for (EHIPaymentMethod method : getManagers().getLoginManager().getProfileCollection().getPaymentProfile().getCardPaymentMethods()) {
                if (method.getPaymentReferenceId().equals(paymentReferenceId)) {
                    return method;
                }
            }
        }
        return null;
    }

    public boolean isCreditCardAddedSuccessfuly() {
        return mCreditCardAddedSuccessfuly.getValue();
    }

    public void setRawPayState(ReservationFlowListener.PayState payState) {
        mPayState.setRawValue(payState);
    }

    public ReservationFlowListener.PayState getPayState() {
        return mPayState.getValue();
    }

    public void setPayState(ReservationFlowListener.PayState payState) {
        mPayState.setValue(payState);
    }

    public void updateContinueButton() {
        final EHIReservation ehiReservation = getRawReservationObject();
        if (ehiReservation == null
                || ehiReservation.getCarClassDetails() == null
                || ehiReservation.getCarClassDetails().getVehicleRates() == null
                || ehiReservation.getCarClassDetails().getVehicleRates().size() == 0) {
            return;
        }

        if (isPrePay()) {
            showPrepayContinueButton();
        } else {
            showNonPrepayContinueButton();
        }
    }

    private void showPrepayContinueButton() {
        final EHICarClassDetails carClassDetails = getRawReservationObject().getCarClassDetails();
        if (carClassDetails.getPrepayPriceSummary() != null
                && carClassDetails.getPrepayPriceSummary().getEstimatedTotalView() != null) {
            continueButton.setPrice(carClassDetails.getPrepayPriceSummary().getEstimatedTotalView().getFormattedPrice(true));
            continueButton.setEnabled(true);
        }

        if (isModify()) {
            continueButton.setVisibility(isUpdated.getRawValue() ? ReactorViewState.VISIBLE : ReactorViewState.GONE);
        }

        if (shouldShowTermsAndConditions()) {
            EHIPaymentMethod preferred = getUserProfileCollection().getPaymentProfile().getPreferred();
            if (preferred != null) {
                setPaymentReferenceId(preferred.getPaymentReferenceId());
                shouldShowPrepayTermsAndConditions.setValue(true);
                continueButton.setEnabled(false);
                creditAddedSuccessfully(true);
            }
        } else {
            shouldShowPrepayTermsAndConditions.setValue(false);
        }

        if (mCreditCardAddedSuccessfuly.getRawValue() || (isModify()
                && !getRawReservationObject().shouldCollectNewPaymentCardInModify())) {
            if (isModify()) {
                continueButton.setTitle(getResources().getString(R.string.reservations_modify_review_book_button_title));
            } else {
                continueButton.setTitle(getResources().getString(R.string.reservations_review_book_button_title));
            }
        } else {
            continueButton.setTitle(getResources().getString(R.string.reservations_review_add_payment_button_title));
        }
        setSubtitle(carClassDetails);
    }

    private void setSubtitle(EHICarClassDetails carClassDetails) {
        final EHIPriceSummary priceSummary = getRawReservationObject().getCarClassDetails().getPriceSummary();
        final boolean isCurrencyConversionScenario = priceSummary.isTravelingBetweenUSAndCanada();

        final boolean shouldShowPriceDifference = isModify()
                && !getRawReservationObject().shouldCollectNewPaymentCardInModify()
                && carClassDetails.getUnpaidRefundAmountPriceDifference(false) != null;

        if (isCurrencyConversionScenario) {
            continueButton.setSubtitle(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.review_prepay_na_book_button_subtitle)
                    .addTokenAndValue(EHIStringToken.AMOUNT, priceSummary.getEstimatedTotalPayment().getFormattedPrice(false))
                    .format().toString());

        } else if (isBillingBusineesTrip()) {
            continueButton.setSubtitle(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservations_review_book_button_billing_subtitle)
                    .addTokenAndValue(EHIStringToken.ACCOUNT, getSelectedPaymentName())
                    .format().toString());
        } else if (shouldShowPriceDifference) {
            showPriceDifferenceSubtitle(carClassDetails);
        } else if (mCreditCardAddedSuccessfuly.getRawValue()) {
            final EHIPaymentMethod paymentMethod = getSelectedPaymentMethod();
            final boolean hasPaymentMethod = paymentMethod != null && paymentMethod.getMaskedCreditCardNumber() != null;
            if (hasPaymentMethod) {
                continueButton.setSubtitle(new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.review_prepay_credit_card_book_button_subtitle)
                        .addTokenAndValue(EHIStringToken.METHOD, getSelectedPaymentMethod().getMaskedCreditCardNumber())
                        .format().toString());
            } else {
                continueButton.setSubtitle(getResources().getString(R.string.review_prepay_pay_now));
            }
        } else {
            continueButton.setSubtitle("");
        }
    }

    private boolean isBillingBusineesTrip() {
        return isBillingAccountChosen()
                && (getTripPurpose() != null
                && getTripPurpose().equalsIgnoreCase(TripPurposeFragment.TRIP_TYPE_BUSINESS));
    }

    private void showPriceDifferenceSubtitle(EHICarClassDetails carClassDetails) {
        final String stringToFormat;
        final CharSequence amountDifference;

        if (carClassDetails.isUnpaidRefundAmountPriceDifferenceNegative()) {
            stringToFormat = getResources().getString(R.string.review_payment_unpaid_amount_action);
            amountDifference = carClassDetails.getUnpaidRefundAmountPriceDifference(false);
        } else {
            stringToFormat = getResources().getString(R.string.review_payment_refund_amount_action);
            amountDifference = carClassDetails.getUnpaidPositiveRefundAmountPriceDifference(false);
        }

        final CharSequence refundOrUnpaidAmountString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(stringToFormat)
                .addTokenAndValue(EHIStringToken.AMOUNT, amountDifference)
                .format();

        continueButton.setSubtitle(refundOrUnpaidAmountString.toString());
        continueButton.setPriceSubtitle(getResources().getString(R.string.review_payment_updated_total_title));
    }

    public boolean shouldShowTermsAndConditions() {
        return shouldShowPrepayTermsAndConditions.getRawValue()
                && isUserLoggedIn()
                && isNorthAmerica()
                && !isModify()
                && getManagers().getLocalDataManager().shouldAutomaticallySelectCard();
    }

    private void showNonPrepayContinueButton() {
        shouldShowPrepayTermsAndConditions.setValue(false);

        final EHICarClassDetails carClassDetails = getRawReservationObject().getCarClassDetails();

        if (carClassDetails.isSecretRateAfterCarSelected()) {
            continueButton.showNetRate();
            continueButton.setEnabled(true);
        } else if (carClassDetails.getPaylaterPriceSummary() != null
                && carClassDetails.getPaylaterPriceSummary().getEstimatedTotalView() != null) {
            continueButton.setPrice(carClassDetails.getPaylaterPriceSummary().getEstimatedTotalView().getFormattedPrice(true));
            continueButton.setEnabled(true);
        }

        if (isModify()) {
            continueButton.setVisibility(isUpdated.getRawValue() ? ReactorViewState.VISIBLE : ReactorViewState.GONE);
        }

        if (isModify()) {
            continueButton.setTitle(getResources().getString(R.string.reservations_modify_review_book_button_title));
        } else {
            continueButton.setTitle(getResources().getString(R.string.reservations_review_book_button_title));
        }
        if (isBillingBusineesTrip() || carClassDetails.isSecretRateAfterCarSelected() || !EHITextUtils.isEmpty(getAccountName())) {
            continueButton.setSubtitle(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservations_review_book_button_billing_subtitle)
                    .addTokenAndValue(EHIStringToken.ACCOUNT, getSelectedPaymentName())
                    .format().toString());
        } else {
            continueButton.setSubtitle(getResources().getString(R.string.reservations_review_book_button_subtitle));
        }
    }

    public void updateModifyPrePayBannerVisibility() {
        modifyPrePayBanner.setVisibility(isModify()
                && isPrePay()
                ? ReactorViewState.VISIBLE : ReactorViewState.GONE);
    }

    public boolean isPrePay() {
        return mPayState.getRawValue() == ReservationFlowListener.PayState.PREPAY;
    }

    public boolean shouldShowCommitAnimation() {
        return mShouldShowCommitAnimation.getValue();
    }

    public void setShouldShowCommitAnimation(boolean show) {
        mShouldShowCommitAnimation.setValue(show);
    }

    public EHI3DSData get3DSData() {
        return mEHI3DSData.getValue();
    }

    public void threeDSAuthorizationCompleted(String paRes) {
        getManagers().getReservationManager().setPARes(paRes);
        commitReservation();
    }

    public void setAsUpdated() {
        isUpdated.setValue(true);
    }

    public String getAccountName() {
        final EHIReservation ehiReservation = getRawReservationObject();
        if (ehiReservation != null
                && ehiReservation.getCorporateAccount() != null
                && !EHITextUtils.isEmpty(ehiReservation.getCorporateAccount().getContractName())) {
            return ehiReservation.getCorporateAccount().getContractName();
        } else {
            return null;
        }
    }

    public boolean shouldShowPaymentsList() {
        return isUserLoggedIn() && isNorthAmerica()
                && getUserProfileCollection().getPaymentProfile() != null
                && !ListUtils.isEmpty(getUserProfileCollection().getPaymentProfile().getAllPaymentMethods());
    }

    public void setTermsAndConditionsVisibility(boolean shouldShow) {
        mShowTermsAndConditions = shouldShow;
    }

    public boolean shouldShowTermsAndConditionsView() {
        return mShowTermsAndConditions;
    }

    public void setPrepayTermsChecked(boolean checked) {
        continueButton.setEnabled(checked);
        mShowTermsAndConditions = !checked;
    }

    public void clearPaymentState(ReservationFlowListener.PayState payState) {
        mPaymentIds = null;
        getManagers().getReservationManager().setPARes(null);
        shouldShowPrepayTermsAndConditions.setRawValue(payState == ReservationFlowListener.PayState.PREPAY);
        creditAddedSuccessfully(payState == ReservationFlowListener.PayState.PAY_LATER || shouldShowTermsAndConditions());
        setPayState(payState);
        postCarClass(null);
    }

    public boolean isBookButtonEnabled() {
        return continueButton.enabled().getRawValue();
    }

    public boolean shouldShowAddCreditCardScreen() {
        return isPrePay() &&
                ((isModify() && getRawReservationObject().shouldCollectNewPaymentCardInModify() && !mCollectedNewPaymentCardInModify) ||
                        (!isModify() && !mCreditCardAddedSuccessfuly.getRawValue()));
    }

    public void setCollectedNewPaymentCardInModify(boolean collectedNewPaymentCardInModify) {
        mCollectedNewPaymentCardInModify = collectedNewPaymentCardInModify;
    }

    public boolean canChangePaymentOptions() {
        return hasCharges() && !isAirportModifyBlockScenario() && getPayState() != REDEMPTION;
    }

    public boolean isAirportModifyBlockScenario() {
        return isModify()
                && getRawReservationObject() != null
                && getRawReservationObject().shouldBlockModifyPickupLocation();
    }

    public boolean isNorthAmericaAirportLocation() {
        return getRawReservationObject().getPickupLocation().isAirport() && isNorthAmerica();
    }

    private boolean hasCharges() {
        final EHICarClassDetails details = getRawReservationObject().getCarClassDetails();
        if (details != null) {
            return (mPayState.getRawValue() == ReservationFlowListener.PayState.PAY_LATER && details.isPrepayRateAvailable())
                    || (mPayState.getRawValue() == ReservationFlowListener.PayState.PREPAY && details.isPayLaterRateAvailable());
        }
        return false;
    }

    public boolean isUserInCorpFlowWithBillingContract() {
        final EHIReservation ehiReservation = getRawReservationObject();
        return ehiReservation != null
                && ehiReservation.contractHasAdditionalBenefits()
                && ehiReservation.getCorporateAccount() != null
                && ehiReservation.getCorporateAccount().isContractAcceptsBilling();
    }

    public boolean isIsLoginAfterStart() {
        return mIsLoginAfterStart;
    }

    public void setIsLoginAfterStart(boolean value) {
        mIsLoginAfterStart = value;
    }
}
