package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.Date;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EditBillingNumberFragmentViewModel extends EditPaymentMethodFragmentViewModel {

    public final ReactorTextViewState billingName = new ReactorTextViewState();
    public final ReactorTextViewState billingNumber = new ReactorTextViewState();
    public final ReactorCompoundButtonState makePreferredCheckBox = new ReactorCompoundButtonState();
    public final ReactorViewState makePreferredView = new ReactorViewState();
    public final ReactorViewState preferredMethodView = new ReactorViewState();

    public void makePreferredClicked() {
        makePreferredCheckBox.setChecked(!makePreferredCheckBox.checked().getRawValue());
    }

    @Override
    protected void populateView(EHIPaymentMethod method) {
        billingName.setText(method.getAlias());
        billingNumber.setText(method.getMaskedNumber());
        makePreferredView.setVisibility(
                method.isPreferred() ? ReactorTextViewState.GONE : ReactorTextViewState.VISIBLE
        );
        preferredMethodView.setVisibility(
                method.isPreferred() ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );
    }

    @Override
    protected void updatePaymentMethod(EHIPaymentMethod method) {
        method.setAlias(billingName.text().getRawValue());
        method.setPreferred(method.isPreferred() || makePreferredCheckBox.checked().getRawValue());
        method.setPaymentServiceContextReferenceIdentifier(method.getPaymentReferenceId());
        method.setLastFour(EHITextUtils.getLastN(billingNumber.text().getRawValue(), 4));
        method.setExpirationDate(new Date());
    }
}
