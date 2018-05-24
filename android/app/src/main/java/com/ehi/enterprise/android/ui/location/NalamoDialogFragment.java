package com.ehi.enterprise.android.ui.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NalamoDialogBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class NalamoDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, NalamoDialogBinding> {

    private static final String TAG = "NalamoDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().okButton) {
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_nalamo_dialog, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        getViewBinding().okButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.info_modal_drive_alliance_title));
    }
}
