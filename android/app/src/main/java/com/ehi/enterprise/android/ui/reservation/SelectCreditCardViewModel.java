package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SelectCreditCardViewModel extends ManagersAccessViewModel {

    final ReactorVar<String> mPrepayTermsAndConditions = new ReactorVar<>();
    final ReactorVar<EHIPaymentProfile> mPaymentProfile = new ReactorVar<>();
    final ReactorViewState continueButton = new ReactorViewState();

    private String mPaymentReferenceId;
    private boolean mShouldAutoSelectLastState;
    private boolean mShouldAutoSelect;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        mPaymentProfile.setValue(getUserProfileCollection().getPaymentProfile());
        mShouldAutoSelectLastState = shouldAutomaticallySelectCard();
    }

    public EHIPaymentProfile getPaymentProfile() {
        return mPaymentProfile.getValue();
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

    public String getPrepayTermsAndConditions() {
        return mPrepayTermsAndConditions.getValue();
    }

    public String getPaymentReferenceId() {
        return mPaymentReferenceId;
    }

    public void setPaymentReferenceId(String value) {
        mPaymentReferenceId = value;
    }

    public void validateForm(boolean termsChecked) {
        continueButton.setEnabled(termsChecked && !EHITextUtils.isEmpty(mPaymentReferenceId));
    }

    public void updateShouldAutomaticallySelectCard() {
        EHIPaymentMethod method = getSelectedPaymentMethod();
        if (method != null && method.isPreferred()) {
            getManagers().getLocalDataManager().setShouldAutomaticallySelectCard(mShouldAutoSelect);
        }
    }

    public EHIPaymentMethod getSelectedPaymentMethod() {
        for (EHIPaymentMethod method : getManagers().getLoginManager().getProfileCollection().getPaymentProfile().getCardPaymentMethods()) {
            if (method.getPaymentReferenceId().equals(mPaymentReferenceId)) {
                return method;
            }
        }
        return null;
    }

    public void setShouldAutoSelect(boolean value) {
        mShouldAutoSelect = value;
    }

    public boolean shouldAutomaticallySelectCard() {
        return getManagers().getLocalDataManager().shouldAutomaticallySelectCard();
    }

    public boolean shouldAutomaticallySelectCardStateChanged() {
        return !mShouldAutoSelectLastState && mShouldAutoSelect;
    }
}
