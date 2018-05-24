package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.KeyFactsPolicyCellViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(KeyFactsPolicyCellViewModel.class)
public class KeyFactsPolicyCell extends DataBindingViewModelView<KeyFactsPolicyCellViewModel, KeyFactsPolicyCellViewBinding>{
    private @Nullable KeyFactsPolicyCellClickListener mListener;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (mListener != null) {
                mListener.onPolicyClicked(getViewModel().getPolicy());
            }
        }
    };

    //region constructors
    public KeyFactsPolicyCell(final Context context) {
        this(context, null);
    }

    public KeyFactsPolicyCell(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyFactsPolicyCell(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_key_facts_policy_cell);
        initViews();
    }
    //endregion

    private void initViews() {
        getViewBinding().getRoot().setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().policyName.text(), getViewBinding().policyName));
    }

    public void setPolicy(EHIKeyFactsPolicy policy){
        getViewModel().setPolicy(policy);
    }

    public void setCellClickListener(@Nullable final KeyFactsPolicyCellClickListener listener) {
        mListener = listener;
    }

    public interface KeyFactsPolicyCellClickListener {
        void onPolicyClicked(EHIKeyFactsPolicy policy);
    }
}
