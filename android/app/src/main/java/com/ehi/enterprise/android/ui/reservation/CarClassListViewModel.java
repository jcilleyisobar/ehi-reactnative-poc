package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectCarClassRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.GetAvailableCarClassesForModifyRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectCarClassModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.ListUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CarClassListViewModel extends CountrySpecificViewModel {

    ReactorVar<EHIReservation> mReservationObject = new ReactorVar<>();
    ReactorVar<EHICarClassDetails> mSelectCarClassResponse = new ReactorVar<>();
    ReactorVar<Boolean> mShowPoints = new ReactorVar<>(false);
    ReactorVar<Float> mLowestPrice = new ReactorVar<>(null);
    ReactorVar<EHIReservation> mCarClassResponse = new ReactorVar<>(null);
    ReactorVar<EHICarClassDetails> mChosenCar = new ReactorVar<>(null);
    final ReactorVar<Boolean> determinateLoader = new ReactorVar<>(false);
    private Boolean mShouldQuickReturn = true;
    private boolean mIsModify;
    private boolean mExtrasAnimationReset;
    private boolean mShowPointsFromToggle;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        getManagers().getReservationManager().setCarUpgradeSelection(null);
        getManagers().getReservationManager().vehicleUpgradeWasPrompted(false);
        mExtrasAnimationReset = true;
        mShowPointsFromToggle = false;
    }

    public EHIReservation getReservationObject() {
        if (mReservationObject != null) {
            return mReservationObject.getValue();
        }
        return null;
    }

    public void populateReservationObject() {
        if (isModify()) {
            EHIReservation currentModifyReservation = getManagers().getReservationManager().getCurrentModifyReservation();
            if (mReservationObject.getRawValue() == null
                    || currentModifyReservation == null
                    || currentModifyReservation.getCarClasses() == null
                    || currentModifyReservation.getCarClasses().size() == 0) {
                determinateLoader.setValue(true);
                performRequest(new GetAvailableCarClassesForModifyRequest(getManagers().getReservationManager().getCurrentModifyReservation().getResSessionId()), new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        determinateLoader.setValue(false);
                        if (response.isSuccess()) {
                            EHIReservation reservation = response.getData();
                            getManagers().getReservationManager().addOrUpdateModifyReservation(reservation);
                            getManagers().getReservationManager().setFilters(response.getData().getCarClassFilterList());
                            mReservationObject.setValue(reservation);
                            populateRedemptionArea();
                        } else {
                            setError(response);
                        }
                    }
                });
            } else {
                mReservationObject.setValue(getManagers().getReservationManager().getCurrentModifyReservation());
            }
        } else {
            mReservationObject.setValue(getManagers().getReservationManager().getCurrentReservation());
            populateRedemptionArea();
        }
    }

    private void populateRedemptionArea() {
        if (isUserLoggedIn()) {
            boolean shouldShow = needShowPoints();

            if (mReservationObject.getRawValue() != null
                    && !ListUtils.isEmpty(mReservationObject.getRawValue().getCarClasses())) {
                List<EHICarClassDetails> detailsList = mReservationObject.getRawValue().getCarClasses();

                for (int i = 0; i < detailsList.size(); i++) {
                    if (mLowestPrice.getValue() == null
                            || (mLowestPrice.getValue() > detailsList.get(i).getRedemptionPoints()
                            && Float.compare(detailsList.get(i).getRedemptionPoints(), 0f) != 0)) {
                        mLowestPrice.setValue(detailsList.get(i).getRedemptionPoints());
                    }
                }
            }

            if (mLowestPrice.getValue() == null) {
                mLowestPrice.setValue(0f);
            }

            final EHILoyaltyData ehiLoyaltyData = getUserProfileCollection().getBasicProfile().getLoyaltyData();
            final long pointsToDate = ehiLoyaltyData != null ? ehiLoyaltyData.getPointsToDate() : 0;

            shouldShow = shouldShow && Float.compare(mLowestPrice.getValue(), 0f) != 0;
            shouldShow = shouldShow && pointsToDate >= mLowestPrice.getValue();

            if (mReservationObject.getRawValue() != null) {
                shouldShow = shouldShow && mReservationObject.getRawValue().doesLocationSupportRedemption();
            }

            mShouldQuickReturn = shouldShow;
            if (!isModify()) {
                mShowPoints.setValue(shouldShow);
                setNeedShowPoints(shouldShow);
            }
        }
    }

    public void populateExtras(EHICarClassDetails carClass) {
        mExtrasAnimationReset = false;

        getManagers().getReservationManager().setSelectedCarClassCharges(carClass.getCharge());

        AbstractRequestProvider request;
        if (isModify()) {
            //this is an edge case when changing from prepay location to pay later location
            //reservation still have prepay as true but all car classes don't have prepay rates
            boolean shouldSelectPrepay = getReservationObject().isPrepaySelected()
                    && (carClass.isPrepayRateAvailable() || carClass.getCharge(EHICharge.PREPAY) != null);
            request = new PostSelectCarClassModifyRequest(
                    getReservationObject().getResSessionId(),
                    carClass.getCode(),
                    0,
                    shouldSelectPrepay
            );
        } else {
            request = new PostSelectCarClassRequest(getReservationObject().getResSessionId(), carClass.getCode(), false);
        }

        performRequest(request, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                if (mExtrasAnimationReset) {
                    mExtrasAnimationReset = false;
                    return;
                }
                if (response.isSuccess()) {
                    EHIReservation reservation = response.getData();
                    if (isModify()) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(reservation);
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(reservation);
                    }
                    mCarClassResponse.setValue(reservation);
                } else {
                    setError(response);
                }
            }
        });
    }

    public void selectCarClass(final EHICarClassDetails carClass) {
        showProgress(true);
        AbstractRequestProvider request;
        if (isModify()) {
            //this is an edge case when changing from prepay location to pay later location
            //reservation still have prepay as true but all car classes don't have prepay rates
            boolean shouldSelectPrepay = getReservationObject().isPrepaySelected()
                    && (carClass.isPrepayRateAvailable() || carClass.isPrepayChargesAvailable());
            request = new PostSelectCarClassModifyRequest(
                    getReservationObject().getResSessionId(),
                    carClass.getCode(),
                    0,
                    shouldSelectPrepay
            );
        } else {
            request = new PostSelectCarClassRequest(getReservationObject().getResSessionId(), carClass.getCode(), false);
        }
        performRequest(request, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    EHIReservation reservation = response.getData();
                    mSelectCarClassResponse.setValue(reservation.getCarClassDetails().merge(carClass));
                    if (isModify()) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(reservation);
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(reservation);
                    }
                } else {
                    setError(response);
                }
            }
        });
    }


    public EHICarClassDetails getCarClassSelectResponse() {
        return mSelectCarClassResponse.getValue();
    }

    public void resetCarClassSelectResponse() {
        if (mSelectCarClassResponse != null) {
            mSelectCarClassResponse.setValue(null);
        }
    }

    public boolean isShowingPoints() {
        return mShowPoints.getValue();
    }

    public void togglePointsVisibility() {
        boolean newValue = !mShowPoints.getRawValue();
        mShowPoints.setValue(newValue);
        mShowPointsFromToggle = true;
        setNeedShowPoints(newValue);
    }

    public float getLowestPointRental() {
        return mLowestPrice.getValue();
    }

    public boolean shouldQuickReturn() {
        return mShouldQuickReturn;
    }

    public EHICarClassDetails getChosenCar() {
        return mChosenCar.getValue();
    }

    public EHIReservation getExtrasReservation() {
        return mCarClassResponse.getValue();
    }

    public void setExtrasReservation(EHIReservation reservation) {
        mCarClassResponse.setValue(reservation);
    }

    public void setChosenCar(EHICarClassDetails details) {
        mChosenCar.setValue(details);
        getManagers().getReservationManager().addOrUpdateSelectedCarClass(details);
    }

    public List<EHIAvailableCarFilters> getCurrentFilters() {
        List<EHIAvailableCarFilters> filters = getManagers().getReservationManager().getFilters();
        if (filters != null) {
            return filters;
        } else {
            return new LinkedList<>();
        }

    }

    public void commitFilters(List<EHIAvailableCarFilters> filters) {
        getManagers().getReservationManager().setFilters(filters);
    }

    public void setIsModify(Boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setIsPromotionAvailableAllCarClasses(List<EHICarClassDetails> carClasses) {
        List<EHICarClassDetails> unavailableCarsList = new ArrayList<>();
        for (EHICarClassDetails car : carClasses) {
            if (!car.getStatus().equals(EHICarClassDetails.AVAILABLE_AT_PROMOTIONAL_RATE)) {
                unavailableCarsList.add(car);
            }
        }
        getManagers().getReservationManager().setIsPromotionAvailableAllCarClasses(unavailableCarsList.size() != carClasses.size());
    }

    public boolean isAvailableAtContractRate() {
        String contractType = getCorporateContractType();
        if (contractType != null) {
            if (contractType.equalsIgnoreCase(EHIContract.CONTRACT_TYPE_CORPORATE)
                    && getManagers().getReservationManager().getIsCorporateAvailableAllCarClasses()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAvailableAtPromotionRate() {
        String contractType = getCorporateContractType();
        if (contractType != null) {
            if (contractType.equalsIgnoreCase(EHIContract.CONTRACT_TYPE_PROMOTION)
                    && getManagers().getReservationManager().getIsPromotionAvailableAllCarClasses()) {
                return true;
            }
        }
        return false;
    }

    public void setIsCorporateAvailableAllCarClasses(List<EHICarClassDetails> carClasses) {
        List<EHICarClassDetails> unavailableCarsList = new ArrayList<>();
        for (EHICarClassDetails car : carClasses) {
            if (!car.getStatus().equals(EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE)) {
                unavailableCarsList.add(car);
            }
        }
        getManagers().getReservationManager().setIsCorporateAvailableAllCarClasses(unavailableCarsList.size() != carClasses.size());
    }

    public void addOrUpdateSelectedCarClass(EHICarClassDetails carClassDetails) {
        getManagers().getReservationManager().addOrUpdateSelectedCarClass(carClassDetails);
    }

    public void animationReset() {
        mExtrasAnimationReset = true;
        showProgress(false);
    }

    public String getCorporateAccountName() {
        return super.getCorporateAccountName(isModify());
    }

    public String getCorporateContractType() {
        return super.getCorporateContractType(isModify());
    }

    public String getTermsAndConditions() {
        return getCorporateAccountTermsAndConditions(isModify());
    }

    public boolean showPointsFromToggle() {
        return mShowPointsFromToggle;
    }

    public EHICharge getCharge(EHICarClassDetails ehiCarClassDetails) {
        EHICharge charge;
        if (getDefaultPayState(isModify()) == ReservationFlowListener.PayState.PREPAY) {
            charge = ehiCarClassDetails.getCharge(EHICharge.PREPAY);
            if (charge == null) {
                charge = ehiCarClassDetails.getCharge(EHICharge.PAYLATER);
            }
        } else {
            charge = ehiCarClassDetails.getCharge(EHICharge.PAYLATER);
            if (charge == null) {
                charge = ehiCarClassDetails.getCharge(EHICharge.PREPAY);
            }
        }
        return charge;
    }

    public boolean shouldShowCurrencyBanner() {
        return !getManagers().getLocalDataManager().getPreferredCountryCode()
                .equals(mReservationObject.getRawValue().getPickupLocation().getAddress().getCountryCode());

    }

    public boolean getShowClassTotalCostAsterisks() {
        return getManagers().getLocalDataManager().getShowClassTotalCostAsterisks();
    }
}
