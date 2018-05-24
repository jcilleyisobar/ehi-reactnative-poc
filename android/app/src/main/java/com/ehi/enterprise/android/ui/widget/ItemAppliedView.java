package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ItemAppliedViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ItemAppliedView extends DataBindingViewModelView<ManagersAccessViewModel, ItemAppliedViewBinding> {


    public ItemAppliedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_item_applied, null));
        }
        else {
            createViewBinding(R.layout.v_item_applied);
        }
    }

    public void setOnRemoveListener(OnClickListener listener) {
        getViewBinding().removeButton.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        getViewBinding().itemTitle.setText(title);
    }
}
