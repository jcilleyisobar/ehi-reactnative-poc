package com.ehi.enterprise.android.ui.confirmation.widgets;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ConfirmationCarClassViewModel extends ManagersAccessViewModel {

    public final ReactorTextViewState carClassTypeText = new ReactorTextViewState();
    public final ReactorTextViewState carClassModelText = new ReactorTextViewState();
    public final ReactorTextViewState carClassTransmissionTextView = new ReactorTextViewState();

    public void setCarClassDetails(EHICarClassDetails carClassDetails) {
        carClassTypeText.setText(carClassDetails.getName());

        String makeModelOrSimilarText;
        if (carClassDetails.getMakeModelOrSimilarText() != null) {
            makeModelOrSimilarText = carClassDetails.getMakeModelOrSimilarText().trim();
        } else {
            makeModelOrSimilarText = "";
        }

        carClassModelText.setText(
                new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .addTokenAndValue(EHIStringToken.MAKE_MODEL, makeModelOrSimilarText)
                        .formatString(R.string.reservation_car_class_make_model_title)
                        .format());

        carClassTransmissionTextView.setText(EHICarClassDetails.getTransmissionDescription(carClassDetails.getFeatures()));
        if (carClassDetails.isManualTransmission()) {
            carClassTransmissionTextView.setDrawableLeft(R.drawable.icon_manual_transmission);
            carClassTransmissionTextView.setCompoundDrawablePaddingInDp(6);
        }
    }
}
