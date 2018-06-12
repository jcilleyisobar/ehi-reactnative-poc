package com.ehi.enterprise.android.ui.support.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CustomerSupportMainHeaderItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class CustomerSupportMainHeaderViewHolder
        extends DataBindingViewHolder<CustomerSupportMainHeaderItemBinding> {

    public CustomerSupportMainHeaderViewHolder(CustomerSupportMainHeaderItemBinding viewBinding) {
        super(viewBinding);
    }

    public static CustomerSupportMainHeaderViewHolder create(Context context, ViewGroup parent){
        return new CustomerSupportMainHeaderViewHolder((CustomerSupportMainHeaderItemBinding) createViewBinding(
                context,
                R.layout.item_customer_support_main_header,
                parent
        ));
    }

    public static void bind(CustomerSupportMainHeaderViewHolder holder){
        holder.getViewBinding().mainHeaderTitle.setText(R.string.customer_support_header_prefix);
    }
}