package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LogoutFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class LogoutFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, LogoutFragmentBinding> {

    public static final String SIGNOUT_BOOLEAN_KEY = "ehi.com.logout.logout";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().cancelButton) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            } else if (view == getViewBinding().signOutButton) {
                Intent intent = new Intent();
                intent.putExtra(SIGNOUT_BOOLEAN_KEY, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_logout, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        getActivity().setTitle(null);

        getViewBinding().cancelButton.setOnClickListener(mOnClickListener);
        getViewBinding().signOutButton.setOnClickListener(mOnClickListener);
    }

}