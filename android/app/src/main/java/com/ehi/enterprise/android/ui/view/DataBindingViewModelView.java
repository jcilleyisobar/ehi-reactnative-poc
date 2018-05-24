package com.ehi.enterprise.android.ui.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class DataBindingViewModelView<T extends ManagersAccessViewModel, V extends ViewDataBinding> extends ViewModelView<T> {

    private V mViewBinding;

    //region constructors
    public DataBindingViewModelView(Context context) {
        super(context);
    }

    public DataBindingViewModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataBindingViewModelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //endregion

    protected V getViewBinding() {
        return mViewBinding;
    }

    protected void createViewBinding(@LayoutRes int layoutResId) {
        createViewBinding(layoutResId, true);
    }

    protected void createViewBinding(@LayoutRes int layoutResId, boolean attachToParent) {
        mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), layoutResId, this, attachToParent);
    }
}
