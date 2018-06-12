package com.ehi.enterprise.android.utils.payment;

import com.ehi.enterprise.android.models.profile.payment.pangui.EHIPaymentContextData;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.ITypedRequestProcessorService;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.profile.GetCardSubmissionKeyRequest;
import com.ehi.enterprise.android.network.requests.profile.PostProfileCardSubmissionKeyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.profile.GetCardSubmissionKeyResponse;
import com.ehi.enterprise.android.utils.payment.interfaces.ICreditCardManager;
import com.ehi.enterprise.android.utils.payment.interfaces.IOnSaveCreditCardCallback;

public class CreditCardManager implements ICreditCardManager {

    private ITypedRequestProcessorService requestProcessorService;

    @Override
    public void setRequestProcessorService(ITypedRequestProcessorService requestProcessorService) {
        this.requestProcessorService = requestProcessorService;
    }

    @Override
    public void uploadCreditCardToService(final CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        requestProcessorService.performRequest(getRequest(creditCard),
                new IApiCallback<GetCardSubmissionKeyResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<GetCardSubmissionKeyResponse> response) {
                        if (response.isSuccess()) {
                            creditCard.setSubmissionKeyResponse(response.getData());

                            ICreditCardManager creditCardManager = getCreditCardManager(getPaymentProcessor(creditCard));
                            creditCardManager.setRequestProcessorService(requestProcessorService);
                            creditCardManager.uploadCreditCardToService(creditCard, onSaveCreditCardCallback);
                        } else {
                            onSaveCreditCardCallback.onFailure(response);
                        }
                    }
                });
    }

    private AbstractRequestProvider<GetCardSubmissionKeyResponse> getRequest(CreditCard creditCard) {
        if (creditCard.shouldSaveToProfile()) {
            return new PostProfileCardSubmissionKeyRequest(creditCard.getIndividualId());
        }
        return new GetCardSubmissionKeyRequest(creditCard.getId());
    }

    @CreditCard.PaymentProcessorType
    private String getPaymentProcessor(CreditCard creditCard) {
        EHIPaymentContextData paymentContextData = creditCard.getSubmissionKeyResponse().getPaymentContextData();
        if (paymentContextData == null || paymentContextData.getPaymentProcessor() == null) {
            return creditCard.getPaymentProcessorType();
        }

        return paymentContextData.getPaymentProcessor();
    }

    private ICreditCardManager getCreditCardManager(@CreditCard.PaymentProcessorType String paymentProcessorType) {
        return paymentProcessorType.equals(CreditCard.FARE_OFFICE) ?
                new FareOfficeCreditCardManager() :
                new PanguiCreditCardManager();
    }
}
