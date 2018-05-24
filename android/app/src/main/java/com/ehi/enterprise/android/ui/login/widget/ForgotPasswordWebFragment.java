package com.ehi.enterprise.android.ui.login.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StandardDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(CountrySpecificViewModel.class)
public class ForgotPasswordWebFragment extends DataBindingViewModelFragment<CountrySpecificViewModel, StandardDialogFragmentBinding> {

    public static final String SCREEN_NAME = "ForgotPasswordFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().positiveButton) {
                openExternalURL();
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

        getViewBinding().title.setText(getString(R.string.signin_password_recovery_modal_title));
        getViewBinding().dialogText.setText(getString(R.string.signin_password_recovery_modal_details_text));
        getViewBinding().positiveButton.setText(getString(R.string.alert_okay_title));
        getViewBinding().negativeButton.setText(getString(R.string.standard_button_cancel));

        getViewBinding().positiveButton.setOnClickListener(mOnClickListener);
        getViewBinding().negativeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SIGN_IN.value, ForgotPasswordWebFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_FORGOT_PASSWORD.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void openExternalURL() {
        String url = getViewModel().getForgotPasswordURL();

        IntentUtils.openUrlViaExternalApp(getActivity(),
                url == null
                        ? getViewModel().getSupportWebsite()
                        : url);
    }
}
