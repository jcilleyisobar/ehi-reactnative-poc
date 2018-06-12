package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.EHIPatterns;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.payment.CreditCard;
import com.ehi.enterprise.android.utils.payment.CreditCardManager;
import com.ehi.enterprise.android.utils.payment.interfaces.IOnSaveCreditCardCallback;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Calendar;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.models.profile.EHICreditCard.CardType.detect;
import static com.ehi.enterprise.android.utils.EHITextUtils.isEmpty;

@AutoUnbindAll
public class AddCreditCardViewModel extends ReservationViewModel {
    final ReactorVar<CharSequence> cardNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> cardNumberError = new ReactorVar<>();
    final ReactorVar<CharSequence> cardExpirationMonthError = new ReactorVar<>();
    final ReactorVar<CharSequence> cardExpirationYearError = new ReactorVar<>();
    final ReactorVar<CharSequence> cardCcvError = new ReactorVar<>();
    final ReactorTextViewState cardName = new ReactorTextViewState();
    final ReactorTextViewState cardExpirationMonth = new ReactorTextViewState();
    final ReactorTextViewState cardExpirationYear = new ReactorTextViewState();
    final ReactorTextViewState cardNumber = new ReactorTextViewState();
    final ReactorTextViewState cardCCV = new ReactorTextViewState();
    final ReactorViewState cardSubmitButton = new ReactorViewState();
    final ReactorViewState addCreditCardContainer = new ReactorViewState();
    final ReactorTextViewState prepayTermsAndConditionsView = new ReactorTextViewState();
    final ReactorTextViewState saveForLaterUseView = new ReactorTextViewState();
    final ReactorVar<Boolean> mCardAdded = new ReactorVar<>(false);
    final ReactorVar<String> mPrepayTermsAndConditions = new ReactorVar<>();
    final ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    final ReactorVar<String> mPaymentErrorMessage = new ReactorVar<>();
    final ReactorVar<Boolean> mDebitCardWarning = new ReactorVar<>(false);

    private boolean mWasNameValid = false;
    private boolean mShouldSaveToProfile = false;

