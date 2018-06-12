package com.ehi.enterprise.android.ui.fragment;


import android.support.v4.app.DialogFragment;
import com.ehi.enterprise.android.utils.FragmentUtils;

public class BaseDialogFragment extends DialogFragment {
    @Override
    public void onStop() {
        super.onStop();
        FragmentUtils.removeProgressFragment(getActivity());
    }
}
