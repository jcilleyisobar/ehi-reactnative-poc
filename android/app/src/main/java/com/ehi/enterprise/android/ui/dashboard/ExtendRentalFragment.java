package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ExtendRentalFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ExtendRentalViewModel.class)
public class ExtendRentalFragment extends DataBindingViewModelFragment<ExtendRentalViewModel, ExtendRentalFragmentBinding> {

    private static final String TAG = "ExtendRentalFragment";

    @Extra(String.class)
    public static String CONFIRMATION_NUMBER = "CONFIRMATION_NUMBER";
    @Extra(String.class)
    public static String PHONE_NUMBER = "PHONE_NUMBER";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().callUsButton) {
                IntentUtils.callNumber(getActivity().getApplicationContext(), getViewModel().getSupportPhoneNumber());
            } else if (view == getViewBinding().closeRentalButton) {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExtendRentalFragmentHelper.Extractor extractor = new ExtendRentalFragmentHelper.Extractor(this);

        getViewModel().setConfirmationNumber(extractor.confirmationNumber());
        getViewModel().callNumber.setText(extractor.phoneNumber());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_dashboard_extend_rental, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().callUsButton.setOnClickListener(mOnClickListener);
        getViewBinding().closeRentalButton.setOnClickListener(mOnClickListener);

        if (EHITextUtils.isEmpty(getViewModel().getConfirmationNumber())) {
            getViewBinding().resNumber.setVisibility(View.GONE);
            getViewBinding().resNumberTitle.setVisibility(View.GONE);
        } else {
            getViewBinding().resNumberTitle.setVisibility(View.VISIBLE);
            getViewBinding().resNumber.setVisibility(View.VISIBLE);
            getViewModel().confirmationNumber.setText(getViewModel().getConfirmationNumber());
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().confirmationNumber.text(), getViewBinding().resNumber));

    }
}