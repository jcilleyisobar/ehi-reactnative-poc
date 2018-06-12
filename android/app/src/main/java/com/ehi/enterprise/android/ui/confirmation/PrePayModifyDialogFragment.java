package com.ehi.enterprise.android.ui.confirmation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PrePayModifyDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class PrePayModifyDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, PrePayModifyDialogFragmentBinding> {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().prepayModifyReservationButton) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (v == getViewBinding().prepayKeepReservationButton) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_prepay_modify_dialog, container);

        initViews();

        return getViewBinding().getRoot();
    }

    private void initViews() {
        getActivity().setTitle(R.string.modify_reservation_prepay_dialog_title);

        getViewBinding().prepayModifyReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().prepayKeepReservationButton.setOnDisabledClickListener(mOnClickListener);
    }
}
