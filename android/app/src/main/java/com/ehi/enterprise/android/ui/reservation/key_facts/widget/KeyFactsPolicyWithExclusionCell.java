package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.KeyFactsCellViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(KeyFactsPolicyWithExclusionViewModel.class)
public class KeyFactsPolicyWithExclusionCell extends DataBindingViewModelView<KeyFactsPolicyWithExclusionViewModel, KeyFactsCellViewBinding>{

    private @Nullable KeyFactsPolicyCellClickListener mCellClickListener;
    private @Nullable KeyFactsEquipmentCellClickListener mEquipmentCellClickListener;

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(v == getViewBinding().policyCell){
                if(mCellClickListener != null){
                    mCellClickListener.onPolicyClicked(getViewModel().getPolicy());
                }
                else if (mEquipmentCellClickListener != null){
                    mEquipmentCellClickListener.onEquipmentExtraClicked(getViewModel().getExtraItem());
                }
            }
            else if(v == getViewBinding().viewExclusionButton){
                if(mCellClickListener != null){
                    mCellClickListener.onExclusionsClicked(getViewModel().getExclusions());
                }
            }
        }
    };

    //region constructors
    public KeyFactsPolicyWithExclusionCell(final Context context) {
        this(context, null);
    }

    public KeyFactsPolicyWithExclusionCell(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyFactsPolicyWithExclusionCell(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            createViewBinding(R.layout.v_key_facts_cell);
            initViews();
        }
    }
    //endregion

    private void initViews() {
        getViewBinding().policyCell.setOnClickListener(mClickListener);
        getViewBinding().viewExclusionButton.setOnClickListener(mClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().policyName.text(), getViewBinding().policyNameButton));
        bind(ReactorTextView.text(getViewModel().policyPrice.textCharSequence(), getViewBinding().policyPrice),
             ReactorTextView.text(getViewModel().policyPrice.text(), getViewBinding().policyPrice),
             ReactorView.visibility(getViewModel().policyPrice.visibility(), getViewBinding().policyPrice));
        bind(ReactorTextView.textRes(getViewModel().exclusion.textRes(), getViewBinding().viewExclusionButton),
             ReactorView.visibility(getViewModel().exclusion.visibility(), getViewBinding().viewExclusionButton));
    }

    public void setKeyFactsClickListener(KeyFactsPolicyCellClickListener cellClickListener){
        mCellClickListener = cellClickListener;
    }

    public void setEquipmentCellClickListener(@Nullable final KeyFactsEquipmentCellClickListener equipmentCellClickListener) {
        mEquipmentCellClickListener = equipmentCellClickListener;
    }

    public void setPolicy(EHIKeyFactsPolicy policy){
        getViewModel().setPolicy(policy);
    }

    public void setExtraItem(EHIExtraItem item){
        getViewModel().setExtraItem(item);
    }

    public void setPolicyExtraPair(PolicyExtraPair policyExtraPair){
        getViewModel().setPolicyExtraPair(policyExtraPair);
    }

    public interface KeyFactsPolicyCellClickListener {
        void onPolicyClicked(EHIKeyFactsPolicy policy);

        void onExclusionsClicked(List<EHIKeyFactsPolicy> exclusions);
    }

    public interface KeyFactsEquipmentCellClickListener {
        void onEquipmentExtraClicked(EHIExtraItem ehiExtraItem);
    }

    public static class PolicyExtraPair {
        public final EHIKeyFactsPolicy policy;
        public final EHIExtraItem policyPrice;

        public PolicyExtraPair(final EHIKeyFactsPolicy policy, final EHIExtraItem policyPrice) {
            this.policy = policy;
            this.policyPrice = policyPrice;
        }
    }
}
