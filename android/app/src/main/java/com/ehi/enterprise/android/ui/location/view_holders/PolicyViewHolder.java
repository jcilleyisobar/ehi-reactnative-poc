package com.ehi.enterprise.android.ui.location.view_holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PolicyItemBinding;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class PolicyViewHolder extends DataBindingViewHolder<PolicyItemBinding> {

    public PolicyViewHolder(PolicyItemBinding viewBinding) {
        super(viewBinding);
    }

    public static PolicyViewHolder create(Context context, ViewGroup parent) {
        return new PolicyViewHolder((PolicyItemBinding) createViewBinding(context,
                R.layout.item_policy_cell,
                parent));
    }

    public static void bind(PolicyViewHolder holder, EHIPolicy policy, View.OnClickListener onPolicyClickListener){
        holder.getViewBinding().policyText.setText(policy.getDescription());
        holder.getViewBinding().policyText.setOnClickListener(onPolicyClickListener);
    }
}
