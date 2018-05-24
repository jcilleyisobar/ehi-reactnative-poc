package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.request_params.profile.PaymentRequestParams;
import com.ehi.enterprise.android.network.request_params.profile.PutProfileParams;
import com.ehi.enterprise.android.network.requests.profile.DeletePaymentRequest;
import com.ehi.enterprise.android.network.requests.profile.PutProfileRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public abstract class EditPaymentMethodFragmentViewModel extends ManagersAccessViewModel {

    private final ReactorVar<Boolean> saveSuccessful = new ReactorVar<>(false);
    private final ReactorVar<Boolean> deleteSuccessful = new ReactorVar<>(false);

    private EHIPaymentMethod method;

    public void setPaymentMethod(EHIPaymentMethod method) {
        this.method = method;

        populateView(method);
    }

    public boolean isSaveSuccessful() {
        return saveSuccessful.getValue();
    }

    public void setSaveSuccessful(boolean isSaveSuccessful) {
        saveSuccessful.setValue(isSaveSuccessful);
    }

    public boolean isDeleteSuccessful() {
        return deleteSuccessful.getValue();
    }

    public void setDeleteSuccessful(boolean isSaveSuccessful) {
        deleteSuccessful.setValue(isSaveSuccessful);
    }

    public void saveUpdatedPaymentMethod() {
        showProgress(true);

        updatePaymentMethod(method);

        final PutProfileParams profileParams = new PutProfileParams.Builder()
                .setLoyaltyNumber(getUserProfileCollection().getBasicProfile().getLoyaltyData().getLoyaltyNumber())
                .setPaymentRequestParam(new PaymentRequestParams(method))
                .build();

        performRequest(
                new PutProfileRequest(
                        getManagers().getLoginManager().getProfileCollection().getProfile().getIndividualId(),
                        profileParams
                ),
                new IApiCallback<EHIProfileResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                        showProgress(false);

                        if (!response.isSuccess()) {
                            setError(response);
                            return;
                        }

                        updateUserProfile(response.getData().getPaymentProfile());
                        setSaveSuccessful(true);
                    }
                }
        );
    }

    public void delete() {
        showProgress(true);

        performRequest(new DeletePaymentRequest(getUserProfileCollection().getProfile().getIndividualId(), method.getPaymentReferenceId()), new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                showProgress(false);

                if (!response.isSuccess()) {
                    setError(response);
                    return;
                }

                final EHIPaymentProfile paymentProfile = response.getData().getPaymentProfile();
                updateUserProfile(paymentProfile == null ? new EHIPaymentProfile() : paymentProfile);
                deleteSuccessful.setValue(true);
            }
        });
    }

    protected abstract void populateView(EHIPaymentMethod method);

    protected abstract void updatePaymentMethod(EHIPaymentMethod method);

    private void updateUserProfile(EHIPaymentProfile paymentProfile) {
        final ProfileCollection profileCollection = getUserProfileCollection();

        profileCollection.setPaymentProfile(paymentProfile);

        getManagers().getLoginManager().setProfile(profileCollection);
    }
}
