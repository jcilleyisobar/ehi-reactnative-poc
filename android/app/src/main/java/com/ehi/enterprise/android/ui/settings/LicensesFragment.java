package com.ehi.enterprise.android.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LicensesFragmentViewBinding;
import com.ehi.enterprise.android.ui.fragment.BaseFragment;
import com.isobar.android.newinstancer.NoExtras;

@NoExtras
public class LicensesFragment extends BaseFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LicensesFragmentViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fr_licenses, container, false);
        getActivity().setTitle(getString(R.string.third_party_licenses_navigation_title));
        return binding.getRoot();
    }
}
