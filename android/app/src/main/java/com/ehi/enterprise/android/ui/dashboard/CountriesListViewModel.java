package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.GetCountriesRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CountriesListViewModel extends ManagersAccessViewModel {

    public final ReactorVar<ResponseWrapper> errorWrapper = new ReactorVar<>();
    final ReactorVar<List<EHICountry>> mCountriesList = new ReactorVar<>();

    public void populateCountriesList() {
        List<EHICountry> countries = getManagers().getLocalDataManager().getCountriesList();
        if (countries == null || countries.size() > 0) {
            showProgress(true);
            performRequest(new GetCountriesRequest(), new IApiCallback<GetCountriesResponse>() {
                @Override
                public void handleResponse(ResponseWrapper<GetCountriesResponse> response) {
                    showProgress(false);
                    if (response.isSuccess()) {
                        mCountriesList.setValue(response.getData().getCountries());
                    }
                    else {
                        errorWrapper.setValue(response);
                    }
                }
            });
        }
        else {
            mCountriesList.setValue(countries);
        }
    }

    public ReactorVar<List<EHICountry>> getCountriesList() {
        return mCountriesList;
    }
}
