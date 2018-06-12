package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.VEplusUnauthCellBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class EplusCellViewUnauthenticated extends DataBindingViewModelView<ManagersAccessViewModel, VEplusUnauthCellBinding> {

    public EplusCellViewUnauthenticated(Context context) {
        this(context, null, 0);
    }

    public EplusCellViewUnauthenticated(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EplusCellViewUnauthenticated(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_eplus_unauth_cell);
    }
}

