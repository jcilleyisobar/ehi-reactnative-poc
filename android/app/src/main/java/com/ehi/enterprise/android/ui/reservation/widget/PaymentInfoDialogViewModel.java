package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetMorePrepayTermsConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class PaymentInfoDialogViewModel extends ManagersAccessViewModel {

    private final ReactorVar<GetMorePrepayTermsConditionsResponse> mTermsOfUse = new ReactorVar<>();
    private final ReactorVar<ResponseWrapper> mErrorResponse = new ReactorVar();

    public void requestTermsOfUse() {
            performRequest(new GetMorePrepayTermsConditionsRequest(getManagers().getLocalDataManager().getPreferredCountryCode()), new IApiCallback<GetMorePrepayTermsConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetMorePrepayTermsConditionsResponse> response) {
                if (response.isSuccess()) {
                    mTermsOfUse.setValue(response.getData());
                }
                else {
                    mErrorResponse.setValue(response);
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

    public ResponseWrapper getErrorResponse() {
        return mErrorResponse.getValue();
    }

    public void setResponse(ResponseWrapper responseWrapper) {
        mErrorResponse.setValue(responseWrapper);
    }
}
