package com.ehi.enterprise.android.utils.payment;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.ITypedRequestProcessorService;
import com.ehi.enterprise.android.network.requests.profile.PostPanguiAddCreditCardRequest;
import com.ehi.enterprise.android.network.requests.profile.PostProfileAddCreditCardRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.profile.GetCardSubmissionKeyResponse;
import com.ehi.enterprise.android.network.responses.profile.PaymentProfileResponse;
import com.ehi.enterprise.android.network.responses.profile.PostPanguiAddCreditCardResponse;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.payment.interfaces.ICreditCardManager;
import com.ehi.enterprise.android.utils.payment.interfaces.IOnSaveCreditCardCallback;

class PanguiCreditCardManager implements ICreditCardManager {

    private ITypedRequestProcessorService requestProcessorService;

    @Override
    public void setRequestProcessorService(ITypedRequestProcessorService requestProcessorService) {
        this.requestProcessorService = requestProcessorService;
    }

    @Override
    public void uploadCreditCardToService(final CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        addCreditCardToPangui(creditCard.getSubmissionKeyResponse(), creditCard, onSaveCreditCardCallback);

    }

    private void addCreditCardToPangui(GetCardSubmissionKeyResponse response, final CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        requestProcessorService.performRequest(new PostPanguiAddCreditCardRequest(
                        response.getPaymentContextData().getCardSubmissionUrl(),
                        response.getCardSubmissionKey(),
                        response.getPaymentContextData().getSourceSystemId(),
                        response.getCallingApplicationName(),
                        creditCard.getCard(),
                        creditCard.getHolderName()),
                new IApiCallback<PostPanguiAddCreditCardResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<PostPanguiAddCreditCardResponse> response) {
                        if (!response.isSuccess()) {
                            onSaveCreditCardCallback.onFailure(response);
                            return;
                        }

                        final PostPanguiAddCreditCardResponse data = response.getData();

                        if (EHITextUtils.isEmpty(data.getPaymentMediaReferenceIdentifier())) {
                            onSaveCreditCardCallback.onPaymentProxyError(data.getErrorMessage());
                            return;
                        }

                        if (data.isDebitCard()) {
                            onSaveCreditCardCallback.onDebitCardEntered();
                            return;
                        }

                        if (creditCard.shouldSaveToProfile()) {
                            addCreditCardToProfile(data.getPaymentMediaReferenceIdentifier(), creditCard, onSaveCreditCardCallback);
                        } else {
                            onSaveCreditCardCallback.onPaymentReferenceIdObtained(response.getData().getPaymentMediaReferenceIdentifier());
                        }
                    }
                }
        );
    }

    private void addCreditCardToProfile(final String referenceId, CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        requestProcessorService.performRequest(new PostProfileAddCreditCardRequest(referenceId, creditCard),
                new IApiCallback<PaymentProfileResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<PaymentProfileResponse> response) {
                        if (response.isSuccess()) {
                            onSaveCreditCardCallback.onSuccess(response.getData().getPaymentMethods(), referenceId);
                        } else {
                            onSaveCreditCardCallback.onFailure(response);
                        }
                    }
                });
    }
}
