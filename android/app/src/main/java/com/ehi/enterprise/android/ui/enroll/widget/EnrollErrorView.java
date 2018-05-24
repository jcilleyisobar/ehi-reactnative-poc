package com.ehi.enterprise.android.ui.enroll.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EnrollErrorViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class EnrollErrorView extends DataBindingViewModelView<ManagersAccessViewModel, EnrollErrorViewBinding> {

    public EnrollErrorView(Context context) {
        this(context, null);
    }

    public EnrollErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnrollErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_enroll_error, null));
            return;
        }

        createViewBinding(R.layout.v_enroll_error);

        getViewBinding().getRoot().setVisibility(GONE);
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        if (errorMessageList == null || errorMessageList.size() == 0) {
            getViewBinding().getRoot().setVisibility(GONE);
            return;
        }

        getViewBinding().getRoot().setVisibility(VISIBLE);

        getViewBinding().enrollErrorContainer.removeAllViews();

        EnrollErrorItemView enrollErrorItemView = new EnrollErrorItemView(getContext());
        enrollErrorItemView.setErrorMessage(getContext().getString(R.string.enroll_field_validation_message));

        getViewBinding().enrollErrorContainer.addView(enrollErrorItemView);

        for (String errorMessage : errorMessageList) {
            enrollErrorItemView = new EnrollErrorItemView(getContext());
            enrollErrorItemView.setErrorMessage(
                    getContext().getString(R.string.text_with_bullet_prefix, errorMessage)
            );

            getViewBinding().enrollErrorContainer.addView(enrollErrorItemView);
        }
    }
}
