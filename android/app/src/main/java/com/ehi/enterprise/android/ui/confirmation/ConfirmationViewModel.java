package com.ehi.enterprise.android.ui.confirmation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHICancellation;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIPayment;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHIVehicleLogistic;
import com.ehi.enterprise.android.models.reservation.ReservationInformation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetMoreTaxesInformationRequest;
import com.ehi.enterprise.android.network.requests.reservation.GetRetrieveReservationRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostCancelReservationRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.GetAvailableCarClassesForModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.CancelReservationResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.network.responses.reservation.GetMoreTaxesInformationResponse;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PAY_LATER;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PREPAY;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.REDEMPTION;

@AutoUnbindAll
public class ConfirmationViewModel extends CountrySpecificViewModel {

    ReactorVar<EHIReservation> mReservationObject = new ReactorVar<>();
    ReactorVar<String> mCanceledConfirmationNumber = new ReactorVar<>();
    ReactorVar<Boolean> mIsModify = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    final ReactorVar<String> mPrepayTermsAndConditions = new ReactorVar<>();
    final ReactorVar<Integer> title = new ReactorVar<>();


    ReactorVar<String> mTaxesAndFeesMoreInformation = new ReactorVar<>();

    private ReactorVar<ResponseWrapper> mRetrieveBeforeModifyResponse = new ReactorVar<>();

