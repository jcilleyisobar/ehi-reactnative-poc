package com.ehi.enterprise.android.utils.payment;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.ITypedRequestProcessorService;
import com.ehi.enterprise.android.network.requests.profile.PostAddCreditCardRequest;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.utils.payment.interfaces.ICreditCardManager;
import com.ehi.enterprise.android.utils.payment.interfaces.IOnSaveCreditCardCallback;

class FareOfficeCreditCardManager implements ICreditCardManager {

    private ITypedRequestProcessorService requestProcessorService;

    @Override
    public void setRequestProcessorService(ITypedRequestProcessorService requestProcessorService) {
        this.requestProcessorService = requestProcessorService;
    }

    @Override
    public void uploadCreditCardToService(final CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        addCardRequest(creditCard, onSaveCreditCardCallback);
    }

    private void addCardRequest(CreditCard creditCard, final IOnSaveCreditCardCallback onSaveCreditCardCallback) {
        final String key = creditCard.getSubmissionKeyResponse().getCardSubmissionKey();
        final String url = creditCard.getSubmissionKeyResponse().getPaymentContextData().getCardSubmissionUrl();
        requestProcessorService.performRequest(new PostAddCreditCardRequest(creditCard.getCard(), url, key), new IApiCallback<BaseResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<BaseResponse> response) {
                if (response.isSuccess()) {
                    onSaveCreditCardCallback.onSuccess(null, null);
                } else {
                    onSaveCreditCardCallback.onFailure(response);
                }
            }
        });
    }
}
