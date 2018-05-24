package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AdditionalRentalPoliciesViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(AdditionalRentalPoliciesViewModel.class)
public class AdditionalRentalPoliciesExpandableCellView extends DataBindingViewModelView<AdditionalRentalPoliciesViewModel, AdditionalRentalPoliciesViewBinding>{

    private boolean mPoliciesAddedToView;
    private @Nullable AdditionalRentalPoliciesListener mPoliciesListener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().cellTitleButton.getRoot()) {
                getViewModel().cellTitleClicked();
            }
        }
    };
    private KeyFactsPolicyCell.KeyFactsPolicyCellClickListener mCellClickListener = new KeyFactsPolicyCell.KeyFactsPolicyCellClickListener() {
        @Override
        public void onPolicyClicked(final EHIKeyFactsPolicy policy) {
            if (mPoliciesListener != null) {
                mPoliciesListener.onRentalPolicyClicked(policy);
            }
        }
    };

    //region constructors
    public AdditionalRentalPoliciesExpandableCellView(final Context context) {
        this(context, null);
    }

    public AdditionalRentalPoliciesExpandableCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdditionalRentalPoliciesExpandableCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()){
            createViewBinding(R.layout.v_key_facts_additional_rental_policies);
            initViews();
        }
    }
    //endregion

    private void initViews() {
        getViewBinding().cellTitleButton.getRoot().setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.textRes(getViewModel().title.textRes(), getViewBinding().cellTitleButton.cellTitle));
        bind(ReactorTextView.textRes(getViewModel().subtitle.textRes(), getViewBinding().subtitle));
        bind(ReactorView.visibility(getViewModel().content.visibility(), getViewBinding().policiesContainer));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(!mPoliciesAddedToView && !ListUtils.isEmpty(getViewModel().getPolicies())){
                    for (EHIKeyFactsPolicy policy : getViewModel().getPolicies()) {
                        KeyFactsPolicyCell cell = new KeyFactsPolicyCell(getContext());
                        cell.setCellClickListener(mCellClickListener);
                        cell.setPolicy(policy);
                        getViewBinding().policiesContainer.addView(cell);
                    }

                    mPoliciesAddedToView = true;
                }
            }
        });
    }

    public void setPolicies(@NonNull List<EHIKeyFactsPolicy> policies){
        getViewModel().setPolicies(policies);
    }

    public void setPoliciesListener(@Nullable final AdditionalRentalPoliciesListener policiesListener) {
        mPoliciesListener = policiesListener;
    }

    public interface AdditionalRentalPoliciesListener {
        void onRentalPolicyClicked(EHIKeyFactsPolicy policy);
    }
}
