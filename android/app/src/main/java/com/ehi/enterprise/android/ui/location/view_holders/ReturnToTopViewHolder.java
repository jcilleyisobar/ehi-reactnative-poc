package com.ehi.enterprise.android.ui.location.view_holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReturnToTopItemBinding;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

public class ReturnToTopViewHolder extends DataBindingViewHolder<ReturnToTopItemBinding> {

    public ReturnToTopViewHolder(ReturnToTopItemBinding viewBinding) {
        super(viewBinding);
    }

    public static ReturnToTopViewHolder create(Context context, ViewGroup parent){
        return new ReturnToTopViewHolder((ReturnToTopItemBinding) createViewBinding(context,
                R.layout.item_return_to_top,
                parent));
    }

    public static void bind(ReturnToTopViewHolder holder, View.OnClickListener listener){
        holder.getViewBinding().returnToTopView.setOnClickListener(listener);
    }

}
