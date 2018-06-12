package com.ehi.enterprise.android.ui.reservation.promotions.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DealsContractDialogViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class DealsContractCombinationDialog extends DataBindingViewModelFragment<ManagersAccessViewModel, DealsContractDialogViewBinding> {

    private static final String TAG = "DealsContractCombinationDialog";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().yesButton) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (view == getViewBinding().noButton) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_deals_contract_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().yesButton.setOnClickListener(mOnClickListener);
        getViewBinding().noButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
