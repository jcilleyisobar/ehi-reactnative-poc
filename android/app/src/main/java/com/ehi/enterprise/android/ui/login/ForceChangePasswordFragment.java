package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ChangePasswordBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class ForceChangePasswordFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, ChangePasswordBinding> {

    public static final int REQUEST_CODE = 9001;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().setResult(v == getViewBinding().okayButton
                    ? Activity.RESULT_OK
                    : Activity.RESULT_CANCELED);
            getActivity().finish();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_force_password_change, container);
        initView();
        return getViewBinding().getRoot();
    }

    private void initView() {
        getViewBinding().cancelButton.setOnClickListener(mOnClickListener);
        getViewBinding().okayButton.setOnClickListener(mOnClickListener);
    }
}
