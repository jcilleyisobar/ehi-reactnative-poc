package com.ehi.enterprise.android.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StandardDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(EditCallUsMemberInfoViewModel.class)
public class EditCallUsMemberInfoFragment extends DataBindingViewModelFragment<EditCallUsMemberInfoViewModel, StandardDialogFragmentBinding> {

    public static final String SCREEN_NAME = "EditMemberInfoNameOrNumberFragment";
    public static final int REQUEST_CODE = 322;
    @Extra(String.class)
    public static final String DESCRIPTION = "DESCRIPTION";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().positiveButton) {
                callSupport();
                getActivity().finish();
            } else if (view == getViewBinding().negativeButton) {
                getActivity().setResult(Activity.RESULT_OK);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditCallUsMemberInfoFragmentHelper.Extractor extractor = new EditCallUsMemberInfoFragmentHelper.Extractor(this);
        getViewModel().setDescription(extractor.description());
    }

    private void initViews() {
        getViewBinding().title.setText(R.string.profile_edit_member_info_non_editable_title);
        getViewBinding().dialogText.setText(getViewModel().getDescription());

        getViewBinding().positiveButton.setText(R.string.profile_edit_member_info_non_editable_action_title);
        getViewBinding().negativeButton.setText(R.string.standard_button_cancel);

        getViewBinding().positiveButton.setOnClickListener(mOnClickListener);
        getViewBinding().negativeButton.setOnClickListener(mOnClickListener);
    }

    private void callSupport() {

        try {
            IntentUtils.callNumber(getActivity(), getViewModel().getPhoneNumber());
            getActivity().setResult(Activity.RESULT_OK);
        } catch (NullPointerException e) {
            DLog.e("CallSupport-EditMemberInfoName", e);
            getActivity().setResult(Activity.RESULT_CANCELED, null);
        }
    }


}
