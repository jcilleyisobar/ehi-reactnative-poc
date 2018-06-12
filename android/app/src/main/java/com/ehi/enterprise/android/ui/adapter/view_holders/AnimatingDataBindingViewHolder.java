package com.ehi.enterprise.android.ui.adapter.view_holders;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AnimatingDataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private boolean mIsAnimating = false;

    private T mViewBinding;

    protected AnimatingDataBindingViewHolder(final View itemView) {
        super(itemView);
    }

    protected AnimatingDataBindingViewHolder(final T viewBinding){
        this(viewBinding.getRoot());
        mViewBinding = viewBinding;
    }

    protected static ViewDataBinding createViewBinding(@NonNull Context context, @LayoutRes int layoutResId, @NonNull ViewGroup parent){
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResId, parent, false);
    }

    public T getViewBinding() {
        return mViewBinding;
    }

    public void startAnimation() {
        mIsAnimating = true;
    }

    public void endAnimation() {
        mIsAnimating = false;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }
}
