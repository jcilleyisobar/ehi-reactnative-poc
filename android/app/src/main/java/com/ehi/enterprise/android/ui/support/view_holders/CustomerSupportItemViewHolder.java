package com.ehi.enterprise.android.ui.support.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CustomerSupportItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class CustomerSupportItemViewHolder extends DataBindingViewHolder<CustomerSupportItemBinding> {

    public CustomerSupportItemViewHolder(CustomerSupportItemBinding viewBinding) {
        super(viewBinding);
    }

    public static CustomerSupportItemViewHolder create(Context context, ViewGroup parent){
        return new CustomerSupportItemViewHolder((CustomerSupportItemBinding) createViewBinding(context,
                R.layout.item_customer_support,
                parent));
    }
}
