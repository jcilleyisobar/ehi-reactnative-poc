package com.ehi.enterprise.android.ui.reservation.widget;

import android.text.TextUtils;
import android.view.View;

import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.reservation.EHIDCDetails;
import com.ehi.enterprise.android.models.reservation.EHIVehicleLogistic;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewDCDetailsViewModel extends ManagersAccessViewModel {

    //region reactive vars/states
    final ReactorViewState headerText = new ReactorViewState();
    final ReactorViewState dcView = new ReactorViewState();
    final ReactorViewState useDcButton = new ReactorViewState();

    final ReactorViewState deliveryArea = new ReactorViewState();
    final ReactorViewState deliveryNotAvailableView = new ReactorViewState();

    final ReactorViewState collectionArea = new ReactorViewState();
    final ReactorViewState collectionNotAvailableView = new ReactorViewState();
    final ReactorViewState collectionSameAsDeliveryView = new ReactorViewState();

    final ReactorImageViewState useDcButtonArrow = new ReactorImageViewState();
    final ReactorImageViewState deliveryIconView = new ReactorImageViewState();
    final ReactorImageViewState collectionIconView = new ReactorImageViewState();

    final ReactorTextViewState deliveryAddressView = new ReactorTextViewState();
    final ReactorTextViewState deliveryPhoneView = new ReactorTextViewState();
    final ReactorTextViewState deliveryCommentView = new ReactorTextViewState();

    final ReactorTextViewState collectionAddressView = new ReactorTextViewState();
    final ReactorTextViewState collectionPhoneView = new ReactorTextViewState();
    final ReactorTextViewState collectionCommentView = new ReactorTextViewState();
    public ReactorViewState dcInformationBody = new ReactorViewState();
    //endregion

    public void setVehicleLogistics(boolean deliveryAllowed, boolean collectionAllowed, EHIVehicleLogistic logistics) {
        //Fields visible
        if (deliveryAllowed) {
            if (logistics.getDeliveryInfo() != null) {
                deliveryArea.setVisibility(View.VISIBLE);
                deliveryNotAvailableView.setVisibility(View.GONE);
                deliveryAddressView.setVisibility(View.VISIBLE);
                deliveryPhoneView.setVisibility(View.VISIBLE);
                deliveryCommentView.setVisibility(View.VISIBLE);
            } else {
                deliveryArea.setVisibility(View.GONE);
                deliveryIconView.setVisibility(View.GONE);
            }
        } else {
            deliveryArea.setVisibility(View.VISIBLE);
            deliveryNotAvailableView.setVisibility(View.VISIBLE);
            deliveryAddressView.setVisibility(View.GONE);
            deliveryPhoneView.setVisibility(View.GONE);
            deliveryCommentView.setVisibility(View.GONE);
        }

        if (collectionAllowed) {
            if (logistics.getCollectionInfo() != null) {
                collectionArea.setVisibility(View.VISIBLE);
                collectionNotAvailableView.setVisibility(View.GONE);
                if (deliveryAllowed
                        && logistics.isSameAsDelivery()) {
                    collectionSameAsDeliveryView.setVisibility(View.VISIBLE);
                    collectionAddressView.setVisibility(View.GONE);
                    collectionPhoneView.setVisibility(View.GONE);
                    collectionCommentView.setVisibility(View.GONE);
                } else {
                    collectionSameAsDeliveryView.setVisibility(View.GONE);
                    collectionAddressView.setVisibility(View.VISIBLE);
                    collectionPhoneView.setVisibility(View.VISIBLE);
                    collectionCommentView.setVisibility(View.VISIBLE);
                }
            } else {
                collectionArea.setVisibility(View.GONE);
                collectionIconView.setVisibility(View.GONE);
            }
        } else {
            collectionArea.setVisibility(View.VISIBLE);
            collectionNotAvailableView.setVisibility(View.VISIBLE);
            collectionAddressView.setVisibility(View.GONE);
            collectionPhoneView.setVisibility(View.GONE);
            collectionCommentView.setVisibility(View.GONE);
        }

        //populating with data
        EHIDCDetails delivery = logistics.getDeliveryInfo();
        if (delivery != null) {
            final EHIAddressProfile ehiAddressProfile = delivery.getAddress();
            String readableAddress = null;

            if (ehiAddressProfile != null) {
                readableAddress = ehiAddressProfile.getReadableAddress();
            }

            if (!TextUtils.isEmpty(readableAddress)) {
                deliveryAddressView.setText(readableAddress);
            } else {
                deliveryAddressView.setVisibility(View.GONE);
            }

            if (delivery.getPhone() != null
                    && !TextUtils.isEmpty(delivery.getPhone().getPhoneNumber())) {
                deliveryPhoneView.setText(delivery.getFormattedPhoneNumber(true));
            } else {
                deliveryPhoneView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(delivery.getComment())) {
                deliveryCommentView.setText(delivery.getComment());
            } else {
                deliveryCommentView.setVisibility(View.GONE);
            }

        }

        EHIDCDetails collection = logistics.getCollectionInfo();
        if (collection != null) {
            final EHIAddressProfile ehiAddressProfile = collection.getAddress();
            String readableAddress = null;

            if (ehiAddressProfile != null) {
                readableAddress = ehiAddressProfile.getReadableAddress();
            }

            if (!TextUtils.isEmpty(readableAddress)) {
                collectionAddressView.setText(readableAddress);
            } else {
                collectionAddressView.setVisibility(View.GONE);
            }

            if (collection.getPhone() != null
                    && !TextUtils.isEmpty(collection.getPhone().getPhoneNumber())) {
                collectionPhoneView.setText(collection.getFormattedPhoneNumber(true));
            } else {
                collectionPhoneView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(collection.getComment())) {
                collectionCommentView.setText(collection.getComment());
            } else {
                collectionCommentView.setVisibility(View.GONE);
            }

        }
    }

    public void setIconVisibility(int visibility) {
        deliveryIconView.setVisibility(visibility);
        collectionIconView.setVisibility(visibility);
    }

    public void setIconResId(int resId) {
        deliveryIconView.setImageResource(resId);
        collectionIconView.setImageResource(resId);
    }

    public void populateWithData(boolean deliveryAllowed,
                                 boolean collectionAllowed,
                                 boolean requiresTravelPurpose,
                                 boolean isBusinessTravelPurpose,
                                 EHIVehicleLogistic vehicleLogistic,
                                 boolean displayOnly) {
        if ((deliveryAllowed
                || collectionAllowed)
                && requiresTravelPurpose
                && isBusinessTravelPurpose
                ) {
            headerText.setVisibility(View.VISIBLE);
            dcView.setVisibility(View.VISIBLE);

            if (vehicleLogistic != null
                    && (vehicleLogistic.getCollectionInfo() != null
                    || vehicleLogistic.getDeliveryInfo() != null)) {
                useDcButton.setVisibility(View.GONE);
                headerText.setVisibility(View.GONE);
                dcInformationBody.setVisibility(View.VISIBLE);
                setVehicleLogistics(deliveryAllowed,
                        collectionAllowed,
                        vehicleLogistic);
            } else {
                if (displayOnly) { //if need to show details only, without add button
                    //if have nothing to show will hide all view with header
                    headerText.setVisibility(View.GONE);
                    useDcButton.setVisibility(View.GONE);
                    headerText.setVisibility(View.GONE);
                    dcView.setVisibility(View.GONE);
                } else {
                    useDcButton.setVisibility(View.VISIBLE);
                    headerText.setVisibility(View.VISIBLE);
                    dcInformationBody.setVisibility(View.GONE);
                }
            }
        } else {
            headerText.setVisibility(View.GONE);
            useDcButton.setVisibility(View.GONE);
            headerText.setVisibility(View.GONE);
            dcView.setVisibility(View.GONE);
        }
    }

    public void hideGreenArrow() {
        useDcButtonArrow.setVisibility(View.GONE);
        deliveryIconView.setVisibility(View.GONE);
        collectionIconView.setVisibility(View.GONE);
    }

    public void showGreenArrow() {
        useDcButtonArrow.setVisibility(View.VISIBLE);
        deliveryIconView.setVisibility(View.VISIBLE);
        collectionIconView.setVisibility(View.VISIBLE);
    }
}
