package com.ehi.enterprise.android.ui.reservation.widget;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIMileageInfo;
import com.ehi.enterprise.android.models.reservation.EHIPaymentLineItem;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class PriceSummaryViewModel extends ManagersAccessViewModel {

    private EHICarClassDetails mCarClassDetails;

    private boolean mIsPrepay;
    private boolean mIsModify;

    //region reactive vars/states
    final ReactorViewState mileageArea = new ReactorViewState();

    final ReactorTextViewState estimatedTotalLabelText = new ReactorTextViewState();
    final ReactorTextViewState estimatedTotalText = new ReactorTextViewState();
    final ReactorTextViewState estimatedTotalTextModify = new ReactorTextViewState();

    final ReactorTextViewState totalPaidAmountLabelText = new ReactorTextViewState();
    final ReactorTextViewState totalPaidAmountDescLabelText = new ReactorTextViewState();
    final ReactorTextViewState totalPaidAmount = new ReactorTextViewState();
    final ReactorTextViewState totalPaidAmountModify = new ReactorTextViewState();

    final ReactorTextViewState totalRefundOweAmountLabelText = new ReactorTextViewState();
    final ReactorTextViewState totalRefundOweAmountDescLabelText = new ReactorTextViewState();
    final ReactorTextViewState totalRefundOweAmount = new ReactorTextViewState();
    final ReactorTextViewState actualAmountText = new ReactorTextViewState();
    final ReactorTextViewState travelingUsCanadaClarificationText = new ReactorTextViewState();

    final ReactorViewState estimatedTotalView = new ReactorViewState();
    final ReactorViewState totalPaidAmountView = new ReactorViewState();

    final ReactorViewState estimatedTotalModifyView = new ReactorViewState();
    final ReactorViewState totalPaidAmountModifyView = new ReactorViewState();

    final ReactorViewState totalPaidView = new ReactorViewState();
    final ReactorViewState totalRefundOweView = new ReactorViewState();

    final ReactorViewState conversionArea = new ReactorViewState();
    final ReactorTextViewState conversionText = new ReactorTextViewState();
    final ReactorTextViewState conversionTotal = new ReactorTextViewState();

    final ReactorViewState promoRateArea = new ReactorViewState();
    final ReactorTextViewState promoRateText = new ReactorTextViewState();
    final ReactorViewState promoRateTriangle = new ReactorViewState();

    final ReactorVar<EHIPaymentLineItem> redemptionLineItem = new ReactorVar<>();
    final ReactorVar<EHIMileageInfo> mileageInfo = new ReactorVar<>();
    final ReactorVar<List<EHIPaymentLineItem>> feesLineItems = new ReactorVar<>();

    final ReactorVar<EHIReservation> mReservation = new ReactorVar<>();

    //endregion

    public void setReservation(EHIReservation ehiReservation) {
        this.mReservation.setValue(ehiReservation);

        if (ehiReservation == null) {
            return;
        }

        setCarClass(ehiReservation.getCarClassDetails());
    }

    public EHIReservation getReservation() {
        return mReservation.getValue();
    }

    public void setCarClass(EHICarClassDetails carClassDetails) {
        mCarClassDetails = carClassDetails;

        final EHIPriceSummary priceSummary = getEhiPriceSummary();
        if (priceSummary == null) {
            return;
        }

        updateMileageInfo();
        updateTotalArea(priceSummary);
        updatePromoRateViewState();
    }

    private EHIPriceSummary getEhiPriceSummary() {
        if (mIsPrepay) {
            return mCarClassDetails.getPrepayPriceSummary();
        }

        return mCarClassDetails.getPaylaterPriceSummary();
    }

    private void updateMileageInfo() {
        EHIMileageInfo info = mCarClassDetails.getMileageInfo();
        if (info != null) {
            mileageArea.setVisibility(ReactorViewState.VISIBLE);
            mileageInfo.setValue(info);
        } else {
            mileageArea.setVisibility(ReactorViewState.GONE);
            mileageInfo.setValue(null);
        }
    }

    private void updateTotalArea(EHIPriceSummary priceSummary) {
        if (mIsPrepay && mCarClassDetails.isPrepayRateAvailable()) {
            if (isModify()
                    && mCarClassDetails.getUnpaidRefundAmountPriceDifference(true) != null
                    && getReservation().getPreviousReservationTotal() != null) {
                estimatedTotalView.setVisibility(ReactorViewState.GONE);
                totalPaidAmountView.setVisibility(ReactorViewState.GONE);
                estimatedTotalModifyView.setVisibility(ReactorViewState.VISIBLE);
                totalPaidAmountModifyView.setVisibility(ReactorViewState.VISIBLE);

                estimatedTotalLabelText.setText(R.string.review_payment_updated_total_title);
                estimatedTotalTextModify.setText(formatPrice(mCarClassDetails.getPrepayPriceSummary().getEstimatedTotalView().getFormattedPrice(false).toString()));

                totalPaidView.setVisibility(ReactorViewState.VISIBLE);
                totalPaidAmountLabelText.setText(R.string.review_payment_paid_amount_title);
                totalPaidAmountDescLabelText.setText(R.string.review_payment_original_total_title);
                totalPaidAmountModify.setText(formatPrice("-" + getReservation().getPreviousReservationTotal()));

                totalRefundOweView.setVisibility(ReactorViewState.VISIBLE);

                final boolean isRefund = !mCarClassDetails.isUnpaidRefundAmountPriceDifferenceNegative();
                totalRefundOweAmountLabelText.setText(!isRefund ? R.string.review_payment_unpaid_amount_title
                        : R.string.review_payment_refund_amount_title);
                totalRefundOweAmountDescLabelText.setText(mCarClassDetails.isUnpaidRefundAmountPriceDifferenceNegative() ? R.string.review_payment_unpaid_at_end_title
                        : R.string.review_payment_refunded_at_end_title);
                totalRefundOweAmount.setText(mCarClassDetails.getUnpaidRefundAmountPriceDifference(true));

                final boolean isTravelingBetweenUSAndCanada = EHIPrice.arePricesUSAndCanadaCurrency(mCarClassDetails.getDifferenceAmountView(), mCarClassDetails.getDifferenceAmountPayment());
                if (isTravelingBetweenUSAndCanada) {
                    final CharSequence destinationCurrencyPayment = mCarClassDetails.getUnpaidRefundAmountPaymentPrice();

                    final CharSequence textFormatted = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.reservation_currency_refund)
                            .addTokenAndValue(EHIStringToken.REFUND, destinationCurrencyPayment)
                            .format();

                    final CharSequence refundExplanationFormatted = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.reservation_currency_conversion_title)
                            .addTokenAndValue(EHIStringToken.CURRENCY_CODE, mCarClassDetails.getDifferenceAmountPayment().getCurrencyCode())
                            .format();

                    actualAmountText.setVisibility(View.VISIBLE);
                    travelingUsCanadaClarificationText.setVisibility(View.VISIBLE);
                    actualAmountText.setText(textFormatted);
                    travelingUsCanadaClarificationText.setText(refundExplanationFormatted);
                } else {
                    actualAmountText.setVisibility(View.GONE);
                    travelingUsCanadaClarificationText.setVisibility(View.GONE);
                }
            } else {
                estimatedTotalLabelText.setText(R.string.reservation_review_prepay_total_title);
                estimatedTotalText.setText(mCarClassDetails.getPrepayPriceSummary().getEstimatedTotalView().getFormattedPrice(true));
            }
        } else {
            estimatedTotalLabelText.setText(R.string.reservation_review_estimated_total_title);
            if (mCarClassDetails.isSecretRateAfterCarSelected()) {
                estimatedTotalTextModify.setText(getResources().getString(R.string.reservation_price_unavailable));
            } else {
                estimatedTotalText.setText(mCarClassDetails.getPaylaterPriceSummary().getEstimatedTotalView().getFormattedPrice(true));
            }
        }

        int message = 0;
        if (mIsPrepay && priceSummary.isTravelingBetweenUSAndCanada()) {
            message = R.string.car_class_details_transparency_total_na;
        } else if (priceSummary.isDifferentPaymentCurrency()) {
            message = R.string.car_class_details_transparency_total;
        }

        if (message != 0) {
            conversionArea.setVisibility(ReactorViewState.VISIBLE);
            CharSequence formattedString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(message)
                    .addTokenAndValue(EHIStringToken.CURRENCY_CODE, priceSummary.getEstimatedTotalPayment().getCurrencyCode())
                    .format();
            conversionText.setText(formattedString);
            if (mCarClassDetails.isSecretRateAfterCarSelected()) {
                conversionTotal.setText(getResources().getString(R.string.reservation_price_unavailable));
            } else {
                conversionTotal.setText(priceSummary.getEstimatedTotalPayment().getFormattedPrice(false).toString());
            }
        } else {
            conversionArea.setVisibility(ReactorViewState.GONE);
        }
    }

    public SpannableStringBuilder formatPrice(CharSequence totalPrice) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.source_sans_light);
        SpannableString spannableString = new SpannableString(totalPrice);
        spannableString.setSpan(
                new CustomTypefaceSpan("", typeface), 0, spannableString.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannableStringBuilder.append(spannableString);
        return spannableStringBuilder;
    }

    public void setIsPrepay(boolean isPrepay) {
        mIsPrepay = isPrepay;
    }

    public boolean isPrepay() {
        return mIsPrepay;
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    private void updatePromoRateViewState() {
        EHIReservation reservation = mReservation.getRawValue();
        if (reservation != null
                && reservation.getCarClassDetails() != null) {
            if (mIsPrepay) {
                promoRateArea.setVisibility(View.GONE);
                promoRateTriangle.setVisibility(View.GONE);
            } else if (EHICarClassDetails.AVAILABLE_AT_CONTRACT_RATE.equalsIgnoreCase(reservation.getCarClassDetails().getStatus())) {
                promoRateArea.setVisibility(View.VISIBLE);
                promoRateTriangle.setVisibility(View.VISIBLE);
                promoRateText.setText(R.string.car_class_cell_negotiated_rate_title);
            } else if (EHICarClassDetails.AVAILABLE_AT_PROMOTIONAL_RATE.equalsIgnoreCase(reservation.getCarClassDetails().getStatus())) {
                promoRateArea.setVisibility(View.VISIBLE);
                promoRateTriangle.setVisibility(View.VISIBLE);
                promoRateText.setText(R.string.car_class_cell_promotional_rate_title);
            } else {
                promoRateArea.setVisibility(View.GONE);
                promoRateTriangle.setVisibility(View.GONE);
            }
        } else {
            promoRateArea.setVisibility(View.GONE);
            promoRateTriangle.setVisibility(View.GONE);
        }
    }

    public String getAccountId() {
        if (mReservation.getValue() != null
                && mReservation.getValue().getCorporateAccount() != null
                && mReservation.getValue().getCorporateAccount().getContractNumber() != null
                && !mReservation.getValue().getCorporateAccount().getContractNumber().isEmpty()) {
            return mReservation.getValue().getCorporateAccount().getContractNumber();
        } else {
            return null;
        }
    }
}