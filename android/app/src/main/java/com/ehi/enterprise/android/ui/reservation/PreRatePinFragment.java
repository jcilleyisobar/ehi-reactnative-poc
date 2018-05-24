package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PinFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(PreRatePinViewModel.class)
public class PreRatePinFragment extends DataBindingViewModelFragment<PreRatePinViewModel, PinFragmentBinding> {

    private static final String TAG = "PreRatePinFragment";

    @Extra(String.class)
    public static final String EXTRA_PIN = "ehi.EXTRA_PIN";

    @Extra(value = String.class, required = false)
    public static final String EXTRA_ERROR_MESSAGE = "ehi.EXTRA_ERROR_MESSAGE";

    public static final String PIN = "PIN";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view == getViewBinding().pinSubmitButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CORPORATE.value, TAG)
                        .state(EHIAnalytics.State.STATE_PIN.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SUBMIT.value)
                        .tagScreen()
                        .tagEvent();
                Intent intent = new Intent();
                intent.putExtra(PIN, getViewBinding().pinInput.getText().toString());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_pre_rate_pin, container);
        getActivity().setTitle(R.string.pin_auth_navigation_title);
        initViews();

        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        PreRatePinFragmentHelper.Extractor extractor = new PreRatePinFragmentHelper.Extractor(this);

        String pin = extractor.extraPin();
        if (!TextUtils.isEmpty(pin)) {
            getViewBinding().pinInput.setText(pin);
        }

        String errorMessage = extractor.extraErrorMessage();
        if (!TextUtils.isEmpty(errorMessage)) {
            DialogUtils.showDialogWithTitleAndText(getContext(), errorMessage, getString(R.string.alert_service_error_title));
        }

    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().pinInput.text(), getViewBinding().pinInput));
        bind(ReactorView.enabled(getViewModel().pinSubmitButton.enabled(), getViewBinding().pinSubmitButton));
    }

    private void initViews() {
        getViewBinding().pinSubmitButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CORPORATE.value, TAG)
                .state(EHIAnalytics.State.STATE_PIN.value)
                .tagScreen()
                .tagEvent();
    }
}
