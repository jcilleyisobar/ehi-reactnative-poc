package com.ehi.enterprise.android.ui.reservation.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.StandardDialogFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class ForgotConfirmationFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, StandardDialogFragmentBinding> {

    public static final String SCREEN_NAME = "ForgotConfirmationFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().positiveButton) {
                callNumber();
                getActivity().finish();
            } else if (view == getViewBinding().negativeButton) {
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

        getViewBinding().title.setText(getString(R.string.rentals_lookup_forgot_confirmation_text));
        getViewBinding().dialogText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.info_modal_forgot_confirmation_details)
                .addTokenAndValue(EHIStringToken.PHONE_NUMBER, getViewModel().getSupportPhoneNumber())
                .format());
        getViewBinding().positiveButton.setText(getString(R.string.info_modal_continue_button));
        getViewBinding().negativeButton.setText(getString(R.string.standard_button_cancel));

        getViewBinding().positiveButton.setOnClickListener(mOnClickListener);
        getViewBinding().negativeButton.setOnClickListener(mOnClickListener);
    }


    private void callNumber() {
        EHIPhone returnedPhone = getViewModel().getValidPhoneNumber();

        String phoneNumber = returnedPhone == null
                ? getViewModel().getSupportPhoneNumber()
                : returnedPhone.getPhoneNumber();

        IntentUtils.callNumber(getActivity(), phoneNumber);
        FragmentUtils.removeProgressFragment(getActivity());

    }

}
