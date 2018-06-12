package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.IncludedExtraItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class ExtraIncludedViewHolder extends DataBindingViewHolder<IncludedExtraItemBinding> {

    public ExtraIncludedViewHolder(IncludedExtraItemBinding viewBinding) {
        super(viewBinding);
    }

    public static ExtraIncludedViewHolder create(ViewGroup parent) {
        return new ExtraIncludedViewHolder((IncludedExtraItemBinding) createViewBinding(parent.getContext(), R.layout.item_extra_included_item,parent));
    }
}
