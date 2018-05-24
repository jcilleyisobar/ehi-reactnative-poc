package com.ehi.enterprise.android.ui.reservation.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewPrepayOrSaveViewModel extends ManagersAccessViewModel {

    final ReactorTextViewState saveIfPayLaterText = new ReactorTextViewState();
    final ReactorTextViewState saveIfPayNowText = new ReactorTextViewState();

    ReservationFlowListener.PayState mPayState;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        if (mPayState == null) {
            saveIfPayLaterText.setVisibility(View.VISIBLE);
            saveIfPayNowText.setVisibility(View.GONE);
        }
    }

    public void toggleVisibility() {
        if (saveIfPayNowText.visibility().getValue() == View.VISIBLE) {
            saveIfPayNowText.setVisibility(View.GONE);
            saveIfPayLaterText.setVisibility(View.VISIBLE);
        } else {
            saveIfPayNowText.setVisibility(View.VISIBLE);
            saveIfPayLaterText.setVisibility(View.GONE);
        }
    }

    public void setPayState(ReservationFlowListener.PayState payState) {
        mPayState = payState;
        setView(payState);
    }

    public void setPrepayOrSaveText(EHICarClassDetails carClassDetails) {
        if (carClassDetails.getPrePayPriceDifference() != null) {
            final String pricePayNow = carClassDetails.getPrePayPriceDifference();
            final CharSequence savingsPayNow = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_review_pay_now_na)
                    .addTokenAndValue(EHIStringToken.AMOUNT, pricePayNow)
                    .format();
            saveIfPayNowText.setText(savingsPayNow);
        }

        if (carClassDetails.getPaylaterVehiclePriceView() != null) {
            final String pricePayLater = carClassDetails.getPaylaterVehiclePriceView().toString();
            final CharSequence savingsPayLater = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_review_pay_later_na)
                    .addTokenAndValue(EHIStringToken.AMOUNT, pricePayLater)
                    .format();
            saveIfPayLaterText.setText(savingsPayLater);
        }
    }

    private void setView(ReservationFlowListener.PayState payState) {
        switch (payState) {
            case PAY_LATER:
                saveIfPayNowText.setVisibility(View.VISIBLE);
                saveIfPayLaterText.setVisibility(View.GONE);
                break;
            case PREPAY:
                saveIfPayNowText.setVisibility(View.GONE);
                saveIfPayLaterText.setVisibility(View.VISIBLE);
                break;
        }
    }

}