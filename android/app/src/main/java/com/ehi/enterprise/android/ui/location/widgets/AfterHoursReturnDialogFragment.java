package com.ehi.enterprise.android.ui.location.widgets;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AfterHoursDialogViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelDialogFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class AfterHoursReturnDialogFragment extends DataBindingViewModelDialogFragment<ManagersAccessViewModel, AfterHoursDialogViewBinding> {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        dismiss();
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_after_hours_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().closeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white)));
        }
    }
}