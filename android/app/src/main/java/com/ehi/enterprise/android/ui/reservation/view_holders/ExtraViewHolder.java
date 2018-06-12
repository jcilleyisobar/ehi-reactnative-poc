package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ExtraItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class ExtraViewHolder extends DataBindingViewHolder<ExtraItemBinding> {

    public ExtraViewHolder(ExtraItemBinding viewBinding) {
        super(viewBinding);
    }

    public static ExtraViewHolder create(Context context, ViewGroup parent) {
        return new ExtraViewHolder((ExtraItemBinding) createViewBinding(context, R.layout.item_extra_item, parent));
    }

}
