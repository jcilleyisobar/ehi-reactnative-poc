package com.ehi.enterprise.android.ui.util;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ProgressFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ProgressFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, ProgressFragmentBinding> {

    private boolean mCancelable = true;

    @Extra(value = boolean.class, required = false)
    public static String IS_CANCELABLE = "IS_CANCELABLE";

    @Extra(value = boolean.class, required = false)
    public static String IS_DETERMINATE_LOADER = "IS_DETERMINATE_LOADER";

    private AnimationDrawable mSpinnerAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createViewBinding(inflater, R.layout.fr_progress, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnimation();
    }

    private void startAnimation() {
        ProgressFragmentHelper.Extractor extractor = new ProgressFragmentHelper.Extractor(this);
        Boolean optionalCancelable = extractor.isCancelable();
        mCancelable = optionalCancelable == null ? true : optionalCancelable;

        if (extractor.isDeterminateLoader() != null && extractor.isDeterminateLoader() && !BaseAppUtils.isLowMemoryDevice(getActivity())) {
            progressUsingDeterminateLoader();
        } else {
            progressUsingSpinnerLoader();
        }
        mSpinnerAnim.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        getViewBinding().genericSpinner.setImageDrawable(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getViewBinding().genericSpinner.setImageDrawable(null);
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public void progressUsingDeterminateLoader() {
        if (getViewBinding() == null || getViewBinding().genericSpinner == null) {
            return;
        }
        getViewBinding().genericSpinner.setImageResource(R.drawable.enterprise_loader);

        ViewGroup.LayoutParams params = getViewBinding().genericSpinner.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        mSpinnerAnim = (AnimationDrawable) getViewBinding().genericSpinner.getDrawable();
        mSpinnerAnim.start();
    }

    public void progressUsingSpinnerLoader() {
        if (getViewBinding() == null || getViewBinding().genericSpinner == null) {
            return;
        }
        getViewBinding().genericSpinner.setImageResource(R.drawable.generic_spinner);

        ViewGroup.LayoutParams params = getViewBinding().genericSpinner.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        mSpinnerAnim = (AnimationDrawable) getViewBinding().genericSpinner.getDrawable();
        mSpinnerAnim.start();
    }

}