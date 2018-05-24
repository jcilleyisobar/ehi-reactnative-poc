package com.ehi.enterprise.android.ui.reservation.redemption;

import android.support.annotation.StringRes;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.GetCarClassDetailsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectCarClassRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectCarClassModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.CarClassDetailsResponse;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class RedemptionViewModel extends ManagersAccessViewModel {

    public static final String TAG = "RedemptionViewModel";
    public static final int REQUEST_DELAY = 800;

    final ReactorVar<Integer> title = new ReactorVar<>();
    public final ReactorVar<ResponseWrapper> mSuccessResponse = new ReactorVar<>();
    public final ReactorVar<Integer> days = new ReactorVar<>(0);
    public final ReactorVar<Long> currentPoints = new ReactorVar<>();
    public final ReactorVar<Integer> pointsPerFreeDay = new ReactorVar<>(0);
    public final ReactorVar<String> pointsSpentText = new ReactorVar<>("");
    public final ReactorVar<CharSequence> pointsPerFreeDayText = new ReactorVar<>();
    public final ReactorVar<Boolean> minusButtonEnabled = new ReactorVar<>(true);
    public final ReactorVar<Boolean> plusButtonEnabled = new ReactorVar<>(true);
    public final ReactorVar<Boolean> maxReached = new ReactorVar<>(false);
    public final ReactorVar<String> stepperText = new ReactorVar<>("");
    public final ReactorVar<CharSequence> bookRentalPrice = new ReactorVar<>();
    public final ReactorVar<Boolean> bookRentalButtonEnabled = new ReactorVar<>(true);
    public final ReactorVar<String> bookButtonText = new ReactorVar<>();
    public final ReactorVar<String> bookRentalButtonSubtitle = new ReactorVar<>();
    public final ReactorVar<CharSequence> originalTotal = new ReactorVar<>();
    public final ReactorVar<EHICarClassDetails> classDetails = new ReactorVar<>();
    public final ReactorVar<CharSequence> creditValue = new ReactorVar<>();

    private int mMaxDays = Integer.MAX_VALUE;
    private EHIPriceSummary mPriceSummary;
    private EHICarClassDetails mOriginalPriceClassDetails;
    private Timer mRequestTimer;
    private boolean mFromChooseYourRate;
    private boolean firstLoad = true;
    private boolean mIsModify;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (classDetails.getValue() == null || mPriceSummary == null) {
            DLog.e(TAG, "RedemptionViewModel requires an EHICarClassDetails and an EHIPriceSummary!");
            return;
        }
        title.setValue(R.string.redemption_navigation_title);
        bookButtonText.setValue(getResources().getString(R.string.redemption_footer_title));
        bookRentalButtonSubtitle.setValue(getResources().getString(R.string.reservation_price_subtitle_total_cost));
    }

    public void updateCurrentPoints() {
        final EHILoyaltyData ehiLoyaltyData = getManagers().getLoginManager().getProfileCollection().getBasicProfile().getLoyaltyData();
        if (ehiLoyaltyData != null) {
            currentPoints.setValue(ehiLoyaltyData.getPointsToDate());
        }
    }

    public void setCarClass(EHICarClassDetails classDetails, boolean shouldFetchPrice) {
        this.classDetails.setValue(classDetails);
        mMaxDays = classDetails.getMaxRedemptionDays();
        setPointsPerFreeDay((int) classDetails.getRedemptionPoints());
        setDays(firstLoad && classDetails.getRedemptionDayCount() == 0 ? mMaxDays : classDetails.getRedemptionDayCount(), shouldFetchPrice);
        firstLoad = false;
        updateCredit();
        updateOriginalTotal();
        setPointsSpent(classDetails.getEplusPointsUsed());
        setPriceSummary(this.classDetails.getRawValue().getPaylaterPriceSummary());
    }

    public EHICarClassDetails getClassDetails() {
        return classDetails.getValue();
    }

    public void setPriceSummary(EHIPriceSummary priceSummary) {
        if (priceSummary.getEstimatedTotalView() != null) {
            mPriceSummary = priceSummary;
            setBookRentalPrice(mPriceSummary.getEstimatedTotalView().getFormattedPrice(true));
            updateCredit();
        }
    }

    private void updateCredit() {
        String daysString;
        if (days.getRawValue() == 1) {
            daysString = getResources().getString(R.string.reservation_rate_daily_unit);
        } else {
            daysString = getResources().getString(R.string.reservation_rate_daily_unit_plural);
        }
        creditValue.setValue("(-" + days.getRawValue() + " " + daysString + ")");
    }

    private void updateOriginalTotal() {
        EHIReservation currentReservation = getReservationObject();
        if (mOriginalPriceClassDetails == null) {
            if (currentReservation == null) {
                return;
            }
            if (classDetails.getRawValue().getRedemptionDayCount() == 0) {
                mOriginalPriceClassDetails = currentReservation.getCarClassDetails();
                updateOriginalTotal();//recursive call
            } else {
                performRequest(new GetCarClassDetailsRequest(currentReservation.getResSessionId(),
                                classDetails.getValue().getCode(),
                                0),
                        new IApiCallback<CarClassDetailsResponse>() {
                            @Override
                            public void handleResponse(ResponseWrapper<CarClassDetailsResponse> response) {
                                if (response.isSuccess()) {
                                    mOriginalPriceClassDetails = response.getData().getDetails();
                                    updateOriginalTotal();//recursive call
                                }
                            }
                        });
            }
        } else {
            if (mOriginalPriceClassDetails.getPriceSummary() != null) {
                originalTotal.setValue(mOriginalPriceClassDetails
                        .getPaylaterPriceSummary()
                        .getEstimatedTotalView()
                        .getFormattedPrice(true));
            }
        }

    }

    @StringRes
    public int getTitle() {
        return R.string.redemption_navigation_title;
    }

    private void setBookRentalPrice(CharSequence bookRentalPrice) {
        this.bookRentalPrice.setValue(bookRentalPrice);
    }

    public void minusClicked() {
        setDays(days.getRawValue() - 1, true);
    }

    public void plusClicked() {
        setDays(days.getRawValue() + 1, true);
    }

    private void setPointsSpent(int pointsSpent) {
        pointsSpentText.setValue(NumberFormat.getNumberInstance().format(pointsSpent));
    }

    private void setPointsPerFreeDay(int pointsPerFreeDay) {
        this.pointsPerFreeDay.setValue(pointsPerFreeDay);

        pointsPerFreeDayText.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.redemption_picker_cell_subtitle)
                .addTokenAndValue(EHIStringToken.POINTS,
                        NumberFormat.getNumberInstance().format(this.pointsPerFreeDay.getValue()))
                .format());
    }

    private void setDays(final int daysValue, boolean shouldFetchPrice) {
        if (0 <= daysValue && daysValue <= mMaxDays) {     // Make sure the user can't go outside the redemption bounds
            days.setValue(daysValue);
            maxReached.setValue(false);
            stepperText.setValue(String.format("%s %d",
                    getResources().getString(R.string.redemption_picker_cell_stepper_title),
                    getDays()));

            minusButtonEnabled.setValue(days.getRawValue() > 0);
            plusButtonEnabled.setValue(days.getRawValue() < mMaxDays);    // Disable the button if the user has reached maximum redemption days


            if (mRequestTimer != null) {
                mRequestTimer.cancel();
                mRequestTimer.purge();
                mRequestTimer = null;
            }

            if (shouldFetchPrice) {
                bookRentalButtonEnabled.setValue(false);
                showProgress(true);
                mRequestTimer = new Timer();
                mRequestTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        postRedemptionDays();
                    }
                }, REQUEST_DELAY);
            }

        } else if (daysValue > mMaxDays) {
            maxReached.setValue(true);     // If they try, let them know
        }
    }

    private void postRedemptionDays() {
        EHIReservation currentReservation = getReservationObject();
        if (currentReservation == null) {
            return;
        }
        showProgress(true);
        performRequest(new GetCarClassDetailsRequest(currentReservation.getResSessionId(),
                        classDetails.getValue().getCode(),
                        days.getRawValue()),
                new IApiCallback<CarClassDetailsResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<CarClassDetailsResponse> response) {
                        if (response.isSuccess()) {
                            if (days.getRawValue() == response.getData().getDetails().getRedemptionDayCount()) {
                                setPriceSummary(response.getData().getDetails().getVehicleRates().get(0).getPriceSummary());
                                setCarClass(response.getData().getDetails(), false);
                                updateCredit();
                                showProgress(false);
                            }
                        } else {
                            showProgress(false);
                            setError(response);
                        }
                        if (!response.isSuccess()
                                || days.getRawValue() == response.getData().getDetails().getRedemptionDayCount()) {
                            bookRentalButtonEnabled.setValue(true);
                            bookRentalButtonSubtitle.setValue(getResources().getString(R.string.redemption_price_subtitle_after_points));
                        }
                    }
                });
    }

    public void continueToRegistration() {
        showProgress(true);
        bookRentalButtonEnabled.setValue(false);
        AbstractRequestProvider request;
        if (isModify()) {
            request = new PostSelectCarClassModifyRequest(getReservationObject().getResSessionId(),
                    classDetails.getValue().getCode(),
                    days.getRawValue(),
                    getReservationObject().isPrepaySelected());
        } else {
            request = new PostSelectCarClassRequest(getReservationObject().getResSessionId(),
                    classDetails.getValue().getCode(),
                    true,
                    days.getRawValue());
        }
        performRequest(request,
                new IApiCallback<EHIReservation>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIReservation> response) {
                        if (response.isSuccess()) {
                            if (isModify()) {
                                getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                            } else {
                                getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                            }
                            mSuccessResponse.setValue(response);
                        } else {
                            setError(response);
                        }
                        showProgress(false);
                        bookRentalButtonEnabled.setValue(true);
                    }
                });
    }

    private int getDays() {
        return days.getValue();
    }

    public ReactorVar<ResponseWrapper> getSuccessResponse() {
        return mSuccessResponse;
    }

    public void setFromChooseYourRate(final boolean fromChooseYourRate) {
        mFromChooseYourRate = fromChooseYourRate;
    }

    public boolean isFromChooseYourRate() {
        return mFromChooseYourRate;
    }

    public void setIsModify(Boolean isModify) {
        mIsModify = isModify != null && isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public ReservationFlowListener.PayState getPayState() {
        if (days.getRawValue() == 0) {
            return ReservationFlowListener.PayState.PAY_LATER;
        }

        return ReservationFlowListener.PayState.REDEMPTION;
    }

    public EHIReservation getReservationObject() {
        if (isModify()) {
            return getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            return getManagers().getReservationManager().getCurrentReservation();
        }
    }
}
