package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CarClassSelectItemBinding;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;
import com.ehi.enterprise.android.utils.DisplayUtils;

public class CarClassSelectViewHolder extends AnimatingDataBindingViewHolder<CarClassSelectItemBinding> {

    public CarClassSelectViewHolder(CarClassSelectItemBinding viewBinding) {
        super(viewBinding);
    }

    public static final CarClassSelectViewHolder create(Context context, ViewGroup parent, boolean withTopMargin) {
        CarClassSelectItemBinding binding = (CarClassSelectItemBinding) createViewBinding(context, R.layout.item_car_class_select_item, parent);
        if (withTopMargin) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.getRoot().getLayoutParams();
            params.setMargins(0, 0, 0, (int) DisplayUtils.dipToPixels(parent.getContext(), 12f));
            binding.getRoot().setLayoutParams(params);
        }
        return new CarClassSelectViewHolder(binding);
    }

}