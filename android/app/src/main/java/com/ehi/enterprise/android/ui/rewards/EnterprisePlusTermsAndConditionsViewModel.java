package com.ehi.enterprise.android.ui.rewards;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetEPlusTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnterprisePlusTermsAndConditionsViewModel extends ManagersAccessViewModel {

    final ReactorVar<GetEPlusTermsAndConditionsResponse> mTermsAndConditions = new ReactorVar<>();

    public void requestTermsAndConditions() {
        showProgress(true);
        performRequest(new GetEPlusTermsAndConditionsRequest(), new IApiCallback<GetEPlusTermsAndConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetEPlusTermsAndConditionsResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    mTermsAndConditions.setValue(response.getData());
                } else {
                    setError(response);
                }
            }
        });
    }

    public GetEPlusTermsAndConditionsResponse getTermsAndConditionsResponse() {
        return mTermsAndConditions.getValue();
    }

    public void setTermsAndConditions(GetEPlusTermsAndConditionsResponse terms) {
        mTermsAndConditions.setValue(terms);
    }

}