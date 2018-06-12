package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StandardDialogFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class ForgotUserNameFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, StandardDialogFragmentBinding> {

    public static final String SCREEN_NAME = "ForgotUserNameFragment";
    public static final int REQUEST_CODE = 322;

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

    public static ForgotUserNameFragment newInstance() {
        return new ForgotUserNameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        createViewBinding(inflater, R.layout.fr_standard_dialog, container);

        initViews();

        return getViewBinding().getRoot();
    }

    private void initViews() {

        getViewBinding().title.setText(getString(R.string.signin_member_number_recovery_modal_title));
        getViewBinding().dialogText.setText(R.string.signin_member_number_recovery_modal_details_text);
        getViewBinding().positiveButton.setText(getString(R.string.rentals_footer_contact_button_text));
        getViewBinding().negativeButton.setText(getString(R.string.standard_button_cancel));

        getViewBinding().positiveButton.setOnClickListener(mOnClickListener);
        getViewBinding().negativeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, ForgotUserNameFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_FORGOT_EMAIL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();

    }

    private void callSupport() {
        EHIPhone returnedPhone = getViewModel().getValidPhoneNumber();

        //this try catch is for scenarios where the introduction-> loginfragment -> callSupport is called and the supportInformationRequest failed
        try {
            String phoneNumber = returnedPhone == null
                    ? getViewModel().getSupportPhoneNumber()
                    : returnedPhone.getPhoneNumber();
            IntentUtils.callNumber(getActivity(), phoneNumber);
            getActivity().setResult(Activity.RESULT_OK);
        } catch (NullPointerException e) {
            DLog.e("CallSupport-ForgotUserName", e);
            getActivity().setResult(Activity.RESULT_CANCELED, null);
        }
    }
}
