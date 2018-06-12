package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ProtectionProductsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ProtectionProductsViewModel.class)
public class ProtectionProductsCellView extends DataBindingViewModelView<ProtectionProductsViewModel, ProtectionProductsViewBinding>{

    private boolean mIncludedProtectionsAdded;
    private boolean mOptionalProtectionsAdded;
    private KeyFactsPolicyWithExclusionCell.KeyFactsPolicyCellClickListener mCellClickListener;

    //region constructors
    public ProtectionProductsCellView(final Context context) {
        this(context, null);
    }

    public ProtectionProductsCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProtectionProductsCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_protection_products);
        }
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().includedProtectionsContainer.visibility(), getViewBinding().includedProtectionsDivider));
        bind(ReactorView.visibility(getViewModel().includedProtectionsContainer.visibility(), getViewBinding().includedProtections));
        bind(ReactorTextView.textRes(getViewModel().includedProtectionsSubtitle.textRes(), getViewBinding().includedProtectionsSubtitle));
        bind(ReactorView.visibility(getViewModel().optionalProtectionsContainer.visibility(), getViewBinding().optionalProtections));
        bind(ReactorTextView.textRes(getViewModel().optionalProtectionsSubtitle.textRes(), getViewBinding().optionalProtectionsSubtitle));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (!mIncludedProtectionsAdded  && !ListUtils.isEmpty(getViewModel().getIncludedProtectionCells())) {
                    for (KeyFactsPolicyWithExclusionCell.PolicyExtraPair policyExtraPair : getViewModel().getIncludedProtectionCells()) {
                        KeyFactsPolicyWithExclusionCell cell = new KeyFactsPolicyWithExclusionCell(getContext());
                        cell.setPolicyExtraPair(policyExtraPair);
                        if(mCellClickListener!= null) {
                            cell.setKeyFactsClickListener(mCellClickListener);
                        }
                        getViewBinding().includedProtections.addView(cell);
                    }

                    mIncludedProtectionsAdded = true;
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (!mOptionalProtectionsAdded && !ListUtils.isEmpty(getViewModel().getOptionalProtectionCells())) {
                    for (KeyFactsPolicyWithExclusionCell.PolicyExtraPair pair: getViewModel().getOptionalProtectionCells()) {
                        KeyFactsPolicyWithExclusionCell cell = new KeyFactsPolicyWithExclusionCell(getContext());
                        cell.setPolicyExtraPair(pair);
                        if(mCellClickListener!= null) {
                            cell.setKeyFactsClickListener(mCellClickListener);
                        }
                        getViewBinding().optionalProtections.addView(cell);
                    }

                    mOptionalProtectionsAdded = true;
                }
            }
        });
    }

    public void setPoliciesAndExtras(List<EHIKeyFactsPolicy> policies, EHIExtras carClassDetailsExtras){
        getViewModel().setPoliciesAndExtras(policies, carClassDetailsExtras);
    }


    public void setCellClickListener(final KeyFactsPolicyWithExclusionCell.KeyFactsPolicyCellClickListener cellClickListener) {
        mCellClickListener = cellClickListener;
    }
}
