package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EquipmentProductsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EquipmentProductsViewModel.class)
public class EquipmentProductsExpandableCellView extends DataBindingViewModelView<EquipmentProductsViewModel, EquipmentProductsViewBinding>{
    public static final String INCLUDED_EQUIPMENT_REACTION = "INCLUDED EQUIPMENT REACTION";
    public static final String OPTIONAL_EQUIPMENT_REACTION = "OPTIONAL EQUIPMENT REACTION";

    private boolean mIncludedExtrasAdded;
    private boolean mOptionalExtrasAdded;
    private @Nullable EquipmentProductListener mListener;
    private KeyFactsPolicyWithExclusionCell.KeyFactsEquipmentCellClickListener mEquipmentCellClickListener = new KeyFactsPolicyWithExclusionCell.KeyFactsEquipmentCellClickListener() {
        @Override
        public void onEquipmentExtraClicked(final EHIExtraItem ehiExtraItem) {
            if(mListener != null){
                mListener.onExtraClicked(ehiExtraItem);
            }
        }
    };

    //region constructors
    public EquipmentProductsExpandableCellView(final Context context) {
        this(context, null);
    }

    public EquipmentProductsExpandableCellView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EquipmentProductsExpandableCellView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_equipment_products);
            initViews();
        }
    }
    //endregion

    private void initViews() {

    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.textRes(getViewModel().includedSubtitle.textRes(), getViewBinding().includedEquipmentSubtitle));
        bind(ReactorView.visibility(getViewModel().includedSubtitleDivider.visibility(), getViewBinding().includedEquipmentSectionDivider));
        bind(ReactorView.visibility(getViewModel().includedContainer.visibility(), getViewBinding().includedEquipmentContainer));

        bind(ReactorTextView.textRes(getViewModel().optionalSubtitle.textRes(), getViewBinding().optionalEquipmentSubtitle));
        bind(ReactorView.visibility(getViewModel().optionalSubtitleDivider.visibility(), getViewBinding().optionalEquipmentSectionDivider));
        bind(ReactorView.visibility(getViewModel().optionalContainer.visibility(), getViewBinding().optionalEquipmentContainer));

        addReaction(INCLUDED_EQUIPMENT_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (!mIncludedExtrasAdded && !ListUtils.isEmpty(getViewModel().getIncludedExtras())) {
                    for (EHIExtraItem item : getViewModel().getIncludedExtras()) {
                        KeyFactsPolicyWithExclusionCell cell = new KeyFactsPolicyWithExclusionCell(getContext());
                        cell.setEquipmentCellClickListener(mEquipmentCellClickListener);
                        cell.setExtraItem(item);
                        getViewBinding().includedEquipmentContainer.addView(cell);
                    }

                    mIncludedExtrasAdded = true;
                }
            }
        });

        addReaction(OPTIONAL_EQUIPMENT_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(!mOptionalExtrasAdded && !ListUtils.isEmpty(getViewModel().getOptionalExtras())){
                    for (EHIExtraItem item : getViewModel().getOptionalExtras()) {
                        KeyFactsPolicyWithExclusionCell cell = new KeyFactsPolicyWithExclusionCell(getContext());
                        cell.setEquipmentCellClickListener(mEquipmentCellClickListener);
                        cell.setExtraItem(item);
                        getViewBinding().optionalEquipmentContainer.addView(cell);
                    }

                    mOptionalExtrasAdded = true;
                }
            }
        });
    }

    public void setExtras(EHIExtras extras){
        getViewModel().setExtras(extras);
    }

    public void setEquipmentProductListener(@Nullable final EquipmentProductListener listener) {
        mListener = listener;
    }

    public interface EquipmentProductListener {
        void onExtraClicked(EHIExtraItem ehiExtraItem);
    }
}
