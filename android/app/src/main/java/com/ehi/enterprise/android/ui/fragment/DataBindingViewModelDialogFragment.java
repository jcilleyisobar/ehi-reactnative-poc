package com.ehi.enterprise.android.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class DataBindingViewModelDialogFragment<T extends ManagersAccessViewModel, V extends ViewDataBinding> extends ViewModelDialogFragment<T> {
    private V mViewBinding;

    protected V getViewBinding() {
        return mViewBinding;
    }

    protected View createViewBinding(LayoutInflater inflater, @LayoutRes int layoutRes, ViewGroup container) {
        mViewBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false);
        return mViewBinding.getRoot();
    }
}