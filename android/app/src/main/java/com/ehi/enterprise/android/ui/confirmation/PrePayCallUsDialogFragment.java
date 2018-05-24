package com.ehi.enterprise.android.ui.confirmation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PrePayCallUsDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class PrePayCallUsDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, PrePayCallUsDialogFragmentBinding> {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().prepayCancelReservationButton) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber());
            } else if (v == getViewBinding().prepayKeepReservationButton) {
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_prepay_call_us_dialog, container);

        initViews();

        return getViewBinding().getRoot();
    }

    private void initViews() {
        getActivity().setTitle(R.string.reservation_cancel_unavailable_title);

        getViewBinding().prepayCancelReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().prepayKeepReservationButton.setOnDisabledClickListener(mOnClickListener);
    }
}
