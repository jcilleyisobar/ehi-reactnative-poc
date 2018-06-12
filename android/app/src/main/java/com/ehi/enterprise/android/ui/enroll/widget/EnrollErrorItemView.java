package com.ehi.enterprise.android.ui.enroll.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EnrollErrorItemViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class EnrollErrorItemView extends DataBindingViewModelView<ManagersAccessViewModel, EnrollErrorItemViewBinding> {

    public EnrollErrorItemView(Context context) {
        this(context, null);
    }

    public EnrollErrorItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnrollErrorItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_enroll_error_item, null));
            return;
        }

        createViewBinding(R.layout.v_enroll_error_item);
    }

    public void setErrorMessage(String errorMessage) {
        getViewBinding().errorMessage.setText(errorMessage);
    }
}
