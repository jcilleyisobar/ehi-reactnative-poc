package com.ehi.enterprise.android.ui.reservation.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DontSeePastRentalsModalBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(DontSeeRentalViewModel.class)
public class DontSeeRentalModalDialogFragment extends DataBindingViewModelFragment<DontSeeRentalViewModel, DontSeePastRentalsModalBinding> {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().closeButton) {
                getActivity().finish();
            } else if (view == getViewBinding().callUs) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_dont_see_past_rentals_modal, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().callUs.setOnClickListener(mOnClickListener);
        getViewBinding().closeButton.setOnClickListener(mOnClickListener);
        getActivity().setTitle(R.string.rentals_cannot_find_title);
    }

}
