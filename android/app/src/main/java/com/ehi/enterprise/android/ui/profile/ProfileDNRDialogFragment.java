package com.ehi.enterprise.android.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ProfileDNRDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class ProfileDNRDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, ProfileDNRDialogFragmentBinding> {

    private static final String TAG = "ProfileDNRDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().frDnrDialogCallUs) {
                IntentUtils.callNumber(getActivity(), getViewModel().getDNRPhoneNumber());
            } else if (view == getViewBinding().frDnrDialogCancel) {
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_dnr_profile_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().frDnrDialogCallUs.setOnClickListener(mOnClickListener);
        getViewBinding().frDnrDialogCancel.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(null);
    }

}
