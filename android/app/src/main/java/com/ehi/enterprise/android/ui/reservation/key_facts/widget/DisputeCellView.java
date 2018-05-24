package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DisputesCellViewBinding;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(DisputeCellViewModel.class)
public class DisputeCellView extends DataBindingViewModelView<DisputeCellViewModel, DisputesCellViewBinding> {

    private DisputeCellListener mDisputeCellListener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().email) {
                if (mDisputeCellListener != null) {
                    mDisputeCellListener.onEmailClicked(getViewModel().getEmail());
                }
            } else if (v == getViewBinding().telephoneNumber) {
                if (mDisputeCellListener != null) {
                    mDisputeCellListener.onTelephoneClicked(getViewModel().getTelephoneNumber());
                }
            } else if (v == getViewBinding().cellTitleButton.getRoot()) {
                getViewModel().cellTitleClicked();
            }
        }
    };

    private KeyFactsPolicyCell.KeyFactsPolicyCellClickListener mCellClickListener = new KeyFactsPolicyCell.KeyFactsPolicyCellClickListener() {
        @Override
        public void onPolicyClicked(final EHIKeyFactsPolicy policy) {
            if (mDisputeCellListener != null) {
                mDisputeCellListener.onRentalPolicyClicked(policy);
            }
        }
    };

    //region constructors
    public DisputeCellView(final Context context) {
        this(context, null);
    }

    public DisputeCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisputeCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_disputes);
            initViews();
        } else {
            addView(inflate(context, R.layout.v_key_facts_disputes, null));
        }
    }

    //endregion

    private void initViews() {
        getViewBinding().cellTitleButton.getRoot().setOnClickListener(mOnClickListener);
        getViewBinding().email.setOnClickListener(mOnClickListener);
        getViewBinding().telephoneNumber.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.textRes(getViewModel().title.textRes(), getViewBinding().cellTitleButton.cellTitle));
        bind(ReactorView.visibility(getViewModel().content.visibility(), getViewBinding().policiesContainer));
        bind(ReactorTextView.text(getViewModel().email.text(), getViewBinding().email));
        bind(ReactorTextView.text(getViewModel().telephoneNumber.text(), getViewBinding().telephoneNumber));
        bind(ReactorView.visibility(getViewModel().contactBranch.visibility(), getViewBinding().contactBranch));
        bind(ReactorView.visibility(getViewModel().disputeView.visibility(), getViewBinding().disputeInfoView));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (!ListUtils.isEmpty(getViewModel().getPolicies())) {
                    getViewBinding().policiesLinksContainer.removeAllViews();
                    for (EHIKeyFactsPolicy policy : getViewModel().getPolicies()) {
                        KeyFactsPolicyCell cell = new KeyFactsPolicyCell(getContext());
                        cell.setCellClickListener(mCellClickListener);
                        cell.setPolicy(policy);
                        getViewBinding().policiesLinksContainer.addView(cell);
                    }
                }
            }
        });
    }

    public void setDisputeInfo(EHICountry disputeInfo) {
        getViewModel().setDisputeInfo(disputeInfo);
    }

    public void setDisputeCellListener(final DisputeCellListener disputeCellListener) {
        mDisputeCellListener = disputeCellListener;
    }

    public void setPolicies(List<EHIKeyFactsPolicy> policies) {
        getViewModel().setPolicies(policies);
    }

    public interface DisputeCellListener {
        void onEmailClicked(String email);

        void onTelephoneClicked(String telephone);

        void onRentalPolicyClicked(EHIKeyFactsPolicy policy);
    }
}
