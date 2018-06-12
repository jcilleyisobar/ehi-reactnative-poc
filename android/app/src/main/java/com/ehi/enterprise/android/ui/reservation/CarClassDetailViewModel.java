package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.models.reservation.EHIVehicleRate;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectCarClassRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectCarClassModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CarClassDetailViewModel extends CountrySpecificViewModel {

    //region ReactorVars
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorVar<EHICarClassDetails> mCarClassDetails = new ReactorVar<>();
    final ReactorVar<String> mPayState = new ReactorVar<>();
    final ReactorVar<Boolean> mNeedShowPointsWrapper = new ReactorVar<>();
    final ReactorVar<Boolean> pointsContainerVisibility = new ReactorVar<>();
    final ReactorVar<Boolean> showPointsOnHeader = new ReactorVar<>();
    final ReactorVar<Boolean> ePointsHeaderVisibility = new ReactorVar<>();
    final ReactorVar<Boolean> ePointsHeaderShouldShowPoints = new ReactorVar<>();
    final ReactorVar<String> nameOfClass = new ReactorVar<>();
    final ReactorVar<String> carClassDescription = new ReactorVar<>();
    final ReactorVar<String> peopleCapacityText = new ReactorVar<>();
    final ReactorVar<String> luggageCapacityText = new ReactorVar<>();
    final ReactorVar<CharSequence> nameOfCar = new ReactorVar<>();
    final ReactorVar<String> carTransmissionDescription = new ReactorVar<>();
    final ReactorVar<Boolean> negotiatedRateVisibility = new ReactorVar<>(false);
    final ReactorVar<Integer> negotiatedRateText = new ReactorVar<>();
    final ReactorVar<Boolean> classDetailsConversionAreaVisibility = new ReactorVar<>(false);
    final ReactorVar<CharSequence> classDetailsConversionTotalText = new ReactorVar<>();
    final ReactorVar<CharSequence> classDetailsConversionText = new ReactorVar<>();
    final ReactorVar<CharSequence> estimatedTotalText = new ReactorVar<>();
    final ReactorTextViewState estimatedTotalLabel = new ReactorTextViewState();
    final ReactorVar<Boolean> priceHeaderVisibility = new ReactorVar<>(true);
    final ReactorVar<Boolean> priceEstimatedTotalContainerVisibility = new ReactorVar<>(true);
    final ReactorVar<Boolean> headerTotalVisibility = new ReactorVar<>(false);
    final ReactorVar<Boolean> headerRentalRangeVisibility = new ReactorVar<>(false);
    final ReactorVar<Boolean> priceUnavailableVisibility = new ReactorVar<>(false);
    final ReactorVar<CharSequence> headerTotalText = new ReactorVar<>();
    final ReactorVar<Boolean> selectThisClassButtonVisibility = new ReactorVar<>(true);
    final ReactorVar<Boolean> callLocationButtonVisibility = new ReactorVar<>(false);
    final ReactorVar<Boolean> noPriceAvailableVisibility = new ReactorVar<>(false);
    final ReactorVar<List<EHIImage>> images = new ReactorVar<>();
    final ReactorTextViewState headerRentalRange = new ReactorTextViewState();

    ReactorVar<EHIReservation> mCarClassResponse = new ReactorVar<>(null);
    //endregion

    @EHIImageUtils.ImageType
    int imageTypeToLoad = 0;
    private boolean mIsModify;
    private boolean mCarInfoInvalid = false;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.reservation_class_details_navigation_title);
        pointsContainerVisibility.setValue(isUserLoggedIn());
        showPointsOnHeader.setValue(!isUserLoggedIn());
        ePointsHeaderVisibility.setValue(isUserLoggedIn());
        ePointsHeaderShouldShowPoints.setValue(!isUserLoggedIn());
        EHICarClassDetails upgrade = getManagers().getReservationManager().getCarUpgradeSelection();
        if (mCarInfoInvalid && upgrade != null) {
            setCarClassDetails(upgrade);
            mCarInfoInvalid = false;
        }
    }

    @Nullable
    public EHICarClassDetails getCarClassDetails() {
        return mCarClassDetails.getValue();
    }

    private EHICarClassDetails getRawCarClassDetails() {
        return mCarClassDetails.getRawValue();
    }

    public void setCarClassDetails(EHICarClassDetails carClassDetails) {
        mCarClassDetails.setValue(carClassDetails);
        nameOfClass.setValue(mCarClassDetails.getRawValue().getName());
        carClassDescription.setValue(mCarClassDetails.getRawValue().getDescription());
        peopleCapacityText.setValue(String.valueOf(mCarClassDetails.getRawValue().getPeopleCapacity()));
        luggageCapacityText.setValue(String.valueOf(mCarClassDetails.getRawValue().getLuggageCapacity()));
        nameOfCar.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.reservation_car_class_make_model_title)
                .addTokenAndValue(EHIStringToken.MAKE_MODEL, mCarClassDetails.getRawValue().getMakeModelOrSimilarText().trim())
                .format());
        carTransmissionDescription.setValue(EHICarClassDetails.getTransmissionDescription(mCarClassDetails.getRawValue().getFeatures()));
        images.setValue(mCarClassDetails.getRawValue().getImages());
        imageTypeToLoad = EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE;
        populateTotals();
    }

    public void setPayState(ReservationFlowListener.PayState payState) {
        if (payState == ReservationFlowListener.PayState.PREPAY){
            mPayState.setValue(EHIVehicleRate.PREPAY);
        } else {
            mPayState.setValue(EHIVehicleRate.PAYLATER);
        }

    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        mCarInfoInvalid = true;
    }

    private void populateTotals() {
        if (EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE.equalsIgnoreCase(mCarClassDetails.getRawValue().getStatus())) {
            negotiatedRateVisibility.setValue(true);
            negotiatedRateText.setValue(R.string.car_class_cell_negotiated_rate_title);
        } else if (EHICarClassDetails.AVAILABLE_AT_PROMOTIONAL_RATE.equalsIgnoreCase(mCarClassDetails.getRawValue().getStatus())) {
            negotiatedRateVisibility.setValue(true);
            negotiatedRateText.setValue(R.string.car_class_cell_promotional_rate_title);
        } else {
            negotiatedRateVisibility.setValue(false);
        }

        final EHIPriceSummary priceSummary = getDefaultPayState(isModify()).equals(ReservationFlowListener.PayState.PREPAY)
                ? mCarClassDetails.getRawValue().getPrepayPriceSummary()
                : mCarClassDetails.getRawValue().getPaylaterPriceSummary();

        if (priceSummary != null) {

            if (getRawCarClassDetails().isSecretRate()) {
                estimatedTotalText.setValue(getResources().getString(R.string.reservation_price_unavailable));
            } else {
                estimatedTotalText.setValue(priceSummary.getEstimatedTotalView().getFormattedPrice(true));
            }

            if (priceSummary.isDifferentPaymentCurrency()) {
                classDetailsConversionAreaVisibility.setValue(true);
                classDetailsConversionText.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.car_class_details_transparency_total_na)
                        .addTokenAndValue(EHIStringToken.CURRENCY_CODE, priceSummary.getEstimatedTotalPayment().getCurrencyCode())
                        .format());
                if (getRawCarClassDetails().isSecretRate()) {
                    classDetailsConversionTotalText.setValue(getResources().getString(R.string.reservation_price_unavailable));
                } else {
                    classDetailsConversionTotalText.setValue(priceSummary.getEstimatedTotalPayment().getFormattedPrice(false));
                }
            } else {
                classDetailsConversionAreaVisibility.setValue(false);
            }
        } else {
            priceHeaderVisibility.setValue(false);
            priceEstimatedTotalContainerVisibility.setValue(false);
        }

        if (!mCarClassDetails.getRawValue().shouldShowCallForAvailability()) {
            if (priceSummary != null) {
                if (getRawCarClassDetails().isSecretRate()) {
                    headerTotalVisibility.setValue(false);
                    headerRentalRangeVisibility.setValue(false);
                    noPriceAvailableVisibility.setValue(true);
                } else {
                    headerTotalVisibility.setValue(true);
                    headerRentalRangeVisibility.setValue(true);
                    priceUnavailableVisibility.setValue(false);
                    headerTotalText.setValue(priceSummary.getEstimatedTotalView().getFormattedPrice(true));
                }
                selectThisClassButtonVisibility.setValue(true);
                callLocationButtonVisibility.setValue(false);
            } else {
                headerTotalVisibility.setValue(false);
                headerRentalRangeVisibility.setValue(false);
                noPriceAvailableVisibility.setValue(true);
            }
        } else {
            headerTotalVisibility.setValue(false);
            headerRentalRangeVisibility.setValue(false);
            priceUnavailableVisibility.setValue(true);

            selectThisClassButtonVisibility.setValue(false);
            callLocationButtonVisibility.setValue(true);
        }

    }

    public String getCorporateContractType() {
        return super.getCorporateContractType(isModify());
    }

    public EHIReservation getReservationObject() {
        if (isModify()) {
            return getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            return getManagers().getReservationManager().getCurrentReservation();
        }
    }

    @Override
    public boolean needShowPoints() {
        boolean showPoints = super.needShowPoints();
        if (mNeedShowPointsWrapper.getRawValue() == null) {
            mNeedShowPointsWrapper.setRawValue(showPoints);
        } else if (showPoints != mNeedShowPointsWrapper.getValue()) {
            mNeedShowPointsWrapper.setValue(showPoints);
        }
        return mNeedShowPointsWrapper.getValue();
    }

    @Override
    public void setNeedShowPoints(boolean showPoints) {
        super.setNeedShowPoints(showPoints);
        mNeedShowPointsWrapper.setValue(showPoints);
    }

    public void ePointsHeaderTopRightButtonClicked() {
        setNeedShowPoints(!needShowPoints());
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setCarClassAsSelected() {
        EHICarClassDetails ehiCarClassDetails = getCarClassDetails();

        getManagers().getReservationManager().addOrUpdateSelectedCarClass(ehiCarClassDetails);

        getManagers().getReservationManager().setSelectedCarClassCharges(ehiCarClassDetails.getCharge());

        AbstractRequestProvider request;
        if (isModify()) {
            request = new PostSelectCarClassModifyRequest(
                    getReservationObject().getResSessionId(), ehiCarClassDetails.getCode(), 0, getReservationObject().isPrepaySelected()
            );
        } else {
            request = new PostSelectCarClassRequest(getReservationObject().getResSessionId(), ehiCarClassDetails.getCode(), false);
        }

        showProgress(true);
        performRequest(request, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                showProgress(false);
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

    public EHIReservation getExtrasReservation() {
        return mCarClassResponse.getValue();
    }

    public void setExtrasReservation(EHIReservation reservation) {
        mCarClassResponse.setValue(reservation);
    }

}