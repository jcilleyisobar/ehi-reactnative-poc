package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.profile.DeletePaymentRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ehi.enterprise.android.app.Settings.MAX_CREDIT_CARDS;

@AutoUnbindAll
public class PaymentsListViewModel extends ManagersAccessViewModel {

    final ReactorViewState warningView = new ReactorViewState();
    final ReactorTextViewState warningViewTitle = new ReactorTextViewState();
    final ReactorTextViewState warningViewText = new ReactorTextViewState();
    final ReactorTextViewState addCreditCard = new ReactorTextViewState();
    final ReactorVar<EHIPaymentProfile> paymentProfile = new ReactorVar<>();
    final ReactorVar<String> deletedPaymentType = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        paymentProfile.setValue(getUserProfileCollection().getPaymentProfile());
    }

    public void setUpWarnings(EHIPaymentProfile paymentProfile) {

        if (ListUtils.isEmpty(paymentProfile.getBillingPaymentMethods())
                && ListUtils.isEmpty(paymentProfile.getCardPaymentMethods())) {
            // show no payment method message
            warningView.setVisibility(VISIBLE);
            warningViewTitle.setVisibility(GONE);
            warningViewText.setText(R.string.profile_payment_options_no_payment_text);
        } else if (ListUtils.isEmpty(paymentProfile.getCardPaymentMethods())) {
            // show no credit card message
            warningView.setVisibility(VISIBLE);
            warningViewTitle.setVisibility(GONE);
            warningViewText.setText(R.string.profile_payment_options_no_credit_card_text);
        } else if (paymentProfile.getCardPaymentMethods().size() == MAX_CREDIT_CARDS) {
            // max message
            warningView.setVisibility(VISIBLE);
            warningViewText.setText(
                    new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.profile_payment_options_max_credit_card_text)
                            .addTokenAndValue(EHIStringToken.COUNT, String.valueOf(MAX_CREDIT_CARDS))
                            .format()
            );

            // not add more
            addCreditCard.setVisibility(GONE);
        } else {
            warningView.setVisibility(GONE);
            addCreditCard.setVisibility(VISIBLE);
        }
    }

    public EHIPaymentProfile getPaymentProfile() {
        return paymentProfile.getValue();
    }

    public void delete(final EHIPaymentMethod method) {
        showProgress(true);

        performRequest(new DeletePaymentRequest(getUserProfileCollection().getProfile().getIndividualId(), method.getPaymentReferenceId()), new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                showProgress(false);

                if (!response.isSuccess()) {
                    setError(response);
                    return;
                }

                ProfileCollection userProfile = getUserProfileCollection();
                userProfile.setPaymentProfile(response.getData().getPaymentProfile());

                getManagers().getLoginManager().setProfile(userProfile);

                paymentProfile.setValue(userProfile.getPaymentProfile());
                deletedPaymentType.setValue(method.getPaymentType());
            }
        });
    }

    public String getDeletedPaymentType() {
        return deletedPaymentType.getValue();
    }

    public void setDeletedPaymentType(String paymentType) {
        deletedPaymentType.setValue(paymentType);
    }
}
