package com.ehi.enterprise.android.ui.location.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterTipViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class FilterTipView extends DataBindingViewModelView<ManagersAccessViewModel, FilterTipViewBinding> {

    public FilterTipView(Context context) {
        this(context, null, 0);
    }

    public FilterTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_filter_tip);
        } else {
            addView(inflate(context, R.layout.v_filter_tip, null));
        }
    }

    public void setCloseBtnListener(View.OnClickListener listener) {
        getViewBinding().closeButton.setOnClickListener(listener);
    }

}
