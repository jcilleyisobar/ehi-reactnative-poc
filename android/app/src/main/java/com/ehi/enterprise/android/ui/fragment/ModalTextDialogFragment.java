package com.ehi.enterprise.android.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ModalDialogFragmentBinding;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;

public class ModalTextDialogFragment extends BaseFragment {

    public static final String TAG = "ModalTextDialogFragment";

    @Extra(value = String.class, required = false)
    public static final String TITLE = "ehi.EXTRA_TITLE";
    @Extra(value = String.class, required = false)
    public static final String TEXT = "ehi.EXTRA_TEXT";
    @Extra(value = String.class, required = false)
    public static final String BUTTON_TEXT = "ehi.EXTRA_BUTTON_TEXT";

    private ModalDialogFragmentBinding mBinding;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mBinding.closeButton) {
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fr_modal_dialog, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        mBinding.closeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
            return;
        }

        ModalTextDialogFragmentHelper.Extractor extractor = new ModalTextDialogFragmentHelper.Extractor(this);
        if (extractor.title() != null) {
            getActivity().setTitle(extractor.title());
        }
        if (extractor.text() != null) {
            mBinding.text.setText(extractor.text());
        }
        if (extractor.buttonText() != null) {
            mBinding.closeButton.setText(extractor.buttonText());
        }
    }
}
