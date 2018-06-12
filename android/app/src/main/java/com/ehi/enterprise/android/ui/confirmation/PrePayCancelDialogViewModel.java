package com.ehi.enterprise.android.ui.confirmation;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICancellation;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.ReactorVar;

public class PrePayCancelDialogViewModel extends CountrySpecificViewModel {

    ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    final ReactorVar<String> mPrepayTermsAndConditions = new ReactorVar<>();
    public ReactorTextViewState cancelTitleViewState = new ReactorTextViewState();
    public ReactorTextViewState cancellationFeeViewState = new ReactorTextViewState();
    public ReactorTextViewState refundedAmountViewState = new ReactorTextViewState();
    public ReactorTextViewState originalAmountViewState = new ReactorTextViewState();
    public ReactorTextViewState refundedAsViewState = new ReactorTextViewState();
    public ReactorTextViewState travelingBetweenUsAndCanadaViewState = new ReactorTextViewState();
    private EHICancellation mEhiCancelation;
    private Boolean mIsModify;
    private String mOriginalAmount;

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

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (mEhiCancelation == null) {
            return;
        }

        if (getOriginalAmount() != null) {
            originalAmountViewState.setText(getOriginalAmount());
        }

        if (isTravelingBetweenUSAndCanada()) {
            final CharSequence formattedRefundAmount = getRefundAmountText();
            final CharSequence formattedConversionTitle = getConversionTitle();

            travelingBetweenUsAndCanadaViewState.setVisibility(View.VISIBLE);
            refundedAsViewState.setVisibility(View.VISIBLE);

            refundedAsViewState.setText(formattedRefundAmount);
            travelingBetweenUsAndCanadaViewState.setText(formattedConversionTitle);
        } else {
            travelingBetweenUsAndCanadaViewState.setVisibility(View.GONE);
            refundedAsViewState.setVisibility(View.GONE);
        }

        final String cancelFee = mEhiCancelation.getCancelFeeAmountView().getFormattedPrice(true).toString();
        this.cancelTitleViewState.setText(getTitle());

        final boolean isCancellationFeeNotZero = Double.valueOf(0.0).equals(mEhiCancelation.getCancelFeeAmountView().getDoubleAmmount());
        cancellationFeeViewState.setText(isCancellationFeeNotZero ? cancelFee :
                        getResources().getString(R.string.text_with_negative_prefix, cancelFee));


        refundedAmountViewState.setText(mEhiCancelation.getRefundAmountView().getFormattedPrice(false));
    }

    public CharSequence getOriginalAmount() {
        if (isTravelingBetweenUSAndCanada()) {
            return mEhiCancelation.getCancelFeeDetail().getOriginalAmountView().getFormattedPrice(false);
        } else {
            return mOriginalAmount;
        }

    }

    public CharSequence getConversionTitle() {
        return new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.reservation_currency_conversion_title)
                        .addTokenAndValue(EHIStringToken.CURRENCY_CODE, mEhiCancelation.getCancelFeeDetail().getRefundAmountPayment().getCurrencyCode())
                        .format();
    }

    public CharSequence getRefundAmountText() {
        return new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(R.string.reservation_cancel_currency_refund)
                        .addTokenAndValue(EHIStringToken.REFUND, mEhiCancelation.getCancelFeeDetail().getRefundAmountPayment().getFormattedPrice(false))
                        .format();
    }

    public CharSequence getTitle() {
        final CharSequence formattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(isNorthAmerica() ? R.string.confirmation_cancel_reservation_message : R.string.reservation_cancel_message_details)
                .addTokenAndValue(EHIStringToken.AMOUNT, mEhiCancelation.getCancelFeeAmountView().getFormattedPrice(false))
                .format();

        SpannableString cancelPrepayTerms = new SpannableString(getResources().getString(R.string.confirmation_cancel_reservation_terms));
        cancelPrepayTerms.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, cancelPrepayTerms.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(formattedTitle)
                .addTokenAndValue(EHIStringToken.TERMS, cancelPrepayTerms)
                .format();
    }

    public ResponseWrapper getErrorWrapper() {
        return mErrorWrapper.getValue();
    }

    public void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    public void setPrepayTermsAndConditions(String prepayTermsAndConditions) {
        mPrepayTermsAndConditions.setValue(prepayTermsAndConditions);
    }

    public boolean isTravelingBetweenUSAndCanada() {
        return EHIPrice.arePricesUSAndCanadaCurrency(getAmountPayment(), getAmountView());
    }

    @Nullable
    public EHIPrice getAmountPayment() {
        if (mEhiCancelation != null && mEhiCancelation.getCancelFeeDetail() != null) {
            return mEhiCancelation.getCancelFeeDetail().getOriginalAmountPayment();
        }
        return null;
    }

    @Nullable
    public EHIPrice getAmountView() {
        if (mEhiCancelation != null && mEhiCancelation.getCancelFeeDetail() != null) {
             return mEhiCancelation.getCancelFeeDetail().getOriginalAmountView();
        }
        return null;
    }

    public String getPrepayTermsAndConditions() {
        return mPrepayTermsAndConditions.getValue();
    }

    public void setEHICancelation(EHICancellation EHICancelation) {
        this.mEhiCancelation = EHICancelation;
    }

    public void setIsModify(Boolean isModify) {
        this.mIsModify = isModify;
    }

    public void setOriginalAmount(String originalAmount) {
        this.mOriginalAmount = originalAmount;
    }
}