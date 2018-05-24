package com.ehi.enterprise.android.ui.location.view_holders;


import android.content.Context;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WayfindingStepItemBinding;
import com.ehi.enterprise.android.models.location.EHIWayfindingStep;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;

public class WayfindingViewHolder extends DataBindingViewHolder<WayfindingStepItemBinding> {

    public WayfindingViewHolder(WayfindingStepItemBinding viewBinding) {
        super(viewBinding);
    }

    public static WayfindingViewHolder create(Context context, ViewGroup parent) {
        return new WayfindingViewHolder((WayfindingStepItemBinding) createViewBinding(context,
                R.layout.item_wayfinding_step,
                parent));
    }

    public static void bind(WayfindingViewHolder holder, EHIWayfindingStep step) {
        EHIImageLoader.with(holder.getViewBinding().stepImage.getContext())
                .load(step.getIconPath())
                .into(holder.getViewBinding().stepImage);
        holder.getViewBinding().stepText.setText(step.getText());
    }

}