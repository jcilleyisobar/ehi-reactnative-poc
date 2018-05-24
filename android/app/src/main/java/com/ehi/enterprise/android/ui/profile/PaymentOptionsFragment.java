package com.ehi.enterprise.android.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PaymentOptionsFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(PaymentOptionsViewModel.class)
public class PaymentOptionsFragment
        extends DataBindingViewModelFragment<PaymentOptionsViewModel, PaymentOptionsFragmentBinding> {

    private static final String TAG = "PaymentOptionsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_payment_options, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        //TODO R1.1
    }
}
