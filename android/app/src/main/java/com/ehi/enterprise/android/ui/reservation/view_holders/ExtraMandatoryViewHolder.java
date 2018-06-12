package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MandatoryExtraItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class ExtraMandatoryViewHolder extends DataBindingViewHolder<MandatoryExtraItemBinding>{

    public ExtraMandatoryViewHolder(MandatoryExtraItemBinding viewBinding) {
        super(viewBinding);
    }

    public static ExtraMandatoryViewHolder create(ViewGroup parent){
        return new ExtraMandatoryViewHolder((MandatoryExtraItemBinding) createViewBinding(parent.getContext(), R.layout.item_extra_mandatory_item,parent));
    }
}
