package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.contract.GetContractRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.contract.GetContractResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AdditionalInfoFragmentViewModel extends ManagersAccessViewModel {
    private List<EHIAdditionalInformation> alreadyAddedInformation;

    private ReactorVar<EHIContract> ehiContractReactorVar = new ReactorVar<>();
    private boolean mPreRateOnly;

    public List<EHIAdditionalInformation> getAlreadyAddedInformation() {
        return alreadyAddedInformation;
    }

    public void setAlreadyAddedInformation(List<EHIAdditionalInformation> alreadyAddedInformation) {
        this.alreadyAddedInformation = alreadyAddedInformation;
    }

    public void fetchContractDetails(String contractNumber) {
        showProgress(true);
        performRequest(new GetContractRequest(contractNumber), new IApiCallback<GetContractResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetContractResponse> response) {
                showProgress(false);

                if (!response.isSuccess()) {
                    setError(response);
                    return;
                }

                ehiContractReactorVar.setValue(response.getData().getContractDetails());
            }
        });

    }

    public EHIContract getContractDetails() {
        return ehiContractReactorVar.getValue();
    }

    public void setContractDetails(EHIContract ehiCorporateAccount) {
        ehiContractReactorVar.setValue(ehiCorporateAccount);
    }

    public void setPreRateOnly(boolean preRateOnly) {
        mPreRateOnly = preRateOnly;
    }

    public boolean isPreRateOnly() {
        return mPreRateOnly;
    }
}
