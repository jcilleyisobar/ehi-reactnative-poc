package com.ehi.enterprise.android.ui.reservation.widget;

import android.support.annotation.IntDef;
import android.support.v4.util.Pair;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@AutoUnbindAll
public class BillingAccountViewModel extends ManagersAccessViewModel {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({DEFAULT, BILLING_CHECKED, PAY_AT_PICKUP_CHECKED})
    public @interface CheckboxState {}

    @CheckboxState private int mLastCheckboxState = DEFAULT;

    public static final int DEFAULT = 0;
    public static final int BILLING_CHECKED = 1;
    public static final int PAY_AT_PICKUP_CHECKED = 2;

    //region fields
    private List<EHIPaymentMethod> mBillingMethods;
    private List<EHIPaymentMethod> mPayAtCounterMethods;
    private BillingAccountView.IBillingCallBack mBillingCallBack;
    private List<String> mPaymentIdsHistory;
    private String mBillingTypeHistory;
    private String mBillingAccountHistory;
    private boolean mHideForever;
    private List<CharSequence> mPayAtCounterOptionsText;
    private List<CharSequence> mBillingOptionsText;
    private boolean mCallbackLocked;
    private boolean mSetupInProgress = false;
    private int mLastBillingOptionSelected = -1;
    private int mLastPaymentOptionSelected = -1;
    //endregion
    //region ReactorVars
    final ReactorVar<Integer> billingSpinnerVisibility = new ReactorVar<>(GONE);
    final ReactorVar<Integer> billingCodeEditTextVisibility = new ReactorVar<>(GONE);
    final ReactorTextViewState maskedBillingAccountText = new ReactorTextViewState();
    final ReactorVar<Boolean> mBillingChecked = new ReactorVar<>(false);
    final ReactorVar<Integer> rootViewVisible = new ReactorVar<>(VISIBLE);
    final ReactorTextViewState billingAccountDescription = new ReactorTextViewState();
    final ReactorVar<Integer> payAtCounterSpinnerVisible = new ReactorVar<>(GONE);
    final ReactorVar<Pair<Integer, CharSequence>> billingSpinnerSelection = new ReactorVar<Pair<Integer, CharSequence>>(null) {
        @Override
        public void setValue(Pair<Integer, CharSequence> value) {
            super.setValue(value);
            selectedBillingOptionChanged(value);
        }

    };
    public final ReactorVar<Pair<Integer, CharSequence>> paymentSpinnerSelection = new ReactorVar<Pair<Integer, CharSequence>>(null) {
        @Override
        public void setValue(Pair<Integer, CharSequence> value) {
            super.setValue(value);
            selectedPaymentOptionChanged(value);
        }
    };
    public ReactorVar<String> newBillingCode = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            newBillingCodeChanged();
        }
    };
    private String mBillingMethodHistory;
    //endregion

    public void setup(ProfileCollection profileCollection, EHIReservation reservationObject, BillingAccountView.IBillingCallBack callBack) {
        billingAccountDescription.setVisibility(GONE);
        maskedBillingAccountText.setVisibility(GONE);
        EHIPaymentProfile paymentProfile = (profileCollection == null)
                ? null
                : profileCollection.getPaymentProfile();

        EHIContract corporateAccount = reservationObject.getCorporateAccount();
        if (paymentProfile == null && corporateAccount == null) {
            rootViewVisible.setValue(GONE);
            invokeBillingCallBack(null, null, null, null);
            mHideForever = true;
            return;
        }
        mHideForever = false;

        mBillingCallBack = callBack;
        List<CharSequence> options = new ArrayList<>();
        mBillingMethods = paymentProfile == null ? null : paymentProfile.getBillingPaymentMethods();
        mPayAtCounterMethods = paymentProfile == null ? null : paymentProfile.getCardPaymentMethods();
        EHIPaymentMethod paymentMethod;
        int selection = 0;
        boolean billingPreferred = false;

        mSetupInProgress = true;
        //region setup logic
        if (reservationObject.contractHasAdditionalBenefits()) {

            if (corporateAccount != null && corporateAccount.isContractAcceptsBilling()) {

                if (corporateAccount.getBillingAccount() != null) {
                    billingSpinnerVisibility.setValue(GONE);
                    maskedBillingAccountText.setText(corporateAccount.getBillingAccount());
                    maskedBillingAccountText.setVisibility(VISIBLE);

                    CharSequence disclaimerText = new TokenizedString.Formatter(getResources())
                            .addTokenAndValue(EHIStringToken.ACCOUNT_NAME, corporateAccount.getContractOrBillingName())
                            .formatString(R.string.review_payment_options_billing_subtitle)
                            .format();
                    billingAccountDescription.setText(disclaimerText);
                    billingAccountDescription.setVisibility(VISIBLE);
                    boolean prefersBilling = setupPaymentSpinner(corporateAccount, false);
                    mBillingChecked.setValue(true);
                    //Override payment profile invoke on callback

                    setBillingCheckRowChecked(true);

                } else {

                    String accountName = corporateAccount.getContractName();
                    accountName = accountName == null || accountName.length() == 0
                            ? ""
                            : accountName;

                    accountName.toUpperCase();
                    billingAccountDescription.setText(new TokenizedString.Formatter(getResources())
                            .addTokenAndValue(EHIStringToken.ACCOUNT_NAME, accountName)
                            .formatString(R.string.review_payment_options_billing_subtitle)
                            .format());
                    billingAccountDescription.setVisibility(VISIBLE);

                    String userProfileCorpId = profileCollection != null && profileCollection.getProfile().getCorporateAccount() != null
                            ? profileCollection.getProfile().getCorporateAccount().getContractNumber()
                            : null;

                    if (corporateAccount.getContractNumber() != null &&
                            corporateAccount.getContractNumber().equalsIgnoreCase(userProfileCorpId)) {
                        for (int i = 0; i < mBillingMethods.size(); i++) {

                            paymentMethod = mBillingMethods.get(i);
                            options.add(paymentMethod.getAlias() + " (" + paymentMethod.getMaskedCreditCardNumber() + ")");
                            if (paymentMethod.isPreferred() && corporateAccount.isContractAcceptsBilling()) {
                                selection = i;
                                billingPreferred = true;
                            }
                        }

                        options.add(getResources().getString(R.string.review_payment_options_billing_entry_dropdown_title));
                        mBillingOptionsText = options;
                        if (options.size() == 1) {
                            billingSpinnerVisibility.setValue(GONE);
                            billingCodeEditTextVisibility.setValue(VISIBLE);
                        } else {
                            billingSpinnerVisibility.setValue(VISIBLE);
                            billingCodeEditTextVisibility.setValue(GONE);
                            if (mLastBillingOptionSelected == -1) {
                                setCurrentBillingSelection(selection, options.get(selection));
                            } else {
                                setCurrentBillingSelection(mLastBillingOptionSelected, options.get(mLastBillingOptionSelected));
                            }
                        }

                    } else {
                        billingSpinnerVisibility.setValue(GONE);
                        billingCodeEditTextVisibility.setValue(VISIBLE);
                    }

                    billingPreferred = setupPaymentSpinner(corporateAccount, billingPreferred);
                    setBillingCheckRowChecked(billingPreferred);
                }
            } else {
                rootViewVisible.setValue(GONE);
                mHideForever = true;
                invokeBillingCallBack(null, null, null, null);
            }
        } else {
            if (corporateAccount != null && corporateAccount.getBillingAccount() != null) {
                rootViewVisible.setValue(GONE);
                mHideForever = true;
                invokeBillingCallBack(null, CommitRequestParams.EXISTING, null, CommitRequestParams.BILLING_ACCOUNT);
                setBillingCheckRowChecked(true);
            } else {
                rootViewVisible.setValue(GONE);
                mHideForever = true;
                invokeBillingCallBack(null, null, null, null);
                setBillingCheckRowChecked(false);
            }
        }
        //endregion
        mSetupInProgress = false;
    }

    private List<String> getPaymentReferenceIdAsList() {
        EHIPaymentMethod selectedMethod = getSelectedMethod(getBillingSelectedOption());
        if (selectedMethod == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        list.add(selectedMethod.getPaymentReferenceId());

        return list;
    }

    /**
     * Extracted setup from {@link #setupPaymentSpinner(EHIContract corporateAccount, boolean billingPrefered)} in order to help with selected preferred payment method
     *
     * @param corporateAccount
     * @param billingPrefered
     * @return payment options are preferred
     */
    private boolean setupPaymentSpinner(EHIContract corporateAccount, boolean billingPrefered) {
        EHIPaymentMethod paymentMethod;
        List<CharSequence> options = new ArrayList<>();
        int selection = 0;

        if (mPayAtCounterMethods == null || mPayAtCounterMethods.size() == 0) {
            payAtCounterSpinnerVisible.setValue(GONE);
            return billingPrefered;
        }

        for (int i = 0, size = mPayAtCounterMethods.size(); i < size; i++) {

            paymentMethod = mPayAtCounterMethods.get(i);
            options.add((paymentMethod.getAlias() + " (" + paymentMethod.getMaskedCreditCardNumber() + ")").trim());
            if (paymentMethod.isPreferred() && corporateAccount.isContractAcceptsBilling()) {
                selection = i;
                billingPrefered = false;
            }
        }

        if (options.size() == 0) {
            payAtCounterSpinnerVisible.setValue(GONE);
        } else {
            options.add(getResources().getString(R.string.reservation_credit_card_option_other));
            mPayAtCounterOptionsText = options;
            if (mLastPaymentOptionSelected == -1) {
                setCurrentPaymentSelection(selection, options.get(selection));
            } else {
                setCurrentPaymentSelection(mLastPaymentOptionSelected, options.get(mLastPaymentOptionSelected));
            }
            payAtCounterSpinnerVisible.setValue(VISIBLE);
        }

        return billingPrefered;
    }

    public void lockBillingCallback(boolean lock) {
        mCallbackLocked = lock;
    }

    /**
     * Null safe wrapper around {@link com.ehi.enterprise.android.ui.reservation.widget.BillingAccountView.IBillingCallBack}
     * <p/>
     * Logic for request is as such:
     * if user chooses to type in a billing code, then call this method: invokeBillingCallback(entered_Custom_Code, CommitRequestParams.CUSTOM, null)
     * <p/>
     * if user chooses either payment or billing option from spinner call invokeBillingCallback(null, null, chosen_Payment_ID)
     * where chosen_Payment_ID is fetched from {@link #getPaymentId()} or {@link #getPaymentReferenceIdAsList()}
     * <p/>
     * in the case where a masked billing code is attached {@link EHIReservation#getBillingAccount()} then call this method: invokeBillingCallback(null, CommitRequestParams.EXISTING, null)
     * <p/>
     * in the case where {@link EHIReservation#contractHasAdditionalBenefits()} is FALSE then:
     * if {@link EHIReservation#getBillingAccount()} != null then invokeBillingCallback(null, CommitRequestParams.EXISTING, null)
     * else invokeBillingCallback(null, null, null)
     *
     * @param billingAccount Billing account value for when a custom account is provided
     * @param billingType    {@link com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams.BillingTypes}
     * @param paymentIds     A list of payment ID's of pay at counter or billing method. If either is chosen, then billingAccount & billingType must be null
     * @param method         A {@link com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams.BillingMethods} which allows outside classes to be aware of what sort of billing they're being given
     */
    protected void invokeBillingCallBack(String billingAccount, String billingType, List<String> paymentIds, @CommitRequestParams.BillingMethods String method) {
        if (mBillingCallBack != null && !mCallbackLocked) {
            if (billingAccount != null || billingType != null || paymentIds != null) {
                mBillingAccountHistory = billingAccount;
                mBillingTypeHistory = billingType;
                mPaymentIdsHistory = paymentIds;
                mBillingMethodHistory = method;
            }
            //sanitize null to an empty string
            if (billingType == CommitRequestParams.CUSTOM && billingAccount == null) {
                billingAccount = "";
            }
            mBillingCallBack.onBillingMethodChanged(billingAccount, billingType, paymentIds, method);
        }
    }

    /**
     * @return
     */
    public List<String> getPaymentId() {
        if (mPayAtCounterMethods == null) {
            return null;
        }
        List<String> list = new ArrayList<>(mPayAtCounterMethods.size());

        int index = getPaymentSelectedOption();
        Pair<Integer, CharSequence> pair = paymentSpinnerSelection.getRawValue();
        if (index == -1 ||
                (pair != null && getResources().getString(R.string.reservation_credit_card_option_other).equals(pair.second.toString()))) {
            return null;
        }
        list.add(mPayAtCounterMethods.get(index).getPaymentReferenceId());
        return list;
    }

    public boolean isHideForever() {
        return mHideForever;
    }

    public boolean isBillingAccountViewChecked() {
        return mBillingChecked.getValue();
    }

    public List<CharSequence> getPaymentOptionsText() {
        return mPayAtCounterOptionsText;
    }

    public List<CharSequence> getBillingOptionsText() {
        return mBillingOptionsText;
    }

    public int getPaymentSelectedOption() {
        Pair<Integer, CharSequence> pair = paymentSpinnerSelection.getRawValue();
        return pair == null ? -1 : pair.first;
    }

    public int getBillingSelectedOption() {
        Pair<Integer, CharSequence> pair = billingSpinnerSelection.getRawValue();
        return pair == null ? -1 : pair.first;
    }

    @CheckboxState
    public int getCheckboxState() {
        return isBillingAccountViewChecked() ? BILLING_CHECKED : PAY_AT_PICKUP_CHECKED;
    }

    public EHIPaymentMethod getSelectedMethod(int index) {
        return mBillingMethods == null || mBillingMethods.size() == index || index == -1 ? null
                : mBillingMethods.get(index);
    }

    public void setLastCheckboxState(@CheckboxState int state) {
        mLastCheckboxState = state;
    }

    public void setCurrentPaymentSelection(int index, CharSequence value) {
        paymentSpinnerSelection.setValue(new Pair<>(index, value));
    }

    public void setCurrentBillingSelection(int index, CharSequence value) {
        billingSpinnerSelection.setValue(new Pair<>(index, value));
    }

    public void setLastBillingSelected(int index) {
        mLastBillingOptionSelected = index;
    }

    public void setLastPaymentSelected(int index) {
        mLastPaymentOptionSelected = index;
    }

    public void selectedBillingOptionChanged(Pair<Integer, CharSequence> value) {
        if (mSetupInProgress) {
            return;
        }
        if (value != null) {
            mLastBillingOptionSelected = value.first;

            //Hiding the billing edittext if we switched the billing option
            if (mBillingMethods != null && value.first != mBillingMethods.size()) {
                billingCodeEditTextVisibility.setValue(GONE);
            }
        }
        setBillingCheckRowChecked(true);
    }

    public void selectedPaymentOptionChanged(Pair<Integer, CharSequence> value) {
        if (mSetupInProgress) {
            return;
        }
        if (value != null) {
            mLastPaymentOptionSelected = value.first;
        }
        setBillingCheckRowChecked(false);
    }


    private void newBillingCodeChanged() {
        if (newBillingCode.getRawValue() != null) {

            if (newBillingCode.getRawValue().length() == 0 && !mBillingChecked.getRawValue()) {
                return;
            }

            if (rootViewVisible.getRawValue() == GONE || billingCodeEditTextVisibility.getRawValue() == GONE) { //Don't do things when billing isn't visible //TODO // FIXME: 8/13/15 needs to be cleaned up during the refactor sprint
                return;
            }

            mBillingChecked.setValue(true);
            invokeBillingCallBack(newBillingCode.getRawValue(), CommitRequestParams.CUSTOM, null, CommitRequestParams.BILLING_ACCOUNT);
        }
    }

    public void setBillingCheckRowChecked(boolean isBillingRowChecked) {
        //update with billing status from restored state
        if (mLastCheckboxState == PAY_AT_PICKUP_CHECKED) {
            isBillingRowChecked = false;
            mLastCheckboxState = DEFAULT;
        } else if (mLastCheckboxState == BILLING_CHECKED) {
            isBillingRowChecked = true;
            mLastCheckboxState = DEFAULT;
        }

        updateSpinnersVisibility(isBillingRowChecked, !isBillingRowChecked);
        mBillingChecked.setValue(isBillingRowChecked);

        if (rootViewVisible.getRawValue() == GONE) {
            //rootView is gone, disable callbacks
            return;
        }
        if (isBillingRowChecked) {
            //Reselecting billing spinner check box with ADD NEW left as option
            if (mBillingMethods != null && getBillingSelectedOption() == mBillingMethods.size()
                    && billingCodeEditTextVisibility.getRawValue() == GONE && billingSpinnerVisibility.getRawValue() == VISIBLE) {
                billingCodeEditTextVisibility.setValue(VISIBLE);
            }
            if (billingCodeEditTextVisibility.getRawValue() == GONE) {
                if (maskedBillingAccountText.visibility().getRawValue() == GONE) {
                    //Spinner is VISIBLE, Custom EditText is GONE
                    invokeBillingCallBack(null, null, getPaymentReferenceIdAsList(), CommitRequestParams.BILLING_ACCOUNT);
                } else {
                    //Spinner & EditText are GONE, masked must be shown
                    invokeBillingCallBack(null, CommitRequestParams.EXISTING, null, CommitRequestParams.BILLING_ACCOUNT);
                }
            } else {
                // EditText is VISIBLE, Spinner is VISIBLE
                invokeBillingCallBack(newBillingCode.getRawValue(), CommitRequestParams.CUSTOM, null, CommitRequestParams.BILLING_ACCOUNT);
            }
        } else {
            //Payment row is agreedToTermsAndConditions, payment option is selected
            if (billingSpinnerVisibility.getRawValue() == VISIBLE) {
                billingCodeEditTextVisibility.setValue(GONE);
            }
            newBillingCode.setValue("");
            invokeBillingCallBack(null,
                    null,
                    getPaymentId(),
                    CommitRequestParams.PAY_AT_COUNTER);
        }

    }

    private void updateSpinnersVisibility(boolean billingIsChecked, boolean payAtCounterIsChecked) {

        if (payAtCounterIsChecked && !ListUtils.isEmpty(getPaymentOptionsText())) {
            payAtCounterSpinnerVisible.setValue(VISIBLE);
        } else {
            payAtCounterSpinnerVisible.setValue(GONE);
        }

        if (billingIsChecked
                && getBillingOptionsText() != null
                && getBillingOptionsText().size() > 1) {
            billingSpinnerVisibility.setValue(VISIBLE);
        } else {
            billingSpinnerVisibility.setValue(GONE);
        }

        billingCodeEditTextVisibility.setValue(billingIsChecked && billingCodeEditTextVisibility.getRawValue() == ReactorViewState.VISIBLE ? VISIBLE : GONE);
    }

    public void setRootViewVisibility(boolean visible) {
        rootViewVisible.setRawValue(visible ? ReactorViewState.VISIBLE : ReactorViewState.GONE);
        if (visible) {
            invokeBillingCallBack(mBillingAccountHistory, mBillingTypeHistory, mPaymentIdsHistory, mBillingMethodHistory);
        } else {
            invokeBillingCallBack(null, null, null, null);
        }
    }
}
