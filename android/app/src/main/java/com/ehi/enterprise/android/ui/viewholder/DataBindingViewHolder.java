package com.ehi.enterprise.android.ui.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder{
    private T mViewBinding;

    protected DataBindingViewHolder(final View itemView) {
        super(itemView);
    }

    protected DataBindingViewHolder(final T viewBinding){
        this(viewBinding.getRoot());
        mViewBinding = viewBinding;
    }

    protected static ViewDataBinding createViewBinding(@NonNull Context context, @LayoutRes int layoutResId, @NonNull ViewGroup parent){
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResId, parent, false);
    }

    public T getViewBinding() {
        return mViewBinding;
    }
}
