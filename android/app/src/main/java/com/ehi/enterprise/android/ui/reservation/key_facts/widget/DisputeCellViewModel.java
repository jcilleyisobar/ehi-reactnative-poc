package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DisputeCellViewModel extends ManagersAccessViewModel {
    final ReactorTextViewState email = new ReactorTextViewState();
    final ReactorTextViewState telephoneNumber = new ReactorTextViewState();
    final ReactorViewState contactBranch = new ReactorViewState();
    final ReactorViewState disputeView = new ReactorViewState();
    final ReactorViewState content = new ReactorViewState();
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorVar<List<EHIKeyFactsPolicy>> mPolicies = new ReactorVar<>();

    @Nullable private EHICountry mEHICountry;

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setText(R.string.key_facts_contact_information_title);
        content.setVisibility(View.GONE);
    }

    public void setDisputeInfo(@NonNull final EHICountry ehiCountry) {

        if(ehiCountry != null) {
            mEHICountry = ehiCountry;
            contactBranch.setVisibility(View.GONE);
            disputeView.setVisibility(View.VISIBLE);
            if (mEHICountry.getKeyFactsDisputeEmail() != null) {
                email.setText(mEHICountry.getKeyFactsDisputeEmail());
            } else {
                email.setVisibility(View.GONE);
            }

            if (mEHICountry.getKeyFactsDisputePhone() != null) {
                telephoneNumber.setText(mEHICountry.getKeyFactsDisputePhone());
            } else {
                telephoneNumber.setVisibility(View.GONE);
            }
        }
        else {
            contactBranch.setVisibility(View.VISIBLE);
            disputeView.setVisibility(View.GONE);
        }
    }

    @Nullable
    public String getEmail() {
        if (mEHICountry != null) {
            return !EHITextUtils.isEmpty(mEHICountry.getKeyFactsDisputeEmail())
                   ? mEHICountry.getKeyFactsDisputeEmail()
                   : null;
        }
        return null;
    }

    @Nullable
    public String getTelephoneNumber() {
        if (mEHICountry != null) {
            return !EHITextUtils.isEmpty(mEHICountry.getKeyFactsDisputePhone())
                   ? mEHICountry.getKeyFactsDisputePhone()
                   : null;
        }
        return null;
    }

    public void cellTitleClicked() {
        content.setVisibility(content.visibility().getRawValue() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void setPolicies(List<EHIKeyFactsPolicy> policies) {
        mPolicies.setValue(policies);
    }

    public List<EHIKeyFactsPolicy> getPolicies() {
        return mPolicies.getValue();
    }
}