    final ReactorVar<Boolean> isNameValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasNameValid |= value;
            super.setValue(value);
        }
    };

    private boolean mWasNumberValid = false;
    final ReactorVar<Boolean> isNumberValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasNumberValid |= value;
            super.setValue(value);
        }
    };

    private boolean mWasExpirationMonthValid = false;
    final ReactorVar<Boolean> isExpirationMonthValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasExpirationMonthValid |= value;
            super.setValue(value);
        }
    };

    private boolean mWasExpirationYearValid = false;
    final ReactorVar<Boolean> isExpirationYearValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasExpirationYearValid |= value;
            super.setValue(value);
        }
    };

    private boolean mWasCcvValid = false;
    final ReactorVar<Boolean> isCcvValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasCcvValid |= value;
            super.setValue(value);
        }
    };

    private final ReactorPropertyChangedListener<String> mFormChangedCallback = new ReactorPropertyChangedListener<String>() {
        @Override
        public void onPropertyChanged(String newValue) {
            formChanged();
        }
    };
    private boolean mSubmitCardHappened;
    private boolean isValid;
    private boolean mConditionsChecked = false;
    private boolean mSaveForLaterUseChecked = false;
    private Bundle mPaymentBundle;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        cardNumber.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {
                cardNumberUpdated(newValue);
                updateCardIcon();
                formChanged();
            }
        });

        if (cardNumber.text().getRawValue() == null) {
            cardNumber.text().setRawValue("");
        }
        if (cardExpirationMonth.text().getRawValue() == null) {
            cardExpirationMonth.text().setRawValue("");
        }
        if (cardExpirationYear.text().getRawValue() == null) {
            cardExpirationYear.text().setRawValue("");
        }
        if (cardCCV.text().getRawValue() == null) {
            cardCCV.text().setRawValue("");
        }

        EHIDriverInfo driverInfo;
        EHIReservation modifyReservation = getManagers().getReservationManager().getCurrentModifyReservation();
        if (isModify() && modifyReservation != null) {
            driverInfo = modifyReservation.getDriverInfo();
        } else {
            driverInfo = getManagers().getReservationManager().getDriverInfo();
        }

        if (EHITextUtils.isEmpty(cardName.text().getRawValue()) && driverInfo != null) {
            cardName.text().setValue(String.format("%s %s", driverInfo.getFirstName(), driverInfo.getLastName()));
        }

        updateCardIcon();
        addCreditCardContainer.visibility().setValue(View.VISIBLE);

        cardName.setTextChangedListener(mFormChangedCallback);
        cardExpirationMonth.setTextChangedListener(mFormChangedCallback);
        cardExpirationYear.setTextChangedListener(mFormChangedCallback);
        cardCCV.setTextChangedListener(mFormChangedCallback);

        if (mShouldSaveToProfile) {
            mConditionsChecked = true;
            prepayTermsAndConditionsView.setVisibility(View.GONE);
        }
        if (shouldHideSaveLaterUseCheckbox()) {
            saveForLaterUseView.setVisibility(View.GONE);
        }

        formChanged();
    }

    private boolean shouldHideSaveLaterUseCheckbox() {
        return !isNorthAmericaPrepayAvailable(isModify())
                || mShouldSaveToProfile
                || !getManagers().getLoginManager().isLoggedIn()
                || getManagers().getLoginManager().getProfileCollection().getPaymentProfile().getCardPaymentMethods().size() > 3;
    }

    private void updateCardIcon() {
        cardNumber.drawableRight().setValue(BaseAppUtils.getCardIcon(getStrippedCardNumber()));
    }

    private void cardNumberUpdated(String number) {
        StringBuilder stringBuilder = new StringBuilder(number);
        int pos = 0;
        while (true) {
            if (pos >= stringBuilder.length()) break;
            if (' ' == stringBuilder.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == stringBuilder.length())) {
                stringBuilder.delete(pos, pos + 1);
            } else {
                pos++;
            }
        }

        // Insert char where needed.
        pos = 4;
        while (true) {
            if (pos >= stringBuilder.length()) break;
            final char c = stringBuilder.charAt(pos);
            // Only if its a digit where there should be a space we insert a space
            if ("0123456789".indexOf(c) >= 0) {
                stringBuilder.insert(pos, "" + ' ');
            }
            pos += 5;
        }
        if (!stringBuilder.toString().equals(number)) {
            cardNumber.text().setValue(stringBuilder.toString());
        }
    }

    private void formChanged() {
        String tempText = cardNumber.text().getRawValue();
        boolean formValid = tempText.length() > 1
                && getStrippedCardNumber().length() < 17
                && EHIPatterns.CREDIT_CARD_SPACES.matcher(tempText).matches();
        boolean currentConditionIsValid = formValid;
        isNumberValid.setValue(currentConditionIsValid);
        if (mWasNumberValid || mSubmitCardHappened) {
            cardNumberError.setValue(currentConditionIsValid ? null : " ");
        }
        formValid &= currentConditionIsValid;

        currentConditionIsValid = !isEmpty(cardName.text().getRawValue());
        isNameValid.setValue(currentConditionIsValid);
        if (mWasNameValid || mSubmitCardHappened) {
            cardNameError.setValue(currentConditionIsValid ? null : " ");
        }
        formValid &= currentConditionIsValid;

        tempText = cardExpirationMonth.text().getRawValue();
        int tempInt = isEmpty(tempText) ? 0 : Integer.parseInt(tempText);
        currentConditionIsValid = !isEmpty(tempText) && tempInt <= 12 && tempInt > 0;
        isExpirationMonthValid.setValue(currentConditionIsValid);
        if (mWasExpirationMonthValid || mSubmitCardHappened) {
            cardExpirationMonthError.setValue(currentConditionIsValid ? null : " ");
        }
        formValid &= currentConditionIsValid;

        tempText = cardExpirationYear.text().getRawValue();
        tempInt = isEmpty(tempText) ? 0 : (Integer.parseInt("20" + tempText) - Calendar.getInstance().get(Calendar.YEAR));
        currentConditionIsValid = !isEmpty(tempText) && tempInt < 50 && tempInt >= 0;
        isExpirationYearValid.setValue(currentConditionIsValid);
        if (mWasExpirationYearValid || mSubmitCardHappened) {
            cardExpirationYearError.setValue(currentConditionIsValid ? null : " ");
        }
        formValid &= currentConditionIsValid;

        tempText = cardCCV.text().getRawValue();
        currentConditionIsValid = EHIPatterns.CCV.matcher(tempText).matches();
        isCcvValid.setValue(currentConditionIsValid);
        if (mWasCcvValid || mSubmitCardHappened) {
            cardCcvError.setValue(currentConditionIsValid ? null : " ");
        }
        formValid &= currentConditionIsValid;

        formValid &= mConditionsChecked;

        cardSubmitButton.enabled().setValue(formValid);
        isValid = formValid;
    }

    public void submitCard() {
        if (isValid) {
            showProgress(true);

            final EHICreditCard creditCard = new EHICreditCard(cardCCV.text().getRawValue(),
                    cardExpirationMonth.text().getRawValue(),
                    "20" + cardExpirationYear.text().getRawValue(),
                    getStrippedCardNumber(),
                    detect(getStrippedCardNumber()).cardName);

            final boolean shouldUsePangui = mShouldSaveToProfile || isNorthAmericaPrepayAvailable(isModify());

            final String loyaltyId;
            final String individualId;
            if (mShouldSaveToProfile || mSaveForLaterUseChecked){
                loyaltyId = getManagers().getLoginManager().getProfileCollection().getBasicProfile().getLoyaltyData().getLoyaltyNumber();
                individualId = getUserProfileCollection().getProfile().getIndividualId();
            } else {
                loyaltyId = getOngoingReservation().getResSessionId();
                individualId = null;
            }

            CreditCard card = new CreditCard(
                    loyaltyId,
                    individualId,
                    creditCard,
                    cardName.text().getRawValue(),
                    shouldUsePangui ? CreditCard.PANGUI : CreditCard.FARE_OFFICE,
                    mShouldSaveToProfile || mSaveForLaterUseChecked
            );

            CreditCardManager creditCardManager = new CreditCardManager();
            creditCardManager.setRequestProcessorService(this);
            creditCardManager.uploadCreditCardToService(
                    card,
                    new IOnSaveCreditCardCallback() {
                        @Override
                        public void onSuccess(List<EHIPaymentMethod> paymentMethods, String addedPaymentReferenceId) {
                            if (paymentMethods != null) {
                                getManagers().getLoginManager().getProfileCollection().getPaymentProfile().setPaymentMethods(paymentMethods);
                            }
                            if (shouldUsePangui && addedPaymentReferenceId != null) {
                                mPaymentBundle = new EHIBundle.Builder()
                                        .putString(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE, addedPaymentReferenceId)
                                        .createBundle();
                            }

                            showProgress(false);
                            mCardAdded.setValue(true);
                        }

                        @Override
                        public void onPaymentReferenceIdObtained(String paymentReferenceId) {
                            if (shouldUsePangui && paymentReferenceId != null) {
                                mPaymentBundle = new EHIBundle.Builder()
                                        .putString(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE, paymentReferenceId)
                                        .createBundle();
                            }

                            showProgress(false);
                            mCardAdded.setValue(true);
                        }

                        @Override
                        public void onFailure(ResponseWrapper response) {
                            showProgress(false);
                            setError(response);
                        }

                        @Override
                        public void onPaymentProxyError(String errorMessage) {
                            showProgress(false);
                            if (errorMessage != null) {
                                mPaymentErrorMessage.setValue(errorMessage);
                            } else {
                                mPaymentErrorMessage.setValue(getResources().getString(R.string.reachability_details));
                            }

                        }

                        @Override
                        public void onDebitCardEntered() {
                            showProgress(false);
                            mDebitCardWarning.setValue(true);
                        }
                });

        } else {
            mSubmitCardHappened = true;
            formChanged();
        }
    }

    private String getStrippedCardNumber() {
        return cardNumber.text().getRawValue().replaceAll("\\s+", "");
    }

    public void setCardIOData(io.card.payment.CreditCard card) {
        cardNumberUpdated(card.cardNumber);
        cardCCV.text().setValue(card.cvv);
        cardExpirationMonth.text().setValue(Integer.toString(card.expiryMonth));
        cardExpirationYear.text().setValue(Integer.toString(card.expiryYear % 100));
        updateCardIcon();
    }

    public boolean getCardAdded() {
        return mCardAdded.getValue();
    }

    public void setConditionsChecked(boolean isChecked) {
        mConditionsChecked = isChecked;
        formChanged();
    }

    public void setLaterUseChecked(boolean isChecked) {
        mSaveForLaterUseChecked = isChecked;
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
                    setError(response);
                }
            }
        });
    }

    public void setPrepayTermsAndConditions(String prepayTermsAndConditions) {
        mPrepayTermsAndConditions.setValue(prepayTermsAndConditions);
    }

    public Bundle getPaymentBundle() {
        return mPaymentBundle;
    }

    public String getPrepayTermsAndConditions() {
        return mPrepayTermsAndConditions.getValue();
    }

    public void setIsFromProfile(boolean value) {
        mShouldSaveToProfile = value;
    }

    public String getPaymentErrorMessage() {
        return mPaymentErrorMessage.getValue();
    }

    public void clearPaymentErrorMessage() {
        mPaymentErrorMessage.setValue(null);
    }

    public boolean shouldShowDebitCardWarning() {
        return mDebitCardWarning.getValue();
    }

    public void clearShowDebitCard() {
        mDebitCardWarning.setValue(false);
    }
}