    private String mModifyButtonErrorText;
    private String mCancelButtonErrorText;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.reservation_confirmation_navigation_title);
        if (!isModify()) {
            setModifyState(false);
        }
    }

    public EHIReservation getReservationObject() {
        return mReservationObject.getValue();
    }

    public void setReservationObject(EHIReservation reservation) {
        mReservationObject.setValue(reservation);
    }

    public String getCanceledConfirmationNumber() {
        return mCanceledConfirmationNumber.getValue();
    }

    public ResponseWrapper getErrorWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    public void cancelReservation() {
        showProgress(true);
        refreshReservation(new RefreshReservationCallback<EHIReservation>() {
            @Override
            public void reservationRefreshed(ResponseWrapper<EHIReservation> responseWrapper) {
                EHIReservation res = responseWrapper.getData();
                performRequest(new PostCancelReservationRequest(res.getResSessionId(), res.getConfirmationNumber()), new IApiCallback<CancelReservationResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<CancelReservationResponse> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            mCanceledConfirmationNumber.setValue(response.getData().getConfirmationNumber());
                        } else {
                            mErrorWrapper.setValue(response);
                        }
                    }
                });
            }
        });

    }

    @Nullable
    public Date getPickupTime() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getPickupTime();
        }
        return null;
    }

    public boolean needToShowQuickPickup() {
        return hasQuickPickupUrl()
                && getQuickPickupUrl() != null
                && !isUserLoggedIn();

    }

    public boolean hasQuickPickupUrl() {
        return mReservationObject.getValue() != null && mReservationObject.getValue().hasQuickRentalUrl();
    }

    public String getQuickPickupUrl() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getQuickRentalUrl();
        }
        return null;
    }

    @Nullable
    public Date getReturnTime() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getReturnTime();
        }
        return null;
    }

    @Nullable
    public String getConfirmationNumber() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getConfirmationNumber();
        }
        return null;
    }

    @Nullable
    public EHILocation getPickupLocation() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getPickupLocation();
        }
        return null;
    }

    @Nullable
    public EHIAirlineInformation getEHIAirlineInformation() {
        return mReservationObject.getValue().getAirlineInformation();
    }

    @Nullable
    public EHIAirlineDetails getCurrentAirlineDetail() {
        if (getEHIAirlineInformation() != null) {
            final EHIReservation ehiReservation = getReservationObject();
            if (!ListUtils.isEmpty(ehiReservation.getPickupLocation().getEHIAirlineDetails())) {
                final int flightIndex = getFlightIndex();
                if (flightIndex != -1) {
                    return ehiReservation.getPickupLocation().getEHIAirlineDetails().get(flightIndex);
                }
            }
        }
        return null;

    }

    @Nullable
    public String getFlightNumber() {
        if (getEHIAirlineInformation() != null) {
            return getEHIAirlineInformation().getFlightNumber();
        }
        return null;
    }

    @Nullable
    public EHILocation getReturnLocation() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getReturnLocation();
        }
        return null;
    }

    @Nullable
    public EHICarClassDetails getCarClassDetails() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getCarClassDetails();
        }
        return null;
    }

    @Nullable
    public EHICancellation getCancelationDetails() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getCancellationDetails();
        }
        return null;
    }

    @Nullable
    public EHIDriverInfo getDriverInfo() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getDriverInfo();
        }
        return null;
    }

    @Nullable
    public EHIVehicleLogistic getVehicleLogistic() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getVehicleLogistic();
        }
        return null;
    }

    public void setIsModify(boolean isModify) {
        mIsModify.setValue(isModify);
    }

    public boolean isModify() {
        return mIsModify.getValue();
    }

    public String getSessionId() {
        return mReservationObject.getValue().getResSessionId();
    }

    public String getAccountName() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getCorporateAccount() != null
                && mReservationObject.getValue().getCorporateAccount().getContractName() != null
                && !mReservationObject.getValue().getCorporateAccount().getContractName().isEmpty()) {
            return mReservationObject.getValue().getCorporateAccount().getContractName();
        } else {
            return null;
        }
    }

    public String getAccountId() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getCorporateAccount() != null
                && mReservationObject.getValue().getCorporateAccount().getContractNumber() != null
                && !mReservationObject.getValue().getCorporateAccount().getContractNumber().isEmpty()) {
            return mReservationObject.getValue().getCorporateAccount().getContractNumber();
        } else {
            return null;
        }
    }

    public String getContractType() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getCorporateAccount() != null
                && mReservationObject.getValue().getCorporateAccount().getContractType() != null
                && !mReservationObject.getValue().getCorporateAccount().getContractType().isEmpty()) {
            return mReservationObject.getValue().getCorporateAccount().getContractType();
        } else {
            return null;
        }
    }

    public List<EHIAdditionalInformation> getAdditionalInformation() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getAdditionalInformation() != null) {
            return mReservationObject.getValue().getAdditionalInformation();
        }
        return null;
    }

    public EHIContract getCorporateAccount() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getCorporateAccount() != null) {
            return mReservationObject.getValue().getCorporateAccount();
        }
        return null;
    }

    public EHIPaymentMethod getPaymentMethod() {
        if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getSelectedPaymentMethod() != null) {
            return mReservationObject.getValue().getSelectedPaymentMethod();
        }
        return null;
    }

    public EHIPayment getPaymentInfo() {
        if (mReservationObject.getValue() != null
                && !ListUtils.isEmpty(mReservationObject.getValue().getPayments())) {
            return mReservationObject.getValue().getPayments().get(0);
        }
        return null;
    }

    public boolean isPrePay() {
        return mReservationObject.getValue().isPrepaySelected();
    }

    void requestLearnMore() {
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

    public void setTaxesAndFeesMoreInformation(String taxesAndFeesMoreInformation) {
        mTaxesAndFeesMoreInformation.setValue(taxesAndFeesMoreInformation);
    }

    public String getTaxesAndFeesMoreInformation() {
        return mTaxesAndFeesMoreInformation.getValue();
    }

    public boolean isReservationCanceled() {
        return mReservationObject.getValue().getReservationStatus().equalsIgnoreCase(EHIReservation.CANCELED);
    }

    public String getRulesOfRoadUrl() {
        return mReservationObject.getValue().getRulesOfRoad();
    }

    public int getRedemptionPointsUsed() {
        int pointsUsed = mReservationObject.getValue().getCarClassDetails().getEplusPointsUsed();
        if (pointsUsed == 0) {
            pointsUsed = mReservationObject.getValue().getEplusPointsUsed();
        }
        return pointsUsed;
    }

    public ReservationInformation getReservationInformation() {
        return ReservationInformation.fromReservationObject(mReservationObject.getRawValue());
    }

    public int getRedemptionPointsRate() {
        return Float.valueOf(mReservationObject.getValue().getCarClassDetails().getRedemptionPoints()).intValue();
    }

    public int getRedemptionDayCount() {
        int daysCount = mReservationObject.getValue().getCarClassDetails().getRedemptionDayCount();
        if (daysCount == 0) {
            daysCount = mReservationObject.getValue().getRedemptionDayCount();
        }
        return daysCount;
    }

    public boolean isReservationBookingSystemEcars() {
        return getReservationBookingSystem() != null && getReservationBookingSystem().equalsIgnoreCase(EHIReservation.BOOKING_SYSTEM_ECARS);
    }

    public boolean isReservationBookingSystemOdyssey() {
        return getReservationBookingSystem() != null && getReservationBookingSystem().equalsIgnoreCase(EHIReservation.BOOKING_SYSTEM_ODYSSEY);
    }

    public String getReservationBookingSystem() {
        return mReservationObject.getValue().getReservationBookingSystem();
    }

    public boolean isReservationEligibilityAvailable() {
        return mReservationObject.getValue().getReservationEligibility() != null;
    }

    public boolean isReservationCancellable() {
        return mReservationObject.getValue().isCancellable();
    }

    public boolean isReservationModifiable() {
        return mReservationObject.getValue().isModifiable();
    }

    public void setModifyReservationObject(EHIReservation reservationObject) {
        getManagers().getReservationManager().addOrUpdateModifyReservation(reservationObject);
        getManagers().getReservationManager().addOrUpdateSelectedCarClass(reservationObject.getCarClassDetails());
    }

    public EHIReservation getModifyReservationObject() {
        return getManagers().getReservationManager().getCurrentModifyReservation();
    }

    public void updateReservationInfo() {
        showProgress(true);

        if (isModify() && getModifyReservationObject() != null) {
            setReservationObject(getModifyReservationObject());
        }

        EHIReservation ehiReservation = getReservationObject();

        performRequest(
                new GetRetrieveReservationRequest(
                        ehiReservation.getConfirmationNumber(),
                        ehiReservation.getDriverInfo().getFirstName(),
                        ehiReservation.getDriverInfo().getLastName()
                ),
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            setReservationObject(response.getData());
                        } else {
                            setError(response);
                        }
                    }
                }
        );
    }

    public int getFlightIndex() {
        if (getEHIAirlineInformation() == null || getEHIAirlineInformation().getCode() == null
                || getPickupLocation() == null) {
            return -1;
        }

        return BaseAppUtils.indexOf(getEHIAirlineInformation(), getPickupLocation().getEHIAirlineDetails(), new BaseAppUtils.CompareTwo<EHIAirlineInformation, EHIAirlineDetails>() {
            @Override
            public boolean equals(EHIAirlineInformation first, EHIAirlineDetails second) {
                return first.getCode().equalsIgnoreCase(second.getCode());
            }
        });
    }

    public boolean shouldDisableModifyButton() {
        if (isReservationCanceled()
                || isReservationBookingSystemEcars()
                || (isReservationEligibilityAvailable()
                && !isReservationModifiable())) {
            mModifyButtonErrorText = getResources().getString(R.string.ecars_reservation_modify_call_prompt_message);
            return true;
        }
        return false;
    }

    public String getModifyButtonErrorText() {
        return mModifyButtonErrorText;
    }

    public boolean shouldDisableCancelButton() {
        if (isReservationCanceled()
                || !isReservationCancellable()) {
            mCancelButtonErrorText = getResources().getString(R.string.ecars_reservation_modify_call_prompt_message);
            return true;
        }
        return false;
    }

    public String getCancelButtonErrorText() {
        return mCancelButtonErrorText;
    }

    public void retrieveReservationForModify() {
        showProgress(true);
        getManagers().getReservationManager().clearSelectedAirlineInformation();
        performRequest(new GetRetrieveReservationRequest(getReservationObject().getConfirmationNumber(),
                        getReservationObject().getDriverInfo().getFirstName(),
                        getReservationObject().getDriverInfo().getLastName()),
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);

                        if (!response.isSuccess()) {
                            setError(response);
                            return;
                        }

                        final EHIReservation ehiReservation = response.getData();

                        setReservationObject(ehiReservation);
                        setModifyReservationObject(getReservationObject());

                        getAvailableCarClassesForModify();
                    }
                });
    }

    public void getAvailableCarClassesForModify() {
        showProgress(true);
        performRequest(new GetAvailableCarClassesForModifyRequest(getManagers().getReservationManager().getCurrentModifyReservation().getResSessionId()), new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    EHIReservation reservation = response.getData();
                    getManagers().getReservationManager().addOrUpdateModifyReservation(reservation);
                    getManagers().getReservationManager().setFilters(response.getData().getCarClassFilterList());
                    setReservationObject(reservation);

                    setRetrieveBeforeModifyResponse(response);
                } else {
                    setError(response);
                }
            }
        });
    }

    public void retrieveReservation(final String confirmationNumber, final String firstName, final String lastName) {
        showProgress(true);
        performRequest(new GetRetrieveReservationRequest(confirmationNumber,
                        firstName,
                        lastName),
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            setReservationObject(response.getData());
                        }
                    }
                });
    }

    public void refreshReservation(final RefreshReservationCallback<EHIReservation> responseCallback) {
        showProgress(true);
        performRequest(new GetRetrieveReservationRequest(getReservationObject().getConfirmationNumber(),
                        getReservationObject().getDriverInfo().getFirstName(),
                        getReservationObject().getDriverInfo().getLastName()),
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        showProgress(false);
                        if (response.isSuccess()) {
                            mReservationObject.setRawValue(response.getData());
                            responseCallback.reservationRefreshed(response);
                        } else {
                            mErrorWrapper.setValue(response);
                        }
                    }
                });
    }

    public ResponseWrapper getRetrieveBeforeModifyResponse() {
        return mRetrieveBeforeModifyResponse.getValue();
    }

    public void setRetrieveBeforeModifyResponse(ResponseWrapper retrieveBeforeModifyResponse) {
        mRetrieveBeforeModifyResponse.setValue(retrieveBeforeModifyResponse);
    }

    public void setModifyState(boolean isModify) {
        getManagers().getReservationManager().setModify(isModify);
    }

    public CharSequence getTitleForCalendar() {
        CharSequence formattedString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.calendar_event_title)
                .addTokenAndValue(EHIStringToken.NUMBER, getConfirmationNumber())
                .format();
        return formattedString;
    }

    public String getDescriptionForCalendar() {
        StringBuilder description = new StringBuilder();
        description.append(getPickupLocation().getAddress().getAddressForCalendar());
        description.append("\n");
        description.append(getSupportPhoneNumber());
        return description.toString();
    }

    public boolean shouldShowPrepayModifyDialog() {
        final boolean isNorthAmericaAirportLocation = getReservationObject().getPickupLocation().isAirport() && isNorthAmerica();
        return isPrePay() && !isNorthAmericaAirportLocation;
    }

    interface RefreshReservationCallback<T> {
        void reservationRefreshed(ResponseWrapper<T> responseWrapper);
    }

    public void setPrepayTermsAndConditions(String prepayTermsAndConditions) {
        mPrepayTermsAndConditions.setValue(prepayTermsAndConditions);
    }

    public String getPrepayTermsAndConditions() {
        return mPrepayTermsAndConditions.getValue();
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

    public boolean is3rdPartyEmailNotify() {
        return super.is3rdPartyEmailNotify(isModify());
    }

    public ReservationFlowListener.PayState getPayState() {
        if (mReservationObject.getValue().isPrepaySelected()) {
            return PREPAY;
        }

        if (getRedemptionDayCount() > 0
                || getRedemptionPointsUsed() > 0) {
            return REDEMPTION;
        }

        return PAY_LATER;
    }

    public boolean shouldShowConfirmationRateUs() {
        if (!isUserLoggedIn()
                && !isLoggedIntoEmeraldClub()) {
            return false;
        }
        getManagers().getLocalDataManager().increaseConfirmationRateUsCounter();
        return getManagers().getLocalDataManager().shouldShowConfirmationRateUs();
    }

    public void markConfirmationRateUsDone() {
        getManagers().getLocalDataManager().markConfirmationRateUsDone();
    }

}