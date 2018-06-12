package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RegionChoiceDiaglogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(RegionChoiceViewModel.class)
public class RegionChoiceDialogFragment extends DataBindingViewModelFragment<RegionChoiceViewModel, RegionChoiceDiaglogFragmentBinding> {

    public static final String SCREEN_NAME = "RegionChoiceDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().changeRegionArea) {
                showModalWithSearchHeader(getActivity(), new CountriesListFragmentHelper.Builder().build());
            } else if (view == getViewBinding().continueButton) {
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_region_choice_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().changeRegionArea.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(null);
        bind(ReactorTextView.text(getViewModel().preferredRegionName, getViewBinding().regionName));
    }
}
