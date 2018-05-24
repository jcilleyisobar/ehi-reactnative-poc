package com.ehi.enterprise.android.ui.reservation;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICharge;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostSelectCarClassRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostSelectCarClassModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorEPointsHeaderState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorRateButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.NumberFormat;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ChooseYourRateViewModel extends CountrySpecificViewModel {

    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorViewState introductionMessageView = new ReactorViewState();
    final ReactorViewState spacementView = new ReactorViewState();
    final ReactorViewState triangleView = new ReactorViewState();
    final ReactorRateButtonState firstPaymentButton = new ReactorRateButtonState();
    final ReactorRateButtonState secondPaymentButton = new ReactorRateButtonState();
    final ReactorRateButtonState redeemPointsButton = new ReactorRateButtonState();
    final ReactorRateButtonState payInfoButton = new ReactorRateButtonState();
    final ReactorEPointsHeaderState ePointsHeader = new ReactorEPointsHeaderState();
    private EHICarClassDetails mCarClassDetails;
    final ReactorVar<EHICarClassDetails> mRequestedCarClassDetails = new ReactorVar<>();
    private final ReactorVar<GetMorePrepayTermsConditionsResponse> mTermsOfUse = new ReactorVar<>();
    private boolean isModify;
    private boolean mAnimatedOnce = false;
    private boolean mShouldShowPrepayIntroductionView = false;

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        mCarClassDetails = getManagers().getReservationManager().getSelectedCarClass();
        mShouldShowPrepayIntroductionView = shouldShowPrepayIntroductionMessage();
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.choose_your_rate_navigation_title);
        ePointsHeader.setVisibility(View.GONE);
        spacementView.setVisibility(ReactorViewState.VISIBLE);

        final List<EHICharge> ehiCharges = getManagers().getReservationManager().getSelectedCarClassCharges();
        final EHIPriceSummary prepayPriceSummary = mCarClassDetails.getPrepayPriceSummary();
        final EHIPriceSummary payLaterPriceSummary = mCarClassDetails.getPaylaterPriceSummary();

        final CharSequence prepayPrice = getFormattedPrice(EHICharge.getPrePayCharge(ehiCharges), prepayPriceSummary);
        final CharSequence payLaterPrice = getFormattedPrice(EHICharge.getPayLaterCharge(ehiCharges), payLaterPriceSummary);

        final boolean isPrepayAvailable = mCarClassDetails.isPrepayChargesAvailable();
        final boolean isPayLaterRateAvailable = mCarClassDetails.isPayLaterChargesAvailable();
        final boolean isLoggedIn = isUserLoggedIn();

        if (isNorthAmericaPrepayAvailable(isModify)) {
            setPayLaterButton(firstPaymentButton, payLaterPrice, isPayLaterRateAvailable);
            setPrepayButton(secondPaymentButton, prepayPrice, getPayNowSavingPrice(), isPrepayAvailable);
        } else {
            setPrepayButton(firstPaymentButton, prepayPrice, getPayNowSavingPrice(), isPrepayAvailable);
            setPayLaterButton(secondPaymentButton, payLaterPrice, isPayLaterRateAvailable);
        }

        setPrepayIntroductionMessageView();

        //redemption
        if (!isLoggedIn) {
            redeemPointsButton.setVisibility(View.GONE);
        } else {
            updateRedemptionState(isPayLaterRateAvailable);
        }

    }

    public ReservationFlowListener.PayState getPayStateForFirstButton() {
        return isNorthAmericaPrepayAvailable(isModify) ? ReservationFlowListener.PayState.PAY_LATER : ReservationFlowListener.PayState.PREPAY;
    }

    public ReservationFlowListener.PayState getPayStateForSecondButton() {
        return isNorthAmericaPrepayAvailable(isModify) ? ReservationFlowListener.PayState.PREPAY : ReservationFlowListener.PayState.PAY_LATER;
    }

    private void setPayLaterButton(ReactorRateButtonState button, CharSequence price, boolean isPayLaterRateAvailable) {
        button.setVisibility(View.VISIBLE);
        button.setSecondaryText(getResources().getString(R.string.reservation_pay_later_cancel_message));
        button.setPrimaryText(getResources().getString(R.string.choose_your_rate_pay_later_title));
        button.setSavingPriceVisibility(false);
        if (isPayLaterRateAvailable) {
            button.setPrice(price);
        } else {
            button.setEnabled(false);
            button.setPriceSubtitle(getResources().getString(R.string.choose_your_rate_prepay_unavailable));
            button.setArrow(false);
            button.setPrice("");
        }
    }

    private void setPrepayButton(ReactorRateButtonState button, CharSequence price, CharSequence savingPrice, boolean isPrepayAvailable) {
        button.setVisibility(View.VISIBLE);
        button.setPrimaryText(getResources().getString(R.string.choose_your_rate_pay_now_title));
        button.setSecondaryText(getResources().getString(R.string.reservation_pay_now_cancel_message));

        if (isPrepayAvailable) {
            button.setPrice(price);
            if (savingPrice != null) {
                button.setSavingPrice(savingPrice);
            }
        } else {
            button.setEnabled(false);
            button.setPriceSubtitle(getResources().getString(R.string.choose_your_rate_prepay_unavailable));
            button.setArrow(false);
            button.setPrice("");
            button.setSavingPriceVisibility(false);
        }
    }

    private void setPrepayIntroductionMessageView() {
        if (mShouldShowPrepayIntroductionView) {
            introductionMessageView.setVisibility(ReactorViewState.VISIBLE);
            spacementView.setVisibility(ReactorViewState.GONE);
            getManagers().getLocalDataManager().setPrepayIntroductionMessageShown();
        } else {
            triangleView.setBackgroundColor(getResources().getColor(R.color.location_info_window_divider_grey));
            introductionMessageView.setVisibility(ReactorViewState.GONE);
        }
    }

    private boolean shouldShowPrepayIntroductionMessage() {
        return getManagers().getLocalDataManager().shouldShowPrepayIntroducingMessage()
                && isNorthAmericaPrepayAvailable(isModify);
    }

    private CharSequence getFormattedPrice(EHICharge ehiCharge, EHIPriceSummary ehiPriceSummary) {
        if (ehiCharge != null && ehiCharge.getPriceView() != null) {
            return ehiCharge.getPriceView().getFormattedPrice(false);
        }

        if (ehiPriceSummary != null && ehiPriceSummary.getEstimatedTotalView() != null) {
            return ehiPriceSummary.getEstimatedTotalView().getFormattedPrice(false);
        }

        return "";
    }

    private void updateRedemptionState(final boolean isPayLaterAvailable) {
        if (!mCarClassDetails.isRedemptionAvailable() || !isPayLaterAvailable) {
            redeemPointsButton.setPriceSubtitle(getResources().getString(R.string.choose_your_rate_prepay_unavailable));
            redeemPointsButton.setSecondaryText("");
            redeemPointsButton.setEnabled(false);
            redeemPointsButton.setArrow(false);
            redeemPointsButton.setShouldShowWarningIcon(false);
            redeemPointsButton.setPrice("");
            return;
        }

        final EHILoyaltyData ehiLoyaltyData = getManagers().getLoginManager().getProfileCollection().getBasicProfile().getLoyaltyData();
        final long pointsToDate = ehiLoyaltyData != null ? ehiLoyaltyData.getPointsToDate() : 0;

        redeemPointsButton.setPriceSubtitle(getResources().getString(R.string.choose_your_rate_points_per_day));
        redeemPointsButton.setPrice(NumberFormat.getInstance().format(mCarClassDetails.getRedemptionPoints()));
        redeemPointsButton.setShouldShowWarningIcon(false);

        if (pointsToDate > mCarClassDetails.getRedemptionPoints()) {
            redeemPointsButton.setVisibility(View.VISIBLE);
            redeemPointsButton.setEnabled(true);
            redeemPointsButton.setSecondaryText(getRedeemPointsSecondaryText(pointsToDate, true));
        } else {
            redeemPointsButton.setEnabled(false);
            redeemPointsButton.setArrow(false);
            redeemPointsButton.setSecondaryText(getRedeemPointsSecondaryText(pointsToDate, false));
        }
    }

    @NonNull
    private SpannableStringBuilder getRedeemPointsSecondaryText(long pointsToDate, boolean enabled) {
        SpannableStringBuilder bld = new SpannableStringBuilder();

        SpannableString yourPoints = new SpannableString(getResources().getString(R.string.choose_your_rate_redeem_points_unit) + ": ");

        yourPoints.setSpan(
                new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_light)),
                0,
                yourPoints.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        bld.append(yourPoints);

        SpannableString points = new SpannableString(getFormattedPoints(pointsToDate));
        points.setSpan(new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_bold)), 0, points.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!enabled) {
            points.setSpan(new ForegroundColorSpan(Color.GRAY), 0, points.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        bld.append(points);
        return bld;
    }

    private String getFormattedPoints(long pointsToDate) {
        return NumberFormat.getNumberInstance().format(pointsToDate);
    }

    public void setCarClassDetails(final EHICarClassDetails carClassDetails) {
        mCarClassDetails = carClassDetails;
    }

    public EHICarClassDetails getCarClassDetails() {
        return mCarClassDetails;
    }

    public void setModify(final boolean modify) {
        isModify = modify;
    }

    public EHIReservation getReservation() {
        return isModify ? getManagers().getReservationManager().getCurrentModifyReservation()
                : getManagers().getReservationManager().getCurrentReservation();
    }

    public void requestCarClassDetails(ReservationFlowListener.PayState payState) {
        showProgress(true);
        AbstractRequestProvider<EHIReservation> selectCarClassRequest;
        if (isModify) {
            selectCarClassRequest = new PostSelectCarClassModifyRequest(
                    getReservation().getResSessionId(),
                    mCarClassDetails.getCode(),
                    0,
                    payState.equals(ReservationFlowListener.PayState.PREPAY)
            );
        } else {
            selectCarClassRequest = new PostSelectCarClassRequest(
                    getReservation().getResSessionId(),
                    mCarClassDetails.getCode(),
                    false,
                    0,
                    payState.equals(ReservationFlowListener.PayState.PREPAY)
            );
        }

        performRequest(selectCarClassRequest, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(final ResponseWrapper<EHIReservation> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    mRequestedCarClassDetails.setValue(response.getData().getCarClassDetails());
                    if (isModify) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                    }
                } else {
                    setError(response);
                }
            }
        });
    }

    public EHICarClassDetails getRequestedCarClassDetails() {
        return mRequestedCarClassDetails.getValue();
    }

    public void setRequestedCarClassDetails(final EHICarClassDetails requestedCarClassDetails) {
        mRequestedCarClassDetails.setValue(requestedCarClassDetails);
    }

    public boolean isAnimatedOnce() {
        return mAnimatedOnce;
    }

    public void setAnimatedOnce(boolean animatedOnce) {
        mAnimatedOnce = animatedOnce;
    }

    public CharSequence getPayNowSavingPrice() {
        final String prepayPriceDifference = mCarClassDetails.getPrePayPriceDifference();
        if (prepayPriceDifference == null) {
            return null;
        }
        return new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.reservation_pay_now_savings)
                .addTokenAndValue(EHIStringToken.AMOUNT, prepayPriceDifference)
                .format();
    }

    public boolean isEuropeanAddress() {
        return getManagers().getLocalDataManager().isEuropeanAddress(LocalDataManager.getInstance().getPreferredCountryCode());
    }

    public void requestTermsOfUse() {
        showProgress(true);
        performRequest(new GetMorePrepayTermsConditionsRequest(getManagers().getLocalDataManager().getPreferredCountryCode()), new IApiCallback<GetMorePrepayTermsConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetMorePrepayTermsConditionsResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    mTermsOfUse.setValue(response.getData());
                } else {
                    setError(response);
                }
            }
        });
    }

    public void setTermsOfUse(GetMorePrepayTermsConditionsResponse terms) {
        mTermsOfUse.setValue(terms);
    }

    public GetMorePrepayTermsConditionsResponse getTermsOfUse() {
        return mTermsOfUse.getValue();
    }
}