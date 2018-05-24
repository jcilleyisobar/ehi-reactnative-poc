package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TimeCellItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class TimeViewHolder extends DataBindingViewHolder<TimeCellItemBinding> {

    public TimeViewHolder(TimeCellItemBinding viewBinding) {
        super(viewBinding);
    }

    public static final TimeViewHolder create(Context context, ViewGroup parent) {
        return new TimeViewHolder((TimeCellItemBinding) createViewBinding(context, R.layout.item_time_cell, parent));
    }
}
