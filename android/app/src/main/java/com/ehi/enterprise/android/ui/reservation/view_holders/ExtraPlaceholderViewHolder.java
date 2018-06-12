package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ExtraPlaceholderItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class ExtraPlaceholderViewHolder extends DataBindingViewHolder<ExtraPlaceholderItemBinding> {

    public ExtraPlaceholderViewHolder(ExtraPlaceholderItemBinding viewBinding) {
        super(viewBinding);
    }

    public static ExtraPlaceholderViewHolder create(ViewGroup parent) {
        return new ExtraPlaceholderViewHolder((ExtraPlaceholderItemBinding) createViewBinding(parent.getContext(), R.layout.item_extra_placeholder, parent));
    }
}
