package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.requests.reservation.PostDcDetailsRequest;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostDcDetailsModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DeliveryAndCollectionViewModel extends ManagersAccessViewModel {

    ReactorVar<EHIReservation> mReservationObject = new ReactorVar<>();
    ReactorVar<ResponseWrapper> mSuccessWrapper = new ReactorVar<>();
    private boolean mIsModify;

    public EHIReservation getReservationObject() {
        return mReservationObject.getValue();
    }

    public ResponseWrapper getSuccessWrapper() {
        return mSuccessWrapper.getValue();
    }

    public void saveDetails(EHIDCDetails delivery, EHIDCDetails collection) {
        AbstractRequestProvider request;
        if (mIsModify) {
            request = new PostDcDetailsModifyRequest(getReservationObject().getResSessionId(), delivery, collection);
        } else {
            request = new PostDcDetailsRequest(getReservationObject().getResSessionId(), delivery, collection);
        }
        showProgress(true);
        performRequest(request, new IApiCallback<EHIReservation>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIReservation> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    if (mIsModify) {
                        getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                    } else {
                        getManagers().getReservationManager().addOrUpdateReservation(response.getData());
                    }
                    mSuccessWrapper.setValue(response);
                } else {
                    setError(response);
                }
            }
        });
    }

    public void populateReservationObject() {
        if (mIsModify) {
            mReservationObject.setValue(getManagers().getReservationManager().getCurrentModifyReservation());
        } else {
            mReservationObject.setValue(getManagers().getReservationManager().getCurrentReservation());
        }
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public void setModify(boolean isModify) {
        mIsModify = isModify;
    }
}
