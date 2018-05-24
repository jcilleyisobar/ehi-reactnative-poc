package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.Nullable;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.GetUpgradeDetailsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostUpdateExtrasRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostExtrasModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.ReactorBookRentalButtonState;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PREPAY;

@AutoUnbindAll
public class CarClassExtrasViewModel extends CountrySpecificViewModel {

    private static final long REQUEST_DELAY_TIME = 800;

    final ReactorBookRentalButtonState continueButton = new ReactorBookRentalButtonState();
    final ReactorVar<EHICarClassDetails> mCarClass = new ReactorVar<>();
    final ReactorVar<EHIReservation> mReservationObject = new ReactorVar<>();
    private boolean mUpgradeRequestInProgress = false;
    private Timer mTimer = new Timer();
    private List<EHIExtraItem> mPendingExtrasItems;
    private boolean mAnimatedOnce = false;
    private boolean mIsModify = false;
    private ReservationFlowListener mReservationFlowListener;
    private boolean mContinueButtonClicked = false;
    private boolean mReservationIsInvalid = false;
    private int mReservationObjectAge = 0;
    private ReservationFlowListener.PayState mPayState;

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        populateReservationObject();
    }

    public ReservationFlowListener.PayState getPayState() {
        return mPayState;
    }

    public void setPayState(final ReservationFlowListener.PayState payState) {
        mPayState = payState;
    }

    public EHICarClassDetails getCarClass() {
        return mCarClass.getValue();
    }

    @Nullable
    public EHICarClassDetails getCarClassDetails() {
        if (mReservationObject.getValue() != null) {
            return mReservationObject.getValue().getCarClassDetails();
        }
        return null;
    }

    public EHIReservation getReservationObject() {
        return mReservationObject.getValue();
    }

    public void populateReservationObject() {
        if (isModify()) {
            mReservationObject.setValue(getManagers().getReservationManager().getCurrentModifyReservation());
        } else {
            mReservationObject.setValue(getManagers().getReservationManager().getCurrentReservation());
        }

    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        populateReservationObject();
        if (mReservationObject.getRawValue() == null) {
            return;
        }
        setCarClass(mReservationObject.getRawValue().getCarClassDetails());
        mReservationObjectAge = 0;
        mContinueButtonClicked = false;
        if (mReservationIsInvalid) {
            final EHICarClassDetails upgrade = getManagers().getReservationManager().getCarUpgradeSelection();
            if (upgrade != null) {
                setCarClass(upgrade);
                populateReservationObject();
            }
            mReservationIsInvalid = false;
        } else {
            fetchUpgradeDetails(mReservationObjectAge);
        }
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        mReservationIsInvalid = true;
    }

    @Override
    public boolean needShowPoints() {
        return super.needShowPoints() && getPayState() != PREPAY;
    }

    private boolean shouldShowUpgrades() {
        int daysCount = mReservationObject.getRawValue().getCarClassDetails().getRedemptionDayCount();
        if (daysCount == 0) {
            daysCount = mReservationObject.getRawValue().getRedemptionDayCount();
        }
        return daysCount == 0 && mReservationObject.getRawValue().isUpgradeVechiclePossible();
    }

    private void fetchUpgradeDetails(final int reservationObjectAge) {
        if (!isModify() && shouldShowUpgrades()) {
            mUpgradeRequestInProgress = true;
            performRequest(new GetUpgradeDetailsRequest(getReservationObject().getResSessionId()), new IApiCallback<EHIReservation>() {
                @Override
                public void handleResponse(ResponseWrapper<EHIReservation> response) {
                    mUpgradeRequestInProgress = false;
                    //Quietly fail if upgrade not possible
                    //ReservationObjectAge is used to prevent from later clicked extras being overridden by earlier clicked extras (if not batched)
                    if (reservationObjectAge == mReservationObjectAge) {
                        if (response.isSuccess()) {
                            getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                        }
                        if (mContinueButtonClicked) {
                            continueClicked();
                        }
                    }
                }
            });
        }
    }

    public void setCarClass(EHICarClassDetails carClasses) {
        mCarClass.setValue(carClasses);
        updateContinueButton(mCarClass.getRawValue());
    }

    private void updateContinueButton(EHICarClassDetails carClassDetails) {
        switch (mPayState) {
            case REDEMPTION:
            case PAY_LATER:
                final EHIPriceSummary paylaterPriceSummary = carClassDetails.getPaylaterPriceSummary();
                if (paylaterPriceSummary == null) {
                    continueButton.setPrice(carClassDetails.getPaylaterChargePriceView());
                } else if (carClassDetails.isSecretRateAfterCarSelected()) {
                    continueButton.showNetRate();
                } else {
                    continueButton.setPrice(paylaterPriceSummary.getEstimatedTotalView().getFormattedPrice(true));
                }
                break;
            case PREPAY:
                final EHIPriceSummary prepayPriceSummary = carClassDetails.getPrepayPriceSummary();
                if (prepayPriceSummary == null) {
                    continueButton.setPrice(carClassDetails.getPrepayChargePriceView());
                } else if (prepayPriceSummary.getEstimatedTotalView() != null) {
                    continueButton.setPrice(prepayPriceSummary.getEstimatedTotalView().getFormattedPrice(true));
                }
                if (carClassDetails.getUnpaidRefundAmountPriceDifference(false) != null) {
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

                break;
        }

        continueButton.setProgress(false);
    }

    public void changeExtrasCount(EHIExtraItem item, int newCount) {
        continueButton.setEnabled(false);
        continueButton.setProgress(true);
        if (mPendingExtrasItems == null) {
            mPendingExtrasItems = getReservationObject().getExtras().getAllExtras();
        }

        for (int i = 0, size = mPendingExtrasItems.size(); i < size; i++) {
            final EHIExtraItem extra = mPendingExtrasItems.get(i);
            if (extra.getCode().equalsIgnoreCase(item.getCode())) {
                extra.setSelectedQuantity(newCount);
            }
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateExtras();
            }
        }, REQUEST_DELAY_TIME);
    }

    private void updateExtras() {
        if (mPendingExtrasItems == null) {
            return;
        }

        final String extras = getSelectedExtras(mPendingExtrasItems);

        final AbstractRequestProvider updateExtras;
        if (isModify()) {
            updateExtras = new PostExtrasModifyRequest(mReservationObject.getRawValue().getResSessionId(), mPendingExtrasItems);
        } else {
            updateExtras = new PostUpdateExtrasRequest(mReservationObject.getRawValue().getResSessionId(), mPendingExtrasItems);
        }

        performRequest(updateExtras, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                continueButton.setEnabled(true);
                if (response.isSuccess()) {
                    mReservationObjectAge++;
                    final EHIReservation reservation = response.getData();

                    if (isModify()) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(reservation);
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(reservation);
                    }

                    if (mPendingExtrasItems == null || extras.equals(getSelectedExtras(mPendingExtrasItems))) {
                        mPendingExtrasItems = null;
                        mReservationObject.setValue(reservation);
                        updateContinueButton(mReservationObject.getRawValue().getCarClassDetails());
                    } else {
                        updateExtras();
                    }
                    fetchUpgradeDetails(mReservationObjectAge);
                } else {
                    setError(response);
                }
            }
        });
    }

    private String getSelectedExtras(List<EHIExtraItem> items) {
        StringBuilder bld = new StringBuilder();
        for (int i = 0, size = items.size(); i < size; i++) {
            final EHIExtraItem item = items.get(i);
            bld.append(item.getCode());
            bld.append(item.getSelectedQuantity().intValue());
        }
        return bld.toString();
    }

    public EHIDriverInfo getDriverInfo() {
        if (isModify()) {
            return mReservationObject.getRawValue().getDriverInfo();
        } else {
            return getManagers().getReservationManager().getDriverInfo();
        }
    }

    public boolean isAnimatedOnce() {
        return mAnimatedOnce;
    }

    public void setAnimatedOnce(boolean animatedOnce) {
        mAnimatedOnce = animatedOnce;
    }

    public void setIsModify(Boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setReservationFlowListener(ReservationFlowListener reservationFlowListener) {
        mReservationFlowListener = reservationFlowListener;
    }

    public void continueClicked() {
        if (mReservationFlowListener == null) {
            return;
        }

        if (mUpgradeRequestInProgress && !mContinueButtonClicked) {
            mContinueButtonClicked = true;
            showProgress(true);
            return;
        }

        showProgress(false);
        String formattedPrice;
        if (!isUserLoggedIn() && getDriverInfo() == null) {
            if (getCarClassDetails().getPriceSummary() != null) {
                formattedPrice = getCarClassDetails().getPriceSummary().getEstimatedTotalView().getFormattedPrice(true).toString();
            } else {
                formattedPrice = "";
            }
            mReservationFlowListener.showDriverInfo(formattedPrice, getDriverInfo(), false);
        } else if (mReservationObject.getValue() != null
                && mReservationObject.getValue().getPickupLocation().isMultiTerminal()) {
            mReservationFlowListener.showMultiTerminal(mReservationObject.getValue().getPickupLocation().getEHIAirlineDetails(), false);
        } else {
            mReservationFlowListener.showReview();
        }
    }

    public void onTotalCostClicked() {
        if (mReservationFlowListener == null) {
            return;
        }

        if (!isUserLoggedIn() && getDriverInfo() == null) {
            mReservationFlowListener.showDriverInfo(
                    getCarClassDetails().getPriceSummary().getEstimatedTotalView().getFormattedPrice(true).toString(),
                    getDriverInfo(),
                    false
            );
        } else {
            mReservationFlowListener.showReview();
        }
    }

    public List<EHICharge> getSelectedCarClassCharges() {
        return getManagers().getReservationManager().getSelectedCarClassCharges();
    }

}