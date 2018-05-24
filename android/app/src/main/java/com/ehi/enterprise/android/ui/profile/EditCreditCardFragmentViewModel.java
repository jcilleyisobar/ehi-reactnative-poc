package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Calendar;
import java.util.Date;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EditCreditCardFragmentViewModel extends EditPaymentMethodFragmentViewModel {

    public final ReactorTextViewState creditCardName = new ReactorTextViewState();
    public final ReactorTextViewState creditCardNumber = new ReactorTextViewState();
    public final ReactorTextViewState creditCardExpirationDate = new ReactorTextViewState();
    public final ReactorCompoundButtonState makePreferredCheckBox = new ReactorCompoundButtonState();
    public final ReactorViewState makePreferredView = new ReactorViewState();
    public final ReactorViewState preferredMethodView = new ReactorViewState();

    private Date expirationDate;

    public void makePreferredClicked() {
        makePreferredCheckBox.setChecked(!makePreferredCheckBox.checked().getRawValue());
    }

    public void setExpirationDate(int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        expirationDate = calendar.getTime();


        creditCardExpirationDate.setText(getManagers().getDateUtilManager().formatDateTime(expirationDate,
                DateUtilManager.FORMAT_NO_MONTH_DAY
                        | DateUtilManager.FORMAT_NUMERIC_DATE));
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    protected void populateView(EHIPaymentMethod method) {
        creditCardName.setText(method.getAlias());
        creditCardNumber.setText(method.getMaskedCreditCardNumber());
        creditCardNumber.setDrawableLeft(BaseAppUtils.getCardIconByType(method.getCardType()));
        creditCardExpirationDate.setText(method.getExpirationDateAsLocalizedString());
        makePreferredView.setVisibility(
                method.isPreferred() ? ReactorTextViewState.GONE : ReactorTextViewState.VISIBLE
        );
        preferredMethodView.setVisibility(
                method.isPreferred() ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );
    }

    @Override
    protected void updatePaymentMethod(EHIPaymentMethod method) {
        method.setAlias(creditCardName.text().getRawValue());
        method.setPreferred(method.isPreferred() || makePreferredCheckBox.checked().getRawValue());
        method.setPaymentServiceContextReferenceIdentifier(method.getPaymentReferenceId());

        if (expirationDate != null) {
            method.setExpirationDate(expirationDate);
        }
    }

}
