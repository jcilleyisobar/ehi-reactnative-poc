package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ChangePaymentBannerViewModel extends ManagersAccessViewModel {

    final ReactorTextViewState messageText = new ReactorTextViewState();
    final ReactorTextViewState creditCardIcon = new ReactorTextViewState();

    public void setup(EHICarClassDetails carClassDetails, ReservationFlowListener.PayState payState) {
        final String priceDifference = carClassDetails.getPrePayPriceDifference();

        if (payState == ReservationFlowListener.PayState.PAY_LATER) {
            messageText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_review_pay_now_savings)
                    .addTokenAndValue(EHIStringToken.AMOUNT, priceDifference)
                    .format());
            creditCardIcon.setVisibility(ReactorViewState.VISIBLE);
        } else if (payState == ReservationFlowListener.PayState.PREPAY){
            messageText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.reservation_review_pay_later_savings)
                    .addTokenAndValue(EHIStringToken.AMOUNT, priceDifference)
                    .format());
            creditCardIcon.setVisibility(ReactorViewState.GONE);
        }
    }

}
