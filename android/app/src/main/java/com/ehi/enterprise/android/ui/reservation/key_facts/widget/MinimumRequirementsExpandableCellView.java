package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.KeyFactsMinimumRequirementsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(MinimumRequirementsViewModel.class)
public class MinimumRequirementsExpandableCellView extends DataBindingViewModelView<MinimumRequirementsViewModel, KeyFactsMinimumRequirementsViewBinding>{
    private KeyFactsPolicyCell.KeyFactsPolicyCellClickListener mPolicyCellClickListener;
    private boolean mPoliciesAdded;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().cellTitleButton.getRoot()) {
                getViewModel().cellTitleClicked();
            }
        }
    };

    //region constructors
    public MinimumRequirementsExpandableCellView(final Context context) {
        this(context, null);
    }

    public MinimumRequirementsExpandableCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinimumRequirementsExpandableCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_minimum_requirements);
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
        bind(ReactorView.visibility(getViewModel().content.visibility(), getViewBinding().policiesContainer));
        bind(ReactorTextView.textRes(getViewModel().subtitle.textRes(), getViewBinding().keyFactsMinimumRequirementsSubtitle));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(getViewModel().getKeyFactsPolicies() != null
                        && !getViewModel().getKeyFactsPolicies().isEmpty()
                        && !mPoliciesAdded){

                    for (EHIKeyFactsPolicy policy : getViewModel().getKeyFactsPolicies()) {
                        KeyFactsPolicyCell policyCell = new KeyFactsPolicyCell(getContext());
                        if(mPolicyCellClickListener != null) {
                            policyCell.setCellClickListener(mPolicyCellClickListener);
                        }
                        policyCell.setPolicy(policy);
                        getViewBinding().policiesContainer.addView(policyCell);
                    }

                    mPoliciesAdded = true;
                }
            }
        });
    }

    public void setPolicies(List<EHIKeyFactsPolicy> policies){
        getViewModel().setKeyFactsPolicies(policies);
    }

    public void setPolicyCellClickListener(final KeyFactsPolicyCell.KeyFactsPolicyCellClickListener policyCellClickListener) {
        mPolicyCellClickListener = policyCellClickListener;
    }
}
