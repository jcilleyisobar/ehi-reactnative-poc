package com.ehi.enterprise.android.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import com.ehi.enterprise.android.ui.activity.ViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public abstract class DataBindingViewModelActivity<T extends ManagersAccessViewModel, V extends ViewDataBinding> extends ViewModelActivity<T> {

    private V mViewBinding;

    protected V getViewBinding() {
        return mViewBinding;
    }

    public void setDataBindingContentView(@LayoutRes int layoutResID) {
        mViewBinding = DataBindingUtil.setContentView(this, layoutResID);
    }
}
