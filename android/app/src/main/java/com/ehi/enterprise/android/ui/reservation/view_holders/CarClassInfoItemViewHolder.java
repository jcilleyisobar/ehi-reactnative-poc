package com.ehi.enterprise.android.ui.reservation.view_holders;

import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BannerBinding;
import com.ehi.enterprise.android.ui.adapter.view_holders.AnimatingDataBindingViewHolder;

public class CarClassInfoItemViewHolder extends AnimatingDataBindingViewHolder<BannerBinding> {

    public CarClassInfoItemViewHolder(BannerBinding viewBinding) {
        super(viewBinding);
    }

    public static CarClassInfoItemViewHolder create(Context context, ViewGroup parent) {
        return new CarClassInfoItemViewHolder((BannerBinding) createViewBinding(
                context,
                R.layout.v_banner,
                parent));
    }

    public static void bind(CarClassInfoItemViewHolder holder, String message) {
        holder.getViewBinding().message.setText(message);
    }
}
