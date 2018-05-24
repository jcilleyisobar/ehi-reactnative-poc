package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StandardDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ModalFragment;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(EmeraldClubLoggedInModalViewModel.class)
public class EmeraldClubLoggedInModalFragment
        extends DataBindingViewModelFragment<EmeraldClubLoggedInModalViewModel, StandardDialogFragmentBinding>
        implements ModalFragment {

    public static final String SCREEN_NAME = "EmeraldClubLoggedInModalFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().positiveButton) {
                getViewModel().yesButtonClicked();
                setResult(true);
                getActivity().finish();
            } else if (view == getViewBinding().negativeButton) {
                setResult(false);
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        createViewBinding(inflater, R.layout.fr_standard_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().positiveButton.setOnClickListener(mOnClickListener);
        getViewBinding().negativeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(ReactorTextView.textRes(getViewModel().dialogText.textRes(), getViewBinding().dialogText));
        bind(ReactorTextView.textRes(getViewModel().positiveButtonText.textRes(), getViewBinding().positiveButton));
        bind(ReactorTextView.textRes(getViewModel().negativeButtonText.textRes(), getViewBinding().negativeButton));
    }

    private void setResult(boolean accepted) {
        getActivity().setResult(accepted ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
    }

    @Override
    public boolean onBackPressed() {
        setResult(false);
        return false;
    }
}
