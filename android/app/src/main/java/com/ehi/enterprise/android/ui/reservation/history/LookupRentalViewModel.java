package com.ehi.enterprise.android.ui.reservation.history;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.GetRetrieveReservationRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LookupRentalViewModel extends ManagersAccessViewModel {
    ReactorVar<EHIReservation> mRetrievedReservation = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mErrorWrapper = new ReactorVar<>();
    ReactorVar<String> mConfirmationNumber = new ReactorVar<>();
    ReactorVar<String> mFirstName = new ReactorVar<>();
    ReactorVar<String> mLastName = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (isUserLoggedIn()) {
            mFirstName.setValue(getUserProfileCollection().getBasicProfile().getFirstName());
            mLastName.setValue(getUserProfileCollection().getBasicProfile().getLastName());
        }
        else {
            mFirstName.setValue(TextUtils.isEmpty(getFirstName()) ? "" : getFirstName());
            mLastName.setValue(TextUtils.isEmpty(getLastName()) ? "" : getLastName());
        }
    }

    public void findRental() {
        findRental(getConfirmationNumber(), getFirstName(), getLastName());
    }

    public void findRental(String confirmationNumber, String firstName, String lastName) {
        performRequest(new GetRetrieveReservationRequest(confirmationNumber, firstName, lastName), new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                if (response.isSuccess()) {
                    setRetrievedReservation(response.getData());
                }
                else {
                    setErrorWrapper(response);
                }
            }
        });
    }

    @Nullable
    public EHIReservation getRetrievedReservation() {
        if (mRetrievedReservation != null) {
            return mRetrievedReservation.getValue();
        }
        return null;
    }

    private void setRetrievedReservation(EHIReservation retrievedReservation) {
        // updates the manager on a successful call - temp fix for EA-4891
        getManagers().getReservationManager().addOrUpdateSelectedCarClass(retrievedReservation.getCarClassDetails());

        if (getManagers().getReservationManager().getDriverInfo() == null) {
            getManagers().getReservationManager().addOrUpdateDriverInfo(retrievedReservation.getDriverInfo(), false);
        }
        mRetrievedReservation.setValue(retrievedReservation);
    }

    @Nullable
    public ResponseWrapper getErrorResponse() {
        if (mErrorWrapper != null) {
            return mErrorWrapper.getValue();
        }
        return null;
    }

    public void setErrorResponse(ResponseWrapper response) {
        mErrorWrapper.setValue(response);
    }

    private void setErrorWrapper(ResponseWrapper errorWrapper) {
        mErrorWrapper.setValue(errorWrapper);
    }

    @Nullable
    public String getFirstName() {
        if (mFirstName != null) {
            return mFirstName.getValue();
        }
        return null;
    }

    @Nullable
    public String getLastName() {
        if (mLastName != null) {
            return mLastName.getValue();
        }
        return null;
    }

    public void setFirstName(String firstName) {
        mFirstName.setValue(firstName);
    }

    public void setLastName(String lastName) {
        mLastName.setValue(lastName);
    }

    @Nullable
    public String getConfirmationNumber() {
        if (mConfirmationNumber != null) {
            return mConfirmationNumber.getValue();
        }
        return null;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        mConfirmationNumber.setValue(confirmationNumber);
    }

    public boolean isButtonEnabled() {
        return !TextUtils.isEmpty(getConfirmationNumber())
                && !TextUtils.isEmpty(getFirstName())
                && !TextUtils.isEmpty(getLastName());
    }

    public void clearRetrievedReservation() {
        mRetrievedReservation.setValue(null);
    }

    public String getSupportPhoneNumber() {
        String phone = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportPhoneNumber(EHIPhone.PhoneType.CONTACT_US);
        if (phone == null) {
            phone = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getSupportPhoneNumber(EHIPhone.PhoneType.ROADSIDE_ASSISTANCE);
        }
        return phone;
    }
}